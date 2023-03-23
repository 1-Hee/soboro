package com.catchtwobirds.soboro.content.entity;

import lombok.*;
import org.bson.types.ObjectId;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContentResposneDto {

    private Long contentNo;
    private Integer consultingNo;
    private String contentText;
    private Boolean contentSpeaker;

    @Builder
    public ContentResposneDto(Content content) {
        this.contentNo = content.getContentNo();
        this.consultingNo = content.getConsultingNo();
        this.contentText = content.getContentText();
        this.contentSpeaker = content.getContentSpeaker();
    }
}
