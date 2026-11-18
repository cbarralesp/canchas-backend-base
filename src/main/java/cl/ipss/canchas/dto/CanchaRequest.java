package cl.ipss.canchas.dto;

import cl.ipss.canchas.enums.Deporte;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA: datos que envía el administrador para crear o editar una cancha.
 * Es un record (clase inmutable); las anotaciones se validan cuando el controller usa @Valid.
 */
public record CanchaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60, message = "El nombre admite hasta 60 caracteres")
        String nombre,

        @NotNull(message = "Debes elegir un deporte")
        Deporte deporte,

        @NotNull(message = "La tarifa por hora es obligatoria")
        @Min(value = 5000, message = "La tarifa mínima es $5.000")
        @Max(value = 100000, message = "La tarifa máxima es $100.000")
        Integer tarifaHora) {
}
