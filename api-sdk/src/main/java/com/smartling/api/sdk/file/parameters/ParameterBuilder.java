package com.smartling.api.sdk.file.parameters;

import org.apache.http.NameValuePair;

import java.util.List;

public interface ParameterBuilder {
    List<NameValuePair> getNameValueList();
}
