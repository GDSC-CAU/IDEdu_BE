package com.gdg.backend.domain.operation.repository;

import com.gdg.backend.domain.operation.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    @Query("SELECT o FROM Operation o JOIN FETCH o.member JOIN FETCH o.document WHERE o.document.id = :documentId AND o.version > :baseVersion")
    List<Operation> findByDocumentIdAndVersionGreaterThanFetchJoin(@Param("documentId") Long documentId, @Param("baseVersion") Long version);

    void deleteByDocumentId(Long testDocId);
}

