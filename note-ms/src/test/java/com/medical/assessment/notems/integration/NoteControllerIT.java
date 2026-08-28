package com.medical.assessment.notems.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.notems.domain.note.service.NoteService;
import com.medical.assessment.notems.infrastructure.client.patient.service.PatientFeignService;
import com.medical.assessment.notems.note.model.NoteCreateDto;
import com.medical.assessment.notems.persistence.entity.Note;
import com.medical.assessment.notems.persistence.repository.NoteRepository;
import com.medical.assessment.notems.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
public class NoteControllerIT {
    public static final String TEST_NOTE_CONTENT = "Test note content";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NoteService noteService;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private PatientFeignService patientFeignService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;

    @AfterEach
    void cleanUp() {
        noteRepository.deleteAll();
    }

    @Test
    @DisplayName("should create a new note")
    void shouldCreateNewNote() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);
        final Long patient_id = 1L;
        final NoteCreateDto noteCreateDto = getNoteCreateDto();

        willDoNothing().given(patientFeignService).verifyPatientExists(patient_id);

        //when & then
        mockMvc.perform(post("/notes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteCreateDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.patientId").value(patient_id))
                .andExpect(jsonPath("$.content").value(TEST_NOTE_CONTENT));

        final List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(String.valueOf(patient_id));
        assertThat(notes).hasSize(1);
        assertThat(notes.getFirst().getContent()).isEqualTo(TEST_NOTE_CONTENT);

    }

    @Test
    @DisplayName("should return notes list successfully")
    void shouldReturnNotesListSuccessfully() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);
        final Long patient_id = 1L;
        final Note note = Note.builder()
                .patientId(String.valueOf(patient_id))
                .content(TEST_NOTE_CONTENT)
                .createdAt(LocalDateTime.now())
                .build();
        noteRepository.save(note);

        mockMvc.perform(get("/notes/patient/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value(TEST_NOTE_CONTENT))
                .andExpect(jsonPath("$[0].patientId").value(patient_id))
                .andExpect(jsonPath("$[0].id").isNotEmpty());
    }

    @Test
    @DisplayName("should return empty notes list successfully")
    void shouldReturnEmptyNotesListSuccessfully() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);

        mockMvc.perform(get("/notes/patient/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("should return notes content list successfully")
    void shouldReturnNotesContentListSuccessfully() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);
        final Long patient_id = 1L;
        final Note note = Note.builder()
                .patientId(String.valueOf(patient_id))
                .content(TEST_NOTE_CONTENT)
                .createdAt(LocalDateTime.now())
                .build();
        noteRepository.save(note);

        mockMvc.perform(get("/notes/patient/1/note-content")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value(TEST_NOTE_CONTENT));
    }

    @Test
    @DisplayName("should return Empty notes content list successfully")
    void shouldReturnEmptyNotesContentListSuccessfully() throws Exception {
        //given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("doctor", "DOCTOR", now, expiration);

        mockMvc.perform(get("/notes/patient/1/note-content")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
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

    private NoteCreateDto getNoteCreateDto() {
        return new NoteCreateDto()
                .patientId(1L)
                .content(TEST_NOTE_CONTENT);
    }
}
