package com.medical.assessment.notems.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.notems.domain.note.service.NoteService;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.note.model.NoteDto;
import com.medical.assessment.notems.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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