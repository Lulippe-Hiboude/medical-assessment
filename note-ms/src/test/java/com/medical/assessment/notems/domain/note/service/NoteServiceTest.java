package com.medical.assessment.notems.domain.note.service;

import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.persistence.entity.Note;
import com.medical.assessment.notems.persistence.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

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
        given(noteRepository.save(any(Note.class))).willReturn(expected);

        //when
        final NoteDto result = noteService.createNote(noteCreateDto);

        //then
        verify(noteRepository, times(1)).save(any(Note.class));
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getContent()).isEqualTo(expected.getContent());
        assertThat(result.getCreatedAt()).isNotNull();
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
        List<NoteDto> result = noteService.getNotesByPatientId(1L);

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
        List<NoteDto> result = noteService.getNotesByPatientId(1L);

        //then
        verify(noteRepository, times(1)).findByPatientIdOrderByCreatedAtDesc("1");
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}