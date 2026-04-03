package com.aadhik.ecommerce.web;

import com.aadhik.ecommerce.model.ShippingRate;
import com.aadhik.ecommerce.model.ShippingThresholdType;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class ShippingBean extends AdminBean implements Serializable {

    private static final String OTHER_STATES_CODE = "OTHER";
    private static final Map<String, String> INDIA_STATES = new LinkedHashMap<>();
    private static final Map<String, BigDecimal> WEIGHT_UNIT_TO_KG = new LinkedHashMap<>();

    static {
        INDIA_STATES.put(OTHER_STATES_CODE, "Other States (Default fallback)");
        INDIA_STATES.put("AN", "Andaman and Nicobar Islands");
        INDIA_STATES.put("AP", "Andhra Pradesh");
        INDIA_STATES.put("AR", "Arunachal Pradesh");
        INDIA_STATES.put("AS", "Assam");
        INDIA_STATES.put("BR", "Bihar");
        INDIA_STATES.put("CH", "Chandigarh");
        INDIA_STATES.put("CT", "Chhattisgarh");
        INDIA_STATES.put("DN", "Dadra and Nagar Haveli and Daman and Diu");
        INDIA_STATES.put("DL", "Delhi");
        INDIA_STATES.put("GA", "Goa");
        INDIA_STATES.put("GJ", "Gujarat");
        INDIA_STATES.put("HR", "Haryana");
        INDIA_STATES.put("HP", "Himachal Pradesh");
        INDIA_STATES.put("JK", "Jammu and Kashmir");
        INDIA_STATES.put("JH", "Jharkhand");
        INDIA_STATES.put("KA", "Karnataka");
        INDIA_STATES.put("KL", "Kerala");
        INDIA_STATES.put("LA", "Ladakh");
        INDIA_STATES.put("LD", "Lakshadweep");
        INDIA_STATES.put("MP", "Madhya Pradesh");
        INDIA_STATES.put("MH", "Maharashtra");
        INDIA_STATES.put("MN", "Manipur");
        INDIA_STATES.put("ML", "Meghalaya");
        INDIA_STATES.put("MZ", "Mizoram");
        INDIA_STATES.put("NL", "Nagaland");
        INDIA_STATES.put("OR", "Odisha");
        INDIA_STATES.put("PY", "Puducherry");
        INDIA_STATES.put("PB", "Punjab");
        INDIA_STATES.put("RJ", "Rajasthan");
        INDIA_STATES.put("SK", "Sikkim");
        INDIA_STATES.put("TN", "Tamil Nadu");
        INDIA_STATES.put("TS", "Telangana");
        INDIA_STATES.put("TR", "Tripura");
        INDIA_STATES.put("UP", "Uttar Pradesh");
        INDIA_STATES.put("UT", "Uttarakhand");
        INDIA_STATES.put("WB", "West Bengal");

        WEIGHT_UNIT_TO_KG.put("KG", BigDecimal.ONE);
        WEIGHT_UNIT_TO_KG.put("G", new BigDecimal("0.001"));
        WEIGHT_UNIT_TO_KG.put("L", BigDecimal.ONE);
        WEIGHT_UNIT_TO_KG.put("ML", new BigDecimal("0.001"));
    }

    private ShippingRate shippingForm;
    private String weightUnit = "KG";
    private String ruleStateCode;
    private ShippingThresholdType ruleThresholdType = ShippingThresholdType.WEIGHT;

    @Override
    public void resetForm() {
        shippingForm = new ShippingRate();
        shippingForm.setActive(true);
        shippingForm.setThresholdValue(BigDecimal.ZERO);
        shippingForm.setCharge(BigDecimal.ZERO);
        weightUnit = "KG";
    }

    @Override
    public void saveForm() {
        if (!validateForm()) {
            return;
        }

        shippingForm.setStateCode(ruleStateCode);
        shippingForm.setStateName(INDIA_STATES.get(ruleStateCode));
        shippingForm.setThresholdType(ruleThresholdType);
        shippingForm.setThresholdValue(normalizedThresholdValue());

        catalogService.saveShippingRate(shippingForm);
        addInfo("Shipping rate saved successfully.");
        resetForm();
    }

    @Override
    public boolean validateForm() {
        if (shippingForm.getThresholdType() == null) {
            shippingForm.setThresholdType(ruleThresholdType);
        }
        if (isBlank(ruleStateCode)) {
            addError("State header required. First select state and rule type.");
            return false;
        }
        if (ruleThresholdType == null) {
            addError("Rule type header required.");
            return false;
        }
        if (shippingForm.getThresholdValue() == null || shippingForm.getThresholdValue().compareTo(BigDecimal.ZERO) < 0) {
            addError("Threshold value must be zero or higher.");
            return false;
        }
        if (ruleThresholdType == ShippingThresholdType.WEIGHT && !WEIGHT_UNIT_TO_KG.containsKey(weightUnit)) {
            addError("Choose a valid weight unit.");
            return false;
        }
        if (shippingForm.getCharge() == null || shippingForm.getCharge().compareTo(BigDecimal.ZERO) < 0) {
            addError("Shipping charge must be zero or higher.");
            return false;
        }
        if (catalogService.existsShippingRule(ruleStateCode, ruleThresholdType, shippingForm.getId())) {
            addError("A rule already exists for this state with a different rule type.");
            return false;
        }
        
        if (catalogService.existsShippingConditionRule(ruleStateCode, ruleThresholdType, normalizedThresholdValue(), shippingForm.getId())) {
            addError("A rule condition already exists for this state.");
            return false;
        }

        return true;
    }

    @Override
    public void editForm(Object form) {
        if (!(form instanceof ShippingRate item)) {
            return;
        }
        ShippingRate draft = new ShippingRate();
        draft.setId(item.getId());
        draft.setStateCode(item.getStateCode());
        draft.setStateName(item.getStateName());
        draft.setThresholdType(item.getThresholdType());
        draft.setThresholdValue(item.getThresholdValue());
        draft.setCharge(item.getCharge());
        draft.setActive(item.isActive());
        shippingForm = draft;
        ruleStateCode = item.getStateCode();
        ruleThresholdType = item.getThresholdType();
        weightUnit = "KG";
        activeMenu = "shipping";
    }

    @Override
    public boolean deleteForm(Object form) {
        if (!(form instanceof ShippingRate item) || item.getId() == null) {
            addError("Invalid shipping rule.");
            return false;
        }
        catalogService.deleteShippingRate(item.getId());
        addInfo("Shipping rate deleted successfully.");
        return true;
    }

    public ShippingRate getShippingForm() {
        return shippingForm;
    }

    public void setShippingForm(ShippingRate shippingForm) {
        this.shippingForm = shippingForm;
    }

    public ShippingThresholdType[] getThresholdTypes() {
        return ShippingThresholdType.values();
    }

    public String thresholdTypeLabel(ShippingThresholdType thresholdType) {
        if (thresholdType == null) {
            return "";
        }
        return thresholdType == ShippingThresholdType.WEIGHT ? "Weight based" : "Price based";
    }

    public String thresholdSuffix(ShippingThresholdType thresholdType) {
        if (thresholdType == ShippingThresholdType.WEIGHT) {
            return "kg";
        }
        return "₹";
    }

    public List<SelectItem> getAvailableStateOptions() {
        List<SelectItem> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : INDIA_STATES.entrySet()) {
            items.add(new SelectItem(entry.getKey(), entry.getValue()));
        }
        return items;
    }

    public List<ShippingRate> getShippingRates() {
        List<ShippingRate> rates = new ArrayList<>(catalogService.getShippingRates());
        rates.sort(Comparator
                .comparing(ShippingRate::getStateName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(ShippingRate::getThresholdType, Comparator.nullsLast(Enum::compareTo))
                .thenComparing(ShippingRate::getThresholdValue, Comparator.nullsLast(BigDecimal::compareTo)));
        return rates;
    }

    public List<RuleHeader> getRuleHeaders() {
        Map<String, RuleHeader> grouped = new LinkedHashMap<>();
        for (ShippingRate rate : getShippingRates()) {
            String key = rate.getStateCode() + "::" + rate.getThresholdType();
            grouped.computeIfAbsent(key, k -> new RuleHeader(rate.getStateCode(), rate.getStateName(), rate.getThresholdType()));
        }
        return new ArrayList<>(grouped.values());
    }

    public List<ShippingRate> getConditionsForHeader(String stateCode, ShippingThresholdType thresholdType) {
        List<ShippingRate> result = new ArrayList<>();
        List<ShippingRate> shippingRates = getShippingRates();
        if (shippingRates == null) {
            return result;
        }
        for (ShippingRate rate : shippingRates) {
            if (rate == null) {
                continue;
            }
            // Check stateCode
            if (!Objects.equals(stateCode, rate.getStateCode())) {
                continue;
            }
            // Check thresholdType
            if (thresholdType != rate.getThresholdType()) {
                continue;
            }
            result.add(rate);
        }
        return result;
    }

    public void useRuleHeader(String stateCode, ShippingThresholdType thresholdType) {
        this.ruleStateCode = stateCode;
        this.ruleThresholdType = thresholdType;
        if (shippingForm.getId() == null) {
            resetForm();
        }
    }

    public String stateLabel(ShippingRate rate) {
        if (rate == null || isBlank(rate.getStateCode())) {
            return "Other states";
        }
        return rate.getStateName();
    }

    public boolean isDefaultOtherStatesRow(ShippingRate rate) {
        return rate != null && OTHER_STATES_CODE.equalsIgnoreCase(rate.getStateCode());
    }

    public String chargeLabel(ShippingRate rate) {
        return "₹ " + rate.getCharge();
    }

    public List<SelectItem> getWeightUnits() {
        return List.of(
                new SelectItem("KG", "Kilogram (kg)"),
                new SelectItem("G", "Gram (g)"),
                new SelectItem("L", "Liter (L)"),
                new SelectItem("ML", "Milli liter (ml)")
        );
    }

    public String thresholdDisplayValue(ShippingRate rate) {
        if (rate == null || rate.getThresholdValue() == null) {
            return "";
        }
        if (rate.getThresholdType() == ShippingThresholdType.WEIGHT) {
            return rate.getThresholdValue().stripTrailingZeros().toPlainString() + " kg";
        }
        return "₹ " + rate.getThresholdValue().stripTrailingZeros().toPlainString();
    }

    public String thresholdInputSuffix() {
        if (ruleThresholdType == ShippingThresholdType.WEIGHT) {
            return weightUnit == null ? "KG" : weightUnit;
        }
        return "₹";
    }

    public String getRuleStateCode() {
        return ruleStateCode;
    }

    public void setRuleStateCode(String ruleStateCode) {
        this.ruleStateCode = ruleStateCode;
    }

    public ShippingThresholdType getRuleThresholdType() {
        return ruleThresholdType;
    }

    public void setRuleThresholdType(ShippingThresholdType ruleThresholdType) {
        this.ruleThresholdType = ruleThresholdType;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    private BigDecimal normalizedThresholdValue() {
        if (ruleThresholdType != ShippingThresholdType.WEIGHT) {
            return shippingForm.getThresholdValue();
        }
        BigDecimal factor = WEIGHT_UNIT_TO_KG.getOrDefault(weightUnit, BigDecimal.ONE);
        return shippingForm.getThresholdValue().multiply(factor);
    }

    public static class RuleHeader {

        private final String stateCode;
        private final String stateName;
        private final ShippingThresholdType thresholdType;

        public RuleHeader(String stateCode, String stateName, ShippingThresholdType thresholdType) {
            this.stateCode = stateCode;
            this.stateName = stateName;
            this.thresholdType = thresholdType;
        }

        public String getStateCode() {
            return stateCode;
        }

        public String getStateName() {
            return stateName;
        }

        public ShippingThresholdType getThresholdType() {
            return thresholdType;
        }
    }
}
