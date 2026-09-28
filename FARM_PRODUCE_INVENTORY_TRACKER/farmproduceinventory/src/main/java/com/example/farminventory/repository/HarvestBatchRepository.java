package com.example.farminventory.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.farminventory.model.HarvestBatch;

public interface HarvestBatchRepository extends JpaRepository<HarvestBatch, Long> {
    @Query("select coalesce(sum(h.quantity), 0) from HarvestBatch h where h.crop.id = :cropId")
    BigDecimal sumHarvestedByCrop(@Param("cropId") Long cropId);

    List<HarvestBatch> findByCropIdOrderByHarvestDateDesc(Long cropId);

    void deleteByCropId(Long cropId);
}
