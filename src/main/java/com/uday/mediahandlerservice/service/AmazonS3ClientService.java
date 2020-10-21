package com.uday.mediahandlerservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AmazonS3ClientService
{
    void uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess) throws IOException;

    void deleteFileFromS3Bucket(String fileName);

    byte[] downloadFileFromS3Bucket(String fileName);
}
