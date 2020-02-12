package com.smartling.api.sdk.file;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.UidAwareFileListItem;

public interface AccountBasedFileApiClient
{

    FileList<UidAwareFileListItem> getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException;

}
