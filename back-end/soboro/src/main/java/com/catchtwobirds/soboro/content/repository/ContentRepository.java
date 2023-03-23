package com.catchtwobirds.soboro.content.repository;


import com.catchtwobirds.soboro.content.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@EnableMongoRepositories(basePackages = "catchtwobirds.soboro")
public interface ContentRepository extends MongoRepository<Content, Long> {

    /**
     * 1. 유저 번호 -> 유저가 가지고 있는 상담인지 확인
     * 2. 상담 번호 -> 해당 상담이 맞는지
     */

//    List<Content> findByUser_UserNoAndConsultingNo(@Param("userNo") Integer userNo, @Param("consultingNo") Integer consultingNo);
    List<Content> findAllByConsultingNoAndUserNo(@Param("userNo") Integer userNo, @Param("consultingNo") Integer consultingNo);
    List<Content> findContentByConsultingNo(@Param("consultingNo") Integer consultingNo);

    List<Content> findAllByConsultingNo(@Param("consultingNo") Integer consultingNo);

//    List<Content> findByConsultingNo(@Param("consultingNo") Integer consultingNo);

    // mongo 쿼리문 작성
//    List<Content> findConsultingContent(@Param("userNo") int userNo, @Param("consultingNo") int consultingNo);
}
