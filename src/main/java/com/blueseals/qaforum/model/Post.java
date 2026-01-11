package com.blueseals.qaforum.model;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Post content is required")
    private String contentText;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean accepted = false;

    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    private ForumThread thread;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // -- file attachment fields --
    private String fileName;
    private String fileType;

    // soft delete flag
    @Column(nullable = false)
    private boolean deleted = false;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();

    public int getScore() {
        return votes.stream()
                .mapToInt(v -> v.getType() == Vote.VoteType.UP ? 1 : -1)
                .sum();
    }
}
