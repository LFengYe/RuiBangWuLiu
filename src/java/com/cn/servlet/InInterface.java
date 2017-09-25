/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.ClassDescription;
import com.cn.bean.Customer;
import com.cn.bean.Employee;
import com.cn.bean.FieldDescription;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.in.DJInWareHouse;
import com.cn.bean.in.DJInWareHouseList;
import com.cn.bean.in.SJBackWareHouse;
import com.cn.bean.in.SJBackWareHouseList;
import com.cn.bean.in.SJOutWareHouse;
import com.cn.bean.in.SJOutWareHouseList;
import com.cn.bean.in.ZDBackWareHouse;
import com.cn.bean.in.ZDBackWareHouseList;
import com.cn.bean.pro.JPQJCForZDTK;
import com.cn.bean.pro.KFJCDJPForSJCK;
import com.cn.bean.pro.KFJCSJPForSJTK;
import com.cn.bean.pro.XPJCForZDTK;
import com.cn.controller.CommonController;
import com.cn.controller.CommonOperate;
import com.cn.controller.InWareHouseController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author LFeng
 */
public class InInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DataInterface.class);

//    private CommonController commonController;
//    private DatabaseOpt opt;
    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        super.init();
//        commonController = new CommonController();
//        opt = new DatabaseOpt();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param params
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String params)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String subUri = uri.substring(uri.lastIndexOf("/") + 1,
                uri.lastIndexOf("."));
        String json = null;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        //logger.info(Units.getIpAddress(request) + "accept:" + subUri + ",time:" + (new Date().getTime()));

        try {
            logger.info(subUri + ",params:" + params);
            JSONObject paramsJson = JSONObject.parseObject(params);
            //logger.info("send:" + subUri + ",time:" + paramsJson.getString("timestamp"));
            String module = paramsJson.getString("module");
            String operation = paramsJson.getString("operation");
            String rely = (paramsJson.getString("rely") == null) ? ("{}") : (paramsJson.getString("rely"));
            String target = paramsJson.getString("target");
            String datas = (paramsJson.getString("datas") == null) ? ("") : paramsJson.getString("datas");
            String update = paramsJson.getString("update");
            String add = paramsJson.getString("add");
            String delete = paramsJson.getString("del");
            String item = paramsJson.getString("item");
            String details = paramsJson.getString("details");
            String detail = paramsJson.getString("detail");
            String fileName = paramsJson.getString("fileName");
            String operateType = (paramsJson.getString("type") == null) ? ("") : paramsJson.getString("type");
            String start = paramsJson.getString("start");
            String end = paramsJson.getString("end");
            int isHistory = paramsJson.getIntValue("isHistory");
            int pageIndex = paramsJson.getIntValue("pageIndex");
            int pageSize = paramsJson.getIntValue("pageSize");

            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";

            /*验证是否登陆*/
            if (!"userLogin".equals(module)
                    && (session.getAttribute("user") == null || session.getAttribute("employee") == null)) {
                session.invalidate();
                json = Units.objectToJson(-99, "未登陆", null);
                PrintWriter out = response.getWriter();
                try {
                    response.setContentType("text/html;charset=UTF-8");
                    response.setHeader("Cache-Control", "no-store");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                    out.print(json);
                } finally {
                    out.close();
                }
                return;
            }
            Employee employee = (Employee) session.getAttribute("employee");

            switch (module) {
                /**
                 * ***************************************部品入库管理**************************************
                 */
                //<editor-fold desc="部品入库管理">
                //<editor-fold desc="待检入库">
                case "待检入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", datas, rely, "", "DJInWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "DJInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_detail": {
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("djInWareHouseID");
                            String mainTabWhereSql = "DJInWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "DJInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                DJInWareHouse djInWareHouse = (DJInWareHouse) list.get(0);

                                Class objClass = Class.forName("com.cn.bean.in." + "DJInWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "DJInWareHouseList", "*", whereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        DJInWareHouseList dj = (DJInWareHouseList) obj;
                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(dj.getSupplierID() + "_" + dj.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        dj.setInboundPackageAmount(containerInfo.getInboundPackageAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(djInWareHouse.getDjRKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
                                    //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partName", "partID", "autoStylingName", "partUnit", "inboundContainerName", "inboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品名称", "部品编号", "车型", "部品单位", "入库盛具", "入库包装数量"};
                                int[] keysWidth = {20, 20, 20, 10, 10, 10, 10};
                                String[] fieldsName = {"partCode", "partName", "partID", "autoStylingName", "partUnit", "inboundContainerName", "inboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else if (result == 2627) {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "djInWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "djInWareHouseID") + "]", "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect()).get(0);
                                        json = Units.objectToJson(-1, "明细中件号有重复!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "djInWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "djInWareHouseID") + "]", "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect()).get(0);
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else if (result == 2627) {
                                    json = Units.objectToJson(-1, "数据以保存, 请勿重复提交!", null);
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "DJInWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.in.", "DJInWareHouse", "DJInWareHouseID", "djRKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.in.", "DJInWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.in.", "DJInWareHouseList", importPath + fileName);
                            //ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "DJInWareHouseList", importPath + fileName);

                            DJInWareHouse dJInWareHouse = JSONObject.parseObject(item, DJInWareHouse.class);
                            if (importData != null) {
                                boolean importSuccess = true;
                                Iterator iterator = importData.iterator();
                                while (iterator.hasNext()) {
                                    DJInWareHouseList houseList = (DJInWareHouseList) iterator.next();
                                    houseList.setSupplierID(dJInWareHouse.getSupplierID());
                                    houseList.setDjInWareHouseID(dJInWareHouse.getDjInWareHouseID());
                                    houseList.setInboundBatch(dJInWareHouse.getInboundBatch());
                                    houseList.setPartState("待检品");

                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + houseList.getPartCode().toLowerCase().toLowerCase()), PartBaseInfo.class);
                                    //System.out.println("partBaseInfo_" + houseList.getPartCode() + "baseInfo:" + JSONObject.toJSONString(baseInfo));
                                    if (baseInfo != null) {
                                        houseList.setPartID(baseInfo.getPartID());
                                        houseList.setPartName(baseInfo.getPartName());
                                        houseList.setPartUnit(baseInfo.getPartUnit());
                                        houseList.setAutoStylingName(baseInfo.getAutoStylingName());
                                    } else {
                                        houseList.setFailedReason("缺失件号信息!");
                                        importSuccess = false;
                                        continue;
                                    }

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + houseList.getSupplierID()), Customer.class);
                                    if (customer != null) {
                                        houseList.setSupplierName(customer.getCustomerAbbName());
                                    } else {
                                        houseList.setFailedReason("缺失供应商信息!");
                                        importSuccess = false;
                                        continue;
                                    }

                                    GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(houseList.getSupplierID() + "_" + houseList.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                    if (containerInfo != null) {
                                        int packageAmount = containerInfo.getInboundPackageAmount();
                                        if (packageAmount > 0) {
                                            int boxAmount = (houseList.getInboundAmount() % packageAmount == 0) ? (houseList.getInboundAmount() / packageAmount) : (houseList.getInboundAmount() / packageAmount + 1);
                                            houseList.setInboundBoxAmount(boxAmount);
                                        } else {
                                            houseList.setFailedReason("缺失入库盛具信息!");
                                            importSuccess = false;
                                        }
                                    } else {
                                        houseList.setFailedReason("缺失入库盛具信息!");
                                        importSuccess = false;
                                    }
                                }

                                if (importSuccess) {
                                    int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        System.out.println("import:" + JSONObject.toJSONString(importData));
                                        result = commonController.dataBaseOperate(JSONObject.toJSONString(importData, Units.features), "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect()).get(0);
                                        if (result == 0) {
                                            JSONObject object = new JSONObject();
                                            object.put("datas", importData);
                                            json = Units.objectToJson(0, "数据添加成功!", JSONObject.toJSONString(object, Units.features));
                                        } else if (result == 2627) {
                                            json = Units.objectToJson(-1, "明细中件号有重复!", null);
                                        } else {
                                            if (!Units.strIsEmpty(Units.getSubJsonStr(item, "djInWareHouseID")))
                                                commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "djInWareHouseID") + "]", "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect());
                                            json = Units.objectToJson(-1, "明细添加失败!", null);
                                        }
                                    } else if (result == 2627) {
                                        json = Units.objectToJson(-1, "数据以保存, 请勿重复提交!", null);
                                    } else {
                                        json = Units.objectToJson(-1, "数据添加失败!", null);
                                    }
                                } else {
                                    JSONObject object = new JSONObject();
                                    object.put("datas", importData);
                                    //json = Units.objectToJson(-1, "数据添加失败!", JSONObject.toJSONString(importData));
                                    json = Units.objectToJson(-1, "数据添加失败!", object.toJSONString());
                                }

                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="待检审核">
                case "待检审核": {
                    String whereCase = "DJRKAuditTime is null";
                    switch (operation) {
                        case "create": {
                            if (isHistory == 0) {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", whereCase, "DJInWareHouseID", opt.getConnect());
                                json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", datas, rely, whereCase, "DJInWareHouseID", opt.getConnect());
                            } else {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", "", "DJInWareHouseID", opt.getConnect());
                                json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", datas, rely, "", "DJInWareHouseID", opt.getConnect());
                            }
                            break;
                        }
                        case "request_detail": {
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("djInWareHouseID");
                            String mainTabWhereSql = "DJInWareHouseID = '" + djInWareHouseID + "'";
                            Class objClass = Class.forName("com.cn.bean.in." + "DJInWareHouseList");
                            Method method = objClass.getMethod("getRecordCount", new Class[0]);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "DJInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                DJInWareHouse djInWareHouse = (DJInWareHouse) list.get(0);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "DJInWareHouseList", "*", whereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        DJInWareHouseList dj = (DJInWareHouseList) obj;
                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(dj.getSupplierID() + "_" + dj.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        dj.setInboundPackageAmount(containerInfo.getInboundPackageAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(djInWareHouse.getDjRKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
                                    //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            /*if (isHistory == 0) {
                                json = queryOperateWithFilter("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOperateWithFilter("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            }*/
                            if (isHistory == 0) {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "audit": {
                            DJInWareHouse dJInWareHouse = JSONObject.parseObject(datas, DJInWareHouse.class);
                            JSONObject proParams = new JSONObject();
                            //obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            //obj.put("djRKAuditTime", Units.getNowTime());
                            proParams.put("DJInWareHouseID", "string," + dJInWareHouse.getDjInWareHouseID());
                            proParams.put("DJRKAuditStaffName", "string," + session.getAttribute("user"));
                            //String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            //ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "DJInWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            JSONArray jsonParams = new JSONArray();
                            jsonParams.add(proParams);
                            ArrayList<Integer> updateResult = commonController.proceduceForUpdate("tbDJInWareHouseAudit", jsonParams, opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检出库">
                case "送检出库": {
                    switch (operation) {
                        case "create": {
                            String whereCase = "exists (select * from tblSJOutWareHouseList list left join viewGYSPartContainerInfo gys"
                                    + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                    + " where list.SJOutWareHouseID = viewSJOutWareHouse.SJOutWareHouseID"
                                    + " and list.WareHouseManagername is null"
                                    + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                            whereCase = (operateType.compareTo("app") == 0) ? (whereCase) : ("");
                            //json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", datas, rely, whereCase, "SJOutWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"送检出库单号\\", ",@SJCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "SJOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"送检出库单号\\", ",@SJCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("sjCKAuditStaffName", session.getAttribute("user"));
                                obj.put("sjCKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "SJOutWareHouse", "update", opt.getConnect());
                            }
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("sjOutWareHouseID");
                            String mainTabWhereSql = "SJOutWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "SJOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJOutWareHouse sJOutWareHouse = (SJOutWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> djpList = commonController.proceduceQuery("spGetKFJCDjpListForSJCK", proParams, "com.cn.bean.pro.KFJCDJPForSJCK", opt.getConnect());
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                for (Object obj : djpList) {
                                    KFJCDJPForSJCK sjck = (KFJCDJPForSJCK) obj;
                                    limitMap.put(sjck.getPartCode(), sjck.getKfJCDjpAmount());
                                }

                                Class objClass = Class.forName("com.cn.bean.in." + "SJOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                String detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewSJOutWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewSJOutWareHouseList.PartCode"
                                        + " and viewSJOutWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "SJOutWareHouseList", "*", whereSql, pageSize, pageIndex, "SJOutWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        SJOutWareHouseList sj = (SJOutWareHouseList) obj;
                                        //sj.setInboundAmount(Integer.valueOf(limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                        sj.setInboundAmount(Integer.valueOf((null == limitMap.get(sj.getPartCode())) ? "0" : limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(sJOutWareHouse.getSjCKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
                                    //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "inboundAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "待检品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCDjpAmount"};
                                //json = queryOperate(target, "com.cn.bean.in.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCDjpListForSJCK", proParams, "com.cn.bean.pro.KFJCDJPForSJCK", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        KFJCDJPForSJCK sjck = (KFJCDJPForSJCK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        filterList.add(sjck);
//                                        PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                        sjck.setWareHouseManagerName(category.getWareHouseManagerName());
                                    }
                                    json = getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCDJPForSJCK", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", "sjCKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("sjCKAuditStaffName", session.getAttribute("user"));
                            obj.put("sjCKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "SJOutWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String sjOutWareHouseID = arrayParam.getJSONObject(0).getString("sjOutWareHouseID");
                            String mainTabWhereSql = "SJOutWareHouseID = '" + sjOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJOutWareHouse", "*", mainTabWhereSql, 11, 1, "SJOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJOutWareHouse sJOutWareHouse = (SJOutWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getSjCKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "SJOutWareHouseList", "update", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                                    } else {
                                        json = Units.objectToJson(-1, "审核失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "单据未审核, 先审核单据才能审核明细!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "sjOutWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "sjOutWareHouseID") + "]", "com.cn.bean.in.", "SJOutWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "SJOutWareHouseList", update, "", delete, "data");
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.in.", "SJOutWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "SJOutWareHouseList", importPath + fileName);
                            if (importData != null) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "sjOutWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "sjOutWareHouseID") + "]", "com.cn.bean.in.", "SJOutWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检返回">
                case "送检返回": {
                    switch (operation) {
                        case "create": {
                            String whereCase = "exists (select * from tblSJBackWareHouseList list left join viewGYSPartContainerInfo gys"
                                    + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                    + " where list.SJBackWareHouseID = viewSJBackWareHouse.SJBackWareHouseID"
                                    + " and list.WareHouseManagername is null"
                                    + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                            whereCase = (operateType.compareTo("app") == 0) ? (whereCase) : ("");

                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJBackWareHouse", datas, rely, whereCase, "SJBackWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"送检返回单号\\", ",@SJTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "SJBackWareHouse.json"));
                            json = Units.insertStr(json, "\\\"送检返回单号\\", ",@SJTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("sjTKAuditStaffName", session.getAttribute("user"));
                            obj.put("sjTKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "SJBackWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.in.", "SJBackWareHouse", "SJBackWareHouseID", "sjTKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String sjBackWareHouseID = arrayParam.getJSONObject(0).getString("sjBackWareHouseID");
                            String mainTabWhereSql = "SJBackWareHouseID = '" + sjBackWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJBackWareHouse", "*", mainTabWhereSql, 11, 1, "SJBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJBackWareHouse sJOutWareHouse = (SJBackWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getSjTKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "SJBackWareHouseList", "update", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                                    } else {
                                        json = Units.objectToJson(-1, "审核失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "单据未审核, 先审核单据才能审核明细!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("sjTKAuditStaffName", session.getAttribute("user"));
                                obj.put("sjTKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "SJBackWareHouse", "update", opt.getConnect());
                            }
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("sjBackWareHouseID");
                            String mainTabWhereSql = "SJBackWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJBackWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "SJBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJBackWareHouse sJBackWareHouse = (SJBackWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> sjp = commonController.proceduceQuery("spGetKFJCSjpListForSJTK", proParams, "com.cn.bean.pro.KFJCSJPForSJTK", opt.getConnect());
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                for (Object obj : sjp) {
                                    KFJCSJPForSJTK sjtk = (KFJCSJPForSJTK) obj;
                                    limitMap.put(sjtk.getPartCode(), sjtk.getKfJCSjpAmount());
                                }

                                Class objClass = Class.forName("com.cn.bean.in." + "SJBackWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                String detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewSJBackWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewSJBackWareHouseList.PartCode"
                                        + " and viewSJBackWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "SJBackWareHouseList", "*", whereSql, pageSize, pageIndex, "SJBackWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        SJBackWareHouseList sj = (SJBackWareHouseList) obj;
                                        //sj.setSjCKAmount(Integer.valueOf(limitMap.get(sj.getPartCode())) + sj.getSjTKAmount());
                                        sj.setSjCKAmount(Integer.valueOf((null == limitMap.get(sj.getPartCode())) ? "0" : limitMap.get(sj.getPartCode())) + sj.getSjTKAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(sJBackWareHouse.getSjTKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
                                    //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "sjCKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "入库数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCSjpAmount"};
                                //json = queryOperate(target, "com.cn.bean.in.", "view", "SJOutWareHouseList", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCSjpListForSJTK", proParams, "com.cn.bean.pro.KFJCSJPForSJTK", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        KFJCSJPForSJTK sjtk = (KFJCSJPForSJTK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjtk.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        sjtk.setPartID(baseInfo.getPartID());
                                        sjtk.setPartName(baseInfo.getPartName());

                                        filterList.add(sjtk);
//                                        PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                        sjtk.setWareHouseManagerName(category.getWareHouseManagerName());
                                    }
                                    json = getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCSJPForSJTK", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }

                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJBackWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJBackWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "sjBackWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "sjBackWareHouseID") + "]", "com.cn.bean.in.", "SJBackWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "SJBackWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.in.", "SJBackWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "SJBackWareHouseList", importPath + fileName);
                            if (importData != null && importData.size() > 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJBackWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.in.", "SJBackWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "sjBackWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "sjBackWareHouseID") + "]", "com.cn.bean.in.", "SJBackWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="终端退库">
                case "终端退库": {
                    switch (operation) {
                        case "create": {
                            String whereCase = "";
                            if (operateType.compareTo("app") == 0) {
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    whereCase = "exists (select * from tblZDBackWareHouseList list left join viewGYSPartContainerInfo gys"
                                            + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                            + " where list.ZDBackWareHouseID = viewZDBackWareHouse.ZDBackWareHouseID"
                                            + " and list.WareHouseManagername is null"
                                            + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "') and ZDTKType <> '不良品'";
                                }

                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {
                                    whereCase = "exists (select * from tblZDBackWareHouseList list"
                                            + " where list.ZDBackWareHouseID = viewZDBackWareHouse.ZDBackWareHouseID"
                                            + " and list.WareHouseManagername is null)"
                                            + " and ZDTKType = '不良品'";
                                }
                            }

                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "ZDBackWareHouse", datas, rely, whereCase, "ZDBackWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"终端退库单据号\\", ",@ZDTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "ZDBackWareHouse.json"));
                            json = Units.insertStr(json, "\\\"终端退库单据号\\", ",@ZDTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String zdBackWareHouseID = arrayParam.getJSONObject(0).getString("zdBackWareHouseID");
                            String mainTabWhereSql = "ZDBackWareHouseID = '" + zdBackWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "ZDBackWareHouse", "*", mainTabWhereSql, 11, 1, "ZDBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                ZDBackWareHouse sJOutWareHouse = (ZDBackWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getZdTKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "ZDBackWareHouseList", "update", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                                    } else {
                                        json = Units.objectToJson(-1, "审核失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "单据未审核, 先审核单据才能审核明细!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("zdTKAuditStaffName", session.getAttribute("user"));
                                obj.put("zdTKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "ZDBackWareHouse", "update", opt.getConnect());
                            }
                            //json = queryOperate("com.cn.bean.in.", "view", "ZDBackWareHouseList", "ZDBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("zdBackWareHouseID");
                            String mainTabWhereSql = "ZDBackWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "ZDBackWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "ZDBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                ZDBackWareHouse zDBackWareHouse = (ZDBackWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("ZDCustomerID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("zdCustomerID"));

                                Class objClass = Class.forName("com.cn.bean.in." + "ZDBackWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                /*
                                String detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewZDBackWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewZDBackWareHouseList.PartCode"
                                        + " and viewZDBackWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                */
                                String detailWhereCase = "WareHouseManagername is null";
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "ZDBackWareHouseList", "*", whereSql, pageSize, pageIndex, "ZDBackWareHouseID", 0, opt.getConnect());
                                ArrayList supplierList = new ArrayList();
                                for (Object obj : detailList) {
                                    ZDBackWareHouseList sj = (ZDBackWareHouseList) obj;
                                    if (!supplierList.contains(sj.getSupplierID())) {
                                        supplierList.add(sj.getSupplierID());
                                    }
                                }
                                String supplierStr = Arrays.toString(supplierList.toArray());
                                proParams.put("SupplierIDStr", "string," + supplierStr.substring(1, supplierStr.length() - 1).replace(" ", ""));
                                //proParams.put("SupplierID", "string," + supplierStr.substring(1, supplierStr.length() - 1).replace(" ", ""));
                                HashMap<String, String> limitMap = new HashMap<>();
                                //System.out.println(proParams.toJSONString());
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("集配区") == 0) {
                                    List<Object> jpqjc = commonController.proceduceQuery("tbGetJPQJCPartListForZDTK_MulSupplier", proParams, "com.cn.bean.pro.JPQJCForZDTK", opt.getConnect());
                                    for (Object obj : jpqjc) {
                                        JPQJCForZDTK zdtk = (JPQJCForZDTK) obj;
                                        limitMap.put(zdtk.getSupplierID() + "_" + zdtk.getPartCode(), zdtk.getJpqJCAmount());
                                    }
                                }

                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("线旁") == 0) {
                                    List<Object> xpjc = commonController.proceduceQuery("tbGetXPJCPartListForXPTK_MulSupplier", proParams, "com.cn.bean.pro.XPJCForZDTK", opt.getConnect());
                                    for (Object obj : xpjc) {
                                        XPJCForZDTK zdtk = (XPJCForZDTK) obj;
                                        limitMap.put(zdtk.getSupplierID() + "_" + zdtk.getPartCode(), zdtk.getXpJCAmount());
                                    }
                                }

                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        ZDBackWareHouseList zdtk = (ZDBackWareHouseList) obj;
                                        int tkAmount = Integer.valueOf((null == limitMap.get(zdtk.getSupplierID() + "_" + zdtk.getPartCode())) ? "0" : limitMap.get(zdtk.getSupplierID() + "_" + zdtk.getPartCode())) + zdtk.getZdTKAmount();
                                        zdtk.setTkAmount(tkAmount);
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(zDBackWareHouse.getZdTKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
                                    //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "tkAmount", "inboundBatch"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "退库数量", "入库批次"};
                                int[] keysWidth = {20, 20, 20, 20, 20};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ZDCustomerID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("zdCustomerID"));

                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("集配区") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetJPQJCPartListForZDTK", proParams, "com.cn.bean.pro.JPQJCForZDTK", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        InWareHouseController controller = new InWareHouseController();
                                        //HashMap minInboundBatchMap = controller.getSupplierInboundBatch(JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            JPQJCForZDTK zdtk = (JPQJCForZDTK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + zdtk.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            zdtk.setPartID(baseInfo.getPartID());
                                            zdtk.setPartName(baseInfo.getPartName());
                                            zdtk.setInboundBatch(controller.getSupplierInboundBatch(JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID")));

                                            filterList.add(zdtk);
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "jpqJCAmount", "inboundBatch"};
                                        json = getSpecialTableJsonStr(filterList, "com.cn.bean.pro.JPQJCForZDTK", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("线旁") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetXPJCPartListForXPTK", proParams, "com.cn.bean.pro.XPJCForZDTK", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        InWareHouseController controller = new InWareHouseController();
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            XPJCForZDTK zdtk = (XPJCForZDTK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + zdtk.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            zdtk.setPartID(baseInfo.getPartID());
                                            zdtk.setPartName(baseInfo.getPartName());
                                            zdtk.setInboundBatch(controller.getSupplierInboundBatch(JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID")));

                                            filterList.add(zdtk);
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "xpJCAmount", "inboundBatch"};
                                        json = getSpecialTableJsonStr(filterList, "com.cn.bean.pro.XPJCForZDTK", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("zdTKAuditStaffName", session.getAttribute("user"));
                            obj.put("zdTKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "ZDBackWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.in.", "ZDBackWareHouse", "ZDBackWareHouseID", "zdTKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "ZDBackWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "ZDBackWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "zdBackWareHouseID")))
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "zdBackWareHouseID") + "]", "com.cn.bean.in.", "ZDBackWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "ZDBackWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //</editor-fold>
            }

        } catch (Exception e) {
            logger.info(subUri);
            logger.error("错误信息:" + e.getMessage(), e);
            json = Units.objectToJson(-1, "输入参数错误!", e.toString());
        }
        //logger.info(Units.getIpAddress(request) + "response:" + subUri + ",time:" + (new Date().getTime()));

        PrintWriter out = response.getWriter();

        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            out.print(json);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private String createOperate(int pageSize, String type, String jsonPackagePath, String beanPackage, String tableName, String orderField, Connection conn) throws Exception {
        return createOperateWithFilter(pageSize, type, jsonPackagePath, beanPackage, tableName, "", orderField, conn);
    }

    /**
     * 数据产生操作
     *
     * @param type
     * @param tableName
     * @param orderField
     * @return
     * @throws FileNotFoundException
     * @throws Exception
     */
    private String createOperateWithFilter(int pageSize, String type, String jsonPackagePath, String beanPackage, String tableName, String whereCase, String orderField, Connection conn) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = Units.returnFileContext(path + jsonPackagePath, tableName + ".json");
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", new Class[0]);
        if (result != null) {
            List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", whereCase, pageSize, 1, orderField, 0, conn);
            if (list != null && list.size() > 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, new Object[]{}));
                result = buffer.toString();
            }
            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "服务器出错!", null);
        }
        return json;
    }

    private String createOperateOnDate(int pageSize, String type, String jsonPackagePath, String beanPackage, String tableName, String datas,
            String rely, String whereCase, String orderField, Connection conn) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = Units.returnFileContext(path + jsonPackagePath, tableName + ".json");
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", new Class[0]);

        String whereSql = commonController.getWhereSQLStrWithDate(objClass, datas, rely, true);
        if (Units.strIsEmpty(whereSql)) {
            whereSql = whereCase;
        } else {
            whereSql = whereSql + (Units.strIsEmpty(whereCase) ? "" : " and " + whereCase);
        }

        if (result != null) {
            List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", whereSql, pageSize, 1, orderField, 0, conn);
            if (list != null && list.size() > 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, new Object[]{}));
                result = buffer.toString();
            }
            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "服务器出错!", null);
        }
        return json;
    }

    private String queryOperate(String beanPackage, String type, String tableName, String orderField, String keyWord, String rely, boolean isAll,
            Connection conn, int pageSize, int pageIndex) throws Exception {
        return queryOperateWithFilter(beanPackage, type, tableName, orderField, keyWord, rely, null, isAll, conn, pageSize, pageIndex);
    }

    /**
     * 数据查询操作
     *
     * @param type
     * @param tableName
     * @param orderField
     * @param conn
     * @return
     * @throws Exception
     */
    private String queryOperateWithFilter(String beanPackage, String type, String tableName, String orderField, String keyWord, String rely,
            String whereCase, boolean isAll, Connection conn, int pageSize, int pageIndex) throws Exception {
        String json;
        String result = "{}";
        CommonController commonController = new CommonController();
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", new Class[0]);
        String whereSql = commonController.getWhereSQLStr(objClass, keyWord, rely, isAll);
        if (Units.strIsEmpty(whereSql)) {
            whereSql = whereCase;
        } else {
            whereSql = whereSql + (Units.strIsEmpty(whereCase) ? "" : " and " + whereCase);
        }
        //System.out.println("whereSql:" + whereSql);
        List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", whereSql, pageSize, pageIndex, orderField, 0, conn);
        if (list != null && list.size() > 0) {
            StringBuffer buffer = new StringBuffer(result);
            buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
            buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
            //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
            result = buffer.toString();

            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "数据为空!", null);
        }

        return json;
    }

    /**
     * 包含日期的查询操作
     *
     * @param beanPackage
     * @param type
     * @param tableName
     * @param orderField
     * @param keyWord
     * @param rely 日期查询的起止日期, 格式为: {"start": "startTime", "end": "endTime"}
     * @param isAll
     * @param conn
     * @param pageSize
     * @param pageIndex
     * @return
     * @throws Exception
     */
    private String queryOnDateOperate(String beanPackage, String type, String tableName, String orderField, String keyWord, String rely, String whereCase,
            boolean isAll, Connection conn, int pageSize, int pageIndex) throws Exception {
        String json;
        String result = "{}";
        CommonController commonController = new CommonController();
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", new Class[0]);

        String whereSql = commonController.getWhereSQLStrWithDate(objClass, keyWord, rely, isAll);
        if (Units.strIsEmpty(whereSql)) {
            whereSql = whereCase;
        } else {
            whereSql = whereSql + (Units.strIsEmpty(whereCase) ? "" : " and " + whereCase);
        }

//        System.out.println("where SQL:" + whereSql);
        List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", whereSql, pageSize, pageIndex, orderField, 0, conn);
        if (list != null && list.size() > 0) {
            StringBuffer buffer = new StringBuffer(result);
            buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
            buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
            //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
            result = buffer.toString();

            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "数据为空!", null);
        }

        return json;
    }

    /**
     * 数据库查询, 只返回指定字段, 给指定字段重新设置key值
     *
     * @param type
     * @param tableName
     * @param orderField
     * @param keyWord
     * @param conn
     * @param pageSize
     * @param pageIndex
     * @param keys 返回的数据中的key值
     * @param keysName 返回的数据中的key值对应的名称(长度必须和keys参数长度保持一致)
     * @param keysWidth 返回的数据中的key值的宽度(长度必须和keys参数长度保持一致)
     * @param fieldsName 返回的key值在类中对应的字段(长度必须和keys参数长度保持一致)
     * @return
     * @throws Exception
     */
    private String queryOperate(String target, String beanPackage, String type, String tableName,
            String orderField, String keyWord, String rely, boolean isAll, Connection conn, int pageSize, int pageIndex,
            String[] keys, String[] keysName, int[] keysWidth, String[] fieldsName) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", new Class[0]);
        List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", commonController.getWhereSQLStr(objClass, keyWord, rely, isAll), pageSize, pageIndex, orderField, 0, conn);
        if (null != list && list.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("{\"titles\":{");
            for (int i = 0; i < keys.length; i++) {
                buffer.append("\"").append(keys[i]).append("\"").append(":");
                buffer.append("\"").append(keysName[i]).append(",").append(keysWidth[i]).append("%").append("\"").append(",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("},\"datas\":[");
            for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                Object object = it.next();
                buffer.append("{");
                for (int i = 0; i < keys.length; i++) {
                    PropertyDescriptor descriptor = new PropertyDescriptor(fieldsName[i], objClass);
                    Method getMethod = descriptor.getReadMethod();
                    buffer.append("\"").append(keys[i]).append("\":").append("\"").append(getMethod.invoke(object)).append("\"").append(",");
                }
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("},");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("]");
            buffer.append(",\"counts\":").append(method.invoke(null, new Object[]{}));
            buffer.append(",\"target\":").append("\"").append(target).append("\"");
            buffer.append(",\"rely\":").append(rely);

            if (objClass.isAnnotationPresent(ClassDescription.class
            )) {
                ClassDescription description = (ClassDescription) objClass.getAnnotation(ClassDescription.class
                );
                buffer.append(",\"module\":").append("\"").append(description.classDesc()).append("\"");
            }
            buffer.append("}");
            json = Units.objectToJson(0, "", buffer.toString());
        } else {
            json = Units.objectToJson(-1, "数据为空!", null);
        }
        return json;
    }

    private String getSpecialTableJsonStr(List<Object> list, String className, String[] keys, String[] keysName, int[] keysWidth, String[] fieldsName,
            String target, String rely) throws Exception {
        Class objClass = Class.forName(className);
        Method method = objClass.getMethod("getRecordCount", new Class[0]);
        StringBuffer buffer = new StringBuffer();
        buffer.append("{\"titles\":{");
        for (int i = 0; i < keys.length; i++) {
            buffer.append("\"").append(keys[i]).append("\"").append(":");
            buffer.append("\"").append(keysName[i]).append(",").append(keysWidth[i]).append("%").append("\"").append(",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("},\"datas\":[");
        for (Iterator<Object> it = list.iterator(); it.hasNext();) {
            Object object = it.next();
            buffer.append("{");
            for (int i = 0; i < keys.length; i++) {
                PropertyDescriptor descriptor = new PropertyDescriptor(fieldsName[i], objClass);
                Method getMethod = descriptor.getReadMethod();
                buffer.append("\"").append(keys[i]).append("\":").append("\"").append(getMethod.invoke(object)).append("\"").append(",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("},");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("]");
        buffer.append(",\"counts\":").append(method.invoke(null, new Object[]{}));
        buffer.append(",\"target\":").append("\"").append(target).append("\"");
        buffer.append(",\"rely\":").append(rely);

        if (objClass.isAnnotationPresent(ClassDescription.class
        )) {
            ClassDescription description = (ClassDescription) objClass.getAnnotation(ClassDescription.class
            );
            buffer.append(",\"module\":").append("\"").append(description.classDesc()).append("\"");
        }
        buffer.append("}");
        return Units.objectToJson(0, "", buffer.toString());
    }

    /**
     * 数据库操作
     *
     * @param tableName
     * @param update
     * @param add
     * @param delete
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private String submitOperate(String beanPackage, String tableName, String update, String add, String delete, String connType) throws Exception {
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        Connection conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(update) && !(update.compareTo("[]") == 0)) {
            ArrayList<Integer> updateResult = commonController.dataBaseOperate(update, beanPackage, tableName, "update", conn);
            if (updateResult.get(0) != 0) {
                return Units.objectToJson(-1, "修改操作失败!", null);
            }
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(add) && !(add.compareTo("[]") == 0)) {
            ArrayList<Integer> addResult = commonController.dataBaseOperate(add, beanPackage, tableName, "add", conn);
            if (addResult.get(0) != 0) {
                return Units.objectToJson(-1, "添加操作失败!", null);
            }
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(delete) && !(delete.compareTo("[]") == 0)) {
            ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, beanPackage, tableName, "delete", conn);
            if (delResult.get(0) != 0) {
                return Units.objectToJson(-1, "删除操作失败!", null);
            }
        }

        return Units.objectToJson(0, "操作成功!", null);
    }

    private ArrayList<Object> importDetailData(String detail, String beanPackage, String tableName, String fileName) throws Exception {
        //获取所有设置字段名称的字段
        Class objClass = Class.forName(beanPackage + tableName);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<Field> accessFields = new ArrayList<>();
        ArrayList<String> fieldDes = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class
            )) {
                FieldDescription description = field.getAnnotation(FieldDescription.class
                );
                if (description.operate().compareTo("import") == 0) {
                    fieldDes.add(description.description());
                    accessFields.add(field);
                }
            }
        }

        //从文件读入数据, 生成Excel解析
        InputStream inputStream = null;
        File file = new File(fileName);
        inputStream = new FileInputStream(file);
        Sheet sheet;
        if (fileName.endsWith(".xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
        } else if (fileName.endsWith(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
        } else {
            logger.info("导入的文件不是Excel文件!");
            return null;
        }

        Row headerRow = sheet.getRow(0);
        //如果第一行标题行为空或上传数据列数和类的字段描述长度不一致, 返回数据格式不正确
        //System.out.println("cells num:" + headerRow.getPhysicalNumberOfCells() + ",des size:" + fieldDes.size());
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != fieldDes.size()) {
//            json = Units.objectToJson(-1, "上传数据格式不正确, 请先下载模板, 按照模板格式录入数据", null);
            return null;
        }

        //根据表头名称与类字段名称找到对应关系
        int[] templateDataIndex = new int[fieldDes.size()];
        for (int i = 0; i < fieldDes.size(); i++) {
            Cell cell = headerRow.getCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String fieldName = cell.getStringCellValue();
            templateDataIndex[fieldDes.indexOf(fieldName)] = i;
        }

        ArrayList<Object> result = new ArrayList<>();
        //解析表格数据, 存入List中
        for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (Units.isEmptyRowForExcel(row)) {
                continue;
            }

            Object object = objClass.newInstance();
            JSONObject jSONObject = JSONObject.parseObject(detail);
            Iterator<String> keysIterator = jSONObject.keySet().iterator();
            while (keysIterator.hasNext()) {
                String key = keysIterator.next();
                for (Field field : fields) {
//                    System.out.println("fieldName:" + field.getName() + ",key:" + key);
                    if (field.getName().compareToIgnoreCase(key) == 0) {
//                        System.out.println("fieldName:" + field.getName() + ",key:" + key + ",key value:" + jSONObject.getString(key));
                        field.setAccessible(true);
                        if (!Units.strIsEmpty(jSONObject.getString(key))) {
                            field.set(object, jSONObject.getString(key));
                        }
                    }
                }
            }

            for (int j = 0; j < accessFields.size(); j++) {
                Field field = accessFields.get(j);
                field.setAccessible(true);
                Cell cell = row.getCell(templateDataIndex[j]);
//                String fieldType = field.getGenericType().toString();

                if (field.getType() == int.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        if (Units.strIsEmpty(Units.getCellValue(cell))) {
                            field.set(object, 0);
                        } else {
                            field.set(object, Integer.valueOf(Units.subZeroAndDot(Units.getCellValue(cell))));

                        }
                    }
                } else if (field.getType() == float.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        field.set(object, Float.valueOf(Units.getCellValue(cell)));

                    }
                } else if (field.getType() == double.class) {
                    if (cell == null) {
                        field.set(object, false);
                    } else {
                        if (Units.strIsEmpty(Units.getCellValue(cell))) {
                            field.set(object, 0);
                        } else {
                            field.set(object, Double.valueOf(Units.getCellValue(cell)));

                        }
                    }
                } else if (field.getType() == boolean.class) {
                    if (cell == null) {
                        field.set(object, false);
                    } else {
                        if (Units.strIsEmpty(Units.getCellValue(cell))) {
                            field.set(object, false);
                        } else {
                            field.set(object, Boolean.valueOf(Units.getCellValue(cell)));
                        }
                    }
                } else {
                    if (cell == null) {
                        field.set(object, null);
                    } else {
                        field.set(object, Units.getCellValue(cell));
                    }
                }
            }

            result.add(object);
        }
        return result;

//        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
//        return JSONObject.toJSONString(result);
    }

    private String exportTemplate(String beanPackage, String tableName, ArrayList<Object> datas) throws Exception {
        Class objClass = Class.forName(beanPackage + tableName);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<String> fieldDes = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class
            )) {
                FieldDescription description = field.getAnnotation(FieldDescription.class
                );
                if (description.operate().compareTo("import") == 0) {
                    fieldDes.add(description.description());
                }
            }
        }

        String filePath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
        String fileName = Units.getNowTimeNoSeparator() + ".xls";
        File file = Units.createNewFile(filePath, fileName);
        OutputStream stream = new FileOutputStream(file);

        ExportExcel exportExcel = new ExportExcel();
        String[] headers = new String[fieldDes.size()];
        for (int i = 0; i < fieldDes.size(); i++) {
            headers[i] = fieldDes.get(i);
        }
        exportExcel.exportExcel("导出", headers, datas, stream, "yyyy-MM-dd HH:mm:ss");
        return Units.objectToJson(0, "导出成功!", "{\"fileUrl\":\"" + getServletContext().getContextPath() + "/exportFile/" + fileName + "\"}");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String params = request.getParameter("params");
//        String params = new String(request.getQueryString().getBytes("iso-8859-1"),"utf-8").replaceAll("%22", "\"");
        processRequest(request, response, params);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String params = getRequestPostStr(request);
        processRequest(request, response, params);
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
