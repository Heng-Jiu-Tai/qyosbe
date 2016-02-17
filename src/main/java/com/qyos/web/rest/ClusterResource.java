package com.qyos.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.qyos.domain.Cluster;
import com.qyos.repository.ClusterRepository;
import com.qyos.repository.search.ClusterSearchRepository;
import com.qyos.web.rest.util.HeaderUtil;
import com.qyos.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Cluster.
 */
@RestController
@RequestMapping("/api")
public class ClusterResource {

    private final Logger log = LoggerFactory.getLogger(ClusterResource.class);
        
    @Inject
    private ClusterRepository clusterRepository;
    
    @Inject
    private ClusterSearchRepository clusterSearchRepository;
    
    /**
     * POST  /clusters -> Create a new cluster.
     */
    @RequestMapping(value = "/clusters",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cluster> createCluster(@Valid @RequestBody Cluster cluster) throws URISyntaxException {
        log.debug("REST request to save Cluster : {}", cluster);
        if (cluster.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cluster", "idexists", "A new cluster cannot already have an ID")).body(null);
        }
        Cluster result = clusterRepository.save(cluster);
        clusterSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/clusters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cluster", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /clusters -> Updates an existing cluster.
     */
    @RequestMapping(value = "/clusters",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cluster> updateCluster(@Valid @RequestBody Cluster cluster) throws URISyntaxException {
        log.debug("REST request to update Cluster : {}", cluster);
        if (cluster.getId() == null) {
            return createCluster(cluster);
        }
        Cluster result = clusterRepository.save(cluster);
        clusterSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cluster", cluster.getId().toString()))
            .body(result);
    }

    /**
     * GET  /clusters -> get all the clusters.
     */
    @RequestMapping(value = "/clusters",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Cluster>> getAllClusters(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Clusters");
        Page<Cluster> page = clusterRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clusters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /clusters/:id -> get the "id" cluster.
     */
    @RequestMapping(value = "/clusters/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cluster> getCluster(@PathVariable Long id) {
        log.debug("REST request to get Cluster : {}", id);
        Cluster cluster = clusterRepository.findOne(id);
        return Optional.ofNullable(cluster)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /clusters/:id -> delete the "id" cluster.
     */
    @RequestMapping(value = "/clusters/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCluster(@PathVariable Long id) {
        log.debug("REST request to delete Cluster : {}", id);
        clusterRepository.delete(id);
        clusterSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cluster", id.toString())).build();
    }

    /**
     * SEARCH  /_search/clusters/:query -> search for the cluster corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/clusters/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Cluster> searchClusters(@PathVariable String query) {
        log.debug("REST request to search Clusters for query {}", query);
        return StreamSupport
            .stream(clusterSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
