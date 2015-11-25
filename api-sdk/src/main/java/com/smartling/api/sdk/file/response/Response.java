package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.web.api.v2.ResponseCode;
import com.smartling.web.api.v2.ResponseData;
import com.smartling.web.api.v2.Error;
import java.util.ArrayList;
import java.util.List;

public class Response<T extends ResponseData> {
    private ResponseCode code;
    private List<com.smartling.web.api.v2.Error> errors;
    private T data;

    public Response() {
    }

    public Response(ResponseCode code, List<Error> errors, T data) {
        this.code = code;
        this.errors = errors;
        this.data = data;
    }

    public ResponseCode getCode() {
        return this.code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

    public List<Error> getErrors() {
        return this.errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public T getData()
    {
        return data;
    }

    public T retrieveData() throws SmartlingApiException
    {
        if (this.data == null && this.errors != null && !errors.isEmpty())
        {
            List<String> messages = new ArrayList<>(errors.size());
            for(Error error : errors) messages.add(error.toString());
            throw new SmartlingApiException(code.toString(), messages);
        }
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
