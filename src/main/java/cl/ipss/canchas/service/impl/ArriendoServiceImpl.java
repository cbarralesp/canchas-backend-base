package cl.ipss.canchas.service.impl;

import cl.ipss.canchas.dto.ArriendoRequest;
import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.dto.CambioArriendoRequest;
import cl.ipss.canchas.dto.CanchaResponse;
import cl.ipss.canchas.dto.DisponibilidadResponse;
import cl.ipss.canchas.entity.Arrendatario;
import cl.ipss.canchas.entity.Arriendo;
import cl.ipss.canchas.entity.Cancha;
import cl.ipss.canchas.enums.EstadoArriendo;
import cl.ipss.canchas.exception.RecursoNoEncontradoException;
import cl.ipss.canchas.exception.ReglaNegocioException;
import cl.ipss.canchas.repository.ArrendatarioRepository;
import cl.ipss.canchas.repository.ArriendoRepository;
import cl.ipss.canchas.repository.CanchaRepository;
import cl.ipss.canchas.service.ArriendoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementación de {@link ArriendoService}. Concentra las reglas de negocio del sistema:
 *
 *  RN-01  Un horario de una cancha no puede arrendarse dos veces (solo cuenta si el arriendo está CONFIRMADO).
 *  RN-02  Solo se arriendan canchas activas.
 *  RN-03  Los bloques son de 1 hora, entre las 08:00 y las 22:00.
 *  RN-04  No se puede arrendar ni mover un arriendo a un horario que ya pasó.
 *  RN-05  Solo se modifica o cancela un arriendo CONFIRMADO que aún no comienza, y solo lo hace su dueño
 *         (identificado por email) o el administrador al cancelar.
 *
 * Cada método de esta clase es una transacción: se guarda todo o no se guarda nada.
 */
@Service
public class ArriendoServiceImpl implements ArriendoService {

    private static final Logger log = LoggerFactory.getLogger(ArriendoServiceImpl.class);

    private final ArriendoRepository arriendos;
    private final CanchaRepository canchas;
    private final ArrendatarioRepository arrendatarios;

    public ArriendoServiceImpl(ArriendoRepository arriendos, CanchaRepository canchas,
                               ArrendatarioRepository arrendatarios) {
        this.arriendos = arriendos;
        this.canchas = canchas;
        this.arrendatarios = arrendatarios;
    }

    // ------------------------------------------------------------------ consultas

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> consultarDisponibilidad(LocalDate fecha) {
        // Una sola consulta trae los arriendos confirmados del día; se agrupan por cancha para saber qué horas están ocupadas.
        Map<Long, Set<Integer>> ocupadasPorCancha = arriendos.findByFechaAndEstado(fecha, EstadoArriendo.CONFIRMADO)
                .stream()
                .collect(Collectors.groupingBy(a -> a.getCancha().getId(),
                        Collectors.mapping(Arriendo::getHora, Collectors.toSet())));

        return canchas.findByActivaTrueOrderByNombreAsc().stream()
                .map(c -> new DisponibilidadResponse(CanchaResponse.from(c),
                        horasLibres(fecha, ocupadasPorCancha.getOrDefault(c.getId(), Set.of()))))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArriendoResponse obtener(Long id, String email) {
        return ArriendoResponse.from(buscarDelCliente(id, email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArriendoResponse> listarPorEmail(String email) {
        return arriendos.findByArrendatarioEmailOrderByFechaDescHoraDesc(normalizar(email))
                .stream().map(ArriendoResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArriendoResponse> listarAgenda(LocalDate fecha) {
        List<Arriendo> lista = (fecha == null)
                ? arriendos.findAllByOrderByFechaDescHoraDesc()
                : arriendos.findByFechaOrderByHoraAsc(fecha);
        return lista.stream().map(ArriendoResponse::from).toList();
    }

    // ------------------------------------------------------------------ comandos

    @Override
    @Transactional
    public ArriendoResponse crear(ArriendoRequest request) {
        Cancha cancha = buscarCanchaActiva(request.canchaId());                       // RN-02
        exigirHorarioValido(request.fecha(), request.hora());                         // RN-03 y RN-04
        exigirHorarioLibre(cancha.getId(), request.fecha(), request.hora(), null);    // RN-01

        Arrendatario arrendatario = obtenerOCrearArrendatario(request);
        Arriendo arriendo = arriendos.save(new Arriendo(cancha, arrendatario, request.fecha(), request.hora()));

        log.info("--- arriendo creado: id={} cancha={} fecha={} hora={} ---",
                arriendo.getId(), cancha.getNombre(), arriendo.getFecha(), arriendo.getHora());
        return ArriendoResponse.from(arriendo);
    }

    @Override
    @Transactional
    public ArriendoResponse modificar(Long id, CambioArriendoRequest request) {
        Arriendo arriendo = buscarDelCliente(id, request.email());
        exigirModificable(arriendo);                                                  // RN-05

        Cancha cancha = buscarCanchaActiva(request.canchaId());                       // RN-02
        exigirHorarioValido(request.fecha(), request.hora());                         // RN-03 y RN-04
        exigirHorarioLibre(cancha.getId(), request.fecha(), request.hora(), id);      // RN-01 (ignora al propio arriendo)

        // No hace falta llamar a save(): el arriendo está gestionado y JPA hace el UPDATE al terminar la transacción.
        arriendo.reprogramar(cancha, request.fecha(), request.hora());
        log.info("--- arriendo modificado: id={} ---", id);
        return ArriendoResponse.from(arriendo);
    }

    @Override
    @Transactional
    public ArriendoResponse cancelar(Long id, String email) {
        Arriendo arriendo = buscarDelCliente(id, email);
        exigirModificable(arriendo);                                                  // RN-05
        arriendo.cancelar();
        log.info("--- arriendo cancelado por el cliente: id={} ---", id);
        return ArriendoResponse.from(arriendo);
    }

    @Override
    @Transactional
    public ArriendoResponse cancelarComoAdmin(Long id) {
        Arriendo arriendo = arriendos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el arriendo con id " + id));
        if (!arriendo.estaConfirmado()) {
            throw new ReglaNegocioException("El arriendo ya está cancelado");
        }
        arriendo.cancelar();
        log.info("--- arriendo cancelado por el administrador: id={} ---", id);
        return ArriendoResponse.from(arriendo);
    }

    // ------------------------------------------------------------------ reglas (métodos pequeños y con nombre claro)

    /** Horas de inicio del día que no están ocupadas y que aún no han pasado. */
    private List<Integer> horasLibres(LocalDate fecha, Set<Integer> ocupadas) {
        return IntStream.rangeClosed(Arriendo.HORA_MINIMA, Arriendo.HORA_MAXIMA)
                .filter(hora -> !ocupadas.contains(hora) && !yaPaso(fecha, hora))
                .boxed()
                .toList();
    }

    /** RN-03 y RN-04: hora dentro del horario del recinto y no pasada. */
    private void exigirHorarioValido(LocalDate fecha, int hora) {
        if (hora < Arriendo.HORA_MINIMA || hora > Arriendo.HORA_MAXIMA) {
            throw new ReglaNegocioException("El horario de atención es de 08:00 a 22:00");
        }
        if (yaPaso(fecha, hora)) {
            throw new ReglaNegocioException("No se puede arrendar un horario que ya pasó");
        }
    }

    /** RN-01: lanza error si otro arriendo CONFIRMADO ocupa ese horario (excluyendo a {@code excluirId} si viene). */
    private void exigirHorarioLibre(Long canchaId, LocalDate fecha, int hora, Long excluirId) {
        boolean ocupado = (excluirId == null)
                ? arriendos.existsByCanchaIdAndFechaAndHoraAndEstado(canchaId, fecha, hora, EstadoArriendo.CONFIRMADO)
                : arriendos.existsByCanchaIdAndFechaAndHoraAndEstadoAndIdNot(canchaId, fecha, hora,
                        EstadoArriendo.CONFIRMADO, excluirId);
        if (ocupado) {
            throw new ReglaNegocioException("Ese horario ya está arrendado. Elige otro");
        }
    }

    /** RN-05: el arriendo debe estar confirmado y no haber comenzado. */
    private void exigirModificable(Arriendo arriendo) {
        if (!arriendo.estaConfirmado()) {
            throw new ReglaNegocioException("El arriendo ya está cancelado y no admite cambios");
        }
        if (!arriendo.puedeModificarse()) {
            throw new ReglaNegocioException("El arriendo ya comenzó o terminó: no se puede modificar ni cancelar");
        }
    }

    private boolean yaPaso(LocalDate fecha, int hora) {
        return LocalDateTime.of(fecha, LocalTime.of(hora, 0)).isBefore(LocalDateTime.now());
    }

    // ------------------------------------------------------------------ búsquedas auxiliares

    private Cancha buscarCanchaActiva(Long canchaId) {
        Cancha cancha = canchas.findById(canchaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cancha con id " + canchaId));
        if (!cancha.isActiva()) {
            throw new ReglaNegocioException("La cancha '" + cancha.getNombre() + "' está fuera de servicio");
        }
        return cancha;
    }

    /**
     * Busca el arriendo y comprueba que sea de esa persona. Si es de otra, responde "no existe" (404) y no
     * "prohibido": así no se revela que ese arriendo existe.
     */
    private Arriendo buscarDelCliente(Long id, String email) {
        String emailNormalizado = normalizar(email);
        return arriendos.findById(id)
                .filter(a -> a.getArrendatario().getEmail().equals(emailNormalizado))
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el arriendo " + id + " para ese email"));
    }

    /** Reutiliza al arrendatario si el email ya existe (actualizando sus datos); si no, lo crea. */
    private Arrendatario obtenerOCrearArrendatario(ArriendoRequest request) {
        String email = normalizar(request.email());
        String nombre = request.nombre().trim();
        return arrendatarios.findByEmail(email)
                .map(existente -> {
                    existente.actualizarContacto(nombre, request.telefono());
                    return existente;
                })
                .orElseGet(() -> arrendatarios.save(new Arrendatario(nombre, email, request.telefono())));
    }

    /** El email identifica a la persona: se compara siempre sin espacios y en minúsculas. */
    private String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
