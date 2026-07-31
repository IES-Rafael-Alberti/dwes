package es.iesrafaelalberti.dwes.catalogoculturalhibrido.service;

import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CatalogRecommendation;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatCandidate;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.chat.CulturalChatGateway;
import es.iesrafaelalberti.dwes.catalogoculturalhibrido.service.dto.CulturalRecord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CatalogRecommendationServiceTest {

    @Test
    void delegatesOnceAndReturnsTheStructuredRecommendation() {
        RecordingGateway gateway = new RecordingGateway();
        CatalogRecommendationService service = new CatalogRecommendationService(gateway);

        CatalogRecommendation result = service.recommend(List.of(record(1), record(2)));

        assertThat(result).isEqualTo(gateway.response);
        assertThat(gateway.calls).isEqualTo(1);
        assertThat(gateway.received).extracting(CulturalChatCandidate::id)
                .containsExactly("OPEN_LIBRARY:OL1W", "OPEN_LIBRARY:OL2W");
    }

    @Test
    void limitsTheChatBoundaryToTenNormalizedRecords() {
        RecordingGateway gateway = new RecordingGateway();
        CatalogRecommendationService service = new CatalogRecommendationService(gateway);
        List<CulturalRecord> records = new ArrayList<>();
        for (int index = 1; index <= 12; index++) {
            records.add(record(index));
        }

        service.recommend(records);

        assertThat(gateway.received).hasSize(10);
        assertThat(gateway.received.getLast().id()).isEqualTo("OPEN_LIBRARY:OL10W");
    }

    @Test
    void rejectsAnEmptyCandidateSetBeforeCallingTheGateway() {
        RecordingGateway gateway = new RecordingGateway();
        CatalogRecommendationService service = new CatalogRecommendationService(gateway);

        assertThatThrownBy(() -> service.recommend(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one");
        assertThat(gateway.calls).isZero();
    }

    private CulturalRecord record(int number) {
        CulturalRecord record = new CulturalRecord();
        record.setSource("OPEN_LIBRARY");
        record.setExternalId("OL" + number + "W");
        record.setTitle("Title " + number);
        record.setCreators(List.of("Creator"));
        record.setSubjects(List.of("Subject"));
        record.setYear(2000 + number);
        return record;
    }

    private static final class RecordingGateway implements CulturalChatGateway {
        private final CatalogRecommendation response = new CatalogRecommendation(
                "Summary", List.of("OPEN_LIBRARY:OL1W"), "Based only on supplied metadata");
        private int calls;
        private List<CulturalChatCandidate> received = List.of();

        @Override
        public CatalogRecommendation recommend(List<CulturalChatCandidate> candidates) {
            calls++;
            received = List.copyOf(candidates);
            return response;
        }
    }
}
