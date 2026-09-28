package com.example.farminventory.controller;

import com.example.farminventory.model.Crop;
import com.example.farminventory.service.CropService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin
public class CropController {
    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Crop create(@Valid @RequestBody Crop crop) {
        return cropService.create(crop);
    }

    @GetMapping
    public List<Crop> getAll() {
        return cropService.findAll();
    }

    @GetMapping("/{cropId}")
    public Crop getById(@PathVariable Long cropId) {
        return cropService.findById(cropId);
    }

    @PutMapping("/{cropId}")
    public Crop update(@PathVariable Long cropId, @Valid @RequestBody Crop crop) {
        return cropService.update(cropId, crop);
    }

    @DeleteMapping("/{cropId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long cropId) {
        cropService.delete(cropId);
    }
}
