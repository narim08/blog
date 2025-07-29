package blogpj.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileImageController {

    // 실제 파일 저장 경로 (윈도우 기준 외부 폴더)
    private final String uploadDir = "C:/profile-images";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        // 폴더가 없으면 생성
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 파일명 설정
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + extension;

        // 저장
        File destination = new File(uploadDir, newFilename);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 저장 실패: " + e.getMessage());
        }

        // 브라우저에서 접근 가능한 경로
        String imageUrl = "/profile-images/" + newFilename;
        return ResponseEntity.ok().body(imageUrl);
    }
}
