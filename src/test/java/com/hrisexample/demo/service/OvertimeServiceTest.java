package com.hrisexample.demo.service;

import com.hrisexample.demo.dto.OvertimeRequest;
import com.hrisexample.demo.dto.OvertimeResponse;
import com.hrisexample.demo.dto.OvertimeSearchRequest;
import com.hrisexample.demo.entity.Overtime;
import com.hrisexample.demo.repository.OvertimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OvertimeServiceTest {

    @Mock
    private OvertimeRepository overtimeRepository;

    @InjectMocks
    private OvertimeService overtimeService;

    private Overtime sampleEntity;
    private OvertimeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleEntity = new Overtime();
        sampleEntity.setId(1L);
        sampleEntity.setEmployeeNip("EMP001");
        sampleEntity.setEmployeeName("Budi Santoso");
        sampleEntity.setOvertimeStartTime(LocalDateTime.of(2026, 3, 17, 18, 0));
        sampleEntity.setOvertimeEndTime(LocalDateTime.of(2026, 3, 17, 21, 0));
        sampleEntity.setReason("Deadline project");
        sampleEntity.setStatus("PENDING");

        sampleRequest = new OvertimeRequest();
        sampleRequest.setEmployeeNip("EMP001");
        sampleRequest.setEmployeeName("Budi Santoso");
        sampleRequest.setOvertimeStartTime(LocalDateTime.of(2026, 3, 17, 18, 0));
        sampleRequest.setOvertimeEndTime(LocalDateTime.of(2026, 3, 17, 21, 0));
        sampleRequest.setReason("Deadline project");
        sampleRequest.setStatus("PENDING");
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_shouldSaveAndReturnResponse() {
        when(overtimeRepository.save(any(Overtime.class))).thenReturn(sampleEntity);

        OvertimeResponse response = overtimeService.create(sampleRequest);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmployeeNip()).isEqualTo("EMP001");
        assertThat(response.getEmployeeName()).isEqualTo("Budi Santoso");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        verify(overtimeRepository, times(1)).save(any(Overtime.class));
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_shouldReturnResponse_whenFound() {
        when(overtimeRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        OvertimeResponse response = overtimeService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmployeeName()).isEqualTo("Budi Santoso");
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(overtimeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> overtimeService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_shouldModifyAndReturnResponse() {
        sampleRequest.setStatus("APPROVED");
        sampleRequest.setReason("Updated reason");

        Overtime updated = new Overtime();
        updated.setId(1L);
        updated.setEmployeeNip("EMP001");
        updated.setEmployeeName("Budi Santoso");
        updated.setOvertimeStartTime(sampleRequest.getOvertimeStartTime());
        updated.setOvertimeEndTime(sampleRequest.getOvertimeEndTime());
        updated.setReason("Updated reason");
        updated.setStatus("APPROVED");

        when(overtimeRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(overtimeRepository.save(any(Overtime.class))).thenReturn(updated);

        OvertimeResponse response = overtimeService.update(1L, sampleRequest);

        assertThat(response.getStatus()).isEqualTo("APPROVED");
        assertThat(response.getReason()).isEqualTo("Updated reason");
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(overtimeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> overtimeService.update(99L, sampleRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_shouldCallDeleteById_whenFound() {
        when(overtimeRepository.existsById(1L)).thenReturn(true);

        overtimeService.delete(1L);

        verify(overtimeRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(overtimeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> overtimeService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(overtimeRepository, never()).deleteById(any());
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    void search_shouldReturnPagedResponse() {
        Page<Overtime> page = new PageImpl<>(List.of(sampleEntity));
        when(overtimeRepository.search(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        OvertimeSearchRequest req = new OvertimeSearchRequest();
        req.setKeyword("Budi");
        req.setStatus("PENDING");

        Page<OvertimeResponse> result = overtimeService.search(req);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEmployeeNip()).isEqualTo("EMP001");
    }

    // ── exportMonthlyExcel ────────────────────────────────────────────────────

    @Test
    void exportMonthlyExcel_shouldReturnNonEmptyBytes() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 1, 0, 0);
        when(overtimeRepository.findByMonth(start, end)).thenReturn(List.of(sampleEntity));

        byte[] result = overtimeService.exportMonthlyExcel(2026, 3);

        assertThat(result).isNotEmpty();
    }
}
