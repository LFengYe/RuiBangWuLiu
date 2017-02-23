/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author LFeng
 */
public class ExcelImport {
    // 对外提供读取excel文件的接口
    public static List<List<Object>> readExcel(File myfile)throws IOException {
        String extension = (myfile.getName().lastIndexOf(".") == -1) ? "": myfile.getName().substring(myfile.getName().lastIndexOf(".") + 1);
        if ("xls".equals(extension)) { // 2003
//            System.err.println("读取excel2003文件内容");
            return read2003Excel(new FileInputStream(myfile));
        } else if ("xlsx".equals(extension)) { // 2007
//            System.err.println("读取excel2007文件内容");
            return read2007Excel(new FileInputStream(myfile));
        } else {
            throw new IOException("不支持的文件类型:" + extension);
        }
    }
    
    /**
    * 读取2003excel
    * @param file
    * @return
    */
    private static List<List<Object>> read2003Excel(InputStream inputStream)
        throws IOException {
        List<List<Object>> dataList = new ArrayList<>();
        HSSFWorkbook wb = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = null;
        HSSFCell cell = null;
        Object val = null;
        DecimalFormat df = new DecimalFormat("0");// 格式化数字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串

        for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<Object> objList = new ArrayList<>();
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum();j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    val = null;
                    objList.add(val);
                    continue;
                }
                switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_STRING:
                    val = cell.getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                        val = df.format(cell.getNumericCellValue());
                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                        val = df.format(cell.getNumericCellValue());
                    } else {
                        val = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    val = cell.getBooleanCellValue();
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    val = "";
                    break;
                default:
                    val = cell.toString();
                    break;
                }
                objList.add(val);
            }
            dataList.add(objList);
        }
        return dataList;
    }

   /**
    * 读取excel表头
    * @param file
    * @return
    * @throws IOException
    */
    public static String[] readExcelHead(File file) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = null;
        HSSFCell cell = null;
        row = sheet.getRow(0);
        String[] buff = new String[row.getLastCellNum()];
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            cell = row.getCell(i);
            buff[i] = cell.getStringCellValue();
        }
        return buff;
    }

    /**
    * 读取2007excel
    * 
    * @param file
    * @return
    */
    private static List<List<Object>> read2007Excel(InputStream inputStream)
        throws IOException {
        List<List<Object>> dataList = new ArrayList<>();
        XSSFWorkbook xwb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = xwb.getSheetAt(0);
        XSSFRow row = null;
        XSSFCell cell = null;
        Object val = null;
        DecimalFormat df = new DecimalFormat("0");// 格式化数字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串

        for (int i = sheet.getFirstRowNum();i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<Object> objList = new ArrayList<>();
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum();j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    val = null;
                    objList.add(val);
                    continue;
                }
                switch (cell.getCellType()) {
                case XSSFCell.CELL_TYPE_STRING:
                    val = cell.getStringCellValue();
                    break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                        val = df.format(cell.getNumericCellValue());
                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                        val = df.format(cell.getNumericCellValue());
                    } else {
                        val = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    val = cell.getBooleanCellValue();
                    break;
                case XSSFCell.CELL_TYPE_BLANK:
                    val = "";
                    break;
                default:
                    val = cell.toString();
                    break;
                }
                objList.add(val);
            }
            dataList.add(objList);
        }
        return dataList;
    }
}
