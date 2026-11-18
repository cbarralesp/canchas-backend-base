package cl.ipss.canchas.dto;

import cl.ipss.canchas.entity.Arriendo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO de ENTRADA para MODIFICAR un arriendo: la nueva cancha, fecha y hora.
 * El email no se cambia: sirve para comprobar que el arriendo pertenece a quien lo pide.
 */
public record CambioArriendoRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotNull(message = "Debes elegir una cancha")
        Long canchaId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        @Min(value = Arriendo.HORA_MINIMA, message = "El primer horario es a las 08:00")
        @Max(value = Arriendo.HORA_MAXIMA, message = "El último horario comienza a las 21:00")
        Integer hora) {
}
