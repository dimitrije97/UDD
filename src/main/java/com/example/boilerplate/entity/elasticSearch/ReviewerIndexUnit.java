package com.example.boilerplate.entity.elasticSearch;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@Getter
@Setter
@Document(indexName = "reviewer-unit")
public class ReviewerIndexUnit {

    @Id
    @Field(type = FieldType.Keyword)
    private String email;

    @GeoPointField
    private GeoPoint location;
}
