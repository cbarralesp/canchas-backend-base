package cl.ipss.canchas.controller.web.form;

import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.dto.CambioArriendoRequest;
import cl.ipss.canchas.entity.Arriendo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Objeto de formulario de la pantalla "Modificar arriendo": nueva cancha, fecha y hora. El email viaja oculto. */
public class EditarArriendoForm {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotNull(message = "Debes elegir una cancha")
    private Long canchaId;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Min(value = Arriendo.HORA_MINIMA, message = "El primer horario es a las 08:00")
    @Max(value = Arriendo.HORA_MAXIMA, message = "El último horario comienza a las 21:00")
    private Integer hora;

    /** Crea el formulario ya relleno con los datos actuales del arriendo. */
    public static EditarArriendoForm desde(ArriendoResponse arriendo) {
        EditarArriendoForm form = new EditarArriendoForm();
        form.email = arriendo.email();
        form.canchaId = arriendo.canchaId();
        form.fecha = arriendo.fecha();
        form.hora = arriendo.hora();
        return form;
    }

    public CambioArriendoRequest toRequest() {
        return new CambioArriendoRequest(email, canchaId, fecha, hora);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
