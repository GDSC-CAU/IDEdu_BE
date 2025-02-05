package com.gdg.backend.domain.mapping;

import com.gdg.backend.domain.course.entity.Course;
import com.gdg.backend.domain.document.entity.Document;
import jakarta.persistence.*;

@Entity
public class ClassDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
}
