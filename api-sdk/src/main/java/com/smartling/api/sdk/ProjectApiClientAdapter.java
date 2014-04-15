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
import com.smartling.api.sdk.dto.project.ProjectLocaleList;
import com.smartling.api.sdk.exceptions.ProjectApiException;

/**
 * Communication point for interacting projects with the Smartling Translation API.
 */
public interface ProjectApiClientAdapter
{
    /**
     * Returns list with all project locales
     *
     * @return {@link com.smartling.api.sdk.dto.ApiResponse} from a success response from the Project API.
     * @throws ProjectApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<ProjectLocaleList> getProjectLocales() throws ProjectApiException;
}
