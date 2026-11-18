package cl.ipss.canchas.controller;

import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.enums.EstadoArriendo;
import cl.ipss.canchas.exception.ReglaNegocioException;
import cl.ipss.canchas.service.ArriendoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de la capa WEB REST con {@code @WebMvcTest}: levanta solo el controller (no toda la aplicación)
 * y simula el service con {@code @MockitoBean}. Verifica códigos HTTP, validaciones y formato del error.
 */
@WebMvcTest(ArriendoRestController.class)
class ArriendoRestControllerTest {

    private static final String JSON_VALIDO = """
            {"canchaId":1,"fecha":"2030-01-10","hora":19,"nombre":"Ana","email":"ana@correo.cl","telefono":"+56912345678"}""";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ArriendoService servicio;

    private ArriendoResponse respuesta() {
        return new ArriendoResponse(5L, 1L, "Cancha 1", LocalDate.of(2030, 1, 10), 19, 20, 30000,
                EstadoArriendo.CONFIRMADO, "Ana", "ana@correo.cl", "+56912345678", true);
    }

    @Test
    void postValidoResponde201ConLocation() throws Exception {
        when(servicio.crear(any())).thenReturn(respuesta());

        mvc.perform(post("/api/arriendos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/arriendos/5"))
                .andExpect(jsonPath("$.precio").value(30000));
    }

    @Test
    void postConDatosInvalidosResponde400ConDetalle() throws Exception {
        String invalido = """
                {"canchaId":1,"fecha":"2030-01-10","hora":19,"nombre":"","email":"no-es-email","telefono":"abc"}""";

        mvc.perform(post("/api/arriendos").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.length()").value(3));
    }

    @Test
    void postConHorarioOcupadoResponde409() throws Exception {
        when(servicio.crear(any())).thenThrow(new ReglaNegocioException("Ese horario ya está arrendado. Elige otro"));

        mvc.perform(post("/api/arriendos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ese horario ya está arrendado. Elige otro"));
    }

    @Test
    void getDisponibilidadResponde200() throws Exception {
        when(servicio.consultarDisponibilidad(LocalDate.of(2030, 1, 10))).thenReturn(List.of());

        mvc.perform(get("/api/disponibilidad").param("fecha", "2030-01-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDisponibilidadSinFechaResponde400() throws Exception {
        mvc.perform(get("/api/disponibilidad")).andExpect(status().isBadRequest());
    }
}
