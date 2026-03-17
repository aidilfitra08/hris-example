package com.hrisexample.demo.repository;

import com.hrisexample.demo.entity.Overtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {

    @Query("""
            SELECT o FROM Overtime o
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(o.employeeNip) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(o.employeeName) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:status IS NULL OR :status = '' OR o.status = :status)
            """)
    Page<Overtime> search(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

    @Query("""
            SELECT o FROM Overtime o
            WHERE o.overtimeStartTime >= :start
              AND o.overtimeStartTime < :end
            """)
    List<Overtime> findByMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
