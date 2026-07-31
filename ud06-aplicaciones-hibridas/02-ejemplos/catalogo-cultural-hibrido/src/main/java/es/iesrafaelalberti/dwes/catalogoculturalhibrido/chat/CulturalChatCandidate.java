package es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat;

import java.util.List;

/**
 * Minimal, source-neutral catalog projection that may cross the chat boundary.
 */
public record CulturalChatCandidate(
        String id,
        String title,
        List<String> creators,
        Integer year,
        List<String> subjects) {

    public CulturalChatCandidate {
        creators = creators == null ? List.of() : List.copyOf(creators);
        subjects = subjects == null ? List.of() : List.copyOf(subjects);
    }
}
