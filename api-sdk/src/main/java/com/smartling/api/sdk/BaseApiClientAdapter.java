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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.ApiResponseWrapper;
import com.smartling.api.sdk.dto.Data;
import com.smartling.api.sdk.util.DateTypeAdapter;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.smartling.api.sdk.file.FileApiParams.API_KEY;
import static com.smartling.api.sdk.file.FileApiParams.PROJECT_ID;

/**
 * Base class for client adapters.
 */
public abstract class BaseApiClientAdapter
{
    private static final String API_KEY_MASK       = "%s-XXXXXXXXXXXX";
    private static final String RESPONSE_MESSAGES  = "Messages: %s";

    private static final String SMARTLING_API_URL         = "https://api.smartling.com/v1";
    private static final String SMARTLING_SANDBOX_API_URL = "https://sandbox-api.smartling.com/v1";

    protected static final String SUCCESS_CODE       = "SUCCESS";

    protected String baseApiUrl;
    protected String apiKey;
    protected String projectId;

    protected ProxyConfiguration proxyConfiguration;

    /**
     * Instantiate using the production mode setting (non sandbox).
     *
     * @param apiKey    your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId your projectId. Can be found at https://dashboard.smartling.com/settings/api
     */
    protected BaseApiClientAdapter(String apiKey, String projectId)
    {
        this(SMARTLING_API_URL, apiKey, projectId);
    }

    /**
     * Instantiate using the production mode setting (non sandbox).
     *
     * @param apiKey             your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId          your projectId. Can be found at https://dashboard.smartling.com/settings/api
     * @param proxyConfiguration proxy configuration, pass {@code NULL} to never use proxy
     */
    protected BaseApiClientAdapter(String apiKey, String projectId, ProxyConfiguration proxyConfiguration)
    {
        this(SMARTLING_API_URL, apiKey, projectId, proxyConfiguration);
    }

    /**
     * @param productionMode True if the production version of the api should be used, false if the Sandbox should be used.
     *                       It is recommended when first integrating your application with the API, that you use the Sandbox and not the production version.
     *                       For more information on the Sandbox, please see https://docs.smartling.com.
     * @param apiKey         your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId      your projectId. Can be found at https://dashboard.smartling.com/settings/api
     */
    protected BaseApiClientAdapter(boolean productionMode, String apiKey, String projectId)
    {
        this(productionMode ? SMARTLING_API_URL : SMARTLING_SANDBOX_API_URL, apiKey, projectId);
    }

    /**
     * @param productionMode     True if the production version of the api should be used, false if the Sandbox should be used.
     *                           It is recommended when first integrating your application with the API, that you use the Sandbox and not the production version.
     *                           For more information on the Sandbox, please see https://docs.smartling.com.
     * @param apiKey             your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId          your projectId. Can be found at https://dashboard.smartling.com/settings/api
     * @param proxyConfiguration proxy configuration, pass {@code NULL} to never use proxy
     */
    protected BaseApiClientAdapter(boolean productionMode, String apiKey, String projectId, ProxyConfiguration proxyConfiguration)
    {
        this(productionMode ? SMARTLING_API_URL : SMARTLING_SANDBOX_API_URL, apiKey, projectId, proxyConfiguration);
    }

    /**
     * @param baseApiUrl the apiUrl to use for interacting with the Smartling Translation API.
     * @param apiKey     your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId  your projectId. Can be found at https://dashboard.smartling.com/settings/api
     */
    protected BaseApiClientAdapter(String baseApiUrl, String apiKey, String projectId)
    {
        this(baseApiUrl, apiKey, projectId, null);
    }

    /**
     * @param baseApiUrl         the apiUrl to use for interacting with the Smartling Translation API.
     * @param apiKey             your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId          your projectId. Can be found at https://dashboard.smartling.com/settings/api
     * @param proxyConfiguration proxy configuration, pass {@code NULL} to never use proxy
     */
    protected BaseApiClientAdapter(String baseApiUrl, String apiKey, String projectId, ProxyConfiguration proxyConfiguration)
    {
        Validate.notNull(baseApiUrl, "Api url is required");
        Validate.notNull(apiKey, "apiKey is required");
        Validate.notNull(projectId, "projectId is required");

        this.baseApiUrl = baseApiUrl;
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.proxyConfiguration = proxyConfiguration;
    }


    protected String buildUrl(String apiServerUrl, String apiParameters)
    {
        StringBuilder urlWithParameters = new StringBuilder(String.format(apiServerUrl, baseApiUrl));
        urlWithParameters.append(apiParameters);
        return urlWithParameters.toString();
    }

    protected String buildParamsQuery(NameValuePair... nameValuePairs)
    {
        List<NameValuePair> qparams = getRequiredParams();

        for (NameValuePair nameValuePair : nameValuePairs)
            if (nameValuePair.getValue() != null)
                qparams.add(nameValuePair);

        return URLEncodedUtils.format(qparams, CharEncoding.UTF_8);
    }

    protected List<NameValuePair> getRequiredParams()
    {
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair(API_KEY, apiKey));
        qparams.add(new BasicNameValuePair(PROJECT_ID, projectId));

        return qparams;
    }

    protected String maskApiKey(String apiKey)
    {
        return apiKey.contains("-") ? String.format(API_KEY_MASK, apiKey.substring(0, apiKey.lastIndexOf("-"))) : apiKey;
    }

    protected List<BasicNameValuePair> getNameValuePairs(String name, List<String> values)
    {
        if (null == values || values.isEmpty())
            return Collections.emptyList();

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        for (String value : values)
            nameValuePairs.add(new BasicNameValuePair(name, value));

        return nameValuePairs;
    }

    protected String getApiResponseMessages(ApiResponse apiResponse)
    {
        String responseMessages = StringUtils.EMPTY;

        if (!SUCCESS_CODE.equals(apiResponse.getCode()))
            responseMessages = String.format(RESPONSE_MESSAGES, StringUtils.join(apiResponse.getMessages(), ", "));

        return responseMessages;
    }

    protected static <T extends Data> ApiResponse<T> getApiResponse(String response, TypeToken<ApiResponseWrapper<T>> responseType)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

        Gson gson = builder.create();
        ApiResponseWrapper<T> responseWrapper = gson.fromJson(response, responseType.getType());

        return responseWrapper.getResponse();
    }
}
