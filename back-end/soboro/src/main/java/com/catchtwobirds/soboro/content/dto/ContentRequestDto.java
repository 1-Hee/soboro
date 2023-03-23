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
public class ContentRequestDto {
    private ObjectId contentNo;
    private Integer consultingNo;
    private String contentText;
    private Boolean contentSpeaker;

    @Builder
    public Content toEntity(Integer consultingNo) {
        return Content.builder()
                .contentNo(contentNo)
                .consultingNo(consultingNo)
                .contentText(contentText)
                .contentSpeaker(contentSpeaker)
                .build();
    }
}
