package com.medical.assessment.notems.domain.note.service;

import com.medical.assessment.notems.domain.note.mapper.NoteMapper;
import com.medical.assessment.notems.infrastructure.client.patient.service.PatientFeignService;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.persistence.entity.Note;
import com.medical.assessment.notems.persistence.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final PatientFeignService patientFeignService;


    public NoteDto createNote(final NoteCreateDto noteCreateDto) {
        patientFeignService.verifyPatientExists(noteCreateDto.getPatientId());

        final Note note = NoteMapper.INSTANCE.toNote(noteCreateDto);
        return NoteMapper.INSTANCE
                .toNoteDto(noteRepository.save(note));
    }

    public List<NoteDto> getNotesByPatientId(final Long patientId) {
        final List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(String.valueOf(patientId));
        return NoteMapper.INSTANCE.toNoteDtoList(notes);
    }

    public List<String> getNotesContentByPatientId(final Long patientId) {
        final List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(String.valueOf(patientId));
        if (notes.isEmpty()) {
            return Collections.emptyList();
        }
        return notes.stream()
                .map(Note::getContent)
                .toList();
    }
}
