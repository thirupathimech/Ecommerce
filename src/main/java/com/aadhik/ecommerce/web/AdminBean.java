package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.SectionType;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class AdminBean implements Serializable {

    @Inject
    private CatalogService catalogService;

    private String activeMenu;
    private boolean productEditorVisible;

    private HomeSlider sliderForm;
    private HomepageSection sectionForm;
    private ProductCollection collectionForm;
    private Product productForm;
    private List<ProductVariantInput> variantInputs;

    @PostConstruct
    public void init() {
        activeMenu = "products";
        productEditorVisible = false;
        variantInputs = new ArrayList<>();
        resetSliderForm();
        resetSectionForm();
        resetCollectionForm();
        resetProductForm();
    }

    public void saveSlider() {
        if (!validateSlider()) {
            return;
        }
        catalogService.saveSlider(sliderForm);
        resetSliderForm();
        addInfo("Slider saved successfully");
    }

    public void saveSection() {
        if (!validateSection()) {
            return;
        }
        catalogService.saveSection(sectionForm);
        resetSectionForm();
        addInfo("Homepage section saved successfully");
    }

    public void saveCollection() {
        if (!validateCollection()) {
            return;
        }
        catalogService.saveCollection(collectionForm);
        resetCollectionForm();
        addInfo("Collection saved successfully");
    }

    public void saveProduct() {
        if (!validateProduct()) {
            return;
        }

        if (productForm.isHasVariants()) {
            productForm.setVariantData(serializeVariants(variantInputs));
        } else {
            productForm.setVariantData(null);
        }
        catalogService.saveProduct(productForm);
        resetProductForm();
        productEditorVisible = false;
        addInfo("Product saved successfully");
    }

    public void editSlider(HomeSlider slider) {
        HomeSlider draft = new HomeSlider();
        draft.setId(slider.getId());
        draft.setTitle(slider.getTitle());
        draft.setSubtitle(slider.getSubtitle());
        draft.setImageUrl(slider.getImageUrl());
        draft.setSortOrder(slider.getSortOrder());
        draft.setActive(slider.isActive());
        sliderForm = draft;
    }

    public void editSection(HomepageSection section) {
        HomepageSection draft = new HomepageSection();
        draft.setId(section.getId());
        draft.setSectionTitle(section.getSectionTitle());
        draft.setSectionType(section.getSectionType());
        draft.setMaxItems(section.getMaxItems());
        draft.setSortOrder(section.getSortOrder());
        draft.setActive(section.isActive());
        draft.setCollection(toCollectionReference(section.getCollection()));
        sectionForm = draft;
    }

    public void editCollection(ProductCollection collection) {
        ProductCollection draft = new ProductCollection();
        draft.setId(collection.getId());
        draft.setName(collection.getName());
        draft.setSlug(collection.getSlug());
        draft.setBannerImage(collection.getBannerImage());
        draft.setDescription(collection.getDescription());
        draft.setActive(collection.isActive());
        collectionForm = draft;
    }

    public void editProduct(Product product) {
        Product draft = new Product();
        draft.setId(product.getId());
        draft.setName(product.getName());
        draft.setDescription(product.getDescription());
        draft.setSku(product.getSku());
        draft.setHsn(product.getHsn());
        draft.setImageUrl(product.getImageUrl());
        draft.setGalleryImages(product.getGalleryImages());
        draft.setPrice(product.getPrice());
        draft.setComparePrice(product.getComparePrice());
        draft.setWeight(product.getWeight());
        draft.setHasVariants(product.isHasVariants());
        draft.setVariantData(product.getVariantData());
        draft.setFeatured(product.isFeatured());
        draft.setActive(product.isActive());
        draft.setCollection(toCollectionReference(product.getCollection()));
        productForm = draft;

        variantInputs = deserializeVariants(product.getVariantData());
        if (variantInputs.isEmpty()) {
            variantInputs.add(new ProductVariantInput());
        }

        activeMenu = "products";
        productEditorVisible = true;
    }

    public void setActiveMenu(String activeMenu) {
        this.activeMenu = activeMenu;
        if (!"products".equals(activeMenu)) {
            productEditorVisible = false;
        }
    }

    public void openNewProductForm() {
        activeMenu = "products";
        productEditorVisible = true;
        resetProductForm();
    }

    public void cancelProductEditor() {
        resetProductForm();
        productEditorVisible = false;
    }

    public void addVariantRow() {
        variantInputs.add(new ProductVariantInput());
    }

    public void removeVariantRow(int index) {
        if (index >= 0 && index < variantInputs.size()) {
            variantInputs.remove(index);
        }
        if (variantInputs.isEmpty()) {
            variantInputs.add(new ProductVariantInput());
        }
    }

    public void resetSliderForm() {
        sliderForm = new HomeSlider();
        sliderForm.setActive(true);
        sliderForm.setSortOrder(1);
    }

    public void resetSectionForm() {
        sectionForm = new HomepageSection();
        sectionForm.setCollection(new ProductCollection());
        sectionForm.setActive(true);
        sectionForm.setMaxItems(4);
        sectionForm.setSortOrder(1);
    }

    public void resetCollectionForm() {
        collectionForm = new ProductCollection();
        collectionForm.setActive(true);
    }

    public void resetProductForm() {
        productForm = new Product();
        productForm.setCollection(new ProductCollection());
        productForm.setActive(true);
        productForm.setHasVariants(false);
        variantInputs = new ArrayList<>();
        variantInputs.add(new ProductVariantInput());
    }

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeSliders();
    }

    public List<CatalogService.HomepageSectionView> getSectionViews() {
        return catalogService.getHomepageSectionsWithProducts();
    }

    public List<ProductCollection> getCollections() {
        return catalogService.getActiveCollections();
    }

    public List<Product> getProducts() {
        return catalogService.getProducts();
    }

    public List<SectionType> getSectionTypes() {
        return Arrays.asList(SectionType.values());
    }

    private void addInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
    }

    public HomeSlider getSliderForm() {
        return sliderForm;
    }

    public void setSliderForm(HomeSlider sliderForm) {
        this.sliderForm = sliderForm;
    }

    public HomepageSection getSectionForm() {
        return sectionForm;
    }

    public void setSectionForm(HomepageSection sectionForm) {
        this.sectionForm = sectionForm;
    }

    public ProductCollection getCollectionForm() {
        return collectionForm;
    }

    public void setCollectionForm(ProductCollection collectionForm) {
        this.collectionForm = collectionForm;
    }

    public Product getProductForm() {
        return productForm;
    }

    public void setProductForm(Product productForm) {
        this.productForm = productForm;
    }

    private ProductCollection toCollectionReference(ProductCollection sourceCollection) {
        ProductCollection collection = new ProductCollection();
        if (sourceCollection != null) {
            collection.setId(sourceCollection.getId());
        }
        return collection;
    }

    private boolean validateSlider() {
        if (isBlank(sliderForm.getTitle())) {
            addError("Slider title is required.");
            return false;
        }

        if (isBlank(sliderForm.getImageUrl())) {
            addError("Slider image URL is required.");
            return false;
        }

        if (sliderForm.getSortOrder() <= 0) {
            addError("Slider sort order must be greater than 0.");
            return false;
        }

        return true;
    }

    private boolean validateSection() {
        if (isBlank(sectionForm.getSectionTitle())) {
            addError("Section title is required.");
            return false;
        }

        if (sectionForm.getSectionType() == null) {
            addError("Section type is required.");
            return false;
        }

        if (sectionForm.getMaxItems() <= 0) {
            addError("Max items must be greater than 0.");
            return false;
        }

        if (sectionForm.getSortOrder() <= 0) {
            addError("Section sort order must be greater than 0.");
            return false;
        }

        return true;
    }

    private boolean validateCollection() {
        if (isBlank(collectionForm.getName())) {
            addError("Collection name is required.");
            return false;
        }

        if (isBlank(collectionForm.getSlug())) {
            addError("Collection slug is required.");
            return false;
        }

        return true;
    }

    private boolean validateProduct() {
        if (isBlank(productForm.getName())) {
            addError("Product title is required.");
            return false;
        }

        if (isBlank(productForm.getDescription())) {
            addError("Product description is required.");
            return false;
        }

        if (isBlank(productForm.getSku())) {
            addError("SKU is required.");
            return false;
        }

        if (isBlank(productForm.getHsn())) {
            addError("HSN is required.");
            return false;
        }

        if (isBlank(productForm.getImageUrl())) {
            addError("Primary product image is required.");
            return false;
        }

        if (productForm.getPrice() == null || productForm.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            addError("Product price must be greater than 0.");
            return false;
        }

        if (productForm.getComparePrice() != null
                && productForm.getComparePrice().compareTo(productForm.getPrice()) < 0) {
            addError("Compare price must be greater than or equal to product price.");
            return false;
        }

        if (!productForm.isHasVariants()) {
            if (productForm.getWeight() == null || productForm.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
                addError("Weight is required for non-variant physical product.");
                return false;
            }
            return true;
        }

        if (variantInputs.isEmpty()) {
            addError("At least one variant is required.");
            return false;
        }

        for (ProductVariantInput variant : variantInputs) {
            if (isBlank(variant.getColor())) {
                addError("Variant color is required.");
                return false;
            }
            if (isBlank(variant.getImageUrl())) {
                addError("Variant image is required.");
                return false;
            }
            if (variant.getPrice() == null || variant.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                addError("Variant price must be greater than 0.");
                return false;
            }
            if (variant.getComparePrice() != null && variant.getComparePrice().compareTo(variant.getPrice()) < 0) {
                addError("Variant compare price must be greater than or equal to variant price.");
                return false;
            }
        }

        return true;
    }

    private String serializeVariants(List<ProductVariantInput> variants) {
        StringBuilder builder = new StringBuilder();
        for (ProductVariantInput variant : variants) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(escape(variant.getColor())).append("|")
                    .append(escape(variant.getImageUrl())).append("|")
                    .append(variant.getPrice() == null ? "" : variant.getPrice()).append("|")
                    .append(variant.getComparePrice() == null ? "" : variant.getComparePrice()).append("|")
                    .append(variant.getWeight() == null ? "" : variant.getWeight());
        }
        return builder.toString();
    }

    private List<ProductVariantInput> deserializeVariants(String variantData) {
        List<ProductVariantInput> variants = new ArrayList<>();
        if (isBlank(variantData)) {
            return variants;
        }

        String[] lines = variantData.split("\\n");
        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            ProductVariantInput variant = new ProductVariantInput();
            variant.setColor(unescape(getSafe(parts, 0)));
            variant.setImageUrl(unescape(getSafe(parts, 1)));
            variant.setPrice(toDecimal(getSafe(parts, 2)));
            variant.setComparePrice(toDecimal(getSafe(parts, 3)));
            variant.setWeight(toDecimal(getSafe(parts, 4)));
            variants.add(variant);
        }

        return variants;
    }

    private BigDecimal toDecimal(String text) {
        if (isBlank(text)) {
            return null;
        }
        return new BigDecimal(text);
    }

    private String getSafe(String[] values, int index) {
        if (index < values.length) {
            return values[index];
        }
        return "";
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("|", "%7C").replace("\n", "%0A");
    }

    private String unescape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("%7C", "|").replace("%0A", "\n");
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        FacesContext.getCurrentInstance().validationFailed();
    }

    public String getActiveMenu() {
        return activeMenu;
    }

    public boolean isProductEditorVisible() {
        return productEditorVisible;
    }

    public List<ProductVariantInput> getVariantInputs() {
        return variantInputs;
    }

    public static class ProductVariantInput implements Serializable {

        private String color;
        private String imageUrl;
        private BigDecimal price;
        private BigDecimal comparePrice;
        private BigDecimal weight;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
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
