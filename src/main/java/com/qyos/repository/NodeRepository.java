package com.qyos.repository;

import com.qyos.domain.Node;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Node entity.
 */
public interface NodeRepository extends JpaRepository<Node,Long> {

}
