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
package com.smartling.api.sdk.file;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling dates with the Smartling API.
 */
public class DateFormatter
{
    /** Date format used by the Smartling API */
    private static final String API_DATE_FORMAT = "YYYY-MM-DDThh:mm:ss";

    /**
     * Simple method to format a date into the string format used by the Smartling API.
     *
     * @param date the date to format
     * @return formatted string version of the date or null if the date is null.
     */
    public static final String formatDate(Date date)
    {
        if (null == date)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT);
        return sdf.format(date);
    }
}
