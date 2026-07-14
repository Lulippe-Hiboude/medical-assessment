package com.medical.assessment.notems.domain.note.service;

import com.medical.assessment.notems.domain.note.mapper.NoteMapper;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.persistence.entity.Note;
import com.medical.assessment.notems.persistence.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteDto createNote(final NoteCreateDto noteCreateDto) {
        final Note note = NoteMapper.INSTANCE.toNote(noteCreateDto);
        return NoteMapper.INSTANCE
                .toNoteDto(noteRepository.save(note));
    }

    public List<NoteDto> getNotesByPatientId(final Long patientId) {
        final List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(String.valueOf(patientId));
        return NoteMapper.INSTANCE.toNoteDtoList(notes);
    }
}
