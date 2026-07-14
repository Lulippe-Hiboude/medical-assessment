package com.medical.assessment.notems.api.controller;

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

    @Override
    public ResponseEntity<NoteDto> createNote(NoteCreateDto noteCreateDto) {
        log.info("Create note");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(noteCreateDto));
    }

    @Override
    public ResponseEntity<List<NoteDto>> getNotesByPatientId(Long patientId) {
        return ResponseEntity.ok(noteService.getNotesByPatientId(patientId));
    }
}
