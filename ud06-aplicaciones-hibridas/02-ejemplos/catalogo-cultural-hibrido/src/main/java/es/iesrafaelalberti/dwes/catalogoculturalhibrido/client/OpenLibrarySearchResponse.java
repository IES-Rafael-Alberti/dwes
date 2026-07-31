package es.iesrafaelalberti.dwes.catalogoculturalhibrido.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * JSON response envelope of the Open Library {@code /search.json} endpoint.
 *
 * <p>Only the fields requested in the query are declared (Jackson 3 ignores
 * unknown keys, so extra provider fields are harmless). Field names match the
 * Open Library Search API contract of the Phase 0 document.</p>
 */
public class OpenLibrarySearchResponse {

    @JsonProperty("numFound")
    private long numFound;

    @JsonProperty("docs")
    private List<OpenLibraryDoc> docs;

    public long getNumFound() {
        return numFound;
    }

    public void setNumFound(long numFound) {
        this.numFound = numFound;
    }

    public List<OpenLibraryDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<OpenLibraryDoc> docs) {
        this.docs = docs;
    }

    /** A single search result document (a work). */
    public static class OpenLibraryDoc {

        /** Work key in Open Library path form, e.g. {@code /works/OL12345W}. */
        @JsonProperty("key")
        private String key;

        @JsonProperty("title")
        private String title;

        @JsonProperty("author_name")
        private List<String> authorName;

        @JsonProperty("first_publish_year")
        private Integer firstPublishYear;

        @JsonProperty("subject")
        private List<String> subject;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getAuthorName() {
            return authorName;
        }

        public void setAuthorName(List<String> authorName) {
            this.authorName = authorName;
        }

        public Integer getFirstPublishYear() {
            return firstPublishYear;
        }

        public void setFirstPublishYear(Integer firstPublishYear) {
            this.firstPublishYear = firstPublishYear;
        }

        public List<String> getSubject() {
            return subject;
        }

        public void setSubject(List<String> subject) {
            this.subject = subject;
        }
    }
}
