package com.aadhik.ecommerce.service;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.SectionType;
import com.aadhik.ecommerce.repository.CatalogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CatalogService {

    @Inject
    private CatalogRepository repository;

    public List<HomeSlider> getHomeSliders() {
        return repository.findActiveSliders();
    }

    public List<HomepageSectionView> getHomepageSectionsWithProducts() {
        List<HomepageSectionView> result = new ArrayList<>();
        List<HomepageSection> sections = repository.findActiveSections();

        for (HomepageSection section : sections) {
            int limit = Math.max(1, section.getMaxItems());
            List<Product> products;

            if (section.getSectionType() == SectionType.COLLECTION
                    && section.getCollection() != null
                    && section.getCollection().getId() != null) {
                products = repository.findProductsByCollection(section.getCollection().getId(), limit);
            } else {
                products = repository.findFeaturedProducts(limit);
            }

            result.add(new HomepageSectionView(section, products));
        }

        return result;
    }

    public List<ProductCollection> getActiveCollections() {
        return repository.findCollections();
    }

    public List<Product> getProducts() {
        return repository.findProducts();
    }

    public HomeSlider saveSlider(HomeSlider slider) {
        return repository.saveSlider(slider);
    }

    public HomepageSection saveSection(HomepageSection section) {
        return repository.saveSection(section);
    }

    public ProductCollection saveCollection(ProductCollection collection) {
        return repository.saveCollection(collection);
    }

    public Product saveProduct(Product product) {
        return repository.saveProduct(product);
    }

    public record HomepageSectionView(HomepageSection section, List<Product> products) {
    }
}
