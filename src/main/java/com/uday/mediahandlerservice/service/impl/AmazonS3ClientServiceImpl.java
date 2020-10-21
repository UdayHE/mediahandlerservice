package com.uday.mediahandlerservice.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.uday.mediahandlerservice.service.AmazonS3ClientService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {
    private String awsS3Bucket;
    private AmazonS3 amazonS3;
    private static final Logger logger = LoggerFactory.getLogger(AmazonS3ClientServiceImpl.class);

    @Autowired
    public AmazonS3ClientServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, String awsS3Bucket) {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3Bucket = awsS3Bucket;
    }

    @SneakyThrows
    @Async
    public void uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        File file = null;
        FileOutputStream fos = null;

        try {
            file = new File(fileName);
            fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());


            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            this.amazonS3.putObject(putObjectRequest);

        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
        } finally {
            fos.close();
            file.delete();
        }
    }

    @Async
    public void deleteFileFromS3Bucket(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(awsS3Bucket, fileName));
        } catch (AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
        }
    }

    @Async
    public byte[] downloadFileFromS3Bucket(String fileName) {
        try (S3Object obj = amazonS3.getObject(awsS3Bucket, fileName);
             S3ObjectInputStream stream = obj.getObjectContent();)
            {
                byte[] content = IOUtils.toByteArray(stream);
                return content;
        } catch (IOException e) {
            logger.error("error [" + e.getMessage() + "] occurred while downloading [" + fileName + "] ");
        }

        return null;
    }
}