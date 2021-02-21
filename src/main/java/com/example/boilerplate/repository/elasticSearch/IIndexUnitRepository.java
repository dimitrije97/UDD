package com.example.boilerplate.repository.elasticSearch;

import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IIndexUnitRepository extends ElasticsearchRepository<IndexUnit, String> {

    Iterable<IndexUnit> findByTitle(String title);

    Iterable<IndexUnit> findByKeywords(String keywords);

    Iterable<IndexUnit> findByWriter(String writer);

    Iterable<IndexUnit> findByGenres(String genres);

    Iterable<IndexUnit> findByTitleAndKeywords(String title, String keywords);

    Iterable<IndexUnit> findByTitleAndWriter(String title, String writer);

    Iterable<IndexUnit> findByTitleAndGenres(String title, String genres);

    Iterable<IndexUnit> findByKeywordsAndWriter(String keywords, String writer);

    Iterable<IndexUnit> findByKeywordsAndGenres(String keywords, String genres);

    Iterable<IndexUnit> findByWriterAndGenres(String writer, String genres);

    Iterable<IndexUnit> findByTitleAndKeywordsAndWriter(String title, String keywords, String writer);

    Iterable<IndexUnit> findByTitleAndKeywordsAndGenres(String title, String keywords, String genres);

    Iterable<IndexUnit> findByKeywordsAndWriterAndGenres(String keywords, String writer, String genres);

    Iterable<IndexUnit> findByTitleOrKeywords(String title, String keywords);

    Iterable<IndexUnit> findByTitleOrWriter(String title, String writer);

    Iterable<IndexUnit> findByTitleOrGenres(String title, String genres);

    Iterable<IndexUnit> findByKeywordsOrWriter(String keywords, String writer);

    Iterable<IndexUnit> findByKeywordsOrGenres(String keywords, String genres);

    Iterable<IndexUnit> findByWriterOrGenres(String writer, String genres);

    Iterable<IndexUnit> findByTitleOrKeywordsOrWriter(String title, String keywords, String writer);

    Iterable<IndexUnit> findByTitleOrKeywordsOrGenres(String title, String keywords, String genres);

    Iterable<IndexUnit> findByKeywordsOrWriterOrGenres(String keywords, String writer, String genres);
}
