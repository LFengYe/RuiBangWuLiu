/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.ClassDescription;
import com.cn.bean.Container;
import com.cn.bean.Customer;
import com.cn.bean.FieldDescription;
import com.cn.bean.container.ConFXInWareHouseList;
import com.cn.bean.container.ConFXOutWareHouseList;
import com.cn.bean.container.ConInWareHouse;
import com.cn.bean.container.ConInWareHouseList;
import com.cn.bean.container.ConOutWareHouseList;
import com.cn.bean.container.ContainerAmount;
import com.cn.controller.CommonController;
import com.cn.controller.CommonOperate;
import com.cn.controller.DetailOperateInterface;
import com.cn.controller.ImportOperateInterface;
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
public class ContainerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(BaseInterface.class);

//    private CommonController commonController;
//    private DatabaseOpt opt;
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

        try {
            logger.info(subUri + ",params:" + params);
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
            String operateType = paramsJson.getString("type") == null ? ("") : paramsJson.getString("type");
            String dataType = (paramsJson.getString("dataType") == null) ? ("isCur") : paramsJson.getString("dataType");// isCur表示当期查询, isHis表示往期查询
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

            switch (module) {
                /**
                 * ***************************************盛具信息管理**************************************
                 */
                //<editor-fold desc="盛具信息管理">
                //<editor-fold desc="盛具入库">
                case "盛具入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConInWareHouse", datas, rely, "", "CONInWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"盛具入库单号\\", ",@CONRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/container/", "ConInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"盛具入库单号\\", ",@CONRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConInWareHouse", "CONInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConInWareHouse", "CONInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conRKAuditStaffName", session.getAttribute("user"));
                                obj.put("conRKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConInWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (opt.getConnectHis()) : (opt.getConnect()));
                            }
                            CommonOperate operate = new CommonOperate();
                            json = operate.getDetailOperate("com.cn.bean.container.", "ConInWareHouse", datas, rely,
                                    "conInWareHouseID", "conRKAuditTime", "wareHouseManagerName", (operateType.compareTo("app") == 0),
                                    dataType, pageSize, pageIndex,
                                    new DetailOperateInterface() {
                                @Override
                                public HashMap<String, String> getLimitMap() {
                                    return null;
                                }

                                @Override
                                public void setLimit(HashMap<String, String> limitMap, Object obj) {
                                }
                            });
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("conRKAuditStaffName", session.getAttribute("user"));
                            obj.put("conRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConInWareHouse", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            CommonOperate operate = new CommonOperate();
                            json = operate.auditItemOperate("com.cn.bean.container.", "ConInWareHouse", datas,
                                    "conInWareHouseID", "conRKAuditTime", "wareHouseManagerName", (String) session.getAttribute("user"));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName"};
                                String[] keysName = {"盛具名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"containerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "import": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.container.", "ConInWareHouseList", importPath + fileName);
                            ConInWareHouse conInWareHouse = JSONObject.parseObject(item, ConInWareHouse.class);
                            if (importData != null && importData.size() > 0) {
                                CommonOperate operate = new CommonOperate();
                                operate.importOperate("com.cn.bean.container.", "ConInWareHouse", item, "conInWareHouseID",
                                        importData, new ImportOperateInterface() {
                                    @Override
                                    public boolean checkData(Object obj) {
                                        ConInWareHouseList list = (ConInWareHouseList) obj;
                                        list.setSupplierID(conInWareHouse.getSupplierID());
                                        list.setConInWareHouseID(conInWareHouse.getConInWareHouseID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + list.getSupplierID()), Customer.class);
                                        if (customer != null) {
                                            list.setSupplierName(customer.getCustomerAbbName());
                                        } else {
                                            list.setFailedReason("缺失供应商信息!");
                                            return false;
                                        }
                                        Container container = JSONObject.parseObject(RedisAPI.get("container_" + list.getContainerName()), Container.class);
                                        if (container != null) {

                                        } else {
                                            list.setFailedReason("缺失盛具信息!");
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void test() {

                                    }
                                });
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.container.", "ConInWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            CommonOperate commonOperate = new CommonOperate();
                            if (operate.compareToIgnoreCase("add") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConInWareHouse", item, "conInWareHouseID", details);
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConInWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.container.", "ConInWareHouse", "ConInWareHouseID", "conRKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修出库">
                case "返修出库": {
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConFXOutWareHouse", datas, rely, "", "conFXOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"返修出库单号\\", ",@CONFX-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"返修出批次\\", ",@" + Units.getNowTimeNoSeparator());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/container/", "ConFXOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"返修出库单号\\", ",@CONFX-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"返修出批次\\", ",@" + Units.getNowTimeNoSeparator());
                            //json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConFXOutWareHouse", "conFXOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConFXOutWareHouse", "conFXOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conFXCKAuditStaffName", session.getAttribute("user"));
                                obj.put("conFXCKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (opt.getConnectHis()) : (opt.getConnect()));
                            }
                            CommonOperate operate = new CommonOperate();
                            json = operate.getDetailOperate("com.cn.bean.container.", "ConFXOutWareHouse", datas, rely,
                                    "conFXOutWareHouseID", "conFXCKAuditTime", "wareHouseManagerName", (operateType.compareTo("app") == 0),
                                    dataType, pageSize, pageIndex,
                                    new DetailOperateInterface() {
                                @Override
                                public HashMap<String, String> getLimitMap() {
                                    try {
                                        JSONObject proParams = new JSONObject();
                                        proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                        proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                        HashMap<String, String> limitMap = new HashMap<>();
                                        List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", opt.getConnect());
                                        if (list != null && list.size() > 0) {
                                            for (Object obj : list) {
                                                ContainerAmount amount = (ContainerAmount) obj;
                                                limitMap.put(amount.getContainerName(), String.valueOf(amount.getContainerTotal()));
                                            }
                                        }
                                        return limitMap;
                                    } catch (Exception e) {
                                    }
                                    return null;
                                }

                                @Override
                                public void setLimit(HashMap<String, String> limitMap, Object obj) {
                                    ConFXOutWareHouseList list = (ConFXOutWareHouseList) obj;
                                    int amount = Integer.valueOf((null == limitMap.get(list.getContainerName())) ? "0" : limitMap.get(list.getContainerName())) + list.getFxCKAmount();
                                    list.setOperateMaxAmount(amount);
                                }
                            });
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("conFXCKAuditStaffName", session.getAttribute("user"));
                            obj.put("conFXCKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXOutWareHouse", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            CommonOperate operate = new CommonOperate();
                            json = operate.auditItemOperate("com.cn.bean.container.", "ConFXOutWareHouse", datas,
                                    "conFXOutWareHouseID", "conFXCKAuditTime", "wareHouseManagerName", (String) session.getAttribute("user"));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName", "operateMaxAmount"};
                                String[] keysName = {"盛具名称", "入库数量"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"containerName", "containerTotal"};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        filterList.add(obj);
                                    }
                                    json = getSpecialTableJsonStr(filterList, "com.cn.bean.container.ContainerAmount", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "import": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.container.", "ConFXOutWareHouseList", importPath + fileName);
                            ConInWareHouse conInWareHouse = JSONObject.parseObject(item, ConInWareHouse.class);
                            if (importData != null && importData.size() > 0) {
                                CommonOperate operate = new CommonOperate();
                                operate.importOperate("com.cn.bean.container.", "ConFXOutWareHouse", item, "conFXOutWareHouseID",
                                        importData, new ImportOperateInterface() {
                                    @Override
                                    public boolean checkData(Object obj) {
                                        ConInWareHouseList list = (ConInWareHouseList) obj;
                                        list.setSupplierID(conInWareHouse.getSupplierID());
                                        list.setConInWareHouseID(conInWareHouse.getConInWareHouseID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + list.getSupplierID()), Customer.class);
                                        if (customer != null) {
                                            list.setSupplierName(customer.getCustomerAbbName());
                                        } else {
                                            list.setFailedReason("缺失供应商信息!");
                                            return false;
                                        }
                                        Container container = JSONObject.parseObject(RedisAPI.get("container_" + list.getContainerName()), Container.class);
                                        if (container != null) {

                                        } else {
                                            list.setFailedReason("缺失盛具信息!");
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void test() {

                                    }
                                });
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.container.", "ConFXOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            CommonOperate commonOperate = new CommonOperate();
                            if (operate.compareToIgnoreCase("add") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConFXOutWareHouse", item, "conFXOutWareHouseID", details);
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConFXOutWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.container.", "ConFXOutWareHouse", "conFXOutWareHouseID", "conFXCKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="返修入库">
                case "返修入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConFXInWareHouse", datas, rely, "", "conFXInWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"返修入库单号\\", ",@CONFX-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/container/", "ConFXInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"返修入库单号\\", ",@CONFX-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            //json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConFXInWareHouse", "conFXInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConFXInWareHouse", "conFXInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conFXRKAuditStaffName", session.getAttribute("user"));
                                obj.put("conFXRKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXInWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (opt.getConnectHis()) : (opt.getConnect()));
                            }
                            CommonOperate operate = new CommonOperate();
                            json = operate.getDetailOperate("com.cn.bean.container.", "ConFXInWareHouse", datas, rely,
                                    "conFXInWareHouseID", "conFXRKAuditTime", "wareHouseManagerName", (operateType.compareTo("app") == 0),
                                    dataType, pageSize, pageIndex,
                                    new DetailOperateInterface() {
                                @Override
                                public HashMap<String, String> getLimitMap() {
                                    try {
                                        JSONObject proParams = new JSONObject();
                                        proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                        proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                        HashMap<String, String> limitMap = new HashMap<String, String>();
                                        List<Object> list = commonController.proceduceQuery("tbGetContainerFXAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", opt.getConnect());
                                        if (list != null && list.size() > 0) {
                                            for (Object obj : list) {
                                                ContainerAmount amount = (ContainerAmount) obj;
                                                limitMap.put(amount.getContainerName(), String.valueOf(amount.getContainerFX()));
                                            }
                                        }
                                        return limitMap;
                                    } catch (Exception e) {
                                    }
                                    return null;
                                }

                                @Override
                                public void setLimit(HashMap<String, String> limitMap, Object obj) {
                                    ConFXInWareHouseList list = (ConFXInWareHouseList) obj;
                                    int amount = Integer.valueOf((null == limitMap.get(list.getContainerName())) ? "0" : limitMap.get(list.getContainerName())) + list.getFxRKAmount();
                                    list.setOperateMaxAmount(amount);
                                }
                            });
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("conFXRKAuditStaffName", session.getAttribute("user"));
                            obj.put("conFXRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXInWareHouse", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            CommonOperate operate = new CommonOperate();
                            json = operate.auditItemOperate("com.cn.bean.container.", "ConFXInWareHouse", datas,
                                    "conFXInWareHouseID", "conFXRKAuditTime", "wareHouseManagerName", (String) session.getAttribute("user"));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName", "operateMaxAmount", "fxOutBatch"};
                                String[] keysName = {"盛具名称", "入库数量", "返修出批次"};
                                int[] keysWidth = {30, 30, 40};
                                String[] fieldsName = {"containerName", "containerFX", "fxOutBatch"};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                List<Object> list = commonController.proceduceQuery("tbGetContainerFXAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        filterList.add(obj);
                                    }
                                    json = getSpecialTableJsonStr(filterList, "com.cn.bean.container.ContainerAmount", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "import": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.container.", "ConFXInWareHouseList", importPath + fileName);
                            ConInWareHouse conInWareHouse = JSONObject.parseObject(item, ConInWareHouse.class);
                            if (importData != null && importData.size() > 0) {
                                CommonOperate operate = new CommonOperate();
                                operate.importOperate("com.cn.bean.container.", "ConFXInWareHouse", item, "conFXInWareHouseID",
                                        importData, new ImportOperateInterface() {
                                    @Override
                                    public boolean checkData(Object obj) {
                                        ConInWareHouseList list = (ConInWareHouseList) obj;
                                        list.setSupplierID(conInWareHouse.getSupplierID());
                                        list.setConInWareHouseID(conInWareHouse.getConInWareHouseID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + list.getSupplierID()), Customer.class);
                                        if (customer != null) {
                                            list.setSupplierName(customer.getCustomerAbbName());
                                        } else {
                                            list.setFailedReason("缺失供应商信息!");
                                            return false;
                                        }
                                        Container container = JSONObject.parseObject(RedisAPI.get("container_" + list.getContainerName()), Container.class);
                                        if (container != null) {

                                        } else {
                                            list.setFailedReason("缺失盛具信息!");
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void test() {

                                    }
                                });
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.container.", "ConFXInWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            CommonOperate commonOperate = new CommonOperate();
                            if (operate.compareToIgnoreCase("add") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConFXInWareHouse", item, "conFXInWareHouseID", details);
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConFXInWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.container.", "ConFXInWareHouse", "conFXInWareHouseID", "conFXRKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="盛具出库">
                case "盛具出库": {
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConOutWareHouse", datas, rely, "", "conOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"出库单号\\", ",@CONCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/container/", "ConOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"出库单号\\", ",@CONCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            //json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConOutWareHouse", "conOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.container.", "view", "ConOutWareHouse", "conOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conCKAuditStaffName", session.getAttribute("user"));
                                obj.put("conCKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (opt.getConnectHis()) : (opt.getConnect()));
                            }
                            CommonOperate operate = new CommonOperate();
                            json = operate.getDetailOperate("com.cn.bean.container.", "ConOutWareHouse", datas, rely,
                                    "conOutWareHouseID", "conCKAuditTime", "wareHouseManagerName", (operateType.compareTo("app") == 0),
                                    dataType, pageSize, pageIndex,
                                    new DetailOperateInterface() {
                                @Override
                                public HashMap<String, String> getLimitMap() {
                                    try {
                                        JSONObject proParams = new JSONObject();
                                        proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                        proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                        HashMap<String, String> limitMap = new HashMap<String, String>();
                                        List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", opt.getConnect());
                                        if (list != null && list.size() > 0) {
                                            for (Object obj : list) {
                                                ContainerAmount amount = (ContainerAmount) obj;
                                                limitMap.put(amount.getContainerName(), String.valueOf(amount.getContainerTotal()));
                                            }
                                        }
                                        return limitMap;
                                    } catch (Exception e) {
                                    }
                                    return null;
                                }

                                @Override
                                public void setLimit(HashMap<String, String> limitMap, Object obj) {
                                    ConOutWareHouseList list = (ConOutWareHouseList) obj;
                                    int amount = Integer.valueOf((null == limitMap.get(list.getContainerName())) ? "0" : limitMap.get(list.getContainerName())) + list.getCkAmount();
                                    list.setOperateMaxAmount(amount);
                                }
                            });
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("conCKAuditStaffName", session.getAttribute("user"));
                            obj.put("conCKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConOutWareHouse", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            CommonOperate operate = new CommonOperate();
                            json = operate.auditItemOperate("com.cn.bean.container.", "ConOutWareHouse", datas,
                                    "conOutWareHouseID", "conCKAuditTime", "wareHouseManagerName", (String) session.getAttribute("user"));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName", "operateMaxAmount"};
                                String[] keysName = {"盛具名称", "入库数量"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"containerName", "containerTotal"};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        filterList.add(obj);
                                    }
                                    json = getSpecialTableJsonStr(filterList, "com.cn.bean.container.ContainerAmount", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "import": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.container.", "ConOutWareHouseList", importPath + fileName);
                            ConInWareHouse conInWareHouse = JSONObject.parseObject(item, ConInWareHouse.class);
                            if (importData != null && importData.size() > 0) {
                                CommonOperate operate = new CommonOperate();
                                operate.importOperate("com.cn.bean.container.", "ConOutWareHouse", item, "conOutWareHouseID",
                                        importData, new ImportOperateInterface() {
                                    @Override
                                    public boolean checkData(Object obj) {
                                        ConInWareHouseList list = (ConInWareHouseList) obj;
                                        list.setSupplierID(conInWareHouse.getSupplierID());
                                        list.setConInWareHouseID(conInWareHouse.getConInWareHouseID());

                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + list.getSupplierID()), Customer.class);
                                        if (customer != null) {
                                            list.setSupplierName(customer.getCustomerAbbName());
                                        } else {
                                            list.setFailedReason("缺失供应商信息!");
                                            return false;
                                        }
                                        Container container = JSONObject.parseObject(RedisAPI.get("container_" + list.getContainerName()), Container.class);
                                        if (container != null) {

                                        } else {
                                            list.setFailedReason("缺失盛具信息!");
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void test() {

                                    }
                                });
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.container.", "ConOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            CommonOperate commonOperate = new CommonOperate();
                            if (operate.compareToIgnoreCase("add") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConOutWareHouse", item, "conOutWareHouseID", details);
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = commonOperate.submitOperate("com.cn.bean.container.", "ConOutWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.container.", "ConOutWareHouse", "conOutWareHouseID", "conCKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
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
            String rely, String whereCase, String orderField, String dataType) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        Connection conn;
        if (dataType.compareToIgnoreCase("isHis") == 0) {
            conn = opt.getConnectHis();
        } else {
            conn = opt.getConnect();
        }

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
        CommonController commonController = new CommonController();
        String result = "{}";
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

    private String queryOnDateOperate(String beanPackage, String type, String tableName, String orderField, String keyWord, String rely, String whereCase,
            boolean isAll, String dataType, int pageSize, int pageIndex) throws Exception {
        String json;
        String result = "{}";
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        Connection conn;
        if (dataType.compareToIgnoreCase("isHis") == 0) {
            conn = opt.getConnectHis();
        } else {
            conn = opt.getConnect();
        }

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

            if (objClass.isAnnotationPresent(ClassDescription.class)) {
                ClassDescription description = (ClassDescription) objClass.getAnnotation(ClassDescription.class);
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

        if (objClass.isAnnotationPresent(ClassDescription.class)) {
            ClassDescription description = (ClassDescription) objClass.getAnnotation(ClassDescription.class);
            buffer.append(",\"module\":").append("\"").append(description.classDesc()).append("\"");
        }
        buffer.append("}");
        return Units.objectToJson(0, "", buffer.toString());
    }

    /**
     * 数据查询操作, 返回数据
     *
     * @param type
     * @param tableName
     * @param orderField
     * @param keyWord
     * @param conn
     * @param pageSize
     * @param pageIndex
     * @return
     * @throws Exception
     */
    private List<Object> queryData(String beanPackage, String type, String tableName, String orderField, String keyWord,
            Connection conn, int pageSize, int pageIndex) throws Exception {
        CommonController commonController = new CommonController();
        Class objClass = Class.forName(beanPackage + tableName);
        return commonController.dataBaseQuery(type, beanPackage, tableName, "*", commonController.getWhereSQLStr(objClass, keyWord, "{}", true), pageSize, pageIndex, orderField, 0, conn);
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

    /**
     * 导入Excel数据
     *
     * @param tableName
     * @param fileName
     * @param conn
     * @return
     * @throws Exception
     */
    private String importData(String beanPackage, String tableName, String fileName, Connection conn) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        //获取所有设置字段名称的字段
        Class objClass = Class.forName(beanPackage + tableName);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<Field> accessFields = new ArrayList<>();
        ArrayList<String> fieldDes = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class)) {
                FieldDescription description = field.getAnnotation(FieldDescription.class);
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
//        System.out.println("cells num:" + headerRow.getPhysicalNumberOfCells() + ",des size:" + fieldDes.size());
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != fieldDes.size()) {
            json = Units.objectToJson(-1, "上传数据格式不正确, 请先下载模板, 按照模板格式录入数据", null);
            return json;
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

        ArrayList<Integer> addResult = commonController.dataBaseOperate(JSONObject.toJSONString(result, Units.features), beanPackage, tableName, "add", conn);
//        System.out.println("import res:" + Arrays.toString(addResult.toArray()));
        if (addResult.get(0) == 0) {
            json = Units.objectToJson(0, "导入成功" + (addResult.size() - 1) + "条数据!", null);
        } else if (addResult.get(0) == 2) {
            json = Units.objectToJson(2, "导入数据为空!", null);
        } else {
            int successNum = 0, failedNum = 0;
            String failedMsg = "失败行数:";
            for (int i = 1; i < addResult.size(); i++) {
                int res = addResult.get(i);
                if (res == 1) {
                    successNum++;
                } else {
                    failedNum++;
                    failedMsg += (i + 1) + ",";
                }
            }
            json = Units.objectToJson(-1, "导入成功" + successNum + "条数据, 导入失败" + failedNum + "条数据!" + failedMsg, null);
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

    private String exportTemplate(String beanPackage, String tableName, ArrayList<Object> datas) throws Exception {
        Class objClass = Class.forName(beanPackage + tableName);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<String> fieldDes = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class)) {
                FieldDescription description = field.getAnnotation(FieldDescription.class);
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
