package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;
import daw2a.gestioneventos.exception.EventoDuplicadoException;
import daw2a.gestioneventos.exception.EventoNoEncontradoException;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class EventoServicioTest {

    @Mock
    private EventoRepo eventoRepo;

    @Mock
    private OrganizadorRepo organizadorRepo;

    @InjectMocks
    private EventoServicio eventoServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarEventosShouldDelegateToRepo() {
        java.time.LocalDateTime inicio = java.time.LocalDateTime.now();
        java.time.LocalDateTime fin = inicio.plusDays(1);
        Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").fechaInicio(inicio).fechaFin(fin).build();
        Pageable pageable = PageRequest.of(0, 10);
        when(eventoRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(e)));

        Page<EventoResponseDTO> result = eventoServicio.listarEventos(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(eventoRepo).findAll(any(Pageable.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void listarEventosShouldReturnEmptyPageWhenEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(eventoRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        Page<EventoResponseDTO> result = eventoServicio.listarEventos(pageable);

        assertThat(result.getContent()).isEmpty();
        verify(eventoRepo).findAll(any(Pageable.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void obtenEventoPorIdShouldReturnDTOWhenExists() {
        java.time.LocalDateTime inicio = java.time.LocalDateTime.now();
        java.time.LocalDateTime fin = inicio.plusDays(1);
        Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").fechaInicio(inicio).fechaFin(fin).build();
        when(eventoRepo.findById(1L)).thenReturn(Optional.of(e));

        EventoResponseDTO found = eventoServicio.obtenEventoPorId(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(eventoRepo).findById(1L);
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void obtenEventoPorIdShouldThrowWhenNotExists() {
        when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));

        verify(eventoRepo).findById(99L);
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void crearEventoShouldThrowWhenNombreYaExiste() {
        EventoRequestDTO nuevo = new EventoRequestDTO();
        nuevo.setNombre("Duplicado");
        when(eventoRepo.existsByNombre("Duplicado")).thenReturn(true);

        assertThatExceptionOfType(EventoDuplicadoException.class)
                .isThrownBy(() -> eventoServicio.crearEvento(nuevo));

        verify(eventoRepo).existsByNombre("Duplicado");
        verify(eventoRepo, never()).save(any(Evento.class));
        verifyNoMoreInteractions(organizadorRepo);
    }

    @Test
    void crearEventoShouldSaveWhenNombreNoExiste() {
        EventoRequestDTO nuevo = new EventoRequestDTO();
        nuevo.setNombre("Nuevo");
        nuevo.setDescripcion("X");
        nuevo.setOrganizadorId(99L);

        Organizador org = Organizador.builder().id(99L).nombre("ORG").build();
        java.time.LocalDateTime inicio = java.time.LocalDateTime.now();
        java.time.LocalDateTime fin = inicio.plusDays(1);
        Evento guardado = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").organizador(org).fechaInicio(inicio).fechaFin(fin).build();

        when(eventoRepo.existsByNombre("Nuevo")).thenReturn(false);
        when(organizadorRepo.findById(99L)).thenReturn(Optional.of(org));
        when(eventoRepo.save(any(Evento.class))).thenReturn(guardado);

        EventoResponseDTO creado = eventoServicio.crearEvento(nuevo);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(10L);
        verify(eventoRepo).existsByNombre("Nuevo");
        verify(organizadorRepo).findById(99L);
        verify(eventoRepo).save(any(Evento.class));
    }

    @Test
    void actualizarEventoShouldThrowWhenEventoNoExiste() {
        when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.actualizarEvento(1L, new EventoRequestDTO()));

        verify(eventoRepo).findById(1L);
        verify(eventoRepo, never()).save(any(Evento.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void actualizarEventoShouldActualizarCamposBasicosYOrganizadorYParticipantes() {
        // TODO: COMPLETAR CON LOS ALUMNOS
        // Objetivo de este test:
        //  - Dado un Evento existente en la BD
        //  - Y un objeto "cambios" con nuevo nombre, descripción, tipo, fechas, organizador y participantes
        //  - Cuando llamamos a eventoServicio.actualizarEvento(id, cambios)
        //  - Entonces se deben actualizar:
        //      * nombre, descripcion, tipo, fechaInicio, fechaFin
        //      * organizador (buscándolo en organizadorRepo por id)
        //      * añadir los nuevos participantes a la lista existente (sin perder los que hubiera)
        //
        // Pistas:
        //  - Usa mocks de eventoRepo y organizadorRepo con Mockito
        //  - eventoRepo.findById(id) debe devolver un Evento "existente"
        //  - organizadorRepo.findById(idOrganizador) debe devolver el Organizador
        //  - eventoRepo.save(...) puede devolver el mismo objeto que recibe (thenAnswer)
        //  - Verifica con assertThat(...) que los cambios se han aplicado correctamente
        //
        // Nota: este test se deja intencionadamente como TODO para practicar TDD en clase.
    }

    @Test
    void eliminarEventoShouldThrowWhenNoExiste() {
        when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.eliminarEvento(1L));

        verify(eventoRepo).findById(1L);
        verify(eventoRepo, never()).delete(any(Evento.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void eliminarEventoShouldDeleteWhenExiste() {
        Evento existente = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
        when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));

        eventoServicio.eliminarEvento(1L);

        verify(eventoRepo).findById(1L);
        verify(eventoRepo).delete(existente);
        verifyNoInteractions(organizadorRepo);
    }
}
