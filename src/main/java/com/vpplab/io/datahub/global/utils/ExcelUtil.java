package com.vpplab.io.datahub.global.utils;

import com.vpplab.io.datahub.domain.bid.ExcelColumnEnum;
import com.vpplab.io.datahub.global.exception.CustomException;
import com.vpplab.io.datahub.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtil {

    public static final String SPREADSHEET_EXTENSION = ".xlsx";

    public static Workbook makeForecastExcelWorkbook(List<Map<String,Object>> bidData){
        try {
            // make excel binary
            Workbook wb = new XSSFWorkbook();
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);

            // create sheet
            Sheet sheet = wb.createSheet("Sheet1");
            sheet.setColumnWidth(0,20 * 256);
            sheet.setColumnWidth(1,20 * 256);
            sheet.setColumnWidth(2,20 * 256);
            sheet.setColumnWidth(3,10 * 256);
            sheet.setColumnWidth(4,20 * 256);
            sheet.setColumnWidth(5,10 * 256);
            sheet.setColumnWidth(6,15 * 256);
            sheet.setColumnWidth(7,15 * 256);

            Row row = null;
            Cell cell = null;
            int rowNum = 0;

            // Header
            row = sheet.createRow(rowNum++);

            // generator info
            for(ExcelColumnEnum v : ExcelColumnEnum.values()){
                int column = v.getOrder();
                cell = row.createCell(column);
                cell.setCellValue(v.toString());
                cell.setCellStyle(cellStyle);
            }

            // time column
            for(int i = 1; i < 25 ; i++){
                int column = i+7;
                cell = row.createCell(column);
                cell.setCellValue(String.valueOf(i));
                cell.setCellStyle(cellStyle);
            }

            // Body
            for(Map<String,Object> map : bidData){
                row = sheet.createRow(rowNum++);

                // generator info
                for(ExcelColumnEnum v : ExcelColumnEnum.values()){
                    int column = v.getOrder();
                    cell = row.createCell(column);
                    cell.setCellValue(String.valueOf(map.get(v.toString())));
                    cell.setCellStyle(cellStyle);
                }

                // time column
                for(int i = 1; i < 25 ; i++){
                    int column = i+7;
                    String onTimeKey = Integer.toString(i);
                    cell = row.createCell(column);
                    cell.setCellValue(String.valueOf(map.get(onTimeKey)));
                    cell.setCellStyle(cellStyle);
                }
            }
            return wb;
        } catch (Exception e) {
            log.debug("[EXCEPTION] : {}", e.getMessage());
            log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(ErrorCode.CREATE_EXCEL_FILE_FAILURE);
        }
    }

}
