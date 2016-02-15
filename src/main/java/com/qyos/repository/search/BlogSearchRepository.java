package com.qyos.repository.search;

import com.qyos.domain.Blog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Blog entity.
 */
public interface BlogSearchRepository extends ElasticsearchRepository<Blog, Long> {
}
