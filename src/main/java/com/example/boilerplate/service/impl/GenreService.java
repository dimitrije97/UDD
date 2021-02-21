package com.example.boilerplate.service.impl;

import com.example.boilerplate.dto.response.GenreResponse;
import com.example.boilerplate.repository.IGenreRepository;
import com.example.boilerplate.service.IGenreService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class GenreService implements IGenreService {

    private final IGenreRepository genreRepository;

    private final ModelMapper modelMapper;

    public GenreService(IGenreRepository genreRepository, ModelMapper modelMapper) {

        this.genreRepository = genreRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GenreResponse> getAllGenres() {
        var genres = genreRepository.findAll();

        return genres.stream()
            .map(genre -> modelMapper.map(genre, GenreResponse.class))
            .collect(Collectors.toList());
    }
}
