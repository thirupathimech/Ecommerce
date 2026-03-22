package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ContentPage;
import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.VideoCarouselItem;
import com.aadhik.ecommerce.service.CatalogService;
import com.aadhik.ecommerce.service.ContentPageService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author THIRUPATHI G
 */
@Named
@RequestScoped
public class HomePageBean extends BaseBean {

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");

    @Inject
    private CatalogService catalogService;

    @Inject
    private ContentPageService contentPageService;

    private Long selectedProductId;
    private Integer selectedVariantIndex;

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeActiveSliders();
    }

    public List<HomeDivSection> getHomeDivSections() {
        return catalogService.getHomeActiveDivSections();
    }

    public List<CatalogService.HomeRenderSection> getOrderedHomeSections() {
        return catalogService.getOrderedHomeRenderSections();
    }

    public boolean hasOrderedHomeSections() {
        return !getOrderedHomeSections().isEmpty();
    }

    public List<VideoCarouselItem> getVideoCarouselItems() {
        return catalogService.getHomeActiveVideoCarouselItems();
    }

    public List<ProductCollection> getActiveCollections() {
        return catalogService.getActiveCollections();
    }

    public MarqueeConfig getActiveMarquee() {
        return catalogService.getActiveMarqueeConfig();
    }

    public List<String> getCarouselVideos(VideoCarouselItem item) {
        if (item == null) {
            return List.of();
        }
        List<String> videos = parseVideoLines(item.getVideoUrls());
        if (videos.isEmpty()) {
            videos = parseVideoLines(item.getVideoUrl());
        }
        return videos;
    }

    public int carouselVideoCount(VideoCarouselItem item) {
        return getCarouselVideos(item).size();
    }

    private List<String> parseVideoLines(String values) {
        if (values == null || values.isBlank()) {
            return List.of();
        }
        return values.lines()
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getMarqueeItems(MarqueeConfig marquee) {
        if (marquee == null || marquee.getItemsData() == null || marquee.getItemsData().isBlank()) {
            return List.of();
        }
        return marquee.getItemsData().lines().map(String::trim).filter(text -> !text.isBlank()).collect(Collectors.toList());
    }

    public List<String> getActiveMarqueeItems() {
        return getMarqueeItems(getActiveMarquee());
    }

    public List<String> getMarqueeLoopedItems(MarqueeConfig marquee) {
        List<String> items = getMarqueeItems(marquee);
        if (items.isEmpty()) {
            return List.of();
        }
        return buildContinuousItems(items, 18);
    }

    public List<String> getActiveMarqueeLoopedItems() {
        return getMarqueeLoopedItems(getActiveMarquee());
    }

    private List<String> buildContinuousItems(List<String> source, int minimumVisibleItems) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        int repeatCount = Math.max(2, (int) Math.ceil((double) minimumVisibleItems / source.size()));
        ArrayList<String> repeated = new ArrayList<>(source.size() * repeatCount);
        for (int i = 0; i < repeatCount; i++) {
            repeated.addAll(source);
        }
        return repeated;
    }

    public String marqueeAnimationClass(MarqueeConfig marquee) {
        if (marquee == null) {
            return "anim-horizontal-rtl";
        }
        if ("ltr".equals(marquee.getDirection())) {
            return "anim-horizontal-ltr";
        }
        if ("ttb".equals(marquee.getDirection())) {
            return "vertical anim-vertical-ttb";
        }
        if ("btt".equals(marquee.getDirection())) {
            return "vertical anim-vertical-btt";
        }
        return "anim-horizontal-rtl";
    }

    public String marqueeAnimationClass() {
        return marqueeAnimationClass(getActiveMarquee());
    }

    public String marqueeBackground(MarqueeConfig marquee) {
        if (marquee == null) {
            return "#0f1f49";
        }
        if ("gradient".equals(marquee.getBackgroundMode()) && marquee.getGradientColors() != null && !marquee.getGradientColors().isBlank()) {
            boolean vertical = "ttb".equals(marquee.getDirection()) || "btt".equals(marquee.getDirection());
            String angle = vertical ? "180deg" : "90deg";
            return "linear-gradient(" + angle + "," + marquee.getGradientColors() + ")";
        }
        return (marquee.getSolidColor() == null || marquee.getSolidColor().isBlank()) ? "#0f1f49" : marquee.getSolidColor();
    }

    public String marqueeBackground() {
        return marqueeBackground(getActiveMarquee());
    }

    public List<ContentPage> getContentPages() {
        return new ArrayList<>(contentPageService.getPages());
    }

    public String resolveMediaUrl(String source) {
        if (source == null || source.isBlank()) {
            return "https://via.placeholder.com/700x900?text=No+Image";
        }
        if (source.startsWith("dbfile:")) {
            return "/resources/files/" + source.substring("dbfile:".length());
        }
        return source;
    }

    public String formatPrice(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return "Rs. " + PRICE_FORMAT.format(value);
    }

    public boolean hasDiscount(Product product) {
        return product != null
                && product.getComparePrice() != null
                && product.getPrice() != null
                && product.getComparePrice().compareTo(product.getPrice()) > 0;
    }

    public String productPriceLabel(Product product) {
        if (product == null || product.getPrice() == null) {
            return "";
        }
        return product.isHasVariants() ? "From " + formatPrice(product.getPrice()) : formatPrice(product.getPrice());
    }

    public String productCtaLabel(Product product) {
        return product != null && product.isHasVariants() ? "Buy Now" : "Add To Cart";
    }
    
    public String defaultQuantityLabel(Product product) {
        return "1";
    }

    public String productCountLabel(int count) {
        return count == 1 ? "1 product" : count + " products";
    }

    public List<ProductVariantOption> getProductVariants(Product product) {
        List<ProductVariantOption> variants = new ArrayList<>();
        if (product == null || isBlank(product.getVariantData())) {
            return variants;
        }
        String[] lines = product.getVariantData().split("\\n");
        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (isBlank(line)) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            ProductVariantOption option = new ProductVariantOption();
            option.setIndex(index);
            option.setName(unescape(getSafe(parts, 0)));
            option.setImageUrl(resolveMediaUrl(unescape(getSafe(parts, 1))));
            option.setPrice(toDecimal(getSafe(parts, 2)));
            option.setComparePrice(toDecimal(getSafe(parts, 3)));
            option.setWeight(toDecimal(getSafe(parts, 4)));
            variants.add(option);
        }
        return variants;
    }

    public void openPurchaseDialog(Product product) {
        selectedProductId = product == null ? null : product.getId();
        selectedVariantIndex = 0;
    }
    
    public void addProductToCart(Product product) {
        if (product == null) {
            addWarn("Product is unavailable.");
            return;
        }
        addInfo(product.getName() + " added to cart. Qty: 1.");
    }

    public Product getSelectedProduct() {
        if (selectedProductId == null) {
            return null;
        }
        for (CatalogService.HomeRenderSection section : getOrderedHomeSections()) {
            for (Product product : section.getProducts()) {
                if (selectedProductId.equals(product.getId())) {
                    return product;
                }
            }
        }
        return null;
    }

    public List<ProductVariantOption> getSelectedProductVariants() {
        return getProductVariants(getSelectedProduct());
    }

    public ProductVariantOption getSelectedVariant() {
        List<ProductVariantOption> variants = getSelectedProductVariants();
        if (variants.isEmpty()) {
            return null;
        }
        int index = selectedVariantIndex == null ? 0 : selectedVariantIndex;
        if (index < 0 || index >= variants.size()) {
            return variants.get(0);
        }
        return variants.get(index);
    }

    public void setSelectedVariantIndex(Integer selectedVariantIndex) {
        this.selectedVariantIndex = selectedVariantIndex;
    }

    public void selectVariant(int variantIndex) {
        this.selectedVariantIndex = variantIndex;
    }

    public Integer getSelectedVariantIndex() {
        return selectedVariantIndex;
    }

    public void addSelectedVariantToCart() {
        Product product = getSelectedProduct();
        ProductVariantOption variant = getSelectedVariant();
        if (product == null || variant == null) {
            addWarn("Select a product variant first.");
            return;
        }
        addInfo(product.getName() + " - " + variant.getName() + " added to cart.");
    }

    public void buySelectedVariantNow() {
        Product product = getSelectedProduct();
        ProductVariantOption variant = getSelectedVariant();
        if (product == null || variant == null) {
            addWarn("Select a product variant first.");
            return;
        }
        addInfo("Buy it now ready for " + product.getName() + " - " + variant.getName() + ".");
    }

    private String getSafe(String[] values, int index) {
        return index < values.length ? values[index] : "";
    }

    private String unescape(String value) {
        return value == null ? "" : value.replace("\\p", "|").replace("\\n", "\n").replace("\\r", "\r");
    }

    private BigDecimal toDecimal(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static class ProductVariantOption {

        private int index;
        private String name;
        private String imageUrl;
        private BigDecimal price;
        private BigDecimal comparePrice;
        private BigDecimal weight;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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
    }
}
