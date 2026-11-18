package cl.ipss.canchas.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Cuerpo JSON uniforme de todos los errores de la API REST. {@code detalles} lista los campos inválidos (puede ir vacío). */
public record ApiError(LocalDateTime fecha, int estado, String error, String mensaje, List<String> detalles) {

    public static ApiError de(int estado, String error, String mensaje, List<String> detalles) {
        return new ApiError(LocalDateTime.now(), estado, error, mensaje, detalles);
    }
}
