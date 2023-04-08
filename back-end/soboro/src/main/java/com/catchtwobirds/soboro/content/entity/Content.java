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
    private ObjectId id;

    // 상담 관련 외래키
    private Integer consultingNo;
    private String contentText;
    private Boolean contentSpeaker;
}
