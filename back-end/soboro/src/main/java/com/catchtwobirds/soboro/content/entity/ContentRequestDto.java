package com.catchtwobirds.soboro.content.entity;

import lombok.*;
import org.bson.types.ObjectId;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContentRequestDto {
    private Long contentNo;
    private Integer consultingNo;
    private String contentText;
    private Boolean contentSpeaker;

    @Builder
    public Content toEntity() {
        return Content.builder()
                .contentNo(contentNo)
                .consultingNo(consultingNo)
                .contentText(contentText)
                .contentSpeaker(contentSpeaker)
                .build();
    }
}
