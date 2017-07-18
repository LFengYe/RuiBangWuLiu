/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Customer;
import com.cn.bean.FieldDescription;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.report.*;
import com.cn.controller.CommonController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class ReportInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ReportInterface.class);

//    private CommonController commonController;
//    private DatabaseOpt opt;
//    private boolean isForward;
//    private String forwardStr;

    @Override
    public void init() throws ServletException {
        super.init();
//        commonController = new CommonController();
//        opt = new DatabaseOpt();
//        isForward = false;
//        forwardStr = null;
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
            String operateType = paramsJson.getString("type");
            String start = paramsJson.getString("start");
            String end = paramsJson.getString("end");
            int isHistory = paramsJson.getIntValue("isHistory");
            int pageIndex = paramsJson.getIntValue("pageIndex");
            int pageSize = paramsJson.getIntValue("pageSize");

            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";

            /*验证是否登陆*/
            if (!"userLogin".equals(module) && session.getAttribute("user") == null) {
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

            switch (module) {
                /**
                 * ***************************************数据报表管理**************************************
                 */
                //<editor-fold desc="部品收发存总表_spGetSFCTotalData">
                case "部品收发存总表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetSFCTotalData", "SFCTotalData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    SFCTotalData data = (SFCTotalData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());
                                    data.setPartUnit(baseInfo.getPartUnit());
                                    data.setAutoStylingName(baseInfo.getAutoStylingName());
                                    data.setDcAmount(String.valueOf(baseInfo.getdCAmount()));

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "export": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            List<Object> list = commonController.proceduceQuery("spGetSFCTotalData", proParams, "com.cn.bean.report.SFCTotalData", opt.getConnect());
                            if (list != null && list.size() > 0) {
                                for (Object obj : list) {
                                    SFCTotalData data = (SFCTotalData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());
                                    data.setPartUnit(baseInfo.getPartUnit());
                                    data.setAutoStylingName(baseInfo.getAutoStylingName());
                                    data.setDcAmount(String.valueOf(baseInfo.getdCAmount()));

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            }
                            json = exportData("com.cn.bean.report.", "SFCTotalData", (ArrayList<Object>) list);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品计划完成情况_spGETJHCKCompletionAllInfo">
                case "部品计划完成情况": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string,");
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGETJHCKCompletionAllInfo", "JHCKCompletionAllInfo", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                }
                            });
                            break;
                        }
                        /*
                        case "create": {
                            String result = Units.returnFileContext(path + "com/cn/json/report/", "JHCKCompletionAllInfo.json");
                            JSONObject proParams = new JSONObject();
                            List<Object> list = commonController.proceduceQuery("spGETJHCKCompletionAllInfo", proParams, "com.cn.bean.report.JHCKCompletionAllInfo", opt.getConnect());
                            if (list != null && list.size() > 0) {
                                StringBuffer buffer = new StringBuffer(result);
                                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                                result = buffer.toString();
                            }
                            json = Units.objectToJson(0, "", result);
                            break;
                        }
                        case "export": {
                            JSONObject proParams = new JSONObject();
                            List<Object> list = commonController.proceduceQuery("spGETJHCKCompletionAllInfo", proParams, "com.cn.bean.report.JHCKCompletionAllInfo", opt.getConnect());
                            json = exportData("com.cn.bean.report.", "JHCKCompletionAllInfo", (ArrayList<Object>) list);
                        }
                        */
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品报警明细表_spGetKCAlertListData">
                case "部品报警明细表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKCAlertListData", "KCAlertListData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KCAlertListData data = (KCAlertListData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="期初明细">
                //</editor-fold>

                //<editor-fold desc="部品出入库明细">
                //<editor-fold desc="待检入库报表_spGetRKListForDjpRK">
                case "待检入库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForDjpRK", "RKListForDjpRK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForDjpRK data = (RKListForDjpRK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForDjpRK", "RKListForDjpRK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForDjpRK data = (RKListForDjpRK) obj;
                                    if (data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检出库报表_spGetRKListForSJCK">
                case "送检出库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForSJCK", "RKListForSJCK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForSJCK data = (RKListForSJCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForSJCK", "RKListForSJCK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForSJCK data = (RKListForSJCK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检返回报表_spGetRKListForSJTK">
                case "送检返回报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForSJTK", "RKListForSJTK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForSJTK data = (RKListForSJTK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForSJTK", "RKListForSJTK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForSJTK data = (RKListForSJTK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="良品入库报表_spGetRKListForLpRK">
                case "良品入库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForLpRK", "RKListForLpRK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForLpRK data = (RKListForLpRK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForLpRK", "RKListForLpRK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForLpRK data = (RKListForLpRK) obj;
                                    if (data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="不良品入库报表_spGetRKListForBLpRK">
                case "不良品入库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForBLpRK", "RKListForBLpRK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForBLpRK data = (RKListForBLpRK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForBLpRK", "RKListForBLpRK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForBLpRK data = (RKListForBLpRK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修良品入库_spGetRKListForFxpRK(良品)">
                case "返修良品入库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForFxpRK", "RKListForFxpRK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForFxpRK data = (RKListForFxpRK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForFxpRK", "RKListForFxpRK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForFxpRK data = (RKListForFxpRK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修不良品入库_spGetRKListForFxpRK(不良品)">
                case "返修不良品入库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKListForFxpRK", "RKListForFxpRK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForFxpRK data = (RKListForFxpRK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKListForFxpRK", "RKListForFxpRK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKListForFxpRK data = (RKListForFxpRK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="良品返修出库_spGetCKListForFxpCK(良品)">
                case "良品返修出库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetCKListForFxpCK", "CKListForFxpCK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForFxpCK data = (CKListForFxpCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKListForFxpCK", "CKListForFxpCK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKListForFxpCK data = (CKListForFxpCK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="不良品返修出库_spGetCKListForFxpCK(不良品)">
                case "不良品返修出库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetCKListForFxpCK", "CKListForFxpCK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForFxpCK data = (CKListForFxpCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKListForFxpCK", "CKListForFxpCK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKListForFxpCK data = (CKListForFxpCK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="计划出库报表_spGetCKListForZCJHCK">
                case "计划出库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetCKListForZCJHCK", "CKListForZCJHCK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForZCJHCK data = (CKListForZCJHCK) obj;
//                                    System.out.println(data.getPartCode() + ":" + RedisAPI.get("partBaseInfo_" + data.getPartCode()));
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                    data.setZdCustomerName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKListForZCJHCK", "CKListForZCJHCK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKListForZCJHCK data = (CKListForZCJHCK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                        data.setZdCustomerName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="临时调货报表_spGetCKListForLSDHCK">
                case "临时调货报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetCKListForLSDHCK", "CKListForZCJHCK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForZCJHCK data = (CKListForZCJHCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                    data.setZdCustomerName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKListForLSDHCK", "CKListForZCJHCK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKListForZCJHCK data = (CKListForZCJHCK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                        data.setZdCustomerName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="非生产领料报表_spGetCKListForFJHCK">
                case "非生产领料报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetCKListForFJHCK", "CKListForFJHCK", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForFJHCK data = (CKListForFJHCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerName()), Customer.class);
                                    data.setFzDCustomerName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKListForFJHCK", "CKListForFJHCK", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKListForFJHCK data = (CKListForFJHCK) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerID()), Customer.class);
                                        data.setFzDCustomerName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="配送上线报表_spGetCKListForJPSX">
                case "配送上线报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetCKListForJPSX", "CKListForJPSX", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForJPSX data = (CKListForJPSX) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                    data.setZdCustomerName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKListForJPSX", "CKListForJPSX", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKListForJPSX data = (CKListForJPSX) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0
                                            && data.getZdCustomerID().compareToIgnoreCase(dataJson.getString("zdCustomerID")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                        data.setZdCustomerName(customer.getCustomerAbbName());

                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="退货出库报表_spGetTHListForBPTH">
                case "退货出库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("PartState", "string," + paramsJson.getString("partStatus"));
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                //proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetTHListForBPTH", "THListForBPTH", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    THListForBPTH data = (THListForBPTH) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetTHListForBPTH", "THListForBPTH", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    THListForBPTH data = (THListForBPTH) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>
                //</editor-fold>

                //<editor-fold desc="部品库存明细">
                //<editor-fold desc="良品库存报表_spGetKFJCListForLp">
                case "良品库存报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                //proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCListForLp", "KFJCListForLp", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCListForLp data = (KFJCListForLp) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFJCListForLp", "KFJCListForLp", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFJCListForLp data = (KFJCListForLp) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="不良品库存报表_spGetKFJCListForBLp">
                case "不良品库存报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCListForBLp", "KFJCListForBLp", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCListForBLp data = (KFJCListForBLp) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFJCListForBLp", "KFJCListForBLp", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFJCListForBLp data = (KFJCListForBLp) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="待检库存报表_spGetKFJCListForDjp">
                case "待检库存报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCListForDjp", "KFJCListForDjp", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCListForDjp data = (KFJCListForDjp) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFJCListForDjp", "KFJCListForDjp", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFJCListForDjp data = (KFJCListForDjp) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检品库存报表_spGetKFJCListForSjp">
                case "送检品库存报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCListForSjp", "KFJCListForSjp", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCListForSjp data = (KFJCListForSjp) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFJCListForSjp", "KFJCListForSjp", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFJCListForSjp data = (KFJCListForSjp) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修品库存报表_spGetKFJCListForFxp">
                case "返修品库存报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCListForFxp", "KFJCListForFxp", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCListForFxp data = (KFJCListForFxp) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFJCListForFxp", "KFJCListForFxp", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFJCListForFxp data = (KFJCListForFxp) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>
                //</editor-fold>

                //<editor-fold desc="分录报表">
                //<editor-fold desc="库存期初分录_spGetKFQCFenLuData">
                case "库存期初分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            json = reportOperate(operateType, "spGetKFQCFenLuData", "KFQCFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFQCFenLuData data = (KFQCFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFQCFenLuData", "KFQCFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFQCFenLuData data = (KFQCFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品退库分录_spGetTKFenLuData">
                case "部品退库分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            json = reportOperate(operateType, "spGetTKFenLuData", "TKFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    TKFenLuData data = (TKFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetTKFenLuData", "TKFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    TKFenLuData data = (TKFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品退货分录_spGetTHFenLuData">
                case "部品退货分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            json = reportOperate(operateType, "spGetTHFenLuData", "THFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    THFenLuData data = (THFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetTHFenLuData", "THFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    THFenLuData data = (THFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品出库分录_spGetCKFenLuData">
                case "部品出库分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }

                            json = reportOperate(operateType, "spGetCKFenLuData", "CKFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKFenLuData data = (CKFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetCKFenLuData", "CKFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    CKFenLuData data = (CKFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品入库分录_spGetRKFenLuData">
                case "部品入库分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetRKFenLuData", "RKFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKFenLuData data = (RKFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetRKFenLuData", "RKFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    RKFenLuData data = (RKFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="库房结存分录_spGetKFJCFenLuData">
                case "库房结存分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCFenLuData", "KFJCFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCFenLuData data = (KFJCFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFJCFenLuData", "KFJCFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFJCFenLuData data = (KFJCFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场期初分录_spGetXCQCFenLuData">
                case "现场期初分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetXCQCFenLuData", "XCQCFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XCQCFenLuData data = (XCQCFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetXCQCFenLuData", "XCQCFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    XCQCFenLuData data = (XCQCFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场结存分录_spGetXCJCFenLuData">
                case "现场结存分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetXCJCFenLuData", "XCJCFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XCJCFenLuData data = (XCJCFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetXCJCFenLuData", "XCJCFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    XCJCFenLuData data = (XCJCFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="库房调账分录_spGetKFTZFenLuData">
                case "库房盈亏分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFTZFenLuData", "KFTZFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFTZFenLuData data = (KFTZFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetKFTZFenLuData", "KFTZFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    KFTZFenLuData data = (KFTZFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场调账分录_spGetXCTZFenLuData">
                case "现场盈亏分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetXCTZFenLuData", "XCTZFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XCTZFenLuData data = (XCTZFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "spGetXCTZFenLuData", "XCTZFenLuData", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    XCTZFenLuData data = (XCTZFenLuData) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0
                                            && data.getPartCode().compareToIgnoreCase(dataJson.getString("partCode")) == 0) {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //</editor-fold>
                
                //<editor-fold desc="盛具报表">
                case "盛具报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "tbGetContainerAmount", "ContainerAmount", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    ContainerAmount data = (ContainerAmount) obj;
                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(operateType, "tbGetContainerAmount", "ContainerAmount", proParams, new ReportInterface.FilterListItemOperate() {
                                @Override
                                public void itemFilter(List<Object> filterList, Object obj) {
                                    ContainerAmount data = (ContainerAmount) obj;
                                    if (data.getSupplierID().compareToIgnoreCase(dataJson.getString("supplierID")) == 0) {
                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                        filterList.add(data);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="报检信息查询">
                case "报检信息查询": {
                    String whereCase1 = "DJInWareHouseID in (select DJInWareHouseID from tblDJInWareHouse where DJRKAuditTime is not null)";
                    String whereCase = "(InspectionTime is not null) and " + whereCase1;
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(Integer.MAX_VALUE, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", datas, rely, whereCase, "DJInWareHouseID", opt.getConnect());
                            break;
                        }
                    }
                    break;
                }
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

    interface ReportItemOperate {

        void itemObjOperate(Object obj);
    }

    interface FilterListItemOperate {

        void itemFilter(List<Object> filterList, Object obj);
    }

    private String createOperateOnDate(int pageSize, String type, String jsonPackagePath, String beanPackage, String tableName, String datas,
            String rely, String whereCase, String orderField, Connection conn) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = Units.returnFileContext(path + jsonPackagePath, tableName + ".json");
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);

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
                buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, null));
                result = buffer.toString();
            }
            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "服务器出错!", null);
        }
        return json;
    }
    
    /**
     * 报表操作
     *
     * @param operateType
     * @param proceduceName
     * @param className
     * @param start
     * @param end
     * @param itemOperate
     * @return
     * @throws Exception
     */
    private String reportOperate(String operateType, String proceduceName, String className, JSONObject proParams, ReportItemOperate itemOperate) throws Exception {
        String result = null;
        String json = null;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        if (operateType.compareTo("create") == 0) {
            result = Units.returnFileContext(path + "com/cn/json/report/", className + ".json");
        }
        if (operateType.compareTo("search") == 0) {
            result = "{}";
        }

        List<Object> list = commonController.proceduceQuery(proceduceName, proParams, "com.cn.bean.report." + className, opt.getConnect());
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                itemOperate.itemObjOperate(obj);
            }
            if (operateType.compareTo("export") == 0) {
                json = exportData("com.cn.bean.report.", className, (ArrayList<Object>) list);
            } else if (operateType.compareTo("create") == 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), ",\"datas\":" + JSONObject.toJSONString(list, Units.features));
                result = buffer.toString();
                json = Units.objectToJson(0, "", result);
            } else if (operateType.compareTo("search") == 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                result = buffer.toString();
                json = Units.objectToJson(0, "", result);
            }
        } else {
            json = Units.objectToJson(0, "数据为空!", result);
        }
        return json;
    }
    
    private String reportOperateWithFilter(String operateType, String proceduceName, String className, JSONObject proParams, FilterListItemOperate itemOperate) throws Exception {
        String result = null;
        String json = null;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        if (operateType.compareTo("create") == 0) {
            result = Units.returnFileContext(path + "com/cn/json/report/", className + ".json");
        }
        if (operateType.compareTo("search") == 0) {
            result = "{}";
        }

        List<Object> list = commonController.proceduceQuery(proceduceName, proParams, "com.cn.bean.report." + className, opt.getConnect());
        List<Object> filterList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                itemOperate.itemFilter(filterList, obj);
            }
            if (operateType.compareTo("export") == 0) {
                json = exportData("com.cn.bean.report.", className, (ArrayList<Object>) filterList);
            } else if (operateType.compareTo("create") == 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), ",\"datas\":" + JSONObject.toJSONString(filterList, Units.features));
                result = buffer.toString();
                json = Units.objectToJson(0, "", result);
            } else if (operateType.compareTo("search") == 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(filterList, Units.features));
                result = buffer.toString();
                json = Units.objectToJson(0, "", result);
            }
        } else {
            json = Units.objectToJson(0, "数据为空!", result);
        }
        return json;
    }

    /**
     * 导入数据到Excel
     *
     * @param tableName
     * @return
     * @throws Exception
     */
    private String exportData(String beanPackage, String tableName, ArrayList<Object> datas) throws Exception {
        Class objClass = Class.forName(beanPackage + tableName);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<String> fieldDes = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class)) {
                FieldDescription description = field.getAnnotation(FieldDescription.class);
                fieldDes.add(description.description());
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
