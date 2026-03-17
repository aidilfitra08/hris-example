package com.hrisexample.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OvertimeSearchRequest {

    private String keyword;
    private String status;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdDate";
    private String sortDir = "desc";
}
