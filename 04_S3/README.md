### S3 java client integration

**Configuration**
```java
package aws.s3.file_handling.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${aws.accessKeyId}")
    private String awsAccessKeyId;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
```

**End points**
```java
public class S3Controller {

    private final S3Service s3Service;

    // Uploads a file and stores it in the public/ S3 folder.
    @PostMapping("/upload/public")
    public Map<String, Object> uploadFilePublic(@RequestParam("file") MultipartFile file) {

    // Uploads a file and stores it in the private/ S3 folder.
    @PostMapping("/upload/private")
    public Map<String, Object> uploadFilePrivate(@RequestParam("file") MultipartFile file) {

    // Checks if a file exists in S3 using its full key (path).
    @GetMapping("/exists")
    public Map<String, Boolean> ifExists(@RequestParam("key") String fileKey) {

    // Lists all files in S3 that match the given prefix (e.g., 'public/' or 'p').
    @GetMapping("/list")
    public List<Map<String, String>> listFiles(@RequestParam("prefix") String prefix) {

    // Redirects the client to the direct public S3 URL for download.
    @GetMapping("/download/public")
    public RedirectView downloadPublic(@RequestParam("key") String fileKeyOrUrl) {

    // Generates a time-limited pre-signed URL for a file in the private/ S3 folder.
    @GetMapping("/download/private")
    public ResponseEntity<Map<String, Object>> downloadPrivate(@RequestParam("key") String fileKey){
}
```

### Bucket policy
To restrict access on public and private folder, I updated the bucket policy. 
- Initially set `Block all public access` to `off`
- Then set this following policy - 
```bash
{ 
  # Specifies the language version for the policy
  "Version": "2012-10-17", 
  "Statement": [ 
    { 
      # The first statement (for Public access)
      "Sid": "AllowPublicReadAccessToPublicFolder", # Unique identifier for this statement
      "Effect": "Allow", # Specifies that the policy allows the defined actions
      "Principal": "*", # Applies to all users and anonymous requests (the public internet)
      "Action": "s3:GetObject", # The action being allowed: retrieving (reading/downloading) an object
      "Resource": "arn:aws:s3:::feeham-test-bkt/public/*" # The bucket objects targeted: all objects under the 'public/' prefix
    }, 
    { 
      # The second statement (for Private access denial)
      "Sid": "DenyPublicReadAccessToPrivateFolder", # Unique identifier for this statement
      "Effect": "Deny", # Specifies that the policy explicitly denies the defined actions
      "Principal": "*", # Applies to all users and anonymous requests
      "Action": "s3:GetObject", # The action being denied: retrieving an object
      "Resource": "arn:aws:s3:::feeham-test-bkt/private/*", # The bucket objects targeted: all objects under the 'private/' prefix
      "Condition": { # Starts the condition block for this denial
        "StringNotEquals": { 
          # Why the Condition?: Without it, the "Deny" applies to EVERYONE (*), 
          # including the user Feeham when generating a Pre-Signed URL.
          # Consequence: The Explicit Deny would override the URL's temporary ALLOW, 
          # making pre-signed URLs unusable for private files.
          # But now stringNotEquals exclude this user and lets him create pre-signed url.
          "aws:PrincipalArn": "arn:aws:iam::758027491688:user/Feeham"
        } 
      } 
    } 
  ] 
} 
```
- Now /public folder items are directly accessible and public.
- /private folder requires presigned url with limited time to be accessible. 
- our endpoint for download/private use a dummy access check then returns with presigned url like this response
```json
{
    "fileName": "02c80_apple.jpg",
    "presignedUrl": "https://feeham-test-bkt.s3.ap-southeast-1.amazonaws.com/private/02c80_apple.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251115T135347Z&X-Amz-SignedHeaders=host&X-Amz-Expires=119&X-Amz-Credential=AKIA3A7PVTFUKPC5JFAU%2F20251115%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Signature=9af3006102d2b6d01fc23f1a01a38b599d5df834462d81d49dc556f182392b34",
    "objectUrl": "https://feeham-test-bkt.s3.ap-southeast-1.amazonaws.com/private/02c80_apple.jpg",
    "status": "SUCCESS",
    "fileKey": "private/02c80_apple.jpg",
    "allowedUntil": "2025-11-15T19:55:47.379730+06:00[Asia/Dhaka]"
}
```