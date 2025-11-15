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


    @PostMapping("/upload/public")
    public Map<String, Object> uploadFilePublic(@RequestParam("file") MultipartFile file) {
        return s3Service.uploadPublicFile(file);
    }

    @PostMapping("/upload/private")
    public Map<String, Object> uploadFilePrivate(@RequestParam("file") MultipartFile file) {
        return s3Service.uploadPrivateFile(file);
    }

    @GetMapping("/exists")
    public Map<String, Boolean> ifExists(@RequestParam("key") String fileKey) {
        boolean exists = s3Service.ifExists(fileKey);
        return Map.of("exists", exists);
    }

    @GetMapping("/list")
    public List<Map<String, String>> listFiles(@RequestParam("prefix") String prefix) {
        return s3Service.listFiles(prefix);
    }

    @GetMapping("/download/public")
    public RedirectView downloadPublic(@RequestParam("key") String fileKeyOrUrl) {
        String publicUrl = s3Service.downloadPublic(fileKeyOrUrl);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(publicUrl);

        return redirectView;
    }

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
