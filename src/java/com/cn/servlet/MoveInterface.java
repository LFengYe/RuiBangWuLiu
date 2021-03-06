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
import com.cn.bean.PartBaseInfo;
import com.cn.bean.move.FXInWareHouse;
import com.cn.bean.move.FXInWareHouseList;
import com.cn.bean.move.FXOutWareHouse;
import com.cn.bean.move.FXOutWareHouseList;
import com.cn.bean.pro.KFJCBLPForFXCK;
import com.cn.bean.pro.KFJCFXPForFXRK;
import com.cn.bean.pro.KFJCLPForFXCK;
import com.cn.controller.CommonController;
import com.cn.controller.CommonOperate;
import com.cn.controller.InterfaceController;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
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
public class MoveInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MoveInterface.class);

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
            System.out.println(subUri + ",params:" + params);
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

            Employee employee = null;
            Customer curCustomer = null;
            if (session.getAttribute("loginType").toString().compareTo("employeeLogin") == 0) {
                employee = (Employee) session.getAttribute("employee");
            }
            if (session.getAttribute("loginType").toString().compareTo("customerLogin") == 0) {
                curCustomer = (Customer) session.getAttribute("employee");
            }

            switch (module) {
                /**
                 * ***************************************部品移库管理**************************************
                 */
                //<editor-fold desc="部品移库管理">
                //<editor-fold desc="报检信息_平板上是否需要区分库管员???">
                case "报检信息": {
                    String whereCase1 = "DJInWareHouseID in (select DJInWareHouseID from tblDJInWareHouse where DJRKAuditTime is not null)";
                    String whereCase = "(InspectionTime is null) and " + whereCase1;
                    switch (operation) {
                        case "create": {
                            if (isHistory == 0) {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", whereCase, "DJInWareHouseID", DatabaseOpt.DATA);
                                json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", datas, rely, whereCase, "DJInWareHouseID", dataType);
                            } else {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", whereCase1, "DJInWareHouseID", DatabaseOpt.DATA);
                                json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", datas, rely, whereCase1, "DJInWareHouseID", dataType);
                            }
                            break;
                        }
                        case "request_page": {
                            if (isHistory == 0) {
                                //json = interfaceController.queryOperateWithFilter("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase, true, DatabaseOpt.DATA, pageSize, pageIndex);
                                json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            } else {
                                //json = interfaceController.queryOperateWithFilter("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, DatabaseOpt.DATA, pageSize, pageIndex);
                                json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, dataType, pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            } else {
                                json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, dataType, pageSize, pageIndex);
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

                            Class objClass = Class.forName("com.cn.bean.move." + "DJInWareHouseList");
                            Method method = objClass.getMethod("getRecordCount", new Class[0]);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("view", "com.cn.bean.move.", "DJInWareHouseList", "*", whereSql, pageSize, pageIndex, "DJInWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                com.cn.bean.move.DJInWareHouseList djInWareHouse = (com.cn.bean.move.DJInWareHouseList) list.get(0);
                                if (Units.strIsEmpty(djInWareHouse.getInspectionTime())) {
                                    json = Units.objectToJson(0, "", "{\"readOnly\":false}");
                                } else {
                                    json = Units.objectToJson(0, "", "{\"readOnly\":true}");
                                }
                            } else {
                                json = Units.objectToJson(-1, "输入参数错误!", null);
                            }
                            break;
                        }
                        case "inspection": {
                            JSONObject obj = JSONObject.parseObject(item);
                            obj.put("inspectorName", session.getAttribute("user"));
                            obj.put("inspectionTime", Units.getNowTime());
                            String inspectionResult = obj.getString("inspectionResult");
                            String updateStr = null;
                            if (inspectionResult.compareTo("合格") == 0) {
                                obj.put("partState", "良品");
                                updateStr = "[{\"partState\":\"良品\"},{\"djInWareHouseID\":\"" + obj.getString("djInWareHouseID") + "\",\"partCode\":\"" + obj.getString("partCode") + "\",\"supplierID\":\"" + obj.getString("supplierID") + "\"}]";
                            }
                            if (inspectionResult.compareTo("不合格") == 0) {
                                obj.put("partState", "不良品");
                                updateStr = "[{\"partState\":\"不良品\"},{\"djInWareHouseID\":\"" + obj.getString("djInWareHouseID") + "\",\"partCode\":\"" + obj.getString("partCode") + "\",\"supplierID\":\"" + obj.getString("supplierID") + "\"}]";
                            }

                            int result = commonController.dataBaseOperate("[" + obj.toJSONString() + "]", "com.cn.bean.move.", "InspectionReportList", "add", DatabaseOpt.DATA).get(0);
                            if (result == 0) {
                                commonController.dataBaseOperate(updateStr, "com.cn.bean.in.", "DJInWareHouseList", "update", DatabaseOpt.DATA);
                                json = Units.objectToJson(0, "检验成功!", obj.toJSONString());
                            } else if (result == 547) {
                                json = Units.objectToJson(-1, "不合格原因不能为空!", null);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "已经检验, 不能重复检验!", null);
                            } else {
                                json = Units.objectToJson(-1, "检验失败!", null);
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
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            String whereCase = "";
                            if (operateType.compareTo("app") == 0) {
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    whereCase = "exists (select * from tblFXOutWareHouseList list left join viewGYSPartContainerInfo gys"
                                            + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                            + " where list.FXOutWareHouseID = viewFXOutWareHouse.FXOutWareHouseID"
                                            + " and list.WareHouseManagername is null"
                                            + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "') and PartState <> '不良品'";
                                }

                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {
                                    whereCase = "exists (select * from tblFXOutWareHouseList list"
                                            + " where list.FXOutWareHouseID = viewFXOutWareHouse.FXOutWareHouseID"
                                            + " and list.WareHouseManagername is null)"
                                            + " and PartState = '不良品'";
                                }
                            }

                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "FXOutWareHouse", datas, rely, whereCase, "FXOutWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"返修出库单号\\", ",@FXCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/move/", "FXOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"返修出库单号\\", ",@FXCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fxCKAuditStaffName", session.getAttribute("user"));
                            obj.put("fxCKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.move.", "FXOutWareHouse", "update", DatabaseOpt.DATA);
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
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.move.", "FXOutWareHouse", "FXOutWareHouseID", "fxCKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String fxOutWareHouseID = arrayParam.getJSONObject(0).getString("fxOutWareHouseID");
                            String mainTabWhereSql = "FXOutWareHouseID = '" + fxOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.move.", "FXOutWareHouse", "*", mainTabWhereSql, 11, 1, "FXOutWareHouseID", 0, DatabaseOpt.DATA);
                            if (list != null && list.size() > 0) {
                                FXOutWareHouse sJOutWareHouse = (FXOutWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getFxCKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.move.", "FXOutWareHouseList", "update", DatabaseOpt.DATA).get(0);
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
                                obj.put("fxCKAuditStaffName", session.getAttribute("user"));
                                obj.put("fxCKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.move.", "FXOutWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            }
                            String fxOutWareHouseID = JSONObject.parseObject(rely).getString("fxOutWareHouseID");
                            String mainTabWhereSql = "FXOutWareHouseID = '" + fxOutWareHouseID + "'";
                            //System.out.println("mainTabWhereSql:" + mainTabWhereSql);
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.move.", "FXOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "FXOutWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                FXOutWareHouse fXOutWareHouse = (FXOutWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("良品") == 0) {
                                    List<Object> lpfxc = commonController.proceduceQuery("spGetKFJCLPListForFXCK", proParams, "com.cn.bean.pro.KFJCLPForFXCK", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                    if (lpfxc != null && lpfxc.size() > 0) {
                                        for (Object obj : lpfxc) {
                                            KFJCLPForFXCK fxck = (KFJCLPForFXCK) obj;
                                            limitMap.put(fxck.getPartCode(), fxck.getKfJCLp());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("不良品") == 0) {
                                    List<Object> blpfxc = commonController.proceduceQuery("spGetKFJCBLPListForFXCK", proParams, "com.cn.bean.pro.KFJCBLPForFXCK", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                    if (blpfxc != null && blpfxc.size() > 0) {
                                        for (Object obj : blpfxc) {
                                            KFJCBLPForFXCK fxck = (KFJCBLPForFXCK) obj;
                                            limitMap.put(fxck.getPartCode(), fxck.getBlPAmount());
                                        }
                                    }
                                }

                                Class objClass = Class.forName("com.cn.bean.move." + "FXOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                
                                String detailWhereCase = "";
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewFXOutWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewFXOutWareHouseList.PartCode"
                                        + " and viewFXOutWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                }
                                
                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {
                                    detailWhereCase = "WareHouseManagername is null";
                                }
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.move.", "FXOutWareHouseList", "*", whereSql, pageSize, pageIndex, "FXOutWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));

                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        FXOutWareHouseList fxck = (FXOutWareHouseList) obj;
//                                        fxck.setFxAmount(Integer.valueOf(limitMap.get(fxck.getPartCode())) + fxck.getFxAmount());
                                        int fxAmount = Integer.valueOf((null == limitMap.get(fxck.getPartCode())) ? "0" : limitMap.get(fxck.getPartCode())) + fxck.getFxAmount();
                                        fxck.setFxAmount(fxAmount);
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(fXOutWareHouse.getFxCKAuditTime())) {
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
                            //json = interfaceController.queryOperate("com.cn.bean.move.", "view", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "fxAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "返修数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCLPListForFXCK", proParams, "com.cn.bean.pro.KFJCLPForFXCK", DatabaseOpt.DATA);
                                    if (list != null && list.size() > 0) {
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            KFJCLPForFXCK fxck = (KFJCLPForFXCK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            fxck.setPartID(baseInfo.getPartID());
                                            fxck.setPartName(baseInfo.getPartName());

                                            filterList.add(fxck);
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCLp"};
                                        json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCLPForFXCK", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("不良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCBLPListForFXCK", proParams, "com.cn.bean.pro.KFJCBLPForFXCK", DatabaseOpt.DATA);
                                    if (list != null && list.size() > 0) {
                                        List<Object> filterList = new ArrayList<>();
                                        for (Object obj : list) {
                                            if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                                continue;
                                            }
                                            KFJCBLPForFXCK fxck = (KFJCBLPForFXCK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxck.getPartCode().toLowerCase()), PartBaseInfo.class);
                                            fxck.setPartID(baseInfo.getPartID());
                                            fxck.setPartName(baseInfo.getPartName());

                                            filterList.add(fxck);
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }
                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "blPAmount"};
                                        json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCBLPForFXCK", keys, keysName, keysWidth, fieldsName, target, rely);
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
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXOutWareHouse", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXOutWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "fxOutWareHouseID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "fxOutWareHouseID") + "]", "com.cn.bean.move.", "FXOutWareHouse", "delete", DatabaseOpt.DATA);
                                        }
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = interfaceController.submitOperate("com.cn.bean.move.", "FXOutWareHouseList", update, add, delete, "data");
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
                            if (employee == null) {
                                json = Units.objectToJson(-99, "未登陆", null);
                                break;
                            }
                            String whereCase = "";
                            if (operateType.compareTo("app") == 0) {
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    whereCase = "exists (select * from tblFXInWareHouseList list left join viewGYSPartContainerInfo gys"
                                            + " on list.SupplierID = gys.SupplierID and list.PartCode = gys.PartCode"
                                            + " where list.FXInWareHouseID = viewFXInWareHouse.FXInWareHouseID"
                                            + " and list.WareHouseManagername is null"
                                            + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "') and PartState <> '不良品'";
                                }

                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {
                                    whereCase = "exists (select * from tblFXInWareHouseList list"
                                            + " where list.FXInWareHouseID = viewFXInWareHouse.FXInWareHouseID"
                                            + " and list.WareHouseManagername is null)"
                                            + " and PartState = '不良品'";
                                }
                            }

                            json = interfaceController.createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "FXInWareHouse", datas, rely, whereCase, "FXInWareHouseID", dataType);
                            json = Units.insertStr(json, "\\\"返修入库单号\\", ",@FXRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/move/", "FXInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"返修入库单号\\", ",@FXRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String fxInWareHouseID = arrayParam.getJSONObject(0).getString("fxInWareHouseID");
                            String mainTabWhereSql = "FXInWareHouseID = '" + fxInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.move.", "FXInWareHouse", "*", mainTabWhereSql, 11, 1, "FXInWareHouseID", 0, DatabaseOpt.DATA);
                            if (list != null && list.size() > 0) {
                                FXInWareHouse fXInWareHouse = (FXInWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(fXInWareHouse.getFxRKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.move.", "FXInWareHouseList", "update", DatabaseOpt.DATA).get(0);
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
                                obj.put("fxRKAuditStaffName", session.getAttribute("user"));
                                obj.put("fxRKAuditTime", Units.getNowTime());
                                String auditInfo = "[" + obj.toJSONString() + "," + rely + "]";
                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.move.", "FXInWareHouse", "update", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            }
                            //json = interfaceController.queryOperate("com.cn.bean.move.", "view", "FXInWareHouseList", "FXInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("fxInWareHouseID");
                            String mainTabWhereSql = "FXInWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.move.", "FXInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "FXInWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                            if (list != null && list.size() > 0) {
                                FXInWareHouse fXInWareHouse = (FXInWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                List<Object> fxrkList = commonController.proceduceQuery("spGetKFJCFxpListForFXRK", proParams, "com.cn.bean.pro.KFJCFXPForFXRK", (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));
                                if (fxrkList != null && fxrkList.size() > 0) {
                                    for (Object obj : fxrkList) {
                                        KFJCFXPForFXRK fxrk = (KFJCFXPForFXRK) obj;
                                        limitMap.put(fxrk.getPartCode(), String.valueOf(fxrk.getFxPAmount()));
                                    }
                                }

                                Class objClass = Class.forName("com.cn.bean.move." + "FXInWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", new Class[0]);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                 
                                String detailWhereCase = "";
                                if (employee.getEmployeeTypeCode().compareTo("5") == 0) {
                                    detailWhereCase = "exists(select * from viewGYSPartContainerInfo gys where"
                                        + " gys.SupplierID = viewFXInWareHouseList.SupplierID"
                                        + " and gys.PartCode = viewFXInWareHouseList.PartCode"
                                        + " and viewFXInWareHouseList.WareHouseManagername is null"
                                        + " and gys.WareHouseManagerName = '" + employee.getEmployeeName() + "')";
                                }
                                
                                if (employee.getEmployeeTypeCode().compareTo("9") == 0) {
                                    detailWhereCase = "WareHouseManagername is null";
                                }
                                
                                if (!Units.strIsEmpty(whereSql)) {
                                    detailWhereCase = whereSql + " and " + detailWhereCase;
                                }
                                whereSql = (operateType.compareTo("app") == 0) ? (detailWhereCase) : (whereSql);

                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.move.", "FXInWareHouseList", "*", whereSql, pageSize, pageIndex, "FXInWareHouseID", 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA));

                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        FXInWareHouseList fxck = (FXInWareHouseList) obj;
                                        //fxck.setFxCKAmount(Integer.valueOf(limitMap.get(fxck.getPartCode())) + fxck.getFxRKAmount());
                                        int fxAmount = Integer.valueOf((null == limitMap.get(fxck.getPartCode())) ? "0" : limitMap.get(fxck.getPartCode())) + fxck.getFxRKAmount();
                                        fxck.setFxCKAmount(fxAmount);
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(fXInWareHouse.getFxRKAuditTime())) {
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
                            //json = interfaceController.queryOperate("com.cn.bean.move.", "view", "FXInWareHouse", "FXInWareHouseID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "FXInWareHouse", "FXInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "view", "FXInWareHouse", "FXInWareHouseID", datas, rely, "", true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fxRKAuditStaffName", session.getAttribute("user"));
                            obj.put("fxRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.move.", "FXInWareHouse", "update", DatabaseOpt.DATA);
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
                                json = operate.batchDeleteOperate(delete, "com.cn.bean.move.", "FXInWareHouse", "FXInWareHouseID", "fxRKAuditTime");
                            } else {
                                json = Units.objectToJson(-1, "未选中数据!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXInWareHouse", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXInWareHouseList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "fxInWareHouseID"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "fxInWareHouseID") + "]", "com.cn.bean.move.", "FXInWareHouse", "delete", DatabaseOpt.DATA);
                                        }
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = interfaceController.submitOperate("com.cn.bean.move.", "FXInWareHouseList", update, add, delete, "data");
                            }
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "fxAmount", "fxRKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "返修数量", ""};
                                int[] keysWidth = {20, 20, 20, 20, 20, 0};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCFxpListForFXRK", proParams, "com.cn.bean.pro.KFJCFXPForFXRK", DatabaseOpt.DATA);
                                if (list != null && list.size() > 0) {
                                    List<Object> filterList = new ArrayList<>();
                                    for (Object obj : list) {
                                        if (!Units.strIsEmpty(datas) && !JSONObject.toJSONString(obj).toLowerCase().contains(datas.toLowerCase())) {
                                            continue;
                                        }
                                        KFJCFXPForFXRK fxrk = (KFJCFXPForFXRK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxrk.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        fxrk.setPartID(baseInfo.getPartID());
                                        fxrk.setPartName(baseInfo.getPartName());

                                        filterList.add(fxrk);
//                                        PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                        fxrk.setWareHouseManagerName(category.getWareHouseManagerName());
                                    }

                                    String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "fxPAmount", "fxPAmount"};
                                    json = interfaceController.getSpecialTableJsonStr(filterList, "com.cn.bean.pro.KFJCFXPForFXRK", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="库房调账">
                case "库房调账": {
                    String whereCase = "TZYMonth in (select TZYMonth from tblKFAdjustAccountList)";
                    switch (operation) {
                        case "create": {
                            //json = createOperateWithFilter(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "AdjustAccount", "TZYMonth in (select TZYMonth from tblXCAdjustAccountList)", "TZYMonth", DatabaseOpt.DATA);
                            json = interfaceController.createOperateOnDate(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "AdjustAccount", datas, rely, whereCase, "TZYMonth", dataType);
                            json = Units.insertStr(json, "\\\"调帐编号\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/move/", "AdjustAccount.json"));
                            json = Units.insertStr(json, "\\\"调帐编号\\", ",@" + Units.getNowTimeNoSeparator());
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

                            json = interfaceController.queryOperate("com.cn.bean.move.", "view", "KFAdjustAccountList", "TZYMonth", datas, rely, true, (dataType.compareToIgnoreCase("isHis") == 0) ? (DatabaseOpt.HIS) : (DatabaseOpt.DATA), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            //json = interfaceController.queryOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = interfaceController.queryOnDateOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, whereCase, true, dataType, pageSize, pageIndex);
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
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "AdjustAccount", "add", DatabaseOpt.DATA).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "KFAdjustAccountList", "add", DatabaseOpt.DATA).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, "tzYMonth"))) {
                                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "tzYMonth") + "]", "com.cn.bean.move.", "AdjustAccount", "delete", DatabaseOpt.DATA);
                                        }
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = interfaceController.submitOperate("com.cn.bean.move.", "KFAdjustAccountList", update, add, delete, "data");
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
