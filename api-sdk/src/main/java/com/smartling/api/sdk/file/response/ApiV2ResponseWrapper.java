package com.smartling.api.sdk.file.response;

import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ApiV2ResponseWrapper<T extends ResponseData>
{
    private Response<T> response;

    /**
     * Retrieve the response.
     *
     * @return the response.
     */
    public Response<T> getResponse()
    {
        return response;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("response", getResponse()).toString();
    }
}
