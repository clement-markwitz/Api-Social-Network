package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDto;
import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDtoPost;
import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDtoUser;
import fr.univartois.butinfo.s5.api_rest.mapper.StatsMapper;
import fr.univartois.butinfo.s5.api_rest.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final StatsMapper statsMapper;

    public StatsController(StatsService statsService, StatsMapper statsMapper) {
        this.statsService = statsService;
        this.statsMapper = statsMapper;
    }

    @GetMapping
    @Operation(summary = "Get global statistics", description = "Retrieve global statistics including total users, posts, communities, and messages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global statistics retrieved successfully")
    })
    public ResponseEntity<StatsDto> getGlobalStats() {
        long users = statsService.countUsers();
        long posts = statsService.countPosts();
        long communities = statsService.countCommunities();
        long messages = statsService.countMessages();

        return ResponseEntity.ok(statsMapper.toStatsDto(users, posts, communities, messages));
    }

    @GetMapping("/users")
    @Operation(summary = "Get user statistics", description = "Retrieve statistics related to users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully")
    })
    public ResponseEntity<StatsDtoUser> getUserStats() {
        long count = statsService.countUsers();
        return ResponseEntity.ok(statsMapper.toStatsDtoUser(count));
    }

    @GetMapping("/posts")
    @Operation(summary = "Get post statistics", description = "Retrieve statistics related to posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post statistics retrieved successfully")
    })
    public ResponseEntity<StatsDtoPost> getPostStats() {
        long count = statsService.countPosts();
        return ResponseEntity.ok(statsMapper.toStatsDtoPost(count));
    }
}