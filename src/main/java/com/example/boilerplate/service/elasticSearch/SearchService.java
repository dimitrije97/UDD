package com.example.boilerplate.service.elasticSearch;

import com.example.boilerplate.dto.elasticSearch.RequiredHighlight;
import com.example.boilerplate.dto.elasticSearch.ResultData;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.repository.elasticSearch.IIndexUnitRepository;
import com.example.boilerplate.util.enums.SearchType;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final IIndexUnitRepository indexUnitRepository;

    public SearchService(IIndexUnitRepository indexUnitRepository) {
        this.indexUnitRepository = indexUnitRepository;
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

    public List<ResultData> search(String title, String keywords, String writer, String genres, String content,
        String titleOperation,
        String keywordsOperation, String writerOperation, String genresOperation, String contentOperation,
        SearchType searchType) throws Exception {

        List<RequiredHighlight> rh = new ArrayList<RequiredHighlight>();

        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (title != null && !title.equals("null")) {
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

        if (keywords != null && !keywords.equals("null")) {
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

        if (writer != null && !writer.equals("null")) {
            QueryBuilder queryWriter = buildQuery(searchType, "writer", writer.toLowerCase());
            if (writerOperation.equals("AND")) {
                builder.must(queryWriter);
            } else if (writerOperation.equals("NOT")) {
                builder.mustNot(queryWriter);
            } else {
                builder.should(queryWriter);
            }
            rh.add(new RequiredHighlight("writer", writer));
        }

        if (genres != null && !genres.equals("null")) {
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

        if (content != null && !content.equals("null")) {
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

        List<ResultData> results = getResults(builder, rh);

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
