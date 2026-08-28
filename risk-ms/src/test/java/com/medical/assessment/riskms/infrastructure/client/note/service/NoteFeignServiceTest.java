package com.medical.assessment.riskms.infrastructure.client.note.service;

import com.medical.assessment.riskms.infrastructure.client.note.NoteFeignClient;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NoteFeignServiceTest {

    @Mock
    private NoteFeignClient noteFeignClient;

    @InjectMocks
    private NoteFeignService noteFeignService;

    @Test
    @DisplayName("should return a list of Note given a patient id")
    void shouldReturnAListOfNoteByPatientId() {
        //given
        final String note1 = "Note 1";
        final String note2 = "Note 2";
        final String note3 = "Note 3";
        final List<String> notes = List.of(note1, note2, note3);
        final Long patientId = 1L;
        given(noteFeignClient.getNoteContentList(patientId)).willReturn(notes);

        //when
        final List<String> expectedNotes = noteFeignService.getNoteContentList(patientId);

        //then
        assertThat(expectedNotes).containsExactlyInAnyOrder(note1, note2, note3);
    }

    @Test
    @DisplayName("Should return an empty list if no note where found for given patient id")
    void shouldReturnAnEmptyListIfNoNoteFoundForGivenPatientId() {
        //given
        final Long patientId = 1L;
        given(noteFeignClient.getNoteContentList(patientId)).willReturn(Collections.emptyList());

        //when
        final List<String> expectedNotes = noteFeignService.getNoteContentList(patientId);

        //then
        assertThat(expectedNotes).isEmpty();
    }

    @Test
    @DisplayName("should throw a Response status exception if a feign exception is catch")
    void shouldThrowAnExceptionIfFeignExceptionIsCatch() {
        //given
        final Long patientId = 1L;
        given(noteFeignClient.getNoteContentList(patientId)).willThrow(FeignException.class);

        //when & then
        assertThatThrownBy(() -> noteFeignService.getNoteContentList(patientId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(responseStatusException.getMessage()).contains("unable to verify patient with id " + patientId);
                });
    }
}