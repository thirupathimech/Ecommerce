package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.VideoCarouselItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class VideoCarouselBean extends AdminBean {

    private VideoCarouselItem videoCarouselForm;

    @Override
    public void resetForm() {
        videoCarouselForm = new VideoCarouselItem();
        videoCarouselForm.setActive(true);
        videoCarouselForm.setSortOrder(1);
        videoCarouselForm.setVideoUrls("");
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        normalizeVideoCarouselForm();
        catalogService.saveVideoCarouselItem(videoCarouselForm);
        resetForm();
        addInfo("Video carousel item saved successfully");
    }

    @Override
    public boolean validateForm() {
        if (isBlank(videoCarouselForm.getTitle())) {
            addError("Video title is required.");
            return false;
        }
        List<String> selectedVideos = extractVideoRefs(videoCarouselForm.getVideoUrls());
        if (selectedVideos.isEmpty() && !isBlank(videoCarouselForm.getVideoUrl())) {
            selectedVideos = extractVideoRefs(videoCarouselForm.getVideoUrl());
        }
        if (selectedVideos.isEmpty()) {
            addError("At least one video file is required.");
            return false;
        }
        if (videoCarouselForm.getSortOrder() <= 0) {
            addError("Sort order must be greater than 0.");
            return false;
        }
        return true;
    }

    @Override
    public void editForm(Object form) {
        if (form instanceof VideoCarouselItem item) {
            VideoCarouselItem draft = new VideoCarouselItem();
            draft.setId(item.getId());
            draft.setTitle(item.getTitle());
            draft.setVideoUrl(item.getVideoUrl());
            draft.setVideoUrls(item.getVideoUrls());
            draft.setSortOrder(item.getSortOrder());
            draft.setActive(item.isActive());
            videoCarouselForm = draft;
        }
    }

    @Override
    public boolean deleteForm(Object form) {
        if (form instanceof VideoCarouselItem item) {
            if (item == null || item.getId() == null) {
                addError("Invalid video carousel selection.");
                return false;
            }
            catalogService.deleteVideoCarouselItem(item.getId());
            if (videoCarouselForm != null && item.getId().equals(videoCarouselForm.getId())) {
                resetForm();
            }
            addInfo("Video carousel item deleted successfully");
            return true;
        }
        return false;
    }

    private void normalizeVideoCarouselForm() {
        List<String> selectedVideos = extractVideoRefs(videoCarouselForm.getVideoUrls());
        if (selectedVideos.isEmpty() && !isBlank(videoCarouselForm.getVideoUrl())) {
            selectedVideos = extractVideoRefs(videoCarouselForm.getVideoUrl());
        }
        videoCarouselForm.setVideoUrls(String.join("\n", selectedVideos));
        if (!selectedVideos.isEmpty()) {
            videoCarouselForm.setVideoUrl(selectedVideos.get(0));
        }
    }

    public void selectFile(MediaFile file) {
        String ref = toDbFileRef(file.getId());
        if ("video-carousel-video".equals(fileSelectionTarget)) {
            if (!"VIDEO".equals(file.getFileType())) {
                addWarn("Please select a video file.");
                return;
            }
            appendVideoToCarouselForm(ref);
            addInfo("File selected");
        }
    }

    private void appendVideoToCarouselForm(String ref) {
        List<String> selectedVideos = extractVideoRefs(videoCarouselForm.getVideoUrls());
        if (!selectedVideos.contains(ref)) {
            selectedVideos.add(ref);
        }
        videoCarouselForm.setVideoUrls(String.join("\n", selectedVideos));
        videoCarouselForm.setVideoUrl(selectedVideos.get(0));
    }

    private List<String> extractVideoRefs(String values) {
        if (isBlank(values)) {
            return new ArrayList<>();
        }
        return values.lines()
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int getVideoCount(VideoCarouselItem item) {
        if (item == null) {
            return 0;
        }
        List<String> selectedVideos = extractVideoRefs(item.getVideoUrls());
        if (selectedVideos.isEmpty()) {
            selectedVideos = extractVideoRefs(item.getVideoUrl());
        }
        return selectedVideos.size();
    }

    public VideoCarouselItem getVideoCarouselForm() {
        return videoCarouselForm;
    }

    public void setVideoCarouselForm(VideoCarouselItem videoCarouselForm) {
        this.videoCarouselForm = videoCarouselForm;
    }

}
