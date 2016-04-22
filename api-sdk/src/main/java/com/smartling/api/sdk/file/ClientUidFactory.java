package com.smartling.api.sdk.file;

import com.google.gson.JsonObject;

class ClientUidFactory
{
    private static final String CLIENT_KEY = "client";
    private static final String VERSION_KEY = "version";

    public static String clientUid(String name, String version)
    {
        JsonObject object = new JsonObject();
        object.addProperty(CLIENT_KEY, name);
        object.addProperty(VERSION_KEY, version);
        return object.toString();
    }
}
