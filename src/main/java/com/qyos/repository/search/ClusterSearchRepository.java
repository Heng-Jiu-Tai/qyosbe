package com.qyos.repository.search;

import com.qyos.domain.Cluster;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Cluster entity.
 */
public interface ClusterSearchRepository extends ElasticsearchRepository<Cluster, Long> {
}
