package cl.ipss.canchas.dto;

import cl.ipss.canchas.entity.Cancha;
import cl.ipss.canchas.enums.Deporte;

/**
 * DTO de SALIDA: lo que se muestra de una cancha. Existe para no exponer la entidad JPA directamente
 * (así un cambio en la base no rompe a quien consume la API, y se evitan problemas con relaciones lazy).
 */
public record CanchaResponse(Long id, String nombre, Deporte deporte, int tarifaHora, boolean activa) {

    /** Convierte la entidad en DTO (reemplaza a una clase "mapper" aparte). */
    public static CanchaResponse from(Cancha c) {
        return new CanchaResponse(c.getId(), c.getNombre(), c.getDeporte(), c.getTarifaHora(), c.isActiva());
    }
}
