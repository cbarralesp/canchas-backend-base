package cl.ipss.canchas.entity;

import cl.ipss.canchas.enums.EstadoArriendo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad MODELO: la reserva de una cancha, un día y una hora (equivale a la "reserva" del restaurante).
 * Une a un {@link Arrendatario} (quién) con una {@link Cancha} (qué) en una fecha y hora (cuándo).
 *
 * Los bloques son de 1 hora y se identifican por la hora de inicio: hora = 19 significa 19:00 a 20:00.
 */
@Entity
@Table(name = "arriendo")
public class Arriendo {

    /** Primer bloque del día: 08:00. Lo usan también las validaciones de los DTO. */
    public static final int HORA_MINIMA = 8;
    /** Último bloque que se puede iniciar: 21:00 (termina a las 22:00). */
    public static final int HORA_MAXIMA = 21;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Muchos arriendos pertenecen a una cancha. LAZY = la cancha se lee de la base solo cuando se usa,
     * así que las consultas que la necesitan la piden con @EntityGraph en el repository.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrendatario_id", nullable = false)
    private Arrendatario arrendatario;

    @Column(nullable = false)
    private LocalDate fecha;

    /** Hora de inicio del bloque (8 a 21). */
    @Column(name = "hora_inicio", nullable = false)
    private int hora;

    /** Precio cobrado: copia de la tarifa de la cancha al arrendar (si la tarifa sube después, no cambia). */
    @Column(nullable = false)
    private int precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoArriendo estado = EstadoArriendo.CONFIRMADO;

    protected Arriendo() {
    }

    public Arriendo(Cancha cancha, Arrendatario arrendatario, LocalDate fecha, int hora) {
        this.cancha = cancha;
        this.arrendatario = arrendatario;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = cancha.getTarifaHora();
    }

    /** Momento exacto en que comienza el arriendo. */
    public LocalDateTime inicio() {
        return LocalDateTime.of(fecha, LocalTime.of(hora, 0));
    }

    public boolean estaConfirmado() {
        return estado == EstadoArriendo.CONFIRMADO;
    }

    /** Se puede modificar o cancelar solo si sigue confirmado y todavía no comienza. */
    public boolean puedeModificarse() {
        return estaConfirmado() && inicio().isAfter(LocalDateTime.now());
    }

    /** Cambia de cancha, fecha y/u hora. El precio se recalcula con la tarifa de la cancha nueva. */
    public void reprogramar(Cancha nuevaCancha, LocalDate nuevaFecha, int nuevaHora) {
        this.cancha = nuevaCancha;
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
        this.precio = nuevaCancha.getTarifaHora();
    }

    /** Pasa el arriendo a CANCELADO; el horario queda libre. */
    public void cancelar() {
        this.estado = EstadoArriendo.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public Arrendatario getArrendatario() {
        return arrendatario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getHora() {
        return hora;
    }

    public int getPrecio() {
        return precio;
    }

    public EstadoArriendo getEstado() {
        return estado;
    }
}
