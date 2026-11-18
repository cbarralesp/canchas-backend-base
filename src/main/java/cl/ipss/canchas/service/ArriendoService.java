package cl.ipss.canchas.service;

import cl.ipss.canchas.dto.ArriendoRequest;
import cl.ipss.canchas.dto.ArriendoResponse;
import cl.ipss.canchas.dto.CambioArriendoRequest;
import cl.ipss.canchas.dto.DisponibilidadResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Casos de uso de los arriendos: consultar disponibilidad, arrendar, ver, modificar y cancelar.
 * Aquí viven las reglas de negocio; los controllers solo reciben la petición y devuelven la respuesta.
 */
public interface ArriendoService {

    /** Para cada cancha activa, las horas libres de la fecha. Excluye horas ocupadas y horas que ya pasaron. */
    List<DisponibilidadResponse> consultarDisponibilidad(LocalDate fecha);

    /**
     * Crea un arriendo CONFIRMADO.
     * @throws cl.ipss.canchas.exception.ReglaNegocioException si la cancha está inactiva, la fecha ya pasó
     *         o el horario está ocupado
     */
    ArriendoResponse crear(ArriendoRequest request);

    /** Un arriendo del cliente. @throws cl.ipss.canchas.exception.RecursoNoEncontradoException si no existe o es de otra persona */
    ArriendoResponse obtener(Long id, String email);

    /** Arriendos de una persona (por email), del más reciente al más antiguo. */
    List<ArriendoResponse> listarPorEmail(String email);

    /** Cambia cancha, fecha y/u hora de un arriendo confirmado que aún no comienza. */
    ArriendoResponse modificar(Long id, CambioArriendoRequest request);

    /** El cliente cancela su arriendo (el horario queda libre). */
    ArriendoResponse cancelar(Long id, String email);

    /** Agenda del administrador. Con fecha = solo ese día; con fecha null = todos los arriendos. */
    List<ArriendoResponse> listarAgenda(LocalDate fecha);

    /** El administrador cancela cualquier arriendo confirmado, sin verificar el email. */
    ArriendoResponse cancelarComoAdmin(Long id);
}
