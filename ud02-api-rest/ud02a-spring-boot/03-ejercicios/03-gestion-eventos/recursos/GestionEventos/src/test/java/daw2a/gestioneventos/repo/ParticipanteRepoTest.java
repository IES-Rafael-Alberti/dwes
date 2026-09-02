package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.dominio.Evento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParticipanteRepoTest {

    @Autowired
    private ParticipanteRepo participanteRepo;

    @Autowired
    private EventoRepo eventoRepo;

    @Test
    void findByNombreContainingIgnoreCaseShouldReturnResults() {
        Evento evento = eventoRepo.save(Evento.builder()
                .nombre("Evento de prueba")
                .descripcion("Descripcion de prueba")
                .build());
        Participante p1 = Participante.builder().nombre("Alice").usuario("alice001").contrasenia("secret").evento(evento).build();
        Participante p2 = Participante.builder().nombre("Bob").usuario("bob0002").contrasenia("secret").evento(evento).build();
        participanteRepo.save(p1);
        participanteRepo.save(p2);

        List<Participante> found = participanteRepo.findByNombreContainingIgnoreCase("ali");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getNombre()).containsIgnoringCase("ali");
    }
}
