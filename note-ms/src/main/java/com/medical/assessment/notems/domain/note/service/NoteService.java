package com.medical.assessment.notems.domain.note.service;

import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;

import java.util.List;

public interface NoteService {

    NoteDto createNote(final NoteCreateDto noteCreateDto);

    List<NoteDto> getNotesByPatientId(final Long patientId);

    List<String> getNotesContentByPatientId(final Long patientId);
}
