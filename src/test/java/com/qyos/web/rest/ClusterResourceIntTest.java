package com.qyos.web.rest;

import com.qyos.Application;
import com.qyos.domain.Cluster;
import com.qyos.repository.ClusterRepository;
import com.qyos.repository.search.ClusterSearchRepository;

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
 * Test class for the ClusterResource REST controller.
 *
 * @see ClusterResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ClusterResourceIntTest {

    private static final String DEFAULT_NAME = "AAA";
    private static final String UPDATED_NAME = "BBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private ClusterSearchRepository clusterSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restClusterMockMvc;

    private Cluster cluster;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClusterResource clusterResource = new ClusterResource();
        ReflectionTestUtils.setField(clusterResource, "clusterSearchRepository", clusterSearchRepository);
        ReflectionTestUtils.setField(clusterResource, "clusterRepository", clusterRepository);
        this.restClusterMockMvc = MockMvcBuilders.standaloneSetup(clusterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        cluster = new Cluster();
        cluster.setName(DEFAULT_NAME);
        cluster.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createCluster() throws Exception {
        int databaseSizeBeforeCreate = clusterRepository.findAll().size();

        // Create the Cluster

        restClusterMockMvc.perform(post("/api/clusters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cluster)))
                .andExpect(status().isCreated());

        // Validate the Cluster in the database
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeCreate + 1);
        Cluster testCluster = clusters.get(clusters.size() - 1);
        assertThat(testCluster.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCluster.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clusterRepository.findAll().size();
        // set the field null
        cluster.setName(null);

        // Create the Cluster, which fails.

        restClusterMockMvc.perform(post("/api/clusters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cluster)))
                .andExpect(status().isBadRequest());

        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClusters() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusters
        restClusterMockMvc.perform(get("/api/clusters?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cluster.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", cluster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cluster.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCluster() throws Exception {
        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

		int databaseSizeBeforeUpdate = clusterRepository.findAll().size();

        // Update the cluster
        cluster.setName(UPDATED_NAME);
        cluster.setDescription(UPDATED_DESCRIPTION);

        restClusterMockMvc.perform(put("/api/clusters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cluster)))
                .andExpect(status().isOk());

        // Validate the Cluster in the database
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeUpdate);
        Cluster testCluster = clusters.get(clusters.size() - 1);
        assertThat(testCluster.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCluster.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

		int databaseSizeBeforeDelete = clusterRepository.findAll().size();

        // Get the cluster
        restClusterMockMvc.perform(delete("/api/clusters/{id}", cluster.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeDelete - 1);
    }
}
