package com.qyos.repository.search;

import com.qyos.domain.Node;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Node entity.
 */
public interface NodeSearchRepository extends ElasticsearchRepository<Node, Long> {
}
