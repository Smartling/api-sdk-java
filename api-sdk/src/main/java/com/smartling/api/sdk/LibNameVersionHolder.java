package com.smartling.api.sdk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Properties;

public class LibNameVersionHolder
{
    private static String clientLibName = LibNameVersionPropertiesHolder.defaultClientLibName();

    private static String clientLibVersion = LibNameVersionPropertiesHolder.defaultClientLibVersion();

    public static String getClientLibName()
    {
        return clientLibName;
    }

    public static void setClientLibName(String clientLibName)
    {
        LibNameVersionHolder.clientLibName = clientLibName;
    }

    public static String getClientLibVersion()
    {
        return clientLibVersion;
    }

    public static void setClientLibVersion(String clientLibVersion)
    {
        LibNameVersionHolder.clientLibVersion = clientLibVersion;
    }

    private static class LibNameVersionPropertiesHolder
    {
        private static final Log LOGGER = LogFactory.getLog(LibNameVersionPropertiesHolder.class);

        private static final Properties PROPERTIES;
        private static final String PROJECT_PROPERTIES_FILE = "sdk-project.properties";
        private static final String PROJECT_VERSION = "version";
        private static final String PROJECT_ARTIFACT_ID = "artifactId";

        static
        {
            PROPERTIES = new Properties();
            try
            {
                PROPERTIES.load(LibNameVersionPropertiesHolder.class.getClassLoader().getResourceAsStream(PROJECT_PROPERTIES_FILE));
            }
            catch (IOException e)
            {
                LOGGER.error(String.format("Could not read properties file=%s", PROJECT_PROPERTIES_FILE));
                throw new RuntimeException(e);
            }
        }

        static String defaultClientLibName()
        {
            return LibNameVersionPropertiesHolder.PROPERTIES.getProperty(PROJECT_ARTIFACT_ID);
        }

        static String defaultClientLibVersion()
        {
            return LibNameVersionPropertiesHolder.PROPERTIES.getProperty(PROJECT_VERSION);
        }
    }
}
