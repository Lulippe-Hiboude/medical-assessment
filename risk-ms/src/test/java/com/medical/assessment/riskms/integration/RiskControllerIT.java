package com.medical.assessment.riskms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.risk_ms.risk_assessment.model.RiskDto;
import com.medical.assessment.riskms.domain.risk.service.RiskService;
import com.medical.assessment.riskms.infrastructure.client.note.NoteFeignClient;
import com.medical.assessment.riskms.infrastructure.client.patient.PatientFeignClient;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.Gender;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import com.medical.assessment.riskms.security.JwtService;
import feign.FeignException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
public class RiskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RiskService riskService;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private PatientFeignClient patientFeignClient;

    @MockitoBean
    private NoteFeignClient noteFeignClient;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;


    @Test
    @DisplayName("should calculate risk successfully")
    void shouldCalculateRiskSuccessfully() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);
        final Long patientId = 1L;

        final String note1 = "Note 1";
        final String note2 = "Note 2";
        final String note3 = "Note 3";
        final List<String> notes = List.of(note1, note2, note3);
        final int age = 30;
        final PatientRiskProfileDto riskProfileDto = getPatientRiskProfileDto(age, Gender.M);
        given(noteFeignClient.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignClient.getPatientRiskProfileById(patientId)).willReturn(riskProfileDto);

        //when
        final MvcResult result = mockMvc.perform(get("/risk/" + patientId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


        final String responseBody = result.getResponse().getContentAsString();

        //then
        assertThat(responseBody)
                .isEqualTo(objectMapper.writeValueAsString(RiskDto.NONE));
    }

    @Test
    @DisplayName("should return 502 when patient service is unavailable")
    void shouldReturn502WhenPatientServiceIsUnavailable() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);
        final Long patientId = 1L;
        final String note1 = "Fumeur";
        final String note2 = "Poids";
        final String note3 = "Taille";
        final List<String> notes = List.of(note1, note2, note3);

        given(noteFeignClient.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignClient.getPatientRiskProfileById(patientId))
                .willThrow(mock(FeignException.class));

        // when & then
        mockMvc.perform(
                        get("/risk/{id}", patientId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "unable to verify patient with id " + patientId
                        )));
    }

    @Test
    @DisplayName("should return 404 when patient is not found")
    void shouldReturn404WhenPatientIsNotFound() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);
        final Long patientId = 1L;
        final String note1 = "Fumeur";
        final String note2 = "Poids";
        final String note3 = "Taille";
        final List<String> notes = List.of(note1, note2, note3);

        given(noteFeignClient.getNoteContentList(patientId)).willReturn(notes);
        given(patientFeignClient.getPatientRiskProfileById(patientId))
                .willThrow(mock(FeignException.NotFound.class));

        // when & then
        mockMvc.perform(
                        get("/risk/{id}", patientId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Patient not found with id " + patientId
                        )));
    }

    private String generateToken(final String username, final String role, final Date now, final Date expiration) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", List.of(role))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey(secretKey))
                .compact();
    }

    private SecretKey getSignKey(final String secretKey) {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return hmacShaKeyFor(keyBytes);
    }

    private static PatientRiskProfileDto getPatientRiskProfileDto(final int age, final Gender female) {
        final PatientRiskProfileDto riskProfileDto = new PatientRiskProfileDto();
        riskProfileDto.setAge(age);
        riskProfileDto.setGender(female);

        return riskProfileDto;
    }
}
