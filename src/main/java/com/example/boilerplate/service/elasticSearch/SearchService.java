package com.example.boilerplate.service.elasticSearch;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.example.boilerplate.dto.elasticSearch.RequiredHighlight;
import com.example.boilerplate.dto.elasticSearch.ResultData;
import com.example.boilerplate.dto.response.UserResponse;
import com.example.boilerplate.entity.User;
import com.example.boilerplate.entity.Writer;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.entity.elasticSearch.ReviewerIndexUnit;
import com.example.boilerplate.repository.IReviewerRepository;
import com.example.boilerplate.repository.IUserRepository;
import com.example.boilerplate.repository.elasticSearch.IIndexUnitRepository;
import com.example.boilerplate.repository.elasticSearch.IReviewerIndexUnitRepository;
import com.example.boilerplate.util.enums.SearchType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final IIndexUnitRepository indexUnitRepository;

    private final IUserRepository userRepository;

    private final IReviewerRepository reviewerRepository;

    @Autowired
    private IReviewerIndexUnitRepository reviewerIndexUnitRepository;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    private ElasticsearchTemplate template;

    public SearchService(IIndexUnitRepository indexUnitRepository,
        IUserRepository userRepository, IReviewerRepository reviewerRepository) {
        this.indexUnitRepository = indexUnitRepository;
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
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

    private List<String> mapToStringArrayAdvanced(List<HighlightField> highlightFields) {
        List<String> retVal = new ArrayList<>();
        for(HighlightField hf : highlightFields){
            for(Text text : hf.getFragments()) {
                retVal.add(text.string() + "...");
            }
        }
        return retVal;
    }

    public List<ResultData> getAll() {

        List<ResultData> results = new ArrayList<ResultData>();
        for (IndexUnit indexUnit : indexUnitRepository.findAll()) {
            results.add(new ResultData(indexUnit.getTitle(), indexUnit.getKeywords(), indexUnit.getWriter(),
                indexUnit.getGenres(), indexUnit.getFilename(), ""));
        }

        return results;
    }

    public List<ResultData> getAllReviewers() {

        List<ResultData> results = new ArrayList<ResultData>();
        for (ReviewerIndexUnit reviewerIndexUnit : reviewerIndexUnitRepository.findAll()) {
            results.add(new ResultData(reviewerIndexUnit.getEmail(), reviewerIndexUnit.getLocation().toString(), String.valueOf(reviewerIndexUnit.getLocation().getLat()),
                String.valueOf(reviewerIndexUnit.getLocation().getLon()), "", ""));
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

    public List<UserResponse> getAvailableReviewers(String email) throws Exception {
        User user = userRepository.findOneByEmail(email).get();
        Writer writer = user.getWriter();

        List<UserResponse> userResponses = new ArrayList<>();

        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("b0fc7df42d5e4b73ac60115bb7e0c7db");
        JOpenCageForwardRequest jOpenCageForwardRequest = new JOpenCageForwardRequest(
            writer.getUser().getCity() + ", " + writer.getUser().getCountry());
        jOpenCageForwardRequest.setMinConfidence(1);
        jOpenCageForwardRequest.setNoAnnotations(false);
        jOpenCageForwardRequest.setNoDedupe(true);
        JOpenCageResponse jOpenCageResponse = jOpenCageGeocoder.forward(jOpenCageForwardRequest);
        double writersLat = jOpenCageResponse.getResults().get(0).getGeometry().getLat();
        double writersLng = jOpenCageResponse.getResults().get(0).getGeometry().getLng();
        System.out
            .println("Writers lat and lng for " + writer.getUser().getCity() + ": " + writersLat + ", " + writersLng);
//        for (Reviewer reviewer : reviewerRepository.findAll()) {
//            jOpenCageForwardRequest = new JOpenCageForwardRequest(
//                reviewer.getUser().getCity() + ", " + reviewer.getUser().getCountry());
//            jOpenCageForwardRequest.setMinConfidence(1);
//            jOpenCageForwardRequest.setNoAnnotations(false);
//            jOpenCageForwardRequest.setNoDedupe(true);
//            jOpenCageResponse = jOpenCageGeocoder.forward(jOpenCageForwardRequest);
//            double reviewerLat = jOpenCageResponse.getResults().get(0).getGeometry().getLat();
//            double reviewersLng = jOpenCageResponse.getResults().get(0).getGeometry().getLng();
//            System.out.println(
//                "Reviewers lat and lng for " + reviewer.getUser().getCity() + ": " + reviewerLat + ", " + reviewersLng);
//
//
//            int R = 6371;
//            double latDistance = Math.toRadians(reviewerLat - writersLat);
//            double lonDistance = Math.toRadians(reviewersLng - writersLng);
//            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(writersLat)) * Math.cos(Math.toRadians(reviewerLat))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//            double distance = R * c; // convert to meters
//
//            System.out.println("Distance in km: " +  distance);
//
//            if(distance > 100) {
//                UserResponse userResponse = new UserResponse();
//                userResponse.setId(reviewer.getUser().getId());
//                userResponse.setEmail(reviewer.getUser().getEmail());
//                userResponse.setCity(reviewer.getUser().getCity());
//                userResponse.setCountry(reviewer.getUser().getCountry());
//                userResponses.add(userResponse);
//            }
//
//        }

        SearchRequest searchRequest = new SearchRequest("reviewer-unit");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        GeoDistanceQueryBuilder myQuery = QueryBuilders.geoDistanceQuery("location").
            point(writersLat, writersLng).distance(100, DistanceUnit.KILOMETERS);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().mustNot(myQuery);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        for(SearchHit searchHit : searchResponse.getHits().getHits()) {
            ReviewerIndexUnit reviewerIndexUnit = new ObjectMapper().readValue(searchHit.getSourceAsString(), ReviewerIndexUnit.class);
            UserResponse userResponse = new UserResponse();
            User reviewer = userRepository.findOneByEmail(reviewerIndexUnit.getEmail()).get();
            if(!Collections.disjoint(reviewer.getGenres(), writer.getUser().getGenres())){
                userResponse.setEmail(reviewer.getEmail());
                userResponse.setCity(reviewer.getCity());
                userResponse.setCountry(reviewer.getCountry());
                userResponse.setId(reviewer.getId());
                userResponses.add(userResponse);
            }
        }

        return userResponses;
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
