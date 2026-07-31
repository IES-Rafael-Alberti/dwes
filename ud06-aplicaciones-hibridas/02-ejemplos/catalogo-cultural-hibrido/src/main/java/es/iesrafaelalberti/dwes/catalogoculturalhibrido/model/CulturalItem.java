package es.iesrafaelalberti.dwes.catalogoculturalhibrido.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "cultural_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source", "externalId"})
)
public class CulturalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Source source;

    @Column(nullable = false, length = 100)
    private String externalId;

    @Column(nullable = false, length = 500)
    private String title;

    @Convert(converter = StringListConverter.class)
    @Column(length = 2000)
    private List<String> creators;

    @Column(name = "publication_year")
    private Integer year;

    @Convert(converter = StringListConverter.class)
    @Column(length = 4000)
    private List<String> subjects;

    @Column(length = 1000)
    private String sourceUrl;

    @Column(length = 100)
    private String license;

    @Column(nullable = false)
    private LocalDateTime retrievedAt;

    protected CulturalItem() {
    }

    public CulturalItem(Source source, String externalId, String title,
                        List<String> creators, Integer year, List<String> subjects,
                        String sourceUrl, String license, LocalDateTime retrievedAt) {
        this.source = source;
        this.externalId = externalId;
        this.title = title;
        this.creators = creators;
        this.year = year;
        this.subjects = subjects;
        this.sourceUrl = sourceUrl;
        this.license = license;
        this.retrievedAt = retrievedAt;
    }

    public Long getId() {
        return id;
    }

    public Source getSource() {
        return source;
    }

    public String getExternalId() {
        return externalId;
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

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }
}
