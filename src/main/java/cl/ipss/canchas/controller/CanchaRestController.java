package cl.ipss.canchas.controller;

import cl.ipss.canchas.dto.CanchaRequest;
import cl.ipss.canchas.dto.CanchaResponse;
import cl.ipss.canchas.service.CanchaService;
import jakarta.validation.Valid;
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
import java.util.List;

/**
 * CONTROLLER REST (JSON) de la administración de canchas: CRUD que consume el panel de administración de Angular.
 * Cada verbo HTTP corresponde a una operación: GET = leer, POST = crear, PUT = actualizar, PATCH = cambio parcial.
 */
@RestController
@RequestMapping("/api/canchas")
public class CanchaRestController {

    private final CanchaService servicio;

    public CanchaRestController(CanchaService servicio) {
        this.servicio = servicio;
    }

    /** GET /api/canchas  ->  todas las canchas (activas e inactivas). */
    @GetMapping
    public List<CanchaResponse> listar() {
        return servicio.listar();
    }

    /** GET /api/canchas/3  ->  una cancha (404 si no existe). */
    @GetMapping("/{id}")
    public CanchaResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    /** POST /api/canchas  ->  201 Created. Si el nombre ya existe responde 409. */
    @PostMapping
    public ResponseEntity<CanchaResponse> crear(@Valid @RequestBody CanchaRequest request) {
        CanchaResponse creada = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/canchas/" + creada.id())).body(creada);
    }

    /** PUT /api/canchas/3  ->  reemplaza nombre, deporte y tarifa. */
    @PutMapping("/{id}")
    public CanchaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CanchaRequest request) {
        return servicio.actualizar(id, request);
    }

    /** PATCH /api/canchas/3/estado?activa=false  ->  activa o desactiva la cancha. */
    @PatchMapping("/{id}/estado")
    public CanchaResponse cambiarEstado(@PathVariable Long id, @RequestParam boolean activa) {
        return servicio.cambiarEstado(id, activa);
    }
}
