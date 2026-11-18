package cl.ipss.canchas.controller;

import cl.ipss.canchas.dto.ArriendoRequest;
import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.dto.CambioArriendoRequest;
import cl.ipss.canchas.dto.DisponibilidadResponse;
import cl.ipss.canchas.service.ArriendoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * CONTROLLER REST (JSON) de los arriendos: lo consume el frontend Angular.
 *
 * Un controller delgado: valida la entrada (@Valid), llama al service y elige el código HTTP.
 * No contiene reglas de negocio ni accede a la base de datos. Los errores (400, 404, 409) los convierte
 * GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api")
public class ArriendoRestController {

    private final ArriendoService servicio;

    public ArriendoRestController(ArriendoService servicio) {
        this.servicio = servicio;
    }

    /** GET /api/disponibilidad?fecha=2026-12-01  ->  200 con las horas libres de cada cancha activa. */
    @GetMapping("/disponibilidad")
    public List<DisponibilidadResponse> disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return servicio.consultarDisponibilidad(fecha);
    }

    /** POST /api/arriendos  ->  201 Created con el arriendo y la cabecera Location apuntando a él. */
    @PostMapping("/arriendos")
    public ResponseEntity<ArriendoResponse> crear(@Valid @RequestBody ArriendoRequest request) {
        ArriendoResponse creado = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/arriendos/" + creado.id())).body(creado);
    }

    /** GET /api/arriendos?email=ana@correo.cl  ->  "Mis arriendos" de esa persona. */
    @GetMapping("/arriendos")
    public List<ArriendoResponse> misArriendos(@RequestParam String email) {
        return servicio.listarPorEmail(email);
    }

    /** GET /api/arriendos/5?email=...  ->  un arriendo (404 si no existe o es de otra persona). */
    @GetMapping("/arriendos/{id}")
    public ArriendoResponse obtener(@PathVariable Long id, @RequestParam String email) {
        return servicio.obtener(id, email);
    }

    /** PUT /api/arriendos/5  ->  modifica cancha, fecha y hora. */
    @PutMapping("/arriendos/{id}")
    public ArriendoResponse modificar(@PathVariable Long id, @Valid @RequestBody CambioArriendoRequest request) {
        return servicio.modificar(id, request);
    }

    /** PATCH /api/arriendos/5/cancelacion?email=...  ->  el cliente cancela su arriendo. */
    @PatchMapping("/arriendos/{id}/cancelacion")
    public ArriendoResponse cancelar(@PathVariable Long id, @RequestParam String email) {
        return servicio.cancelar(id, email);
    }

    /** GET /api/admin/arriendos?fecha=...  ->  agenda del administrador (sin fecha = todos los arriendos). */
    @GetMapping("/admin/arriendos")
    public List<ArriendoResponse> agenda(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return servicio.listarAgenda(fecha);
    }

    /** PATCH /api/admin/arriendos/5/cancelacion  ->  el administrador cancela cualquier arriendo. */
    @PatchMapping("/admin/arriendos/{id}/cancelacion")
    public ArriendoResponse cancelarComoAdmin(@PathVariable Long id) {
        return servicio.cancelarComoAdmin(id);
    }
}
