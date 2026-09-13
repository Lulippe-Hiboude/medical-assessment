package com.medical.assessment.notems.domain.note.service;

import com.medical.assessment.notems.domain.note.mapper.NoteMapper;
import com.medical.assessment.notems.infrastructure.client.patient.service.PatientFeignService;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.persistence.entity.Note;
import com.medical.assessment.notems.persistence.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final PatientFeignService patientFeignService;


    /**
     * Creates a new note for a patient.
     *
     * <p>Before creating the note, the existence of the patient is verified
     * through the patient service. The note is then mapped from the creation
     * request, persisted in the repository, and mapped to a {@link NoteDto}.</p>
     *
     * @param noteCreateDto the data required to create the note
     * @return the created note as a {@link NoteDto}
     * @throws ResponseStatusException if the patient does not exist or if
     *         the patient service cannot be reached
     */
    @Override
    public NoteDto createNote(final NoteCreateDto noteCreateDto) {
        patientFeignService.verifyPatientExists(noteCreateDto.getPatientId());

        final Note note = NoteMapper.INSTANCE.toNote(noteCreateDto);
        return NoteMapper.INSTANCE
                .toNoteDto(noteRepository.save(note));
    }

    /**
     * Retrieves all notes associated with a patient.
     *
     * <p>The notes are retrieved from the repository and ordered from the most
     * recently created to the oldest.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return a list of {@link NoteDto} representing the patient's notes,
     *         ordered by creation date in descending order
     */
    @Override
    public List<NoteDto> getNotesByPatientId(final Long patientId) {
        final List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(String.valueOf(patientId));
        return NoteMapper.INSTANCE.toNoteDtoList(notes);
    }

    /**
     * Retrieves the content of all notes associated with a patient.
     *
     * <p>The notes are retrieved in descending order of creation date and
     * their content is extracted. If no notes are found, an empty list is returned.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return a list containing the content of the patient's notes,
     *         ordered by creation date in descending order
     */
    @Override
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
