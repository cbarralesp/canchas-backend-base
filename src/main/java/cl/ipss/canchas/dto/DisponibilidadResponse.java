package cl.ipss.canchas.dto;

import java.util.List;

/**
 * DTO de SALIDA de la consulta de disponibilidad: una cancha y las horas de inicio que siguen libres
 * en la fecha consultada (por ejemplo [8, 9, 12, 19]). Las horas que no aparecen están ocupadas o ya pasaron.
 */
public record DisponibilidadResponse(CanchaResponse cancha, List<Integer> horasLibres) {
}
