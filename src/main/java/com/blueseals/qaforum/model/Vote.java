package com.blueseals.qaforum.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "votes",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;

    public enum VoteType {UP, DOWN}

}
