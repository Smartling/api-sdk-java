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
package com.smartling.api.sdk.file.commandline;

import java.util.List;

/**
 * The parameters used when uploading a file to the Smartling Translation API.
 */
public class UploadFileParams extends BaseFileParams
{
    private String pathToFile;
    private String fileUri;
    private String fileType;
    private Boolean approveContent;
    private String callbackUrl;
    private List<String> localesToApprove;
    private Boolean overwriteApprovedLocales;

    public String getPathToFile()
    {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile)
    {
        this.pathToFile = pathToFile;
    }

    public String getFileUri()
    {
        return fileUri;
    }

    public void setFileUri(String fileUri)
    {
        this.fileUri = fileUri;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public Boolean getApproveContent()
    {
        return approveContent;
    }

    public void setApproveContent(Boolean approveContent)
    {
        this.approveContent = approveContent;
    }

    public String getCallbackUrl()
    {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
    }

    public List<String> getLocalesToApprove() {
        return localesToApprove;
    }

    public void setLocalesToApprove(List<String> localesToApprove) {
        this.localesToApprove = localesToApprove;
    }

    public Boolean getOverwriteApprovedLocales() {
        return overwriteApprovedLocales;
    }

    public void setOverwriteApprovedLocales(Boolean overwriteApprovedLocales) {
        this.overwriteApprovedLocales = overwriteApprovedLocales;
    }
}
