package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.HomeSectionOrderItem;
import com.aadhik.ecommerce.model.HomeSectionType;
import com.aadhik.ecommerce.model.HomeSlider;
import com.aadhik.ecommerce.model.HomepageSection;
import com.aadhik.ecommerce.model.MarqueeConfig;
import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.Product;
import com.aadhik.ecommerce.model.ProductCollection;
import com.aadhik.ecommerce.model.SectionType;
import com.aadhik.ecommerce.model.VideoCarouselItem;
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
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class AdminBean implements Serializable {

    @Inject
    private CatalogService catalogService;

    private String activeMenu;
    private boolean productEditorVisible;
    private int homeTabIndex;

    private HomeSlider sliderForm;
    private HomeDivSection homeDivSectionForm;
    private VideoCarouselItem videoCarouselForm;
    private HomepageSection sectionForm;
    private ProductCollection collectionForm;
    private Product productForm;
    private MarqueeConfig marqueeForm;
    private String marqueeItemInput;
    private int gradientColorCount;
    private String gradientColor1;
    private String gradientColor2;
    private String gradientColor3;
    private String gradientColor4;
    private String gradientColor5;
    private List<ProductVariantInput> variantInputs;

    private String fileSelectionTarget;
    private int fileSelectionVariantIndex;
    private List<MediaFile> selectedMediaFiles;
    private DualListModel<String> homeSectionOrderPickList;
    private Map<String, String> homeSectionOrderOptionLabelMap;

    @PostConstruct
    public void init() {
        activeMenu = "products";
        productEditorVisible = false;
        homeTabIndex = 0;
        variantInputs = new ArrayList<>();
        fileSelectionTarget = "primary";
        fileSelectionVariantIndex = -1;
        selectedMediaFiles = new ArrayList<>();
        marqueeForm = new MarqueeConfig();
        resetSliderForm();
        resetVideoCarouselForm();
        resetHomeDivSectionForm();
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

    public void onHomeTabChange(TabChangeEvent event) {
        if (event != null && event.getTab() != null) {
            homeTabIndex = event.getIndex();
        }
    }

    public void loadHomeSectionOrderPickList() {
        homeSectionOrderOptionLabelMap = new LinkedHashMap<>();
        List<String> source = new ArrayList<>();

        for (HomeSlider slider : getSliders()) {
            addHomeSectionOrderOption(source, HomeSectionType.HOME_SLIDER, slider.getId(), "Home Slider", slider.getTitle());
        }
        for (HomeDivSection divSection : getHomeDivSections()) {
            addHomeSectionOrderOption(source, HomeSectionType.DIV_SECTION, divSection.getId(), "Div Section", divSection.getHeading());
        }
        for (VideoCarouselItem videoItem : getVideoCarouselItems()) {
            addHomeSectionOrderOption(source, HomeSectionType.VIDEO_CAROUSEL, videoItem.getId(), "Video Carousel", videoItem.getTitle());
        }
        for (HomepageSection section : catalogService.getHomepageSections()) {
            addHomeSectionOrderOption(source, HomeSectionType.COLLECTION_SECTION, section.getId(), "Collections Maker", section.getSectionTitle());
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

    private String buildMarqueeLabel(MarqueeConfig marqueeConfig) {
        if (marqueeConfig == null || isBlank(marqueeConfig.getItemsData())) {
            return "Marquee #" + (marqueeConfig == null ? "" : marqueeConfig.getId());
        }
        return marqueeConfig.getItemsData().lines().map(String::trim).filter(text -> !text.isBlank()).findFirst().orElse("Marquee #" + marqueeConfig.getId());
    }

    private String toHomeSectionOrderKey(HomeSectionType sectionType, Long id) {
        return sectionType.name() + ":" + id;
    }

    public String resolveHomeSectionOrderLabel(String key) {
        if (homeSectionOrderOptionLabelMap == null || homeSectionOrderOptionLabelMap.isEmpty()) {
            loadHomeSectionOrderPickList();
        }
        return homeSectionOrderOptionLabelMap.getOrDefault(key, key);
    }

    public void saveHomeSectionOrder() {
        if (homeSectionOrderPickList == null) {
            loadHomeSectionOrderPickList();
            return;
        }

        List<HomeSectionOrderItem> items = new ArrayList<>();
        int sortOrder = 1;
        for (String key : homeSectionOrderPickList.getTarget()) {
            String[] values = key.split(":");
            if (values.length != 2) {
                continue;
            }
            try {
                HomeSectionType type = HomeSectionType.valueOf(values[0]);
                Long recordId = Long.parseLong(values[1]);
                HomeSectionOrderItem item = new HomeSectionOrderItem();
                item.setSectionType(type);
                item.setRecordId(recordId);
                item.setSortOrder(sortOrder++);
                items.add(item);
            } catch (IllegalArgumentException ignored) {
            }
        }

        catalogService.saveHomeSectionOrderItems(items);
        addInfo("Home section order saved successfully");
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

    public void openFilePickerForVideoCarouselThumbnail() {
        fileSelectionTarget = "video-carousel-thumbnail";
        fileSelectionVariantIndex = -1;
    }

    public void openFilePickerForVideoCarouselVideo() {
        fileSelectionTarget = "video-carousel-video";
        fileSelectionVariantIndex = -1;
    }

    public void openFilePickerForHomeDivSectionImage() {
        fileSelectionTarget = "home-div-section-image";
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
        } else if ("video-carousel-thumbnail".equals(fileSelectionTarget)) {
            if (!"IMAGE".equals(file.getFileType())) {
                addWarn("Please select an image file for thumbnail.");
                return;
            }
            videoCarouselForm.setThumbnailUrl(ref);
        } else if ("video-carousel-video".equals(fileSelectionTarget)) {
            if (!"VIDEO".equals(file.getFileType())) {
                addWarn("Please select a video file.");
                return;
            }
            videoCarouselForm.setVideoUrl(ref);
        } else if ("home-div-section-image".equals(fileSelectionTarget)) {
            if (!"IMAGE".equals(file.getFileType())) {
                addWarn("Please select an image file.");
                return;
            }
            homeDivSectionForm.setImageUrl(ref);
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

    public void saveVideoCarouselItem() {
        if (!validateVideoCarouselItem()) {
            return;
        }

        catalogService.saveVideoCarouselItem(videoCarouselForm);
        resetVideoCarouselForm();
        addInfo("Video carousel item saved successfully");
    }

    public void saveHomeDivSection() {
        if (!validateHomeDivSection()) {
            return;
        }

        catalogService.saveHomeDivSection(homeDivSectionForm);
        resetHomeDivSectionForm();
        addInfo("Div section saved successfully");
    }

    public void editHomeDivSection(HomeDivSection section) {
        HomeDivSection draft = new HomeDivSection();
        draft.setId(section.getId());
        draft.setHeading(section.getHeading());
        draft.setDescription(section.getDescription());
        draft.setButtonLabel(section.getButtonLabel());
        draft.setButtonLink(section.getButtonLink());
        draft.setImageUrl(section.getImageUrl());
        draft.setImageSide(section.getImageSide());
        draft.setContentAlign(section.getContentAlign());
        draft.setSortOrder(section.getSortOrder());
        draft.setActive(section.isActive());
        homeDivSectionForm = draft;
    }

    public void deleteHomeDivSection(HomeDivSection section) {
        if (section == null || section.getId() == null) {
            addError("Invalid div section selection.");
            return;
        }

        catalogService.deleteHomeDivSection(section.getId());
        if (homeDivSectionForm != null && section.getId().equals(homeDivSectionForm.getId())) {
            resetHomeDivSectionForm();
        }
        addInfo("Div section deleted successfully");
    }

    public void editVideoCarouselItem(VideoCarouselItem item) {
        VideoCarouselItem draft = new VideoCarouselItem();
        draft.setId(item.getId());
        draft.setTitle(item.getTitle());
        draft.setThumbnailUrl(item.getThumbnailUrl());
        draft.setVideoUrl(item.getVideoUrl());
        draft.setSortOrder(item.getSortOrder());
        draft.setActive(item.isActive());
        videoCarouselForm = draft;
    }

    public void deleteVideoCarouselItem(VideoCarouselItem item) {
        if (item == null || item.getId() == null) {
            addError("Invalid video carousel selection.");
            return;
        }

        catalogService.deleteVideoCarouselItem(item.getId());
        if (videoCarouselForm != null && item.getId().equals(videoCarouselForm.getId())) {
            resetVideoCarouselForm();
        }
        addInfo("Video carousel item deleted successfully");
    }

    public void resetHomeDivSectionForm() {
        homeDivSectionForm = new HomeDivSection();
        homeDivSectionForm.setHeading("From mother's heart to baby's first smile 😇");
        homeDivSectionForm.setDescription("The Mothers Care brings you 100% pure, organic baby essentials crafted with love and nature's finest ingredients");
        homeDivSectionForm.setButtonLabel("Visit");
        homeDivSectionForm.setButtonLink("#");
        homeDivSectionForm.setImageSide("LEFT");
        homeDivSectionForm.setContentAlign("CENTER");
        homeDivSectionForm.setSortOrder(1);
        homeDivSectionForm.setActive(true);
    }

    public void saveSection() {
        if (!validateSection()) {
            return;
        }
        catalogService.saveSection(sectionForm);
        resetSectionForm();
        addInfo("Homepage section saved successfully");
    }

    public void saveMarquee() {
        if (!validateMarquee()) {
            return;
        }
        marqueeForm.setGradientColors(String.join(",", getGradientColors()));
        marqueeForm.setItemsData(String.join("\n", getMarqueeItems()));
        catalogService.saveMarqueeConfig(marqueeForm);
        resetMarqueeForm();
        addInfo("Marquee saved successfully");
    }

    public void editMarquee(MarqueeConfig config) {
        MarqueeConfig draft = new MarqueeConfig();
        draft.setId(config.getId());
        draft.setItemsData(config.getItemsData());
        draft.setDirection(config.getDirection());
        draft.setSpeedSeconds(config.getSpeedSeconds());
        draft.setBackgroundMode(config.getBackgroundMode());
        draft.setSolidColor(config.getSolidColor());
        draft.setGradientColors(config.getGradientColors());
        draft.setFontSizePx(config.getFontSizePx());
        draft.setFontWeight(config.getFontWeight());
        draft.setTextColor(config.getTextColor());
        draft.setPauseOnHover(config.isPauseOnHover());
        draft.setActive(config.isActive());
        marqueeForm = draft;
        loadGradientColorsFromString(config.getGradientColors());
    }

    public void deleteMarqueeConfig(MarqueeConfig config) {
        catalogService.deleteMarqueeConfig(config.getId());
        addInfo("Marquee deleted successfully.");
    }

    public void resetMarqueeForm() {
        marqueeForm = new MarqueeConfig();
        marqueeForm.setItemsData("Free shipping above $50\nFlash sale live now");
        marqueeForm.setDirection("rtl");
        marqueeForm.setSpeedSeconds(12);
        marqueeForm.setBackgroundMode("solid");
        marqueeForm.setSolidColor("#0f1f49");
        marqueeForm.setGradientColors("#0f1f49,#325ac7");
        gradientColor1 = "#0f1f49";
        gradientColor2 = "#325ac7";
        gradientColor3 = "#5f85ee";
        gradientColor4 = "#7f9bff";
        gradientColor5 = "#b7c8ff";
        marqueeForm.setFontSizePx(22);
        marqueeForm.setFontWeight("700");
        marqueeForm.setTextColor("#ffffff");
        marqueeForm.setPauseOnHover(true);
        marqueeForm.setActive(true);
        marqueeItemInput = "";
        gradientColorCount = 2;
    }

    public void addMarqueeItem() {
        if (isBlank(marqueeItemInput)) {
            addWarn("Please enter marquee content.");
            return;
        }
        List<String> items = getMarqueeItems();
        items.add(marqueeItemInput.trim());
        marqueeForm.setItemsData(String.join("\n", items));
        marqueeItemInput = "";
    }

    public void removeMarqueeItem(int index) {
        List<String> items = getMarqueeItems();
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            marqueeForm.setItemsData(String.join("\n", items));
        } else {
            marqueeForm.setItemsData(String.join("\n", items));
        }
    }

    public void applyGradientColorCount() {
        if (gradientColorCount < 2) {
            gradientColorCount = 2;
        }
        if (gradientColorCount > 5) {
            gradientColorCount = 5;
        }
        marqueeForm.setGradientColors(String.join(",", getGradientColors()));
    }

    public List<String> getMarqueeItems() {
        List<String> items = new ArrayList<>();
        if (marqueeForm == null) {
            marqueeForm = new MarqueeConfig();
        }
        if (isBlank(marqueeForm.getItemsData())) {
            return items;
        }
        String[] lines = marqueeForm.getItemsData().split("\\n");
        for (String line : lines) {
            if (!isBlank(line)) {
                items.add(line.trim());
            }
        }
        return items;
    }

    public List<String> getLoopedMarqueeItems() {
        List<String> items = getMarqueeItems();
        if (items.isEmpty()) {
            items.add("Add marquee content");
        }
        return buildContinuousItems(items, 18);
    }

    private List<String> buildContinuousItems(List<String> source, int minimumVisibleItems) {
        int repeatCount = Math.max(2, (int) Math.ceil((double) minimumVisibleItems / source.size()));
        List<String> repeated = new ArrayList<>(source.size() * repeatCount);
        for (int i = 0; i < repeatCount; i++) {
            repeated.addAll(source);
        }
        return repeated;
    }

    public List<String> getGradientColors() {
        List<String> colors = new ArrayList<>();
        colors.add(defaultColor(gradientColor1, "#0f1f49"));
        colors.add(defaultColor(gradientColor2, "#325ac7"));
        if (gradientColorCount >= 3) {
            colors.add(defaultColor(gradientColor3, "#5f85ee"));
        }
        if (gradientColorCount >= 4) {
            colors.add(defaultColor(gradientColor4, "#7f9bff"));
        }
        if (gradientColorCount >= 5) {
            colors.add(defaultColor(gradientColor5, "#b7c8ff"));
        }
        return colors;
    }

    public void setGradientColorByIndex(int index, String value) {
        switch (index) {
            case 0 ->
                gradientColor1 = value;
            case 1 ->
                gradientColor2 = value;
            case 2 ->
                gradientColor3 = value;
            case 3 ->
                gradientColor4 = value;
            case 4 ->
                gradientColor5 = value;
        }
    }

    private String defaultColor(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private void loadGradientColorsFromString(String text) {
        gradientColor1 = "#0f1f49";
        gradientColor2 = "#325ac7";
        gradientColor3 = "#5f85ee";
        gradientColor4 = "#7f9bff";
        gradientColor5 = "#b7c8ff";
        gradientColorCount = 2;
        if (isBlank(text)) {
            return;
        }
        String[] colors = text.split(",");
        gradientColorCount = Math.max(2, Math.min(5, colors.length));
        if (colors.length > 0 && !isBlank(colors[0])) {
            gradientColor1 = colors[0].trim();
        }
        if (colors.length > 1 && !isBlank(colors[1])) {
            gradientColor2 = colors[1].trim();
        }
        if (colors.length > 2 && !isBlank(colors[2])) {
            gradientColor3 = colors[2].trim();
        }
        if (colors.length > 3 && !isBlank(colors[3])) {
            gradientColor4 = colors[3].trim();
        }
        if (colors.length > 4 && !isBlank(colors[4])) {
            gradientColor5 = colors[4].trim();
        }
    }

    public String buildMarqueePreviewBackground() {
        if ("gradient".equals(marqueeForm.getBackgroundMode())) {
            String angle = isVerticalDirection() ? "180deg" : "90deg";
            return "linear-gradient(" + angle + "," + String.join(",", getGradientColors()) + ")";
        }
        return isBlank(marqueeForm.getSolidColor()) ? "#0f1f49" : marqueeForm.getSolidColor();
    }

    public String getMarqueeAnimationClass() {
        if ("ltr".equals(marqueeForm.getDirection())) {
            return "anim-horizontal-ltr";
        }
        if ("ttb".equals(marqueeForm.getDirection())) {
            return "vertical anim-vertical-ttb";
        }
        if ("btt".equals(marqueeForm.getDirection())) {
            return "vertical anim-vertical-btt";
        }
        return "anim-horizontal-rtl";
    }

    public boolean isVerticalDirection() {
        return "ttb".equals(marqueeForm.getDirection()) || "btt".equals(marqueeForm.getDirection());
    }

    public int getMarqueeFontWeightValue() {
        if (isBlank(marqueeForm.getFontWeight())) {
            return 700;
        }
        try {
            int parsed = Integer.parseInt(marqueeForm.getFontWeight());
            return Math.max(400, Math.min(800, parsed));
        } catch (NumberFormatException exception) {
            return 700;
        }
    }

    public void setMarqueeFontWeightValue(int value) {
        int bounded = Math.max(400, Math.min(800, value));
        int normalized = (bounded / 100) * 100;
        marqueeForm.setFontWeight(String.valueOf(normalized));
    }

    private boolean validateMarquee() {
        if (getMarqueeItems().isEmpty()) {
            addError("Add at least one marquee item.");
            return false;
        }
        if (marqueeForm.getSpeedSeconds() < 5 || marqueeForm.getSpeedSeconds() > 30) {
            addError("Speed must be between 5 and 30 seconds.");
            return false;
        }
        if (marqueeForm.getFontSizePx() < 8 || marqueeForm.getFontSizePx() > 60) {
            addError("Font size must be between 8 and 60 px.");
            return false;
        }
        return true;
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

    public void resetVideoCarouselForm() {
        videoCarouselForm = new VideoCarouselItem();
        videoCarouselForm.setActive(true);
        videoCarouselForm.setSortOrder(1);
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

    public List<MarqueeConfig> getMarqueeConfigs() {
        return catalogService.getMarqueeConfigs();
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

    private boolean validateVideoCarouselItem() {
        if (isBlank(videoCarouselForm.getTitle())) {
            addError("Video title is required.");
            return false;
        }
        if (isBlank(videoCarouselForm.getThumbnailUrl())) {
            addError("Thumbnail image is required.");
            return false;
        }
        if (isBlank(videoCarouselForm.getVideoUrl())) {
            addError("Video file is required.");
            return false;
        }
        if (videoCarouselForm.getSortOrder() <= 0) {
            addError("Sort order must be greater than 0.");
            return false;
        }
        return true;
    }

    private boolean validateHomeDivSection() {
        if (isBlank(homeDivSectionForm.getHeading())) {
            addError("Heading is required.");
            return false;
        }
        if (isBlank(homeDivSectionForm.getDescription())) {
            addError("Description is required.");
            return false;
        }
        if (isBlank(homeDivSectionForm.getImageUrl())) {
            addError("Image is required.");
            return false;
        }
        if (homeDivSectionForm.getSortOrder() <= 0) {
            addError("Sort order must be greater than 0.");
            return false;
        }
        if (!"LEFT".equals(homeDivSectionForm.getImageSide()) && !"RIGHT".equals(homeDivSectionForm.getImageSide())) {
            addError("Invalid image side.");
            return false;
        }
        if (!"LEFT".equals(homeDivSectionForm.getContentAlign())
                && !"CENTER".equals(homeDivSectionForm.getContentAlign())
                && !"RIGHT".equals(homeDivSectionForm.getContentAlign())) {
            addError("Invalid content alignment.");
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

        if (catalogService.isExistSKU(productForm.getSku(), productForm.getId())) {
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

    public int getHomeTabIndex() {
        return homeTabIndex;
    }

    public void setHomeTabIndex(int homeTabIndex) {
        this.homeTabIndex = homeTabIndex;
    }

    public HomeSlider getSliderForm() {
        return sliderForm;
    }

    public void setSliderForm(HomeSlider sliderForm) {
        this.sliderForm = sliderForm;
    }

    public HomeDivSection getHomeDivSectionForm() {
        return homeDivSectionForm;
    }

    public void setHomeDivSectionForm(HomeDivSection homeDivSectionForm) {
        this.homeDivSectionForm = homeDivSectionForm;
    }

    public VideoCarouselItem getVideoCarouselForm() {
        return videoCarouselForm;
    }

    public void setVideoCarouselForm(VideoCarouselItem videoCarouselForm) {
        this.videoCarouselForm = videoCarouselForm;
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

    public MarqueeConfig getMarqueeForm() {
        return marqueeForm;
    }

    public void setMarqueeForm(MarqueeConfig marqueeForm) {
        this.marqueeForm = marqueeForm;
    }

    public String getMarqueeItemInput() {
        return marqueeItemInput;
    }

    public void setMarqueeItemInput(String marqueeItemInput) {
        this.marqueeItemInput = marqueeItemInput;
    }

    public int getGradientColorCount() {
        return gradientColorCount;
    }

    public void setGradientColorCount(int gradientColorCount) {
        this.gradientColorCount = gradientColorCount;
    }

    public String getGradientColor1() {
        return gradientColor1;
    }

    public void setGradientColor1(String gradientColor1) {
        this.gradientColor1 = gradientColor1;
    }

    public String getGradientColor2() {
        return gradientColor2;
    }

    public void setGradientColor2(String gradientColor2) {
        this.gradientColor2 = gradientColor2;
    }

    public String getGradientColor3() {
        return gradientColor3;
    }

    public void setGradientColor3(String gradientColor3) {
        this.gradientColor3 = gradientColor3;
    }

    public String getGradientColor4() {
        return gradientColor4;
    }

    public void setGradientColor4(String gradientColor4) {
        this.gradientColor4 = gradientColor4;
    }

    public String getGradientColor5() {
        return gradientColor5;
    }

    public void setGradientColor5(String gradientColor5) {
        this.gradientColor5 = gradientColor5;
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

    public DualListModel<String> getHomeSectionOrderPickList() {
        if (homeSectionOrderPickList == null) {
            loadHomeSectionOrderPickList();
        }
        return homeSectionOrderPickList;
    }

    public void setHomeSectionOrderPickList(DualListModel<String> homeSectionOrderPickList) {
        this.homeSectionOrderPickList = homeSectionOrderPickList;
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
