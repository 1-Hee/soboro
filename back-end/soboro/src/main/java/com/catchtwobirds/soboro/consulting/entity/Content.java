package com.catchtwobirds.soboro.consulting.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//@Entity
@Document
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @Id
    @Column(name = "content_no")
    private Long id;

    // 외래키
    private Long consultingNo;
    private String contentText;
    private boolean contentSpeaker;
}
