package cl.ipss.canchas.entity;

import cl.ipss.canchas.enums.Deporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad MODELO: una cancha que se puede arrendar (equivale a la "mesa" del restaurante).
 * Spring Data JPA la convierte en la tabla "cancha".
 *
 * El administrador puede cambiar su nombre, deporte y tarifa, y desactivarla (borrado lógico): una cancha
 * inactiva deja de aparecer en la disponibilidad pero sus arriendos antiguos se conservan.
 */
@Entity
@Table(name = "cancha")
public class Cancha {

    /** Clave primaria autogenerada por la base de datos (columna identity). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre visible; es único (la base lo exige con unique = true). */
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    /** Se guarda el NOMBRE del enum (texto) y no su posición, para que reordenar el enum no corrompa datos. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Deporte deporte;

    /** Precio de una hora de arriendo, en pesos chilenos (entero: el CLP no usa decimales). */
    @Column(name = "tarifa_hora", nullable = false)
    private int tarifaHora;

    /** false = fuera de servicio (no se puede arrendar). */
    @Column(nullable = false)
    private boolean activa = true;

    /** JPA exige un constructor sin argumentos. */
    protected Cancha() {
    }

    public Cancha(String nombre, Deporte deporte, int tarifaHora) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.tarifaHora = tarifaHora;
    }

    /** Actualiza los datos editables desde el formulario de administración. */
    public void actualizar(String nombre, Deporte deporte, int tarifaHora) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.tarifaHora = tarifaHora;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public int getTarifaHora() {
        return tarifaHora;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
