/*
 * Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.ApiResponseWrapper;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.project.ProjectLocaleList;
import com.smartling.api.sdk.exceptions.ApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;

/**
 * Base implementation of the {@link ProjectApiClientAdapter}.
 */
public class ProjectApiClientAdapterImpl extends BaseApiClientAdapter implements ProjectApiClientAdapter
{
    private static final Log logger = LogFactory.getLog(ProjectApiClientAdapterImpl.class);

    private final static String GET_PROJECT_LOCALES_API_URL = "%s/project/locale/list?";

    public ProjectApiClientAdapterImpl(final String apiKey, final String projectId)
    {
        super(apiKey, projectId);
    }

    public ProjectApiClientAdapterImpl(final String apiKey, final String projectId, final ProxyConfiguration proxyConfiguration)
    {
        super(apiKey, projectId, proxyConfiguration);
    }

    public ProjectApiClientAdapterImpl(final boolean productionMode, final String apiKey, final String projectId)
    {
        super(productionMode, apiKey, projectId);
    }

    public ProjectApiClientAdapterImpl(final boolean productionMode, final String apiKey, final String projectId, final ProxyConfiguration proxyConfiguration)
    {
        super(productionMode, apiKey, projectId, proxyConfiguration);
    }

    public ProjectApiClientAdapterImpl(final String baseApiUrl, final String apiKey, final String projectId)
    {
        super(baseApiUrl, apiKey, projectId);
    }

    public ProjectApiClientAdapterImpl(final String baseApiUrl, final String apiKey, final String projectId, final ProxyConfiguration proxyConfiguration)
    {
        super(baseApiUrl, apiKey, projectId, proxyConfiguration);
    }

    @Override
    public ApiResponse<ProjectLocaleList> getProjectLocales() throws ApiException
    {
        logger.debug(String.format("Get project locales: projectId = %s, apiKey = %s",
                this.projectId, maskApiKey(this.apiKey)
        )
        );

        String params = buildParamsQuery();
        HttpGet getRequest = new HttpGet(buildUrl(GET_PROJECT_LOCALES_API_URL, params));

        StringResponse response = getHttpUtils().executeHttpCall(getRequest, proxyConfiguration);
        ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<ProjectLocaleList>>() {});
        logger.debug(String.format("Get last modified: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }
}
