package com.example.boilerplate.service.elasticSearch;

import com.example.boilerplate.dto.elasticSearch.RequiredHighlight;
import com.example.boilerplate.dto.elasticSearch.ResultData;
import com.example.boilerplate.dto.elasticSearch.SearchFilter;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.repository.elasticSearch.IIndexUnitRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final IIndexUnitRepository indexUnitRepository;

    public SearchService(IIndexUnitRepository indexUnitRepository) {
        this.indexUnitRepository = indexUnitRepository;
    }

    public List<ResultData> asd(String title) {
        List<ResultData> results = new ArrayList<ResultData>();
        for(IndexUnit indexUnit: indexUnitRepository.findByTitle(title)){
            results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                indexUnit.getGenres(), indexUnit.getFilename(), ""));
        }
        return results;
    }

    public List<ResultData> asdd(String title, String keywords) {
        List<ResultData> results = new ArrayList<ResultData>();
        for(IndexUnit indexUnit: indexUnitRepository.findByTitleAndKeywords(title, keywords)){
            results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                indexUnit.getGenres(), indexUnit.getFilename(), ""));
        }
        return results;
    }

    public List<ResultData> getResults(QueryBuilder query,
        List<RequiredHighlight> requiredHighlights) {
        if (query == null) {
            return null;
        }

        List<ResultData> results = new ArrayList<ResultData>();
        for (IndexUnit indexUnit : indexUnitRepository.search(query)) {
            results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                indexUnit.getGenres(), indexUnit.getFilename(), ""));
        }

        return results;
    }

    public List<ResultData> getAll() {

        List<ResultData> results = new ArrayList<ResultData>();
        for (IndexUnit indexUnit : indexUnitRepository.findAll()) {
            results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                indexUnit.getGenres(), indexUnit.getFilename(), ""));
        }

        return results;
    }

    public List<ResultData> searchWithFilters(SearchFilter searchFilter) {

        List<ResultData> results = new ArrayList<ResultData>();
        for (IndexUnit indexUnit : indexUnitRepository.findAll()) {
            results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                indexUnit.getGenres(), indexUnit.getFilename(), ""));
        }

        return results.stream()
            .filter(indexUnit -> {
                if(searchFilter.getTitle() != null){
                    return indexUnit.getTitle().toLowerCase().contains(searchFilter.getTitle().toLowerCase());
                } else {
                    return true;
                }
            }).filter(indexUnit -> {
                if(searchFilter.getKeywords() != null){
                    return indexUnit.getKeywords().toLowerCase().contains(searchFilter.getKeywords().toLowerCase());
                } else {
                    return true;
                }
            }).filter(indexUnit -> {
                if(searchFilter.getWriter() != null){
                    return indexUnit.getWriter().toLowerCase().contains(searchFilter.getWriter().toLowerCase());
                } else {
                    return true;
                }
            }).filter(indexUnit -> {
                if(searchFilter.getGenres() != null){
                    return indexUnit.getGenres().toLowerCase().contains(searchFilter.getGenres().toLowerCase());
                } else {
                    return true;
                }
            })
            .filter(indexUnit -> {
                if(searchFilter.getNotTitle() != null){
                    return !indexUnit.getTitle().toLowerCase().contains(searchFilter.getNotTitle().toLowerCase());
                } else {
                    return true;
                }
            }).filter(indexUnit -> {
                if(searchFilter.getNotKeywords() != null){
                    return indexUnit.getKeywords().toLowerCase().contains(searchFilter.getNotKeywords().toLowerCase());
                } else {
                    return true;
                }
            }).filter(indexUnit -> {
                if(searchFilter.getNotWriter() != null){
                    return !indexUnit.getWriter().toLowerCase().contains(searchFilter.getNotWriter().toLowerCase());
                } else {
                    return true;
                }
            }).filter(indexUnit -> {
                if(searchFilter.getNotGenres() != null){
                    return !indexUnit.getGenres().toLowerCase().contains(searchFilter.getNotGenres().toLowerCase());
                } else {
                    return true;
                }
            })
            .collect(Collectors.toList());
    }
}
