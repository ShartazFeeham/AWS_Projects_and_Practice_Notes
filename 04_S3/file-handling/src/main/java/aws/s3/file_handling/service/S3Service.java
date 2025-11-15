package aws.s3.file_handling.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String awsRegion;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public Map<String, Object> uploadPublicFile(MultipartFile multipartFile) {
        return upload(multipartFile, "public");
    }

    public Map<String, Object> uploadPrivateFile(MultipartFile multipartFile) {
        return upload(multipartFile, "private");
    }

    private Map<String, Object> upload(MultipartFile multipartFile, String folder) {
        File temporaryFile = null;
        try {
            String fileName = getFileName(multipartFile);
            temporaryFile = File.createTempFile(fileName, "");
            multipartFile.transferTo(temporaryFile);

            String s3Key = folder + "/" + fileName;
            PutObjectRequest s3ObjectRequest = new PutObjectRequest(bucketName, s3Key, temporaryFile);
            PutObjectResult s3ObjectResponse = amazonS3.putObject(s3ObjectRequest);

            log.info("Uploading: {}, response: {}", s3ObjectRequest, s3ObjectResponse);

            return Map.of(
                    "status", "SUCCESS",
                    "fileName", fileName,
                    "fileKey", s3Key,
                    "objectUrl", getObjectUrl(s3Key)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error during file upload to S3: " + e.getMessage(), e);
        } finally {
            deleteTempFile(temporaryFile);
        }
    }

    public boolean ifExists(String filePath) {
        try {
            amazonS3.getObjectMetadata(bucketName, filePath);
            return true;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            throw new RuntimeException("Error checking file existence: " + e.getMessage(), e);
        }
    }

    public List<Map<String, String>> listFiles(String prefix) {
        String searchPrefix = prefix.endsWith("/") ? prefix : prefix + "/";

        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result result = amazonS3.listObjectsV2(req);

        return result.getObjectSummaries().stream()
                .map(s3ObjectSummary -> Map.of(
                        "fileName", s3ObjectSummary.getKey().substring(s3ObjectSummary.getKey().lastIndexOf('/') + 1),
                        "fileKey", s3ObjectSummary.getKey(),
                        "objectUrl", getObjectUrl(s3ObjectSummary.getKey())
                ))
                .collect(Collectors.toList());
    }

    public String downloadPublic(String key) {
        if (key.startsWith("http")) {
            try {
                URI uri = new URI(key);
                String path = uri.getPath();
                key = path.substring(path.indexOf('/') + 1);
            } catch (Exception e) {
                log.warn("Invalid URL passed as key: {}", key);
            }
        }

        if (!key.startsWith("public/")) {
            throw new IllegalArgumentException("Access Denied: File key does not start with 'public/'. Use the private download method.");
        }

        return getObjectUrl(key);
    }

    public Map<String, Object> downloadPrivate(String key) {
        if (!key.startsWith("private/")) {
            throw new IllegalArgumentException("Key must point to a private file ('private/').");
        }
        if (!hasAccessPermission()) {
            throw new SecurityException("Access permission denied for this resource.");
        }

        ZonedDateTime allowedUntil = ZonedDateTime.now().plusMinutes(2);
        Date expiration = Date.from(allowedUntil.toInstant());

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        String fileName = key.substring(key.lastIndexOf('/') + 1);

        return Map.of(
                "status", "SUCCESS",
                "fileName", fileName,
                "fileKey", key,
                "objectUrl", getObjectUrl(key),
                "presignedUrl", url.toString(),
                "allowedUntil", allowedUntil.toString()
        );
    }

    private boolean hasAccessPermission() {
        return ThreadLocalRandom.current().nextInt(100) < 60;
    }

    private String getObjectUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, awsRegion, key);
    }

    private String getFileName(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null) {
            fileName = "untitled";
        }
        return UUID.randomUUID().toString().substring(0, 5) + "_" + fileName;
    }

    private static void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            log.info("Deleted temp file: {}", deleted);
        }
    }
}