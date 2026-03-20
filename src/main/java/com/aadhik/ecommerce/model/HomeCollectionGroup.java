package com.aadhik.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "home_collection_group")
public class HomeCollectionGroup {

    public static final String DISPLAY_MODE_BOX = "BOX";
    public static final String DISPLAY_MODE_SCROLL = "SCROLL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(name = "collection_ids", length = 3000)
    private String collectionIds;

    @Column(name = "display_mode", nullable = false, length = 20)
    private String displayMode = DISPLAY_MODE_BOX;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 1;

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    @PreUpdate
    public void normalizeDisplayMode() {
        if (displayMode == null || displayMode.isBlank()) {
            displayMode = DISPLAY_MODE_BOX;
            return;
        }
        String normalized = displayMode.trim().toUpperCase();
        if (!DISPLAY_MODE_SCROLL.equals(normalized)) {
            normalized = DISPLAY_MODE_BOX;
        }
        displayMode = normalized;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCollectionIds() {
        return collectionIds;
    }

    public void setCollectionIds(String collectionIds) {
        this.collectionIds = collectionIds;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isScrollDisplayMode() {
        return DISPLAY_MODE_SCROLL.equalsIgnoreCase(displayMode);
    }
}
