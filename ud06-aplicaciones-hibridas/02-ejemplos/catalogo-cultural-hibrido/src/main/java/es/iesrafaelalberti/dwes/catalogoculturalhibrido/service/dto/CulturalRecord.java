package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto;

import java.util.List;

/**
 * Source-neutral normalized ingestion record used by every provider adapter
 * (Wikidata fixture and Open Library remote API) before persistence.
 *
 * <p>Fields follow the Phase 0 contract ({@code source}, {@code externalId},
 * {@code title}, {@code creators}, {@code year}, {@code subjects},
 * {@code sourceUrl}, {@code license}, {@code retrievedAt}). {@code retrievedAt}
 * is an ISO-8601 local timestamp string parsed by {@code IngestionService}.</p>
 */
public class CulturalRecord {

    private String source;
    private String externalId;
    private String title;
    private List<String> creators;
    private Integer year;
    private List<String> subjects;
    private String sourceUrl;
    private String license;
    private String retrievedAt;

    public CulturalRecord() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(String retrievedAt) {
        this.retrievedAt = retrievedAt;
    }
}
