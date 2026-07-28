package daw2a.gestioneventos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;
import daw2a.gestioneventos.servicio.EventoServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoControlador.class)
class EventoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoServicio eventoServicio;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listShouldReturnEvents() throws Exception {
        EventoResponseDTO dto = new EventoResponseDTO(1L, "Prueba", null, null, null);
        Page<EventoResponseDTO> page = new PageImpl<>(java.util.List.of(dto));
        when(eventoServicio.listarEventos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/eventos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        // Datos de entrada y salida
        EventoRequestDTO in = new EventoRequestDTO();
        in.setNombre("Nuevo");
        in.setDescripcion("X");
        in.setOrganizadorId(99L);

        EventoResponseDTO saved = new EventoResponseDTO(10L, "Nuevo", null, null, 99L);
        when(eventoServicio.crearEvento(any(EventoRequestDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }
}
