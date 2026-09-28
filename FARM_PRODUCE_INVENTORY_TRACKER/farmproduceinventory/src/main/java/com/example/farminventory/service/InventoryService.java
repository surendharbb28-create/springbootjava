package com.example.farminventory.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.farminventory.exception.BusinessRuleException;
import com.example.farminventory.exception.ResourceNotFoundException;
import com.example.farminventory.model.Crop;
import com.example.farminventory.model.HarvestBatch;
import com.example.farminventory.model.Sale;
import com.example.farminventory.repository.CropRepository;
import com.example.farminventory.repository.HarvestBatchRepository;
import com.example.farminventory.repository.SaleRepository;

@Service
public class InventoryService {
    private final CropRepository cropRepository;
    private final HarvestBatchRepository harvestBatchRepository;
    private final SaleRepository saleRepository;

    public InventoryService(CropRepository cropRepository,
                            HarvestBatchRepository harvestBatchRepository,
                            SaleRepository saleRepository) {
        this.cropRepository = cropRepository;
        this.harvestBatchRepository = harvestBatchRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public HarvestBatch addHarvest(Long cropId, HarvestBatch request) {
        Crop crop = getCrop(cropId);
        request.setCrop(crop);
        return harvestBatchRepository.save(request);
    }

    public List<HarvestBatch> getHarvests(Long cropId) {
        getCrop(cropId);
        return harvestBatchRepository.findByCropIdOrderByHarvestDateDesc(cropId);
    }

    @Transactional
    public HarvestBatch updateHarvest(Long cropId, Long harvestId, HarvestBatch request) {
        Crop crop = getCrop(cropId);
        HarvestBatch harvest = harvestBatchRepository.findById(harvestId)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest batch not found with id: " + harvestId));
        if (!harvest.getCrop().getId().equals(cropId)) {
            throw new BusinessRuleException("Harvest batch does not belong to crop id: " + cropId);
        }
        BigDecimal oldQty = harvest.getQuantity();
        BigDecimal newQty = request.getQuantity();
        BigDecimal totalHarvested = harvestBatchRepository.sumHarvestedByCrop(cropId);
        BigDecimal totalSold = saleRepository.sumSoldByCrop(cropId);
        BigDecimal potentialTotal = totalHarvested.subtract(oldQty).add(newQty);
        if (potentialTotal.compareTo(totalSold) < 0) {
            throw new BusinessRuleException("Cannot reduce harvest quantity to " + newQty +
                    " " + crop.getUnit() + ". Sold quantity (" + totalSold + " " + crop.getUnit() +
                    ") would exceed total harvested quantity (" + potentialTotal + " " + crop.getUnit() + ").");
        }
        harvest.setQuantity(newQty);
        harvest.setHarvestDate(request.getHarvestDate());
        return harvestBatchRepository.save(harvest);
    }

    @Transactional
    public void deleteHarvest(Long cropId, Long harvestId) {
        Crop crop = getCrop(cropId);
        HarvestBatch harvest = harvestBatchRepository.findById(harvestId)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest batch not found with id: " + harvestId));
        if (!harvest.getCrop().getId().equals(cropId)) {
            throw new BusinessRuleException("Harvest batch does not belong to crop id: " + cropId);
        }
        BigDecimal totalHarvested = harvestBatchRepository.sumHarvestedByCrop(cropId);
        BigDecimal totalSold = saleRepository.sumSoldByCrop(cropId);
        BigDecimal remainingHarvest = totalHarvested.subtract(harvest.getQuantity());
        if (remainingHarvest.compareTo(totalSold) < 0) {
            throw new BusinessRuleException("Cannot delete harvest batch. Sold quantity (" + totalSold +
                    " " + crop.getUnit() + ") would exceed remaining harvest quantity (" + remainingHarvest +
                    " " + crop.getUnit() + ").");
        }
        harvestBatchRepository.delete(harvest);
    }

    @Transactional
    public Sale recordSale(Long cropId, Sale request) {
        Crop crop = getCrop(cropId);
        BigDecimal stock = getCurrentStock(cropId);

        if (request.getQuantity().compareTo(stock) > 0) {
            throw new BusinessRuleException(
                    "Sale rejected. Requested " + request.getQuantity() +
                    " " + crop.getUnit() + " but only " + stock +
                    " " + crop.getUnit() + " is available.");
        }

        request.setCrop(crop);
        return saleRepository.save(request);
    }

    public List<Sale> getSales(Long cropId) {
        getCrop(cropId);
        return saleRepository.findByCropIdOrderBySaleDateDesc(cropId);
    }

    @Transactional
    public Sale updateSale(Long cropId, Long saleId, Sale request) {
        Crop crop = getCrop(cropId);
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + saleId));
        if (!sale.getCrop().getId().equals(cropId)) {
            throw new BusinessRuleException("Sale does not belong to crop id: " + cropId);
        }
        BigDecimal totalHarvested = harvestBatchRepository.sumHarvestedByCrop(cropId);
        BigDecimal otherSold = saleRepository.sumSoldByCrop(cropId).subtract(sale.getQuantity());
        BigDecimal availableStock = totalHarvested.subtract(otherSold).max(BigDecimal.ZERO);
        if (request.getQuantity().compareTo(availableStock) > 0) {
            throw new BusinessRuleException(
                    "Sale update rejected. Requested " + request.getQuantity() +
                    " " + crop.getUnit() + " but only " + availableStock +
                    " " + crop.getUnit() + " is available.");
        }
        sale.setQuantity(request.getQuantity());
        sale.setPricePerUnit(request.getPricePerUnit());
        sale.setSaleDate(request.getSaleDate());
        return saleRepository.save(sale);
    }

    @Transactional
    public void deleteSale(Long cropId, Long saleId) {
        getCrop(cropId);
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + saleId));
        if (!sale.getCrop().getId().equals(cropId)) {
            throw new BusinessRuleException("Sale does not belong to crop id: " + cropId);
        }
        saleRepository.delete(sale);
    }

    public BigDecimal getCurrentStock(Long cropId) {
        getCrop(cropId);
        BigDecimal harvested = harvestBatchRepository.sumHarvestedByCrop(cropId);
        BigDecimal sold = saleRepository.sumSoldByCrop(cropId);
        return harvested.subtract(sold).max(BigDecimal.ZERO);
    }

    public BigDecimal getRevenue(Long cropId, LocalDate from, LocalDate to) {
        getCrop(cropId);
        if (from == null || to == null) {
            throw new BusinessRuleException("Both from and to dates are required.");
        }
        if (from.isAfter(to)) {
            throw new BusinessRuleException("From date cannot be after to date.");
        }
        return saleRepository.totalRevenue(cropId, from, to);
    }

    private Crop getCrop(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + cropId));
    }
}
