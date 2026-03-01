package com.aadhik.ecommerce.service;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.MediaFile;
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

    public List<HomeSlider> getHomeActiveSliders() {
        return repository.findHomeSliders(true);
    }
    
     public List<HomeSlider> getHomeSliders() {
        return repository.findHomeSliders(false);
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
        return repository.findCollections(true);
    }
    
    public List<ProductCollection> getCollections() {
        return repository.findCollections(false);
    }

    public List<Product> getProducts() {
        return repository.findProducts();
    }

    public List<MediaFile> getMediaFiles() {
        return repository.findMediaFiles();
    }

    public MediaFile getMediaFile(Long id) {
        return repository.findMediaFileById(id);
    }

    public long getFileUsageCount(Long fileId) {
        return repository.countFileUsage(fileId);
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

    public MediaFile saveMediaFile(MediaFile mediaFile) {
        return repository.saveMediaFile(mediaFile);
    }

    public void deleteMediaFile(Long id) {
        repository.deleteMediaFileById(id);
    }
    
    public boolean isExistSKU(String sku, Long ignoreProductId) {
       return repository.isExistSKU(sku, ignoreProductId);
    }

    public static class HomepageSectionView {

        private final HomepageSection section;
        private final List<Product> products;

        public HomepageSectionView(HomepageSection section, List<Product> products) {
            this.section = section;
            this.products = products;
        }

        public HomepageSection getSection() {
            return section;
        }

        public List<Product> getProducts() {
            return products;
        }
    }
}
