package blogpj.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter    //Lombok 라이브러리
@NoArgsConstructor //기본 생성자 자동 생성
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동 ++됨
    private Long id; //테이블 고유 id

    @Column(nullable = false, length = 255)
    private String title;

    /*@Column(nullable = false, length = 100)
    private String userName;*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT 타입
    private String content;

    @Column(name = "tag")
    private String tag;

    @Column(nullable = false, updatable = false) //생성 시간은 수정 불가
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    //1:N에서 1쪽은 매핑되었다는 걸 써줘야 됨
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true) //연쇄 삭제 설정
    private List<Comment> comments = new ArrayList<>(); //한개의 게시글은 여러개의 댓글을 list로 보관

    //조회수 필드 추가
    @Column(nullable = false)
    private int viewCount = 0;

    public void increaseViewCount() {
        this.viewCount++;
    }

    //좋아요 필드 추가
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeEntity> likes = new ArrayList<>();

    public int getLikeCount() {
        return likes.size();
    }

}

