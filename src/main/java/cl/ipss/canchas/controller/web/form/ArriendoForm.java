package cl.ipss.canchas.controller.web.form;

import cl.ipss.canchas.dto.ArriendoRequest;
import cl.ipss.canchas.dto.Validaciones;
import cl.ipss.canchas.entity.Arriendo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Objeto de formulario (backing bean) de la pantalla "Arrendar". Thymeleaf lo enlaza con {@code th:object} y
 * {@code th:field}: al enviar el formulario Spring llena estos campos por sus setters y valida las anotaciones.
 * Es una clase con getters y setters (no un record) porque Thymeleaf necesita leer y escribir propiedades JavaBean.
 */
public class ArriendoForm {

    @NotNull(message = "Debes elegir una cancha")
    private Long canchaId;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Min(value = Arriendo.HORA_MINIMA, message = "El primer horario es a las 08:00")
    @Max(value = Arriendo.HORA_MAXIMA, message = "El último horario comienza a las 21:00")
    private Integer hora;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre admite hasta 80 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 120, message = "El email admite hasta 120 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = Validaciones.TELEFONO, message = Validaciones.MENSAJE_TELEFONO)
    private String telefono;

    /** Convierte el formulario en el DTO que entiende el service (el service no conoce los formularios). */
    public ArriendoRequest toRequest() {
        return new ArriendoRequest(canchaId, fecha, hora, nombre, email, telefono);
    }

    public Long getCanchaId() {
        return canchaId;
    }

    public void setCanchaId(Long canchaId) {
        this.canchaId = canchaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
