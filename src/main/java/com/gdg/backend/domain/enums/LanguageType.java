package com.gdg.backend.domain.enums;

public enum LanguageType {
    C,
    JAVA,
    PYTHON;

    public static LanguageType fromString(String type) {
        for (LanguageType languageType : LanguageType.values()) {
            if (languageType.toString().equals(type)) {
                return languageType;
            }
        }
        return null;
    }
}
