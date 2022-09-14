package com.lhind.annualleavemanagement.util.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    
    public ReportService() {
        workbook = new XSSFWorkbook();
    }
    
    
}
