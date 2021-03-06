/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Customer;
import com.cn.bean.Employee;
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
            String module = paramsJson.getString("module");
            String operation = paramsJson.getString("operation");
            String rely = (paramsJson.getString("rely") == null) ? ("{}") : (paramsJson.getString("rely"));
            String datas = (paramsJson.getString("datas") == null) ? ("") : paramsJson.getString("datas");
            String operateType = paramsJson.getString("type");
            String clientType = paramsJson.getString("clientType");
            String dataType = (paramsJson.getString("dataType") == null) ? ("isCur") : paramsJson.getString("dataType");// isCur表示当期查询, isHis表示往期查询
            String start = paramsJson.getString("start");
            String end = paramsJson.getString("end");
            String jzyMonth = paramsJson.getString("jzyMonth");
            int pageIndex = paramsJson.getIntValue("pageIndex");
            int pageSize = paramsJson.getIntValue("pageSize");

            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";

            /*验证是否登陆*/
            if (!"userLogin".equals(module)
                    && (session.getAttribute("user") == null || session.getAttribute("loginType") == null || session.getAttribute("employee") == null)) {
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

            String loginType = session.getAttribute("loginType").toString();
            String userName = session.getAttribute("user").toString();
            final Employee employee = (loginType.compareTo("employeeLogin") == 0 && session.getAttribute("employee") != null) ? ((Employee) session.getAttribute("employee")) : (null);
            final Customer curCustomer = (loginType.compareTo("customerLogin") == 0 && session.getAttribute("employee") != null) ? ((Customer) session.getAttribute("employee")) : (null);

            switch (module) {
                /**
                 * ***************************************数据报表管理**************************************
                 */
                //<editor-fold desc="部品收发存总表_spGetSFCTotalData">
                case "部品收发存总表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetSFCTotalDataWithFilter", "com.cn.bean.report.", "SFCTotalData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetSFCTotalDataWithFilter", "com.cn.bean.report.", "SFCTotalData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                                /*
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    json = reportOperateWithPage(dataType, operateType, "spGetSFCTotalDataWithFilter", "com.cn.bean.report.", "SFCTotalData",
                                            "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                            new ReportItemOperateAdapter() {
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

                                        @Override
                                        public void itemFilter(List<Object> filterList, Object obj) {
                                            SFCTotalData data = (SFCTotalData) obj;
                                            System.out.println("gys:" + RedisAPI.get(data.getSupplierID() + "_" + data.getPartCode().toLowerCase()));
                                            GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(data.getSupplierID() + "_" + data.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                            if (containerInfo.getWareHouseManagerName().compareTo(employee.getEmployeeName()) != 0) {
                                                return;
                                            }
                                            super.itemFilter(filterList, obj);
                                        }
                                    });
                                } else {
                                    json = reportOperateWithPage(dataType, operateType, "spGetSFCTotalDataWithFilter", "com.cn.bean.report.", "SFCTotalData",
                                            "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                            new ReportItemOperateAdapter() {
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
                                }
                                 */
                            }
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
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGETJHCKCompletionAllInfoWithFilter", "com.cn.bean.report.", "JHCKCompletionAllInfo",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGETJHCKCompletionAllInfoWithFilter", "com.cn.bean.report.", "JHCKCompletionAllInfo",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                    }
                                });
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品报警明细表_spGetKCAlertListData">
                case "部品报警明细表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKCAlertListDataWithFilter", "com.cn.bean.report.", "KCAlertListData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKCAlertListDataWithFilter", "com.cn.bean.report.", "KCAlertListData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="期初明细">
                //</editor-fold
                //<editor-fold desc="部品出入库明细">
                //<editor-fold desc="待检入库报表_spGetRKListForDjpRK">
                case "待检入库报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetRKListForDjpRKWithFilter", "com.cn.bean.report.", "RKListForDjpRK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetRKListForDjpRKWithFilter", "com.cn.bean.report.", "RKListForDjpRK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetRKListForDjpRKWithFilter", "com.cn.bean.report.", "RKListForDjpRK",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                            json = reportOperate(dataType, operateType, "spGetRKListForSJCK", "RKListForSJCK", proParams, new ReportItemOperateAdapter() {
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
                            json = reportOperateWithFilter(dataType, operateType, "spGetRKListForSJCK", "RKListForSJCK", proParams, new ReportInterface.FilterListItemOperate() {
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
                            json = reportOperate(dataType, operateType, "spGetRKListForSJTK", "RKListForSJTK", proParams, new ReportItemOperateAdapter() {
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
                            json = reportOperateWithFilter(dataType, operateType, "spGetRKListForSJTK", "RKListForSJTK", proParams, new ReportInterface.FilterListItemOperate() {
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
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetRKListForLpRKWithFilter", "com.cn.bean.report.", "RKListForLpRK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetRKListForLpRKWithFilter", "com.cn.bean.report.", "RKListForLpRK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetRKListForLpRKWithFilter", "com.cn.bean.report.", "RKListForLpRK",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="不良品入库报表_spGetRKListForBLpRK">
                case "不良品入库报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetRKListForBLpRKWithFilter", "com.cn.bean.report.", "RKListForBLpRK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetRKListForBLpRKWithFilter", "com.cn.bean.report.", "RKListForBLpRK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetRKListForBLpRKWithFilter", "com.cn.bean.report.", "RKListForBLpRK",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修良品入库_spGetRKListForFxpRK(良品)">
                case "返修良品入库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.RKListForFxpRK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForFxpRKWithFilter", "RKListForFxpRK", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.RKListForFxpRK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForFxpRKWithFilter", "RKListForFxpRK", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修不良品入库_spGetRKListForFxpRK(不良品)">
                case "返修不良品入库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.RKListForFxpRK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForFxpRKWithFilter", "RKListForFxpRK", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.RKListForFxpRK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForFxpRKWithFilter", "RKListForFxpRK", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="良品返修出库_spGetCKListForFxpCK(良品)">
                case "良品返修出库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.CKListForFxpCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForFxpCKWithFilter", "CKListForFxpCK", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.CKListForFxpCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForFxpCKWithFilter", "CKListForFxpCK", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="不良品返修出库_spGetCKListForFxpCK(不良品)">
                case "不良品返修出库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.CKListForFxpCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForFxpCKWithFilter", "CKListForFxpCK", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.CKListForFxpCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForFxpCKWithFilter", "CKListForFxpCK", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="良品终端退库_spGetRKListForZDTK_JPQ(良品)">
                case "良品终端退库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.RKListForZDTK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForZDTK_JPQWithFilter", "RKListForZDTK", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForZDTK data = (RKListForZDTK) obj;
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
                            Class classPath = Class.forName("com.cn.bean.report.RKListForZDTK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForZDTK_JPQWithFilter", "RKListForZDTK", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForZDTK data = (RKListForZDTK) obj;
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

                //<editor-fold desc="不良品终端退库_spGetRKListForZDTK_JPQ(不良品)">
                case "不良品终端退库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.RKListForZDTK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForZDTK_JPQWithFilter", "RKListForZDTK", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForZDTK data = (RKListForZDTK) obj;
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
                            Class classPath = Class.forName("com.cn.bean.report.RKListForZDTK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetRKListForZDTK_JPQWithFilter", "RKListForZDTK", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForZDTK data = (RKListForZDTK) obj;
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="线旁退集配区报表_spGetRKListForZDTK_XP">
                case "线旁退集配区报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetRKListForZDTK_XPWithFilter", "com.cn.bean.report.", "RKListForZDTK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        RKListForZDTK data = (RKListForZDTK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                        data.setZdCustomerName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetRKListForZDTK_XPWithFilter", "com.cn.bean.report.", "RKListForZDTK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        RKListForZDTK data = (RKListForZDTK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getZdCustomerID()), Customer.class);
                                        data.setZdCustomerName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetRKListForZDTK_XPWithFilter", "com.cn.bean.report.", "RKListForZDTK",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    RKListForZDTK data = (RKListForZDTK) obj;
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="计划出库报表_spGetCKListForZCJHCK">
                case "计划出库报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.CKListForZCJHCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("ZDCustomerID", "string,9998");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("EndTime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForZCJHCKWithFilter", "CKListForZCJHCK", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.CKListForZCJHCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("ZDCustomerID", "string,9998");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("EndTime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForZCJHCKWithFilter", "CKListForZCJHCK", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="临时调货报表_spGetCKListForLSDHCK">
                case "临时调货报表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.CKListForFJHCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("ZDCustomerID", "string,9998");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForLSDHCKWithFilter", "CKListForFJHCK", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForFJHCK data = (CKListForFJHCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerID()), Customer.class);
                                    data.setFzDCustomerName(customer.getCustomerAbbName());
                                }
                            });
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.CKListForFJHCK");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("ZDCustomerID", "string,9998");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetCKListForLSDHCKWithFilter", "CKListForFJHCK", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForFJHCK data = (CKListForFJHCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerID()), Customer.class);
                                    data.setFzDCustomerName(customer.getCustomerAbbName());
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
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetCKListForFJHCKWithFilter", "com.cn.bean.report.", "CKListForFJHCK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        CKListForFJHCK data = (CKListForFJHCK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerID()), Customer.class);
                                        data.setFzDCustomerName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetCKListForFJHCKWithFilter", "com.cn.bean.report.", "CKListForFJHCK",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        CKListForFJHCK data = (CKListForFJHCK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());

                                        customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerID()), Customer.class);
                                        data.setFzDCustomerName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetCKListForFJHCKWithFilter", "com.cn.bean.report.", "CKListForFJHCK",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    CKListForFJHCK data = (CKListForFJHCK) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    data.setPartName(baseInfo.getPartName());
                                    data.setPartID(baseInfo.getPartID());

                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());

                                    customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getFzDCustomerID()), Customer.class);
                                    data.setFzDCustomerName(customer.getCustomerAbbName());
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
                            json = reportOperate(dataType, operateType, "spGetCKListForJPSX", "CKListForJPSX", proParams, new ReportItemOperateAdapter() {
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
                            json = reportOperateWithFilter(dataType, operateType, "spGetCKListForJPSX", "CKListForJPSX", proParams, new ReportInterface.FilterListItemOperate() {
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
                            Class classPath = Class.forName("com.cn.bean.report.THListForBPTH");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string," + paramsJson.getString("partStatus"));
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetTHListForBPTHWithFilter", "THListForBPTH", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.THListForBPTH");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string," + paramsJson.getString("partStatus"));
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetTHListForBPTHWithFilter", "THListForBPTH", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="退货出库报表_spGetTHListForBPTH">
                case "良品退货出库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.THListForBPTH");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetTHListForBPTHWithFilter", "THListForBPTH", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.THListForBPTH");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string,不良品");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetTHListForBPTHWithFilter", "THListForBPTH", proParams, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="退货出库报表_spGetTHListForBPTH">
                case "不良品退货出库": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.report.THListForBPTH");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string," + paramsJson.getString("partStatus"));
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetTHListForBPTHWithFilter", "THListForBPTH", proParams, new ReportItemOperateAdapter() {
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
                            Class classPath = Class.forName("com.cn.bean.report.THListForBPTH");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
                            }
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            proParams.put("PartState", "string," + paramsJson.getString("partStatus"));
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }
                            json = reportOperate(dataType, operateType, "spGetTHListForBPTHWithFilter", "THListForBPTH", proParams, new ReportItemOperateAdapter() {
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
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFJCListForLpWithFilter", "com.cn.bean.report.", "KFJCListForLp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth, new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFJCListForLpWithFilter", "com.cn.bean.report.", "KFJCListForLp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth, new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFJCListForLpWithFilter", "com.cn.bean.report.", "KFJCListForLp",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth, new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="不良品库存报表_spGetKFJCListForBLp">
                case "不良品库存报表": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFJCListForBLpWithFilter", "com.cn.bean.report.", "KFJCListForBLp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFJCListForBLpWithFilter", "com.cn.bean.report.", "KFJCListForBLp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFJCListForBLpWithFilter", "com.cn.bean.report.", "KFJCListForBLp",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="待检库存报表_spGetKFJCListForDjp">
                case "待检库存报表": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFJCListForDjpWithFilter", "com.cn.bean.report.", "KFJCListForDjp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFJCListForDjpWithFilter", "com.cn.bean.report.", "KFJCListForDjp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFJCListForDjpWithFilter", "com.cn.bean.report.", "KFJCListForDjp",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检品库存报表_spGetKFJCListForSjp">
                case "送检品库存报表": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFJCListForSjpWithFilter", "com.cn.bean.report.", "KFJCListForSjp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFJCListForSjpWithFilter", "com.cn.bean.report.", "KFJCListForSjp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFJCListForSjpWithFilter", "com.cn.bean.report.", "KFJCListForSjp",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修品库存报表_spGetKFJCListForFxp">
                case "返修品库存报表": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFJCListForFxpWithFilter", "com.cn.bean.report.", "KFJCListForFxp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFJCListForFxpWithFilter", "com.cn.bean.report.", "KFJCListForFxp",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFJCListForFxpWithFilter", "com.cn.bean.report.", "KFJCListForFxp",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFQCFenLuDataWithFilter", "com.cn.bean.report.", "KFQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFQCFenLuDataWithFilter", "com.cn.bean.report.", "KFQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFQCFenLuDataWithFilter", "com.cn.bean.report.", "KFQCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品退库分录_spGetTKFenLuData">
                case "部品退库分录": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetTKFenLuDataWithFilter", "com.cn.bean.report.", "TKFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetTKFenLuDataWithFilter", "com.cn.bean.report.", "TKFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetTKFenLuDataWithFilter", "com.cn.bean.report.", "TKFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品退货分录_spGetTHFenLuData">
                case "部品退货分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetTHFenLuDataWithFilter", "com.cn.bean.report.", "THFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetTHFenLuDataWithFilter", "com.cn.bean.report.", "THFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetTHFenLuDataWithFilter", "com.cn.bean.report.", "THFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品出库分录_spGetCKFenLuData">
                case "部品出库分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetCKFenLuDataWithFilter", "com.cn.bean.report.", "CKFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetCKFenLuDataWithFilter", "com.cn.bean.report.", "CKFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetCKFenLuDataWithFilter", "com.cn.bean.report.", "CKFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品入库分录_spGetRKFenLuData">
                case "部品入库分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetRKFenLuDataWithFilter", "com.cn.bean.report.", "RKFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetRKFenLuDataWithFilter", "com.cn.bean.report.", "RKFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetRKFenLuDataWithFilter", "com.cn.bean.report.", "RKFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="库房结存分录_spGetKFJCFenLuData">
                case "库房结存分录": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFJCFenLuDataWithFilter", "com.cn.bean.report.", "KFJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFJCFenLuDataWithFilter", "com.cn.bean.report.", "KFJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFJCFenLuDataWithFilter", "com.cn.bean.report.", "KFJCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场期初分录_spGetXCQCFenLuData">
                case "现场期初分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXCQCFenLuDataWithFilter", "com.cn.bean.report.", "XCQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXCQCFenLuDataWithFilter", "com.cn.bean.report.", "XCQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXCQCFenLuDataWithFilter", "com.cn.bean.report.", "XCQCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="集配区期初分录_spGetXC_JPQQCFenLuData">
                case "集配区期初分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXC_JPQQCFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_JPQQCFenLuData data = (XC_JPQQCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXC_JPQQCFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_JPQQCFenLuData data = (XC_JPQQCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXC_JPQQCFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQQCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XC_JPQQCFenLuData data = (XC_JPQQCFenLuData) obj;
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

                //<editor-fold desc="线旁期初分录_spGetXC_XPQCFenLuData">
                case "线旁期初分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXC_XPQCFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_XPQCFenLuData data = (XC_XPQCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXC_XPQCFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPQCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_XPQCFenLuData data = (XC_XPQCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXC_XPQCFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPQCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XC_XPQCFenLuData data = (XC_XPQCFenLuData) obj;
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

                //<editor-fold desc="现场结存分录_spGetXCJCFenLuData">
                case "现场结存分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXCJCFenLuDataWithFilter", "com.cn.bean.report.", "XCJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXCJCFenLuDataWithFilter", "com.cn.bean.report.", "XCJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXCJCFenLuDataWithFilter", "com.cn.bean.report.", "XCJCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="集配区结存分录_spGetXC_JPQJCFenLuData">
                case "集配区结存分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXC_JPQJCFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_JPQJCFenLuData data = (XC_JPQJCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXC_JPQJCFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_JPQJCFenLuData data = (XC_JPQJCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXC_JPQJCFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQJCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XC_JPQJCFenLuData data = (XC_JPQJCFenLuData) obj;
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

                //<editor-fold desc="线旁结存分录_spGetXC_XPJCFenLuData">
                case "线旁结存分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXC_XPJCFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_XPJCFenLuData data = (XC_XPJCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXC_XPJCFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPJCFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_XPJCFenLuData data = (XC_XPJCFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXC_XPJCFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPJCFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XC_XPJCFenLuData data = (XC_XPJCFenLuData) obj;
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

                //<editor-fold desc="库房调账分录_spGetKFTZFenLuData">
                case "库房调账分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFTZFenLuDataWithFilter", "com.cn.bean.report.", "KFTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFTZFenLuDataWithFilter", "com.cn.bean.report.", "KFTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFTZFenLuDataWithFilter", "com.cn.bean.report.", "KFTZFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场调账分录_spGetXCTZFenLuData">
                case "现场调账分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXCTZFenLuDataWithFilter", "com.cn.bean.report.", "XCTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXCTZFenLuDataWithFilter", "com.cn.bean.report.", "XCTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                        new ReportItemOperateAdapter() {
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
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXCTZFenLuDataWithFilter", "com.cn.bean.report.", "XCTZFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, jzyMonth,
                                    new ReportItemOperateAdapter() {
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="集配区调账分录_spGetXC_JPQTZFenLuData">
                case "集配区调账分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXC_JPQTZFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_JPQTZFenLuData data = (XC_JPQTZFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXC_JPQTZFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_JPQTZFenLuData data = (XC_JPQTZFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXC_JPQTZFenLuDataWithFilter", "com.cn.bean.report.", "XC_JPQTZFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XC_JPQTZFenLuData data = (XC_JPQTZFenLuData) obj;
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

                //<editor-fold desc="线旁调账分录_spGetXC_XPTZFenLuData">
                case "线旁调账分录": {
                    switch (operation) {
                        case "create": {

                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetXC_XPTZFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_XPTZFenLuData data = (XC_XPTZFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetXC_XPTZFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPTZFenLuData",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        XC_XPTZFenLuData data = (XC_XPTZFenLuData) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetXC_XPTZFenLuDataWithFilter", "com.cn.bean.report.", "XC_XPTZFenLuData",
                                    "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    XC_XPTZFenLuData data = (XC_XPTZFenLuData) obj;
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
                //</editor-fold>

                //<editor-fold desc="期初明细报表">
                //<editor-fold desc="待检品期初报表">
                case "待检品期初报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFQCDjpListWithFilter", "com.cn.bean.report.", "KFQCDjpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCDjpList data = (KFQCDjpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFQCDjpListWithFilter", "com.cn.bean.report.", "KFQCBLpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCDjpList data = (KFQCDjpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFQCDjpListWithFilter", "com.cn.bean.report.", "KFQCBLpList",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFQCDjpList data = (KFQCDjpList) obj;
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

                //<editor-fold desc="送检品期初报表">
                case "送检品期初报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFQCSjpListWithFilter", "com.cn.bean.report.", "KFQCSjpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCSjpList data = (KFQCSjpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFQCSjpListWithFilter", "com.cn.bean.report.", "KFQCSjpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCSjpList data = (KFQCSjpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFQCSjpListWithFilter", "com.cn.bean.report.", "KFQCSjpList",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFQCSjpList data = (KFQCSjpList) obj;
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

                //<editor-fold desc="良品期初报表">
                case "良品期初报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFQCLpListWithFilter", "com.cn.bean.report.", "KFQCLpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCLpList data = (KFQCLpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFQCLpListWithFilter", "com.cn.bean.report.", "KFQCLpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCLpList data = (KFQCLpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFQCLpListWithFilter", "com.cn.bean.report.", "KFQCLpList",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFQCLpList data = (KFQCLpList) obj;
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

                //<editor-fold desc="不良品期初报表">
                case "不良品期初报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFQCBLpListWithFilter", "com.cn.bean.report.", "KFQCBLpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCBLpList data = (KFQCBLpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFQCBLpListWithFilter", "com.cn.bean.report.", "KFQCBLpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCBLpList data = (KFQCBLpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFQCBLpListWithFilter", "com.cn.bean.report.", "KFQCBLpList",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFQCBLpList data = (KFQCBLpList) obj;
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

                //<editor-fold desc="返修品期初报表">
                case "返修品期初报表": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "spGetKFQCFXpListWithFilter", "com.cn.bean.report.", "KFQCFXpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCFXpList data = (KFQCFXpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "spGetKFQCFXpListWithFilter", "com.cn.bean.report.", "KFQCFXpList",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        KFQCFXpList data = (KFQCFXpList) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        data.setPartName(baseInfo.getPartName());
                                        data.setPartID(baseInfo.getPartID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetKFQCFXpListWithFilter", "com.cn.bean.report.", "KFQCFXpList",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    KFQCFXpList data = (KFQCFXpList) obj;
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
                //</editor-fold>
                
                //<editor-fold desc="盛具报表">
                case "盛具报表": {
                    switch (operation) {
                        case "create": {
                            /*
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(dataType, operateType, "tbGetContainerAmount", "ContainerAmount", proParams, new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    ContainerAmount data = (ContainerAmount) obj;
                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                    data.setSupplierName(customer.getCustomerAbbName());
                                }
                            });
                             */
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "tbGetContainerAmountWithFilter", "com.cn.bean.report.", "ContainerAmount",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        ContainerAmount data = (ContainerAmount) obj;
                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "tbGetContainerAmountWithFilter", "com.cn.bean.report.", "ContainerAmount",
                                        "SupplierID", datas, pageSize, pageIndex, start, end, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        ContainerAmount data = (ContainerAmount) obj;
                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            JSONObject proParams = new JSONObject();
                            JSONObject dataJson = JSONObject.parseObject(datas);
                            json = reportOperateWithFilter(dataType, operateType, "tbGetContainerAmount", "ContainerAmount", proParams, new ReportInterface.FilterListItemOperate() {
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

                //<editor-fold desc="盛具需求">
                case "盛具需求": {
                    switch (operation) {
                        case "create": {
                            json = reportOperateWithPage(dataType, operateType, "tbGetContainerDemandAndKCWithFilter", "com.cn.bean.report.", "ContainerDemand",
                                    "OutboundContainerName", datas, pageSize, pageIndex, start, end, null,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="盛具维修">
                case "盛具维修": {
                    switch (operation) {
                        case "create": {
                            if (loginType.compareTo("customerLogin") == 0) {
                                json = reportOperateWithPageForSupplier(dataType, userName, operateType, "tbGetContainerFXAlarmWithFilter", "com.cn.bean.report.", "ContainerRepair",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        ContainerRepair data = (ContainerRepair) obj;

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            } else {
                                json = reportOperateWithPage(dataType, operateType, "tbGetContainerFXAlarmWithFilter", "com.cn.bean.report.", "ContainerRepair",
                                        "SupplierID", datas, pageSize, pageIndex, null, null, null,
                                        new ReportItemOperateAdapter() {
                                    @Override
                                    public void itemObjOperate(Object obj) {
                                        ContainerRepair data = (ContainerRepair) obj;

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                                        data.setSupplierName(customer.getCustomerAbbName());
                                    }
                                });
                            }
                            break;
                        }
                        case "request_detail": {
                            json = reportOperateDetailWithPage(dataType, operateType, "spGetFXAlarmWithFilter", "com.cn.bean.report.", "ContainerRepair",
                                    "SupplierID", datas, pageSize, pageIndex, null, null, jzyMonth,
                                    new ReportItemOperateAdapter() {
                                @Override
                                public void itemObjOperate(Object obj) {
                                    ContainerRepair data = (ContainerRepair) obj;

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

                //<editor-fold desc="报检信息查询">
                case "报检信息查询": {
                    String whereCase1 = "DJInWareHouseID in (select DJInWareHouseID from tblDJInWareHouse where DJRKAuditTime is not null)";
                    String whereCase = "(InspectionTime is not null) and " + whereCase1;
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate("view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList",
                                    datas, rely, whereCase, "DJInWareHouseID", pageIndex, pageSize, DatabaseOpt.DATA);
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

    class ReportItemOperateAdapter implements ReportItemOperate {

        @Override
        public void itemFilter(List<Object> filterList, Object obj) {
            filterList.add(obj);
        }

        @Override
        public void itemObjOperate(Object obj) {
        }
    }

    interface ReportItemOperate {

        void itemObjOperate(Object obj);

        void itemFilter(List<Object> filterList, Object obj);
    }

    interface FilterListItemOperate {

        void itemFilter(List<Object> filterList, Object obj);
    }

    private String createOperateOnDate(String type, String jsonPackagePath, String beanPackage, String tableName, String datas,
            String rely, String whereCase, String orderField, int pageIndex, int pageSize, String conn) throws Exception {
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
            List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", whereSql, pageSize, pageIndex, orderField, 0, conn);
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

    private String reportOperateWithPage(String dataType, String operateType, String proceduceName, String packageName, String className, String orderField,
            String datas, int pageSize, int pageIndex, String start, String end, String jzyMonth, ReportItemOperate itemOperate) throws Exception {
        String json = null;
        JSONObject proParams = new JSONObject();
        CommonController commonController = new CommonController();
        Class classPath = Class.forName(packageName + className);
        proParams.put("fields", "string,*");
        proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
        proParams.put("pageSize", "int," + pageSize);
        proParams.put("pageIndex", "int," + pageIndex);
        proParams.put("orderField", "string," + orderField);
        proParams.put("orderFlag", "int,0");
        if (!Units.strIsEmpty(end)) {
            proParams.put("Endtime", "string," + end);
        }
        if (!Units.strIsEmpty(start)) {
            proParams.put("BeginTime", "string," + start);
        }
        if (!Units.strIsEmpty(jzyMonth)) {
            proParams.put("LastJZYMonth", "string," + jzyMonth);
        }
        json = reportOperate(dataType, operateType, proceduceName, className, proParams, itemOperate);
        return json;
    }

    private String reportOperateDetailWithPage(String dataType, String operateType, String proceduceName, String packageName, String className, String orderField,
            String datas, int pageSize, int pageIndex, String start, String end, String jzyMonth, ReportItemOperate itemOperate) throws Exception {
        String json = null;
        JSONObject proParams = new JSONObject();
        CommonController commonController = new CommonController();
        Class classPath = Class.forName(packageName + className);
        proParams.put("fields", "string,*");
        proParams.put("wherecase", "string," + commonController.getWhereSQLStrWithObject(classPath, JSONObject.parseObject(datas)));
        proParams.put("pageSize", "int," + pageSize);
        proParams.put("pageIndex", "int," + pageIndex);
        proParams.put("orderField", "string," + orderField);
        proParams.put("orderFlag", "int,0");
        if (!Units.strIsEmpty(end)) {
            proParams.put("Endtime", "string," + end);
        }
        if (!Units.strIsEmpty(start)) {
            proParams.put("BeginTime", "string," + start);
        }
        if (!Units.strIsEmpty(jzyMonth)) {
            proParams.put("LastJZYMonth", "string," + jzyMonth);
        }
        json = reportOperate(dataType, operateType, proceduceName, className, proParams, itemOperate);
        return json;
    }

    private String reportOperateWithPageForSupplier(String dataType, String supplierID, String operateType, String proceduceName, String packageName, String className, String orderField,
            String datas, int pageSize, int pageIndex, String start, String end, String jzyMonth, ReportItemOperate itemOperate) throws Exception {
        String json = null;
        JSONObject proParams = new JSONObject();
        CommonController commonController = new CommonController();
        Class classPath = Class.forName(packageName + className);
        proParams.put("fields", "string,*");
        proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + supplierID + "'");
        proParams.put("pageSize", "int," + pageSize);
        proParams.put("pageIndex", "int," + pageIndex);
        proParams.put("orderField", "string," + orderField);
        proParams.put("orderFlag", "int,0");
        if (!Units.strIsEmpty(end)) {
            proParams.put("Endtime", "string," + end);
        }
        if (!Units.strIsEmpty(start)) {
            proParams.put("BeginTime", "string," + start);
        }
        if (!Units.strIsEmpty(jzyMonth)) {
            proParams.put("LastJZYMonth", "string," + jzyMonth);
        }
        json = reportOperate(dataType, operateType, proceduceName, className, proParams, itemOperate);
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
    private String reportOperate(String dataType, String operateType, String proceduceName, String className, JSONObject proParams, ReportItemOperate itemOperate) throws Exception {
        String result = null;
        String json = null;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        String conn = DatabaseOpt.DATA;
        if (dataType.compareToIgnoreCase("isHis") == 0) {
            conn = DatabaseOpt.HIS;
        }
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        Class objClass = Class.forName("com.cn.bean.report." + className);
        //Method method = objClass.getMethod("getRecordCount", null);

        if (operateType.compareTo("create") == 0) {
            result = Units.returnFileContext(path + "com/cn/json/report/", className + ".json");
        }
        if (operateType.compareTo("search") == 0) {
            result = "{}";
        }

        List<Object> list = commonController.proceduceQuery(proceduceName, proParams, "com.cn.bean.report." + className, conn);
        List<Object> filterList = new ArrayList<>();
        if (list != null && list.size() > 0) {

            for (Object obj : list) {
                itemOperate.itemFilter(filterList, obj);
            }

            for (Object obj : filterList) {
                itemOperate.itemObjOperate(obj);
            }
            if (operateType.compareTo("export") == 0) {
                json = exportData("com.cn.bean.report.", className, (ArrayList<Object>) filterList);
            } else if (operateType.compareTo("create") == 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), ",\"datas\":" + JSONObject.toJSONString(filterList, Units.features));
                //buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, null));
                result = buffer.toString();
                json = Units.objectToJson(0, "", result);
            } else if (operateType.compareTo("search") == 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(filterList, Units.features));
                //buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, null));
                result = buffer.toString();
                json = Units.objectToJson(0, "", result);
            }
        } else {
            json = Units.objectToJson(0, "数据为空!", result);
        }
        return json;
    }

    /**
     * 带筛选条件的报表操作
     *
     * @param operateType
     * @param proceduceName
     * @param className
     * @param proParams
     * @param itemOperate
     * @return
     * @throws Exception
     */
    private String reportOperateWithFilter(String dataType, String operateType, String proceduceName, String className, JSONObject proParams, FilterListItemOperate itemOperate) throws Exception {
        String result = null;
        String json = null;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        String conn = DatabaseOpt.DATA;
        if (dataType.compareToIgnoreCase("isHis") == 0) {
            conn = DatabaseOpt.HIS;
        }
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        if (operateType.compareTo("create") == 0) {
            result = Units.returnFileContext(path + "com/cn/json/report/", className + ".json");
        }
        if (operateType.compareTo("search") == 0) {
            result = "{}";
        }

        List<Object> list = commonController.proceduceQuery(proceduceName, proParams, "com.cn.bean.report." + className, conn);
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
