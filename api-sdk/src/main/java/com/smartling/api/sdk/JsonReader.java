package com.smartling.api.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.ApiResponseWrapper;
import com.smartling.api.sdk.dto.Data;
import com.smartling.api.sdk.util.DateTypeAdapter;

import java.util.Date;

public class JsonReader {
        public static <T extends Data> ApiResponse<T> parseApiResponse(String response, TypeToken<ApiResponseWrapper<T>> responseType) {
            final GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

            final Gson gson = builder.create();
            final ApiResponseWrapper<T> responseWrapper = gson.fromJson(response, responseType.getType());

            return responseWrapper.getResponse();
        }
}
