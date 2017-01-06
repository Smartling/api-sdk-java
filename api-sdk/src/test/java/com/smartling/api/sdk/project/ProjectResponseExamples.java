package com.smartling.api.sdk.project;

public class ProjectResponseExamples {
    public static final String PROJECT_DETAILS_RESPONSE = "{\n"
            + "  \"response\": {\n"
            + "      \"code\": \"SUCCESS\",\n"
            + "      \"data\": {\n"
            + "         \"projectId\": \"2249fadc3\",\n"
            + "         \"projectName\": \"Project 111\",\n"
            + "         \"accountUid\": \"48ade9f989\",\n"
            + "         \"sourceLocaleId\": \"en-US\",\n"
            + "         \"sourceLocaleDescription\": \"English (United States)\",\n"
            + "         \"archived\": \"false\",\n"
            + "         \"targetLocales\": [{\n"
            + "             \"localeId\": \"de-DE\",\n"
            + "             \"description\": \"German (Germany)\""
            + "         }]\n"
            + "      }\n"
            + "   }\n"
            + "}\n";

    public static final String PROJECT_DETAILS_BAD_RESPONSE = "{\n"
            + "  \"response\": {\n"
            + "      \"code\": \"NOT_EXISTING_CODE\",\n"
            + "      \"errors\": [\n"
            + "         {"
            + "          \"message\": \"Invalid token\",\n"
            + "          \"key\": \"invalid_token\",\n"
            + "          \"details\": {}\n"
            + "         }"
            + "     ]\n"
            + "}\n"
            + "}";
}
