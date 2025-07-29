package blogpj.blog.controller;

import blogpj.blog.domain.User;
import blogpj.blog.dto.UserLoginRequestDTO;
import blogpj.blog.dto.UserLoginResponseDTO;
import blogpj.blog.dto.UserRegisterRequestDTO;
import blogpj.blog.dto.UserRegisterResponseDTO;
import blogpj.blog.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 존재하는 사용자입니다."));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setJoinDate(LocalDateTime.now());  // 가입일 설정

        userRepository.save(user);

        UserRegisterResponseDTO responseDTO = new UserRegisterResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname()
        );

        return ResponseEntity.ok(responseDTO);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequestDTO request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "존재하지 않는 사용자입니다."));
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "비밀번호가 일치하지 않습니다."));
        }

        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO(
                user.getUsername(),
                user.getNickname()
        );

        return ResponseEntity.ok(responseDTO);
    }

    // 프로필 정보 가져오기 (닉네임, 이미지 경로, 입도일)
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        String profileImage = user.getProfileImagePath() != null ? user.getProfileImagePath() : "/images/default-profile.png";

        Map<String, Object> response = Map.of(
                "nickname", user.getNickname(),
                "profileImagePath", profileImage,
                "joinDate", user.getJoinDate()
        );

        return ResponseEntity.ok(response);
    }
}
