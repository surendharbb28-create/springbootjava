package com.example.farminventory.repository;

import com.example.farminventory.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("select coalesce(sum(s.quantity), 0) from Sale s where s.crop.id = :cropId")
    BigDecimal sumSoldByCrop(@Param("cropId") Long cropId);

    @Query("select coalesce(sum(s.quantity * s.pricePerUnit), 0) from Sale s " +
           "where s.crop.id = :cropId and s.saleDate between :from and :to")
    BigDecimal totalRevenue(@Param("cropId") Long cropId,
                            @Param("from") LocalDate from,
                            @Param("to") LocalDate to);

    List<Sale> findByCropIdOrderBySaleDateDesc(Long cropId);

    void deleteByCropId(Long cropId);
}
