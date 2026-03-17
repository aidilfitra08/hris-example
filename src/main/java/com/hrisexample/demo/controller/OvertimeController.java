package com.hrisexample.demo.controller;

import com.hrisexample.demo.dto.OvertimeRequest;
import com.hrisexample.demo.dto.OvertimeResponse;
import com.hrisexample.demo.dto.OvertimeSearchRequest;
import com.hrisexample.demo.service.OvertimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    // POST /api/v1/overtime — single create
    @PostMapping
    public ResponseEntity<OvertimeResponse> create(@Valid @RequestBody OvertimeRequest request) {
        return ResponseEntity.ok(overtimeService.create(request));
    }

    // POST /api/v1/overtime/import — bulk import via Excel
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file)
            throws IOException {
        int count = overtimeService.importFromExcel(file);
        return ResponseEntity.ok(Map.of("imported", count, "message", count + " records imported successfully"));
    }

    // GET /api/v1/overtime — search + pagination
    @GetMapping
    public ResponseEntity<Page<OvertimeResponse>> search(OvertimeSearchRequest request) {
        return ResponseEntity.ok(overtimeService.search(request));
    }

    // GET /api/v1/overtime/{id} — get by id
    @GetMapping("/{id}")
    public ResponseEntity<OvertimeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(overtimeService.findById(id));
    }

    // PUT /api/v1/overtime/{id} — update
    @PutMapping("/{id}")
    public ResponseEntity<OvertimeResponse> update(@PathVariable Long id,
            @Valid @RequestBody OvertimeRequest request) {
        return ResponseEntity.ok(overtimeService.update(id, request));
    }

    // DELETE /api/v1/overtime/{id} — delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        overtimeService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Overtime with id " + id + " deleted successfully"));
    }

    // GET /api/v1/overtime/export?year=2026&month=3 — download monthly Excel
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMonthly(@RequestParam int year,
            @RequestParam int month) throws IOException {
        byte[] bytes = overtimeService.exportMonthlyExcel(year, month);
        String filename = "overtime_" + year + "_" + String.format("%02d", month) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(Objects.requireNonNull(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .body(bytes);
    }
}
