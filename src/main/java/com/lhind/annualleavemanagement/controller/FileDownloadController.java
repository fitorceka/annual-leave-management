package com.lhind.annualleavemanagement.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.lhind.annualleavemanagement.security.CurrentAuthenticatedUser;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.util.service.ReportService;

@Controller
public class FileDownloadController {

    @GetMapping(value = "/export/excel")
    public String downloadReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=leaves_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        ReportService excelExporter = new ReportService(user);

        excelExporter.export(response);

        return "manage-my-leaves";
    }
}
