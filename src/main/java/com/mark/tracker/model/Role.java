package com.mark.tracker.model;

public enum Role {
    TECHNICAL_CONSULTANT("Technical Consultant"),
    PROJECT_MANAGER("Project Manager"),
    DIRECTOR("Director"),
    CHIEF("Chief");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
