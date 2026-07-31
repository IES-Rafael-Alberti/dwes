package es.iesrafaelalberti.dwes.catalogoculturalhibrido.repository;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.CulturalItem;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CulturalItemRepository extends JpaRepository<CulturalItem, Long> {

    Optional<CulturalItem> findBySourceAndExternalId(Source source, String externalId);

    boolean existsBySourceAndExternalId(Source source, String externalId);
}
