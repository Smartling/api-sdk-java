package com.smartling.api.sdk.project.response;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Information about a single target locale
 */
public class TargetLocale {

    private String localeId;
    private String description;

    public TargetLocale(String localeId, String description) {
        this.localeId = localeId;
        this.description = description;
    }

    public String getLocaleId() {
        return localeId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof TargetLocale)) {
            return false;
        }

        TargetLocale otherTargetLocale = (TargetLocale)other;
        return new EqualsBuilder()
                .append(otherTargetLocale.getLocaleId(), getLocaleId())
                .append(otherTargetLocale.getDescription(), getDescription()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getLocaleId()).append(getDescription()).hashCode();
    }
}
