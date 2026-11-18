package cl.ipss.canchas.repository;

import cl.ipss.canchas.entity.Arriendo;
import cl.ipss.canchas.enums.EstadoArriendo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Acceso a datos de los arriendos.
 *
 * {@code @EntityGraph} le dice a JPA que cargue la cancha y el arrendatario en la MISMA consulta (con un join).
 * Sin él, mostrar una lista de N arriendos haría N consultas extra (el problema "N+1").
 */
public interface ArriendoRepository extends JpaRepository<Arriendo, Long> {

    /** ¿Hay un arriendo en ese estado para esa cancha, fecha y hora? Base de la regla "no doble arriendo". */
    boolean existsByCanchaIdAndFechaAndHoraAndEstado(Long canchaId, LocalDate fecha, int hora, EstadoArriendo estado);

    /** Igual que el anterior pero ignorando un arriendo (el que se está modificando, para que no choque consigo mismo). */
    boolean existsByCanchaIdAndFechaAndHoraAndEstadoAndIdNot(Long canchaId, LocalDate fecha, int hora,
                                                             EstadoArriendo estado, Long id);

    /** Arriendos de un día en un estado. Con ellos se calculan las horas ocupadas de todas las canchas en una sola consulta. */
    @EntityGraph(attributePaths = "cancha")
    List<Arriendo> findByFechaAndEstado(LocalDate fecha, EstadoArriendo estado);

    /** "Mis arriendos": los de una persona, del más reciente al más antiguo. */
    @EntityGraph(attributePaths = {"cancha", "arrendatario"})
    List<Arriendo> findByArrendatarioEmailOrderByFechaDescHoraDesc(String email);

    /** Agenda del administrador filtrada por un día, ordenada por hora. */
    @EntityGraph(attributePaths = {"cancha", "arrendatario"})
    List<Arriendo> findByFechaOrderByHoraAsc(LocalDate fecha);

    /** Agenda completa del administrador: todos los arriendos, del más reciente al más antiguo. */
    @EntityGraph(attributePaths = {"cancha", "arrendatario"})
    List<Arriendo> findAllByOrderByFechaDescHoraDesc();
}
