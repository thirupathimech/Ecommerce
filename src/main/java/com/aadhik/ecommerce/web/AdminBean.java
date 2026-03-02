package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.MediaFile;
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
import org.primefaces.event.FileUploadEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private String fileSelectionTarget;
    private int fileSelectionVariantIndex;
    private List<MediaFile> selectedMediaFiles;

    @PostConstruct
    public void init() {
        activeMenu = "products";
        productEditorVisible = false;
        variantInputs = new ArrayList<>();
        fileSelectionTarget = "primary";
        fileSelectionVariantIndex = -1;
        selectedMediaFiles = new ArrayList<>();
        resetSliderForm();
        resetSectionForm();
        resetCollectionForm();
        resetProductForm();
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

    public void openFilePickerForPrimary() {
        fileSelectionTarget = "primary";
        fileSelectionVariantIndex = -1;
        selectedMediaFiles = new ArrayList<>();
    }

    public void openFilePickerForGallery() {
        fileSelectionTarget = "gallery";
        fileSelectionVariantIndex = -1;
    }

    public void openFilePickerForVariant(int index) {
        fileSelectionTarget = "variant";
        fileSelectionVariantIndex = index;
    }

    public void openFilePickerForSlider() {
        fileSelectionTarget = "slider";
        fileSelectionVariantIndex = -1;
    }

    public void openFilePickerForCollectionBanner() {
        fileSelectionTarget = "collection-banner";
        fileSelectionVariantIndex = -1;
    }

    public void selectFile(MediaFile file) {
        String ref = toDbFileRef(file.getId());
        if ("primary".equals(fileSelectionTarget)) {
            productForm.setImageUrl(ref);
        } else if ("gallery".equals(fileSelectionTarget)) {
            if (isBlank(productForm.getGalleryImages())) {
                productForm.setGalleryImages(ref);
            } else if (!productForm.getGalleryImages().contains(ref)) {
                productForm.setGalleryImages(productForm.getGalleryImages() + "," + ref);
            }
        } else if ("slider".equals(fileSelectionTarget)) {
            sliderForm.setImageUrl(ref);
        } else if ("collection-banner".equals(fileSelectionTarget)) {
            collectionForm.setBannerImage(ref);
        } else if ("variant".equals(fileSelectionTarget)
                && fileSelectionVariantIndex >= 0
                && fileSelectionVariantIndex < variantInputs.size()) {
            variantInputs.get(fileSelectionVariantIndex).setImageUrl(ref);
        }
        addInfo("File selected");
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            String contentType = event.getFile().getContentType() == null ? "application/octet-stream" : event.getFile().getContentType();
            if (!isAllowedType(contentType)) {
                addError("Only images, videos and PDFs are allowed.");
                return;
            }

            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileName(event.getFile().getFileName());
            mediaFile.setContentType(contentType);
            mediaFile.setFileType(resolveFileType(contentType));
            mediaFile.setFileSize(event.getFile().getSize());
            mediaFile.setData(event.getFile().getContent());
            mediaFile.setUploadedAt(LocalDateTime.now());
            catalogService.saveMediaFile(mediaFile);
            addInfo("File uploaded successfully");
        } catch (Exception exception) {
            addError("Upload failed: " + exception.getMessage());
        }
    }

    private boolean isAllowedType(String contentType) {
        return contentType.startsWith("image/")
                || contentType.startsWith("video/")
                || "application/pdf".equals(contentType);
    }

    private String resolveFileType(String contentType) {
        if (contentType.startsWith("image/")) {
            return "IMAGE";
        }
        if (contentType.startsWith("video/")) {
            return "VIDEO";
        }
        if ("application/pdf".equals(contentType)) {
            return "PDF";
        }
        return "OTHER";
    }

    public String toDbFileRef(Long fileId) {
        return "dbfile:" + fileId;
    }

    public String resolveMediaUrl(String source) {
        if (isBlank(source)) {
            return "https://via.placeholder.com/62x62?text=NA";
        }
        if (source.startsWith("dbfile:")) {
            return "/resources/files/" + source.substring("dbfile:".length());
        }
        return source;
    }

    public String previewLabel(String source) {
        if (isBlank(source)) {
            return "Not selected";
        }
        if (source.startsWith("dbfile:")) {
            return "Selected from Files: " + source;
        }
        return source;
    }

    public String fileThumbnail(MediaFile file) {
        if ("IMAGE".equals(file.getFileType())) {
            return "/resources/files/" + file.getId();
        }
        if ("VIDEO".equals(file.getFileType())) {
            return "https://via.placeholder.com/62x62?text=VIDEO";
        }
        if ("PDF".equals(file.getFileType())) {
            return "https://via.placeholder.com/62x62?text=PDF";
        }
        return "https://via.placeholder.com/62x62?text=FILE";
    }

    public String humanSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.1f KB", kb);
        }
        return String.format("%.2f MB", kb / 1024.0);
    }

    public long fileUsageCount(Long fileId) {
        return catalogService.getFileUsageCount(fileId);
    }

    public void deleteFile(MediaFile file) {
        if (file == null || file.getId() == null) {
            addError("Invalid file selection.");
            return;
        }

        long usageCount = fileUsageCount(file.getId());
        if (usageCount > 0) {
            addWarn("Cannot delete file. It is currently used in " + usageCount + " place(s).");
            return;
        }

        catalogService.deleteMediaFile(file.getId());
        selectedMediaFiles.removeIf(selected -> selected.getId() != null && selected.getId().equals(file.getId()));
        addInfo("File deleted successfully");
    }

    public void deleteSelectedFiles() {
        if (selectedMediaFiles == null || selectedMediaFiles.isEmpty()) {
            addWarn("Please select at least one file to delete.");
            return;
        }

        int deleted = 0;
        int skipped = 0;
        for (MediaFile file : new ArrayList<>(selectedMediaFiles)) {
            if (file == null || file.getId() == null) {
                continue;
            }
            if (fileUsageCount(file.getId()) > 0) {
                skipped++;
                continue;
            }
            catalogService.deleteMediaFile(file.getId());
            deleted++;
        }

        selectedMediaFiles.clear();
        if (deleted > 0) {
            addInfo(deleted + " file(s) deleted successfully.");
        }
        if (skipped > 0) {
            addWarn(skipped + " file(s) skipped because they are in use.");
        }
    }

    public String getFileTypeStorageChartModel() {

        Map<String, Long> storageByType = new LinkedHashMap<>();
        for (MediaFile file : getMediaFiles()) {
            String key = isBlank(file.getFileType()) ? "OTHER" : file.getFileType();
            storageByType.put(key,
                    storageByType.getOrDefault(key, 0L) + Math.max(0L, file.getFileSize()));
        }

        StringBuilder labels = new StringBuilder();
        StringBuilder values = new StringBuilder();
        StringBuilder colors = new StringBuilder();

        String[] bgColors = {
            "'#4e79a7'", "'#f28e2b'", "'#e15759'",
            "'#76b7b2'", "'#59a14f'", "'#edc949'"
        };

        int colorIndex = 0;
        for (Map.Entry<String, Long> entry : storageByType.entrySet()) {

            labels.append("'")
                    .append(entry.getKey())
                    .append(" (")
                    .append(humanSize(entry.getValue()))
                    .append(")'")
                    .append(",");

            values.append(entry.getValue()).append(",");
            colors.append(bgColors[colorIndex % bgColors.length]).append(",");

            colorIndex++;
        }

        // Remove last comma safely
        if (labels.length() > 0) {
            labels.setLength(labels.length() - 1);
        }
        if (values.length() > 0) {
            values.setLength(values.length() - 1);
        }
        if (colors.length() > 0) {
            colors.setLength(colors.length() - 1);
        }

        return "{"
                + "type:'pie',"
                + "data:{"
                + "labels:[" + labels + "],"
                + "datasets:[{"
                + "label:'Storage Usage by File Type',"
                + "data:[" + values + "],"
                + "backgroundColor:[" + colors + "]"
                + "}]"
                + "},"
                + "options:{"
                + "plugins:{"
                + "legend:{position:'right'},"
                + "title:{display:true,text:'Storage Usage by File Type'}"
                + "}"
                + "}"
                + "}";
    }

    public long getTotalFileStorageBytes() {
        long total = 0L;
        for (MediaFile file : getMediaFiles()) {
            total += Math.max(0L, file.getFileSize());
        }
        return total;
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
        try {
            catalogService.saveProduct(productForm);
            resetProductForm();
            productEditorVisible = false;
            addInfo("Product saved successfully");
        } catch (Exception ex) {
            addError("Product update failed.");
        }
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

    public List<MediaFile> getMediaFiles() {
        return catalogService.getMediaFiles();
    }

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeSliders();
    }
    
    public List<CatalogService.HomepageSectionView> getSectionViews() {
        return catalogService.getHomepageSectionsWithProducts();
    }

    public List<ProductCollection> getCollections() {
        return catalogService.getCollections();
    }

    public List<Product> getProducts() {
        return catalogService.getProducts();
    }

    public List<SectionType> getSectionTypes() {
        return Arrays.asList(SectionType.values());
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
            addError("Slider image selection is required.");
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
        
        if(catalogService.isExistSKU(productForm.getSku(), productForm.getId())){
            addError("Duplicate SKU cannot be allowed");
            return false;
        }

        if (isBlank(productForm.getImageUrl())) {
            addError("Primary product image selection is required.");
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
                addError("Variant image selection is required.");
                return false;
            }
            if (variant.getPrice() == null || variant.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                addError("Variant price must be greater than 0.");
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

    private void addInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    }

    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        FacesContext.getCurrentInstance().validationFailed();
    }

    private void addWarn(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
    }

    public String getActiveMenu() {
        return activeMenu;
    }

    public boolean isProductEditorVisible() {
        return productEditorVisible;
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

    public List<ProductVariantInput> getVariantInputs() {
        return variantInputs;
    }

    public List<MediaFile> getSelectedMediaFiles() {
        return selectedMediaFiles;
    }

    public void setSelectedMediaFiles(List<MediaFile> selectedMediaFiles) {
        this.selectedMediaFiles = selectedMediaFiles;
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
