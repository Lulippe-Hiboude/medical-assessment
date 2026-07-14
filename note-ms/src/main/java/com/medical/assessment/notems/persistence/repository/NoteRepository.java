package com.medical.assessment.notems.persistence.repository;

import com.medical.assessment.notems.persistence.entity.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByPatientIdOrderByCreatedAtDesc(final String patientId);
}
