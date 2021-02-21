package com.example.boilerplate.controller;

import com.example.boilerplate.dto.response.GenreResponse;
import com.example.boilerplate.service.IGenreService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final IGenreService genreService;

    public GenreController(IGenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreResponse> getAll() {
        return genreService.getAllGenres();
    }
}
