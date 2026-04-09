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
    private int dayNumber;
    private int currentLocationIndex;
    private int cash;
    private int morale;
    private int coffee;
    private int hype;
    private int bugs;

    @Enumerated(EnumType.STRING)
    private GameStatus status;
}
