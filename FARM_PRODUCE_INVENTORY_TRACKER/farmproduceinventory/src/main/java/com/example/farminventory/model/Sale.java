package com.example.farminventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "crop_id", nullable = false)
    @NotNull(message = "Crop is required")
    private Crop crop;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerUnit;

    @NotNull(message = "Sale date is required")
    @Column(nullable = false)
    private LocalDate saleDate;

    public Sale() {}

    public Long getId() { return id; }
    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }

    @Transient
    public BigDecimal getRevenue() {
        if (quantity == null || pricePerUnit == null) return BigDecimal.ZERO;
        return quantity.multiply(pricePerUnit);
    }
}
