package com.smartling.api.sdk.project.response;

import com.smartling.api.sdk.dto.SmartlingData;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Contains information about a project.
 */
public class ProjectDetails implements SmartlingData, ResponseData {

    private String projectId;
    private String projectName;
    private String accountUid;
    private String sourceLocaleId;
    private String sourceLocaleDescription;
    private boolean archived;
    private List<TargetLocale> targetLocales;


    /**
     * @return Unique identifier for the project
     */
    public String getProjectId() {
        return this.projectId;
    }

    /**
     * @return The name of the project
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * @return The unique identifier for your account.
     */
    public String getAccountUid() {
        return this.accountUid;
    }

    /**
     * @return Smartling locale code for the source language
     */
    public String getSourceLocaleId() {
        return this.sourceLocaleId;
    }

    /**
     * @return Full source language description for the project.
     */
    public String getSourceLocaleDescription() {
        return this.sourceLocaleDescription;
    }

    /**
     * @return Records whether the project has been archived.
     */
    public boolean isArchived() {
        return this.archived;
    }

    /**
     * @return Array of target locales for the project.
     */
    public List<TargetLocale> getTargetLocales() {
        return this.targetLocales;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("fileUri", getProjectId())
            .append("totalStringCount", getProjectName())
            .append("totalWordCount", getAccountUid())
            .append("authorizedStringCount", getSourceLocaleId())
            .append("authorizedWordCount", getSourceLocaleDescription())
            .append("completedWordCount", isArchived())
            .append("excludedStringCount", getTargetLocales()).toString();
    }
}
