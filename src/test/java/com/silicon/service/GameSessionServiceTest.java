package com.silicon.service;

import com.silicon.enums.GameStatus;
import com.silicon.model.GameSession;
import com.silicon.model.Location;
import com.silicon.repositories.GameSessionRepository;
import com.silicon.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSessionServiceTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private WeatherService weatherService;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private GameSessionService gameSessionService;

    private UUID gameId;
    private GameSession game;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();

        game = GameSession.builder()
                .id(gameId)
                .cash(100)
                .dayNumber(1)
                .bugs(0)
                .coffee(10)
                .morale(80)
                .currentLocationIndex(0)
                .hype(20)
                .progress(0)
                .status(GameStatus.IN_PROGRESS)
                .build();
    }

    @Test
    void rest_shouldIncreaseMoraleAndDayNumber() {
        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.rest(gameId);

        assertEquals(2, result.getDayNumber());
        assertEquals(90, result.getMorale());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());

        verify(gameSessionRepository).findById(gameId);
        verify(gameSessionRepository).save(game);
    }

    @Test
    void rest_shouldNotLetMoraleGoAbove100() {
        game.setMorale(95);

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.rest(gameId);

        assertEquals(100, result.getMorale());
        assertEquals(2, result.getDayNumber());
    }

    @Test
    void workOnProduct_shouldReduceBugsMoraleAndCoffee() {
        game.setBugs(5);

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.workOnProduct(gameId);

        assertEquals(2, result.getDayNumber());
        assertEquals(2, result.getBugs());
        assertEquals(78, result.getMorale());
        assertEquals(9, result.getCoffee());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void workOnProduct_shouldSetStatusToLostWhenCoffeeReachesZero() {
        game.setCoffee(1);

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.workOnProduct(gameId);

        assertEquals(0, result.getCoffee());
        assertEquals(GameStatus.LOST, result.getStatus());
    }

    @Test
    void marketingPush_shouldDecreaseCashAndCoffeeAndIncreaseHype() {
        game.setBugs(3);

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.marketingPush(gameId);

        assertEquals(2, result.getDayNumber());
        assertEquals(85, result.getCash());
        assertEquals(9, result.getCoffee());
        assertEquals(30, result.getHype());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void marketingPush_shouldIncreaseHypeBy5WhenBugsAreGreaterThan5() {
        game.setBugs(6);

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.marketingPush(gameId);

        assertEquals(25, result.getHype());
    }

    @Test
    void travel_shouldMovePlayerForwardAndUpdateProgress() {
        Location location = Location.builder()
                .routeIndex(1)
                .cityName("Santa Clara")
                .latitude(37.3541)
                .longitude(-121.9552)
                .build();

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(locationRepository.findByRouteIndex(1)).thenReturn(location);
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.travel(gameId);

        assertEquals(1, result.getCurrentLocationIndex());
        assertEquals(2, result.getDayNumber());
        assertEquals(2, result.getBugs());
        assertEquals(16, result.getProgress());
        assertEquals(9, result.getCoffee());

        verify(weatherService).applyWeatherEffects(game, 37.3541, -121.9552);
    }

    @Test
    void travel_shouldSetStatusToWonWhenReachingFinalLocation() {
        game.setCurrentLocationIndex(5);

        Location location = Location.builder()
                .routeIndex(6)
                .cityName("San Francisco")
                .latitude(37.7749)
                .longitude(-122.4194)
                .build();

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(locationRepository.findByRouteIndex(6)).thenReturn(location);
        when(gameSessionRepository.save(any(GameSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GameSession result = gameSessionService.travel(gameId);

        assertEquals(6, result.getCurrentLocationIndex());
        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(100, result.getProgress());
    }

    @Test
    void rest_shouldReturnGameUnchangedIfGameIsAlreadyWon() {
        game.setStatus(GameStatus.WON);

        when(gameSessionRepository.findById(gameId)).thenReturn(Optional.of(game));

        GameSession result = gameSessionService.rest(gameId);

        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(1, result.getDayNumber());

        verify(gameSessionRepository, never()).save(any());
    }
}