package com.smartling.api.sdk.file.parameters;


import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Properties;

class ProjectPropertiesHolder
{

    private static final Log LOGGER = LogFactory.getLog(ProjectPropertiesHolder.class);

    private static final String PROJECT_PROPERTIES_FILE = "sdk-project.properties";
    private static final String PROJECT_VERSION = "version";
    private static final String PROJECT_ARTIFACT_ID = "artifactId";

    private static final String CLIENT_KEY = "client";
    private static final String VERSION_KEY = "version";

    static String defaultClientUid()
    {
        return clientUid(Holder.PROPERTIES.getProperty(PROJECT_ARTIFACT_ID), Holder.PROPERTIES.getProperty(PROJECT_VERSION));
    }

    static String clientUid(String name, String version)
    {
        JsonObject object = new JsonObject();
        object.addProperty(CLIENT_KEY, name);
        object.addProperty(VERSION_KEY, version);
        return object.toString();
    }

    private static class Holder
    {
        private static final Properties PROPERTIES;

        static {
            PROPERTIES = new Properties();
            try
            {
                PROPERTIES.load(Holder.class.getClassLoader().getResourceAsStream(PROJECT_PROPERTIES_FILE));
            } catch (IOException e)
            {
                LOGGER.error(String.format("Could not read properties file=%s",PROJECT_PROPERTIES_FILE));
                throw new RuntimeException(e);
            }
        }
    }

}
