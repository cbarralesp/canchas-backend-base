package cl.ipss.canchas.dto;

import cl.ipss.canchas.entity.Arriendo;
import cl.ipss.canchas.enums.EstadoArriendo;

import java.time.LocalDate;

/**
 * DTO de SALIDA: un arriendo listo para mostrar (incluye datos de la cancha y de la persona).
 * {@code modificable} le dice a la pantalla si debe ofrecer los botones "Modificar" y "Cancelar".
 */
public record ArriendoResponse(
        Long id,
        Long canchaId,
        String nombreCancha,
        LocalDate fecha,
        int hora,
        int horaFin,
        int precio,
        EstadoArriendo estado,
        String nombreArrendatario,
        String email,
        String telefono,
        boolean modificable) {

    public static ArriendoResponse from(Arriendo a) {
        return new ArriendoResponse(
                a.getId(),
                a.getCancha().getId(),
                a.getCancha().getNombre(),
                a.getFecha(),
                a.getHora(),
                a.getHora() + 1,
                a.getPrecio(),
                a.getEstado(),
                a.getArrendatario().getNombre(),
                a.getArrendatario().getEmail(),
                a.getArrendatario().getTelefono(),
                a.puedeModificarse());
    }
}
