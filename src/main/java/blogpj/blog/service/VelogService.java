package blogpj.blog.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VelogService {

    @Getter
    public static class VelogPost {
        private final String title;
        private final String link;

        public VelogPost(String title, String link) {
            this.title = title;
            this.link = link;
        }
    }

    private final List<VelogPost> allPosts = Arrays.asList(
            // 1
            new VelogPost("🚀 당근에서의 6개월, 나는 얼마나 달라졌나", "https://velog.io/@wuzoo/%EB%8B%B9%EA%B7%BC%EC%97%90%EC%84%9C%EC%9D%98-6%EA%B0%9C%EC%9B%94-%EB%82%98%EB%8A%94-%EC%96%BC%EB%A7%88%EB%82%98-%EB%8B%AC%EB%9D%BC%EC%A1%8C%EB%82%98"),
            new VelogPost("삼성전자 퇴사하고 2년 뒤에 에이블리 개발자 된 썰", "https://velog.io/@kokomen/%EC%82%BC%EC%84%B1%EC%A0%84%EC%9E%90-%ED%87%B4%EC%82%AC%ED%95%98%EA%B3%A0-2%EB%85%84-%EB%92%A4%EC%97%90-%EC%97%90%EC%9D%B4%EB%B8%94%EB%A6%AC-%EA%B0%9C%EB%B0%9C%EC%9E%90-%EB%90%9C-%EC%8D%B0"),
            new VelogPost("Next.js의 전체 페이지 캐싱: 신선도를 잃지 않고 SSR 페이지를 캐시하는 방법", "https://velog.io/@surim014/full-page-caching-in-nextjs-how-to-cache-ssr-pages-without-losing-freshness-1icc"),
            new VelogPost("시멘틱 HTML이 여전히 중요한 이유", "https://velog.io/@tap_kim/why-semantic-html-still-matters"),
            new VelogPost("자바스크립트의 논리 할당 연산자: 간단한 문법, 큰 이점", "https://velog.io/@superlipbalm/logical-assignment-operators-in-javascript-small-syntax-big-wins"),

            // 2
            new VelogPost("프론트엔드 자동화, 모니터링", "https://velog.io/@kennys/%ED%94%84%EB%A1%A0%ED%8A%B8%EC%97%94%EB%93%9C-%EC%9E%90%EB%8F%99%ED%99%94-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81"),
            new VelogPost("구글 코리아 코딩 인터뷰 본 후기", "https://velog.io/@making-a-scene/%EA%B5%AC%EA%B8%80-%EC%BD%94%EB%A6%AC%EC%95%84-%EC%BD%94%EB%94%A9-%EC%9D%B8%ED%84%B0%EB%B7%B0-%EB%B3%B8-%ED%9B%84%EA%B8%B0%EA%B0%99%EC%9D%80-%EC%9D%BC%EA%B8%B0"),
            new VelogPost("LLM - OpenAI 가 알려주는 할루시네이션의 이유?", "https://velog.io/@qlgks1/why-language-models-hallucinate"),
            new VelogPost("왜 스타트업은 Node.js를 많이 쓸까?", "https://velog.io/@chae0738/%EC%99%9C-%EC%8A%A4%ED%83%80%ED%8A%B8%EC%97%85%EC%9D%80-Node.js%EB%A5%BC-%EB%A7%8E%EC%9D%B4-%EC%93%B8%EA%B9%8C"),
            new VelogPost("[웹 인터랙션] 2. 명령형과 선언형의 차이", "https://velog.io/@dltldn333/%EC%9B%B9-%EC%9D%B8%ED%84%B0%EB%9E%99%EC%85%98-%EB%B0%91%EB%B0%94%EB%8B%A5%EB%B6%80%ED%84%B0-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B02%EB%91%90%EA%B0%80%EC%A7%80-%EB%B0%A9%EC%8B%9D%EC%9D%98-%EC%95%A0%EB%8B%88%EB%A9%94%EC%9D%B4%EC%85%98")
    );

    public List<VelogPost> getTopPosts() {
        // 전체 포스트에서 랜덤하게 5개 선택
        List<VelogPost> shuffled = new ArrayList<>(allPosts);
        Collections.shuffle(shuffled);

        return shuffled.subList(0, Math.min(5, shuffled.size()));
    }
}