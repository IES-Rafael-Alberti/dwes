package daw2a.gestioneventos.config;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import daw2a.gestioneventos.repo.ParticipanteRepo;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    ApplicationRunner loadData(OrganizadorRepo organizadorRepo,
                               EventoRepo eventoRepo,
                               ParticipanteRepo participanteRepo,
                               @Value("${seed.organizadores:5}") int seedOrganizadores,
                               @Value("${seed.eventos:10}") int seedEventos,
                               @Value("${seed.participantes:20}") int seedParticipantes) {
        return args -> {
            Faker faker = new Faker(new Locale("es"));
            Random rnd = new Random();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (organizadorRepo.count() == 0 && seedOrganizadores > 0) {
                for (int i = 0; i < seedOrganizadores; i++) {
                    Organizador org = new Organizador();
                    org.setNombre(faker.company().name());
                    organizadorRepo.save(org);
                }
            }

            if (eventoRepo.count() == 0 && seedEventos > 0) {
                List<Organizador> organizadores = organizadorRepo.findAll();
                if (!organizadores.isEmpty()) {
                    for (int i = 0; i < seedEventos; i++) {
                        Evento ev = new Evento();
                        ev.setNombre(faker.book().title());
                        ev.setDescripcion(faker.lorem().sentence(12));
                        ev.setOrganizador(organizadores.get(rnd.nextInt(organizadores.size())));
                        // Tipo y fechas realistas
                        var tipos = daw2a.gestioneventos.dominio.TipoEvento.values();
                        ev.setTipo(tipos[rnd.nextInt(tipos.length)]);
                        LocalDateTime start = LocalDateTime.now().plusDays(rnd.nextInt(90) + 1).withHour(rnd.nextInt(10) + 8).withMinute(0);
                        LocalDateTime end = start.plusHours(rnd.nextInt(7) + 2);
                        ev.setFechaInicio(start);
                        ev.setFechaFin(end);
                        eventoRepo.save(ev);
                    }
                }
            }

            if (participanteRepo.count() == 0 && seedParticipantes > 0) {
                List<Evento> eventos = eventoRepo.findAll();
                if (!eventos.isEmpty()) {
                    for (int i = 0; i < seedParticipantes; i++) {
                        Participante p = new Participante();
                        p.setNombre(faker.name().fullName());
                        p.setUsuario(faker.internet().username());
                        String rawPwd = faker.internet().password(10, 18);
                        p.setContrasenia(encoder.encode(rawPwd));
                        p.setEvento(eventos.get(rnd.nextInt(eventos.size())));
                        participanteRepo.save(p);
                    }
                }
            }
        };
    }
}
