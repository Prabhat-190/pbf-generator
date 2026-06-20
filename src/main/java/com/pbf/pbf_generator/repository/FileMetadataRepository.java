package com.pbf.pbf_generator.repository;

import com.pbf.pbf_generator.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository
        extends JpaRepository<FileMetadata, Long> {
}