package com.aadhik.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "theme_config")
public class ThemeConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_bg", nullable = false, length = 20)
    private String primaryBackground;

    @Column(name = "primary_color", nullable = false, length = 20)
    private String primaryColor;

    @Column(name = "buy_now_bg", nullable = false, length = 20)
    private String buyNowBackground;

    @Column(name = "buy_now_color", nullable = false, length = 20)
    private String buyNowTextColor;

    @Column(name = "add_cart_bg", nullable = false, length = 20)
    private String addCartBackground;

    @Column(name = "add_cart_color", nullable = false, length = 20)
    private String addCartTextColor;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimaryBackground() {
        return primaryBackground;
    }

    public void setPrimaryBackground(String primaryBackground) {
        this.primaryBackground = primaryBackground;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getBuyNowBackground() {
        return buyNowBackground;
    }

    public void setBuyNowBackground(String buyNowBackground) {
        this.buyNowBackground = buyNowBackground;
    }

    public String getBuyNowTextColor() {
        return buyNowTextColor;
    }

    public void setBuyNowTextColor(String buyNowTextColor) {
        this.buyNowTextColor = buyNowTextColor;
    }

    public String getAddCartBackground() {
        return addCartBackground;
    }

    public void setAddCartBackground(String addCartBackground) {
        this.addCartBackground = addCartBackground;
    }

    public String getAddCartTextColor() {
        return addCartTextColor;
    }

    public void setAddCartTextColor(String addCartTextColor) {
        this.addCartTextColor = addCartTextColor;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}