package cl.ipss.canchas.repository;

import cl.ipss.canchas.entity.Arrendatario;
import cl.ipss.canchas.entity.Arriendo;
import cl.ipss.canchas.entity.Cancha;
import cl.ipss.canchas.enums.Deporte;
import cl.ipss.canchas.enums.EstadoArriendo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de la capa de datos con {@code @DataJpaTest}: levanta solo JPA con una base H2 en memoria
 * y comprueba que las consultas derivadas de los repositories devuelven lo esperado.
 */
@DataJpaTest
class ArriendoRepositoryTest {

    private static final LocalDate DIA = LocalDate.of(2030, 1, 10);

    @Autowired
    private ArriendoRepository arriendos;
    @Autowired
    private CanchaRepository canchas;
    @Autowired
    private ArrendatarioRepository arrendatarios;

    private Cancha cancha;
    private Arrendatario ana;

    @BeforeEach
    void datos() {
        cancha = canchas.save(new Cancha("Cancha 1", Deporte.FUTBOL, 30000));
        ana = arrendatarios.save(new Arrendatario("Ana", "ana@correo.cl", "+56912345678"));
    }

    @Test
    void existsDetectaUnHorarioConfirmadoYIgnoraUnoCancelado() {
        Arriendo confirmado = arriendos.save(new Arriendo(cancha, ana, DIA, 19));
        Arriendo cancelado = new Arriendo(cancha, ana, DIA, 20);
        cancelado.cancelar();
        arriendos.save(cancelado);

        assertThat(arriendos.existsByCanchaIdAndFechaAndHoraAndEstado(cancha.getId(), DIA, 19, EstadoArriendo.CONFIRMADO)).isTrue();
        assertThat(arriendos.existsByCanchaIdAndFechaAndHoraAndEstado(cancha.getId(), DIA, 20, EstadoArriendo.CONFIRMADO)).isFalse();
        // excluyéndose a sí mismo, el horario 19 queda libre (caso "modificar")
        assertThat(arriendos.existsByCanchaIdAndFechaAndHoraAndEstadoAndIdNot(
                cancha.getId(), DIA, 19, EstadoArriendo.CONFIRMADO, confirmado.getId())).isFalse();
    }

    @Test
    void findByFechaAndEstadoSoloTraeLosConfirmadosDelDia() {
        arriendos.save(new Arriendo(cancha, ana, DIA, 19));
        arriendos.save(new Arriendo(cancha, ana, DIA.plusDays(1), 19));

        List<Arriendo> r = arriendos.findByFechaAndEstado(DIA, EstadoArriendo.CONFIRMADO);

        assertThat(r).hasSize(1);
        assertThat(r.get(0).getCancha().getNombre()).isEqualTo("Cancha 1");
    }

    @Test
    void misArriendosVienenDelMasRecienteAlMasAntiguo() {
        arriendos.save(new Arriendo(cancha, ana, DIA, 9));
        arriendos.save(new Arriendo(cancha, ana, DIA.plusDays(2), 9));
        arriendos.save(new Arriendo(cancha, ana, DIA.plusDays(1), 9));

        List<Arriendo> r = arriendos.findByArrendatarioEmailOrderByFechaDescHoraDesc("ana@correo.cl");

        assertThat(r).extracting(Arriendo::getFecha).containsExactly(DIA.plusDays(2), DIA.plusDays(1), DIA);
    }

    @Test
    void elNombreDeCanchaSeComparaSinDistinguirMayusculas() {
        assertThat(canchas.existsByNombreIgnoreCase("CANCHA 1")).isTrue();
        assertThat(canchas.existsByNombreIgnoreCaseAndIdNot("cancha 1", cancha.getId())).isFalse();
    }
}
