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
import com.cn.bean.report.CKFenLuData;
import com.cn.bean.report.CKListForFJHCK;
import com.cn.bean.report.CKListForFxpCK;
import com.cn.bean.report.CKListForJPSX;
import com.cn.bean.report.CKListForZCJHCK;
import com.cn.bean.report.KFJCFenLuData;
import com.cn.bean.report.KFJCListForBLp;
import com.cn.bean.report.KFJCListForDjp;
import com.cn.bean.report.KFJCListForFxp;
import com.cn.bean.report.KFJCListForLp;
import com.cn.bean.report.KFJCListForSjp;
import com.cn.bean.report.KFQCFenLuData;
import com.cn.bean.report.RKFenLuData;
import com.cn.bean.report.RKListForBLpRK;
import com.cn.bean.report.RKListForDjpRK;
import com.cn.bean.report.RKListForFxpRK;
import com.cn.bean.report.RKListForLpRK;
import com.cn.bean.report.RKListForSJCK;
import com.cn.bean.report.RKListForSJTK;
import com.cn.bean.report.SFCTotalData;
import com.cn.bean.report.THFenLuData;
import com.cn.bean.report.THListForBPTH;
import com.cn.bean.report.TKFenLuData;
import com.cn.bean.report.XCJCFenLuData;
import com.cn.bean.report.XCQCFenLuData;
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

    private CommonController commonController;
    private DatabaseOpt opt;

    @Override
    public void init() throws ServletException {
        super.init();
        commonController = new CommonController();
        opt = new DatabaseOpt();
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

        try {
            System.out.println(subUri + ",params:" + params);
            JSONObject paramsJson = JSONObject.parseObject(params);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                        case "request_detail": {

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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品报警明细表_spGetKCAlertListData">
                case "部品报警明细表": {
                    switch (operation) {
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
                            break;
                        }
                    }
                    break;
                }
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetTHListForBPTH", "THListForBPTH", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    THListForBPTH data = (THListForBPTH) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetKFJCListForLp", "KFJCListForLp", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFJCListForLp data = (KFJCListForLp) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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

                //<editor-fold desc="部品退库分录_spGetTKFenLuData">
                case "部品退库分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            json = reportOperate(operateType, "spGetTKFenLuData", "TKFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    TKFenLuData data = (TKFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                
                //<editor-fold desc="部品退货分录_spGetTHFenLuData">
                case "部品退货分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            json = reportOperate(operateType, "spGetTHFenLuData", "THFenLuData", proParams, new ReportInterface.ReportItemOperate() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    THFenLuData data = (THFenLuData) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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

                //<editor-fold desc="部品计划出库分录_spGetCKFenLuData">
                case "部品计划出库分录": {
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            if (paramsJson.getString("name").compareTo("jhck2") == 0) {
                                JSONObject proParams = new JSONObject();
                                if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                    proParams.put("BeginTime", "string," + start);
                                    proParams.put("Endtime", "string," + end);
                                }
                                JSONObject dataJson = JSONObject.parseObject(datas);
                                proParams.put("ZDCustomerID", "string,9998");
                                proParams.put("PartCode", "string," + dataJson.getString("partCode"));
                                proParams.put("SupplierID", "string," + dataJson.getString("supplierID"));

                                json = reportOperate(operateType, "spGetCKDetailListForZCJHCK", "CKListForZCJHCK", proParams, new ReportInterface.ReportItemOperate() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        CKListForZCJHCK data = (CKListForZCJHCK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                        data.setZdCustomerName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            if (paramsJson.getString("name").compareTo("lsdh1") == 0) {

                            }
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
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
                //</editor-fold>
            }
        } catch (Exception e) {
            logger.info(subUri);
            logger.error("错误信息:" + e.getMessage(), e);
            json = Units.objectToJson(-1, "输入参数错误!", e.toString());
        }

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
