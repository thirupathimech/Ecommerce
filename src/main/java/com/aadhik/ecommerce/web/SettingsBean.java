package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.MediaFile;
import com.aadhik.ecommerce.model.StoreSettings;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;

/**
 * @author THIRUPATHI G
 */
@Named
@ViewScoped
public class SettingsBean extends AdminBean {

    @Inject
    private StoreSettingsViewBean storeSettingsViewBean;
    private Long id;
    private String storeName;
    private String storeLogo;
    private String storeFavicon;
    private String storeEmail;
    private String storePhoneNumber;
    private String storeAddress;
    private String storePinCode;
    private String storePassword;
    private String gstNumber;
    private boolean orderEmailNotification;
    private boolean adminOrderAlert;
    private String smsApiKey;
    private String whatsappApiKey;
    private String otpProvider;
    private String metaTitle;
    private String metaDescription;
    private String googleAnalyticsId;
    private String googlePlacesApiKey;
    private String facebookPixelId;
    private String sitemapUrl;
    private boolean allowGuestCheckout;
    private Integer orderCancellationTime;
    private Integer returnPolicyDays;
    private String whatsappApi;
    private String smsApi;
    private String emailSmtp;
    private String deliveryTrackingApi;

    @Override
    public void resetForm() {
        loadSettings();
    }

    @Override
    public void saveForm() {
        StoreSettings settings = new StoreSettings();
        settings.setId(id);
        settings.setStoreName(storeName);
        settings.setStoreLogo(storeLogo);
        settings.setStoreFavicon(storeFavicon);
        settings.setStoreEmail(storeEmail);
        settings.setStorePhoneNumber(storePhoneNumber);
        settings.setStoreAddress(storeAddress);
        settings.setStorePinCode(storePinCode);
        settings.setStorePassword(storePassword);
        settings.setGstNumber(gstNumber);
        settings.setGooglePlacesApiKey(googlePlacesApiKey);
        settings.setMetaTitle(metaTitle);
        settings.setMetaDescription(metaDescription);
        settings.setGoogleAnalyticsId(googleAnalyticsId);
        settings.setFacebookPixelId(facebookPixelId);
        settings.setSitemapUrl(sitemapUrl);
        settings.setUpdatedAt(LocalDateTime.now());
        StoreSettings saved = catalogService.saveStoreSettings(settings);
        id = saved.getId();
        storeSettingsViewBean.refresh();
        addInfo("Settings saved successfully");
    }

    @Override
    public boolean validateForm() {
        return false;
    }

    @Override
    public void editForm(Object form) {
    }

    @Override
    public boolean deleteForm(Object form) {
        return false;
    }

    private void loadSettings() {
        StoreSettings settings = catalogService.getStoreSettings();
        if (settings == null) {
            loadDefaultSettings();
            return;
        }
        id = settings.getId();
        storeName = settings.getStoreName();
        storeLogo = settings.getStoreLogo();
        storeFavicon = settings.getStoreFavicon();
        storeEmail = settings.getStoreEmail();
        storePhoneNumber = settings.getStorePhoneNumber();
        storeAddress = settings.getStoreAddress();
        storePinCode = settings.getStorePinCode();
        storePassword = settings.getStorePassword();
        gstNumber = settings.getGstNumber();
        googlePlacesApiKey = settings.getGooglePlacesApiKey();
        metaTitle = settings.getMetaTitle();
        metaDescription = settings.getMetaDescription();
        googleAnalyticsId = settings.getGoogleAnalyticsId();
        facebookPixelId = settings.getFacebookPixelId();
        sitemapUrl = settings.getSitemapUrl();
        returnPolicyDays = 7;
        allowGuestCheckout = true;
        orderCancellationTime = 24;
        orderEmailNotification = true;
        adminOrderAlert = true;
        otpProvider = "twilio";
        whatsappApi = "Meta Cloud API";
        smsApi = "Twilio";
        emailSmtp = "smtp.gmail.com:587";
        deliveryTrackingApi = "Shiprocket";
    }

    private void loadDefaultSettings() {
        id = null;
        storeName = "My Ecommerce Store";
        storeLogo = "";
        storeFavicon = "";
        storeEmail = "support@example.com";
        storePhoneNumber = "+91";
        storeAddress = "";
        storePinCode = "";
        storePassword = "";
        gstNumber = "";
        orderEmailNotification = true;
        adminOrderAlert = true;
        smsApiKey = "";
        whatsappApiKey = "";
        otpProvider = "twilio";
        metaTitle = "Buy Online | My Ecommerce Store";
        metaDescription = "Best offers and products from our ecommerce store.";
        googleAnalyticsId = "";
        googlePlacesApiKey = "";
        facebookPixelId = "";
        sitemapUrl = "https://example.com/sitemap.xml";
        allowGuestCheckout = true;
        orderCancellationTime = 24;
        returnPolicyDays = 7;
        whatsappApi = "Meta Cloud API";
        smsApi = "Twilio";
        emailSmtp = "smtp.gmail.com:587";
        deliveryTrackingApi = "Shiprocket";
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getStoreFavicon() {
        return storeFavicon;
    }

    public void setStoreFavicon(String storeFavicon) {
        this.storeFavicon = storeFavicon;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public void setStorePhoneNumber(String storePhoneNumber) {
        this.storePhoneNumber = storePhoneNumber;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStorePinCode() {
        return storePinCode;
    }

    public void setStorePinCode(String storePinCode) {
        this.storePinCode = storePinCode;
    }

    public String getStorePassword() {
        return storePassword;
    }

    public void setStorePassword(String storePassword) {
        this.storePassword = storePassword;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public boolean isOrderEmailNotification() {
        return orderEmailNotification;
    }

    public void setOrderEmailNotification(boolean orderEmailNotification) {
        this.orderEmailNotification = orderEmailNotification;
    }

    public boolean isAdminOrderAlert() {
        return adminOrderAlert;
    }

    public void setAdminOrderAlert(boolean adminOrderAlert) {
        this.adminOrderAlert = adminOrderAlert;
    }

    public String getSmsApiKey() {
        return smsApiKey;
    }

    public void setSmsApiKey(String smsApiKey) {
        this.smsApiKey = smsApiKey;
    }

    public String getWhatsappApiKey() {
        return whatsappApiKey;
    }

    public void setWhatsappApiKey(String whatsappApiKey) {
        this.whatsappApiKey = whatsappApiKey;
    }

    public String getOtpProvider() {
        return otpProvider;
    }

    public void setOtpProvider(String otpProvider) {
        this.otpProvider = otpProvider;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getGooglePlacesApiKey() {
        return googlePlacesApiKey;
    }

    public void setGooglePlacesApiKey(String googlePlacesApiKey) {
        this.googlePlacesApiKey = googlePlacesApiKey;
    }

    public void selectFile(MediaFile file) {
        if (file == null || file.getId() == null) {
            return;
        }
        String ref = toDbFileRef(file.getId());
        if ("STORE_LOGO".equalsIgnoreCase(fileSelectionTarget)) {
            storeLogo = ref;
        } else if ("STORE_FAVICON".equalsIgnoreCase(fileSelectionTarget)) {
            storeFavicon = ref;
        }
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    public String getFacebookPixelId() {
        return facebookPixelId;
    }

    public void setFacebookPixelId(String facebookPixelId) {
        this.facebookPixelId = facebookPixelId;
    }

    public String getSitemapUrl() {
        return sitemapUrl;
    }

    public void setSitemapUrl(String sitemapUrl) {
        this.sitemapUrl = sitemapUrl;
    }

    public boolean isAllowGuestCheckout() {
        return allowGuestCheckout;
    }

    public void setAllowGuestCheckout(boolean allowGuestCheckout) {
        this.allowGuestCheckout = allowGuestCheckout;
    }

    public Integer getOrderCancellationTime() {
        return orderCancellationTime;
    }

    public void setOrderCancellationTime(Integer orderCancellationTime) {
        this.orderCancellationTime = orderCancellationTime;
    }

    public Integer getReturnPolicyDays() {
        return returnPolicyDays;
    }

    public void setReturnPolicyDays(Integer returnPolicyDays) {
        this.returnPolicyDays = returnPolicyDays;
    }

    public String getWhatsappApi() {
        return whatsappApi;
    }

    public void setWhatsappApi(String whatsappApi) {
        this.whatsappApi = whatsappApi;
    }

    public String getSmsApi() {
        return smsApi;
    }

    public void setSmsApi(String smsApi) {
        this.smsApi = smsApi;
    }

    public String getEmailSmtp() {
        return emailSmtp;
    }

    public void setEmailSmtp(String emailSmtp) {
        this.emailSmtp = emailSmtp;
    }

    public String getDeliveryTrackingApi() {
        return deliveryTrackingApi;
    }

    public void setDeliveryTrackingApi(String deliveryTrackingApi) {
        this.deliveryTrackingApi = deliveryTrackingApi;
    }

}
