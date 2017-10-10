/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author LFeng
 */
public class Units {

    private static SerializeConfig mapping = new SerializeConfig();
    private static String dateFormat;

    static {
        dateFormat = "yyyy-MM-dd HH:mm:ss";
        mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    }

    /**
     * 验证码序列
     */
    public static final char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final double EARTH_RADIUS = 6378137;//赤道半径(单位m)
    public static final String BAIDU_CONVERT_KEY = "UGTSrlHZTd3O95SiMiQkhLO2";
    public static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullNumberAsZero,
        SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullListAsEmpty,
        SerializerFeature.WriteDateUseDateFormat};

    /**
     * 将指定类型坐标转换成为百度坐标
     *
     * @param lnglat 待转换的经纬度列表, 字符串格式为: longitude,latitude
     * @param type 待转换的坐标类型, 类型的取值如下: 1：GPS设备获取的角度坐标，wgs84坐标;
     * 2：GPS获取的米制坐标、sogou地图所用坐标;
     * 3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局坐标; 4：3中列表地图坐标对应的米制坐标;
     * 5：百度地图采用的经纬度坐标; 6：百度地图采用的米制坐标; 7：mapbar地图坐标; 8：51地图坐标
     * @return 返回坐标为: bd09ll(百度经纬度坐标)
     */
    public static String getBaiduLnglatConvert(String[] lnglat, int type) {
        String httpUrl = "http://api.map.baidu.com/geoconv/v1/";
        String httpArg = "";
        String coords = "coords=";
        if (lnglat.length < 100) {
            for (String lnglat1 : lnglat) {
                coords += (lnglat1 + ";");
            }
            coords = coords.substring(0, coords.length() - 1);
            httpArg += coords;
            httpArg += "&form=" + type + "&to=5&ak=" + BAIDU_CONVERT_KEY + "&output=json";
            return requestWithNoHeaderKey(httpUrl, httpArg);
        } else {
            return "经纬度点大于1000个";
        }
    }

    /**
     * 根据身份证号获取身份信息
     *
     * @param idCardNO
     * @return JSON返回示例 : { "errNum": 0, "retMsg": "success", "retData": {
     * "sex": "M", //M-男，F-女，N-未知 "birthday": "1987-04-20", "address":
     * "湖北省孝感市汉川市" } }
     */
    public static String getIDCardInfo(String idCardNO) {
        /*身份证信息验证URL*/
        String httpUrl = "http://apis.baidu.com/apistore/idservice/id";
        String httpArg = "id=" + idCardNO;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * 根据城市名称获取天气信息
     *
     * @param cityName
     * @return * JSON返回示例 :{ errNum: 0, errMsg: "success", retData: { city:
     * "北京", //城市 pinyin: "beijing", //城市拼音 citycode: "101010100", //城市编码 date:
     * "15-02-11", //日期 time: "11:00", //发布时间 postCode: "100000", //邮编
     * longitude: 116.391, //经度 latitude: 39.904, //维度 altitude: "33", //海拔
     * weather: "晴", //天气情况 temp: "10", //气温 l_tmp: "-4", //最低气温 h_tmp: "10",
     * //最高气温 WD: "无持续风向",	//风向 WS: "微风(<10m/h)", //风力 sunrise: "07:12", //日出时间
     * sunset: "17:44" //日落时间 } }
     */
    public static String getWeatherInfo(String cityName) {

        /*天气信息获取*/
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
        String httpArg = "cityname=" + cityName;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * 根据汉语拼音获取天气信息
     *
     * @param cityPinyin
     * @return JSON返回示例 :{ errNum: 0, errMsg: "success", retData: { city: "北京",
     * //城市 pinyin: "beijing", //城市拼音 citycode: "101010100", //城市编码 date:
     * "15-02-11", //日期 time: "11:00", //发布时间 postCode: "100000", //邮编
     * longitude: 116.391, //经度 latitude: 39.904, //维度 altitude: "33", //海拔
     * weather: "晴", //天气情况 temp: "10", //气温 l_tmp: "-4", //最低气温 h_tmp: "10",
     * //最高气温 WD: "无持续风向",	//风向 WS: "微风(<10m/h)", //风力 sunrise: "07:12", //日出时间
     * sunset: "17:44" //日落时间 } }
     */
    public static String getWeacherInfoWithPinyin(String cityPinyin) {
        /*天气信息获取*/
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/weather";
        String httpArg = "cityname=" + cityPinyin;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * 返回城市信息
     *
     * @param cityName
     * @return JSON返回示例 : { errNum: 0, retMsg: "success", retData: { cityName:
     * "北京", provinceName: "北京", cityCode: "101010100", //天气预报城市代码 zipCode:
     * "100000", //邮编 telAreaCode: "010" //电话区号 } }
     */
    public static String getCityInfo(String cityName) {
        /*城市信息获取URL*/
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityinfo";
        String httpArg = "cityname=" + cityName;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "d39fd8a034a21aa3b7d45ebf97c8ffd9");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 发送请求头不包含apikey的http请求
     *
     * @param httpUrl
     * @param httpArg
     * @return
     */
    public static String requestWithNoHeaderKey(String httpUrl, String httpArg) {
        BufferedReader reader;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;
        //System.out.println("httpUrl:" + httpUrl);
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            //connection.setRequestProperty("apikey", "d39fd8a034a21aa3b7d45ebf97c8ffd9");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 发送post请求
     *
     * @param httpUrl
     * @param sendBody
     * @return
     */
    public static String requestWithPost(String httpUrl, String sendBody) {
        BufferedReader reader;
        String result;
        StringBuilder sbf = new StringBuilder();
        try {

            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            if (null != sendBody) {
                System.out.println(sendBody);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                out.write(sendBody);
                out.flush();
                out.close();
            }

            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                result = sbf.toString();
                return result;
            } else {
                System.out.println(connection.getResponseCode() + ",message:" + connection.getResponseMessage());
            }
        } catch (IOException e) {
            Logger.getLogger(Units.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    /**
     * 将列表转换成为json格式数据
     *
     * @param result
     * @param recordCount
     * @return
     */
    public static String listToJson(ArrayList<?> result, int recordCount) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (null != result && result.size() > 0) {
            json.append("\"").append("status").append("\"").append(":");
            json.append(0).append(",");
            json.append("\"").append("message").append("\"").append(":");
            json.append("\"").append("\"").append(",");
            json.append("\"").append("count").append("\"").append(":");
            json.append("\"").append(recordCount).append("\"").append(",");
            json.append("\"").append("data").append("\"").append(":");
            json.append(JSONObject.toJSONString(result, features));
        } else {
            json.append("\"").append("status").append("\"").append(":");
            json.append(-1).append(",");
            json.append("\"").append("message").append("\"").append(":");
            json.append("\"").append("the record is null").append("\"").append(",");
            json.append("\"").append("count").append("\"").append(":");
            json.append("\"").append(0).append("\"").append(",");
            json.append("\"").append("data").append("\"").append(":");
            json.append("\"").append("\"");
        }
        json.append("}");
        return json.toString();
    }

    /**
     * 将对象转成json格式数据
     *
     * @param status
     * @param message
     * @param object
     * @return
     */
    public static String objectToJson(int status, String message, Object object) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"").append("status").append("\"").append(":");
        json.append(status).append(",");
        json.append("\"").append("message").append("\"").append(":");
        json.append("\"").append(message).append("\"").append(",");
        json.append("\"").append("data").append("\"").append(":");
        //json.append(JSONObject.toJSONString(object, features));
        json.append(JSON.toJSONStringWithDateFormat(object, "YYYY-MM-dd HH:mm:ss", features));
        json.append("}");
        return json.toString();
    }

    /**
     * json字符串转换成json格式数据
     *
     * @param status
     * @param message
     * @param jsonStr
     * @return
     */
    public static String jsonStrToJson(int status, String message, String jsonStr) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"").append("status").append("\"").append(":");
        json.append(status).append(",");
        json.append("\"").append("message").append("\"").append(":");
        json.append("\"").append(message).append("\"").append(",");
        json.append("\"").append("data").append("\"").append(":");
        json.append(jsonStr);
        json.append("}");
        return json.toString();
    }

    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     *
     * @param lon1 第一点的经度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的经度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     */
    public static double GetDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 转化为弧度(rad)
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 判断给定的日期是不是今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        Date nowDate = new Date();
        return (date.getTime() >= dayBegin(nowDate).getTime())
                && (date.getTime() <= dayEnd(nowDate).getTime());
    }

    /**
     * 获取当前系统时间字符串
     *
     * @return 返回yyyy-MM-dd HH:mm:ss格式时间字符串
     */
    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        return format.format(nowDate);
    }

    /**
     * 获取当前系统时间没有分隔符的字符串
     *
     * @return 返回yyyyMMddHHmmss格式时间字符串
     */
    public static String getNowTimeNoSeparator() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date nowDate = new Date();
        return format.format(nowDate);
    }

    /**
     * 获取系统当前日期字符串
     *
     * @return
     */
    public static String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date();
        return format.format(nowDate);
    }

    /**
     * 返回指定时间与当前时间之间的时间间隔
     *
     * @param date
     * @return 若指定时间早于当前时间则返回正值; 若指定时间晚于当前时间则返回负值.
     */
    public static long getIntervalTimeWithNow(Date date) {
        Date nowDate = new Date();
        return nowDate.getTime() - date.getTime();
    }

    /**
     * 根据状态码及消息内容返回json个数数据
     *
     * @param statusCode
     * @param message
     * @return json格式数据
     */
    public static String createJsonWithResult(String statusCode, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"").append("status").append("\"").append(":");
        builder.append("\"").append("success").append("\"").append(",");
        builder.append("\"").append("message").append("\"").append(":");
        builder.append("\"").append(message).append("\"").append(",");
        builder.append("\"").append("result").append("\"").append(":");
        builder.append("\"").append(statusCode).append("\"");
        builder.append("}");

        return builder.toString();
    }

    /**
     * 指定字符串是否在指定的字符串数组中
     *
     * @param str
     * @param array
     * @return
     */
    public static boolean isStrInArray(String str, String[] array) {
        for (String string : array) {
            if (string.compareTo(str) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个字符串是否为空, 长度为0或者是null都为空
     *
     * @param string
     * @return
     */
    public static boolean strIsEmpty(String string) {
        if (string == null) {
            return true;
        }
        if (string.trim().length() <= 0) {
            return true;
        }
        if (string.compareToIgnoreCase("null") == 0) {
            return true;
        }
        return false;
    }

    public static String getSubJsonStr(String jsonStr, String keyName) {
        JSONObject obj = new JSONObject();
        obj.put(keyName, JSONObject.parseObject(jsonStr).getString(keyName));
        return obj.toJSONString();
    }

    public static String getSubJsonValue(String jsonStr, String keyName) {
        return JSONObject.parseObject(jsonStr).getString(keyName);
    }

    /**
     * 指定文件的内容作为字符串返回
     *
     * @param path
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static String returnFileContext(String path, String fileName) throws FileNotFoundException {
        File file = new File(path + fileName);
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file, "utf-8");
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        return builder.toString();
    }

    /**
     * 根据给定的路径和文件名生产文件
     *
     * @param filePath
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createNewFile(String filePath, String fileName) throws IOException {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 在srcStr字符串中查找desStr字符串, 然后在desStr后面插入insertStr字符串
     *
     * @param srcStr
     * @param desStr
     * @param insertStr
     * @return
     */
    public static String insertStr(String srcStr, String desStr, String insertStr) {
        StringBuffer buffer = new StringBuffer(srcStr);
        int index = 0;
        while ((index = buffer.indexOf(desStr, index)) != -1) {
            index += desStr.length() - 1;
            buffer.insert(index, insertStr);
        }
        return buffer.toString();
    }

    /**
     * 获取Excel单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String result = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                result = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                result = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            default:
                result = cell.getStringCellValue();
                break;
        }
        return result.trim();
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    public static String getStringCellValue(Cell cell) {
        String strCell = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                strCell = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell.trim();
    }

    /**
     * 获取单元格数据内容为日期类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    public static String getDateCellValue(Cell cell) {
        String result = "";
        try {
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_NUMERIC) {
                Date date = cell.getDateCellValue();
                result = (date.getYear() + 1900) + "-" + (date.getMonth() + 1)
                        + "-" + date.getDate();
            } else if (cellType == Cell.CELL_TYPE_STRING) {
                String date = getStringCellValue(cell);
                result = date.replaceAll("[年月]", "-").replace("日", "").trim();
            } else if (cellType == Cell.CELL_TYPE_BLANK) {
                result = "";
            }
        } catch (Exception e) {
            System.out.println("日期格式不正确!");
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 根据Cell类型设置数据
     *
     * @param cell
     * @return
     */
    public static String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case Cell.CELL_TYPE_NUMERIC:
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式

                        //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                        //cellvalue = cell.getDateCellValue().toLocaleString();
                        //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        cellvalue = sdf.format(date);
                    } // 如果是纯数字
                    else {
                        // 取得当前Cell的数值
                        cellvalue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case Cell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
        return s;
    }

    public static boolean isEmptyRowForExcel(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查询给定结果集中是否包含指定列名
     *
     * @param res
     * @param columnName
     * @return
     */
    public static boolean isExistColumn(ResultSet res, String columnName) {
        try {
            if (res.findColumn(columnName) > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * ************************************容联云通讯短信发送平台*****************************************
     */
    /**
     * 生成指定位数验证码(包含字母和数字)
     *
     * @param codeCount
     * @return
     */
    /*
     public static String createPhoneValidateCode(int codeCount) {
     // randomCode记录随机产生的验证码
     StringBuilder randomCode = new StringBuilder();
     Random random = new Random();
     // 随机产生codeCount个字符的验证码
     for (int i = 0; i < codeCount; i++) {

     String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
     // 将产生的codeCount个随机数组合在一起
     randomCode.append(strRand);
     }
     return randomCode.toString();
     }
     */
    /**
     * 向指定的手机号发送验证码
     *
     * @param to 要发送的手机号，多个手机号用英文状态逗号隔开，一次最多支持100个手机号
     * @param verificationCode 要发送的验证码
     * @return
     */
    /*
     public static String sendSMSVerificationCode(String to, String verificationCode) {
     //初始化SDK
     CCPRestSmsSDK restAPI = new CCPRestSmsSDK();

     //*初始化服务器地址和端口
     restAPI.init(Constants.SMS_PLATFORM_URL_DISTRIBUTION, Constants.SMS_PLATFORM_PORT);

     //*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN
     restAPI.setAccount(Constants.SMS_PLATFORM_ACCOUNT_SID, Constants.SMS_PLATFORM_AUTH_TOKEN);

     //*初始化应用ID
     restAPI.setAppId(Constants.SMS_PLATFORM_APP_ID);

     //*调用发送模板短信的接口发送短信
     HashMap<String, Object> result = restAPI.sendTemplateSMS(to, Constants.SMS_PLATFORM_TEMPLATE_ID, new String[]{verificationCode, Constants.SMS_EXPIRED_MINUTE});

     String json;
     if ("000000".equals(result.get("statusCode"))) {
     json = createJsonWithResult(String.valueOf(result.get("statusCode")), "验证码已发送到您的手机，请于5分钟内输入手机验证码，以免失效！");
     } else {
     json = createJsonWithResult(String.valueOf(result.get("statusCode")), String.valueOf(result.get("statusMsg")));
     }
     return json;
     }
     */
    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public final static String getRealIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址  

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }
}
