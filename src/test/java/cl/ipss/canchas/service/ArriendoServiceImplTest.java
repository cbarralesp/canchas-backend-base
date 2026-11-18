package cl.ipss.canchas.service;

import cl.ipss.canchas.dto.ArriendoRequest;
import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.dto.CambioArriendoRequest;
import cl.ipss.canchas.dto.DisponibilidadResponse;
import cl.ipss.canchas.entity.Arrendatario;
import cl.ipss.canchas.entity.Arriendo;
import cl.ipss.canchas.entity.Cancha;
import cl.ipss.canchas.enums.Deporte;
import cl.ipss.canchas.enums.EstadoArriendo;
import cl.ipss.canchas.exception.RecursoNoEncontradoException;
import cl.ipss.canchas.exception.ReglaNegocioException;
import cl.ipss.canchas.repository.ArrendatarioRepository;
import cl.ipss.canchas.repository.ArriendoRepository;
import cl.ipss.canchas.repository.CanchaRepository;
import cl.ipss.canchas.service.impl.ArriendoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueba UNITARIA del service con Mockito: los repositories son simulados (mocks), así que no se necesita base de
 * datos y solo se prueba la lógica de negocio de {@link ArriendoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ArriendoServiceImplTest {

    private static final LocalDate MANANA = LocalDate.now().plusDays(1);

    @Mock
    private ArriendoRepository arriendos;
    @Mock
    private CanchaRepository canchas;
    @Mock
    private ArrendatarioRepository arrendatarios;

    private ArriendoServiceImpl servicio;
    private Cancha cancha;
    private Arrendatario ana;

    @BeforeEach
    void preparar() {
        servicio = new ArriendoServiceImpl(arriendos, canchas, arrendatarios);
        cancha = new Cancha("Cancha 1", Deporte.FUTBOL, 30000);
        ReflectionTestUtils.setField(cancha, "id", 1L);
        ana = new Arrendatario("Ana", "ana@correo.cl", "+56912345678");
    }

    private ArriendoRequest solicitud(LocalDate fecha, int hora) {
        return new ArriendoRequest(1L, fecha, hora, "Ana", "Ana@Correo.cl ", "+56912345678");
    }

    private Arriendo arriendoGuardadoDeAna() {
        Arriendo a = new Arriendo(cancha, ana, MANANA, 19);
        ReflectionTestUtils.setField(a, "id", 7L);
        return a;
    }

    @Test
    void crearGuardaElArriendoConLaTarifaDeLaCanchaYNormalizaElEmail() {
        when(canchas.findById(1L)).thenReturn(Optional.of(cancha));
        when(arriendos.existsByCanchaIdAndFechaAndHoraAndEstado(1L, MANANA, 19, EstadoArriendo.CONFIRMADO))
                .thenReturn(false);
        when(arrendatarios.findByEmail("ana@correo.cl")).thenReturn(Optional.empty());
        when(arrendatarios.save(any(Arrendatario.class))).thenAnswer(i -> i.getArgument(0));
        when(arriendos.save(any(Arriendo.class))).thenAnswer(i -> i.getArgument(0));

        ArriendoResponse r = servicio.crear(solicitud(MANANA, 19));

        assertThat(r.precio()).isEqualTo(30000);
        assertThat(r.email()).isEqualTo("ana@correo.cl");
        assertThat(r.estado()).isEqualTo(EstadoArriendo.CONFIRMADO);
        assertThat(r.horaFin()).isEqualTo(20);
    }

    @Test
    void crearRechazaUnHorarioYaArrendado() {
        when(canchas.findById(1L)).thenReturn(Optional.of(cancha));
        when(arriendos.existsByCanchaIdAndFechaAndHoraAndEstado(1L, MANANA, 19, EstadoArriendo.CONFIRMADO))
                .thenReturn(true);

        assertThatThrownBy(() -> servicio.crear(solicitud(MANANA, 19)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya está arrendado");
        verify(arriendos, never()).save(any());
    }

    @Test
    void crearRechazaUnaCanchaInactiva() {
        cancha.setActiva(false);
        when(canchas.findById(1L)).thenReturn(Optional.of(cancha));

        assertThatThrownBy(() -> servicio.crear(solicitud(MANANA, 19)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("fuera de servicio");
    }

    @Test
    void crearRechazaUnaFechaPasada() {
        when(canchas.findById(1L)).thenReturn(Optional.of(cancha));

        assertThatThrownBy(() -> servicio.crear(solicitud(LocalDate.now().minusDays(1), 19)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya pasó");
    }

    @Test
    void crearRechazaUnaHoraFueraDelHorarioDeAtencion() {
        when(canchas.findById(1L)).thenReturn(Optional.of(cancha));

        assertThatThrownBy(() -> servicio.crear(solicitud(MANANA, 23)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("08:00 a 22:00");
    }

    @Test
    void crearConUnaCanchaInexistenteLanzaNoEncontrado() {
        when(canchas.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.crear(solicitud(MANANA, 19)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void modificarCambiaLaHoraSinChocarConSiMismo() {
        Arriendo arriendo = arriendoGuardadoDeAna();
        when(arriendos.findById(7L)).thenReturn(Optional.of(arriendo));
        when(canchas.findById(1L)).thenReturn(Optional.of(cancha));
        when(arriendos.existsByCanchaIdAndFechaAndHoraAndEstadoAndIdNot(1L, MANANA, 20, EstadoArriendo.CONFIRMADO, 7L))
                .thenReturn(false);

        ArriendoResponse r = servicio.modificar(7L, new CambioArriendoRequest("ana@correo.cl", 1L, MANANA, 20));

        assertThat(r.hora()).isEqualTo(20);
    }

    @Test
    void modificarUnArriendoAjenoResponde404() {
        when(arriendos.findById(7L)).thenReturn(Optional.of(arriendoGuardadoDeAna()));

        assertThatThrownBy(() -> servicio.modificar(7L, new CambioArriendoRequest("otro@correo.cl", 1L, MANANA, 20)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void cancelarMarcaElArriendoComoCancelado() {
        when(arriendos.findById(7L)).thenReturn(Optional.of(arriendoGuardadoDeAna()));

        ArriendoResponse r = servicio.cancelar(7L, "ANA@correo.cl");

        assertThat(r.estado()).isEqualTo(EstadoArriendo.CANCELADO);
    }

    @Test
    void noSePuedeCancelarDosVeces() {
        Arriendo arriendo = arriendoGuardadoDeAna();
        arriendo.cancelar();
        when(arriendos.findById(7L)).thenReturn(Optional.of(arriendo));

        assertThatThrownBy(() -> servicio.cancelar(7L, "ana@correo.cl"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya está cancelado");
    }

    @Test
    void elAdministradorPuedeCancelarCualquierArriendo() {
        when(arriendos.findById(7L)).thenReturn(Optional.of(arriendoGuardadoDeAna()));

        assertThat(servicio.cancelarComoAdmin(7L).estado()).isEqualTo(EstadoArriendo.CANCELADO);
    }

    @Test
    void laDisponibilidadExcluyeLasHorasOcupadas() {
        when(canchas.findByActivaTrueOrderByNombreAsc()).thenReturn(List.of(cancha));
        when(arriendos.findByFechaAndEstado(MANANA, EstadoArriendo.CONFIRMADO))
                .thenReturn(List.of(arriendoGuardadoDeAna())); // ocupa las 19:00

        List<DisponibilidadResponse> r = servicio.consultarDisponibilidad(MANANA);

        assertThat(r).hasSize(1);
        assertThat(r.get(0).horasLibres()).doesNotContain(19).contains(8, 18, 20, 21).hasSize(13);
    }
}
