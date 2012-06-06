package com.smartling.api.sdk.file;

public enum FileType
{
    JAVA_PROPERTIES("javaProperties"),  // Java resources
    IOS("ios"),                         // iOS resources
    ANDROID("android"),                 // Android resources
    GETTEXT("gettext"),                 // GetText .PO/.POT file
    XLIFF("xliff"),                     // XLIFF file
    YAML("yaml"),                       // Ruby/YAML file
    JSON("json");                       // generic JSON file

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
        for(FileType fileType : FileType.values())
        {
            if (fileType.identifier.equalsIgnoreCase(fileTypeString))
                return fileType;
        }

        return null;
    }
}