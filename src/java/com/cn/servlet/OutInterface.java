/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.ClassDescription;
import com.cn.bean.FieldDescription;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.out.BPTHOutWareHouse;
import com.cn.bean.out.BPTHOutWareHouseList;
import com.cn.bean.out.FJHOutWareHouseList;
import com.cn.bean.out.FZInWareHouse;
import com.cn.bean.out.FZInWareHouseList;
import com.cn.bean.out.JHOutWareHouseList;
import com.cn.bean.out.LPKCListInfo;
import com.cn.bean.out.XCJS;
import com.cn.bean.out.XCJSList;
import com.cn.bean.pro.KFJCBLPForBPTH;
import com.cn.bean.pro.KFJCDJPForBPTH;
import com.cn.bean.pro.KFJCDJPForSJCK;
import com.cn.bean.pro.KFJCFXPForBPTH;
import com.cn.bean.pro.KFJCLPForBPTH;
import com.cn.controller.CommonController;
import com.cn.controller.JHOutWareHouseController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private CommonController commonController;
    private DatabaseOpt opt;

    /**
     *
     * @throws ServletException
     */
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
                 * ***************************************部品出库管理**************************************
                 */
                //<editor-fold desc="部品出库管理">
                //<editor-fold desc="计划出库">
                case "计划出库": {
                    String whereCase = "JHType = '正常计划'";
                    switch (operation) {
                        case "create": {
                            //json = createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetSupplierKFJCLpListForJHCK", proParams, "com.cn.bean.out.LPKCListInfo", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());
                                    }
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
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
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
//                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                                commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", opt.getConnect()).get(0);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                            } else {
                                json = Units.objectToJson(-1, "计划分包失败!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "JHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, JHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(submitData, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="总成计划">
                case "总成计划": {
                    String whereCase = "JHType = '正常计划'";
                    switch (operation) {
                        case "create": {
                            //json = createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetSupplierKFJCLpListForJHCK", proParams, "com.cn.bean.out.LPKCListInfo", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());
                                    }
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
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
                            /**
                             * 总成计划根据BOM分解出来的明细计划
                             */
                            ArrayList<Object> detailResult = controller.importZCData(importData);
                            /**
                             * 明细计划入库
                             */
                            ArrayList<JHOutWareHouseList> result = controller.importData(detailResult, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
//                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                                commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", opt.getConnect()).get(0);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                            } else {
                                json = Units.objectToJson(-1, "计划分包失败!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "JHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, JHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            /**
                             * 总成计划根据BOM分解出来的明细计划
                             */
                            ArrayList<Object> detailResult = controller.importZCData(submitData);
                            
                            ArrayList<JHOutWareHouseList> result = controller.importData(detailResult, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="临时调货">
                case "临时调货": {
                    String whereCase = "JHType = '紧急计划'";
                    switch (operation) {
                        case "create": {
                            //json = createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", datas, rely, whereCase, "JHOutWareHouseID", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetSupplierKFJCLpListForJHCK", proParams, "com.cn.bean.out.LPKCListInfo", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());
                                    }
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
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
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
//                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                                commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", opt.getConnect()).get(0);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                            } else {
                                json = Units.objectToJson(-1, "计划分包失败!", null);
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "JHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, JHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(submitData, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "JHOutWareHouse", "delete", opt.getConnect());
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="非生产领料">
                case "非生产领料": {
                    switch (operation) {
                        case "create": {
                            //json = createOperateWithFilter(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", whereCase, "JHOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "FJHOutWareHouse", datas, rely, "", "FJHOutWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "FJHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "view", "FJHOutWareHouseList", "FJHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("fzdCustomerID") == 0) {
                                String[] keys = {"fzdCustomerID", "fzdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerAbbName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量", "良品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount", "lpAmount"};
                                //json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetSupplierKFJCLpListForJHCK", proParams, "com.cn.bean.out.LPKCListInfo", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        LPKCListInfo sjck = (LPKCListInfo) obj;

                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(sjck.getSupplierID() + "_" + sjck.getPartCode()), GYSPartContainerInfo.class);
                                        sjck.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        sjck.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());
                                    }
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.out.LPKCListInfo", keys, keysName, keysWidth, fieldsName, target, rely);
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
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "FJHOutWareHouseList", importPath + fileName);
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<FJHOutWareHouseList> result = controller.importFJHData(importData, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "FJHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
//                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "FJHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "FJHOutWareHouse", "delete", opt.getConnect());
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
                                json = Units.objectToJson(-1, "上传数据为空或文件格式不正确!", JSONObject.toJSONString(result));
                            }
                            break;
                        }
                        /*case "confirm": {
                            JSONObject obj = new JSONObject();
                            obj.put("jhConfirm", "true");
                            int result = commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "FJHOutWareHouse", "update", opt.getConnect()).get(0);
                            if (result == 0) {
                                JHOutWareHouseController controller = new JHOutWareHouseController();
                                ArrayList<Integer> res = controller.jhPartitionPackage(JSONObject.parseObject(datas).getString("jhOutWareHouseID"));
                                if (res != null) {
                                    result = res.get(0);
                                } else {
                                    result = -1;
                                }
                                if (result == 0) {
                                    json = Units.objectToJson(0, "确认成功!", null);
                                } else if (result == 2627) {
                                    json = Units.objectToJson(-1, "计划已确认,请勿重复确认!", null);
                                } else {
                                    json = Units.objectToJson(-1, "计划分包失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }
                            break;
                        }*/
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "FJHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, FJHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<FJHOutWareHouseList> result = controller.importFJHData(submitData, item);
                            if (result != null && result.size() > 0) {
                                if (result.get(0).getListNumber() > 0) {
                                    //当前库存满足该计划
                                    int addRes = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "FJHOutWareHouse", "add", opt.getConnect()).get(0);
                                    if (addRes == 0) {
                                        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
                                        addRes = commonController.dataBaseOperate(JSONObject.toJSONString(result), "com.cn.bean.out.", "FJHOutWareHouseList", "add", opt.getConnect()).get(0);
                                        if (addRes == 0) {
                                            json = Units.objectToJson(0, "计划添加成功!", JSONObject.toJSONString(result));
                                        } else {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "jhOutWareHouseID") + "]", "com.cn.bean.out.", "FJHOutWareHouse", "delete", opt.getConnect());
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
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "FJHOutWareHouse", "delete", opt.getConnect());
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="现场结算">
                case "现场结算": {
                    switch (operation) {
                        case "create": {
                            //json = createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "XCJS", "XCJSID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "XCJS", datas, rely, "", "XCJSID", opt.getConnect());
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
                            //json = queryOperate("com.cn.bean.out.", "view", "XCJSList", "XCJSID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            String xcjsId = JSONObject.parseObject(rely).getString("xcJSID");
                            String mainTabWhereSql = "XCJSID = '" + xcjsId + "'";
                            Class objClass = Class.forName("com.cn.bean.out." + "XCJSList");
                            Method method = objClass.getMethod("getRecordCount", null);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "XCJS", "*", mainTabWhereSql, pageSize, pageIndex, "XCJSID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                XCJS xcjs = (XCJS) list.get(0);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "XCJSList", "*", whereSql, pageSize, pageIndex, "XCJSID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(xcjs.getXcJSAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
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
                            //json = queryOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"非终端客户代码", "非终端客户名称"};
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
                                String[] keys = {"partCode", "partName", "partID", "autoStylingName", "partUnit"};
                                String[] keysName = {"部品件号", "部品名称", "部品编号", "车型", "部品单位"};
                                int[] keysWidth = {20, 20, 20, 20, 20,};
                                String[] fieldsName = {"partCode", "partName", "partID", "autoStylingName", "partUnit"};
                                json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "XCJS", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "XCJSList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "xcJSID") + "]", "com.cn.bean.out.", "XCJS", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.out.", "XCJSList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("xcJSAuditStaffName", session.getAttribute("user"));
                            obj.put("xcJSAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "XCJS", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "XCJS", "delete", opt.getConnect());
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 入库单下存在明细数据!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "XCJSList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "XCJSList", importPath + fileName);
                            XCJS xcjs = JSONObject.parseObject(item, XCJS.class);
                            System.out.println("item:" + item);
                            if (importData != null) {
                                Iterator iterator = importData.iterator();
                                while (iterator.hasNext()) {
                                    XCJSList houseList = (XCJSList) iterator.next();
                                    houseList.setXcJSID(xcjs.getXcJSID());
                                }
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "XCJS", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.out.", "XCJSList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.out.", "view", "XCJS", "XCJSID", "", item, true, opt.getConnect(), importData.size(), 1));
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "xcJSID") + "]", "com.cn.bean.out.", "XCJS", "delete", opt.getConnect());
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
                            //json = createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "BPTHOutWareHouse", "BPTHOutWareHoseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "BPTHOutWareHouse", datas, rely, "", "BPTHOutWareHoseID", opt.getConnect());
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
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String bpTHOutWareHoseID = arrayParam.getJSONObject(0).getString("bpTHOutWareHoseID");
                            String mainTabWhereSql = "BPTHOutWareHoseID = '" + bpTHOutWareHoseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "BPTHOutWareHouse", "*", mainTabWhereSql, 11, 1, "BPTHOutWareHoseID", 0, opt.getConnect());
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
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "BPTHOutWareHouseList", "update", opt.getConnect()).get(0);
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
                            //json = queryOperate("com.cn.bean.out.", "view", "BPTHOutWareHouseList", "BPTHOutWareHoseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("bpTHOutWareHoseID");
                            String mainTabWhereSql = "BPTHOutWareHoseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "BPTHOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "BPTHOutWareHoseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                BPTHOutWareHouse bPTHOutWareHouse = (BPTHOutWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("待检品") == 0) {
                                    List<Object> djp = commonController.proceduceQuery("spGetKFJCDjpListForBPTH", proParams, "com.cn.bean.pro.KFJCDJPForBPTH", opt.getConnect());
                                    if (djp != null && djp.size() > 0) {
                                        for (Object obj : djp) {
                                            KFJCDJPForBPTH bpth = (KFJCDJPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfDjpJCAmount());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("良品") == 0) {
                                    List<Object> lp = commonController.proceduceQuery("spGetKFJCLpListForBPTH", proParams, "com.cn.bean.pro.KFJCLPForBPTH", opt.getConnect());
                                    if (lp != null && lp.size() > 0) {
                                        for (Object obj : lp) {
                                            KFJCLPForBPTH bpth = (KFJCLPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfJCLpAmount());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("不良品") == 0) {
                                    List<Object> blp = commonController.proceduceQuery("spGetKFJCBLpListForBPTH", proParams, "com.cn.bean.pro.KFJCBLPForBPTH", opt.getConnect());
                                    if (blp != null && blp.size() > 0) {
                                        for (Object obj : blp) {
                                            KFJCBLPForBPTH bpth = (KFJCBLPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfJCBLpAmount());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("返修品") == 0) {
                                    List<Object> fxp = commonController.proceduceQuery("spGetKFJCFXpListForBPTH", proParams, "com.cn.bean.pro.KFJCFXPForBPTH", opt.getConnect());
                                    if (fxp != null && fxp.size() > 0) {
                                        for (Object obj : fxp) {
                                            KFJCFXPForBPTH bpth = (KFJCFXPForBPTH) obj;
                                            limitMap.put(bpth.getPartCode(), bpth.getKfJCFXpAmount());
                                        }
                                    }
                                }

                                Class objClass = Class.forName("com.cn.bean.out." + "BPTHOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "BPTHOutWareHouseList", "*", whereSql, pageSize, pageIndex, "BPTHOutWareHoseID", 0, opt.getConnect());

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
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
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
                            //json = queryOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "thAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "退货数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("待检品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCDjpListForBPTH", proParams, "com.cn.bean.pro.KFJCDJPForBPTH", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        for (Object obj : list) {
                                            KFJCDJPForBPTH bpth = (KFJCDJPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + bpth.getPartCode()), PartBaseInfo.class);
                                            bpth.setPartID(baseInfo.getPartID());
                                            bpth.setPartName(baseInfo.getPartName());

//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            bpth.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfDjpJCAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCDJPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCLpListForBPTH", proParams, "com.cn.bean.pro.KFJCLPForBPTH", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        for (Object obj : list) {
                                            KFJCLPForBPTH bpth = (KFJCLPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + bpth.getPartCode()), PartBaseInfo.class);
                                            bpth.setPartID(baseInfo.getPartID());
                                            bpth.setPartName(baseInfo.getPartName());

//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            bpth.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCLpAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCLPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("不良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCBLpListForBPTH", proParams, "com.cn.bean.pro.KFJCBLPForBPTH", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        for (Object obj : list) {
                                            KFJCBLPForBPTH fxck = (KFJCBLPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxck.getPartCode()), PartBaseInfo.class);
                                            fxck.setPartID(baseInfo.getPartID());
                                            fxck.setPartName(baseInfo.getPartName());

//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }
                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCBLpAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCBLPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("thPartState").compareTo("返修品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCFXpListForBPTH", proParams, "com.cn.bean.pro.KFJCFXPForBPTH", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        for (Object obj : list) {
                                            KFJCFXPForBPTH fxck = (KFJCFXPForBPTH) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxck.getPartCode()), PartBaseInfo.class);
                                            fxck.setPartID(baseInfo.getPartID());
                                            fxck.setPartName(baseInfo.getPartName());

//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }
                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCFXpAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCFXPForBPTH", keys, keysName, keysWidth, fieldsName, target, rely);
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
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "BPTHOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "BPTHOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "bpTHOutWareHoseID") + "]", "com.cn.bean.move.", "BPTHOutWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "BPTHOutWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "BPTHOutWareHouse", "delete", opt.getConnect());
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 入库单下存在明细数据!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
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
                            //json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "FZInWareHouse", datas, rely, "", "FZInWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"分装入库单号\\", ",@FZRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "FZInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"分装入库单号\\", ",@SJCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            String fzInWareHouseID = JSONObject.parseObject(rely).getString("fzInWareHouseID");
                            String mainTabWhereSql = "FZInWareHouseID = '" + fzInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FZInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "SJOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                FZInWareHouse sJOutWareHouse = (FZInWareHouse) list.get(0);

                                /*
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> djpList = commonController.proceduceQuery("spGetKFJCDjpListForSJCK", proParams, "com.cn.bean.pro.KFJCDJPForSJCK", opt.getConnect());
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                for (Object obj : djpList) {
                                    KFJCDJPForSJCK sjck = (KFJCDJPForSJCK) obj;
                                    limitMap.put(sjck.getPartCode(), sjck.getKfJCDjpAmount());
                                }
                                */

                                Class objClass = Class.forName("com.cn.bean.out." + "FZInWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "FZInWareHouseList", "*", whereSql, pageSize, pageIndex, "FZInWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    /*
                                    for (Object obj : detailList) {
                                        FZInWareHouseList sj = (FZInWareHouseList) obj;
                                        //sj.setInboundAmount(Integer.valueOf(limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                        sj.setInboundAmount(Integer.valueOf((null == limitMap.get(sj.getPartCode())) ? "0" : limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                    }
                                    */
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(sJOutWareHouse.getFzRKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
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
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "FZInWareHouse", "FZInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "FZInWareHouse", "FZInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "FZInWareHouse", "delete", opt.getConnect());
//                                System.out.println("res:" + Arrays.toString(delResult.toArray()));
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 入库单下存在明细数据!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fzRKAuditStaffName", session.getAttribute("user"));
                            obj.put("fzRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "FZInWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "FZInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "FZInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "fzInWareHouseID") + "]", "com.cn.bean.out.", "FZInWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.out.", "FZInWareHouseList", update, "", delete, "data");
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "FZInWareHouseList", null);
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
                            //json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "FZInWareHouse", datas, rely, "", "FZInWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"分装入库单号\\", ",@FZRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "FZInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"分装入库单号\\", ",@SJCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            String fzInWareHouseID = JSONObject.parseObject(rely).getString("fzInWareHouseID");
                            String mainTabWhereSql = "FZInWareHouseID = '" + fzInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.out.", "FZInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "SJOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                FZInWareHouse sJOutWareHouse = (FZInWareHouse) list.get(0);

                                /*
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> djpList = commonController.proceduceQuery("spGetKFJCDjpListForSJCK", proParams, "com.cn.bean.pro.KFJCDJPForSJCK", opt.getConnect());
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                for (Object obj : djpList) {
                                    KFJCDJPForSJCK sjck = (KFJCDJPForSJCK) obj;
                                    limitMap.put(sjck.getPartCode(), sjck.getKfJCDjpAmount());
                                }
                                */

                                Class objClass = Class.forName("com.cn.bean.out." + "FZInWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.out.", "FZInWareHouseList", "*", whereSql, pageSize, pageIndex, "FZInWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    /*
                                    for (Object obj : detailList) {
                                        FZInWareHouseList sj = (FZInWareHouseList) obj;
                                        //sj.setInboundAmount(Integer.valueOf(limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                        sj.setInboundAmount(Integer.valueOf((null == limitMap.get(sj.getPartCode())) ? "0" : limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                    }
                                    */
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(sJOutWareHouse.getFzRKAuditTime())) {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
                                    } else {
                                        buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
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
                            json = queryOnDateOperate("com.cn.bean.out.", "view", "FZInWareHouse", "FZInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "FZInWareHouse", "FZInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.out.", "FZInWareHouse", "delete", opt.getConnect());
//                                System.out.println("res:" + Arrays.toString(delResult.toArray()));
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "删除操作成功!", null);
                                } else if (delResult.get(0) == 547) {
                                    int count = 0;
                                    for (int i = 1; i < delResult.size(); i++) {
                                        if (delResult.get(i) != 1) {
                                            count++;
                                        }
                                    }
                                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除, 入库单下存在明细数据!", null);
                                } else {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                }
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fzRKAuditStaffName", session.getAttribute("user"));
                            obj.put("fzRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.out.", "FZInWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "FZInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "FZInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "fzInWareHouseID") + "]", "com.cn.bean.out.", "FZInWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.out.", "FZInWareHouseList", update, "", delete, "data");
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "FZInWareHouseList", null);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="备货确认">
                case "备货确认": {
                    String whereCase = "JHStatus = 0 or JHStatus = -1";
                    switch (operation) {
                        case "create": {
                            //json = createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", "JHOutWareHouseID", opt.getConnect());
                            //json = createOperateWithFilter(10, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", whereCase, "JHOutWareHouseID", opt.getConnect());
                            if (isHistory == 0)
                                json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHOutWareHouseID", opt.getConnect());
                            if (isHistory == 1)
                                json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, "", "JHOutWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_detail": {
                            String detailCase = "FinishTime is null";
                            if (isHistory == 0) {
                                json = queryOperateWithFilter("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", datas, rely, detailCase, false, opt.getConnect(), pageSize, pageIndex);
                            }
                            if (isHistory == 1) {
                                json = queryOperateWithFilter("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", datas, rely, "", false, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.out.", "view", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 0)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                            int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "BHProgressList", "update", opt.getConnect()).get(0);
                            if (result == 0) {
                                JSONObject checkObj = new JSONObject();
                                checkObj.put("JHOutWareHouseID", "string," + arrayParam.getJSONObject(0).getString("jhOutWareHouseID"));
                                checkObj.put("SupplierID", "string," + arrayParam.getJSONObject(0).getString("supplierID"));
                                checkObj.put("PartCode", "string," + arrayParam.getJSONObject(0).getString("partCode"));
                                
                                System.out.println("json:" + checkObj.toJSONString());
                                commonController.proceduceForUpdate("tbBHJHFinishedCheck", checkObj, opt.getConnect());
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

                //<editor-fold desc="领货确认">
                case "领货确认": {
                    String whereCase = "exists(select * from tblBHProgressList bhList where viewJHOutWareHouseList.JHOutWareHouseID = bhList.JHOutWareHouseID" +
                            " and viewJHOutWareHouseList.SupplierID = bhList.SupplierID and viewJHOutWareHouseList.PartCode = bhList.PartCode and bhList.BHTime is not null)";
                    String whereCase1 = whereCase + " and (JHStatus <> 2)";
                    switch (operation) {
                        case "create": {
                            //json = createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", "JHOutWareHouseID", opt.getConnect());
                            if (isHistory == 0)
                                json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase1, "JHOutWareHouseID", opt.getConnect());
                            if (isHistory == 1)
                                json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHOutWareHouseID", opt.getConnect());
                            //System.out.println("json:" + json);
                            break;
                        }
                        case "request_detail": {
                            String detailCase = "FinishTime is null";
                            if (isHistory == 0)
                                json = queryOperateWithFilter("com.cn.bean.out.", "view", "LHProgressList", "JHOutWareHouseID", datas, rely, detailCase, false, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOperateWithFilter("com.cn.bean.out.", "view", "LHProgressList", "JHOutWareHouseID", datas, rely, "", false, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            if (isHistory == 0)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
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
                            int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "LHProgressList", "update", opt.getConnect()).get(0);
                            if (result == 0) {
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

                //<editor-fold desc="配送确认">
                case "配送确认": {
                    String whereCase = "exists(select * from tblLHProgressList lhList where viewJHOutWareHouseList.JHOutWareHouseID = lhList.JHOutWareHouseID" +
                            " and viewJHOutWareHouseList.SupplierID = lhList.SupplierID and viewJHOutWareHouseList.PartCode = lhList.PartCode and lhList.LHTime is not null)";
                    String whereCase1 = whereCase + " and JHStatus <> 3";
                    switch (operation) {
                        case "create": {
                            if (isHistory == 0)
                                json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase1, "JHOutWareHouseID", opt.getConnect());
                            if (isHistory == 1)
                                json = createOperateOnDate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", datas, rely, whereCase, "JHOutWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_detail": {
                            String detailCase = "FinishTime is null";
                            if (isHistory == 0)
                                json = queryOperateWithFilter("com.cn.bean.out.", "view", "SXProgressList", "JHOutWareHouseID", datas, rely, detailCase, false, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOperateWithFilter("com.cn.bean.out.", "view", "SXProgressList", "JHOutWareHouseID", datas, rely, "", false, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            if (isHistory == 0)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            if (isHistory == 1)
                                json = queryOnDateOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            JSONArray updateArray = new JSONArray();
                            for (int i = 0; i < arrayParam.size(); i++) {
                                JSONObject obj = new JSONObject();
                                obj.put("sxTime", Units.getNowTime());
                                updateArray.add(obj);
                                updateArray.add(arrayParam.getJSONObject(i));
                            }
                            int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.out.", "SXProgressList", "update", opt.getConnect()).get(0);
                            if (result == 0) {
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

                //<editor-fold desc="良品库存">
                case "良品库存": {
                    switch (operation) {
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
                            list.clear();
                            list.addAll(map.values());
                            if (list != null && list.size() > 0) {
                                Collections.sort(list, new Comparator<LPKCListInfo>() {
                                    @Override
                                    public int compare(LPKCListInfo o1, LPKCListInfo o2) {
                                        return o1.getSupplierID().compareTo(o2.getSupplierID());
                                    }
                                });
                                StringBuffer buffer = new StringBuffer(result);
                                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                                result = buffer.toString();
                            }
                            json = Units.objectToJson(0, "", result);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="排序卡打印">
                case "排序卡打印": {
                    switch (operation) {
                        case "create": {
                            json = Units.objectToJson(0, "", "");
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

                //<editor-fold desc="下线信息">
                case "下线信息": {
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

    private String createOperateOnDate(int pageSize, String type, String jsonPackagePath, String beanPackage, String tableName, String datas,
            String rely, String whereCase, String orderField, Connection conn) throws Exception {
        String json;
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
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);
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
            buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
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
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);

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
            buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
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
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);
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
            buffer.append(",\"counts\":").append(method.invoke(null, null));
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
        Method method = objClass.getMethod("getRecordCount", null);
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
        buffer.append(",\"counts\":").append(method.invoke(null, null));
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
