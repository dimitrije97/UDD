package com.example.boilerplate.repository.elasticSearch;

import com.example.boilerplate.entity.elasticSearch.ReviewerIndexUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewerIndexUnitRepository extends ElasticsearchRepository<ReviewerIndexUnit, String> {

}
