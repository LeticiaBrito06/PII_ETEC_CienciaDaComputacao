package com.labquest.backend.controller;

import com.labquest.backend.dtos.game.GameCreateRequest;
import com.labquest.backend.dtos.game.GameFinishRequest;
import com.labquest.backend.dtos.game.GameResponse;
import com.labquest.backend.service.GameService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse start(@Valid @RequestBody GameCreateRequest request) {
        return gameService.start(request);
    }

    @PutMapping("/{id}/finish")
    public GameResponse finish(@PathVariable Integer id, @Valid @RequestBody GameFinishRequest request) {
        return gameService.finish(id, request);
    }

    @GetMapping("/{id}")
    public GameResponse getById(@PathVariable Integer id) {
        return gameService.getById(id);
    }

    @GetMapping
    public List<GameResponse> listByStudent(@RequestParam Integer alunoId) {
        return gameService.listByStudent(alunoId);
    }
}
