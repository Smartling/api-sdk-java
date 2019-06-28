package com.smartling.api.sdk.projects;

class ResponseExamples
{
    static final String PROJECT_DETAILS_RESPONSE = "{\n"
            + "  \"response\": {\n"
            + "    \"code\": \"SUCCESS\",\n"
            + "    \"data\": {\n"
            + "      \"projectId\": \"testProject\",\n"
            + "      \"projectName\": \"Project Name\",\n"
            + "      \"accountUid\": \"test-account-uid\",\n"
            + "      \"archived\": false,\n"
            + "      \"projectTypeCode\": \"test-projects-type-code\",\n"
            + "      \"sourceLocaleId\": \"test-source-locale-id\",\n"
            + "      \"sourceLocaleDescription\": \"test-source-locale-description\",\n"
            + "      \"targetLocales\": [\n"
            + "        {\n"
            + "          \"localeId\": \"be-BY\",\n"
            + "          \"description\": \"test-locale-be-BY-description\",\n"
            + "          \"enabled\": true\n"
            + "        },\n"
            + "        {\n"
            + "          \"localeId\": \"it-IT\",\n"
            + "          \"description\": \"test-locale-it-IT-description\",\n"
            + "          \"enabled\": false\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  }\n"
            + "}";

    static final String NOT_EXISTING_CODE_RESPONSE = "{\n"
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
