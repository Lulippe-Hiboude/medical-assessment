package com.medical.assessment.riskms.config;

import com.medical.assessment.riskms.infrastructure.client.note.service.NoteFeignService;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.Gender;
import com.medical.assessment.riskms.infrastructure.client.patient.dto.PatientRiskProfileDto;
import com.medical.assessment.riskms.infrastructure.client.patient.service.PatientFeignService;
import com.medical.assessment.riskms.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private NoteFeignService noteFeignService;

    @MockitoBean
    private PatientFeignService patientFeignService;

    private String secretKey;
    private static final long EXPIRATION_DATE = 3600L;

    @BeforeEach
    void setup() {
        final SecretKey key = Jwts.SIG.HS256.key().build();
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        ReflectionTestUtils.setField(jwtService, "secretKey",
                secretKey);

        ReflectionTestUtils.setField(jwtService, "expirationDate", EXPIRATION_DATE);
    }

    @Test
    @DisplayName("should_deny_access_to_protected_endpoint_without_token")
    void should_deny_access_to_protected_endpoint_without_token() throws Exception {
        //when & then
        mockMvc.perform(get("/risk/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should_allow_access_to_protected_endpoint_with_valid_token")
    void should_allow_access_to_protected_endpoint_with_valid_token() throws Exception {
        // given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + EXPIRATION_DATE);
        final String token = generateToken("test", "DOCTOR", now, expiration);
        final long patientId = 1L;

        final PatientRiskProfileDto riskProfileDto = new PatientRiskProfileDto();
        riskProfileDto.setAge(31);
        riskProfileDto.setGender(Gender.M);

        given(noteFeignService.getNoteContentList(patientId)).willReturn(List.of());
        given(patientFeignService.getPatientRiskProfile(patientId)).willReturn(riskProfileDto);

        // when & then
        mockMvc.perform(get("/risk/{id}", patientId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should_fail_when_token_is_expired")
    void should_fail_when_token_is_expired() throws Exception {
        // given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() - EXPIRATION_DATE);
        final String expiredToken = generateToken("test", "DOCTOR", now, expiration);
        final long patientId = 1L;

        // when & then
        mockMvc.perform(get("/risk/{id}", patientId)
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should deny access when token role is not doctor")
    void should_deny_access_when_token_role_is_doctor() throws Exception {
        // given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + EXPIRATION_DATE);
        final String token = generateToken("test", "ORGANIZER", now, expiration);
        final long patientId = 1L;

        // when & then
        mockMvc.perform(get("/risk/{id}", patientId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
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
}