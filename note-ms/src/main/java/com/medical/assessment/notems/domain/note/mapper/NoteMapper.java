package com.medical.assessment.notems.domain.note.mapper;

import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.persistence.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoteMapper {
    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    @Mapping(target = "patientId", expression = "java(Long.valueOf(note.getPatientId()))")
    NoteDto toNoteDto(final Note note);

    List<NoteDto> toNoteDtoList(final List<Note> notes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientId", expression = "java(String.valueOf(noteCreateDto.getPatientId()))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Note toNote(final NoteCreateDto noteCreateDto);
}
