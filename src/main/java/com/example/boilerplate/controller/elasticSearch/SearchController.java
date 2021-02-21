package com.example.boilerplate.controller.elasticSearch;

import com.example.boilerplate.dto.elasticSearch.AdvancedQuery;
import com.example.boilerplate.dto.elasticSearch.RequiredHighlight;
import com.example.boilerplate.dto.elasticSearch.ResultData;
import com.example.boilerplate.dto.elasticSearch.SearchFilter;
import com.example.boilerplate.dto.elasticSearch.SimpleQuery;
import com.example.boilerplate.service.elasticSearch.SearchService;
import com.example.boilerplate.util.enums.SearchType;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/search/{title}")
    public List<ResultData> asd(@PathVariable String title) {
        return searchService.asd(title);
    }

    @GetMapping("/search/{title}/{keywords}")
    public List<ResultData> asd(@PathVariable String title, @PathVariable String keywords) {
        return searchService.asdd(title, keywords);
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

    @GetMapping()
    public ResponseEntity<List<ResultData>> findAll() throws Exception {
        List<ResultData> results = searchService.getAll();
        return new ResponseEntity<List<ResultData>>(results, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ResultData>> findAllWithFilter(SearchFilter searchFilter) throws Exception {
        List<ResultData> results = searchService.searchWithFilters(searchFilter);
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

    @PostMapping("/advanced")
    public List<ResultData> advancedSearch(@RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "keywords", required = false) String keywords,
        @RequestParam(value = "writer", required = false) String writer,
        @RequestParam(value = "genres", required = false) String genres,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "titleOperation", required = false) String titleOperation,
        @RequestParam(value = "keywordsOperation", required = false) String keywordsOperation,
        @RequestParam(value = "writerOperation", required = false) String writerOperations,
        @RequestParam(value = "genresOperation", required = false) String genresOperation,
        @RequestParam(value = "contentOperation", required = false) String contentOperation,
        @RequestParam(value = "searchType") SearchType searchType) throws Exception {

        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();

        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (title != null) {
            QueryBuilder queryTitle = buildQuery(searchType, "title", title.toLowerCase());
            if (titleOperation.equals("AND")) {
                builder.must(queryTitle);
            } else if (titleOperation.equals("NOT")) {
                builder.mustNot(queryTitle);
            } else {
                builder.should(queryTitle);
            }
            rh.add(new RequiredHighlight("title", title));
        }

        if (keywords != null) {
            QueryBuilder queryKeywords = buildQuery(searchType, "keywords", keywords.toLowerCase());
            if (keywordsOperation.equals("AND")) {
                builder.must(queryKeywords);
            } else if (keywordsOperation.equals("NOT")) {
                builder.mustNot(queryKeywords);
            } else {
                builder.should(queryKeywords);
            }
            rh.add(new RequiredHighlight("keywords", keywords));
        }

        if (writer != null) {
            QueryBuilder queryWriter = buildQuery(searchType, "writer", writer.toLowerCase());
            if (writerOperations.equals("AND")) {
                builder.must(queryWriter);
            } else if (writerOperations.equals("NOT")) {
                builder.mustNot(queryWriter);
            } else {
                builder.should(queryWriter);
            }
            rh.add(new RequiredHighlight("writer", writer));
        }

        if (genres != null) {
            QueryBuilder queryGenres = buildQuery(searchType, "genres", genres.toLowerCase());
            if (genresOperation.equals("AND")) {
                builder.must(queryGenres);
            } else if (genresOperation.equals("NOT")) {
                builder.mustNot(queryGenres);
            } else {
                builder.should(queryGenres);
            }
            rh.add(new RequiredHighlight("genres", genres));
        }

        if (content != null) {
            QueryBuilder queryContent = QueryBuilders.queryStringQuery(content.toLowerCase());
            if (contentOperation.equals("AND")) {
                builder.must(queryContent);
            } else if (contentOperation.equals("NOT")) {
                builder.mustNot(queryContent);
            } else {
                builder.should(queryContent);
            }
            rh.add(new RequiredHighlight("content", content));
        }

        List<ResultData> results = searchService.getResults(builder, rh);

        return results;
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
