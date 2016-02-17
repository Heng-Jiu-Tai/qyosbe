package com.qyos.web.rest;

import com.qyos.Application;
import com.qyos.domain.Node;
import com.qyos.repository.NodeRepository;
import com.qyos.repository.search.NodeSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the NodeResource REST controller.
 *
 * @see NodeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class NodeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private NodeRepository nodeRepository;

    @Inject
    private NodeSearchRepository nodeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restNodeMockMvc;

    private Node node;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NodeResource nodeResource = new NodeResource();
        ReflectionTestUtils.setField(nodeResource, "nodeSearchRepository", nodeSearchRepository);
        ReflectionTestUtils.setField(nodeResource, "nodeRepository", nodeRepository);
        this.restNodeMockMvc = MockMvcBuilders.standaloneSetup(nodeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        node = new Node();
        node.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createNode() throws Exception {
        int databaseSizeBeforeCreate = nodeRepository.findAll().size();

        // Create the Node

        restNodeMockMvc.perform(post("/api/nodes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(node)))
                .andExpect(status().isCreated());

        // Validate the Node in the database
        List<Node> nodes = nodeRepository.findAll();
        assertThat(nodes).hasSize(databaseSizeBeforeCreate + 1);
        Node testNode = nodes.get(nodes.size() - 1);
        assertThat(testNode.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = nodeRepository.findAll().size();
        // set the field null
        node.setName(null);

        // Create the Node, which fails.

        restNodeMockMvc.perform(post("/api/nodes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(node)))
                .andExpect(status().isBadRequest());

        List<Node> nodes = nodeRepository.findAll();
        assertThat(nodes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNodes() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodes
        restNodeMockMvc.perform(get("/api/nodes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get the node
        restNodeMockMvc.perform(get("/api/nodes/{id}", node.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(node.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNode() throws Exception {
        // Get the node
        restNodeMockMvc.perform(get("/api/nodes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

		int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

        // Update the node
        node.setName(UPDATED_NAME);

        restNodeMockMvc.perform(put("/api/nodes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(node)))
                .andExpect(status().isOk());

        // Validate the Node in the database
        List<Node> nodes = nodeRepository.findAll();
        assertThat(nodes).hasSize(databaseSizeBeforeUpdate);
        Node testNode = nodes.get(nodes.size() - 1);
        assertThat(testNode.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

		int databaseSizeBeforeDelete = nodeRepository.findAll().size();

        // Get the node
        restNodeMockMvc.perform(delete("/api/nodes/{id}", node.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Node> nodes = nodeRepository.findAll();
        assertThat(nodes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
