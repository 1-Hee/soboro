package com.catchtwobirds.soboro.content.repository;


import com.catchtwobirds.soboro.content.entity.Content;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@EnableMongoRepositories(basePackages = "catchtwobirds.soboro")
public interface ContentRepository extends MongoRepository<Content, ObjectId> {

    /**
     * 1. 유저 번호 -> 유저가 가지고 있는 상담인지 확인
     * 2. 상담 번호 -> 해당 상담이 맞는지
     */

//    List<Content> findByUser_UserNoAndConsultingNo(@Param("userNo") Integer userNo, @Param("consultingNo") Integer consultingNo);
//    List<Content> findAllByConsultingNoWithUserNo(@Param("userNo") Integer userNo, @Param("consultingNo") Integer consultingNo);
    Slice<Content> findAllByConsultingNo(@Param("consultingNo") Integer consultingNo, Pageable pageable);
}
