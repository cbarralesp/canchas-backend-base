package cl.ipss.canchas.repository;

import cl.ipss.canchas.entity.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a datos de las canchas. Extender JpaRepository entrega gratis save, findById, findAll, count, etc.
 * Los demás métodos son "consultas derivadas": Spring Data genera el SQL a partir del nombre del método.
 */
public interface CanchaRepository extends JpaRepository<Cancha, Long> {

    /** Todas las canchas (también las inactivas) ordenadas por nombre: lista del administrador. */
    List<Cancha> findAllByOrderByNombreAsc();

    /** Solo las canchas en servicio: las que se ofrecen para arrendar. */
    List<Cancha> findByActivaTrueOrderByNombreAsc();

    /** ¿Ya existe una cancha con ese nombre? (sin distinguir mayúsculas). Se usa al crear. */
    boolean existsByNombreIgnoreCase(String nombre);

    /** Igual que el anterior pero ignorando a la propia cancha. Se usa al editar. */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
