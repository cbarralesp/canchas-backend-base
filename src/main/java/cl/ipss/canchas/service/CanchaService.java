package cl.ipss.canchas.service;

import cl.ipss.canchas.dto.CanchaRequest;
import cl.ipss.canchas.dto.CanchaResponse;

import java.util.List;

/**
 * Casos de uso de la gestión de canchas (CRUD del administrador).
 * Los controllers dependen de esta interfaz, no de la implementación: así se pueden probar con un mock.
 */
public interface CanchaService {

    /** Todas las canchas, activas e inactivas, ordenadas por nombre. */
    List<CanchaResponse> listar();

    /** Una cancha por id. @throws cl.ipss.canchas.exception.RecursoNoEncontradoException si no existe */
    CanchaResponse obtener(Long id);

    /** Crea una cancha activa. @throws cl.ipss.canchas.exception.ReglaNegocioException si el nombre ya existe */
    CanchaResponse crear(CanchaRequest request);

    /** Edita nombre, deporte y tarifa. Los arriendos ya hechos conservan el precio que pagaron. */
    CanchaResponse actualizar(Long id, CanchaRequest request);

    /** Activa o desactiva una cancha (borrado lógico). */
    CanchaResponse cambiarEstado(Long id, boolean activa);
}
