package com.hrisexample.demo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OvertimeRequest {

    @NotBlank(message = "employee_nip is required")
    @ExcelProperty("NIP Karyawan")
    private String employeeNip;

    @NotBlank(message = "employee_name is required")
    @ExcelProperty("Nama Karyawan")
    private String employeeName;

    @NotNull(message = "overtime_start_time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("Waktu Mulai Lembur")
    private LocalDateTime overtimeStartTime;

    @NotNull(message = "overtime_end_time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("Waktu Selesai Lembur")
    private LocalDateTime overtimeEndTime;

    @ExcelProperty("Alasan")
    private String reason;

    @NotBlank(message = "status is required")
    @Pattern(regexp = "APPROVED|PENDING|REJECTED", message = "status must be APPROVED, PENDING, or REJECTED")
    @ExcelProperty("Status")
    private String status;
}
