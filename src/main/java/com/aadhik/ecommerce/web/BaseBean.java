package com.aadhik.ecommerce.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;

/**
 * @author THIRUPATHI G
 */
public abstract class BaseBean implements Serializable {

    protected boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    protected void addInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    }

    protected void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        FacesContext.getCurrentInstance().validationFailed();
    }

    protected void addWarn(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
    }
}
