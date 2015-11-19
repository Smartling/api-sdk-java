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
package com.smartling.api.sdk.file.parameters;

/**
 * Simple class used to hold the names of the params for interacting with the Smartling Translation API.
 */
public class FileApiParameter
{
    public static final String LOCALE                       = "locale";
    public static final String FILE_URI                     = "fileUri";
    public static final String FILE_TYPE                    = "fileType";
    public static final String APPROVED                     = "approved";
    public static final String URI_MASK                     = "uriMask";
    public static final String LAST_UPLOADED_AFTER          = "lastUploadedAfter";
    public static final String LAST_UPLOADED_BEFORE         = "lastUploadedBefore";
    public static final String LAST_MODIFIED_AFTER          = "lastModifiedAfter";
    public static final String FILE_TYPES                   = "fileTypes";
    public static final String OFFSET                       = "offset";
    public static final String LIMIT                        = "limit";
    public static final String CONDITIONS                   = "conditions";
    public static final String FILE                         = "file";
    public static final String RETRIEVAL_TYPE               = "retrievalType";
    public static final String CALLBACK_URL                 = "callbackUrl";
    public static final String LOCALES_TO_APPROVE           = "localesToApprove";
    public static final String OVERWRITE_APPROVED_LOCALES   = "overwriteApprovedLocales";
    public static final String INCLUDE_ORIGINAL_STRINGS     = "includeOriginalStrings";
    public static final String CLIENT_LIB_ID                = "smartling.client_lib_id";
}
