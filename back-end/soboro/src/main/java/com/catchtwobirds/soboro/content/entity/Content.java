package com.catchtwobirds.soboro.content.entity;

import lombok.*;
//import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.persistence.Column;

//@Entity
@Document
@Getter @Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Content {

    @MongoId
    @Field(name = "content_no")
    private Long contentNo;
//    private ObjectId contentNo;

    // 상담 관련 외래키
    private Integer consultingNo;
    // 유저관련 외래키
    private Integer userNo;
    private String contentText;
    private Boolean contentSpeaker;
}
