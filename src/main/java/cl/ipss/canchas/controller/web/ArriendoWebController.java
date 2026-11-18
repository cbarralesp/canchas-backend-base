package cl.ipss.canchas.controller.web;

import cl.ipss.canchas.controller.web.form.ArriendoForm;
import cl.ipss.canchas.controller.web.form.EditarArriendoForm;
import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.entity.Arriendo;
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
import java.util.List;
import java.util.stream.IntStream;

/**
 * CONTROLLER WEB (Spring MVC + Thymeleaf) del cliente: consultar disponibilidad, arrendar, ver, modificar y cancelar.
 *
 * Rol en MVC: recibe la petición, llama al service (Modelo) y devuelve el NOMBRE de una plantilla (Vista) junto con
 * los datos que esta necesita. No contiene reglas de negocio.
 *
 * Patrón Post/Redirect/Get: toda operación exitosa termina en "redirect:..." para que al refrescar la página
 * el navegador no repita el POST. Los mensajes de éxito o error viajan en "flash attributes".
 */
@Controller
@Hidden
@RequestMapping("/web")
public class ArriendoWebController {

    private final ArriendoService arriendos;
    private final CanchaService canchas;

    public ArriendoWebController(ArriendoService arriendos, CanchaService canchas) {
        this.arriendos = arriendos;
        this.canchas = canchas;
    }

    // ------------------------------------------------------------ disponibilidad

    /** GET /web/disponibilidad?fecha=... -> muestra las horas libres de cada cancha para la fecha (hoy por defecto). */
    @GetMapping("/disponibilidad")
    public String disponibilidad(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {
        LocalDate dia = (fecha == null || fecha.isBefore(LocalDate.now())) ? LocalDate.now() : fecha;
        model.addAttribute("fecha", dia);
        model.addAttribute("fechaMinima", LocalDate.now());
        model.addAttribute("horas", horasDelDia());
        model.addAttribute("disponibilidad", arriendos.consultarDisponibilidad(dia));
        return "disponibilidad";
    }

    // ------------------------------------------------------------ arrendar

    /** GET /web/arrendar?canchaId=&fecha=&hora= -> formulario con la cancha y el horario ya elegidos. */
    @GetMapping("/arrendar")
    public String formularioArrendar(@RequestParam Long canchaId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                     @RequestParam Integer hora, Model model, RedirectAttributes flash) {
        ArriendoForm form = new ArriendoForm();
        form.setCanchaId(canchaId);
        form.setFecha(fecha);
        form.setHora(hora);
        model.addAttribute("form", form);
        return mostrarFormularioArrendar(model, canchaId, flash);
    }

    /**
     * POST /web/arrendar -> crea el arriendo.
     * Si la validación falla (BindingResult) o se rompe una regla de negocio NO se redirige: se devuelve la misma
     * vista con el mensaje, y el formulario conserva lo que la persona escribió.
     */
    @PostMapping("/arrendar")
    public String arrendar(@Valid @ModelAttribute("form") ArriendoForm form, BindingResult errores,
                           Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrarFormularioArrendar(model, form.getCanchaId(), flash);
        }
        try {
            ArriendoResponse creado = arriendos.crear(form.toRequest());
            flash.addFlashAttribute("exito", "¡Arriendo confirmado! " + creado.nombreCancha() + ", "
                    + creado.fecha() + " a las " + creado.hora() + ":00.");
            flash.addAttribute("email", creado.email());
            return "redirect:/web/mis-arriendos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormularioArrendar(model, form.getCanchaId(), flash);
        }
    }

    // ------------------------------------------------------------ mis arriendos

    /** GET /web/mis-arriendos?email=... -> lista los arriendos de esa persona (sin email solo muestra el buscador). */
    @GetMapping("/mis-arriendos")
    public String misArriendos(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        if (email != null && !email.isBlank()) {
            model.addAttribute("arriendos", arriendos.listarPorEmail(email));
        }
        return "mis-arriendos";
    }

    // ------------------------------------------------------------ modificar

    /** GET /web/arriendos/5/editar?email=... -> formulario relleno con los datos actuales del arriendo. */
    @GetMapping("/arriendos/{id}/editar")
    public String formularioEditar(@PathVariable Long id, @RequestParam String email, Model model,
                                   RedirectAttributes flash) {
        try {
            model.addAttribute("form", EditarArriendoForm.desde(arriendos.obtener(id, email)));
            return mostrarFormularioEditar(model, id);
        } catch (RecursoNoEncontradoException e) {
            return volverAMisArriendos(email, "error", e.getMessage(), flash);
        }
    }

    /** POST /web/arriendos/5/editar -> guarda los cambios (o vuelve al formulario con el error). */
    @PostMapping("/arriendos/{id}/editar")
    public String editar(@PathVariable Long id, @Valid @ModelAttribute("form") EditarArriendoForm form,
                         BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrarFormularioEditar(model, id);
        }
        try {
            arriendos.modificar(id, form.toRequest());
            return volverAMisArriendos(form.getEmail(), "exito", "Arriendo modificado correctamente", flash);
        } catch (RecursoNoEncontradoException e) {
            return volverAMisArriendos(form.getEmail(), "error", e.getMessage(), flash);
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormularioEditar(model, id);
        }
    }

    // ------------------------------------------------------------ cancelar

    /** POST /web/arriendos/5/cancelar -> cancela el arriendo y vuelve a "Mis arriendos" con un aviso. */
    @PostMapping("/arriendos/{id}/cancelar")
    public String cancelar(@PathVariable Long id, @RequestParam String email, RedirectAttributes flash) {
        try {
            arriendos.cancelar(id, email);
            return volverAMisArriendos(email, "exito", "Arriendo cancelado. El horario quedó libre", flash);
        } catch (RecursoNoEncontradoException | ReglaNegocioException e) {
            return volverAMisArriendos(email, "error", e.getMessage(), flash);
        }
    }

    // ------------------------------------------------------------ auxiliares

    /** Prepara los datos que necesita la plantilla "arrendar" y la devuelve. */
    private String mostrarFormularioArrendar(Model model, Long canchaId, RedirectAttributes flash) {
        try {
            model.addAttribute("cancha", canchas.obtener(canchaId));
            return "arrendar";
        } catch (RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/disponibilidad";
        }
    }

    /** Prepara los datos que necesita la plantilla "editar-arriendo": id, canchas activas y lista de horas. */
    private String mostrarFormularioEditar(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("canchas", canchas.listar().stream().filter(c -> c.activa()).toList());
        model.addAttribute("horas", horasDelDia());
        model.addAttribute("fechaMinima", LocalDate.now());
        return "editar-arriendo";
    }

    /** Horas de inicio posibles en el día (8 a 21), para dibujar la grilla y la lista desplegable. */
    private List<Integer> horasDelDia() {
        return IntStream.rangeClosed(Arriendo.HORA_MINIMA, Arriendo.HORA_MAXIMA).boxed().toList();
    }

    /** Redirige a "Mis arriendos" del email dado, dejando un mensaje ("exito" o "error") para mostrar allí. */
    private String volverAMisArriendos(String email, String tipoMensaje, String mensaje, RedirectAttributes flash) {
        flash.addFlashAttribute(tipoMensaje, mensaje);
        flash.addAttribute("email", email);
        return "redirect:/web/mis-arriendos";
    }
}
