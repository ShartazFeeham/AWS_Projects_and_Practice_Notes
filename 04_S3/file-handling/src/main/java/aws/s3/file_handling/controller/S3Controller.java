package aws.s3.file_handling.controller;

import aws.s3.file_handling.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    // Uploads a file and stores it in the public/ S3 folder.
    @PostMapping("/upload/public")
    public Map<String, Object> uploadFilePublic(@RequestParam("file") MultipartFile file) {
        return s3Service.uploadPublicFile(file);
    }

    // Uploads a file and stores it in the private/ S3 folder.
    @PostMapping("/upload/private")
    public Map<String, Object> uploadFilePrivate(@RequestParam("file") MultipartFile file) {
        return s3Service.uploadPrivateFile(file);
    }

    // Checks if a file exists in S3 using its full key (path).
    @GetMapping("/exists")
    public Map<String, Boolean> ifExists(@RequestParam("key") String fileKey) {
        boolean exists = s3Service.ifExists(fileKey);
        return Map.of("exists", exists);
    }

    // Lists all files in S3 that match the given prefix (e.g., 'public/' or 'p').
    @GetMapping("/list")
    public List<Map<String, String>> listFiles(@RequestParam("prefix") String prefix) {
        return s3Service.listFiles(prefix);
    }

    // Redirects the client to the direct public S3 URL for download.
    @GetMapping("/download/public")
    public RedirectView downloadPublic(@RequestParam("key") String fileKeyOrUrl) {
        String publicUrl = s3Service.downloadPublic(fileKeyOrUrl);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(publicUrl);

        return redirectView;
    }

    // Generates a time-limited pre-signed URL for a file in the private/ S3 folder.
    @GetMapping("/download/private")
    public ResponseEntity<Map<String, Object>> downloadPrivate(@RequestParam("key") String fileKey) {
        try {
            Map<String, Object> response = s3Service.downloadPrivate(fileKey);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("status", "DENIED", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("status", "ERROR", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "ERROR", "message", "Internal server error: " + e.getMessage()));
        }
    }

}