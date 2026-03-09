package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.HomeDivSection;
import com.aadhik.ecommerce.model.MediaFile;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class HomeDivSectionBean extends AdminBean {

    private HomeDivSection homeDivSectionForm;

    @Override
    public void resetForm() {
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

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }
        catalogService.saveHomeDivSection(homeDivSectionForm);
        resetForm();
        addInfo("Div section saved successfully");
    }

    @Override
    public boolean validateForm() {
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

    @Override
    public void editForm(Object form) {
        if (form instanceof HomeDivSection section) {
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
    }

    @Override
    public boolean deleteForm(Object form) {
        if (form instanceof HomeDivSection section) {
            if (section == null || section.getId() == null) {
                addError("Invalid div section selection.");
                return false;
            }

            catalogService.deleteHomeDivSection(section.getId());
            if (homeDivSectionForm != null && section.getId().equals(homeDivSectionForm.getId())) {
                resetForm();
            }
            addInfo("Div section deleted successfully");
            return true;
        }
        return false;
    }
    
    public void selectFile(MediaFile file) {
        String ref = toDbFileRef(file.getId());
        if ("home-div-section-image".equals(fileSelectionTarget)) {
            if (!"IMAGE".equals(file.getFileType())) {
                addWarn("Please select an image file.");
                return;
            }
            homeDivSectionForm.setImageUrl(ref);
        }
        addInfo("File selected");
    }

    public HomeDivSection getHomeDivSectionForm() {
        return homeDivSectionForm;
    }

    public void setHomeDivSectionForm(HomeDivSection homeDivSectionForm) {
        this.homeDivSectionForm = homeDivSectionForm;
    }

}
