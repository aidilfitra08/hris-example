package com.hrisexample.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hrisexample.demo.dto.OvertimeRequest;
import com.hrisexample.demo.dto.OvertimeResponse;
import com.hrisexample.demo.dto.OvertimeSearchRequest;
import com.hrisexample.demo.service.OvertimeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OvertimeController.class)
class OvertimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OvertimeService overtimeService;

    private ObjectMapper objectMapper;
    private OvertimeResponse sampleResponse;
    private OvertimeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleResponse = OvertimeResponse.builder()
                .id(1L)
                .employeeNip("EMP001")
                .employeeName("Budi Santoso")
                .overtimeStartTime(LocalDateTime.of(2026, 3, 17, 18, 0))
                .overtimeEndTime(LocalDateTime.of(2026, 3, 17, 21, 0))
                .reason("Deadline project")
                .status("PENDING")
                .build();

        sampleRequest = new OvertimeRequest();
        sampleRequest.setEmployeeNip("EMP001");
        sampleRequest.setEmployeeName("Budi Santoso");
        sampleRequest.setOvertimeStartTime(LocalDateTime.of(2026, 3, 17, 18, 0));
        sampleRequest.setOvertimeEndTime(LocalDateTime.of(2026, 3, 17, 21, 0));
        sampleRequest.setReason("Deadline project");
        sampleRequest.setStatus("PENDING");
    }

    // ── POST /api/v1/overtime ─────────────────────────────────────────────────

    @Test
    void create_shouldReturn200WithResponse() throws Exception {
        when(overtimeService.create(any(OvertimeRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/overtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.employeeNip").value("EMP001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void create_shouldReturn400_whenInvalidBody() throws Exception {
        OvertimeRequest invalid = new OvertimeRequest(); // missing required fields

        mockMvc.perform(post("/api/v1/overtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/v1/overtime/import ──────────────────────────────────────────

    @Test
    void importExcel_shouldReturn200WithCount() throws Exception {
        when(overtimeService.importFromExcel(any())).thenReturn(5);

        MockMultipartFile file = new MockMultipartFile(
                "file", "overtime.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy-excel-bytes".getBytes());

        mockMvc.perform(multipart("/api/v1/overtime/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(5));
    }

    // ── GET /api/v1/overtime ──────────────────────────────────────────────────

    @Test
    void search_shouldReturn200WithPage() throws Exception {
        when(overtimeService.search(any(OvertimeSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/v1/overtime")
                .param("keyword", "Budi")
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].employeeNip").value("EMP001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ── GET /api/v1/overtime/{id} ─────────────────────────────────────────────

    @Test
    void findById_shouldReturn200_whenFound() throws Exception {
        when(overtimeService.findById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/overtime/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.employeeName").value("Budi Santoso"));
    }

    @Test
    void findById_shouldReturn404_whenNotFound() throws Exception {
        when(overtimeService.findById(99L)).thenThrow(new EntityNotFoundException("Overtime not found with id 99"));

        mockMvc.perform(get("/api/v1/overtime/99"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/v1/overtime/{id} ─────────────────────────────────────────────

    @Test
    void update_shouldReturn200WithUpdatedResponse() throws Exception {
        OvertimeResponse updated = OvertimeResponse.builder()
                .id(1L)
                .employeeNip("EMP001")
                .employeeName("Budi Santoso")
                .overtimeStartTime(LocalDateTime.of(2026, 3, 17, 18, 0))
                .overtimeEndTime(LocalDateTime.of(2026, 3, 17, 22, 0))
                .reason("Updated reason")
                .status("APPROVED")
                .build();

        when(overtimeService.update(eq(1L), any(OvertimeRequest.class))).thenReturn(updated);

        sampleRequest.setStatus("APPROVED");
        mockMvc.perform(put("/api/v1/overtime/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void update_shouldReturn404_whenNotFound() throws Exception {
        when(overtimeService.update(eq(99L), any(OvertimeRequest.class)))
                .thenThrow(new EntityNotFoundException("Overtime not found with id 99"));

        mockMvc.perform(put("/api/v1/overtime/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/v1/overtime/{id} ──────────────────────────────────────────

    @Test
    void delete_shouldReturn200_whenFound() throws Exception {
        doNothing().when(overtimeService).delete(1L);

        mockMvc.perform(delete("/api/v1/overtime/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Overtime with id 1 deleted successfully"));
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Overtime not found with id 99"))
                .when(overtimeService).delete(99L);

        mockMvc.perform(delete("/api/v1/overtime/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/v1/overtime/export ───────────────────────────────────────────

    @Test
    void exportMonthly_shouldReturn200WithExcelFile() throws Exception {
        byte[] fakeExcel = "fake-excel-content".getBytes();
        when(overtimeService.exportMonthlyExcel(2026, 3)).thenReturn(fakeExcel);

        mockMvc.perform(get("/api/v1/overtime/export")
                .param("year", "2026")
                .param("month", "3"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=overtime_2026_03.xlsx"));
    }
}
