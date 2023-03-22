package com.catchtwobirds.soboro.content.repository;


import com.catchtwobirds.soboro.content.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepostiory extends MongoRepository<Content, String> {

    /**
     * 1. 유저 번호 -> 유저가 가지고 있는 상담인지 확인
     * 2. 상담 번호 -> 해당 상담이 맞는지
     */

    // mongo 쿼리문 작성
    List<Content> findConsultingContent(@Param("userNo") int userNo, @Param("consultingNo") int consultingNo);
}
