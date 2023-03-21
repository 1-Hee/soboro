package com.catchtwobirds.soboro.test;

import lombok.Data;

@Data
public class TestDto {
    private String id;
    private String name;
    private String pw;

    public TestDto(String id, String name, String pw) {
        this.id = id;
        this.name = name;
        this.name = pw;
    }
}
