package com.example.farminventory.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.farminventory.model.HarvestBatch;
import com.example.farminventory.model.Sale;
import com.example.farminventory.service.InventoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/crops/{cropId}/harvests")
    @ResponseStatus(HttpStatus.CREATED)
    public HarvestBatch addHarvest(@PathVariable Long cropId,
                                   @Valid @RequestBody HarvestRequest request) {
        HarvestBatch harvest = new HarvestBatch();
        harvest.setQuantity(request.quantity());
        harvest.setHarvestDate(request.harvestDate());
        return inventoryService.addHarvest(cropId, harvest);
    }

    @GetMapping("/crops/{cropId}/harvests")
    public List<HarvestBatch> getHarvests(@PathVariable Long cropId) {
        return inventoryService.getHarvests(cropId);
    }

    @PutMapping("/crops/{cropId}/harvests/{harvestId}")
    public HarvestBatch updateHarvest(@PathVariable Long cropId,
                                      @PathVariable Long harvestId,
                                      @Valid @RequestBody HarvestRequest request) {
        HarvestBatch harvest = new HarvestBatch();
        harvest.setQuantity(request.quantity());
        harvest.setHarvestDate(request.harvestDate());
        return inventoryService.updateHarvest(cropId, harvestId, harvest);
    }

    @DeleteMapping("/crops/{cropId}/harvests/{harvestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHarvest(@PathVariable Long cropId,
                              @PathVariable Long harvestId) {
        inventoryService.deleteHarvest(cropId, harvestId);
    }

    @PostMapping("/crops/{cropId}/sales")
    @ResponseStatus(HttpStatus.CREATED)
    public Sale recordSale(@PathVariable Long cropId,
                           @Valid @RequestBody SaleRequest request) {
        Sale sale = new Sale();
        sale.setQuantity(request.quantity());
        sale.setPricePerUnit(request.pricePerUnit());
        sale.setSaleDate(request.saleDate());
        return inventoryService.recordSale(cropId, sale);
    }

    @GetMapping("/crops/{cropId}/sales")
    public List<Sale> getSales(@PathVariable Long cropId) {
        return inventoryService.getSales(cropId);
    }

    @PutMapping("/crops/{cropId}/sales/{saleId}")
    public Sale updateSale(@PathVariable Long cropId,
                           @PathVariable Long saleId,
                           @Valid @RequestBody SaleRequest request) {
        Sale sale = new Sale();
        sale.setQuantity(request.quantity());
        sale.setPricePerUnit(request.pricePerUnit());
        sale.setSaleDate(request.saleDate());
        return inventoryService.updateSale(cropId, saleId, sale);
    }

    @DeleteMapping("/crops/{cropId}/sales/{saleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSale(@PathVariable Long cropId,
                           @PathVariable Long saleId) {
        inventoryService.deleteSale(cropId, saleId);
    }

    @GetMapping("/crops/{cropId}/stock")
    public StockResponse getStock(@PathVariable Long cropId) {
        return new StockResponse(cropId, inventoryService.getCurrentStock(cropId));
    }

    @GetMapping("/crops/{cropId}/revenue")
    public RevenueResponse getRevenue(
            @PathVariable Long cropId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return new RevenueResponse(cropId, from, to,
                inventoryService.getRevenue(cropId, from, to));
    }

    public record StockResponse(Long cropId, BigDecimal currentStock) {}
    public record RevenueResponse(Long cropId, LocalDate from, LocalDate to, BigDecimal totalRevenue) {}
        public record HarvestRequest(
            @NotNull(message = "Quantity is required")
            @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
            BigDecimal quantity,
            @NotNull(message = "Harvest date is required") LocalDate harvestDate) {}
        public record SaleRequest(
            @NotNull(message = "Quantity is required")
            @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
            BigDecimal quantity,
            @NotNull(message = "Price per unit is required")
            @DecimalMin(value = "0.00", message = "Price cannot be negative")
            BigDecimal pricePerUnit,
            @NotNull(message = "Sale date is required") LocalDate saleDate) {}
}
