package com.medical.assessment.patientms.mapper;

import com.medical.assessment.patientms.patient.model.PatientCreateDto;
import com.medical.assessment.patientms.patient.model.PatientDto;
import com.medical.assessment.patientms.patient.model.PatientRiskProfile;
import com.medical.assessment.patientms.patient.model.PatientUpdateDto;
import com.medical.assessment.patientms.persistence.entity.Patient;

import com.medical.assessment.patientms.persistence.enums.Gender;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "patientId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    PatientDto toPatientDto(final Patient patient);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", expression = "java(org.apache.commons.lang3.StringUtils.normalizeSpace(patientCreateDto.getFirstName()))")
    @Mapping(target = "lastName", expression = "java(org.apache.commons.lang3.StringUtils.normalizeSpace(patientCreateDto.getLastName()))")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "phoneNumber", expression = "java(org.apache.commons.lang3.StringUtils.normalizeSpace(patientCreateDto.getPhoneNumber()))")
    @Mapping(target = "address", expression = "java(org.apache.commons.lang3.StringUtils.normalizeSpace(patientCreateDto.getAddress()))")
    Patient toPatient(final PatientCreateDto patientCreateDto);

    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "age", source = "age")
    PatientRiskProfile toPatientRiskProfile(final Gender gender, final int age);

    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "emptyToNull")
    @Mapping(target = "address", source = "address", qualifiedByName = "emptyToNull")
    Patient updatePatient(final PatientUpdateDto patientUpdateDto, @MappingTarget final Patient existingPatient);

    @Named("emptyToNull")
    default String emptyToNull(final String value) {
        if (value == null) return null;

        return StringUtils.isBlank(value) ? null : value;
    }
}
