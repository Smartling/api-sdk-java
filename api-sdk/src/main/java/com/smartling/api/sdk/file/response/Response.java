package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.util.HttpUtils;
import com.smartling.web.api.v2.ResponseCode;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Response<T extends ResponseData> {
    private ResponseCode code;
    private List<Error> errors;
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
        if (this.code != ResponseCode.SUCCESS && this.code != ResponseCode.ACCEPTED)
        {
            List<String> messages = new ArrayList<>(errors.size());
            for(Error error : errors) messages.add(error.toString());
            throw new SmartlingApiException(code.toString()+ '\n' + StringUtils.join(messages, '\n'), errors);
        }
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
