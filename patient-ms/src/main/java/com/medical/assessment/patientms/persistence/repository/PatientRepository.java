package com.medical.assessment.patientms.persistence.repository;

import com.medical.assessment.patientms.persistence.entity.Patient;
import com.medical.assessment.patientms.persistence.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientId(final Long patientId);

    List<Patient> findByLastNameIgnoreCaseAndFirstNameIgnoreCaseAndBirthDateAndGender(final String lastName,
                                                                                      final String firstName,
                                                                                      final LocalDate birthDate,
                                                                                      final Gender gender);
}
