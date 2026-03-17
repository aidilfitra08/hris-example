package com.hrisexample.demo.excel;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import com.hrisexample.demo.dto.OvertimeRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OvertimeImportListener implements ReadListener<OvertimeRequest> {

    private final List<OvertimeRequest> rows = new ArrayList<>();

    @Override
    public void invoke(OvertimeRequest data, AnalysisContext context) {
        rows.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // all rows are collected in `rows`
    }
}
