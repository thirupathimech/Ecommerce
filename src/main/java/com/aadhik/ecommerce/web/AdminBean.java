package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeCollectionGroup;
import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSectionOrderItem;
import com.aadhik.ecommerce.model.HomeSectionType;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.SectionType;
import com.aadhik.ecommerce.model.VideoCarouselItem;
import com.aadhik.ecommerce.service.CatalogService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.primefaces.event.FileUploadEvent;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.model.DualListModel;

/**
 * @author THIRUPATHI G
 */
public abstract class AdminBean extends BaseBean implements Serializable {

    @Inject
    protected CatalogService catalogService;

    protected String activeMenu;

    protected String fileSelectionTarget;
    
    protected int fileSelectionVariantIndex;
    private static Object selectFileBean;
    private List<MediaFile> selectedMediaFiles;

    protected Map<String, String> homeSectionOrderOptionLabelMap;
    protected DualListModel<String> homeSectionOrderPickList;

    @PostConstruct
    public void init() {
        activeMenu = activeMenu == null ? "dashboard" : activeMenu;
        fileSelectionVariantIndex = -1;
        selectedMediaFiles = new ArrayList<>();
        resetForm();
    }

    public abstract void resetForm();

    public abstract void saveForm();

    public abstract boolean validateForm();

    public abstract void editForm(Object form);

    public abstract boolean deleteForm(Object form);

    public void selectFile(MediaFile file) {
        if(selectFileBean == null) return;
        if(selectFileBean instanceof ProductBean productBean){
            productBean.selectFile(file);
        } else if(selectFileBean instanceof HomeDivSectionBean homeDivSectionBean){
            homeDivSectionBean.selectFile(file);
        } else if(selectFileBean instanceof HomeSliderBean homeSliderBean){
            homeSliderBean.selectFile(file);
        } else if(selectFileBean instanceof VideoCarouselBean videoCarouselBean){
            videoCarouselBean.selectFile(file);
        } else if(selectFileBean instanceof ProductCollectionBean productCollectionBean){
            productCollectionBean.selectFile(file);
        }
        selectFileBean = null;
    }

    public void openFilePicker(Object bean, String fileTarget, int index) {
        fileSelectionVariantIndex = index;
        selectFileBean = bean;
        fileSelectionTarget = fileTarget;
        if ("PRODUCT".equalsIgnoreCase(fileSelectionTarget)) {
            selectedMediaFiles = new ArrayList<>();
        }
    }

    public void openFilePicker(Object bean, String fileTarget) {
        openFilePicker(bean, fileTarget, -1);
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

        return """
               {
                type:'pie',
                data:{
                    labels:[%s],
                    datasets:[{
                        label:'Storage Usage by File Type',
                        data:[%s],
                        backgroundColor:[%s]
                    }]
                },
                options:{
                    plugins:{
                        legend:{position:'right'},
                        title:{display:true,text:'Storage Usage by File Type'}
                    }
                }
                }""".formatted(labels, values, colors);
    }

    public long getTotalFileStorageBytes() {
        long total = 0L;
        for (MediaFile file : getMediaFiles()) {
            total += Math.max(0L, file.getFileSize());
        }
        return total;
    }

    public List<MediaFile> getMediaFiles() {
        return catalogService.getMediaFiles();
    }

    public List<HomeSlider> getSliders() {
        return catalogService.getHomeSliders();
    }

    public List<VideoCarouselItem> getVideoCarouselItems() {
        return catalogService.getVideoCarouselItems();
    }

    public List<HomeDivSection> getHomeDivSections() {
        return catalogService.getHomeDivSections();
    }

    public List<HomeCollectionGroup> getHomeCollectionGroups() {
        return catalogService.getHomeCollectionGroups();
    }

    public List<MarqueeConfig> getMarqueeConfigs() {
        return catalogService.getMarqueeConfigs();
    }

    public List<String> getImageSideOptions() {
        return Arrays.asList("LEFT", "RIGHT");
    }

    public List<String> getContentAlignOptions() {
        return Arrays.asList("LEFT", "CENTER", "RIGHT");
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

    public String resolveCollectionNameById(String idText) {
        if (isBlank(idText)) {
            return "Unknown";
        }
        try {
            Long id = Long.valueOf(idText);
            for (ProductCollection collection : getCollections()) {
                if (collection.getId() != null && collection.getId().equals(id)) {
                    return collection.getName();
                }
            }
        } catch (NumberFormatException ignored) {
        }
        return "Collection #" + idText;
    }

    public String resolveProductNameById(String idText) {
        if (isBlank(idText)) {
            return "Unknown";
        }
        try {
            Long id = Long.valueOf(idText);
            for (Product product : getProducts()) {
                if (product.getId() != null && product.getId().equals(id)) {
                    return product.getName();
                }
            }
        } catch (NumberFormatException ignored) {
        }
        return "Product #" + idText;
    }

    protected ProductCollection toCollectionReference(ProductCollection sourceCollection) {
        ProductCollection collection = new ProductCollection();
        if (sourceCollection != null) {
            collection.setId(sourceCollection.getId());
        }
        return collection;
    }

    protected List<ProductCollection> toCollectionReferences(List<ProductCollection> sourceCollections) {
        List<ProductCollection> references = new ArrayList<>();
        if (sourceCollections == null) {
            return references;
        }
        for (ProductCollection sourceCollection : sourceCollections) {
            if (sourceCollection == null || sourceCollection.getId() == null) {
                continue;
            }
            references.add(toCollectionReference(sourceCollection));
        }
        return references;
    }

    public String resolveCollectionNames(Product product) {
        if (product == null || product.getCollections() == null || product.getCollections().isEmpty()) {
            return "No Collection";
        }
        return product.getCollections().stream()
                .filter(collection -> collection != null && collection.getId() != null)
                .map(collection -> resolveCollectionNameById(String.valueOf(collection.getId())))
                .collect(Collectors.joining(", "));
    }

    public List<String> getProductNamesByCollection(ProductCollection collection) {
        if (collection == null || collection.getId() == null) {
            return List.of();
        }
        return getProducts().stream()
                .filter(product -> product.getCollections() != null && product.getCollections().stream().anyMatch(c -> c != null
                && c.getId() != null
                && c.getId().equals(collection.getId())))
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    public void loadHomeSectionOrderPickList() {
        homeSectionOrderOptionLabelMap = new LinkedHashMap<>();
        List<String> source = new ArrayList<>();

        for (HomeDivSection divSection : getHomeDivSections()) {
            addHomeSectionOrderOption(source, HomeSectionType.DIV_SECTION, divSection.getId(), "Div Section", divSection.getHeading());
        }
        for (VideoCarouselItem videoItem : getVideoCarouselItems()) {
            addHomeSectionOrderOption(source, HomeSectionType.VIDEO_CAROUSEL, videoItem.getId(), "Video Carousel", videoItem.getTitle());
        }
        for (HomeCollectionGroup group : getHomeCollectionGroups()) {
            addHomeSectionOrderOption(source, HomeSectionType.COLLECTION_GROUP, group.getId(), "Collections Group", group.getTitle());
        }
        for (ProductCollection collection : getCollections()) {
            addHomeSectionOrderOption(source, HomeSectionType.PRODUCTS_COLLECTION, collection.getId(), "Collection", collection.getName());
        }
        for (MarqueeConfig marqueeConfig : getMarqueeConfigs()) {
            addHomeSectionOrderOption(source, HomeSectionType.MARQUEE, marqueeConfig.getId(), "Marquee", buildMarqueeLabel(marqueeConfig));
        }

        List<String> target = new ArrayList<>();
        for (HomeSectionOrderItem orderItem : catalogService.getHomeSectionOrderItems()) {
            String key = toHomeSectionOrderKey(orderItem.getSectionType(), orderItem.getRecordId());
            if (homeSectionOrderOptionLabelMap.containsKey(key) && !target.contains(key)) {
                target.add(key);
            }
        }
        source.removeAll(target);
        homeSectionOrderPickList = new DualListModel<>(source, target);
    }

    private void addHomeSectionOrderOption(List<String> source, HomeSectionType sectionType, Long id, String sectionLabel, String recordLabel) {
        if (id == null) {
            return;
        }
        String key = toHomeSectionOrderKey(sectionType, id);
        String display = sectionLabel + " - " + (isBlank(recordLabel) ? ("Record #" + id) : recordLabel);
        homeSectionOrderOptionLabelMap.put(key, display);
        source.add(key);
    }

    private String toHomeSectionOrderKey(HomeSectionType sectionType, Long id) {
        return sectionType.name() + ":" + id;
    }

    private String buildMarqueeLabel(MarqueeConfig marqueeConfig) {
        if (marqueeConfig == null || isBlank(marqueeConfig.getItemsData())) {
            return "Marquee #" + (marqueeConfig == null ? "" : marqueeConfig.getId());
        }
        return marqueeConfig.getItemsData().lines().map(String::trim).filter(text -> !text.isBlank()).findFirst().orElse("Marquee #" + marqueeConfig.getId());
    }

    public List<MediaFile> getSelectedMediaFiles() {
        return selectedMediaFiles;
    }

    public void setSelectedMediaFiles(List<MediaFile> selectedMediaFiles) {
        this.selectedMediaFiles = selectedMediaFiles;
    }

}
