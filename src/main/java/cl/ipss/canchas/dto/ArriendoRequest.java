package cl.ipss.canchas.dto;

import cl.ipss.canchas.entity.Arriendo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de ENTRADA para CREAR un arriendo: qué cancha, cuándo y los datos de contacto de quien arrienda.
 */
public record ArriendoRequest(
        @NotNull(message = "Debes elegir una cancha")
        Long canchaId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        @Min(value = Arriendo.HORA_MINIMA, message = "El primer horario es a las 08:00")
        @Max(value = Arriendo.HORA_MAXIMA, message = "El último horario comienza a las 21:00")
        Integer hora,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite hasta 80 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email admite hasta 120 caracteres")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = Validaciones.TELEFONO, message = Validaciones.MENSAJE_TELEFONO)
        String telefono) {
}
