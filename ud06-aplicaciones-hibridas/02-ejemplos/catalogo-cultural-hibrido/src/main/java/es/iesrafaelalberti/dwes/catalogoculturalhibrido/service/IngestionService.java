package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.CulturalItem;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.model.Source;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.repository.CulturalItemRepository;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IngestionService {

    private final CulturalItemRepository repository;
    private final ObjectMapper objectMapper;

    public IngestionService(CulturalItemRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<CulturalItem> ingestFixture(String classpathResource) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) {
                throw new IngestionException("Fixture not found on classpath: " + classpathResource);
            }
            List<CulturalRecord> records = objectMapper.readValue(
                    is, new TypeReference<List<CulturalRecord>>() {
                    });
            return ingestRecords(records);
        } catch (JacksonException | IOException e) {
            throw new IngestionException("Failed to parse fixture: " + e.getMessage(), e);
        }
    }

    @Transactional
    public List<CulturalItem> ingestRecords(List<CulturalRecord> records) {
        return records.stream()
                .map(record -> upsertRecord(Source.valueOf(record.getSource().toUpperCase()), record))
                .toList();
    }

    private CulturalItem upsertRecord(Source source, CulturalRecord record) {
        var existing = repository.findBySourceAndExternalId(source, record.getExternalId());
        if (existing.isPresent()) {
            CulturalItem item = existing.get();
            item.setTitle(record.getTitle());
            item.setCreators(record.getCreators());
            item.setYear(record.getYear());
            item.setSubjects(record.getSubjects());
            item.setSourceUrl(record.getSourceUrl());
            item.setLicense(record.getLicense());
            item.setRetrievedAt(LocalDateTime.parse(record.getRetrievedAt()));
            return repository.save(item);
        }
        CulturalItem item = new CulturalItem(
                source,
                record.getExternalId(),
                record.getTitle(),
                record.getCreators(),
                record.getYear(),
                record.getSubjects(),
                record.getSourceUrl(),
                record.getLicense(),
                LocalDateTime.parse(record.getRetrievedAt())
        );
        return repository.save(item);
    }

    public static class IngestionException extends RuntimeException {
        public IngestionException(String message) {
            super(message);
        }

        public IngestionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
