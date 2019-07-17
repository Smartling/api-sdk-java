package com.smartling.api.sdk.file;

import org.junit.Assert;
import org.junit.Test;

import static com.smartling.api.sdk.file.FileType.JAVA_PROPERTIES;

public class FileTypeTest
{
    @Test
    public void testLookup()
    {
        Assert.assertEquals(JAVA_PROPERTIES, FileType.lookup("javaProperties"));
        Assert.assertEquals(JAVA_PROPERTIES, FileType.lookup("JaVaPrOpErTiEs"));
        Assert.assertEquals(JAVA_PROPERTIES, FileType.lookup("JaVa_PrOpErTiEs"));
        Assert.assertEquals(JAVA_PROPERTIES, FileType.lookup(JAVA_PROPERTIES.name()));
        Assert.assertEquals(JAVA_PROPERTIES, FileType.lookup(JAVA_PROPERTIES.toString()));
        Assert.assertNull(FileType.lookup("A_NON_EXISTING_CONTENT_TYPE"));
    }
}