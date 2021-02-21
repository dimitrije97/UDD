package com.example.boilerplate.service;

import com.example.boilerplate.dto.response.GenreResponse;
import java.util.List;

public interface IGenreService {

    List<GenreResponse> getAllGenres();
}
