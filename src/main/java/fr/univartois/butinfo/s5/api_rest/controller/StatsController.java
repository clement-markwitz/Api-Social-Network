package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDto;
import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDtoPost;
import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDtoUser;
import fr.univartois.butinfo.s5.api_rest.mapper.StatsMapper;
import fr.univartois.butinfo.s5.api_rest.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Controller for statistics endpoints.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final StatsMapper statsMapper;

    /**
     * Constructor for StatsController.
     *
     * @param statsService the statistics service
     * @param statsMapper  the statistics mapper
     */
    public StatsController(StatsService statsService, StatsMapper statsMapper) {
        this.statsService = statsService;
        this.statsMapper = statsMapper;
    }

    /**
     * Convert LocalDate to start of day LocalDateTime.
     *
     * @param date the LocalDate
     * @return the start of day LocalDateTime or null if date is null
     */
    private LocalDateTime toStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Convert LocalDate to end of day LocalDateTime.
     *
     * @param date the LocalDate
     * @return the end of day LocalDateTime or null if date is null
     */
    private LocalDateTime toEndOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX) : null;
    }

    /**
     * Get global statistics with optional date range.
     *
     * @param startDate the start date (optional)
     * @param endDate   the end date (optional)
     * @return ResponseEntity with StatsDto
     */
    @GetMapping
    @Operation(summary = "Get global statistics", description = "Retrieve global statistics with optional date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global statistics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    public ResponseEntity<StatsDto> getGlobalStats(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime start = toStartOfDay(startDate);
        LocalDateTime end = toEndOfDay(endDate);

        long users = statsService.countUsers(start, end);
        long posts = statsService.countPosts(start, end);
        long communities = statsService.countCommunities(start, end);
        long messages = statsService.countMessages(start, end);

        return ResponseEntity.ok(statsMapper.toStatsDto(users, posts, communities, messages));
    }

    /**
     * Get user statistics with optional date range.
     *
     * @param startDate the start date (optional)
     * @param endDate   the end date (optional)
     * @return ResponseEntity with StatsDtoUser
     */
    @GetMapping("/users")
    @Operation(summary = "Get user statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    public ResponseEntity<StatsDtoUser> getUserStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        long count = statsService.countUsers(toStartOfDay(startDate), toEndOfDay(endDate));
        return ResponseEntity.ok(statsMapper.toStatsDtoUser(count));
    }

    /**
     * Get post statistics with optional date range.
     *
     * @param startDate the start date (optional)
     * @param endDate   the end date (optional)
     * @return ResponseEntity with StatsDtoPost
     */
    @GetMapping("/posts")
    @Operation(summary = "Get post statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post statistics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    public ResponseEntity<StatsDtoPost> getPostStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        long count = statsService.countPosts(toStartOfDay(startDate), toEndOfDay(endDate));
        return ResponseEntity.ok(statsMapper.toStatsDtoPost(count));
    }
}