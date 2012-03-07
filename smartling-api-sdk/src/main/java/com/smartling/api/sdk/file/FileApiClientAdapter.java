package com.smartling.api.sdk.file;

import com.smartling.api.sdk.file.FileApiException;


public interface FileApiClientAdapter
{
    /**
     * Uploads a file for translation to the Smartling File API.
     *
     * @param fileType the type of file to upload
     * @param fileUri the identifier of the file
     * @param fileName the full path of the file
     * @param fileEncoding the encoding of the file. Can be null but best if encoding is specified.
     * @return success string returned
     * @throws FileApiException if an exception or non success is returned from the file api.
     */
    String uploadFile(String fileType, String fileUri, String fileName, String fileEncoding) throws FileApiException;

    /**
     * Get the translated (or original file).
     *
     * @param fileUri the identifier of the file
     * @param locale the locale to retrieve the translation for, or null to request the original file.
     * @return the contents of the requested file.
     * @throws FileApiException if an exception or non success is returned from the file api.
     */
    String getFile(String fileUri, String locale) throws FileApiException;

    /**
     * Get the listing of original files from the file api.
     *
     * @return listing of files
     * @throws FileApiException if an exception or non success is returned from the file api.
     */
    String getFilesList() throws FileApiException;

    /**
     * Get the listing of translated files for the specified locale.
     *
     * @param locale the locale
     * @return listing of translated files.
     * @throws FileApiException if an exception or non success is returned from the file api.
     */
    String getFilesList(String locale) throws FileApiException;

    /**
     * Get the status of a file for the specified locale
     *
     * @param fileUri the identifier of the file
     * @param locale the locale
     * @return status of the file
     * @throws FileApiException if an exception or non success is returned from the file api.
     */
    String getFileStatus(String fileUri, String locale) throws FileApiException;

}