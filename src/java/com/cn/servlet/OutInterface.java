/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Customer;
import com.cn.bean.Employee;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.out.BPTHOutWareHouse;
import com.cn.bean.out.BPTHOutWareHouseList;
import com.cn.bean.out.FJHOutWareHouse;
import com.cn.bean.out.FJHOutWareHouseList;
import com.cn.bean.out.JHOutWareHouseList;
import com.cn.bean.out.JHOutWareHouseShort;
import com.cn.bean.out.LLJS;
import com.cn.bean.out.LPKCListInfo;
import com.cn.bean.out.XCJS;
import com.cn.bean.out.XCJSList;
import com.cn.bean.pro.KFJCBLPForBPTH;
import com.cn.bean.pro.KFJCDJPForBPTH;
import com.cn.bean.pro.KFJCFXPForBPTH;
import com.cn.bean.pro.KFJCLPForBPTH;
import com.cn.controller.CommonController;
import com.cn.controller.CommonOperate;
import com.cn.controller.FJHOutWareHouseController;
import com.cn.controller.InterfaceController;
import com.cn.controller.JHOutWareHouseController;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
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

/**
 *
 * @author LFeng
 */
public class OutInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OutInterface.class);

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
        InterfaceController interfaceController = new  InterfaceController();
        DatabaseOpt opt = new DatabaseOpt();
        //logger.info(Units.getIpAddress(request) + "accept:" + subUri + ",time:" + (new Date().getTime()));

        try {
            logger.info(subUri + ",params:" + params);
            //System.out.println(subUri + ",params:" + params);
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
            String dataType = (paramsJson.getString("dataType") == null) ? ("isCur") : paramsJson.getString("dataType");// isCur表示当期查询, isHis表示往期查询
            String start = paramsJson.getString("start");
            String end = paramsJson.getString("end");
            String patch = paramsJson.getString("patch");
            int isHistory = paramsJson.getIntValue("isHistory");
            int pageIndex = paramsJson.getIntValue("pageIndex");
            int pageSize = paramsJson.getIntValue("pageSize");

            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";
            //String exportPath = getServletContext().getContextPath() + "/exportFile/";
            String exportPath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
            String servletPath = getServletContext().getContextPath();

            //System.out.println("employee:" + JSONObject.toJSONString(session.getAttribute("employee")));
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

            Employee employee = null;
            Customer curCustomer = null;
            String loginType = session.getAttribute("loginType").toString();
            String userName = session.getAttribute("user").toString();
            if (session.getAttribute("loginType").toString().compareTo("employeeLogin") == 0) {
                employee = (Employee) session.getAttribute("employee");
            }
            if (session.getAttribute("loginType").toString().compareTo("customerLogin") == 0) {
                curCustomer = (Customer) session.getAttribute("employee");
            }

            switch (module) {
                /**
                 ****************************************部品出库管理**************************************
                 */
                //<editor-fold desc="部品出库管理">
                //<editor-fold desc="计划出库">
                case "计划出库": {
                    String whereCase = "JHType = '正常计划' and JHMethod = '明细计划'";
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@正常计划");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "JHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@正常计划");
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            json = interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCLpListForJHCKWithSupplier", proParams, "com.cn.bean.out.LPKCListInfo", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());

                                        filterList.add(sjck);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "importDetail": {
                            /**
                             * 导入的数据
                             */
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "JHOutWareHouseList", importPath + fileName);
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(importData, item);
                            //logger.info(JSONObject.toJSONString(result));
                            if (result != null && result.size() > 0) {
                                int addRes = controller.addJHData(item);
                                //logger.info("json:" + json);
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    if (addRes == 0) {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(0, "计划导入成功, 计划已保存!", object.toJSONString());
                                    } else {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(-1, "计划导入失败, 计划未保存!", object.toJSONString());
                                    }
                                } else {
                                    //当前库存不满足该计划
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<JHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        JHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
                                            houseShort.setJhCKAmount(list.getJhCKAmount());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    json = Units.objectToJson(-1, (addRes == 0) ? "计划导入失败, 计划已保存!" : "计划导入失败, 计划未保存!", object.toJSONString());
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "confirm": {
                            int result;
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<Integer> res = controller.jhPartitionPackage(JSONObject.parseObject(datas).getString("jhOutWareHouseID"));
                            if (res != null) {
                                result = res.get(0);
                            } else {
                                result = -1;
                            }
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                                JSONObject obj = new JSONObject();
                                obj.put("jhConfirm", "true");
                                commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", DatabaseOpt.DATA).get(0);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                            } else {
                                json = Units.objectToJson(-1, "计划分包失败!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "JHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            JSONObject object = JSONObject.parseObject(item);
                            List verifyList = commonController.dataBaseQuery("table", "com.cn.bean.out.", "JHOutWareHouse", "*", "JHOutWareHouseID = '" + object.getString("jhOutWareHouseID") + "'", 11, 1, "JHOutWareHouseID", 1, DatabaseOpt.DATA);
                            if (verifyList != null && verifyList.size() > 0) {
                                json = Units.objectToJson(-1, "计划已保存!", null);
                                break;
                            }
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, JHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(submitData, item);
                            if (result != null && result.size() > 0) {
                                int addRes = controller.addJHData(item);
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    if (addRes == 0) {
                                        JSONObject resObject = new JSONObject();
                                        resObject.put("datas", result);
                                        json = Units.objectToJson(0, "计划导入成功, 计划已保存!", resObject.toJSONString());
                                    } else {
                                        JSONObject resObject = new JSONObject();
                                        resObject.put("datas", result);
                                        json = Units.objectToJson(-1, "计划导入失败, 计划未保存!", resObject.toJSONString());
                                    }
                                } else {
                                    //当前库存不满足该计划
                                    //json = Units.objectToJson(-1, "计划添加失败!", JSONObject.toJSONString(result));
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<JHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        JHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
                                            houseShort.setJhCKAmount(list.getJhCKAmount());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject returnObject = new JSONObject();
                                    returnObject.put("datas", result);
                                    returnObject.put("fileUrl", filePath);
                                    //json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                    json = Units.objectToJson(-1, (addRes == 0) ? "计划导入失败, 计划已保存!" : "计划导入失败, 计划未保存!", returnObject.toJSONString());
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 计划已确认!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
                            }
                            break;
                        }
                        case "finish": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            if (!Units.strIsEmpty(delete) && employee.getEmployeeName().compareTo("管理员") == 0) {
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    object.put("jhOutWareHouseID", "string," + object.get("jhOutWareHouseID"));
                                }
                                //System.out.println(paramsArray.toJSONString());
                                ArrayList<Integer> delResult = commonController.proceduceForUpdate("tbFinishJH", paramsArray, DatabaseOpt.DATA);
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "操作成功!", null);
                                } else {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据确认失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中数据或没有权限进行该操作", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="总成计划">
                case "总成计划": {
                    String whereCase = "JHType = '正常计划' and JHMethod = '总成计划'";
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@正常计划");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "JHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@正常计划");
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            json = interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "importDetail": {
                            /**
                             * 导入的数据
                             */
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "JHOutWareHouseList", importPath + fileName);
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            /**
                             * 总成计划根据BOM分解出来的明细计划
                             */
                            ArrayList<Object> detailResult = controller.importZCData(importData, item);
                            /**
                             * 明细计划入库
                             */
                            ArrayList<JHOutWareHouseList> result = controller.importData(detailResult, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    JSONObject jhOutWareHouse = JSONObject.parseObject(item);
                                    jhOutWareHouse.put("jhMethod", "总成计划");
                                    int addRes = commonController.dataBaseOperate("[" + jhOutWareHouse.toJSONString() + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", DatabaseOpt.DATA).get(0);
                                    if (addRes == 0) {
//                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                        if (addRes == 0) {
                                            JSONObject object = new JSONObject();
                                            object.put("datas", result);
                                            json = Units.objectToJson(0, "计划添加成功!", object.toJSONString());
                                            //json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            if (!Units.strIsEmpty(Units.getSubJsonStr(item, "jhOutWareHouseID"))) {
                                                commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                            }
                                            json = Units.objectToJson(-1, "明细添加失败!", null);
                                        }
                                    } else {
                                        json = Units.objectToJson(-1, "计划添加失败!", null);
                                    }
                                } else {
                                    //当前库存不满足该计划
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<JHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        JHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
//                                            houseShort.setPartID(list.getPartID());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
//                                            houseShort.setInboundBatch(list.getInboundBatch());
                                            houseShort.setJhCKAmount(list.getJhCKAmount());
//                                            houseShort.setAutoStylingName(list.getAutoStylingName());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    //json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                    json = Units.objectToJson(-1, "计划导入失败!", object.toJSONString());
                                    //json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "confirm": {
                            int result;
                            JSONObject jhOutWareHouse = JSONObject.parseObject(datas);
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<Integer> res = controller.jhPartitionPackage(jhOutWareHouse.getString("jhOutWareHouseID"));
                            if (res != null) {
                                result = res.get(0);
                            } else {
                                result = -1;
                            }
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                                JSONObject obj = new JSONObject();
                                obj.put("jhConfirm", "true");
                                commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", DatabaseOpt.DATA).get(0);
                                /*总成计划写入分装入库与分装出库表
                                commonController.dataBaseOperate("[" + RedisAPI.get("ZCJH_" + jhOutWareHouse.getString("jhOutWareHouseID")) + "]", "com.cn.bean.out.", "FZInWareHouse", "add", DatabaseOpt.DATA);
                                commonController.dataBaseOperate("[" + RedisAPI.get("ZCJH_" + jhOutWareHouse.getString("jhOutWareHouseID")) + "]", "com.cn.bean.out.", "FZOutWareHouse", "add", DatabaseOpt.DATA);
                                commonController.dataBaseOperate(RedisAPI.get("ZCJHDetail_" + jhOutWareHouse.getString("jhOutWareHouseID")), "com.cn.bean.out.", "FZInWareHouseList", "add", DatabaseOpt.DATA);
                                commonController.dataBaseOperate(RedisAPI.get("ZCJHDetail_" + jhOutWareHouse.getString("jhOutWareHouseID")), "com.cn.bean.out.", "FZOutWareHouseList", "add", DatabaseOpt.DATA);
                                 */
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                            } else {
                                json = Units.objectToJson(-1, "计划分包失败!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "JHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            JSONObject object = JSONObject.parseObject(item);
                            List verifyList = commonController.dataBaseQuery("table", "com.cn.bean.out.", "JHOutWareHouse", "*", "JHOutWareHouseID = '" + object.getString("jhOutWareHouseID") + "'", 11, 1, "JHOutWareHouseID", 1, DatabaseOpt.DATA);
                            if (verifyList != null && verifyList.size() > 0) {
                                json = Units.objectToJson(-1, "计划已保存!", null);
                                break;
                            }
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, JHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            /**
                             * 总成计划根据BOM分解出来的明细计划
                             */
                            ArrayList<Object> detailResult = controller.importZCData(submitData, item);

                            ArrayList<JHOutWareHouseList> result = controller.importData(detailResult, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    JSONObject jhOutWareHouse = JSONObject.parseObject(item);
                                    jhOutWareHouse.put("jhMethod", "总成计划");
                                    int addRes = commonController.dataBaseOperate("[" + jhOutWareHouse.toJSONString() + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", DatabaseOpt.DATA).get(0);
                                    if (addRes == 0) {
                                        //System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            if (!Units.strIsEmpty(Units.getSubJsonStr(item, "jhOutWareHouseID"))) {
                                                commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                            }
                                            json = Units.objectToJson(-1, "明细添加失败!", null);
                                        }
                                    } else {
                                        json = Units.objectToJson(-1, "计划添加失败!", null);
                                    }
                                } else {
                                    //当前库存不满足该计划
                                    json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 计划已确认!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中删除行!", null);
                            }
                            break;
                        }
                        case "print": {
                            if (!Units.strIsEmpty(delete)) {
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                List<Object> list = new ArrayList<>();
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    String whereStr = "JHOutWareHouseID = '" + object.get("jhOutWareHouseID") + "'";
                                    list.addAll(interfaceController.queryOperate("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", "", "{}", whereStr, false, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                                }

                                String result = "{}";
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中删除行!", null);
                            }
                            break;
                        }
                        case "printItem": {
                            if (!Units.strIsEmpty(delete)) {
                                logger.info(JSONObject.toJSONString(delete));
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                List<Object> list = new ArrayList<>();
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中删除行!", null);
                            }
                            break;
                        }
                        case "finish": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            if (!Units.strIsEmpty(delete) && employee.getEmployeeName().compareTo("管理员") == 0) {
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    object.put("jhOutWareHouseID", "string," + object.get("jhOutWareHouseID"));
                                }
                                //System.out.println(paramsArray.toJSONString());
                                ArrayList<Integer> delResult = commonController.proceduceForUpdate("tbFinishJH", paramsArray, DatabaseOpt.DATA);
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "操作成功!", null);
                                } else {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据确认失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中数据或没有权限进行该操作", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="紧急计划">
                case "紧急计划": {
                    String whereCase = "JHType = '紧急计划'";
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@紧急计划");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "JHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@紧急计划");
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            json = interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetSupplierKFJCLpListForJHCK", proParams, "com.cn.bean.out.LPKCListInfo", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());

                                        filterList.add(sjck);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "importDetail": {
                            /**
                             * 导入的数据
                             */
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "JHOutWareHouseList", importPath + fileName);
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(importData, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", DatabaseOpt.DATA).get(0);
                                    if (addRes == 0) {
//                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                        if (addRes == 0) {
                                            JSONObject object = new JSONObject();
                                            object.put("datas", result);
                                            json = Units.objectToJson(0, "计划添加成功!", object.toJSONString());
                                        } else {
                                            if (!Units.strIsEmpty(Units.getSubJsonStr(item, "jhOutWareHouseID"))) {
                                                commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                            }
                                            json = Units.objectToJson(-1, "明细添加失败!", null);
                                        }
                                    } else {
                                        json = Units.objectToJson(-1, "计划添加失败!", null);
                                    }
                                } else {
                                    //当前库存不满足该计划
                                    //当前库存不满足该计划
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<JHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        JHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
//                                            houseShort.setPartID(list.getPartID());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
//                                            houseShort.setInboundBatch(list.getInboundBatch());
                                            houseShort.setJhCKAmount(list.getJhCKAmount());
//                                            houseShort.setAutoStylingName(list.getAutoStylingName());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    //json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                    json = Units.objectToJson(-1, "计划导入失败!", object.toJSONString());
                                    //json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "confirm": {
                            int result;
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<Integer> res = controller.jhPartitionPackage(JSONObject.parseObject(datas).getString("jhOutWareHouseID"));
                            if (res != null) {
                                result = res.get(0);
                            } else {
                                result = -1;
                            }
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                                JSONObject obj = new JSONObject();
                                obj.put("jhConfirm", "true");
                                commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", DatabaseOpt.DATA).get(0);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                            } else {
                                json = Units.objectToJson(-1, "计划分包失败!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "JHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            JSONObject object = JSONObject.parseObject(item);
                            List verifyList = commonController.dataBaseQuery("table", "com.cn.bean.out.", "JHOutWareHouse", "*", "JHOutWareHouseID = '" + object.getString("jhOutWareHouseID") + "'", 11, 1, "JHOutWareHouseID", 1, DatabaseOpt.DATA);
                            if (verifyList != null && verifyList.size() > 0) {
                                json = Units.objectToJson(-1, "计划已保存!", null);
                                break;
                            }
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, JHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(submitData, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", DatabaseOpt.DATA).get(0);
                                    if (addRes == 0) {
                                        //System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            if (!Units.strIsEmpty(Units.getSubJsonStr(item, "jhOutWareHouseID"))) {
                                                commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                            }
                                            json = Units.objectToJson(-1, "明细添加失败!", null);
                                        }
                                    } else {
                                        json = Units.objectToJson(-1, "计划添加失败!", null);
                                    }
                                } else {
                                    //当前库存不满足该计划
                                    json = Units.objectToJson(-1, "计划导入失败!", JSONObject.toJSONString(result));
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "JHOutWareHouse", "delete", DatabaseOpt.DATA);
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 计划已确认!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
                            }
                            break;
                        }
                        case "finish": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            if (!Units.strIsEmpty(delete) && employee.getEmployeeName().compareTo("管理员") == 0) {
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    object.put("jhOutWareHouseID", "string," + object.get("jhOutWareHouseID"));
                                }
                                //System.out.println(paramsArray.toJSONString());
                                ArrayList<Integer> delResult = commonController.proceduceForUpdate("tbFinishJH", paramsArray, DatabaseOpt.DATA);
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "操作成功!", null);
                                } else {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据确认失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中数据或没有权限进行该操作", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="临时调货">
                case "临时调货": {
                    String jhType = "FJHType = '临时调货'";
                    switch (operation) {
                        case "create": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            String whereCase = "exists (select * from tblFJHOutWareHouseList list left join viewGYSPartContainerInfo gys"
                                    + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                    + " where list.FJHOutWareHouseID = viewFJHOutWareHouse.FJHOutWareHouseID"
                                    + " and list.WareHouseManagername is null"
                                    + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')"
                                    + " and " + jhType;
                            whereCase = (operateType.compareTo("app") == 0) ? (whereCase) : (jhType);

                            //json = interfaceController.createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "FJHOutWareHouse", datas, rely, whereCase, "FJHOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@临时调货");
                            json = Units.insertStr(json, "\\\"客户类型,hidden\\", ",@终端客户");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "FJHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@临时调货");
                            json = Units.insertStr(json, "\\\"客户类型,hidden\\", ",@终端客户");
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_detail": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }

                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("fhStaffName", session.getAttribute("user"));
                                String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "FJHOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            }
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "FJHOutWareHouseList", "FJHOutWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            String fjhOutWareHouseID = JSONObject.parseObject(rely).getString("fjhOutWareHouseID");
                            String mainTabWhereSql = "FJHOutWareHouseID = '" + fjhOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FJHOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "FJHOutWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                FJHOutWareHouse fJHOutWareHouse = (FJHOutWareHouse) list.get(0);

                                //获取明细
                                Class objClass = Class.forName("com.cn.bean.out." + "FJHOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                String detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewFJHOutWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewFJHOutWareHouseList.PartCode"
                                        + " and viewFJHOutWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "FJHOutWareHouseList", "*", whereSql, pageSize, pageIndex, "FJHOutWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));

                                //根据明细, 获取明细包含的供应商编号拼接
                                ArrayList supplierList = new ArrayList();
                                for (Object obj : detailList) {
                                    FJHOutWareHouseList sj = (FJHOutWareHouseList) obj;
                                    if (!supplierList.contains(sj.getSupplierID())) {
                                        supplierList.add(sj.getSupplierID());
                                    }
                                }
                                String supplierStr = Arrays.toString(supplierList.toArray());
                                HashMap<String, String> limitMap = new HashMap<>();
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierIDStr", "string," + supplierStr.substring(1, supplierStr.length() - 1).replace(" ", ""));
                                //proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> lp = commonController.proceduceQuery("tbGetKFJCLpListForJHCKWithSupplier_MulSupplier", proParams, "com.cn.bean.out.LPKCListInfo", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                if (lp != null && lp.size() > 0) {
                                    for (Object obj : lp) {
                                        LPKCListInfo info = (LPKCListInfo) obj;
                                        limitMap.put(info.getSupplierID() + "_" + info.getPartCode(), String.valueOf(info.getLpAmount()));
                                    }
                                }

                                String result = "{}";
                                //System.out.println(limitMap.size());
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        FJHOutWareHouseList bpth = (FJHOutWareHouseList) obj;
                                        bpth.setLpAmount(Integer.valueOf(
                                                (null == limitMap.get(bpth.getSupplierID() + "_" + bpth.getPartCode()))
                                                ? "0" : limitMap.get(bpth.getSupplierID() + "_" + bpth.getPartCode())
                                        ) + bpth.getFjhCKAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(fJHOutWareHouse.getFhStaffName())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
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
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, jhType, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, jhType, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("fzdCustomerID") == 0) {
                                String[] keys = {"fzdCustomerID", "fzdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCLpListForJHCKWithSupplier", proParams, "com.cn.bean.out.LPKCListInfo", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());

                                        filterList.add(sjck);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fhStaffName", session.getAttribute("user"));
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "FJHOutWareHouse", "update", DatabaseOpt.DATA);
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
                            String fjhOutWareHouseID = arrayParam.getJSONObject(0).getString("fjhOutWareHouseID");
                            String mainTabWhereSql = "FJHOutWareHouseID = '" + fjhOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FJHOutWareHouse", "*", mainTabWhereSql, 11, 1, "FJHOutWareHouseID", 0, DatabaseOpt.DATA);
                            if (list != null && list.size() > 0) {
                                JSONArray updateArray = new JSONArray();
                                for (int i = 0; i < arrayParam.size(); i++) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("fjhCKTime", Units.getNowTime());
                                    obj.put("wareHouseManagerName", session.getAttribute("user"));
                                    updateArray.add(obj);
                                    updateArray.add(arrayParam.getJSONObject(i));
                                }
                                int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "FJHOutWareHouseList", "update", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("wareHouseManagerName", session.getAttribute("user"));
                                    json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                                } else {
                                    json = Units.objectToJson(-1, "审核失败!", null);
                                }

                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "importDetail": {
                            /**
                             * 导入的数据
                             */
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "FJHOutWareHouseList", importPath + fileName);
                            FJHOutWareHouseController controller = new FJHOutWareHouseController();
                            ArrayList<FJHOutWareHouseList> result = controller.importFJHData(importData, item);
                            if (result != null && result.size() > 0) {
                                int addRes = controller.addFJHData(item, "临时调货");
                                if (result.get(0).getListNumber() > 0) {
                                    if (addRes == 0) {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(0, "计划导入成功, 计划已保存!", object.toJSONString());
                                    } else {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(-1, "计划导入失败, 计划未保存!", object.toJSONString());
                                    }
                                } else {
                                    //返回差缺表
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<FJHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        FJHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
//                                            houseShort.setPartID(list.getPartID());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
//                                            houseShort.setInboundBatch(list.getInboundBatch());
                                            houseShort.setJhCKAmount(list.getFjhCKAmount());
//                                            houseShort.setAutoStylingName(list.getAutoStylingName());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    json = Units.objectToJson(-1, (addRes == 0) ? "计划导入失败, 计划已保存!" : "计划导入失败, 计划未保存!", object.toJSONString());
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "FJHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            List verifyList = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FJHOutWareHouse", "*", "FJHOutWareHouseID = '" + Units.getSubJsonStr(item, "fjhOutWareHouseID") + "'", 11, 1, "FJHOutWareHouseID", 1, DatabaseOpt.DATA);
                            if (verifyList != null && verifyList.size() > 0) {
                                json = Units.objectToJson(-1, "计划已保存!", null);
                                break;
                            }
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, FJHOutWareHouseList.class));

                            FJHOutWareHouseController controller = new FJHOutWareHouseController();
                            ArrayList<FJHOutWareHouseList> result = controller.importFJHData(submitData, item);
                            if (result != null && result.size() > 0) {
                                int addRes = controller.addFJHData(item, "临时调货");
                                if (result.get(0).getListNumber() > 0) {
                                    if (addRes == 0) {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(0, "计划导入成功, 计划已保存!", object.toJSONString());
                                    } else {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(-1, "计划导入失败, 计划未保存!", object.toJSONString());
                                    }
                                } else {
                                    //返回差缺表
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<FJHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        FJHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
//                                            houseShort.setPartID(list.getPartID());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
//                                            houseShort.setInboundBatch(list.getInboundBatch());
                                            houseShort.setJhCKAmount(list.getFjhCKAmount());
//                                            houseShort.setAutoStylingName(list.getAutoStylingName());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    json = Units.objectToJson(-1, (addRes == 0) ? "计划导入失败, 计划已保存!" : "计划导入失败, 计划未保存!", object.toJSONString());
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.out.", "FJHOutWareHouse", "FJHOutWareHouseID", "fhStaffName");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="非生产领料">
                case "非生产领料": {
                    String jhType = "FJHType = '非生产领料'";
                    switch (operation) {
                        case "create": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            String whereCase = "exists (select * from tblFJHOutWareHouseList list left join viewGYSPartContainerInfo gys"
                                    + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                    + " where list.FJHOutWareHouseID = viewFJHOutWareHouse.FJHOutWareHouseID"
                                    + " and list.WareHouseManagername is null"
                                    + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')"
                                    + " and " + jhType;
                            whereCase = (operateType.compareTo("app") == 0) ? (whereCase) : (jhType);

                            //json = interfaceController.createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "FJHOutWareHouse", datas, rely, whereCase, "FJHOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@非生产领料");
                            json = Units.insertStr(json, "\\\"客户类型,hidden\\", ",@非终端客户");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "FJHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"计划类型\\", ",@非生产领料");
                            json = Units.insertStr(json, "\\\"客户类型,hidden\\", ",@非终端客户");
                            json = Units.insertStr(json, "\\\"失败原因,0%\\", ",10%");
                            break;
                        }
                        case "request_detail": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }

                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("fhStaffName", session.getAttribute("user"));
                                String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "FJHOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            }
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "FJHOutWareHouseList", "FJHOutWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            String fjhOutWareHouseID = JSONObject.parseObject(rely).getString("fjhOutWareHouseID");
                            String mainTabWhereSql = "FJHOutWareHouseID = '" + fjhOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FJHOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "FJHOutWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                FJHOutWareHouse fJHOutWareHouse = (FJHOutWareHouse) list.get(0);

                                //获取明细
                                Class objClass = Class.forName("com.cn.bean.out." + "FJHOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                String detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewFJHOutWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewFJHOutWareHouseList.PartCode"
                                        + " and viewFJHOutWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "FJHOutWareHouseList", "*", whereSql, pageSize, pageIndex, "FJHOutWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));

                                //根据明细, 获取明细包含的供应商编号拼接
                                ArrayList supplierList = new ArrayList();
                                for (Object obj : detailList) {
                                    FJHOutWareHouseList sj = (FJHOutWareHouseList) obj;
                                    if (!supplierList.contains(sj.getSupplierID())) {
                                        supplierList.add(sj.getSupplierID());
                                    }
                                }
                                String supplierStr = Arrays.toString(supplierList.toArray());
                                HashMap<String, String> limitMap = new HashMap<>();
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierIDStr", "string," + supplierStr.substring(1, supplierStr.length() - 1).replace(" ", ""));
                                //proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> lp = commonController.proceduceQuery("tbGetKFJCLpListForJHCKWithSupplier_MulSupplier", proParams, "com.cn.bean.out.LPKCListInfo", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                if (lp != null && lp.size() > 0) {
                                    for (Object obj : lp) {
                                        LPKCListInfo info = (LPKCListInfo) obj;
                                        limitMap.put(info.getSupplierID() + "_" + info.getPartCode(), String.valueOf(info.getLpAmount()));
                                    }
                                }

                                String result = "{}";
                                //System.out.println(limitMap.size());
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        FJHOutWareHouseList bpth = (FJHOutWareHouseList) obj;
                                        bpth.setLpAmount(Integer.valueOf(
                                                (null == limitMap.get(bpth.getSupplierID() + "_" + bpth.getPartCode()))
                                                ? "0" : limitMap.get(bpth.getSupplierID() + "_" + bpth.getPartCode())
                                        ) + bpth.getFjhCKAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(fJHOutWareHouse.getFhStaffName())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
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
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, jhType, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, jhType, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("fzdCustomerID") == 0) {
                                String[] keys = {"fzdCustomerID", "fzdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCLpListForJHCKWithSupplier", proParams, "com.cn.bean.out.LPKCListInfo", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());

                                        filterList.add(sjck);
                                    }
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fhStaffName", session.getAttribute("user"));
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "FJHOutWareHouse", "update", DatabaseOpt.DATA);
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
                            String fjhOutWareHouseID = arrayParam.getJSONObject(0).getString("fjhOutWareHouseID");
                            String mainTabWhereSql = "FJHOutWareHouseID = '" + fjhOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FJHOutWareHouse", "*", mainTabWhereSql, 11, 1, "FJHOutWareHouseID", 0, DatabaseOpt.DATA);
                            if (list != null && list.size() > 0) {
                                JSONArray updateArray = new JSONArray();
                                for (int i = 0; i < arrayParam.size(); i++) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("fjhCKTime", Units.getNowTime());
                                    obj.put("wareHouseManagerName", session.getAttribute("user"));
                                    updateArray.add(obj);
                                    updateArray.add(arrayParam.getJSONObject(i));
                                }
                                int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "FJHOutWareHouseList", "update", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("wareHouseManagerName", session.getAttribute("user"));
                                    json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                                } else {
                                    json = Units.objectToJson(-1, "审核失败!", null);
                                }

                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "importDetail": {
                            /**
                             * 导入的数据
                             */
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "FJHOutWareHouseList", importPath + fileName);
                            FJHOutWareHouseController controller = new FJHOutWareHouseController();
                            ArrayList<FJHOutWareHouseList> result = controller.importFJHData(importData, item);
                            if (result != null && result.size() > 0) {
                                int addRes = controller.addFJHData(item, "非生产领料");
                                if (result.get(0).getListNumber() > 0) {
                                    if (addRes == 0) {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(0, "计划导入成功, 计划已保存!", object.toJSONString());
                                    } else {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(-1, "计划导入失败, 计划未保存!", object.toJSONString());
                                    }
                                } else {
                                    //返回差缺表
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<FJHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        FJHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
//                                            houseShort.setPartID(list.getPartID());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
//                                            houseShort.setInboundBatch(list.getInboundBatch());
                                            houseShort.setJhCKAmount(list.getFjhCKAmount());
//                                            houseShort.setAutoStylingName(list.getAutoStylingName());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    json = Units.objectToJson(-1, (addRes == 0) ? "计划导入失败, 计划已保存!" : "计划导入失败, 计划未保存!", object.toJSONString());
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "FJHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            List verifyList = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FJHOutWareHouse", "*", "FJHOutWareHouseID = '" + Units.getSubJsonStr(item, "fjhOutWareHouseID") + "'", 11, 1, "FJHOutWareHouseID", 1, DatabaseOpt.DATA);
                            if (verifyList != null && verifyList.size() > 0) {
                                json = Units.objectToJson(-1, "计划已保存!", null);
                                break;
                            }
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, FJHOutWareHouseList.class));

                            FJHOutWareHouseController controller = new FJHOutWareHouseController();
                            ArrayList<FJHOutWareHouseList> result = controller.importFJHData(submitData, item);
                            if (result != null && result.size() > 0) {
                                int addRes = controller.addFJHData(item, "非生产领料");
                                if (result.get(0).getListNumber() > 0) {
                                    if (addRes == 0) {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(0, "计划导入成功, 计划已保存!", object.toJSONString());
                                    } else {
                                        JSONObject object = new JSONObject();
                                        object.put("datas", result);
                                        json = Units.objectToJson(-1, "计划导入失败, 计划未保存!", object.toJSONString());
                                    }
                                } else {
                                    //返回差缺表
                                    List<Object> shortList = new ArrayList<>();//差缺表
                                    Iterator<FJHOutWareHouseList> iterator = result.iterator();
                                    while (iterator.hasNext()) {
                                        FJHOutWareHouseList list = iterator.next();
                                        if (list.getListNumber() < 0) {
                                            JHOutWareHouseShort houseShort = new JHOutWareHouseShort();
                                            houseShort.setSupplierID(list.getSupplierID());
                                            houseShort.setSupplierName(list.getSupplierName());
                                            houseShort.setPartName(list.getPartName());
                                            houseShort.setPartCode(list.getPartCode());
                                            houseShort.setJhCKAmount(list.getFjhCKAmount());
                                            houseShort.setShortAmount(list.getShortAmount());
                                            houseShort.setKcCount(list.getKcCount());

                                            shortList.add(houseShort);
                                        }
                                    }
                                    String filePath = interfaceController.exportDataReturnFileName(exportPath, servletPath, "com.cn.bean.out.", "JHOutWareHouseShort", shortList);
                                    JSONObject object = new JSONObject();
                                    object.put("datas", result);
                                    object.put("fileUrl", filePath);
                                    json = Units.objectToJson(-1, (addRes == 0) ? "计划导入失败, 计划已保存!" : "计划导入失败, 计划未保存!", object.toJSONString());
                                }
                            } else {
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.out.", "FJHOutWareHouse", "FJHOutWareHouseID", "fhStaffName");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场结算">
                case "现场结算": {
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "XCJS", "XCJSID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "XCJS", datas, rely, "", "XCJSID", dataType);
                            json = Units.insertStr(json, "\\\"现场结算单据号\\", ",@XCJS-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "XCJS.json"));
                            json = Units.insertStr(json, "\\\"现场结算单据号\\", ",@XCJS-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "XCJSList", "XCJSID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            String xcjsId = JSONObject.parseObject(rely).getString("xcJSID");
                            String mainTabWhereSql = "XCJSID = '" + xcjsId + "'";
                            Class objClass = Class.forName("com.cn.bean.out." + "XCJSList");
                            Method method = objClass.getMethod("getRecordCount", new Class[0]);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "XCJS", "*", mainTabWhereSql, pageSize, pageIndex, "XCJSID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                XCJS xcjs = (XCJS) list.get(0);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "XCJSList", "*", whereSql, pageSize, pageIndex, "XCJSID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(xcjs.getXcJSAuditTime())) {
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
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"非终端客户代码", "非终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partName", "partID", "autoStylingName", "partUnit"};
                                String[] keysName = {"部品件号", "部品名称", "部品编号", "车型", "部品单位"};
                                int[] keysWidth = {20, 20, 20, 20, 20,};
                                String[] fieldsName = {"partCode", "partName", "partID", "autoStylingName", "partUnit"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "XCJS", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "XCJSList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "xcJSID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "xcJSID") + "]", "com.cn.bean.out.", "XCJS", "delete", DatabaseOpt.DATA);
                                        }
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = interfaceController.submitOperate("com.cn.bean.out.", "XCJSList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("xcJSAuditStaffName", session.getAttribute("user"));
                            obj.put("xcJSAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "XCJS", "update", DatabaseOpt.DATA);
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
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.out.", "XCJS", "XCJSID", "xcJSAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "XCJSList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "XCJSList", importPath + fileName);
                            XCJS xcjs = JSONObject.parseObject(item, XCJS.class);
                            //System.out.println("item:" + item);
                            if (importData != null) {
                                Iterator iterator = importData.iterator();
                                while (iterator.hasNext()) {
                                    XCJSList houseList = (XCJSList) iterator.next();
                                    houseList.setXcJSID(xcjs.getXcJSID());
                                }
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "XCJS", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.out.", "XCJSList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", interfaceController.queryOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", "", item, true, DatabaseOpt.DATA, importData.size(), 1));
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "xcJSID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "xcJSID") + "]", "com.cn.bean.out.", "XCJS", "delete", DatabaseOpt.DATA);
                                        }
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

                //<editor-fold desc="领料结算">
                case "领料结算": {
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "XCJS", "XCJSID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "LLJS", datas, rely, "", "LLJSID", dataType);
                            json = Units.insertStr(json, "\\\"领料结算单据号\\", ",@LLJS-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "LLJS.json"));
                            json = Units.insertStr(json, "\\\"领料结算单据号\\", ",@LLJS-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "XCJSList", "XCJSID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            String xcjsId = JSONObject.parseObject(rely).getString("llJSID");
                            String mainTabWhereSql = "LLJSID = '" + xcjsId + "'";
                            Class objClass = Class.forName("com.cn.bean.out." + "LLJSList");
                            Method method = objClass.getMethod("getRecordCount", new Class[0]);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "LLJS", "*", mainTabWhereSql, pageSize, pageIndex, "LLJSID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                LLJS xcjs = (LLJS) list.get(0);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "LLJSList", "*", whereSql, pageSize, pageIndex, "LLJSID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(xcjs.getLlJSAuditTime())) {
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
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "LLJS", "LLJSID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "LLJS", "LLJSID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"非终端客户代码", "非终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partName", "partID", "autoStylingName", "partUnit"};
                                String[] keysName = {"部品件号", "部品名称", "部品编号", "车型", "部品单位"};
                                int[] keysWidth = {20, 20, 20, 20, 20,};
                                String[] fieldsName = {"partCode", "partName", "partID", "autoStylingName", "partUnit"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "LLJS", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "LLJSList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "llJSID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "llJSID") + "]", "com.cn.bean.out.", "LLJS", "delete", DatabaseOpt.DATA);
                                        }
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = interfaceController.submitOperate("com.cn.bean.out.", "LLJSList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("xcJSAuditStaffName", session.getAttribute("user"));
                            obj.put("xcJSAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "LLJS", "update", DatabaseOpt.DATA);
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
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.out.", "LLJS", "LLJSID", "llJSAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(exportPath, servletPath,  "com.cn.bean.out.", "LLJSList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "LLJSList", importPath + fileName);
                            XCJS xcjs = JSONObject.parseObject(item, XCJS.class);
                            //System.out.println("item:" + item);
                            if (importData != null) {
                                Iterator iterator = importData.iterator();
                                while (iterator.hasNext()) {
                                    XCJSList houseList = (XCJSList) iterator.next();
                                    houseList.setXcJSID(xcjs.getXcJSID());
                                }
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "LLJSList", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.out.", "LLJSList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", interfaceController.queryOperate("com.cn.bean.out.", "view", "LLJS", "LLJSID", "", item, true, DatabaseOpt.DATA, importData.size(), 1));
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "llJSID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "llJSID") + "]", "com.cn.bean.out.", "LLJS", "delete", DatabaseOpt.DATA);
                                        }
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

                //<editor-fold desc="部品退货">
                case "部品退货": {
                    switch (operation) {
                        case "create": {
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            String whereCase = "";
                            if (operateType.compareTo("app") == 0) {
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    whereCase = "exists (select * from tblBPTHOutWareHouseList list left join viewGYSPartContainerInfo gys"
                                            + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                            + " where list.BPTHOutWareHoseID = viewBPTHOutWareHouse.BPTHOutWareHoseID"
                                            + " and list.WareHouseManagername is null"
                                            + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "') and THPartState <> '不良品'";
                                }

                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {
                                    whereCase = "exists (select * from tblBPTHOutWareHouseList list"
                                            + " where list.BPTHOutWareHoseID = viewBPTHOutWareHouse.BPTHOutWareHoseID"
                                            + " and list.WareHouseManagername is null)"
                                            + " and THPartState = '不良品'";
                                }
                            }

                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "BPTHOutWareHouse", datas, rely, whereCase, "BPTHOutWareHoseID", dataType);
                            json = Units.insertStr(json, "\\\"退货出库单据号\\", ",@THCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "BPTHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"退货出库单据号\\", ",@THCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("bpTHAuditStaffName", session.getAttribute("user"));
                            obj.put("bpTHAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "BPTHOutWareHouse", "update", DatabaseOpt.DATA);
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
                            String bpTHOutWareHoseID = arrayParam.getJSONObject(0).getString("bpTHOutWareHoseID");
                            String mainTabWhereSql = "BPTHOutWareHoseID = '" + bpTHOutWareHoseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "BPTHOutWareHouse", "*", mainTabWhereSql, 11, 1, "BPTHOutWareHoseID", 0, DatabaseOpt.DATA);
                            if (list != null && list.size() > 0) {
                                BPTHOutWareHouse fXInWareHouse = (BPTHOutWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(fXInWareHouse.getBpTHAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "BPTHOutWareHouseList", "update", DatabaseOpt.DATA).get(0);
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
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            
                            if (operateType.compareToIgnoreCase("app") == 0) {
                                JSONObject obj = new JSONObject();
                                obj.put("bpTHAuditStaffName", session.getAttribute("user"));
                                obj.put("bpTHAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "BPTHOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            }
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "BPTHOutWareHouseList", "BPTHOutWareHoseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("bpTHOutWareHoseID");
                            String mainTabWhereSql = "BPTHOutWareHoseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "BPTHOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "BPTHOutWareHoseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                BPTHOutWareHouse bPTHOutWareHouse = (BPTHOutWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("待检品") == 0) {
                                    List<Object> djp = commonController.proceduceQuery("spGetKFJCDjpListForBPTH", proParams, "com.cn.bean.pro.KFJCDJPForBPTH", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                    if (djp != null && djp.size() > 0) {
                                        for (Object obj : djp) {
                                            KFJCDJPForBPTH bpth = (KFJCDJPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfDjpJCAmount());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("良品") == 0) {
                                    List<Object> lp = commonController.proceduceQuery("spGetKFJCLpListForBPTH", proParams, "com.cn.bean.pro.KFJCLPForBPTH", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                    if (lp != null && lp.size() > 0) {
                                        for (Object obj : lp) {
                                            KFJCLPForBPTH bpth = (KFJCLPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfJCLpAmount());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("不良品") == 0) {
                                    List<Object> blp = commonController.proceduceQuery("spGetKFJCBLpListForBPTH", proParams, "com.cn.bean.pro.KFJCBLPForBPTH", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                    if (blp != null && blp.size() > 0) {
                                        for (Object obj : blp) {
                                            KFJCBLPForBPTH bpth = (KFJCBLPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfJCBLpAmount());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("返修品") == 0) {
                                    List<Object> fxp = commonController.proceduceQuery("spGetKFJCFXpListForBPTH", proParams, "com.cn.bean.pro.KFJCFXPForBPTH", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                    if (fxp != null && fxp.size() > 0) {
                                        for (Object obj : fxp) {
                                            KFJCFXPForBPTH bpth = (KFJCFXPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfJCFXpAmount());
                                        }
                                    }
                                }

                                Class objClass = Class.forName("com.cn.bean.out." + "BPTHOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                //一张单据可能有多个库的产品, 明细也区分库管员
                                String detailWhereCase = "";
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewBPTHOutWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewBPTHOutWareHouseList.PartCode"
                                        + " and viewBPTHOutWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                }
                                
                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {//不良品库管员
                                    detailWhereCase = "WareHouseManagername is null";
                                }
                                /*
                                //单据分库管员, 明细不再区分库管员(需要一个单子的明细是一个库的产品)
                                String detailWhereCase = "WareHouseManagername is null";
                                */
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "BPTHOutWareHouseList", "*", whereSql, pageSize, pageIndex, "BPTHOutWareHoseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));

                                String result = "{}";
                                //System.out.println(limitMap.size());
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        BPTHOutWareHouseList bpth = (BPTHOutWareHouseList) obj;
                                        bpth.setThAmount(Integer.valueOf((null == limitMap.get(bpth.getPartCode())) ? "0" : limitMap.get(bpth.getPartCode())) + bpth.getThCKAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(bPTHOutWareHouse.getBpTHAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, new Object[]{}));
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
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "thAmount", "thCKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "退货数量", ""};
                                int[] keysWidth = {20, 20, 20, 20, 20, 0};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("待检品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCDjpListForBPTH", proParams, "com.cn.bean.pro.KFJCDJPForBPTH", DatabaseOpt.DATA);
                                    if (list != null && list.size() > 0) {
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            KFJCDJPForBPTH bpth = (KFJCDJPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + bpth.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            bpth.setPartID(baseInfo.getPartID());
                                            bpth.setPartName(baseInfo.getPartName());

                                            filterList.add(bpth);
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            bpth.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfDjpJCAmount", "kfDjpJCAmount"};
                                        json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCDJPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCLpListForBPTH", proParams, "com.cn.bean.pro.KFJCLPForBPTH", DatabaseOpt.DATA);
                                    if (list != null && list.size() > 0) {
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            KFJCLPForBPTH bpth = (KFJCLPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + bpth.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            bpth.setPartID(baseInfo.getPartID());
                                            bpth.setPartName(baseInfo.getPartName());

                                            filterList.add(bpth);
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            bpth.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCLpAmount", "kfJCLpAmount"};
                                        json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCLPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("不良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCBLpListForBPTH", proParams, "com.cn.bean.pro.KFJCBLPForBPTH", DatabaseOpt.DATA);
                                    if (list != null && list.size() > 0) {
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            KFJCBLPForBPTH bpth = (KFJCBLPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + bpth.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            bpth.setPartID(baseInfo.getPartID());
                                            bpth.setPartName(baseInfo.getPartName());

                                            filterList.add(bpth);
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }
                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCBLpAmount", "kfJCBLpAmount"};
                                        json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCBLPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("返修品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCFXpListForBPTH", proParams, "com.cn.bean.pro.KFJCFXPForBPTH", DatabaseOpt.DATA);
                                    if (list != null && list.size() > 0) {
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            KFJCFXPForBPTH bpth = (KFJCFXPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + bpth.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            bpth.setPartID(baseInfo.getPartID());
                                            bpth.setPartName(baseInfo.getPartName());

                                            filterList.add(bpth);
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }
                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCFXpAmount", "kfJCFXpAmount"};
                                        json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCFXPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "BPTHOutWareHouse", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "BPTHOutWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "bpTHOutWareHoseID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "bpTHOutWareHoseID") + "]", "com.cn.bean.move.", "BPTHOutWareHouse", "delete", DatabaseOpt.DATA);
                                        }
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = interfaceController.submitOperate("com.cn.bean.out.", "BPTHOutWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                CommonOperate operate = new CommonOperate();
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.out.", "BPTHOutWareHouse", "BPTHOutWareHoseID", "bpTHAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="分装入库">
                case "分装入库": {
                    switch (operation) {
                        case "create": {
                            json = interfaceController.createOperate(15, "table", "com/cn/json/out/", "com.cn.bean.out.", "FZInWareHouse", "FZInWareHouseID", DatabaseOpt.DATA);
                            json = Units.insertStr(json, "\\\"分装入库单号\\", ",@FZRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"部品件号\\", ",@" + ((Employee) session.getAttribute("employee")).getEmployeeRemark());
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.out.", "table", "FZInWareHouse", "FZInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.out.", "FZInWareHouse", "", add, "", "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="分装出库">
                case "分装出库": {
                    switch (operation) {
                        case "create": {
                            json = interfaceController.createOperate(15, "table", "com/cn/json/out/", "com.cn.bean.out.", "FZOutWareHouse", "FZOutWareHouseID", DatabaseOpt.DATA);
                            json = Units.insertStr(json, "\\\"分装出库单号\\", ",@FZCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"部品件号\\", ",@" + ((Employee) session.getAttribute("employee")).getEmployeeRemark());
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            json = Units.insertStr(json, "\\\"最大出库数量,hidden\\", ",@" + controller.getFZMaxOutboundMount(((Employee) session.getAttribute("employee")).getEmployeeRemark()));
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.out.", "table", "FZOutWareHouse", "FZOutWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.out.", "FZOutWareHouse", "", add, "", "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="备货管理">
                case "备货管理": {
                    String whereCase = "exists(select * from tblBHProgressList bhList"
                                + " where BHTime is null and viewJHOutWareHouseList.JHOutWareHouseID = bhList.JHOutWareHouseID"
                                + " and viewJHOutWareHouseList.SupplierID = bhList.SupplierID and viewJHOutWareHouseList.PartCode = bhList.PartCode"
                                + " and viewJHOutWareHouseList.InboundBatch = bhList.InboundBatch)";
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", "JHOutWareHouseID", DatabaseOpt.DATA);
                            //json = interfaceController.createOperateWithFilter(10, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            if (isHistory == 0) {
                                json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHDemandTime", dataType);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, "", "JHDemandTime", dataType);
                            }
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            String detailCase = "FinishTime is null";
                            if (isHistory == 0) {
                                json = interfaceController.queryOperateWithFilter("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", datas, rely, detailCase, false, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOperateWithFilter("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", datas, rely, "", false, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_page": {
                            //json = interfaceController.queryOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, "", true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, "", true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            JSONArray updateArray = new JSONArray();
                            for (int i = 0; i < arrayParam.size(); i++) {
                                JSONObject obj = new JSONObject();
                                obj.put("bhTime", Units.getNowTime());
                                updateArray.add(obj);
                                updateArray.add(arrayParam.getJSONObject(i));
                            }
                            int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "BHProgressList", "update", DatabaseOpt.DATA).get(0);
                            if (result == 0) {
                                JSONObject checkObj = new JSONObject();
                                checkObj.put("JHOutWareHouseID", "string," + arrayParam.getJSONObject(0).getString("jhOutWareHouseID"));
                                checkObj.put("SupplierID", "string," + arrayParam.getJSONObject(0).getString("supplierID"));
                                checkObj.put("PartCode", "string," + arrayParam.getJSONObject(0).getString("partCode"));

                                //System.out.println("json:" + checkObj.toJSONString());
                                commonController.proceduceForUpdate("tbBHJHFinishedCheck", checkObj, DatabaseOpt.DATA);
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }

                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="领货管理">
                case "领货管理": {
                    if (employee == null) {
                        json = Units.objectToJson(-99, "未登陆", null);
                        break;
                    }
                    int tmpPageSize;
                    String whereCase;
                    if (operateType.compareTo("app") == 0) {
                        isHistory = 0;
                        tmpPageSize = Integer.MAX_VALUE;
                        /*
                        whereCase = "exists(select * from tblBHProgressList bhList left join tblPartBHStaff part on bhList.PartCode = part.PartCode"
                                + " where viewJHOutWareHouseList.JHOutWareHouseID = bhList.JHOutWareHouseID"
                                + " and viewJHOutWareHouseList.SupplierID = bhList.SupplierID and viewJHOutWareHouseList.PartCode = bhList.PartCode"
                                + " and viewJHOutWareHouseList.InboundBatch = bhList.InboundBatch and bhList.BHTime is not null"
                                + " and (JHStatus = 1 or JHStatus = -2) and part.LHEmployeeName = '" + employee.getEmployeeName() + "')";
                        */
                        whereCase = "exists(select * from (select * from tblBHProgressList where BHTime is not null) bhList"
                                + " inner join (select * from tblLHProgressList where LHTime is null) lhList"
                                + " on bhList.JHOutWareHouseID = lhList.JHOutWareHouseID and bhList.SupplierID = lhList.SupplierID"
                                + " and bhList.PartCode = lhList.PartCode and bhList.InboundBatch = lhList.InboundBatch"
                                + " and bhList.PackingNumber = lhList.PackingNumber"
                                + " inner join (select * from tblPartBHStaff where LHEmployeeName = '" + employee.getEmployeeName() + "') part"
                                + " on bhList.PartCode = part.PartCode"
                                + " where viewJHOutWareHouseList.JHOutWareHouseID = bhList.JHOutWareHouseID"
                                + " and viewJHOutWareHouseList.SupplierID = bhList.SupplierID and viewJHOutWareHouseList.PartCode = bhList.PartCode"
                                + " and viewJHOutWareHouseList.InboundBatch = bhList.InboundBatch)";
                    } else {
                        whereCase = "exists(select * from (select * from tblBHProgressList where BHTime is not null) bhList"
                                + " inner join (select * from tblLHProgressList where LHTime is null) lhList"
                                + " on bhList.JHOutWareHouseID = lhList.JHOutWareHouseID and bhList.SupplierID = lhList.SupplierID"
                                + " and bhList.PartCode = lhList.PartCode and bhList.InboundBatch = lhList.InboundBatch"
                                + " and bhList.PackingNumber = lhList.PackingNumber"
                                + " where viewJHOutWareHouseList.JHOutWareHouseID = bhList.JHOutWareHouseID"
                                + " and viewJHOutWareHouseList.SupplierID = bhList.SupplierID and viewJHOutWareHouseList.PartCode = bhList.PartCode"
                                + " and viewJHOutWareHouseList.InboundBatch = bhList.InboundBatch)";
                        tmpPageSize = 20;
                    }
                    //String whereCase1 = whereCase + " and (JHStatus < 2)";

                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", "JHOutWareHouseID", DatabaseOpt.DATA);
                            if (isHistory == 0) {
                                json = interfaceController.createOperateOnDate(tmpPageSize, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHDemandTime", dataType);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.createOperateOnDate(tmpPageSize, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHDemandTime", dataType);
                            }
                            //System.out.println("json:" + json);
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            String detailCase = "exists(select * from tblBHProgressList bhList where viewLHProgressList.JHOutWareHouseID = bhList.JHOutWareHouseID"
                                    + " and viewLHProgressList.SupplierID = bhList.SupplierID and viewLHProgressList.PartCode = bhList.PartCode"
                                    + " and viewLHProgressList.PackingNumber = bhList.PackingNumber and bhList.BHTime is not null)";
                            String detailCase1 = detailCase + " and FinishTime is null";
                            if (operateType.compareTo("app") == 0) {
                                isHistory = 0;
                            }

                            if (isHistory == 0) {
                                json = interfaceController.queryOperateWithFilter("com.cn.bean.out.", "view", "LHProgressList", "JHOutWareHouseID", datas, rely, detailCase1, false, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOperateWithFilter("com.cn.bean.out.", "view", "LHProgressList", "JHOutWareHouseID", datas, rely, detailCase, false, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_page": {
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            JSONArray updateArray = new JSONArray();
                            for (int i = 0; i < arrayParam.size(); i++) {
                                JSONObject obj = new JSONObject();
                                obj.put("lhTime", Units.getNowTime());
                                updateArray.add(obj);
                                updateArray.add(arrayParam.getJSONObject(i));
                            }
                            int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "LHProgressList", "update", DatabaseOpt.DATA).get(0);
                            if (result == 0) {
                                JSONObject checkObj = new JSONObject();
                                checkObj.put("JHOutWareHouseID", "string," + arrayParam.getJSONObject(0).getString("jhOutWareHouseID"));
                                checkObj.put("SupplierID", "string," + arrayParam.getJSONObject(0).getString("supplierID"));
                                checkObj.put("PartCode", "string," + arrayParam.getJSONObject(0).getString("partCode"));

                                //System.out.println("json:" + checkObj.toJSONString());
                                commonController.proceduceForUpdate("tbLHJHFinishedCheck", checkObj, DatabaseOpt.DATA);
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }

                            break;
                        }
                        case "confirm": {
                            JSONObject updateObj = new JSONObject();
                            updateObj.put("lhTime", Units.getNowTime());
                            JSONObject whereObj = new JSONObject();
                            whereObj.put("jhOutWareHouseID", paramsJson.getString("jhOutWareHouseID"));
                            whereObj.put("partCode", paramsJson.getString("partCode"));
                            whereObj.put("supplierID", paramsJson.getString("supplierID"));
                            whereObj.put("packingNumber", paramsJson.getIntValue("packingNumber"));
                            int result = commonController.dataBaseOperate("[" + updateObj.toJSONString() + "," + whereObj.toJSONString() + "]",
                                    "com.cn.bean.out.", "LHProgressList", "update", DatabaseOpt.DATA).get(0);
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }
                            
                            /*
                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));
                            commonController.proceduceForUpdate("tbLHJHFinishedCheck", checkObj, DatabaseOpt.DATA);
                            */
                            break;
                        }
                        case "finished": {
                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));
                            commonController.proceduceForUpdate("tbLHJHFinishedCheck", checkObj, DatabaseOpt.DATA);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="配送管理">
                case "配送管理": {
                    if (employee == null) {
                        json = Units.objectToJson(-99, "未登陆", null);
                        break;
                    }
                    int tmpPageSize;
                    String whereCase;
                    if (operateType.compareTo("app") == 0) {
                        isHistory = 0;
                        tmpPageSize = Integer.MAX_VALUE;
                        whereCase = "exists(select * from (select * from tblLHProgressList where LHTime is not null) lhList"
                                + " inner join (select * from tblSXProgressList where SXTime is null) sxList"
                                + " on sxList.JHOutWareHouseID = lhList.JHOutWareHouseID and sxList.SupplierID = lhList.SupplierID"
                                + " and sxList.PartCode = lhList.PartCode and sxList.InboundBatch = lhList.InboundBatch"
                                + " and sxList.PackingNumber = lhList.PackingNumber"
                                + " inner join (select * from tblPartBHStaff where PSEmployeeName = '" + employee.getEmployeeName() + "') part"
                                + " on lhList.PartCode = part.PartCode"
                                + " where viewJHOutWareHouseList.JHOutWareHouseID = lhList.JHOutWareHouseID"
                                + " and viewJHOutWareHouseList.SupplierID = lhList.SupplierID and viewJHOutWareHouseList.PartCode = lhList.PartCode"
                                + " and viewJHOutWareHouseList.InboundBatch = lhList.InboundBatch) and AssemblingStation = 3";
                    } else {
                        tmpPageSize = 20;
                        whereCase = "exists(select * from (select * from tblLHProgressList where LHTime is not null) lhList"
                                + " inner join (select * from tblSXProgressList where SXTime is null) sxList"
                                + " on sxList.JHOutWareHouseID = lhList.JHOutWareHouseID and sxList.SupplierID = lhList.SupplierID"
                                + " and sxList.PartCode = lhList.PartCode and sxList.InboundBatch = lhList.InboundBatch"
                                + " and sxList.PackingNumber = lhList.PackingNumber"
                                + " where viewJHOutWareHouseList.JHOutWareHouseID = lhList.JHOutWareHouseID"
                                + " and viewJHOutWareHouseList.SupplierID = lhList.SupplierID and viewJHOutWareHouseList.PartCode = lhList.PartCode"
                                + " and viewJHOutWareHouseList.InboundBatch = lhList.InboundBatch)";
                        
                    }
                    //String whereCase1 = whereCase + " and JHStatus < 3";
                    switch (operation) {
                        case "create": {
                            if (isHistory == 0) {
                                json = interfaceController.createOperateOnDate(tmpPageSize, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHDemandTime", dataType);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.createOperateOnDate(tmpPageSize, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHDemandTime", dataType);
                            }
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            String detailCase = "exists(select * from tblLHProgressList lhList where viewSXProgressList.JHOutWareHouseID = lhList.JHOutWareHouseID"
                                    + " and viewSXProgressList.SupplierID = lhList.SupplierID and viewSXProgressList.PartCode = lhList.PartCode"
                                    + " and viewSXProgressList.PackingNumber = lhList.PackingNumber and lhList.LHTime is not null)";
                            String detailCase1 = detailCase + " and FinishTime is null";
                            if (operateType.compareTo("app") == 0) {
                                isHistory = 0;
                            }
                            if (isHistory == 0) {
                                json = interfaceController.queryOperateWithFilter("com.cn.bean.out.", "view", "SXProgressList", "JHOutWareHouseID", datas, rely, detailCase1, false, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOperateWithFilter("com.cn.bean.out.", "view", "SXProgressList", "JHOutWareHouseID", datas, rely, detailCase, false, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_page": {
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHDemandTime", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            JSONArray updateArray = new JSONArray();
                            for (int i = 0; i < arrayParam.size(); i++) {
                                PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + arrayParam.getJSONObject(i).getString("partCode").toLowerCase()), PartBaseInfo.class);
                                JSONObject obj = new JSONObject();
                                if (baseInfo.getAssemblingStation().compareTo("3") == 0) {
                                    obj.put("fzTime", Units.getNowTime());
                                } else {
                                    obj.put("sxTime", Units.getNowTime());
                                }
                                updateArray.add(obj);
                                updateArray.add(arrayParam.getJSONObject(i));
                            }
                            int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "SXProgressList", "update", DatabaseOpt.DATA).get(0);
                            if (result == 0) {
                                JSONObject checkObj = new JSONObject();
                                checkObj.put("JHOutWareHouseID", "string," + arrayParam.getJSONObject(0).getString("jhOutWareHouseID"));
                                checkObj.put("SupplierID", "string," + arrayParam.getJSONObject(0).getString("supplierID"));
                                checkObj.put("PartCode", "string," + arrayParam.getJSONObject(0).getString("partCode"));

                                //System.out.println("json:" + checkObj.toJSONString());
                                commonController.proceduceForUpdate("tbPSJHFinishedCheck", checkObj, DatabaseOpt.DATA);
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }

                            break;
                        }
                        case "confirm": {
                            //PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + paramsJson.getString("partCode").toLowerCase()), PartBaseInfo.class);
                            JSONObject updateObj = new JSONObject();
                            updateObj.put("sxTime", Units.getNowTime());

                            JSONObject whereObj = new JSONObject();
                            whereObj.put("jhOutWareHouseID", paramsJson.getString("jhOutWareHouseID"));
                            whereObj.put("partCode", paramsJson.getString("partCode"));
                            whereObj.put("supplierID", paramsJson.getString("supplierID"));
                            whereObj.put("packingNumber", paramsJson.getIntValue("packingNumber"));
                            int result = commonController.dataBaseOperate("[" + updateObj.toJSONString() + "," + whereObj.toJSONString() + "]",
                                    "com.cn.bean.out.", "SXProgressList", "update", DatabaseOpt.DATA).get(0);
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }

                            /*
                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));
                            commonController.proceduceForUpdate("tbPSJHFinishedCheck", checkObj, DatabaseOpt.DATA).get(0);
                            */
                            break;
                        }
                        case "finished": {
                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));

                            //System.out.println("json:" + checkObj.toJSONString());
                            commonController.proceduceForUpdate("tbPSJHFinishedCheck", checkObj, DatabaseOpt.DATA).get(0);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="虚拟良品库存">
                case "虚拟良品库存": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            Class classPath = Class.forName("com.cn.bean.out." + "LPKCListInfo");
                            proParams.put("fields", "string,*");
                            if (loginType.compareTo("customerLogin") == 0) {
                                proParams.put("wherecase", "string,(" + commonController.getWhereSQLStrAllField(classPath, datas) + ") and SupplierID = '" + userName + "'");
                            } else {
                                proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            }
                            //proParams.put("wherecase", "string," + commonController.getWhereSQLStrAllField(classPath, datas));
                            proParams.put("pageSize", "int," + pageSize);
                            proParams.put("pageIndex", "int," + pageIndex);
                            proParams.put("orderField", "string,SupplierID");
                            proParams.put("orderFlag", "int,0");
                            if (!Units.strIsEmpty(end)) {
                                proParams.put("Endtime", "string," + end);
                            }
                            if (!Units.strIsEmpty(start)) {
                                proParams.put("BeginTime", "string," + start);
                            }

                            String result = null;
                            if (operateType.compareTo("create") == 0) {
                                result = Units.returnFileContext(path + "com/cn/json/out/", "LPKCListInfo.json");
                            }
                            if (operateType.compareTo("search") == 0) {
                                result = "{}";
                            }

                            List<Object> list = commonController.proceduceQuery("spGetKFJCLpListForJHCKWithFilter", proParams, "com.cn.bean.out.LPKCListInfo", DatabaseOpt.DATA);
                            if (list != null && list.size() > 0) {
                                for (Object obj : list) {
                                    LPKCListInfo info = (LPKCListInfo) obj;
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + info.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + info.getSupplierID()), Customer.class);
                                    info.setSupplierName(customer.getCustomerAbbName());
                                    info.setPartID(baseInfo.getPartID());
                                    info.setPartName(baseInfo.getPartName());
                                }
                                if (operateType.compareTo("export") == 0) {
                                    json = interfaceController.exportData(exportPath, servletPath, "com.cn.bean.out.", "LPKCListInfo", (ArrayList<Object>) list);
                                } else if (operateType.compareTo("create") == 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    //buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, null));
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else if (operateType.compareTo("search") == 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    //buffer.insert(buffer.lastIndexOf("}"), ", \"counts\":" + method.invoke(null, null));
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                }
                            } else {
                                json = Units.objectToJson(0, "数据为空!", result);
                            }
                            break;
                        }
                        /*
                        case "create": {
                            String result = Units.returnFileContext(path + "com/cn/json/out/", "LPKCListInfo.json");
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            List<LPKCListInfo> list = controller.getLPKCData(null, null, null);
                            HashMap<String, LPKCListInfo> map = new HashMap<>();
                            for (LPKCListInfo info : list) {
                                if (map.containsKey(info.getType())) {
                                    info.setLpAmount(map.get(info.getType()).getLpAmount() + info.getLpAmount());
                                }
                                map.put(info.getType(), info);
                            }
                            List<Object> resList = new ArrayList<>();
                            resList.addAll(map.values());

                            if (list.size() > 0) {
                                Collections.sort(resList, new Comparator<Object>() {
                                    @Override
                                    public int compare(Object o1, Object o2) {
                                        LPKCListInfo info1 = (LPKCListInfo) o1;
                                        LPKCListInfo info2 = (LPKCListInfo) o2;
                                        return info1.getSupplierID().compareTo(info2.getSupplierID());
                                    }
                                });
                                StringBuffer buffer = new StringBuffer(result);
                                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(resList, Units.features));
                                result = buffer.toString();
                            }

                            if (operateType.compareTo("create") == 0) {
                                json = Units.objectToJson(0, "", result);
                            }
                            if (operateType.compareTo("export") == 0) {
                                json = interfaceController.exportData("com.cn.bean.out.", "LPKCListInfo", resList);
                            }
                            break;
                        }
                         */
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="条码打印">
                case "条码打印": {
                    String whereCase = "exists(select * from tblJHOutWareHouseZCList zc where"
                            + " viewJHOutWareHouse.JHOutWareHouseID = zc.JHOutWareHouseID)";
                    switch (operation) {
                        case "create": {
                            //json = interfaceController.createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", dataType);
                            break;
                        }
                        case "request_detail": {
                            /*Connection conn;
                            if (dataType.compareToIgnoreCase("isHis") == 0) {
                                conn = DatabaseOpt.HIS;
                            } else {
                                conn = DatabaseOpt.DATA;
                            }*/

                            json = interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseZCList", "JHOutWareHouseID", datas, rely, true, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "print": {
                            if (!Units.strIsEmpty(delete)) {
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                List<Object> list = new ArrayList<>();
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    String whereStr = "JHOutWareHouseID = '" + object.get("jhOutWareHouseID") + "'";
                                    list.addAll(interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseZCPacking", "JHOutWareHouseID", "", "{}", whereStr, false, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                                }

                                String result = "{}";
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中删除行!", null);
                            }
                            break;
                        }
                        case "printItem": {
                            if (!Units.strIsEmpty(delete)) {
                                //logger.info(JSONObject.toJSONString(delete));
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                List<Object> list = new ArrayList<>();
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    String whereStr = "JHOutWareHouseID = '" + object.getString("jhOutWareHouseID") + "'";
                                    whereStr += " and SupplierID = '" + object.getString("supplierID") + "'";
                                    whereStr += " and PartCode = '" + object.getString("partCode") + "'";
                                    list.addAll(interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseZCPacking", "JHOutWareHouseID", "", "{}", whereStr, false, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                                }

                                String result = "{}";
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "printPatch": {
                            if (!Units.strIsEmpty(delete)) {
                                //logger.info(JSONObject.toJSONString(delete) + ",patch:" + patch);
                                JSONArray paramsArray = JSONArray.parseArray(delete);
                                String[] patchs = patch.split("-");
                                List<Object> list = new ArrayList<>();
                                for (int i = 0; i < paramsArray.size(); i++) {
                                    JSONObject object = paramsArray.getJSONObject(i);
                                    String whereStr = "JHOutWareHouseID = '" + object.getString("jhOutWareHouseID") + "'";
                                    whereStr += " and SupplierID = '" + object.getString("supplierID") + "'";
                                    whereStr += " and PartCode = '" + object.getString("partCode") + "'";
                                    whereStr += " and PackingNumber between " + patchs[0] + " and " + patchs[1];
                                    list.addAll(interfaceController.queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseZCPacking", "JHOutWareHouseID", "", "{}", whereStr, false, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                                }

                                String result = "{}";
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    result = buffer.toString();
                                    json = Units.objectToJson(0, "", result);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送货清单">
                case "送货清单": {
                    switch (operation) {
                        case "create": {
                            json = Units.objectToJson(0, "", "");
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
