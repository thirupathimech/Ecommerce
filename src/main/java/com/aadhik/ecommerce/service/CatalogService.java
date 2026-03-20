package com.aadhik.ecommerce.service;

import com.aadhik.ecommerce.model.HomeCollectionGroup;
import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSectionOrderItem;
import com.aadhik.ecommerce.model.HomeSectionType;
import static com.aadhik.ecommerce.model.HomeSectionType.DIV_SECTION;
import static com.aadhik.ecommerce.model.HomeSectionType.HOME_SLIDER;
import static com.aadhik.ecommerce.model.HomeSectionType.MARQUEE;
import static com.aadhik.ecommerce.model.HomeSectionType.VIDEO_CAROUSEL;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.SectionType;
import com.aadhik.ecommerce.model.VideoCarouselItem;
import com.aadhik.ecommerce.repository.CatalogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import static com.aadhik.ecommerce.model.HomeSectionType.PRODUCTS_COLLECTION;

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

    public List<HomeDivSection> getHomeActiveDivSections() {
        return repository.findHomeDivSections(true);
    }

    public List<HomeCollectionGroup> getHomeActiveCollectionGroups() {
        return repository.findHomeCollectionGroups(true);
    }

    public List<HomeDivSection> getHomeDivSections() {
        return repository.findHomeDivSections(false);
    }

    public List<HomeCollectionGroup> getHomeCollectionGroups() {
        return repository.findHomeCollectionGroups(false);
    }

    public List<VideoCarouselItem> getHomeActiveVideoCarouselItems() {
        return repository.findVideoCarouselItems(true);
    }

    public List<VideoCarouselItem> getVideoCarouselItems() {
        return repository.findVideoCarouselItems(false);
    }

    public List<HomepageSection> getHomepageSections() {
        return repository.findSections(false);
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

    public List<HomeSectionOrderItem> getHomeSectionOrderItems() {
        return repository.findHomeSectionOrderItems();
    }

    public void saveHomeSectionOrderItems(List<HomeSectionOrderItem> items) {
        repository.replaceHomeSectionOrderItems(items);
    }

    public List<HomeRenderSection> getOrderedHomeRenderSections() {
        List<HomeRenderSection> sections = new ArrayList<>();
        for (HomeSectionOrderItem item : repository.findHomeSectionOrderItems()) {
            if (item.getSectionType() == null || item.getRecordId() == null) {
                continue;
            }
            switch (item.getSectionType()) {
                case HOME_SLIDER -> {
                    HomeSlider slider = repository.findHomeSliderById(item.getRecordId());
                    if (slider != null && slider.isActive()) {
                        sections.add(HomeRenderSection.forSlider(slider));
                    }
                }
                case DIV_SECTION -> {
                    HomeDivSection divSection = repository.findHomeDivSectionById(item.getRecordId());
                    if (divSection != null && divSection.isActive()) {
                        sections.add(HomeRenderSection.forDivSection(divSection));
                    }
                }
                case VIDEO_CAROUSEL -> {
                    VideoCarouselItem videoItem = repository.findVideoCarouselItemById(item.getRecordId());
                    if (videoItem != null && videoItem.isActive()) {
                        sections.add(HomeRenderSection.forVideoCarousel(videoItem));
                    }
                }
                case PRODUCTS_COLLECTION -> {
                    HomepageSection collectionSection = repository.findHomepageSectionById(item.getRecordId());
                    if (collectionSection != null && collectionSection.isActive()) {
                        int limit = Math.max(1, collectionSection.getMaxItems());
                        List<Product> products;
                        if (collectionSection.getSectionType() == SectionType.COLLECTION
                                && collectionSection.getCollection() != null
                                && collectionSection.getCollection().getId() != null) {
                            products = repository.findProductsByCollection(collectionSection.getCollection().getId(), limit);
                        } else {
                            products = repository.findFeaturedProducts(limit);
                        }
                        sections.add(HomeRenderSection.forCollectionSection(collectionSection, products));
                    }
                }
                case COLLECTION_GROUP -> {
                    HomeCollectionGroup collectionGroup = repository.findHomeCollectionGroupById(item.getRecordId());
                    if (collectionGroup != null && collectionGroup.isActive()) {
                        sections.add(HomeRenderSection.forCollectionGroup(collectionGroup,
                                toCollections(collectionGroup.getCollectionIds())));
                    }
                }
                case MARQUEE -> {
                    MarqueeConfig marquee = repository.findMarqueeConfigById(item.getRecordId());
                    if (marquee != null && marquee.isActive()) {
                        sections.add(HomeRenderSection.forMarquee(marquee));
                    }
                }
                default -> {
                }
            }
        }
        return sections;
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

    public List<MarqueeConfig> getMarqueeConfigs() {
        return repository.findMarqueeConfigs();
    }

    public MarqueeConfig getActiveMarqueeConfig() {
        return repository.findActiveMarqueeConfig();
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

    public VideoCarouselItem saveVideoCarouselItem(VideoCarouselItem item) {
        return repository.saveVideoCarouselItem(item);
    }

    public HomeDivSection saveHomeDivSection(HomeDivSection section) {
        return repository.saveHomeDivSection(section);
    }

    public void deleteHomeDivSection(Long id) {
        repository.deleteHomeDivSection(id);
    }

    public HomeCollectionGroup saveHomeCollectionGroup(HomeCollectionGroup group) {
        return repository.saveHomeCollectionGroup(group);
    }

    public void deleteHomeCollectionGroup(Long id) {
        repository.deleteHomeCollectionGroup(id);
    }

    public void deleteVideoCarouselItem(Long id) {
        repository.deleteVideoCarouselItem(id);
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

    public MarqueeConfig saveMarqueeConfig(MarqueeConfig marqueeConfig) {
        return repository.saveMarqueeConfig(marqueeConfig);
    }

    public boolean deleteMarqueeConfig(Long id) {
        return repository.deleteMarqueeConfig(id);
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

    private List<ProductCollection> toCollections(String collectionIds) {
        if (collectionIds == null || collectionIds.isBlank()) {
            return List.of();
        }
        List<ProductCollection> result = new ArrayList<>();
        for (String row : collectionIds.split("\n")) {
            String value = row == null ? "" : row.trim();
            if (value.isEmpty()) {
                continue;
            }
            try {
                Long id = Long.parseLong(value);
                ProductCollection collection = repository.findCollectionById(id);
                if (collection != null && collection.isActive()) {
                    result.add(collection);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    public static class HomeRenderSection {

        private HomeSectionType type;
        private HomeSlider slider;
        private HomeDivSection divSection;
        private VideoCarouselItem videoItem;
        private HomepageSection collectionSection;
        private HomeCollectionGroup collectionGroup;
        private List<Product> products = List.of();
        private List<ProductCollection> collections = List.of();
        private MarqueeConfig marquee;

        public static HomeRenderSection forSlider(HomeSlider slider) {
            HomeRenderSection section = new HomeRenderSection();
            section.type = HomeSectionType.HOME_SLIDER;
            section.slider = slider;
            return section;
        }

        public static HomeRenderSection forDivSection(HomeDivSection divSection) {
            HomeRenderSection section = new HomeRenderSection();
            section.type = HomeSectionType.DIV_SECTION;
            section.divSection = divSection;
            return section;
        }

        public static HomeRenderSection forVideoCarousel(VideoCarouselItem videoItem) {
            HomeRenderSection section = new HomeRenderSection();
            section.type = HomeSectionType.VIDEO_CAROUSEL;
            section.videoItem = videoItem;
            return section;
        }

        public static HomeRenderSection forCollectionSection(HomepageSection collectionSection, List<Product> products) {
            HomeRenderSection section = new HomeRenderSection();
            section.type = HomeSectionType.PRODUCTS_COLLECTION;
            section.collectionSection = collectionSection;
            section.products = products == null ? List.of() : products;
            return section;
        }

        public static HomeRenderSection forCollectionGroup(HomeCollectionGroup collectionGroup, List<ProductCollection> collections) {
            HomeRenderSection section = new HomeRenderSection();
            section.type = HomeSectionType.COLLECTION_GROUP;
            section.collectionGroup = collectionGroup;
            section.collections = collections == null ? List.of() : collections;
            return section;
        }

        public static HomeRenderSection forMarquee(MarqueeConfig marquee) {
            HomeRenderSection section = new HomeRenderSection();
            section.type = HomeSectionType.MARQUEE;
            section.marquee = marquee;
            return section;
        }

        public HomeSectionType getType() {
            return type;
        }

        public HomeSlider getSlider() {
            return slider;
        }

        public HomeDivSection getDivSection() {
            return divSection;
        }

        public VideoCarouselItem getVideoItem() {
            return videoItem;
        }

        public HomepageSection getCollectionSection() {
            return collectionSection;
        }

        public HomeCollectionGroup getCollectionGroup() {
            return collectionGroup;
        }

        public List<Product> getProducts() {
            return products;
        }

        public List<ProductCollection> getCollections() {
            return collections;
        }

        public MarqueeConfig getMarquee() {
            return marquee;
        }
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
