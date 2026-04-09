package com.silicon.model;

import com.silicon.enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "game_sessions")

public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(name = "current_location_index", nullable = false)
    private int currentLocationIndex;

    @Column(name = "cash", nullable = false)
    private int cash;

    @Column(name = "morale", nullable = false)
    private int morale;

    @Column(name = "coffee", nullable = false)
    private int coffee;

    @Column(name = "hype", nullable = false)
    private int hype;

    @Column(name = "bugs", nullable = false)
    private int bugs;

    @Column(name = "progress", nullable = false)
    private int progress;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status", nullable = false)
    private GameStatus status;
}
