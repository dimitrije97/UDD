package com.example.boilerplate.entity.elasticSearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "lib")
public class IndexUnit {

    @Id
    @Field(type = FieldType.Keyword)
    private String filename;

    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian")
    private String content;

    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian")
    private String title;

    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian")
    private String keywords;

    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian")
    private String genres;

    @Field(type = FieldType.Text, analyzer = "serbian", searchAnalyzer = "serbian")
    private String writer;
}
