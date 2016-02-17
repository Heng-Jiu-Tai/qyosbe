package com.qyos.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.qyos.domain.Node;
import com.qyos.repository.NodeRepository;
import com.qyos.repository.search.NodeSearchRepository;
import com.qyos.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Node.
 */
@RestController
@RequestMapping("/api")
public class NodeResource {

    private final Logger log = LoggerFactory.getLogger(NodeResource.class);
        
    @Inject
    private NodeRepository nodeRepository;
    
    @Inject
    private NodeSearchRepository nodeSearchRepository;
    
    /**
     * POST  /nodes -> Create a new node.
     */
    @RequestMapping(value = "/nodes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Node> createNode(@Valid @RequestBody Node node) throws URISyntaxException {
        log.debug("REST request to save Node : {}", node);
        if (node.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("node", "idexists", "A new node cannot already have an ID")).body(null);
        }
        Node result = nodeRepository.save(node);
        nodeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/nodes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("node", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /nodes -> Updates an existing node.
     */
    @RequestMapping(value = "/nodes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Node> updateNode(@Valid @RequestBody Node node) throws URISyntaxException {
        log.debug("REST request to update Node : {}", node);
        if (node.getId() == null) {
            return createNode(node);
        }
        Node result = nodeRepository.save(node);
        nodeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("node", node.getId().toString()))
            .body(result);
    }

    /**
     * GET  /nodes -> get all the nodes.
     */
    @RequestMapping(value = "/nodes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Node> getAllNodes() {
        log.debug("REST request to get all Nodes");
        return nodeRepository.findAll();
            }

    /**
     * GET  /nodes/:id -> get the "id" node.
     */
    @RequestMapping(value = "/nodes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Node> getNode(@PathVariable Long id) {
        log.debug("REST request to get Node : {}", id);
        Node node = nodeRepository.findOne(id);
        return Optional.ofNullable(node)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /nodes/:id -> delete the "id" node.
     */
    @RequestMapping(value = "/nodes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        log.debug("REST request to delete Node : {}", id);
        nodeRepository.delete(id);
        nodeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("node", id.toString())).build();
    }

    /**
     * SEARCH  /_search/nodes/:query -> search for the node corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/nodes/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Node> searchNodes(@PathVariable String query) {
        log.debug("REST request to search Nodes for query {}", query);
        return StreamSupport
            .stream(nodeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
