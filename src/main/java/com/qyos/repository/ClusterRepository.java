package com.qyos.repository;

import com.qyos.domain.Cluster;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Cluster entity.
 */
public interface ClusterRepository extends JpaRepository<Cluster,Long> {

    @Query("select cluster from Cluster cluster where cluster.user.login = ?#{principal.username}")
    List<Cluster> findByUserIsCurrentUser();

}
