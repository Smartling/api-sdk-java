/* Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for handling dates with the Smartling Translation API.
 */
public abstract class DateFormatter
{
    /** Date format used by the Smartling Translation API */
    private static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private DateFormatter()
    {
    }

    /**
     * Simple method to format a date into the string format used by the Smartling Translation API.
     *
     * @param date the date to format
     * @return formatted string version of the date or null if the date is null.
     */
    public static String format(Date date)
    {
        if (null == date)
            return null;

        return getDateFormat().format(date);
    }

    /**
     * Simple method to parse a date from the string format used by the Smartling Translation API.
     *
     * @param date the string to parse
     * @return parsed date version of the string or null if the string is null.
     * @throws ParseException parse error
     */
    public static Date parse(String date) throws ParseException
    {
        if (null == date)
            return null;

        return getDateFormat().parse(date);
    }

    private static DateFormat getDateFormat()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(API_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
}
