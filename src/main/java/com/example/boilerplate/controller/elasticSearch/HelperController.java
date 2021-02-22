package com.example.boilerplate.controller.elasticSearch;

import com.example.boilerplate.dto.elasticSearch.AdvancedQuery;
import com.example.boilerplate.dto.elasticSearch.RequiredHighlight;
import com.example.boilerplate.dto.elasticSearch.ResultData;
import com.example.boilerplate.dto.elasticSearch.SearchFilter;
import com.example.boilerplate.dto.elasticSearch.SimpleQuery;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.repository.elasticSearch.IIndexUnitRepository;
import com.example.boilerplate.service.elasticSearch.SearchService;
import com.example.boilerplate.util.enums.SearchType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.queryparser.classic.ParseException;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/helper")
public class HelperController {

    private final SearchService searchService;

    private final IIndexUnitRepository indexUnitRepository;

    public HelperController(SearchService searchService,
        IIndexUnitRepository indexUnitRepository) {
        this.searchService = searchService;
        this.indexUnitRepository = indexUnitRepository;
    }

    @PostMapping("/term")
    public ResponseEntity<List<ResultData>> searchTermQuery(@RequestBody SimpleQuery simpleQuery) throws Exception {
        QueryBuilder query = buildQuery(SearchType.regular, simpleQuery.getField(),
            simpleQuery.getValue().toLowerCase());
        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        rh.add(new RequiredHighlight(simpleQuery.getField(), simpleQuery.getValue().toLowerCase()));
        List<ResultData> results = searchService.getResults(query, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping(value = "/search/fuzzy", consumes = "application/json")
    public ResponseEntity<List<ResultData>> searchFuzzy(@RequestBody SimpleQuery simpleQuery) throws Exception {
        QueryBuilder query = buildQuery(SearchType.fuzzy, simpleQuery.getField(), simpleQuery.getValue());
        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        rh.add(new RequiredHighlight(simpleQuery.getField(), simpleQuery.getValue()));
        List<ResultData> results = searchService.getResults(query, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping(value = "/search/prefix", consumes = "application/json")
    public ResponseEntity<List<ResultData>> searchPrefix(@RequestBody SimpleQuery simpleQuery) throws Exception {
        QueryBuilder query = buildQuery(SearchType.prefix, simpleQuery.getField(), simpleQuery.getValue());
        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        rh.add(new RequiredHighlight(simpleQuery.getField(), simpleQuery.getValue()));
        List<ResultData> results = searchService.getResults(query, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping(value = "/search/range", consumes = "application/json")
    public ResponseEntity<List<ResultData>> searchRange(@RequestBody SimpleQuery simpleQuery) throws Exception {
        QueryBuilder query = buildQuery(SearchType.range, simpleQuery.getField(), simpleQuery.getValue());
        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        rh.add(new RequiredHighlight(simpleQuery.getField(), simpleQuery.getValue()));
        List<ResultData> results = searchService.getResults(query, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping(value = "/search/phrase", consumes = "application/json")
    public ResponseEntity<List<ResultData>> searchPhrase(@RequestBody SimpleQuery simpleQuery) throws Exception {
        QueryBuilder query = buildQuery(SearchType.phrase, simpleQuery.getField(), simpleQuery.getValue());
        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        rh.add(new RequiredHighlight(simpleQuery.getField(), simpleQuery.getValue()));
        List<ResultData> results = searchService.getResults(query, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping(value = "/search/boolean", consumes = "application/json")
    public ResponseEntity<List<ResultData>> searchBoolean(@RequestBody AdvancedQuery advancedQuery) throws Exception {
        QueryBuilder query1 = buildQuery(SearchType.regular, advancedQuery.getField1(), advancedQuery.getValue1());
        QueryBuilder query2 = buildQuery(SearchType.regular, advancedQuery.getField2(), advancedQuery.getValue2());

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (advancedQuery.getOperation().equalsIgnoreCase("AND")) {
            builder.must(query1);
            builder.must(query2);
        } else if (advancedQuery.getOperation().equalsIgnoreCase("OR")) {
            builder.should(query1);
            builder.should(query2);
        } else if (advancedQuery.getOperation().equalsIgnoreCase("NOT")) {
            builder.must(query1);
            builder.mustNot(query2);
        }

        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        rh.add(new RequiredHighlight(advancedQuery.getField1(), advancedQuery.getValue1()));
        rh.add(new RequiredHighlight(advancedQuery.getField2(), advancedQuery.getValue2()));
        List<ResultData> results = searchService.getResults(builder, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @PostMapping(value = "/search/queryParser", consumes = "application/json")
    public ResponseEntity<List<ResultData>> search(@RequestBody SimpleQuery simpleQuery) throws Exception {
        QueryBuilder query = QueryBuilders.queryStringQuery(simpleQuery.getValue());
        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();
        List<ResultData> results = searchService.getResults(query, rh);
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public List<ResultData> findAllWithFilter(SearchFilter searchFilter) throws Exception {
            List<ResultData> results = new ArrayList<ResultData>();
            for (IndexUnit indexUnit : indexUnitRepository.findAll()) {
                results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                    indexUnit.getGenres(), indexUnit.getFilename(), ""));
            }

            return results.stream()
                .filter(indexUnit -> {
                    if (searchFilter.getTitle() != null) {
                        return indexUnit.getTitle().toLowerCase().contains(searchFilter.getTitle().toLowerCase());
                    } else {
                        return true;
                    }
                }).filter(indexUnit -> {
                    if (searchFilter.getKeywords() != null) {
                        return indexUnit.getKeywords().toLowerCase().contains(searchFilter.getKeywords().toLowerCase());
                    } else {
                        return true;
                    }
                }).filter(indexUnit -> {
                    if (searchFilter.getWriter() != null) {
                        return indexUnit.getWriter().toLowerCase().contains(searchFilter.getWriter().toLowerCase());
                    } else {
                        return true;
                    }
                }).filter(indexUnit -> {
                    if (searchFilter.getGenres() != null) {
                        return indexUnit.getGenres().toLowerCase().contains(searchFilter.getGenres().toLowerCase());
                    } else {
                        return true;
                    }
                })
                .filter(indexUnit -> {
                    if (searchFilter.getNotTitle() != null) {
                        return !indexUnit.getTitle().toLowerCase().contains(searchFilter.getNotTitle().toLowerCase());
                    } else {
                        return true;
                    }
                }).filter(indexUnit -> {
                    if (searchFilter.getNotKeywords() != null) {
                        return indexUnit.getKeywords().toLowerCase().contains(searchFilter.getNotKeywords().toLowerCase());
                    } else {
                        return true;
                    }
                }).filter(indexUnit -> {
                    if (searchFilter.getNotWriter() != null) {
                        return !indexUnit.getWriter().toLowerCase().contains(searchFilter.getNotWriter().toLowerCase());
                    } else {
                        return true;
                    }
                }).filter(indexUnit -> {
                    if (searchFilter.getNotGenres() != null) {
                        return !indexUnit.getGenres().toLowerCase().contains(searchFilter.getNotGenres().toLowerCase());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public static QueryBuilder buildQuery(SearchType queryType, String field, String value)
        throws IllegalArgumentException, ParseException {
        String errorMessage = "";
        if (field == null || field.equals("")) {
            errorMessage += "Field not specified";
        }
        if (value == null) {
            if (!errorMessage.equals("")) {
                errorMessage += "\n";
            }
            errorMessage += "Value not specified";
        }
        if (!errorMessage.equals("")) {
            throw new IllegalArgumentException(errorMessage);
        }

        QueryBuilder retVal = null;
        if (queryType.equals(SearchType.regular)) {
            retVal = QueryBuilders.termQuery(field, value);
        } else if (queryType.equals(SearchType.fuzzy)) {
            retVal = QueryBuilders.fuzzyQuery(field, value).fuzziness(Fuzziness.fromEdits(1));
        } else if (queryType.equals(SearchType.prefix)) {
            retVal = QueryBuilders.prefixQuery(field, value);
        } else if (queryType.equals(SearchType.range)) {
            String[] values = value.split(" ");
            retVal = QueryBuilders.rangeQuery(field).from(values[0]).to(values[1]);
        } else {
            retVal = QueryBuilders.matchPhraseQuery(field, value);
        }

        return retVal;
    }
}
