package cl.ipss.canchas.controller.web;

import cl.ipss.canchas.controller.web.form.CanchaForm;
import cl.ipss.canchas.enums.Deporte;
import cl.ipss.canchas.exception.RecursoNoEncontradoException;
import cl.ipss.canchas.exception.ReglaNegocioException;
import cl.ipss.canchas.service.ArriendoService;
import cl.ipss.canchas.service.CanchaService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * CONTROLLER WEB del administrador (personal del recinto): configurar las canchas y ver/cancelar los arriendos.
 * Igual que el controller del cliente, es delgado: delega todo en los services y devuelve vistas Thymeleaf.
 * (El sistema no tiene inicio de sesión: la autenticación queda fuera del alcance de la evaluación.)
 */
@Controller
@Hidden
@RequestMapping("/web/admin")
public class AdminWebController {

    private final CanchaService canchas;
    private final ArriendoService arriendos;

    public AdminWebController(CanchaService canchas, ArriendoService arriendos) {
        this.canchas = canchas;
        this.arriendos = arriendos;
    }

    // ------------------------------------------------------------ canchas (CRUD)

    /** GET /web/admin/canchas -> lista de canchas + formulario para crear una nueva. */
    @GetMapping("/canchas")
    public String listarCanchas(Model model) {
        model.addAttribute("form", new CanchaForm());
        return mostrarCanchas(model);
    }

    /** POST /web/admin/canchas -> crea una cancha. */
    @PostMapping("/canchas")
    public String crearCancha(@Valid @ModelAttribute("form") CanchaForm form, BindingResult errores,
                              Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrarCanchas(model);
        }
        try {
            canchas.crear(form.toRequest());
            flash.addFlashAttribute("exito", "Cancha creada correctamente");
            return "redirect:/web/admin/canchas";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarCanchas(model);
        }
    }

    /** GET /web/admin/canchas/3/editar -> formulario de edición con los datos actuales. */
    @GetMapping("/canchas/{id}/editar")
    public String formularioEditarCancha(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("form", CanchaForm.desde(canchas.obtener(id)));
            return mostrarEdicion(model, id);
        } catch (RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/admin/canchas";
        }
    }

    /** POST /web/admin/canchas/3/editar -> guarda los cambios. */
    @PostMapping("/canchas/{id}/editar")
    public String editarCancha(@PathVariable Long id, @Valid @ModelAttribute("form") CanchaForm form,
                               BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrarEdicion(model, id);
        }
        try {
            canchas.actualizar(id, form.toRequest());
            flash.addFlashAttribute("exito", "Cancha actualizada correctamente");
            return "redirect:/web/admin/canchas";
        } catch (RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/admin/canchas";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarEdicion(model, id);
        }
    }

    /** POST /web/admin/canchas/3/estado?activa=false -> activa o desactiva la cancha. */
    @PostMapping("/canchas/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam boolean activa, RedirectAttributes flash) {
        try {
            canchas.cambiarEstado(id, activa);
            flash.addFlashAttribute("exito", activa ? "Cancha activada" : "Cancha desactivada");
        } catch (RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/admin/canchas";
    }

    // ------------------------------------------------------------ arriendos

    /** GET /web/admin/arriendos?fecha=... -> todos los arriendos, o solo los del día elegido. */
    @GetMapping("/arriendos")
    public String agenda(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                         Model model) {
        model.addAttribute("fecha", fecha);
        model.addAttribute("arriendos", arriendos.listarAgenda(fecha));
        return "admin/arriendos";
    }

    /** POST /web/admin/arriendos/5/cancelar -> el administrador cancela cualquier arriendo. */
    @PostMapping("/arriendos/{id}/cancelar")
    public String cancelarArriendo(@PathVariable Long id,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                   RedirectAttributes flash) {
        try {
            arriendos.cancelarComoAdmin(id);
            flash.addFlashAttribute("exito", "Arriendo cancelado");
        } catch (RecursoNoEncontradoException | ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        if (fecha != null) {
            flash.addAttribute("fecha", fecha);
        }
        return "redirect:/web/admin/arriendos";
    }

    // ------------------------------------------------------------ auxiliares

    private String mostrarCanchas(Model model) {
        model.addAttribute("canchas", canchas.listar());
        model.addAttribute("deportes", Deporte.values());
        return "admin/canchas";
    }

    private String mostrarEdicion(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("deportes", Deporte.values());
        return "admin/cancha-editar";
    }
}
