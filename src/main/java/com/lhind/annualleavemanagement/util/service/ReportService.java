package com.lhind.annualleavemanagement.util.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    public ReportService() {
        workbook = new XSSFWorkbook();
        sheet = null;
    }

}
