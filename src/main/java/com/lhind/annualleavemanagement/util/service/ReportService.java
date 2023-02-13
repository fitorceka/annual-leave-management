package com.lhind.annualleavemanagement.util.service;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

public class ReportService {
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final UserEntity user;

    public ReportService(UserEntity user) {
        workbook = new XSSFWorkbook();
        this.user = user;
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Full Name", style);
        createCell(row, 1, "E-mail", style);
        createCell(row, 2, "Leave Reason", style);
        createCell(row, 3, "From Date", style);
        createCell(row, 4, "To Date", style);
        createCell(row, 5, "To Date", style);
        createCell(row, 6, "Total Days", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (LeaveEntity leave : user.getLeaves()) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, user.getFullName(), style);
            createCell(row, columnCount++, user.getEmail(), style);
            createCell(row, columnCount++, leave.getLeaveReason(), style);
            createCell(row, columnCount++, leave.getFromDate().toString(), style);
            createCell(row, columnCount++, leave.getToDate().toString(), style);
            createCell(row, columnCount++, leave.getStatus(), style);
            createCell(row, columnCount++, leave.getNoOfDays(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
