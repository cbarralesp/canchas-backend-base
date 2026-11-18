package cl.ipss.canchas.controller;

import cl.ipss.canchas.controller.web.ArriendoWebController;
import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.dto.CanchaResponse;
import cl.ipss.canchas.enums.Deporte;
import cl.ipss.canchas.enums.EstadoArriendo;
import cl.ipss.canchas.exception.ReglaNegocioException;
import cl.ipss.canchas.service.ArriendoService;
import cl.ipss.canchas.service.CanchaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** Prueba del controller Thymeleaf: nombre de la vista, datos del modelo, patrón Post/Redirect/Get y errores. */
@WebMvcTest(ArriendoWebController.class)
class ArriendoWebControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ArriendoService arriendos;
    @MockitoBean
    private CanchaService canchas;

    private final CanchaResponse cancha = new CanchaResponse(1L, "Cancha 1", Deporte.FUTBOL, 30000, true);

    private ArriendoResponse arriendo() {
        return new ArriendoResponse(5L, 1L, "Cancha 1", LocalDate.of(2030, 1, 10), 19, 20, 30000,
                EstadoArriendo.CONFIRMADO, "Ana", "ana@correo.cl", "+56912345678", true);
    }

    @Test
    void disponibilidadMuestraLaVistaConLasCanchas() throws Exception {
        when(arriendos.consultarDisponibilidad(any())).thenReturn(List.of());

        mvc.perform(get("/web/disponibilidad"))
                .andExpect(status().isOk())
                .andExpect(view().name("disponibilidad"))
                .andExpect(model().attributeExists("fecha", "horas", "disponibilidad"));
    }

    @Test
    void arrendarValidoRedirigeAMisArriendos() throws Exception {
        when(arriendos.crear(any())).thenReturn(arriendo());

        mvc.perform(post("/web/arrendar")
                        .param("canchaId", "1").param("fecha", "2030-01-10").param("hora", "19")
                        .param("nombre", "Ana").param("email", "ana@correo.cl").param("telefono", "+56912345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/mis-arriendos?email=ana%40correo.cl"));
    }

    @Test
    void arrendarConDatosInvalidosVuelveAlFormularioSinLlamarAlService() throws Exception {
        when(canchas.obtener(1L)).thenReturn(cancha);

        mvc.perform(post("/web/arrendar")
                        .param("canchaId", "1").param("fecha", "2030-01-10").param("hora", "19")
                        .param("nombre", "").param("email", "mal").param("telefono", "x"))
                .andExpect(status().isOk())
                .andExpect(view().name("arrendar"))
                .andExpect(model().attributeHasFieldErrors("form", "nombre", "email", "telefono"));
        verify(arriendos, never()).crear(any());
    }

    @Test
    void arrendarConHorarioOcupadoMuestraElErrorSinRedirigir() throws Exception {
        when(canchas.obtener(1L)).thenReturn(cancha);
        when(arriendos.crear(any())).thenThrow(new ReglaNegocioException("Ese horario ya está arrendado. Elige otro"));

        mvc.perform(post("/web/arrendar")
                        .param("canchaId", "1").param("fecha", "2030-01-10").param("hora", "19")
                        .param("nombre", "Ana").param("email", "ana@correo.cl").param("telefono", "+56912345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("arrendar"))
                .andExpect(model().attribute("error", "Ese horario ya está arrendado. Elige otro"));
    }

    @Test
    void cancelarRedirigeConMensajeDeExito() throws Exception {
        mvc.perform(post("/web/arriendos/5/cancelar").param("email", "ana@correo.cl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/mis-arriendos?email=ana%40correo.cl"));
        verify(arriendos).cancelar(5L, "ana@correo.cl");
    }
}
