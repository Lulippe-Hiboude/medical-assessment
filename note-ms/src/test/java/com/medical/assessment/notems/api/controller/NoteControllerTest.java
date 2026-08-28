package com.medical.assessment.notems.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.notems.api.controller.note.NoteController;
import com.medical.assessment.notems.domain.note.service.NoteService;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.security.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("should create a note successfully")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldCreateNoteSuccessfully() throws Exception {
        //given
        final NoteCreateDto noteCreateDto = getNoteCreateDto();
        final NoteDto noteDto = getNoteDto();

        given(noteService.createNote(noteCreateDto)).willReturn(noteDto);

        //when
        final MvcResult result = mockMvc.perform(post("/notes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteCreateDto))
                )
                .andExpect(status().isCreated())
                .andReturn();

        //then
        final String responseContent = result.getResponse().getContentAsString();
        final String expectedResponseContent = objectMapper.writeValueAsString(noteDto);
        assertThat(responseContent).isEqualTo(expectedResponseContent);
    }

    @Test
    @DisplayName("should get all notes for a patient successfully")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldGetAllNotesForPatientSuccessfully() throws Exception {
        //given
        final NoteDto noteDto = getNoteDto();
        final NoteDto noteDto2 = getNoteDto();
        given(noteService.getNotesByPatientId(1L)).willReturn(List.of(noteDto, noteDto2));

        //when
        final MvcResult result = mockMvc.perform(get("/notes/patient/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        final String responseContent = result.getResponse().getContentAsString();
        final String expectedResponseContent = objectMapper.writeValueAsString(List.of(noteDto, noteDto2));
        assertThat(responseContent).isEqualTo(expectedResponseContent);
    }

    @Test
    @DisplayName("should return empty list when no notes found for a patient")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnEmptyListWhenNoNotesFoundForPatient() throws Exception {
        //given
        given(noteService.getNotesByPatientId(1L)).willReturn(List.of());

        //when
        final MvcResult result = mockMvc.perform(get("/notes/patient/1"))

                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    @DisplayName("should return NOT FOUND when patient does not exists")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnNotFoundWhenPatientDoesNotExists() throws Exception {
        //given
        final long patientId = 1L;
        given(noteService.getNotesByPatientId(patientId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Patient not found with id " + patientId));

        //when
        final MvcResult result = mockMvc.perform(get("/notes/patient/1"))

                .andExpect(status().isNotFound())
                .andReturn();

        //then
        final ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("Patient not found with id " + patientId);
    }

    @Test
    @DisplayName("should return BAD GATEWAY when patient service unavailable")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnBadGatewayWhenPatientServiceUnavailable() throws Exception {
        //given
        final long patientId = 1L;
        given(noteService.getNotesByPatientId(patientId)).willThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "unable to verify patient with id " + patientId));

        //when
        final MvcResult result = mockMvc.perform(get("/notes/patient/1"))

                .andExpect(status().isBadGateway())
                .andReturn();

        //then
        final ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.message()).isEqualTo("unable to verify patient with id " + patientId);
    }


    @Test
    @DisplayName("should return BAD REQUEST for invalidNoteCreateDto with negative id and empty content")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnBadRequestForInvalidNoteCreateDtoWithNegativeIdAndEmptyContent() throws Exception {
        //given
        final NoteCreateDto invalidNoteCreateDto = new NoteCreateDto();
        invalidNoteCreateDto.setPatientId(-1L);
        invalidNoteCreateDto.setContent(StringUtils.EMPTY);

        //when
        final MvcResult result = mockMvc.perform(post("/notes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidNoteCreateDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.errors())
                .containsEntry("patientId", "patient Id must be a positive number")
                .containsEntry("content", "content is required");
    }

    @Test
    @DisplayName("should return BAD REQUEST for invalidNoteCreateDto with null id and content exceed max size")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnBadRequestForInvalidNoteCreateDtoWithNullIdAndContentExceedMaxSize() throws Exception {
        //given
        final String content = "a".repeat(2001);
        final NoteCreateDto invalidNoteCreateDto = new NoteCreateDto();
        invalidNoteCreateDto.setPatientId(null);
        invalidNoteCreateDto.setContent(content);

        //when
        final MvcResult result = mockMvc.perform(post("/notes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidNoteCreateDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        final ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.errors())
                .containsEntry("patientId", "patient Id is required")
                .containsEntry("content", "content must not exceed 2000 characters");
    }

    @Test
    @DisplayName("should return list of all notes content for a given patient Id")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnListOfAllNotesContentForGivenPatientId() throws Exception {
        //given
        final String contentNote1 = "content note 1";
        final String contentNote2 = "content note 2";
        final List<String> notesContent = List.of(
                contentNote1,
                contentNote2
        );

        given(noteService.getNotesContentByPatientId(1L)).willReturn(notesContent);

        //when
        final MvcResult result = mockMvc.perform(get("/notes/patient/1/note-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        final String responseContent = result.getResponse().getContentAsString();
        final String expectedResponseContent = objectMapper.writeValueAsString(List.of(contentNote1, contentNote2));
        assertThat(responseContent).isEqualTo(expectedResponseContent);
    }

    @Test
    @DisplayName("should return empty list if no notes found")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnEmptyListIfNoNotesFound() throws Exception {
        //given
        given(noteService.getNotesContentByPatientId(1L)).willReturn(List.of());

        //when
        final MvcResult result = mockMvc.perform(get("/notes/patient/1/note-content"))

                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    private NoteCreateDto getNoteCreateDto() {
        return new NoteCreateDto()
                .patientId(1L)
                .content("Test note content");
    }

    private NoteDto getNoteDto() {
        return new NoteDto()
                .id("abc123")
                .patientId(1L)
                .content("Test note content")
                .createdAt(LocalDateTime.of(2026, 7, 10, 10, 0, 0, 0));
    }

}