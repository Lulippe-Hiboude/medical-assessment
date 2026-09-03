package com.medical.assessment.riskms.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;
import com.medical.assessment.riskms.domain.risk.service.RiskService;
import com.medical.assessment.riskms.security.JwtService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RiskController.class)
class RiskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RiskService riskService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("should calculate risk successfully")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldCalculateRiskSuccessfully() throws Exception {
        //given
        final Long patientId = 1L;
        final RiskDto riskDto = RiskDto.IN_DANGER;
        given(riskService.calculatePatientRisk(patientId)).willReturn(riskDto);

        //when
        final MvcResult result = mockMvc.perform(get("/risk/" + patientId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riskDto))
                )
                .andExpect(status().isOk())
                .andReturn();


        //then
        final String responseContent = result.getResponse().getContentAsString();
        final String expectedResponseContent = objectMapper.writeValueAsString(riskDto);
        assertThat(responseContent).isEqualTo(expectedResponseContent);

    }

    @Test
    @DisplayName("should return NO_DATA if no content note found for given patient id")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldReturnNoDataIfNoContentNoteFoundForGivenPatientId() throws Exception {
        //given
        final Long patientId = 1L;
        final RiskDto riskDto = RiskDto.NO_DATA;
        given(riskService.calculatePatientRisk(patientId)).willReturn(riskDto);

        //when
        final MvcResult result = mockMvc.perform(get("/risk/" + patientId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riskDto))
                )
                .andExpect(status().isOk())
                .andReturn();


        //then
        final String responseContent = result.getResponse().getContentAsString();
        final String expectedResponseContent = objectMapper.writeValueAsString(riskDto);
        assertThat(responseContent).isEqualTo(expectedResponseContent);
    }

    @Test
    @DisplayName("should throw Error Response if feign call throw exception Bad Gateway")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldThrowErrorResponseIfFeignCallThrowExceptionBadGateway() throws Exception {
        //given
        final Long patientId = 1L;

        given(riskService.calculatePatientRisk(patientId)).willThrow(
                new ResponseStatusException(HttpStatus.BAD_GATEWAY, "unable to verify patient with id " + patientId));

        //when & then
        mockMvc.perform(get("/risk/{id}", patientId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message")
                        .value("unable to verify patient with id " + patientId));
    }

    @Test
    @DisplayName("should throw Error Response if feign call throw exception Not Found")
    @WithMockUser(username = "doctor", roles = "DOCTOR")
    void shouldThrowErrorResponseIfFeignCallThrowExceptionNotFound() throws Exception {
        //given
        final Long patientId = 1L;

        given(riskService.calculatePatientRisk(patientId)).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id " + patientId));

        //when & then
        mockMvc.perform(get("/risk/{id}", patientId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Patient not found with id " + patientId));
    }
}