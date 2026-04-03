package com.aadhik.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "shipping_rates")
public class ShippingRate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state_code", length = 30)
    private String stateCode;

    @Column(name = "state_name", length = 80)
    private String stateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "threshold_type", nullable = false, length = 10)
    private ShippingThresholdType thresholdType;

    @Column(name = "threshold_value", nullable = false, precision = 12, scale = 3)
    private BigDecimal thresholdValue;

    @Column(name = "charge", nullable = false, precision = 12, scale = 2)
    private BigDecimal charge;

    @Column(name = "active", nullable = false)
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public ShippingThresholdType getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(ShippingThresholdType thresholdType) {
        this.thresholdType = thresholdType;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
