package cl.ipss.canchas.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Traduce las excepciones a respuestas JSON con el código HTTP correcto, para que los controllers REST no
 * tengan try/catch. {@code annotations = RestController.class} limita este manejador a la API: las páginas
 * Thymeleaf manejan sus errores por su cuenta.
 */
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404: no existe lo que se pidió. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException e) {
        log.debug("--- recurso no encontrado: {} ---", e.getMessage());
        return responder(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
    }

    /** 409: se rompió una regla de negocio. */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> reglaNegocio(ReglaNegocioException e) {
        log.debug("--- regla de negocio: {} ---", e.getMessage());
        return responder(HttpStatus.CONFLICT, e.getMessage(), List.of());
    }

    /** 400: falló una validación de Bean Validation (@NotBlank, @Email, @Min...). Se listan todos los campos con error. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException e) {
        List<String> detalles = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Hay datos inválidos en la solicitud", detalles);
    }

    /** 400: falta un parámetro, tiene un formato imposible (fecha mal escrita) o el JSON está mal formado. */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> solicitudMalFormada(Exception e) {
        return responder(HttpStatus.BAD_REQUEST, "La solicitud está mal formada: revisa los parámetros", List.of());
    }

    /** 500: error inesperado. Se registra completo en el log pero al cliente solo se le da un mensaje genérico. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> inesperado(Exception e) {
        log.error("--- error inesperado ---", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", List.of());
    }

    private ResponseEntity<ApiError> responder(HttpStatus estado, String mensaje, List<String> detalles) {
        return ResponseEntity.status(estado)
                .body(ApiError.de(estado.value(), estado.getReasonPhrase(), mensaje, detalles));
    }
}
