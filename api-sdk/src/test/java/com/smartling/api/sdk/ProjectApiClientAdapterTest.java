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

import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.project.ProjectLocale;
import com.smartling.api.sdk.dto.project.ProjectLocaleList;
import com.smartling.api.sdk.exceptions.ProjectApiException;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ProjectApiClientAdapterTest
{
    private ProjectApiClientAdapter projectApiClientAdapter;

    @Before
    public void setup()
    {
        boolean testMode = ApiTestHelper.getTestMode();
        String apiKey = ApiTestHelper.getApiKey();
        String projectId = ApiTestHelper.getProjectId();

        projectApiClientAdapter = new ProjectApiClientAdapterImpl(testMode, apiKey, projectId);
    }

    @Test
    public void testProjectActions() throws ProjectApiException, IOException
    {
        ApiResponse<ProjectLocaleList> projectLocaleListResponse = projectApiClientAdapter.getProjectLocales();
        ApiTestHelper.verifyApiResponse(projectLocaleListResponse);

        Assert.assertNotNull(projectLocaleListResponse.getData());

        List<ProjectLocale> projectLocales = projectLocaleListResponse.getData().getLocales();
        Assert.assertNotNull(projectLocales);
        Assert.assertTrue(projectLocales.size() >= 1);

        for (ProjectLocale locale : projectLocales)
        {
            Assert.assertTrue("Locale is blank", StringUtils.isNotBlank(locale.getLocale()));
            Assert.assertTrue("Locale name is blank", StringUtils.isNotBlank(locale.getName()));
            Assert.assertTrue("Locale translation is blank", StringUtils.isNotBlank(locale.getTranslated()));
        }
    }

}
