package com.catchtwobirds.soboro.consulting.repository;

import com.catchtwobirds.soboro.consulting.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
