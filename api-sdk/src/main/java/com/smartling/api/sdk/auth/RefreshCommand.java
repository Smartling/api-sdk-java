package com.smartling.api.sdk.auth;

import java.util.Objects;

public class RefreshCommand
{
    private final String refreshToken;

    public RefreshCommand(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RefreshCommand that = (RefreshCommand) o;
        return Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(refreshToken);
    }

    @Override
    public String toString()
    {
        return "RefreshCommand{" +
                "refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
