package com.example.farminventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "crops")
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Crop name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String category;

    @NotBlank(message = "Unit is required")
    @Column(nullable = false)
    private String unit;

    public Crop() {}

    public Crop(String name, String category, String unit) {
        this.name = name;
        this.category = category;
        this.unit = unit;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
