package com.aadhik.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 1200)
    private String description;

    @Column(length = 80, unique = true)
    private String sku;

    @Column(length = 40)
    private String hsn;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "gallery_images", length = 3000)
    private String galleryImages;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "compare_price", precision = 10, scale = 2)
    private BigDecimal comparePrice;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "has_variants", nullable = false)
    private boolean hasVariants = false;

    @Column(name = "variant_data", length = 6000)
    private String variantData;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean featured = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_collection_map",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id"))
    private List<ProductCollection> collections = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(String galleryImages) {
        this.galleryImages = galleryImages;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getComparePrice() {
        return comparePrice;
    }

    public void setComparePrice(BigDecimal comparePrice) {
        this.comparePrice = comparePrice;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public boolean isHasVariants() {
        return hasVariants;
    }

    public void setHasVariants(boolean hasVariants) {
        this.hasVariants = hasVariants;
    }

    public String getVariantData() {
        return variantData;
    }

    public void setVariantData(String variantData) {
        this.variantData = variantData;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public List<ProductCollection> getCollections() {
        return collections;
    }

    public void setCollections(List<ProductCollection> collections) {
        this.collections = collections == null ? new ArrayList<>() : collections;
    }

    public ProductCollection getCollection() {
        return collections.isEmpty() ? null : collections.get(0);
    }

    public void setCollection(ProductCollection collection) {
        this.collections = new ArrayList<>();
        if (collection != null) {
            this.collections.add(collection);
        }
    }

}
