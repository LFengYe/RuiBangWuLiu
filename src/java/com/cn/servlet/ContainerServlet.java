/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Container;
import com.cn.bean.Customer;
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
import com.cn.controller.InterfaceController;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ContainerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ContainerServlet.class);

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
        InterfaceController interfaceController = new  InterfaceController();
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
            String filePath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
            String servletPath = getServletContext().getContextPath();

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
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConInWareHouse", datas, rely, "", "CONInWareHouseID", dataType);
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
                            //json = interfaceController.queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConInWareHouse", "CONInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConInWareHouse", "CONInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conRKAuditStaffName", session.getAttribute("user"));
                                obj.put("conRKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConInWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
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
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConInWareHouse", "update", DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName"};
                                String[] keysName = {"盛具名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"containerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                        case "interfaceController.exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.container.", "ConInWareHouseList", null);
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
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConFXOutWareHouse", datas, rely, "", "conFXOutWareHouseID", dataType);
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
                            //json = interfaceController.queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConFXOutWareHouse", "conFXOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConFXOutWareHouse", "conFXOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conFXCKAuditStaffName", session.getAttribute("user"));
                                obj.put("conFXCKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
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
                                        List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", DatabaseOpt.DATA);
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
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXOutWareHouse", "update", DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName", "operateMaxAmount"};
                                String[] keysName = {"盛具名称", "入库数量"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"containerName", "containerTotal"};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        filterList.add(obj);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.container.ContainerAmount", keys, keysName, keysWidth, fieldsName, target, rely);
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
                        case "interfaceController.exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.container.", "ConFXOutWareHouseList", null);
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
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConFXInWareHouse", datas, rely, "", "conFXInWareHouseID", dataType);
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
                            //json = interfaceController.queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConFXInWareHouse", "conFXInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConFXInWareHouse", "conFXInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conFXRKAuditStaffName", session.getAttribute("user"));
                                obj.put("conFXRKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXInWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
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
                                        List<Object> list = commonController.proceduceQuery("tbGetContainerFXAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", DatabaseOpt.DATA);
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
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConFXInWareHouse", "update", DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName", "operateMaxAmount", "fxOutBatch"};
                                String[] keysName = {"盛具名称", "入库数量", "返修出批次"};
                                int[] keysWidth = {30, 30, 40};
                                String[] fieldsName = {"containerName", "containerFX", "fxOutBatch"};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                List<Object> list = commonController.proceduceQuery("tbGetContainerFXAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        filterList.add(obj);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.container.ContainerAmount", keys, keysName, keysWidth, fieldsName, target, rely);
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
                        case "interfaceController.exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.container.", "ConFXInWareHouseList", null);
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
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/container/", "com.cn.bean.container.", "ConOutWareHouse", datas, rely, "", "conOutWareHouseID", dataType);
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
                            //json = interfaceController.queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConOutWareHouse", "conOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.container.", "view", "ConOutWareHouse", "conOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_detail": {
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("conCKAuditStaffName", session.getAttribute("user"));
                                obj.put("conCKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
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
                                        List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", DatabaseOpt.DATA);
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
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.container.", "ConOutWareHouse", "update", DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("containerName") == 0) {
                                String[] keys = {"containerName", "operateMaxAmount"};
                                String[] keysName = {"盛具名称", "入库数量"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"containerName", "containerTotal"};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ContainerStatus", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("containerStatus"));
                                List<Object> list = commonController.proceduceQuery("tbGetContainerKCAmountListForSupplier", proParams, "com.cn.bean.container.ContainerAmount", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        filterList.add(obj);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.container.ContainerAmount", keys, keysName, keysWidth, fieldsName, target, rely);
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
                        case "interfaceController.exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.container.", "ConOutWareHouseList", null);
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
