package cl.ipss.canchas.controller.web.form;

import cl.ipss.canchas.dto.CanchaRequest;
import cl.ipss.canchas.dto.CanchaResponse;
import cl.ipss.canchas.enums.Deporte;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Objeto de formulario para crear y editar canchas en el panel de administración. */
public class CanchaForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre admite hasta 60 caracteres")
    private String nombre;

    @NotNull(message = "Debes elegir un deporte")
    private Deporte deporte;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @Min(value = 5000, message = "La tarifa mínima es $5.000")
    @Max(value = 100000, message = "La tarifa máxima es $100.000")
    private Integer tarifaHora;

    /** Crea el formulario ya relleno con los datos de una cancha existente (pantalla de edición). */
    public static CanchaForm desde(CanchaResponse cancha) {
        CanchaForm form = new CanchaForm();
        form.nombre = cancha.nombre();
        form.deporte = cancha.deporte();
        form.tarifaHora = cancha.tarifaHora();
        return form;
    }

    public CanchaRequest toRequest() {
        return new CanchaRequest(nombre, deporte, tarifaHora);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    public Integer getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(Integer tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
}
