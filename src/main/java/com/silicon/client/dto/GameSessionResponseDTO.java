package com.silicon.client.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GameSessionResponseDTO {

    private int dayNumber;
    private String cityName;
    private String description;

    private int cash;
    private int morale;
    private int coffee;
    private int hype;
    private int bugs;

    private String weatherSummary;
    private String resultMessage;   // null unless won/lost
    private boolean gameOver;
}