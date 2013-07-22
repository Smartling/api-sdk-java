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
package com.smartling.api.sdk.file.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * {@link Date} type adapter for Gson
 */
public class DateTypeAdapter extends TypeAdapter<Date>
{
    public Date read(JsonReader reader) throws IOException
    {
        if (reader.peek() == JsonToken.NULL)
        {
            reader.nextNull();
            return null;
        }

        String value = reader.nextString();
        try
        {
            return DateFormatter.parse(value);
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }
    }

    public void write(JsonWriter writer, Date value) throws IOException
    {
        if (value == null)
        {
            writer.nullValue();
            return;
        }

        String xy = DateFormatter.format(value);
        writer.value(xy);
    }
}
