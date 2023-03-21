package com.catchtwobirds.soboro.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseList <T> extends ApiResponseHeader{
    List<T> data;
}
