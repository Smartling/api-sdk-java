package com.smartling.api.sdk.file.parameters;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Properties;

class ProjectPropertiesHolder {

    private static final Log logger = LogFactory.getLog(ProjectPropertiesHolder.class);

    private static final String PROJECT_PROPERTIES_FILE = "sdk-project.properties";
    private static final String PROJECT_VERSION = "version";
    private static final String PROJECT_ARTIFACT_ID = "artifactId";

    private static final String CLIENT_KEY = "client";
    private static final String VERSION_KEY = "version";

    static String defaultClientUid() {
        StringBuilder builder = new StringBuilder();
        Properties properties = Holder.PROPERTIES;
        builder
                .append("{")
                    .append("\"").append(CLIENT_KEY).append("\"")
                    .append(":")
                    .append("\"").append(properties.getProperty(PROJECT_ARTIFACT_ID)).append("\"")
                .append(",")
                    .append("\"").append(VERSION_KEY).append("\"")
                    .append(":")
                    .append("\"").append(properties.getProperty(PROJECT_VERSION)).append("\"")
                .append("}");
        return builder.toString();
    }

    private static class Holder {
        private static final Properties PROPERTIES;

        static {
            PROPERTIES = new Properties();
            try {
                PROPERTIES.load(Holder.class.getClassLoader().getResourceAsStream(PROJECT_PROPERTIES_FILE));
            } catch (IOException e) {
                logger.error("Could not read properties file="+ PROJECT_PROPERTIES_FILE);
                throw new RuntimeException(e);
            }
        }
    }

}
