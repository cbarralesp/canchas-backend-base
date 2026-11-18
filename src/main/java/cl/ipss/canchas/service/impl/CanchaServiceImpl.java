package cl.ipss.canchas.service.impl;

import cl.ipss.canchas.dto.CanchaRequest;
import cl.ipss.canchas.dto.CanchaResponse;
import cl.ipss.canchas.entity.Cancha;
import cl.ipss.canchas.exception.RecursoNoEncontradoException;
import cl.ipss.canchas.exception.ReglaNegocioException;
import cl.ipss.canchas.repository.CanchaRepository;
import cl.ipss.canchas.service.CanchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de {@link CanchaService}: CRUD de canchas con sus reglas (nombre único).
 * {@code @Transactional} hace que cada método sea una transacción: si algo falla, no queda nada a medias.
 */
@Service
public class CanchaServiceImpl implements CanchaService {

    private static final Logger log = LoggerFactory.getLogger(CanchaServiceImpl.class);

    private final CanchaRepository canchas;

    /** Inyección por constructor: Spring entrega el repository ya creado (no se usa @Autowired ni Lombok). */
    public CanchaServiceImpl(CanchaRepository canchas) {
        this.canchas = canchas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> listar() {
        return canchas.findAllByOrderByNombreAsc().stream().map(CanchaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CanchaResponse obtener(Long id) {
        return CanchaResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public CanchaResponse crear(CanchaRequest request) {
        String nombre = request.nombre().trim();
        if (canchas.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una cancha llamada '" + nombre + "'");
        }
        Cancha cancha = canchas.save(new Cancha(nombre, request.deporte(), request.tarifaHora()));
        log.info("--- cancha creada: id={} nombre={} ---", cancha.getId(), cancha.getNombre());
        return CanchaResponse.from(cancha);
    }

    @Override
    @Transactional
    public CanchaResponse actualizar(Long id, CanchaRequest request) {
        Cancha cancha = buscar(id);
        String nombre = request.nombre().trim();
        if (canchas.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra cancha llamada '" + nombre + "'");
        }
        // La entidad está "gestionada": al terminar la transacción JPA detecta el cambio y hace el UPDATE solo.
        cancha.actualizar(nombre, request.deporte(), request.tarifaHora());
        log.info("--- cancha actualizada: id={} ---", id);
        return CanchaResponse.from(cancha);
    }

    @Override
    @Transactional
    public CanchaResponse cambiarEstado(Long id, boolean activa) {
        Cancha cancha = buscar(id);
        cancha.setActiva(activa);
        log.info("--- cancha id={} activa={} ---", id, activa);
        return CanchaResponse.from(cancha);
    }

    private Cancha buscar(Long id) {
        return canchas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cancha con id " + id));
    }
}
