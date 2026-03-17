package com.hrisexample.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "overtime")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Overtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_nip", length = 50)
    private String employeeNip;

    @Column(name = "employee_name", length = 100)
    private String employeeName;

    @Column(name = "overtime_start_time")
    private LocalDateTime overtimeStartTime;

    @Column(name = "overtime_end_time")
    private LocalDateTime overtimeEndTime;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 20)
    private String status;

    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 100)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}
