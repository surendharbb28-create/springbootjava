package com.example.farminventory.service;

import com.example.farminventory.exception.BusinessRuleException;
import com.example.farminventory.exception.ResourceNotFoundException;
import com.example.farminventory.model.Crop;
import com.example.farminventory.repository.CropRepository;
import com.example.farminventory.repository.HarvestBatchRepository;
import com.example.farminventory.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CropService {
    private final CropRepository cropRepository;
    private final HarvestBatchRepository harvestBatchRepository;
    private final SaleRepository saleRepository;

    public CropService(CropRepository cropRepository,
                       HarvestBatchRepository harvestBatchRepository,
                       SaleRepository saleRepository) {
        this.cropRepository = cropRepository;
        this.harvestBatchRepository = harvestBatchRepository;
        this.saleRepository = saleRepository;
    }

    public Crop create(Crop crop) {
        cropRepository.findByNameIgnoreCase(crop.getName()).ifPresent(existing -> {
            throw new BusinessRuleException("Crop already exists: " + crop.getName());
        });
        return cropRepository.save(crop);
    }

    public List<Crop> findAll() {
        return cropRepository.findAll();
    }

    public Crop findById(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + id));
    }

    @Transactional
    public Crop update(Long id, Crop updatedCrop) {
        Crop crop = findById(id);
        if (!crop.getName().equalsIgnoreCase(updatedCrop.getName())) {
            cropRepository.findByNameIgnoreCase(updatedCrop.getName()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new BusinessRuleException("Crop already exists: " + updatedCrop.getName());
                }
            });
        }
        crop.setName(updatedCrop.getName());
        crop.setCategory(updatedCrop.getCategory());
        crop.setUnit(updatedCrop.getUnit());
        return cropRepository.save(crop);
    }

    @Transactional
    public void delete(Long id) {
        Crop crop = findById(id);
        harvestBatchRepository.deleteByCropId(id);
        saleRepository.deleteByCropId(id);
        cropRepository.delete(crop);
    }
}
