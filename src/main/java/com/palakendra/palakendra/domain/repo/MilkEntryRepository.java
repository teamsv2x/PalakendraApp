package com.palakendra.palakendra.domain.repo;

import com.palakendra.palakendra.domain.entity.MilkEntry;
import com.palakendra.palakendra.domain.entity.enums.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MilkEntryRepository extends JpaRepository<MilkEntry, Long> {
    List<MilkEntry> findByCustomerOrgIdAndDateBetween(Long customerOrgId, LocalDate from, LocalDate to);

    @Query("select coalesce(sum(m.liters), 0) from MilkEntry m where m.customerOrg.id = :coId and m.date between :from and :to")
    BigDecimal sumLitersBetween(@Param("coId") Long coId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(m.liters), 0) from MilkEntry m where m.customerOrg.id = :coId and m.date = :day")
    BigDecimal sumForDay(@Param("coId") Long coId, @Param("day") LocalDate day);

    @Query("select coalesce(sum(m.liters), 0) from MilkEntry m where m.customerOrg.id = :coId and m.date = :day and m.shift = :shift")
    BigDecimal sumForDayAndShift(@Param("coId") Long coId, @Param("day") LocalDate day, @Param("shift") Shift shift);


    @Modifying
    @Query("delete from MilkEntry m where m.customerOrg.id = :coId")
    void deleteByCustomerOrgId(Long coId);
}
