package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {
    public static int kTOPMATCH = 3;

    public static void main(String[] args) {
        // write your code here
        List<Student> studentList = getStudentList();
        for (Student s : studentList) {
            List<Student> topMatch = getTopMatch(s, studentList);
            StringBuilder message = new StringBuilder();
            if (topMatch.size() == 0) {
                message.append("Sorry we don't have any student matches with your requirement.");
            }
            for (int i = 0; i < topMatch.size(); i++) {
                message.append("Matching result ").append(i + 1).append(" :");
                message.append(topMatch.get(i).getName());
                message.append(" (").append(topMatch.get(i).getEmail()).append(")\n");
                message.append("Here's the message that ");
                if (topMatch.get(i).getGender().equals("Male")) {
                    message.append("he ");
                } else {
                    message.append("she ");
                }
                message.append("leaves: ").append(topMatch.get(i).getMessageLeave());
                message.append("\n\n");
            }
            Email email = new Email(s.getEmail().trim(), "Match result", message.toString());
            email.send();
        }

    }

    public static List<Student> getStudentList() {
        Workbook wb = null;           //initialize Workbook null
        try {
            //reading data from a file in the form of bytes
            FileInputStream fis = new FileInputStream("D:\\2020Spring\\CS126\\"
                + "RoommateMatch\\File\\MatchFile.xlsx");
            //constructs an XSSFWorkbook object, by buffering the whole stream into the memory
            wb=new XSSFWorkbook(fis);
        } catch(IOException e) {
            e.printStackTrace();
        }
        assert wb != null;
        Sheet sheet = wb.getSheetAt(0);   //getting the XSSFSheet object at given index
        Row row = null; //returns the logical row
        Cell cell = null; //getting the cell representing the given column

        Map<String, Integer> colMapByName = new HashMap<>();
        if (sheet.getRow(0).cellIterator().hasNext()) {
            for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                colMapByName.put((sheet.getRow(0).getCell(j)).toString(), j);
            }
        }

        List<Student> studentList = new ArrayList<>();
        // iterator through each student
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            Student student = new Student();
            boolean isInfoValid = true;
            // iterate through each column
            for (Entry<String, Integer> colData : colMapByName.entrySet()) {
                //gives the index of column from  colMapByName Map by passing column name
                try {
                    cell = row.getCell(colMapByName.get(colData.getKey()));
                    switch (colData.getKey()) {
                        case "姓名（必填）":
                            if (cell.getStringCellValue().length() == 0) {
                                isInfoValid = false;
                            }
                            student.setName(cell.getStringCellValue());
                            break;
                        case "性别（必填）":
                            student.setGender(cell.getStringCellValue());
                            break;
                        case "您的学校邮箱（必填）":
                            student.setEmail(cell.getStringCellValue());
                            break;
                        case "所偏好的所有PCH宿舍（必填）":
                            student.setPchHouse(cell.getStringCellValue().split(","));
                            break;
                        case "所偏好的所有URH宿舍（必填）":
                            student.setUrhHouse(cell.getStringCellValue().split(","));
                            break;
                        case "是否有在寝室开party的需求（必填）":
                            student.setNeedParty(cell.getBooleanCellValue());
                            break;
                        case "是否有在寝室玩乐器的需求（必填）":
                            student.setNeedPlayMusic(cell.getBooleanCellValue());
                            break;
                        case "是否有使用机械键盘的习惯（必填）":
                            student.setNeedMechKeyboard(cell.getBooleanCellValue());
                            break;
                        case "平均睡觉时间（必填）":
                            student.setSleepTime(cell.getStringCellValue());
                            break;
                        case "平均起床时间（必填）":
                            student.setGetUpTime(cell.getStringCellValue());
                            break;
                        case "您的学院名称（必填）":
                            student.setCollegeName(cell.getStringCellValue());
                            break;
                        case "室友安静的重要程度（必填）":
                            student.setQuiet_importance(Integer.parseInt(cell.toString()));
                            break;
                        case "室友和您睡觉时间接近的重要程度（必填）":
                            student.setTime_importance(Integer.parseInt(cell.toString()));
                            break;
                        case "希望室友所在的学院（可多选）（必填）":
                            student.setDesireCollege(cell.getStringCellValue().split(","));
                            break;
                        case "我同意将我的邮箱自动发送给推荐的匹配室友（必填）":
                        case "我已知晓除姓名邮箱以外的所有信息都不会泄漏给任何个体或者组织（必填）":
                            if (!cell.getBooleanCellValue()) {
                                isInfoValid = false;
                            }
                            break;
                        case "是否有吸烟习惯":
                            student.setNeedSmoke(cell.getBooleanCellValue());
                            break;
                        case "是否有饮酒习惯（必填）":
                            student.setNeedDrink(cell.getBooleanCellValue());
                            break;
                        case "室友保持整洁干净的重要性（必填）":
                            student.setTidy_importance(Integer.parseInt(cell.toString()));
                            break;
                        case "是否接受室友吸烟（必填）":
                            if (cell.getStringCellValue().equals("No")) {
                                student.setAcceptSmoke(false);
                            } else {
                                student.setAcceptSmoke(true);
                            }
                            break;
                        case "是否接受室友饮酒（必填）":
                            if (cell.getStringCellValue().equals("No")) {
                                student.setAcceptSmoke(false);
                            } else {
                                student.setAcceptSmoke(true);
                            }
                            break;
                        case "希望留给推荐匹配室友的话（必填）":
                            student.setMessageLeave(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    isInfoValid = false;
                }
            }
            // if provided info is valid, add student to the list
            if (isInfoValid) {
                studentList.add(student);
            }
        }
        return studentList;
    }

    public static List<Student> getTopMatch(Student student, List<Student> waitList) {
        List<Pair<Student, Double>> resultList = new ArrayList<>();
        List<Student> returnResult = new ArrayList<>();
        if (student == null || waitList == null || waitList.size() <= 1) {
            return returnResult;
        }
        for (Student s : waitList) {
            // if s is student him/her self, or different gender, skip
            if (s.getName().equals(student.getName()) || !(s.getGender().equals(student.gender))) {
                continue;
            }
            if (!checkSmoke(s, student) || !checkDrink(s, student)
                || !checkCollege(s, student) || !checkHouse(s, student)) {
                continue;
            }
            double index = getHabitIndex(s, student);
            index += getHabitIndex(student, s);
            resultList.add(new Pair(s, index));
        }
        resultList.sort(Comparator.comparing(p -> -p.getValue()));
        if (resultList.size() >= kTOPMATCH) {
            for (int i = 0; i < kTOPMATCH; i++) {
                returnResult.add(resultList.get(i).getKey());
            }
        } else {
            for (Pair<Student, Double> pair : resultList) {
                returnResult.add(pair.getKey());
            }
        }
        return returnResult;
    }

    private static boolean checkSmoke(Student first, Student second) {
        if (first.isNeedSmoke() && !second.isAcceptSmoke()) {
            return false;
        }
        return first.isAcceptSmoke() || !second.isNeedSmoke();
    }

    private static boolean checkDrink(Student first, Student second) {
        if (first.isNeedDrink() && !second.isAcceptDrink()) {
            return false;
        }
        return first.isAcceptDrink() || !second.isNeedDrink();
    }

    private static boolean checkCollege(Student first, Student second) {
        boolean bool = false;
        String first_college = first.getCollegeName().trim();
        String second_college = second.getCollegeName().trim();
        // check if second student want first student's college
        for (String a : second.getDesireCollege()) {
            if (a.trim().equalsIgnoreCase(first_college)) {
                bool = true;
                break;
            }
        }
        // if not, fail to match
        if (!bool) {
            return false;
        }
        // else check if first student want second student's college
        for (String a : first.getDesireCollege()) {
            if (a.trim().equalsIgnoreCase(second_college)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkHouse(Student first, Student second) {
        List<String> houseList = new ArrayList<>();
        // add first student's house choice to list
        for (String s : first.getPchHouse()) {
            houseList.add(s.trim());
        }
        for (String s : first.getUrhHouse()) {
            houseList.add(s.trim());
        }
        // check if second student's choice overlap
        for (String s : second.getPchHouse()) {
            if (houseList.contains(s.trim())) {
                return true;
            }
        }
        for (String s : second.getUrhHouse()) {
            if (houseList.contains(s.trim())) {
                return true;
            }
        }
        return false;
    }

    private static double getHabitIndex(Student first, Student second) {
        double index = 0;
        int first_quiet_importance = first.getQuiet_importance();
        int first_tidy_importance = first.getTidy_importance();
        int first_time_importance = first.getTime_importance();
        // check quiet factor
        if (!second.isNeedMechKeyboard()) {
            index += first_quiet_importance;
        }
        if (!second.isNeedParty()) {
            index += first_quiet_importance;
        }
        if (!second.isNeedPlayMusic()) {
            index += first.quiet_importance;
        }
        // normalize the index
        index /= 3;
        // check tidy factor
        index += 5 - Math.abs(first_tidy_importance - second.getTidy_importance());
        // check time factor
        int first_sleep, first_getup, second_sleep, second_getup = 0;
        first_sleep = convertTimeToIndex(true, first.getSleepTime());
        second_sleep = convertTimeToIndex(true, second.getSleepTime());
        first_getup = convertTimeToIndex(false, first.getGetUpTime());
        second_getup = convertTimeToIndex(false, second.getGetUpTime());
        index += Math.sqrt(first_time_importance * (5 - Math.abs(first_sleep - second_sleep)));
        index += Math.sqrt(first_time_importance * (5 - Math.abs(first_getup - second_getup)));
        return index;
    }
    private static int convertTimeToIndex(boolean isSleep, String time) {
        // if it's sleep time
        if (isSleep) {
            switch (time.trim()) {
                case "Before 22:00":
                    return 1;
                case "22:00 - 22:59":
                    return 2;
                case "23:00 - 23:59":
                    return 3;
                case "00:00 - 00:59":
                    return 4;
                default:
                    return 5;
            }
        }
        // else it is get up time
        switch (time.trim()) {
            case "Before 06:00":
                return 1;
            case "06:00 - 06:59":
                return 2;
            case "07:00 - 07:59":
                return 3;
            case "08:00 - 08:59":
                return 4;
            default:
                return 5;
        }
    }
}
