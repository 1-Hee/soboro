package com.catchtwobirds.soboro.content.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Column;
import javax.persistence.Id;

//@Entity
@Document
@Getter @Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Content {

    @Id
    @Column(name = "content_no")
    private Long contentNo;

    // 외래키
    private Integer consultingNo;
    private String contentText;
    private Boolean contentSpeaker;
}
