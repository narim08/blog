package blogpj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserPostRankDTO {
    private int rank;
    private String username;
    private long postCount;
}