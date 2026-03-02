package com.aadhik.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "marquee_config")
public class MarqueeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "items_data", nullable = false, length = 4000)
    private String itemsData;

    @Column(nullable = false, length = 3)
    private String direction = "rtl";

    @Column(name = "speed_seconds", nullable = false)
    private int speedSeconds = 12;

    @Column(name = "background_mode", nullable = false, length = 20)
    private String backgroundMode = "solid";

    @Column(name = "solid_color", nullable = false, length = 20)
    private String solidColor = "#0f1f49";

    @Column(name = "gradient_colors", length = 200)
    private String gradientColors = "#0f1f49,#325ac7";

    @Column(name = "font_size_px", nullable = false)
    private int fontSizePx = 22;

    @Column(name = "font_weight", nullable = false, length = 3)
    private String fontWeight = "700";

    @Column(name = "text_color", nullable = false, length = 20)
    private String textColor = "#ffffff";

    @Column(name = "pause_on_hover", nullable = false)
    private boolean pauseOnHover = true;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemsData() {
        return itemsData;
    }

    public void setItemsData(String itemsData) {
        this.itemsData = itemsData;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getSpeedSeconds() {
        return speedSeconds;
    }

    public void setSpeedSeconds(int speedSeconds) {
        this.speedSeconds = speedSeconds;
    }

    public String getBackgroundMode() {
        return backgroundMode;
    }

    public void setBackgroundMode(String backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public String getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(String solidColor) {
        this.solidColor = solidColor;
    }

    public String getGradientColors() {
        return gradientColors;
    }

    public void setGradientColors(String gradientColors) {
        this.gradientColors = gradientColors;
    }

    public int getFontSizePx() {
        return fontSizePx;
    }

    public void setFontSizePx(int fontSizePx) {
        this.fontSizePx = fontSizePx;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public boolean isPauseOnHover() {
        return pauseOnHover;
    }

    public void setPauseOnHover(boolean pauseOnHover) {
        this.pauseOnHover = pauseOnHover;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}