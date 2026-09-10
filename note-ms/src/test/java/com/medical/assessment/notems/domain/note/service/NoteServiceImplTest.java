package com.medical.assessment.notems.domain.note.service;

import com.medical.assessment.notems.infrastructure.client.patient.service.PatientFeignService;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.persistence.entity.Note;
import com.medical.assessment.notems.persistence.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private PatientFeignService patientFeignService;

    @InjectMocks
    private NoteServiceImpl noteServiceImpl;

    @Test
    @DisplayName("Should create a note successfully")
    void shouldCreateNoteSuccessfully() {
        //given
        final NoteCreateDto noteCreateDto = new NoteCreateDto()
                .patientId(1L)
                .content("Test note content");

        final Note expected = Note.builder()
                .id("abc123")
                .patientId("1")
                .content("Test note content")
                .createdAt(LocalDateTime.now())
                .build();
        willDoNothing().given(patientFeignService).verifyPatientExists(noteCreateDto.getPatientId());
        given(noteRepository.save(any(Note.class))).willReturn(expected);

        //when
        final NoteDto result = noteServiceImpl.createNote(noteCreateDto);

        //then
        verify(noteRepository, times(1)).save(any(Note.class));
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getContent()).isEqualTo(expected.getContent());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should throw exception when patient does not exist and do nothing")
    void shouldThrowExceptionWhenPatientDoesNotExist() {
        //given
        final NoteCreateDto noteCreateDto = new NoteCreateDto()
                .patientId(1L)
                .content("Test note content");
        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Patient not found with id " + noteCreateDto.getPatientId()))
                .given(patientFeignService).verifyPatientExists(noteCreateDto.getPatientId());

        //when & then
        assertThrows(ResponseStatusException.class, () -> noteServiceImpl.createNote(noteCreateDto));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    @DisplayName("should throw exception when feign call failed and do nothing")
    void shouldThrowExceptionWhenFeignCallFailed() {
        //given
        final NoteCreateDto noteCreateDto = new NoteCreateDto()
                .patientId(1L)
                .content("Test note content");
        willThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "unable to verify patient with id " + noteCreateDto.getPatientId()))
                .given(patientFeignService).verifyPatientExists(noteCreateDto.getPatientId());

        //when & then
        assertThrows(ResponseStatusException.class, () -> noteServiceImpl.createNote(noteCreateDto));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    @DisplayName("Should get notes by patient ID successfully")
    void shouldGetNotesByPatientIdSuccessfully() {
        //given
        final Note note1 = Note.builder()
                .id("note1")
                .patientId("1")
                .content("Note 1 content")
                .createdAt(LocalDateTime.now())
                .build();
        final Note note2 = Note.builder()
                .id("note2")
                .patientId("1")
                .content("Note 2 content")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        final List<Note> notes = List.of(note1, note2);

        given(noteRepository.findByPatientIdOrderByCreatedAtDesc("1")).willReturn(notes);


        //when
        List<NoteDto> result = noteServiceImpl.getNotesByPatientId(1L);

        //then
        verify(noteRepository, times(1)).findByPatientIdOrderByCreatedAtDesc("1");
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.getFirst().getId()).isEqualTo(note1.getId());
        assertThat(result.getFirst().getContent()).isEqualTo(note1.getContent());
        assertThat(result.get(1).getId()).isEqualTo(note2.getId());
        assertThat(result.get(1).getContent()).isEqualTo(note2.getContent());
    }

    @Test
    @DisplayName("Should return empty list when no notes found for patient ID")
    void shouldReturnEmptyListWhenNoNotesFoundForPatientId() {
        //given
        given(noteRepository.findByPatientIdOrderByCreatedAtDesc("1")).willReturn(List.of());

        //when
        List<NoteDto> result = noteServiceImpl.getNotesByPatientId(1L);

        //then
        verify(noteRepository, times(1)).findByPatientIdOrderByCreatedAtDesc("1");
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return empty list content when no notes found for patient ID")
    void shouldReturnEmptyListContentWhenNoNotesFoundForPatientId() {
        //given
        given(noteRepository.findByPatientIdOrderByCreatedAtDesc("1")).willReturn(List.of());

        //when
        List<String> result = noteServiceImpl.getNotesContentByPatientId(1L);

        //then
        verify(noteRepository, times(1)).findByPatientIdOrderByCreatedAtDesc("1");
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return list of notes content successfully for a patient ID")
    void shouldListOfNotesSuccessfullyForPatientId() {
        //given
        final Note note1 = Note.builder()
                .id("note1")
                .patientId("1")
                .content("Note 1 content")
                .createdAt(LocalDateTime.now())
                .build();
        final Note note2 = Note.builder()
                .id("note2")
                .patientId("1")
                .content("Note 2 content")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        final List<Note> notes = List.of(note1, note2);

        given(noteRepository.findByPatientIdOrderByCreatedAtDesc("1")).willReturn(notes);

        //when
        List<String> result = noteServiceImpl.getNotesContentByPatientId(1L);

        //then
        verify(noteRepository, times(1)).findByPatientIdOrderByCreatedAtDesc("1");
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.getFirst()).isEqualTo(note1.getContent());
        assertThat(result.getLast()).isEqualTo(note2.getContent());
    }
}