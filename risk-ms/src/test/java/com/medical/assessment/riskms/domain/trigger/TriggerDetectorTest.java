package com.medical.assessment.riskms.domain.trigger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.medical.assessment.riskms.domain.trigger.TriggerTermsEnum.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TriggerDetectorTest {

    @Test
    @DisplayName("should return number of trigger in a list of note")
    void shouldReturnNumberOfTriggerInAListOfNote() {
        //given
        final String note1 = "Fumeuse";
        final String note2 = "Cholesterol";
        final String note3 = "Reaction";
        final List<String> notes = List.of(note1, note2, note3);

        //when
        final Set<TriggerTermsEnum> actual = TriggerDetector.detectTriggerTerms(notes);

        //then
        assertThat(actual).containsExactlyInAnyOrder(FUMEUR,CHOLESTEROL,REACTION);
    }

    @Test
    @DisplayName("should count trigger once regardless of occurrence count")
    void shouldCountTriggerOnceRegardlessOfOccurrenceCount() {
        //given
        final String note1 = "Fumeuse";
        final String note2 = "Fumeur";
        final String note3 = "Fumeurs";
        final List<String> notes = List.of(note1, note2, note3);

        //when
        final Set<TriggerTermsEnum> actual = TriggerDetector.detectTriggerTerms(notes);

        //then
        assertThat(actual).containsExactlyInAnyOrder(FUMEUR);
        assertThat(actual).size().isEqualTo(1);
    }

    @Test
    @DisplayName("should normalize note content")
    void shouldNormalizeNoteContent() {
        //given
        final String note1 = "FumeUse";
        final String note2 = "CholEstéroL";
        final String note3 = "HémoglObinE A1C";
        final List<String> notes = List.of(note1, note2, note3);

        //when
        final Set<TriggerTermsEnum> actual = TriggerDetector.detectTriggerTerms(notes);

        //then
        assertThat(actual).containsExactlyInAnyOrder(FUMEUR,CHOLESTEROL,HEMOGLOBINE_A1C);

    }
}