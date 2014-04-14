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
package com.smartling.api.sdk.file;

public enum FileType
{
    JAVA_PROPERTIES("javaProperties"),  // Java resources
    IOS("ios"),                         // iOS resources
    ANDROID("android"),                 // Android resources
    GETTEXT("gettext"),                 // GetText .PO/.POT file
    XLIFF("xliff"),                     // XLIFF file
    YAML("yaml"),                       // Ruby/YAML file
    JSON("json"),                       // generic JSON file
    XML("xml"),                         // generic XML file
    HTML("html"),                       // HTML file
    FREEMARKER("freemarker"),           // FreeMarker template
    DOCX("docx"),                       // DOCX
    DOC("doc"),                         // DOC file (Microsoft Word)
    PPTX("pptx"),                       // PPTX
    XLSX("xlsx"),                       // XLSX
    XLS("xls"),                         // XLS
    IDML("idml"),                       // IDML
    RESX("resx"),                       // .NET resource (.resx, .resw)
    QT("qt"),                           // Qt Linguist (.TS file)
    CSV("csv"),                         // CSV (Comma-separated values)
    PLAIN_TEXT("plainText"),            // plain text (.txt files)
    PPT("ppt"),                         // PPT binary file format
    PRES("pres");                       // Pres resources

    private String identifier;

    private FileType(String identifier)
    {
        this.identifier = identifier;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public static FileType lookup(String fileTypeString)
    {
        for (FileType fileType : FileType.values())
        {
            if (fileType.identifier.equalsIgnoreCase(fileTypeString))
                return fileType;
        }

        return null;
    }
}
