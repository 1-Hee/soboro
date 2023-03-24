package com.catchtwobirds.soboro.content.dto;

import com.catchtwobirds.soboro.content.entity.Content;
import lombok.*;
import org.bson.types.ObjectId;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContentResponseDto {
    private Integer consultingNo;
    private String contentText;
    private Boolean contentSpeaker;

    @Builder
    public ContentResponseDto(Content content) {
        this.consultingNo = content.getConsultingNo();
        this.contentText = content.getContentText();
        this.contentSpeaker = content.getContentSpeaker();
    }
}
