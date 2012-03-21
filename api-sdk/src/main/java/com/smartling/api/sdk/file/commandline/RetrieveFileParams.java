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

/**
 * The parameters used when retrieving a file from the Smartling Api.
 */
public class RetrieveFileParams extends BaseFileParams
{
    private String pathToFile;
    private String locale;
    private String pathToStoreFile;

    public String getPathToFile()
    {
        return pathToFile;
    }

    public void setPathToFile(String getPathToFile)
    {
        this.pathToFile = getPathToFile;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public String getPathToStoreFile()
    {
        return pathToStoreFile;
    }

    public void setPathToStoreFile(String pathToStoreFile)
    {
        this.pathToStoreFile = pathToStoreFile;
    }
}
