package com.example.boilerplate.controller.elasticSearch;

import com.example.boilerplate.dto.elasticSearch.ResultData;
import com.example.boilerplate.dto.elasticSearch.SimpleQuery;
import com.example.boilerplate.dto.response.UserResponse;
import com.example.boilerplate.service.elasticSearch.SearchService;
import com.example.boilerplate.util.enums.SearchType;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<List<ResultData>> getAll() throws Exception {
        List<ResultData> results = searchService.getAll();
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping
    public List<ResultData> search(@RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "keywords", required = false) String keywords,
        @RequestParam(value = "writer", required = false) String writer,
        @RequestParam(value = "genres", required = false) String genres,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "titleOperation", required = false) String titleOperation,
        @RequestParam(value = "keywordsOperation", required = false) String keywordsOperation,
        @RequestParam(value = "writerOperation", required = false) String writerOperation,
        @RequestParam(value = "genresOperation", required = false) String genresOperation,
        @RequestParam(value = "contentOperation", required = false) String contentOperation,
        @RequestParam(value = "searchType") SearchType searchType) throws Exception {

        return searchService
            .search(title, keywords, writer, genres, content, titleOperation, keywordsOperation, writerOperation,
                genresOperation, contentOperation, searchType);
    }

    @GetMapping("/{email}/writer")
    public List<UserResponse> getAvailableReviewers(@PathVariable String email) throws Exception {
        return searchService.getAvailableReviewers(email);
    }
}
