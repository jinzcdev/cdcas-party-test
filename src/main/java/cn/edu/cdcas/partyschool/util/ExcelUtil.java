package cn.edu.cdcas.partyschool.util;

import cn.edu.cdcas.partyschool.model.User;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * the util class packaging the common methods to import and export excel.
 *
 * @author Char Jin(Jin zhichao)
 * @date 2018-07-12
 */

public class ExcelUtil {

    /**
     * transform the data of excel into map. And the list,key of which equals
     * -1,stores the header of column.
     *
     * @return the map transformed by excel.
     */
    public Map<Integer, List<String>> getDataMap(MultipartFile excelFile, int colNum) throws IOException {
        Map<Integer, List<String>> map = null;
        String suffix = excelFile.getOriginalFilename().substring(excelFile.getOriginalFilename().lastIndexOf("."));

        Workbook workbook;
        if (suffix.equals(".xls"))
            workbook = new HSSFWorkbook(excelFile.getInputStream());
        else
            workbook = new XSSFWorkbook(excelFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        map = new HashMap<>();
        int i = -1;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            List<String> list = new ArrayList<>();
            int j = 0;
            for (int cellNum = 0; cellNum < colNum; cellNum++) {
                Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellType(CellType.STRING);
                list.add(j++, cell.getStringCellValue());
                if (j == colNum) break;
            }
            map.put(i++, list);
        }
        workbook.close();


        return map;
    }

    /**
     * export students to output stream from database.
     *
     * @param users
     * @param out   the output stream.
     * @throws IOException
     */
    public void exportStudentScore(List<User> users, OutputStream out) throws IOException {
        Map<Integer, List<String>> map = this.getStudentScoreMap(users);
        List<String> header_list = map.get(-1);

        int rowCount = map.size() - 1, columnCount = header_list.size();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Info");
        XSSFRow header = sheet.createRow(0);
        sheet.setDefaultColumnWidth(14);

        for (int i = 0; i < columnCount; i++) {
            XSSFCell cell = header.createCell(i, CellType.STRING);
            cell.setCellStyle(this.createXSSFCellStyle(workbook, "??????", 12, BorderStyle.THIN, true, false));
            cell.setCellValue(header_list.get(i));
        }

        XSSFCellStyle contentCellStyle = this.createXSSFCellStyle(workbook, "??????", 12, BorderStyle.THIN, false, false);
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            XSSFRow row = sheet.createRow(rowNum + 1);
            List<String> row_list = map.get(rowNum);
            XSSFCellStyle numericCellStyle = this.createXSSFCellStyle(workbook, "??????", 12, BorderStyle.THIN, false, true);
            for (int colNum = 0; colNum < columnCount; colNum++) {
                if (colNum == 0 || colNum == 5 || colNum == 6 || colNum == 7) {
                    XSSFCell cell = row.createCell(colNum, CellType.NUMERIC);
                    cell.setCellStyle(numericCellStyle);
                    if (row_list.get(colNum).equals("null"))
                        cell.setCellValue(0d);
                    else
                        cell.setCellValue(Double.valueOf(row_list.get(colNum)));
                } else {
                    XSSFCell cell = row.createCell(colNum, CellType.STRING);
                    cell.setCellStyle(contentCellStyle);
                    cell.setCellValue(row_list.get(colNum));
                }
            }
        }

        workbook.write(out);
        out.close();
        workbook.close();
    }

    /**
     * transform the students into map.
     *
     * @param users students attending exam.
     * @return
     */
    public Map<Integer, List<String>> getStudentScoreMap(List<User> users) {
        Map<Integer, List<String>> map = new HashMap<>();
        String[] header_arr = new String[]{"??????", "??????", "??????", "??????", "??????", "??????", "????????????", "????????????"};

        int rowCount = 0;

        // set the key of column header to -1;
        map.put(-1, Arrays.asList(header_arr));

        // put data of users into map.
        for (User user : users) {
            List<String> row = new ArrayList<>();
            row.add(0, user.getIdx() + "");
            row.add(1, user.getGrade());
            row.add(2, user.getDepartment());
            row.add(3, user.getMajor());
            row.add(4, user.getName());
            row.add(5, user.getStudentNo());
            row.add(6, user.getExamScore() + "");
            row.add(7, user.getMakeUpScore() + "");
            map.put(rowCount++, row);
        }

        return map;
    }


    /**
     * set cell style.
     *
     * @param workbook
     * @param fontName
     * @param fontSize
     * @param fontBold
     * @param borderStyle
     * @return HSSFCellStyle
     */
    public XSSFCellStyle createXSSFCellStyle(XSSFWorkbook workbook, String fontName, int fontSize, BorderStyle borderStyle,
                                             boolean fontBold, boolean isNumeric) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(fontBold);
        style.setFont(font);

        if (isNumeric) {
            XSSFDataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat("#"));
        }

        style.setBorderBottom(borderStyle);
        style.setBorderTop(borderStyle);
        style.setBorderRight(borderStyle);
        style.setBorderLeft(borderStyle);

        return style;
    }
}
