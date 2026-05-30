package us.sportsanalytics.backend.services;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import us.sportsanalytics.backend.config.StorageProperties;

@Service
@RequiredArgsConstructor
public class S3StorageService {
        private final S3Client s3client;
        private final StorageProperties storageProperties;
        private final S3Presigner presigner;
        private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

        /* Create a presigned URL for the FE to send file directly to s3 */
        public String createPresignedPutUrl(String keyName, Map<String, String> metadata) {

                PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(storageProperties.getBucket())
                                .key(keyName).contentType("text/csv").metadata(metadata).build();

                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                .signatureDuration(Duration.ofMinutes(storageProperties.getPresignedURLDuration()))
                                .putObjectRequest(objectRequest).build();

                // presigner sign the PUT object request with the aws credentials defined under
                // ~/aws/credentials
                PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
                String putURL = presignedRequest.url().toString();
                log.info("Presigned URL to upload a file to: [{}]", putURL);
                log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

                return presignedRequest.url().toExternalForm();
        }

        /*
         * Create a presigned URL for either Airflow/Spark to download file directly
         * from s3
         */
        public String createPresignedGetUrl(String keyName, Map<String, String> metadata) {

                GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(storageProperties.getBucket())
                                .key(keyName).build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                                .signatureDuration(Duration.ofMinutes(storageProperties.getPresignedURLDuration()))
                                .getObjectRequest(objectRequest).build();

                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

                String getURL = presignedRequest.url().toString();
                log.info("Presigned URL: [{}]", getURL);
                log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

                return presignedRequest.url().toExternalForm();
        }

        public InputStream getObjectStream(String keyName) {
                GetObjectRequest request = GetObjectRequest.builder().bucket(storageProperties.getBucket()).key(keyName)
                                // .range("bytes=0-2097152")
                                .build();

                ResponseInputStream<GetObjectResponse> response = s3client.getObject(request);

                return response;
        }
}
