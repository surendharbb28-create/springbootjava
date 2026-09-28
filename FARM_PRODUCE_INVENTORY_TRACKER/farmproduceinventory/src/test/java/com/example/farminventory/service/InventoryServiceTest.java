package com.example.farminventory.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.farminventory.exception.BusinessRuleException;
import com.example.farminventory.model.Crop;
import com.example.farminventory.model.Sale;
import com.example.farminventory.repository.CropRepository;
import com.example.farminventory.repository.HarvestBatchRepository;
import com.example.farminventory.repository.SaleRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock CropRepository cropRepository;
    @Mock HarvestBatchRepository harvestBatchRepository;
    @Mock SaleRepository saleRepository;
    @InjectMocks InventoryService inventoryService;

    @Test
    void rejectsSaleWhenQuantityExceedsStock() {
        Crop crop = new Crop("Tomato", "Vegetable", "kg");
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));
        when(harvestBatchRepository.sumHarvestedByCrop(1L)).thenReturn(new BigDecimal("10"));
        when(saleRepository.sumSoldByCrop(1L)).thenReturn(new BigDecimal("3"));

        Sale sale = new Sale();
        sale.setQuantity(new BigDecimal("8"));
        sale.setPricePerUnit(new BigDecimal("40"));
        sale.setSaleDate(LocalDate.now());

        assertThrows(BusinessRuleException.class, () -> inventoryService.recordSale(1L, sale));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void currentStockNeverReturnsNegativeValue() {
        when(cropRepository.findById(1L)).thenReturn(Optional.of(new Crop("Onion", "Vegetable", "kg")));
        when(harvestBatchRepository.sumHarvestedByCrop(1L)).thenReturn(new BigDecimal("100"));
        when(saleRepository.sumSoldByCrop(1L)).thenReturn(new BigDecimal("150"));

        assertEquals(BigDecimal.ZERO, inventoryService.getCurrentStock(1L));
    }
}
