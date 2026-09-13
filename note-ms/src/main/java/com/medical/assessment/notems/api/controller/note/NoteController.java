package com.medical.assessment.notems.api.controller.note;

import com.medical.assessment.notems.domain.note.service.NoteService;
import com.medical.assessment.notems.note.api.NotesApi;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class NoteController implements NotesApi {
    final NoteService noteService;


    /**
     * Creates a new note for a patient.
     *
     * <p>The note is created from the provided request and returned with
     * HTTP status {@code 201 CREATED}.</p>
     *
     * @param noteCreateDto the data required to create the note
     * @return a {@link ResponseEntity} containing the created {@link NoteDto}
     *         with HTTP status {@code 201 CREATED}
     */
    @Override
    public ResponseEntity<NoteDto> createNote(NoteCreateDto noteCreateDto) {
        log.info("Create note");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(noteCreateDto));
    }

    /**
     * Retrieves all notes associated with a patient.
     *
     * <p>The notes are returned in descending order of creation date.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return a {@link ResponseEntity} containing the list of the patient's notes
     *         with HTTP status {@code 200 OK}
     */
    @Override
    public ResponseEntity<List<NoteDto>> getNotesByPatientId(Long patientId) {
        return ResponseEntity.ok(noteService.getNotesByPatientId(patientId));
    }

    /**
     * Retrieves the content of all notes associated with a patient.
     *
     * <p>The note contents are returned in descending order of creation date.
     * If the patient has no notes, an empty list is returned.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return a {@link ResponseEntity} containing the list of note contents
     *         with HTTP status {@code 200 OK}
     */
    @Override
    public ResponseEntity<List<String>> getNotesContentByPatientId(final Long patientId) {
        return ResponseEntity.ok(noteService.getNotesContentByPatientId(patientId));
    }
}
