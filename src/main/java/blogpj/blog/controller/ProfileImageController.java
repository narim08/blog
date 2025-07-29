package blogpj.blog.controller;

import blogpj.blog.domain.User;
import blogpj.blog.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
public class ProfileImageController {

    private final String uploadDir = "C:/profile-images";

    private final UserRepository userRepository;

    public ProfileImageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile file,
                                                @RequestParam("username") String username) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if(dotIndex > -1) {
            extension = originalFilename.substring(dotIndex);
        }

        String newFilename = username + extension;

        File destination = new File(uploadDir, newFilename);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 저장 실패: " + e.getMessage());
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.setProfileImagePath("/profile-images/" + newFilename);
        userRepository.save(user);

        String imageUrl = "/profile-images/" + newFilename;
        return ResponseEntity.ok().body(imageUrl);
    }
}
