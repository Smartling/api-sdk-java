package com.smartling.api.sdk.project;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.project.response.ProjectDetails;

/**
 * Interface for Project resource
 */
public interface ProjectApiClient {

    /**
     * Returns basic details on a specific Smartling project.
     * @return {@link ProjectDetails} details about the project.
     * @throws SmartlingApiException if something goes wrong.
     */
    ProjectDetails getDetails() throws SmartlingApiException;
}
