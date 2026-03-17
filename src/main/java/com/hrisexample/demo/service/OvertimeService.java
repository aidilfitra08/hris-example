package com.hrisexample.demo.service;

import cn.idev.excel.FastExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.hrisexample.demo.dto.OvertimeRequest;
import com.hrisexample.demo.dto.OvertimeResponse;
import com.hrisexample.demo.dto.OvertimeSearchRequest;
import com.hrisexample.demo.entity.Overtime;
import com.hrisexample.demo.excel.OvertimeImportListener;
import com.hrisexample.demo.repository.OvertimeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;

    // ── Single create ─────────────────────────────────────────────────────────
    public OvertimeResponse create(OvertimeRequest req) {
        Overtime entity = toEntity(new Overtime(), req);
        return toResponse(overtimeRepository.save(entity));
    }

    // ── Bulk import via Excel ─────────────────────────────────────────────────
    public int importFromExcel(MultipartFile file) throws IOException {
        OvertimeImportListener listener = new OvertimeImportListener();
        FastExcel.read(file.getInputStream(), OvertimeRequest.class, listener).sheet().doRead();
        List<Overtime> entities = listener.getRows().stream()
                .map(req -> toEntity(new Overtime(), req))
                .toList();
        overtimeRepository.saveAll(entities);
        return entities.size();
    }

    // ── Search with pagination ────────────────────────────────────────────────
    public Page<OvertimeResponse> search(OvertimeSearchRequest req) {
        Sort sort = req.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(req.getSortBy()).ascending()
                : Sort.by(req.getSortBy()).descending();
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sort);
        return overtimeRepository.search(req.getKeyword(), req.getStatus(), pageable)
                .map(this::toResponse);
    }

    // ── Get by id ─────────────────────────────────────────────────────────────
    public OvertimeResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    // ── Update ────────────────────────────────────────────────────────────────
    public OvertimeResponse update(Long id, OvertimeRequest req) {
        Overtime entity = getOrThrow(id);
        toEntity(entity, req);
        return toResponse(overtimeRepository.save(entity));
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    public void delete(Long id) {
        if (!overtimeRepository.existsById(id)) {
            throw new EntityNotFoundException("Overtime not found with id " + id);
        }
        overtimeRepository.deleteById(id);
    }

    // ── Export monthly Excel ──────────────────────────────────────────────────
    public byte[] exportMonthlyExcel(int year, int month) throws IOException {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().plusDays(1).atStartOfDay();

        List<Overtime> data = overtimeRepository.findByMonth(start, end);
        List<OvertimeResponse> rows = data.stream().map(this::toResponse).toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WriteSheet sheet = FastExcel.writerSheet("Overtime " + year + "-" + String.format("%02d", month)).build();
        try (var writer = FastExcel.write(out, OvertimeResponse.class).build()) {
            writer.write(rows, sheet);
        }
        return out.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Overtime getOrThrow(Long id) {
        return overtimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Overtime not found with id " + id));
    }

    private Overtime toEntity(Overtime entity, OvertimeRequest req) {
        entity.setEmployeeNip(req.getEmployeeNip());
        entity.setEmployeeName(req.getEmployeeName());
        entity.setOvertimeStartTime(req.getOvertimeStartTime());
        entity.setOvertimeEndTime(req.getOvertimeEndTime());
        entity.setReason(req.getReason());
        entity.setStatus(req.getStatus());
        return entity;
    }

    private OvertimeResponse toResponse(Overtime e) {
        return OvertimeResponse.builder()
                .id(e.getId())
                .employeeNip(e.getEmployeeNip())
                .employeeName(e.getEmployeeName())
                .overtimeStartTime(e.getOvertimeStartTime())
                .overtimeEndTime(e.getOvertimeEndTime())
                .reason(e.getReason())
                .status(e.getStatus())
                .createdBy(e.getCreatedBy())
                .createdDate(e.getCreatedDate())
                .lastModifiedBy(e.getLastModifiedBy())
                .lastModifiedDate(e.getLastModifiedDate())
                .build();
    }
}
