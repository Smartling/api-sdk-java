package com.smartling.api.sdk.projects;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.projects.response.ProjectDetails;

public interface ProjectsApiClient
{
    ProjectDetails getProjectDetails() throws SmartlingApiException;
}