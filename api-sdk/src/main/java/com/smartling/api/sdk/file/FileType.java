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

import org.apache.commons.lang3.StringUtils;

public enum FileType
{
    JAVA_PROPERTIES("text/plain", true),            // Java resources
    IOS("text/plain", true),                        // iOS resources
    ANDROID("application/xml", true),               // Android resources
    GETTEXT("text/plain", true),                    // GetText .PO/.POT file
    XLIFF("application/xml", true),                 // XLIFF file
    YAML("text/plain", true),                       // Ruby/YAML file
    JSON("application/json", true),                 // generic JSON file
    XML("application/xml", true),                   // generic XML file
    HTML("text/html", true),                        // HTML file
    FREEMARKER("application/octet-stream", false),  // FreeMarker template
    DOCX("application/octet-stream", false),        // DOCX
    DOC("application/octet-stream", false),         // DOC file (Microsoft Word)
    PPTX("application/octet-stream", false),        // PPTX
    XLSX("application/octet-stream", false),        // XLSX
    XLS("application/octet-stream", false),         // XLS
    IDML("application/octet-stream", false),        // IDML
    RESX("application/xml", true),                  // .NET resource (.resx, .resw)
    QT("application/xml", true),                    // Qt Linguist (.TS file)
    CSV("text/csv", true),                          // CSV (Comma-separated values)
    PLAIN_TEXT("text/plain", true),                 // plain text (.txt files)
    PPT("application/octet-stream", false),         // PPT binary file format
    PRES("text/plain", true),                       // Pres resources
    STRINGSDICT("application/xml", true),           // iOS/OSX resources in dictionary format
    MADCAP("application/octet-stream", false),      // MADCAP file
    SRT("text/plain", false),                       // SubRip Text Format
    MARKDOWN("text/markdown", true);                // Markdown Text Format

    private final String identifier;
    private final String mimeType;
    private final boolean isTextFormat;

    private static String createIdentifier(String s)
    {
        StringBuilder buf = new StringBuilder();
        String[] parts = s.split("_");

        for (int i = 0; i < parts.length; i++)
            buf.append((i == 0) ? parts[i].toLowerCase() : StringUtils.capitalize(parts[i].toLowerCase()));

        return buf.toString();
    }

    private FileType(final String mimeType, final boolean isTextFormat)
    {
        this.identifier = createIdentifier(name());
        this.mimeType = mimeType;
        this.isTextFormat = isTextFormat;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public boolean isTextFormat()
    {
        return isTextFormat;
    }

    public static FileType lookup(final String fileTypeString)
    {
        for (final FileType fileType : FileType.values())
        {
            if (fileType.identifier.equalsIgnoreCase(fileTypeString))
                return fileType;
        }

        return null;
    }
}
