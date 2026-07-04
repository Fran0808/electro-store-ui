package com.store.inventario.model.guia;

import com.store.inventario.module.auth.model.entity.User;
import java.util.List;

public class InventoryGuide {
    private String code;
    private User user;
    private String type;
    private String reason;
    private String description;
    private String guideDate;
    private List<GuideDetail> details;

    public InventoryGuide() {
    }

    public InventoryGuide(String code, User user, String type, String reason, String description, String guideDate, List<GuideDetail> details) {
        this.code = code;
        this.user = user;
        this.type = type;
        this.reason = reason;
        this.description = description;
        this.guideDate = guideDate;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuideDate() {
        return guideDate;
    }

    public void setGuideDate(String guideDate) {
        this.guideDate = guideDate;
    }

    public List<GuideDetail> getDetails() {
        return details;
    }

    public void setDetails(List<GuideDetail> details) {
        this.details = details;
    }
}
