package com.blog.demo.common.config;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.blog.demo.post.entity.Post;
import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.post.repository.PostRepository;
import com.blog.demo.profile.entity.AuthorProfile;
import com.blog.demo.profile.repository.AuthorProfileRepository;
import com.blog.demo.series.entity.Series;
import com.blog.demo.series.entity.SeriesPostItem;
import com.blog.demo.series.repository.SeriesPostItemRepository;
import com.blog.demo.series.repository.SeriesRepository;
import com.blog.demo.topic.entity.Topic;
import com.blog.demo.topic.repository.TopicRepository;

import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final TopicRepository topicRepository;
    private final SeriesRepository seriesRepository;
    private final SeriesPostItemRepository seriesPostItemRepository;
    private final PostRepository postRepository;
    private final AuthorProfileRepository authorProfileRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (topicRepository.count() > 0) {
            log.info("DataSeeder: data already seeded, skipping.");
            return;
        }

        List<AuthorProfile> profiles = authorProfileRepository.findAll();
        if (profiles.isEmpty()) {
            log.warn("DataSeeder: no author profile found, skipping seed data.");
            return;
        }
        AuthorProfile profile = profiles.get(0);

        // ── Topics ──────────────────────────────────────────────────────────
        Topic java = topicRepository.save(new Topic("Java", "java",
                "Ngôn ngữ lập trình Java, JVM ecosystem và các thư viện liên quan."));
        Topic springBoot = topicRepository.save(new Topic("Spring Boot", "spring-boot",
                "Framework Java phổ biến để xây dựng ứng dụng web và microservices."));
        Topic reactjs = topicRepository.save(new Topic("ReactJS", "reactjs",
                "Thư viện JavaScript của Meta để xây dựng giao diện người dùng hiện đại."));
        Topic database = topicRepository.save(new Topic("Database", "database",
                "Cơ sở dữ liệu quan hệ và phi quan hệ, thiết kế schema và tối ưu truy vấn."));
        Topic systemDesign = topicRepository.save(new Topic("System Design", "system-design",
                "Kiến trúc và thiết kế hệ thống phân tán có khả năng mở rộng quy mô lớn."));

        // ── Series ──────────────────────────────────────────────────────────
        Series seriesSpring = seriesRepository.save(new Series(
                "Spring Boot từ cơ bản đến nâng cao",
                "spring-boot-tu-co-ban-den-nang-cao",
                "Series học Spring Boot toàn diện: từ Hello World đến kiến trúc production-ready. Phù hợp cho lập trình viên Java muốn xây dựng backend hiện đại."));
        Series seriesSD = seriesRepository.save(new Series(
                "Thiết kế hệ thống thực tế",
                "thiet-ke-he-thong-thuc-te",
                "Phân tích và thiết kế các hệ thống phổ biến trong ngành: URL Shortener, Newsfeed, Chat... Chuẩn bị cho System Design Interview."));

        // ── Posts ────────────────────────────────────────────────────────────
        LocalDateTime now = LocalDateTime.now();

        // Series Spring Boot — 6 posts
        Post p1 = post(profile, "Spring Boot là gì? Tại sao nên học Spring Boot?",
                "spring-boot-la-gi", Set.of(java, springBoot), now.minusDays(90), MD_P01);
        Post p2 = post(profile, "Cấu trúc dự án Spring Boot theo Package-by-Feature",
                "cau-truc-du-an-spring-boot", Set.of(springBoot), now.minusDays(83), MD_P02);
        Post p3 = post(profile, "Spring Data JPA: CRUD cơ bản và Repository Pattern",
                "spring-data-jpa-co-ban", Set.of(springBoot, database), now.minusDays(76), MD_P03);
        Post p4 = post(profile, "Xử lý ngoại lệ trong Spring Boot với @ControllerAdvice",
                "xu-ly-ngoai-le-spring-boot", Set.of(springBoot, java), now.minusDays(69), MD_P04);
        Post p5 = post(profile, "Spring Security và JWT: Xác thực Stateless",
                "spring-security-jwt", Set.of(springBoot), now.minusDays(62), MD_P05);
        Post p6 = post(profile, "Các kỹ thuật tối ưu hiệu năng ứng dụng Spring Boot",
                "toi-uu-hieu-nang-spring-boot", Set.of(springBoot, database), now.minusDays(55), MD_P06);

        // Series System Design — 4 posts
        Post p7 = post(profile, "System Design là gì? Tại sao mọi lập trình viên cần học?",
                "system-design-la-gi", Set.of(systemDesign), now.minusDays(48), MD_P07);
        Post p8 = post(profile, "Thiết kế hệ thống URL Shortener như bit.ly",
                "thiet-ke-url-shortener", Set.of(systemDesign, database), now.minusDays(41), MD_P08);
        Post p9 = post(profile, "Database Sharding: Chiến lược mở rộng cơ sở dữ liệu",
                "database-sharding", Set.of(systemDesign, database), now.minusDays(34), MD_P09);
        Post p10 = post(profile, "Microservices vs Monolith: Khi nào nên chuyển đổi?",
                "microservices-vs-monolith", Set.of(systemDesign), now.minusDays(27), MD_P10);

        // Standalone posts — 5 posts
        post(profile, "Java Stream API: Xử lý Collection hiệu quả",
                "java-stream-api", Set.of(java), now.minusDays(21), MD_P11);
        post(profile, "React Hooks: useState và useEffect từ A đến Z",
                "react-hooks-usestate-useeffect", Set.of(reactjs), now.minusDays(18), MD_P12);
        post(profile, "SQL vs NoSQL: Khi nào nên chọn cái nào?",
                "sql-vs-nosql", Set.of(database), now.minusDays(14), MD_P13);
        post(profile, "Design Pattern thông dụng trong Java: Singleton, Builder, Strategy",
                "design-pattern-trong-java", Set.of(java), now.minusDays(9), MD_P14);
        post(profile, "React Router v7: Routing hiện đại cho ứng dụng React",
                "react-router-v7", Set.of(reactjs), now.minusDays(3), MD_P15);

        // ── Series post items ────────────────────────────────────────────────
        seriesPostItemRepository.save(new SeriesPostItem(seriesSpring, p1, 1));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSpring, p2, 2));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSpring, p3, 3));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSpring, p4, 4));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSpring, p5, 5));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSpring, p6, 6));

        seriesPostItemRepository.save(new SeriesPostItem(seriesSD, p7, 1));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSD, p8, 2));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSD, p9, 3));
        seriesPostItemRepository.save(new SeriesPostItem(seriesSD, p10, 4));

        log.info("DataSeeder: seeded 5 topics, 2 series, 15 posts.");
    }

    private Post post(AuthorProfile profile, String title, String slug,
                      Set<Topic> topics, LocalDateTime publishedAt, String markdown) {
        Post p = new Post();
        p.setAuthor(profile);
        p.setTitle(title);
        p.setSlug(slug);
        p.setContentMarkdown(markdown);
        p.setStatus(PostStatusEnum.PUBLISHED);
        p.setPublishedAt(publishedAt);
        p.setTopics(new HashSet<>(topics));
        return postRepository.save(p);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Markdown content
    // ════════════════════════════════════════════════════════════════════════

    private static final String MD_P01 = """
            ## Spring Boot là gì?

            Spring Boot là một framework xây dựng trên nền tảng Spring Framework, ra đời với mục tiêu đơn giản hoá việc
            khởi tạo và phát triển ứng dụng Java. Thay vì phải viết hàng trăm dòng XML cấu hình như Spring truyền thống,
            Spring Boot áp dụng triết lý **convention over configuration** — tự động cấu hình dựa trên những gì có trong
            classpath, để bạn tập trung vào business logic.

            ### Những tính năng nổi bật

            - **Auto-configuration** — Spring Boot phát hiện jar nào đang có và tự cấu hình bean phù hợp.
            - **Embedded server** — Tomcat, Jetty hoặc Undertow được đóng gói sẵn, chạy trực tiếp bằng `java -jar`.
            - **Starter dependencies** — Gói các dependency liên quan thành một artifact duy nhất như `spring-boot-starter-web`.
            - **Actuator** — Cung cấp endpoint giám sát sức khoẻ ứng dụng (`/actuator/health`, `/actuator/metrics`).
            - **Spring Initializr** — Công cụ web sinh skeleton project trong vài giây.

            ## Tạo REST endpoint đầu tiên

            Sau khi khởi tạo project với Spring Initializr và chọn dependency `Spring Web`, tạo controller đơn giản:

            ```java
            @RestController
            @RequestMapping("/api")
            public class HelloController {

                @GetMapping("/hello")
                public String hello(@RequestParam(defaultValue = "World") String name) {
                    return "Hello, " + name + "!";
                }
            }
            ```

            Chạy ứng dụng bằng Maven Wrapper:

            ```bash
            ./mvnw spring-boot:run
            ```

            Truy cập `http://localhost:8080/api/hello?name=HVChinh` sẽ trả về `Hello, HVChinh!`.

            ## Cấu hình qua application.yaml

            Spring Boot đọc cấu hình từ `src/main/resources/application.yaml` (hoặc `.properties`):

            ```yaml
            server:
              port: 8080

            spring:
              application:
                name: my-blog

            app:
              max-posts-per-page: 10
            ```

            Để inject giá trị vào bean, dùng `@Value` hoặc `@ConfigurationProperties`:

            ```java
            @ConfigurationProperties(prefix = "app")
            public record AppProperties(int maxPostsPerPage) {}
            ```

            ## Khi nào nên dùng Spring Boot?

            Spring Boot phù hợp cho hầu hết các dự án Java backend hiện đại:

            - REST API cho ứng dụng web/mobile
            - Microservices trong kiến trúc phân tán
            - Batch processing với Spring Batch
            - Hệ thống messaging với Spring Kafka/RabbitMQ

            Nếu bạn cần một thứ gì đó rất nhỏ gọn (ví dụ function-as-a-service), có thể cân nhắc Quarkus hoặc Micronaut.
            Nhưng với đa số dự án enterprise, Spring Boot vẫn là lựa chọn tốt nhất trong hệ sinh thái Java.

            ## Kết luận

            Spring Boot loại bỏ boilerplate, giúp lập trình viên Java focus vào điều thực sự quan trọng: business logic.
            Ở các bài tiếp theo trong series, chúng ta sẽ đi sâu vào từng thành phần: JPA, Security, Exception Handling và
            các kỹ thuật tối ưu hiệu năng.
            """;

    private static final String MD_P02 = """
            ## Tại sao cấu trúc dự án quan trọng?

            Khi dự án còn nhỏ, mọi thứ có vẻ đơn giản. Nhưng khi codebase phát triển lên hàng chục nghìn dòng, cấu trúc
            folder sẽ quyết định tốc độ phát triển và bảo trì. Một cấu trúc tốt giúp:

            - Tìm file nhanh hơn mà không cần IDE toàn năng
            - Cô lập thay đổi — sửa một feature không vô tình ảnh hưởng feature khác
            - Onboarding thành viên mới nhanh hơn

            ## Package-by-Feature là gì?

            Thay vì tổ chức code theo layer kỹ thuật (`controller/`, `service/`, `repository/`), **package-by-feature**
            nhóm code theo business domain:

            ```
            com.blog
            ├── post
            │   ├── controller
            │   ├── service
            │   ├── repository
            │   ├── entity
            │   └── dto
            ├── topic
            ├── series
            └── common
                ├── config
                └── exception
            ```

            Tất cả code liên quan đến `post` nằm trong một package. Khi cần sửa logic bài viết, bạn chỉ cần làm việc
            trong `post/` thay vì nhảy qua lại giữa `controllers/PostController.java`, `services/PostService.java`, v.v.

            ### Quy tắc trong từng layer

            **Controller** — chỉ nhận request, validate input, gọi service, trả response. Không chứa business logic.

            ```java
            @RestController
            @RequestMapping("/api/posts")
            @RequiredArgsConstructor
            public class PostController {

                private final PostService postService;

                @GetMapping("/{slug}")
                public ApiResponse<PostDetailDTO> getDetail(@PathVariable String slug) {
                    return ApiResponse.of(postService.getPostDetail(slug));
                }
            }
            ```

            **Service** — nơi duy nhất chứa business logic và transaction. Không gọi trực tiếp database từ controller.

            **Repository** — chỉ truy cập database. Không có business logic trong query name hay method body.

            ## Package-by-Feature vs Package-by-Layer

            | Tiêu chí | Package-by-Layer | Package-by-Feature |
            |---|---|---|
            | Tìm code liên quan | Phải tìm nhiều folder | Tất cả trong 1 package |
            | Đóng gói feature | Khó | Dễ |
            | Phù hợp microservice | Không | Có |
            | Quen thuộc | Dễ học | Cần làm quen |

            ## Sử dụng constructor injection

            Spring khuyến khích **constructor injection** thay vì field injection (`@Autowired` trên field):

            ```java
            // Tốt — constructor injection với Lombok
            @Service
            @RequiredArgsConstructor
            public class PostService {
                private final PostRepository postRepository;
                private final TopicRepository topicRepository;
            }

            // Tránh — field injection khó test và che giấu dependency
            @Service
            public class PostService {
                @Autowired
                private PostRepository postRepository;
            }
            ```

            ## Kết luận

            Package-by-feature là cấu trúc phù hợp nhất cho hầu hết dự án Spring Boot hiện đại, đặc biệt khi dự án có
            thể sẽ tách thành microservices sau này. Hãy áp dụng nhất quán ngay từ đầu — refactor cấu trúc sau này sẽ
            tốn nhiều công hơn bạn tưởng.
            """;

    private static final String MD_P03 = """
            ## Giới thiệu Spring Data JPA

            Spring Data JPA là một abstraction layer nằm trên Hibernate, giúp tương tác với cơ sở dữ liệu quan hệ mà
            không cần viết SQL thủ công cho các thao tác CRUD thông thường. Hibernate xử lý việc ánh xạ object-relational,
            còn Spring Data JPA cung cấp `Repository` interface với các method sẵn có.

            ## Định nghĩa Entity

            ### BaseEntity — Tránh lặp code

            Hầu hết entity đều có `id`, `createdAt`, `updatedAt`. Đặt vào `BaseEntity` để tái sử dụng:

            ```java
            @MappedSuperclass
            @Getter
            public abstract class BaseEntity {

                @Id
                @Column(length = 36)
                private String id;

                @PrePersist
                void generateId() {
                    if (id == null) {
                        id = UUID.randomUUID().toString();
                    }
                }
            }
            ```

            Entity kế thừa `BaseEntity` và thêm field nghiệp vụ:

            ```java
            @Entity
            @Table(name = "topic")
            @Getter @Setter @NoArgsConstructor
            public class Topic extends BaseEntity {

                @Column(nullable = false, unique = true)
                private String name;

                @Column(nullable = false, unique = true)
                private String slug;

                @Column(columnDefinition = "TEXT")
                private String description;
            }
            ```

            ## Repository Pattern

            ### JpaRepository interface

            Khai báo interface kế thừa `JpaRepository` — Spring Data tự sinh implementation lúc runtime:

            ```java
            public interface TopicRepository extends JpaRepository<Topic, String> {

                Optional<Topic> findBySlug(String slug);

                boolean existsByNameIgnoreCase(String name);
            }
            ```

            Bạn có ngay các method: `findById`, `findAll`, `save`, `delete`, `count`... mà không cần viết bất kỳ
            dòng SQL nào.

            ### Custom query với JPQL

            Khi cần query phức tạp hơn, dùng `@Query` với JPQL (không phải SQL):

            ```java
            @Query(\"""
                    SELECT new com.blog.demo.topic.dto.TopicListItemDTO(
                        t.id, t.name, t.slug, t.description, COUNT(p.id))
                    FROM Topic t
                    LEFT JOIN t.posts p WITH p.status = :status
                    GROUP BY t.id
                    ORDER BY t.name ASC
                    \""")
            List<TopicListItemDTO> findAllWithPublishedPostCount(@Param("status") PostStatusEnum status);
            ```

            ### Phân trang kết quả

            Truyền `Pageable` vào method repository để tự động phân trang:

            ```java
            Page<Post> findByStatusOrderByPublishedAtDesc(PostStatusEnum status, Pageable pageable);
            ```

            Trong service:

            ```java
            Pageable pageable = PageRequest.of(page - 1, size); // page 1-based từ client
            Page<Post> postPage = postRepository
                    .findByStatusOrderByPublishedAtDesc(PostStatusEnum.PUBLISHED, pageable);
            ```

            ## Dùng DTO, không expose Entity

            Entity là persistence model — không bao giờ trả trực tiếp từ API. Dùng DTO và MapStruct để ánh xạ:

            ```java
            @Mapper(componentModel = "spring")
            public interface TopicMapper {
                TopicResponseDTO toResponse(Topic topic);
            }
            ```

            ## Kết luận

            Spring Data JPA giảm đáng kể lượng boilerplate code khi làm việc với cơ sở dữ liệu. Kết hợp với Flyway để
            quản lý schema migration, bạn có một data layer mạnh mẽ và an toàn cho production.
            """;

    private static final String MD_P04 = """
            ## Vấn đề với exception handling mặc định

            Khi ứng dụng Spring Boot ném một exception chưa được xử lý, Spring trả về response dạng:

            ```json
            {
              "timestamp": "2024-06-17T08:00:00.000+00:00",
              "status": 500,
              "error": "Internal Server Error",
              "path": "/api/posts/not-exist"
            }
            ```

            Response này vừa lộ thông tin nội bộ, vừa không nhất quán với các endpoint khác. Client không biết lỗi
            là `POST_NOT_FOUND` hay `UNAUTHORIZED`.

            ## @ControllerAdvice là gì?

            `@ControllerAdvice` là annotation đánh dấu một class là **global exception handler** — tất cả exception ném
            ra từ bất kỳ controller nào đều được bắt và xử lý tập trung tại đây.

            ## Tạo custom exception

            Định nghĩa exception riêng thay vì dùng `RuntimeException` chung chung:

            ```java
            public class ResourceNotFoundException extends RuntimeException {

                private final String errorCode;

                public ResourceNotFoundException(String errorCode, String message) {
                    super(message);
                    this.errorCode = errorCode;
                }

                public String getErrorCode() {
                    return errorCode;
                }
            }
            ```

            Ném exception trong service:

            ```java
            Post post = postRepository.findBySlugAndStatus(slug, PostStatusEnum.PUBLISHED)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "POST_NOT_FOUND", "Bài viết không tồn tại: " + slug));
            ```

            ## GlobalExceptionHandler

            ```java
            @RestControllerAdvice
            @RequiredArgsConstructor
            public class GlobalExceptionHandler {

                private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

                @ExceptionHandler(ResourceNotFoundException.class)
                @ResponseStatus(HttpStatus.NOT_FOUND)
                public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
                    return new ErrorResponse(ex.getErrorCode(), ex.getMessage());
                }

                @ExceptionHandler(MethodArgumentNotValidException.class)
                @ResponseStatus(HttpStatus.BAD_REQUEST)
                public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
                    String message = ex.getBindingResult().getFieldErrors().stream()
                            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                            .collect(Collectors.joining("; "));
                    return new ErrorResponse("VALIDATION_ERROR", message);
                }

                @ExceptionHandler(Exception.class)
                @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                public ErrorResponse handleUnexpected(Exception ex) {
                    log.error("Unexpected error", ex);
                    return new ErrorResponse("INTERNAL_ERROR", "Đã xảy ra lỗi, vui lòng thử lại.");
                }
            }
            ```

            ## Response format thống nhất

            Định nghĩa `ErrorResponse` record để đảm bảo format nhất quán:

            ```java
            public record ErrorResponse(String code, String message) {}
            ```

            Kết quả: mọi lỗi đều trả về dạng:

            ```json
            { "code": "POST_NOT_FOUND", "message": "Bài viết không tồn tại: spring-boot-la-gi" }
            ```

            Client chỉ cần kiểm tra field `code` để biết cách xử lý — không phụ thuộc vào HTTP status hay message text.

            ## Không log sensitive data

            Lưu ý quan trọng: **không bao giờ log mật khẩu, token, hay thông tin cá nhân** của người dùng.

            ```java
            // Sai
            log.info("Login attempt: email={}, password={}", email, password);

            // Đúng
            log.info("Login attempt: email={}", email);
            ```

            ## Kết luận

            `@ControllerAdvice` kết hợp custom exception và `ErrorResponse` record tạo nên một exception handling layer
            sạch sẽ, nhất quán. Client nhận được thông tin lỗi rõ ràng; server không lộ stack trace ra ngoài.
            """;

    private static final String MD_P05 = """
            ## JWT là gì?

            JSON Web Token (JWT) là một chuẩn mở (RFC 7519) để truyền thông tin giữa các bên dưới dạng JSON object được
            ký số. JWT phù hợp với **stateless authentication** — server không cần lưu session, mỗi request mang token
            và server verify độc lập.

            ### Cấu trúc JWT

            JWT gồm ba phần ngăn cách bởi dấu chấm: `header.payload.signature`

            ```
            eyJhbGciOiJIUzI1NiJ9          <- Header (Base64)
            .eyJzdWIiOiJhZG1pbiJ9         <- Payload (Base64)
            .SflKxwRJSMeKKF2QT4fwpMeJf36  <- Signature (HMAC-SHA256)
            ```

            Payload chứa **claims** như `sub` (subject), `iat` (issued at), `exp` (expiration). Không lưu thông tin nhạy
            cảm trong payload vì ai cũng có thể decode Base64.

            ## Tích hợp Spring Security

            ### SecurityFilterChain

            Cấu hình Spring Security: tắt session, bật JWT filter:

            ```java
            @Bean
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                        .csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/posts/**", "/api/topics", "/api/profile").permitAll()
                                .anyRequest().authenticated())
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .build();
            }
            ```

            ### JwtAuthenticationFilter

            Filter đọc header `Authorization: Bearer <token>`, verify và set `SecurityContext`:

            ```java
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                String header = request.getHeader("Authorization");
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);
                    if (tokenProvider.isValid(token)) {
                        String email = tokenProvider.getSubject(token);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(email, null,
                                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                chain.doFilter(request, response);
            }
            ```

            ## Access Token vs Refresh Token

            Dùng hai loại token để cân bằng bảo mật và trải nghiệm người dùng:

            | | Access Token | Refresh Token |
            |---|---|---|
            | Thời hạn | 30 phút | 30 ngày |
            | Lưu ở đâu | Memory / cookie | HttpOnly cookie |
            | Lưu trên server | Không | Hash SHA-256 trong DB |
            | Mục đích | Xác thực mỗi request | Lấy Access Token mới |

            Khi Refresh Token được dùng, hệ thống **rotate** — cấp cặp token mới và vô hiệu hoá token cũ. Cơ chế này
            phát hiện token bị đánh cắp: nếu kẻ tấn công dùng token cũ đã rotate, server biết có sự cố và revoke toàn
            bộ session.

            ## Kết luận

            Stateless JWT authentication với Spring Security loại bỏ sự phụ thuộc vào session và giúp horizontal scaling
            dễ dàng hơn. Kết hợp Access Token ngắn hạn và Refresh Token rotation là best practice hiện đại cho mọi
            ứng dụng web.
            """;

    private static final String MD_P06 = """
            ## Connection pooling với HikariCP

            Mỗi lần tạo connection đến database mất khoảng 50–100ms. Với HikariCP (mặc định trong Spring Boot),
            connection được tái sử dụng qua pool. Cấu hình pool size hợp lý:

            ```yaml
            spring:
              datasource:
                hikari:
                  maximum-pool-size: 10
                  minimum-idle: 5
                  connection-timeout: 30000
                  idle-timeout: 600000
            ```

            Nguyên tắc chọn pool size: `số CPU cores * 2 + số effective spindle` (theo công thức của HikariCP).
            Đừng đặt quá cao — nhiều connection không luôn nhanh hơn, vì database cũng có giới hạn.

            ## Tránh N+1 query

            ### Vấn đề N+1

            Khi load danh sách post với topic, nếu không cẩn thận sẽ sinh N+1 query:

            ```java
            // 1 query lấy N posts
            List<Post> posts = postRepository.findAll();

            // Mỗi post lại gọi thêm 1 query lấy topics => N query nữa
            posts.forEach(p -> p.getTopics().size()); // LAZY loading trigger
            ```

            Tổng: `1 + N` query cho N bài viết. Với 100 bài, đó là 101 query!

            ### Giải pháp với JOIN FETCH

            ```java
            @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.topics WHERE p.status = :status")
            List<Post> findPublishedWithTopics(@Param("status") PostStatusEnum status);
            ```

            Hoặc dùng `@EntityGraph` để khai báo eager loading cho từng use case cụ thể mà không ảnh hưởng default:

            ```java
            @EntityGraph(attributePaths = "topics")
            List<Post> findByStatus(PostStatusEnum status);
            ```

            ## Caching kết quả ít thay đổi

            Topics và profile hiếm khi thay đổi — cache lại để tránh query database mỗi request:

            ```java
            @Service
            public class TopicService {

                @Cacheable("topics")
                public List<TopicListItemDTO> getAllTopics() {
                    return topicRepository.findAllWithPublishedPostCount(PostStatusEnum.PUBLISHED);
                }

                @CacheEvict(value = "topics", allEntries = true)
                public TopicResponseDTO createTopic(TopicCreateRequestDTO dto) {
                    // ...
                }
            }
            ```

            Thêm dependency `spring-boot-starter-cache` và annotation `@EnableCaching` vào main class để bật.

            ## Phân trang, không lấy toàn bộ dữ liệu

            Không bao giờ dùng `findAll()` cho bảng lớn. Luôn phân trang:

            ```java
            Page<Post> findByStatusOrderByPublishedAtDesc(PostStatusEnum status, Pageable pageable);
            ```

            ## Bật slow query log

            Cấu hình Hibernate log query chậm hơn ngưỡng (ms):

            ```yaml
            spring:
              jpa:
                properties:
                  hibernate:
                    session:
                      events:
                        log:
                          LOG_QUERIES_SLOWER_THAN_MS: 100
            ```

            ## Kết luận

            Tối ưu hiệu năng là quá trình liên tục: đo lường trước, tối ưu sau. Đừng tối ưu sớm khi chưa biết
            điểm nghẽn thực sự. Nhưng tránh N+1 và cấu hình connection pool đúng là những việc nên làm ngay từ đầu.
            """;

    private static final String MD_P07 = """
            ## System Design là gì?

            System Design là quá trình xác định kiến trúc, thành phần, module, interface và dữ liệu cần thiết để
            đáp ứng các yêu cầu của hệ thống. Nói đơn giản, đây là bài toán: **"Làm thế nào để xây dựng một hệ thống
            có thể phục vụ hàng triệu người dùng đồng thời?"**

            System Design khác với coding thuần tuý ở chỗ nó đòi hỏi suy nghĩ về:

            - Scalability — mở rộng khi lưu lượng tăng
            - Availability — hệ thống luôn hoạt động kể cả khi có lỗi
            - Reliability — dữ liệu không bị mất hay sai
            - Maintainability — dễ thay đổi và bảo trì

            ## Tại sao mọi lập trình viên cần học?

            ### Trong phỏng vấn

            System Design Interview là vòng bắt buộc tại hầu hết các công ty tech lớn (Google, Amazon, Meta, Grab,
            Shopee...). Đây là vòng phân biệt Junior với Senior — bạn có thể code tốt, nhưng nếu không hiểu trade-off
            khi thiết kế hệ thống, khó thăng tiến lên vị trí cao hơn.

            ### Trong thực tế

            Dù không đi phỏng vấn, hiểu System Design giúp bạn:

            - Đưa ra quyết định kỹ thuật đúng đắn (chọn SQL hay NoSQL, caching ở đâu, queue hay sync call?)
            - Giao tiếp hiệu quả hơn với team và stakeholder
            - Dự đoán vấn đề trước khi chúng xảy ra ở production

            ## Các khái niệm cốt lõi

            Trước khi thiết kế hệ thống, bạn cần nắm vững:

            - **Load Balancer** — phân phối traffic đến nhiều server
            - **Horizontal Scaling** — thêm server thay vì nâng cấp server hiện có
            - **CDN** — phân phối static content gần người dùng hơn
            - **Caching** — Redis, Memcached để giảm tải database
            - **Message Queue** — Kafka, RabbitMQ để xử lý tác vụ bất đồng bộ
            - **Database Sharding** — phân vùng dữ liệu trên nhiều database server
            - **CAP Theorem** — Consistency, Availability, Partition Tolerance — chỉ đạt được 2 trong 3

            ## Quy trình thiết kế hệ thống

            Một buổi System Design Interview thường diễn ra trong 45 phút theo khung sau:

            1. **Clarify requirements** (5 phút) — hỏi rõ functional và non-functional requirements, scale
            2. **Estimate scale** (5 phút) — số user, số request/s, dung lượng lưu trữ
            3. **High-level design** (15 phút) — vẽ sơ đồ tổng thể, các thành phần chính
            4. **Deep dive** (15 phút) — đi sâu vào thành phần quan trọng nhất
            5. **Wrap up** (5 phút) — trade-off, bottleneck, cải tiến tiếp theo

            ## Kết luận

            System Design không có đáp án duy nhất đúng. Mỗi thiết kế là tập hợp các trade-off. Bài tiếp theo trong
            series sẽ áp dụng quy trình này để thiết kế một URL Shortener thực tế.
            """;

    private static final String MD_P08 = """
            ## Yêu cầu hệ thống

            ### Functional requirements

            - Tạo short URL từ long URL (ví dụ `https://bit.ly/3xKmN2p`)
            - Redirect từ short URL về long URL
            - Tuỳ chọn custom alias (ví dụ `bit.ly/my-blog`)
            - Short URL hết hạn sau 1 năm (có thể cấu hình)

            ### Non-functional requirements

            - **Availability cao**: 99.9% uptime — hệ thống redirect không được down
            - **Độ trễ thấp**: redirect < 10ms
            - **Scale**: 100 triệu URL được tạo mỗi ngày, tỉ lệ đọc/ghi = 100:1

            ## Ước tính quy mô

            - Ghi: 100 triệu URL/ngày ≈ 1.200 URL/giây
            - Đọc (redirect): 100 × 1.200 = 120.000 request/giây
            - Lưu trữ: 100 triệu × 500 bytes × 365 ngày × 5 năm ≈ 91 TB

            Với 120.000 request đọc/giây, database thuần tuý không đủ — cần **caching layer**.

            ## Thiết kế cơ sở dữ liệu

            Chỉ cần một bảng đơn giản:

            | Cột | Kiểu | Ghi chú |
            |---|---|---|
            | `short_key` | VARCHAR(8) PK | Khoá ngắn, có index |
            | `long_url` | TEXT | URL gốc |
            | `created_at` | DATETIME | Thời điểm tạo |
            | `expires_at` | DATETIME | Thời điểm hết hạn |
            | `user_id` | BIGINT FK | Null nếu anonymous |

            ## Thuật toán tạo short key

            ### Cách 1 — Base62 encoding

            Sinh số nguyên tự tăng (ID), rồi chuyển sang Base62 (a-z, A-Z, 0-9):

            - ID = 1.000.000 → Base62 = `4c92`
            - 6 ký tự Base62 = 62^6 ≈ 56 tỉ URL

            Ưu điểm: đơn giản, không xung đột. Nhược điểm: ID tăng dần lộ thứ tự tạo, dễ enum.

            ### Cách 2 — MD5 / SHA256 + truncate

            Hash long URL, lấy 6-8 ký tự đầu:

            Ưu điểm: cùng URL cho ra cùng short key. Nhược điểm: xung đột (collision), cần xử lý thêm.

            ## Kiến trúc tổng thể

            ```
            Client
              |
            Load Balancer
              |
            API Servers (stateless, horizontal scale)
              |
            Redis Cache ──── (cache: shortKey -> longUrl, TTL 24h)
              |
            MySQL (Primary) ── Replica
            ```

            Luồng redirect:
            1. Request đến `bit.ly/3xKmN2p`
            2. API server tìm trong Redis cache → nếu có, redirect ngay (< 1ms)
            3. Nếu không có, query MySQL, cập nhật cache, rồi redirect

            ## Kết luận

            URL Shortener tưởng đơn giản nhưng ẩn chứa nhiều quyết định kỹ thuật thú vị. Cache layer là chìa khoá để
            đạt được độ trễ thấp. Bài tiếp theo, chúng ta sẽ tìm hiểu Database Sharding khi dữ liệu vượt khả năng một
            server MySQL.
            """;

    private static final String MD_P09 = """
            ## Scaling cơ sở dữ liệu

            Khi dữ liệu tăng đến mức một server MySQL không đủ sức chứa, có hai hướng:

            - **Vertical scaling** — nâng cấp phần cứng (RAM, SSD, CPU). Giới hạn bởi vật lý và chi phí.
            - **Horizontal scaling** — thêm nhiều server. Đây là hướng của Sharding.

            ## Sharding là gì?

            **Database Sharding** là kỹ thuật phân chia dữ liệu theo chiều ngang (horizontal partitioning) — mỗi
            **shard** là một database instance riêng, chứa một phần dữ liệu. Tổng hợp lại mới có toàn bộ dataset.

            Ví dụ: Bảng `user` có 100 triệu record được chia thành 4 shard:

            - Shard 1: user_id 0–24.999.999
            - Shard 2: user_id 25.000.000–49.999.999
            - Shard 3: user_id 50.000.000–74.999.999
            - Shard 4: user_id 75.000.000–99.999.999

            ### Horizontal Sharding

            Mỗi shard có cùng schema nhưng chứa subset of rows. Khác với **vertical partitioning** — tách bảng theo
            cột (đưa các cột ít dùng sang bảng khác).

            ## Các chiến lược Sharding

            ### Range-based Sharding

            Phân vùng theo khoảng giá trị của shard key:

            - Đơn giản, dễ hiểu
            - **Nhược điểm**: hot spot — nếu user mới đều vào Shard 4, shard đó bị quá tải

            ### Hash-based Sharding

            `shard_id = hash(shard_key) % num_shards`

            - Phân bố đều hơn, không có hot spot
            - **Nhược điểm**: thêm shard phải rehash toàn bộ dữ liệu. Giải pháp: **Consistent Hashing**.

            ### Directory-based Sharding

            Duy trì một bảng lookup service ánh xạ key → shard:

            - Linh hoạt nhất, có thể di chuyển record giữa shard
            - **Nhược điểm**: lookup service trở thành single point of failure

            ## Thách thức khi dùng Sharding

            Sharding không phải silver bullet. Những vấn đề bạn sẽ gặp:

            - **Cross-shard join**: JOIN giữa hai bảng nằm ở shard khác nhau không thể dùng SQL thông thường
            - **Cross-shard transaction**: đảm bảo ACID giữa nhiều shard rất phức tạp (cần 2-Phase Commit)
            - **Rebalancing**: khi thêm shard, phải di chuyển data mà không downtime
            - **Tăng độ phức tạp**: dev và ops team đều phải hiểu logic sharding

            ## Khi nào nên dùng Sharding?

            Sharding là giải pháp cuối cùng, sau khi đã:

            1. Tối ưu query và index
            2. Thêm Read Replica để phân tải đọc
            3. Dùng caching (Redis)
            4. Vertical scaling đến mức tối đa hợp lý

            Nhiều hệ thống ở quy mô vừa không bao giờ cần Sharding.

            ## Kết luận

            Sharding là kỹ thuật mạnh nhưng phức tạp. Hiểu rõ trade-off trước khi quyết định áp dụng. Với nhiều bài
            toán, Read Replica kết hợp caching đã giải quyết được 90% vấn đề về scale mà không cần Sharding.
            """;

    private static final String MD_P10 = """
            ## Monolith là gì?

            Monolith (kiến trúc nguyên khối) là toàn bộ ứng dụng được triển khai như một đơn vị duy nhất. Backend,
            business logic, data access layer — tất cả nằm trong một codebase, build thành một artifact và deploy
            lên một (hoặc nhiều) server.

            ### Ưu điểm của Monolith

            - **Đơn giản để phát triển ban đầu** — một codebase, một IDE, một pipeline CI/CD
            - **Dễ test end-to-end** — không cần mock service bên ngoài
            - **Hiệu năng cao cho internal call** — in-process function call nhanh hơn HTTP request
            - **Dễ debug** — một stack trace, không phải trace qua nhiều service

            ### Nhược điểm của Monolith

            - **Tight coupling** — một thay đổi nhỏ có thể ảnh hưởng toàn bộ hệ thống
            - **Scale hạn chế** — phải scale toàn bộ monolith dù chỉ một module cần thêm tài nguyên
            - **Deploy rủi ro** — deploy một feature nhỏ phải deploy lại toàn bộ
            - **Technology lock-in** — khó dùng ngôn ngữ/framework khác cho từng phần

            ## Microservices là gì?

            Microservices chia ứng dụng thành nhiều service nhỏ, độc lập, mỗi service:

            - Chịu trách nhiệm một business domain cụ thể (User Service, Order Service, Payment Service)
            - Có database riêng
            - Giao tiếp qua API (REST, gRPC) hoặc message queue
            - Deploy và scale độc lập

            ### Ưu điểm của Microservices

            - **Scale độc lập** — service nào bận thì scale riêng service đó
            - **Technology diversity** — mỗi service có thể dùng ngôn ngữ/framework phù hợp nhất
            - **Fault isolation** — một service lỗi không kéo sập toàn hệ thống
            - **Team autonomy** — mỗi team sở hữu và deploy service của mình

            ### Nhược điểm của Microservices

            - **Complexity tăng vọt** — distributed system problems: network latency, partial failure, data consistency
            - **Overhead vận hành** — cần Kubernetes, service mesh, distributed tracing, centralized logging
            - **Khó debug** — một request đi qua 5 service, lỗi ở bước nào?
            - **Data consistency** — không có ACID transaction giữa các service, phải dùng Saga pattern

            ## Khi nào nên chuyển đổi?

            Không phải mọi dự án đều cần Microservices. Hãy bắt đầu với Monolith và chỉ chuyển khi gặp những dấu
            hiệu cụ thể:

            - Team lớn (>50 người) và các nhóm liên tục conflict khi merge code
            - Một module cụ thể cần scale riêng mà không thể làm được với Monolith
            - Cần dùng công nghệ khác cho một phần cụ thể (ví dụ: ML service bằng Python)
            - Deployment frequency bị chặn vì phụ thuộc giữa các module quá nhiều

            Một câu hỏi đơn giản: **"Monolith hiện tại đang gây ra vấn đề gì cụ thể?"** Nếu không trả lời được,
            chưa cần Microservices.

            ## Kết luận

            Microservices không phải tiến hoá tự nhiên từ Monolith mà là một lựa chọn kiến trúc với chi phí và lợi
            ích rõ ràng. Monolith được thiết kế tốt vẫn phục vụ hàng triệu người dùng hiệu quả. Đánh giá kỹ trước
            khi quyết định — nhiều công ty đã refactor microservices về monolith vì overhead quá lớn.
            """;

    private static final String MD_P11 = """
            ## Stream API là gì?

            Java Stream API (ra đời từ Java 8) cho phép xử lý dãy dữ liệu theo phong cách **functional** và
            **declarative** — bạn mô tả *muốn gì* thay vì *làm thế nào*. Stream không phải là cấu trúc dữ liệu;
            nó là pipeline xử lý dữ liệu lười biếng (lazy).

            ## Các thao tác cơ bản

            ### filter — lọc phần tử

            ```java
            List<String> names = List.of("Alice", "Bob", "Anna", "Charlie");

            List<String> startsWithA = names.stream()
                    .filter(name -> name.startsWith("A"))
                    .toList(); // ["Alice", "Anna"]
            ```

            ### map — chuyển đổi phần tử

            ```java
            List<Post> posts = postRepository.findAll();

            List<String> titles = posts.stream()
                    .map(Post::getTitle)
                    .toList();
            ```

            Chuyển đổi kiểu: `map` từ `Post` sang `String`.

            ### reduce và collect

            ```java
            int totalPublished = posts.stream()
                    .filter(Post::isPublished)
                    .mapToInt(p -> 1)
                    .sum();

            // Nhóm post theo status
            Map<PostStatusEnum, List<Post>> byStatus = posts.stream()
                    .collect(Collectors.groupingBy(Post::getStatus));
            ```

            ## Stream nâng cao

            ### flatMap — làm phẳng nested collection

            ```java
            List<Topic> allTopics = posts.stream()
                    .flatMap(post -> post.getTopics().stream())
                    .distinct()
                    .sorted(Comparator.comparing(Topic::getName))
                    .toList();
            ```

            ### Optional và Stream

            ```java
            Optional<Post> found = postRepository.findBySlug(slug);

            // Thay vì if/else
            String title = found
                    .map(Post::getTitle)
                    .orElse("Bài viết không tồn tại");
            ```

            ## Parallel Stream — cẩn thận khi dùng

            ```java
            // Dùng parallel khi xử lý tập dữ liệu lớn, CPU-bound, không có shared mutable state
            long count = largeList.parallelStream()
                    .filter(item -> heavyComputation(item))
                    .count();
            ```

            Đừng dùng `parallelStream()` mặc định cho mọi thứ — với collection nhỏ, overhead của thread pool còn
            chậm hơn sequential stream.

            ## So sánh với vòng lặp truyền thống

            ```java
            // Vòng lặp truyền thống
            List<String> result = new ArrayList<>();
            for (Post post : posts) {
                if (post.isPublished()) {
                    result.add(post.getTitle().toUpperCase());
                }
            }

            // Stream — ngắn gọn và dễ đọc hơn
            List<String> result = posts.stream()
                    .filter(Post::isPublished)
                    .map(p -> p.getTitle().toUpperCase())
                    .toList();
            ```

            ## Kết luận

            Stream API là một trong những tính năng quan trọng nhất của Java hiện đại. Nắm vững `filter`, `map`,
            `flatMap`, `collect` và bạn có thể xử lý hầu hết bài toán collection một cách súc tích và dễ đọc. Kết
            hợp với `Optional`, code Java trở nên an toàn hơn và ít `NullPointerException` hơn.
            """;

    private static final String MD_P12 = """
            ## Hooks là gì?

            React Hooks (ra mắt trong React 16.8) cho phép function component sử dụng state và các tính năng của
            React mà trước đây chỉ class component mới có. Hai hook quan trọng nhất là `useState` và `useEffect`.

            ## useState — Quản lý state cục bộ

            ### Cú pháp

            ```jsx
            import { useState } from 'react';

            function Counter() {
              const [count, setCount] = useState(0); // giá trị khởi tạo = 0

              return (
                <div>
                  <p>Đã click: {count} lần</p>
                  <button onClick={() => setCount(count + 1)}>Click me</button>
                </div>
              );
            }
            ```

            `useState` trả về array gồm hai phần tử: **giá trị hiện tại** và **hàm cập nhật**.

            ### Cập nhật state dựa trên state trước đó

            ```jsx
            // Sai — có thể bị stale closure
            setCount(count + 1);

            // Đúng — dùng functional update khi state mới phụ thuộc vào state cũ
            setCount(prev => prev + 1);
            ```

            ### State với object

            ```jsx
            const [form, setForm] = useState({ name: '', email: '' });

            // Cập nhật một field, giữ nguyên field còn lại
            const handleChange = (e) => {
              setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
            };
            ```

            ## useEffect — Side effects và lifecycle

            `useEffect` thay thế `componentDidMount`, `componentDidUpdate`, `componentWillUnmount` của class component.

            ### Dependency array

            ```jsx
            useEffect(() => {
              // Chạy sau mỗi lần render
            });

            useEffect(() => {
              // Chỉ chạy 1 lần sau khi component mount (tương đương componentDidMount)
            }, []);

            useEffect(() => {
              // Chạy khi `postId` thay đổi
              fetchPost(postId);
            }, [postId]);
            ```

            ### Fetch data trong useEffect

            ```jsx
            function PostDetail({ slug }) {
              const [post, setPost] = useState(null);
              const [loading, setLoading] = useState(true);

              useEffect(() => {
                let cancelled = false;

                fetch(`/api/posts/${slug}`)
                  .then(res => res.json())
                  .then(data => {
                    if (!cancelled) setPost(data.data);
                  })
                  .finally(() => {
                    if (!cancelled) setLoading(false);
                  });

                return () => { cancelled = true; }; // cleanup: tránh setState sau unmount
              }, [slug]);

              if (loading) return <p>Đang tải...</p>;
              return <article>{post?.title}</article>;
            }
            ```

            ### Cleanup function

            Cleanup chạy trước khi effect chạy lần tiếp theo hoặc khi component unmount — dùng để huỷ
            subscription, clear timer, cancel fetch:

            ```jsx
            useEffect(() => {
              const timer = setInterval(() => setTime(Date.now()), 1000);
              return () => clearInterval(timer); // cleanup
            }, []);
            ```

            ## Custom Hooks — Tái sử dụng logic

            Đặt logic phức tạp vào custom hook để tái sử dụng:

            ```jsx
            function usePost(slug) {
              const [post, setPost] = useState(null);
              const [loading, setLoading] = useState(true);

              useEffect(() => {
                fetch(`/api/posts/${slug}`)
                  .then(r => r.json())
                  .then(d => setPost(d.data))
                  .finally(() => setLoading(false));
              }, [slug]);

              return { post, loading };
            }

            // Dùng trong component
            function PostDetail({ slug }) {
              const { post, loading } = usePost(slug);
              // ...
            }
            ```

            ## Kết luận

            `useState` và `useEffect` là nền tảng của React hiện đại. Hiểu rõ dependency array và cleanup function
            giúp tránh được các lỗi phổ biến như memory leak, stale closure, và infinite loop. Khi logic tái sử dụng,
            hãy tách ra thành custom hook.
            """;

    private static final String MD_P13 = """
            ## SQL Database

            SQL (Structured Query Language) database — hay Relational Database — tổ chức dữ liệu theo bảng với
            schema cố định, ràng buộc quan hệ giữa các bảng thông qua foreign key.

            ### Đặc điểm chính

            - **Schema cứng** — phải định nghĩa cấu trúc bảng trước, thay đổi cần migration
            - **ACID transactions** — Atomicity, Consistency, Isolation, Durability
            - **Quan hệ phức tạp** — JOIN nhiều bảng, foreign key, cascade
            - **Ngôn ngữ chuẩn** — SQL là chuẩn mực, dễ chuyển giữa các hệ quản trị

            Ví dụ phổ biến: **MySQL**, **PostgreSQL**, **MariaDB**, **SQL Server**.

            ### Khi nào dùng SQL?

            - Dữ liệu có cấu trúc rõ ràng và ổn định (đơn hàng, sản phẩm, người dùng)
            - Cần ACID transaction (thanh toán, chuyển khoản)
            - Query phức tạp, JOIN nhiều bảng
            - Cần reporting và analytics trên dữ liệu quan hệ

            ## NoSQL Database

            NoSQL ra đời để giải quyết hạn chế của SQL khi scale và khi cấu trúc dữ liệu linh hoạt.

            ### Các loại NoSQL

            **Document Store** (MongoDB, CouchDB):
            - Lưu JSON/BSON document
            - Mỗi document có thể có schema khác nhau
            - Phù hợp cho: CMS, catalogue sản phẩm, user profile

            **Key-Value Store** (Redis, DynamoDB):
            - Cặp key-value đơn giản
            - Cực kỳ nhanh, thường dùng làm cache
            - Phù hợp cho: session, cache, leaderboard, rate limiting

            **Wide-Column Store** (Cassandra, HBase):
            - Schema linh hoạt theo row
            - Tối ưu cho write-heavy workload
            - Phù hợp cho: time-series, IoT, log

            **Graph Database** (Neo4j):
            - Dữ liệu dạng đồ thị (node và edge)
            - Phù hợp cho: mạng xã hội, recommendation engine, fraud detection

            ### Khi nào dùng NoSQL?

            - Dữ liệu không có cấu trúc cố định hoặc thay đổi thường xuyên
            - Cần horizontal scale rất lớn (hàng tỉ record)
            - Tốc độ đọc/ghi là ưu tiên hàng đầu, consistency có thể hy sinh
            - Dữ liệu phân cấp hoặc dạng tài liệu (JSON)

            ## So sánh tổng hợp

            | Tiêu chí | SQL | NoSQL |
            |---|---|---|
            | Schema | Cố định, cần migration | Linh hoạt, schema-less |
            | Transaction | ACID đầy đủ | Eventual consistency (tuỳ loại) |
            | Scale | Vertical (chủ yếu) | Horizontal dễ dàng |
            | Query | Mạnh (JOIN, subquery) | Giới hạn (tuỳ loại) |
            | Mature | Rất trưởng thành | Đa dạng, một số còn mới |

            ## Kết luận

            Câu trả lời cho "SQL hay NoSQL" luôn là **"tuỳ bài toán"**. Nhiều hệ thống hiện đại dùng cả hai:
            PostgreSQL cho dữ liệu nghiệp vụ cốt lõi và Redis cho caching. Hãy chọn dựa trên đặc điểm dữ liệu,
            yêu cầu consistency và pattern truy cập — không phải vì trend.
            """;

    private static final String MD_P14 = """
            ## Design Pattern là gì?

            Design Pattern là các giải pháp được tổng quát hoá cho các vấn đề lặp đi lặp lại trong thiết kế phần
            mềm. Chúng không phải đoạn code copy-paste được, mà là **template tư duy** để giải quyết một loại vấn
            đề cụ thể. Gang of Four (GoF) phân loại thành ba nhóm: Creational, Structural, Behavioral.

            ## Singleton Pattern — Creational

            Đảm bảo chỉ có một instance duy nhất của class trong suốt vòng đời ứng dụng.

            ### Vấn đề thread-safe

            ```java
            // Lazy initialization — không thread-safe
            public class DatabasePool {
                private static DatabasePool instance;

                private DatabasePool() {}

                public static DatabasePool getInstance() {
                    if (instance == null) {
                        instance = new DatabasePool(); // race condition!
                    }
                    return instance;
                }
            }
            ```

            Giải pháp thread-safe với **double-checked locking**:

            ```java
            public class DatabasePool {
                private static volatile DatabasePool instance;

                private DatabasePool() {}

                public static DatabasePool getInstance() {
                    if (instance == null) {
                        synchronized (DatabasePool.class) {
                            if (instance == null) {
                                instance = new DatabasePool();
                            }
                        }
                    }
                    return instance;
                }
            }
            ```

            Trong Spring Boot, bean mặc định đã là singleton — không cần tự implement.

            ## Builder Pattern — Creational

            Tách quá trình khởi tạo object phức tạp ra khỏi representation của nó.

            ### Ví dụ thực tế

            ```java
            @Builder
            public class EmailMessage {
                private final String to;
                private final String subject;
                private final String body;
                private final List<String> cc;
                private final boolean htmlContent;
            }

            // Sử dụng
            EmailMessage email = EmailMessage.builder()
                    .to("user@example.com")
                    .subject("Chào mừng!")
                    .body("<h1>Xin chào</h1>")
                    .htmlContent(true)
                    .build();
            ```

            Lombok `@Builder` tự sinh builder class, tránh phải viết thủ công. Builder đặc biệt hữu ích khi object
            có nhiều optional field và thứ tự không quan trọng.

            ## Strategy Pattern — Behavioral

            Định nghĩa một họ thuật toán, đóng gói từng thuật toán và cho phép hoán đổi chúng.

            ### Ví dụ thực tế

            ```java
            // Interface Strategy
            public interface PricingStrategy {
                double calculate(double basePrice);
            }

            // Concrete strategies
            public class RegularPricing implements PricingStrategy {
                public double calculate(double basePrice) { return basePrice; }
            }

            public class MemberPricing implements PricingStrategy {
                public double calculate(double basePrice) { return basePrice * 0.9; } // 10% off
            }

            public class SalePricing implements PricingStrategy {
                private final double discountRate;
                public SalePricing(double discountRate) { this.discountRate = discountRate; }
                public double calculate(double basePrice) { return basePrice * (1 - discountRate); }
            }

            // Context
            public class OrderService {
                private PricingStrategy pricingStrategy;

                public void setPricingStrategy(PricingStrategy strategy) {
                    this.pricingStrategy = strategy;
                }

                public double calculateTotal(List<Item> items) {
                    double total = items.stream().mapToDouble(Item::getPrice).sum();
                    return pricingStrategy.calculate(total);
                }
            }
            ```

            Strategy Pattern loại bỏ các `if-else` hoặc `switch` phức tạp, và thêm pricing rule mới không cần
            sửa code hiện có — tuân thủ Open/Closed Principle.

            ## Kết luận

            Design Pattern không phải mục tiêu — chúng là công cụ. Đừng cố nhồi nhét pattern vào code chỉ vì
            pattern trông "chuyên nghiệp". Áp dụng khi bạn nhận ra vấn đề mà pattern đó giải quyết. Và nhớ rằng:
            `@Builder` của Lombok, bean của Spring, đã implement sẵn nhiều pattern cho bạn.
            """;

    private static final String MD_P15 = """
            ## React Router v7 có gì mới?

            React Router v7 (ra mắt cuối 2024) hợp nhất với Remix framework, mang đến ba chế độ sử dụng: library
            mode (giống v6), framework mode (full-stack với SSR), và SPA mode. Bài này tập trung vào **library mode**
            — phù hợp cho SPA thuần React.

            ### Thay đổi quan trọng so với v6

            - API `createBrowserRouter` thay thế `<BrowserRouter>` component
            - Type-safe routes với TypeScript
            - Data loading với `loader` function tích hợp sẵn

            ## Cài đặt và cấu hình

            ```bash
            npm install react-router
            ```

            ### createBrowserRouter

            ```jsx
            import { createBrowserRouter, RouterProvider } from 'react-router';

            const router = createBrowserRouter([
              {
                path: '/',
                element: <RootLayout />,
                children: [
                  { index: true, element: <HomePage /> },
                  { path: 'posts/:slug', element: <PostDetailPage /> },
                  { path: 'topics/:slug', element: <TopicPage /> },
                ],
              },
              {
                path: '/admin',
                element: <AdminLayout />,
                children: [
                  { index: true, element: <AdminDashboard /> },
                  { path: 'posts', element: <AdminPostList /> },
                  { path: 'posts/new', element: <AdminPostEditor /> },
                  { path: 'posts/:id/edit', element: <AdminPostEditor /> },
                ],
              },
            ]);

            export default function App() {
              return <RouterProvider router={router} />;
            }
            ```

            ## Nested Routes

            ### Outlet component

            `<Outlet />` là nơi React Router render route con trong route cha:

            ```jsx
            function AdminLayout() {
              const token = localStorage.getItem('adminToken');

              // Redirect về login nếu chưa đăng nhập
              if (!token) return <Navigate to="/admin/login" replace />;

              return (
                <div className="flex h-screen">
                  <AdminSidebar />
                  <main className="flex-1 overflow-auto p-6">
                    <Outlet /> {/* Route con render ở đây */}
                  </main>
                </div>
              );
            }
            ```

            ## Lấy params và query string

            ```jsx
            import { useParams, useSearchParams } from 'react-router';

            function PostDetailPage() {
              const { slug } = useParams();         // /posts/:slug
              return <div>Post: {slug}</div>;
            }

            function SearchPage() {
              const [searchParams, setSearchParams] = useSearchParams();
              const keyword = searchParams.get('keyword') ?? '';

              const handleSearch = (value) => {
                setSearchParams({ keyword: value, page: '1' });
              };
              // ...
            }
            ```

            ## Programmatic Navigation

            ```jsx
            import { useNavigate } from 'react-router';

            function LoginForm() {
              const navigate = useNavigate();

              const handleSubmit = async (e) => {
                e.preventDefault();
                const res = await login(email, password);
                if (res.ok) {
                  navigate('/admin', { replace: true }); // replace tránh back về login page
                }
              };
              // ...
            }
            ```

            ## Data Loading với loader

            ```jsx
            const router = createBrowserRouter([
              {
                path: 'posts/:slug',
                loader: async ({ params }) => {
                  const res = await fetch(`/api/posts/${params.slug}`);
                  if (!res.ok) throw new Response('Not Found', { status: 404 });
                  return res.json();
                },
                element: <PostDetailPage />,
              },
            ]);

            function PostDetailPage() {
              const { data } = useLoaderData(); // type-safe với TypeScript
              return <article>{data.title}</article>;
            }
            ```

            `loader` chạy trước khi component render — giải quyết vấn đề waterfall request và loading state phức tạp.

            ## Kết luận

            React Router v7 mang đến API rõ ràng hơn và tích hợp data loading tốt hơn. Nếu bạn đang dùng v6,
            migration lên v7 khá nhẹ nhàng ở library mode. Hãy tận dụng `createBrowserRouter` và `loader` để code
            routing sạch và performant hơn.
            """;
}
