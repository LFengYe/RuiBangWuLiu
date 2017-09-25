/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.AreaLedIPInfo;
import com.cn.bean.Customer;
import com.cn.bean.Employee;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartCategory;
import com.cn.bean.PartStore;
import com.cn.bean.PlatformRoleRight;
import com.cn.bean.app.JHOutWareHouseList;
import com.cn.bean.app.ProcessList;
import com.cn.bean.app.UnFinishAmount;
import com.cn.controller.CommonController;
import com.cn.controller.ProcessListController;
import com.cn.test.LedControl;
import com.cn.util.DatabaseOpt;
import com.cn.util.PushUnits;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
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
public class AppInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AppInterface.class);

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
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        String json = null;
        try {
            logger.info(subUri + ",params:" + params);
            JSONObject paramsJson = JSONObject.parseObject(params);
            String module = paramsJson.getString("module");
            String operation = paramsJson.getString("operation");

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
            //System.out.println("employee:" + JSONObject.toJSONString(employee));

            switch (module) {
                //<editor-fold desc="用户登陆模板">
                case "userLogin": {
                    switch (operation) {
                        //<editor-fold desc="员工登陆">
                        case "employeeLogin": {
                            String whereSql = "EmployeeName = '" + paramsJson.getString("username") + "'";
                            List<Object> res = commonController.dataBaseQuery("table", "com.cn.bean.", "Employee", "*", whereSql, 1, 1, "EmployeeName", 1, opt.getConnect());
                            String type = paramsJson.getString("type");
                            if (res != null && res.size() > 0) {
                                Employee loginEmp = (Employee) res.get(0);
                                if (employee.getEmployeePassword().compareTo(paramsJson.getString("password")) == 0) {
                                    session.setAttribute("user", paramsJson.getString("username"));
                                    session.setAttribute("employee", loginEmp);
                                    String whereCase = "RoleCode in ('" + employee.getEmployeeTypeCode() + "')";
                                    List<Object> roleRight = commonController.dataBaseQuery("table", "com.cn.bean.", "PlatformRoleRight", "*", whereCase, Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                    if (roleRight != null && roleRight.size() > 0) {
                                        ArrayList<String> roleRightList = new ArrayList<>();
                                        roleRight.stream().map((obj) -> (PlatformRoleRight) obj).forEach((right) -> {
                                            roleRightList.add(right.getRightCode());
                                        });

                                        JSONObject object = new JSONObject();
                                        object.put("roleRightList", roleRightList);
                                        object.put("employee", employee);
                                        json = Units.objectToJson(0, "登陆成功!", object);
                                    } else {
                                        json = Units.objectToJson(-1, "没有为用户分配权限!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "用户名或密码不正确!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "用户名不存在!", null);
                            }

                            break;
                        }
                        //</editor-fold>
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="未完成数量_unFinishAmount">
                case "unFinishAmount": {
                    JSONObject object = new JSONObject();
                    object.put("EmployeeName", "string," + employee.getEmployeeName());
                    object.put("EmployeeTypeCode", "int," + employee.getEmployeeTypeCode());
                    UnFinishAmount amount = (UnFinishAmount) commonController.proceduceQuery("tbGetUnFinishAmount", object, "com.cn.bean.app.UnFinishAmount", opt.getConnect()).get(0);
                    json = Units.objectToJson(0, "", amount);
                    break;
                }
                //</editor-fold>

                /**
                 * ***************************************部品出库管理**************************************
                 */
                //<editor-fold desc="部品出库管理">
                //<editor-fold desc="备货管理">
                case "备货管理": {
                    switch (operation) {
                        case "create": {
                            String employeeType = employee.getEmployeeType();
                            if (employeeType.compareTo("仓管员") == 0) {
                                JSONObject proParams = new JSONObject();
                                proParams.put("WareHouseManagerName", "string," + employee.getEmployeeName());
                                proParams.put("StartTime", "string," + paramsJson.getString("startTime"));
                                proParams.put("EndTime", "string," + paramsJson.getString("endTime"));
                                proParams.put("ZDCustomerID", "string," + paramsJson.getString("ZDCustomerID"));
                                proParams.put("IsFinished", "int," + paramsJson.getString("isFinished"));
                                StringBuffer buffer = new StringBuffer(Units.returnFileContext(path + "com/cn/json/app/", "JHOutWareHouseList.json"));
                                List<Object> list = commonController.proceduceQuery("tbGetJHCKBHListForKGY", proParams, "com.cn.bean.app.JHOutWareHouseList", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        JHOutWareHouseList wareHouseList = (JHOutWareHouseList) obj;
                                        Customer supplier = JSONObject.parseObject(RedisAPI.get("customer_" + wareHouseList.getSupplierID()), Customer.class);
                                        wareHouseList.setSupplierName(supplier.getCustomerAbbName());
                                        Customer zdCustomer = JSONObject.parseObject(RedisAPI.get("customer_" + wareHouseList.getZdCustomerID()), Customer.class);
                                        wareHouseList.setZdCustomerName(zdCustomer.getCustomerAbbName());
                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(wareHouseList.getSupplierID() + "_" + wareHouseList.getPartCode().toLowerCase()), GYSPartContainerInfo.class);
                                        wareHouseList.setOutboundContainerName(containerInfo.getOutboundContainerName());
                                        wareHouseList.setOutboundPackageAmount(containerInfo.getOutboundPackageAmount());
                                    }
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else if (employeeType.compareTo("备货员") == 0) {
                                JSONObject proParams = new JSONObject();
                                proParams.put("BHStaff", "string," + employee.getEmployeeName());
                                StringBuffer buffer = new StringBuffer(Units.returnFileContext(path + "com/cn/json/app/", "ProcessList.json"));
                                List<Object> list = commonController.proceduceQuery("tbGetJHCKBHListForBHY", proParams, "com.cn.bean.app.ProcessList", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    list.stream().map((obj) -> (ProcessList) obj).map((processList) -> {
                                        Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + processList.getSupplierID()), Customer.class);
                                        processList.setSupplierName(customer.getCustomerAbbName());
                                        return processList;
                                    }).forEachOrdered((processList) -> {
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + processList.getPartCode().toLowerCase()), PartBaseInfo.class);
                                        processList.setPartName(baseInfo.getPartName());
                                        processList.setAutoStylingName(baseInfo.getAutoStylingName());
                                    });
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "数据为空!", null);
                            }
                            break;
                        }
                        case "confirm": {
                            JHOutWareHouseList list = JSONObject.parseObject(params, JHOutWareHouseList.class);

                            String employeeType = employee.getEmployeeType();
                            if (employeeType.compareTo("仓管员") == 0) {
                                ProcessListController controller = new ProcessListController();
                                int result;
                                synchronized(this) {
                                    result = controller.bhConfirmForKGY(
                                        paramsJson.getString("jhOutWareHouseID"),
                                        paramsJson.getString("partCode"),
                                        paramsJson.getString("supplierID"),
                                        paramsJson.getString("inboundBatch"),
                                        paramsJson.getIntValue("jhStatus"),
                                        paramsJson.getString("jhOutWareHouseListRemark"));
                                }
                                if (result == 0) {
                                    PartStore partStore = JSONObject.parseObject(RedisAPI.get("partStore_" + paramsJson.getString("supplierID") + "_" + paramsJson.getString("partCode").toLowerCase()), PartStore.class);
                                    if (partStore == null) {
                                        logger.info(RedisAPI.get("partStore_" + paramsJson.getString("supplierID") + "_" + paramsJson.getString("partCode").toLowerCase()) + "缺少存放地址信息");
                                        json = Units.objectToJson(-1, "请补全存放地址!", null);
                                        break;
                                    }
                                    //logger.info(RedisAPI.get("partStore_" + paramsJson.getString("supplierID") + "_" + paramsJson.getString("partCode").toLowerCase()));
                                    //AreaLedIPInfo ledIPInfo = JSONObject.parseObject(RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()), AreaLedIPInfo.class);
                                    //logger.info(paramsJson.getString("supplierID") + "-" + paramsJson.getString("partCode") + "库房存放地址:" + partStore.getKfCFAddress() + "," + RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()));
                                    if (paramsJson.getIntValue("jhStatus") == 0
                                            || paramsJson.getIntValue("jhStatus") == -2) {//库管员开始计划
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                AreaLedIPInfo ledIPInfo = JSONObject.parseObject(RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()), AreaLedIPInfo.class);
                                                logger.info(paramsJson.getString("supplierID") + "-" + paramsJson.getString("partCode") + "库房存放地址:" + partStore.getKfCFAddress() + "," + RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()));
                                                if (ledIPInfo != null)
                                                    LedControl.setLedPlanList(list, ledIPInfo);
                                                
                                                JsonObject object = new JsonObject();
                                                object.addProperty("jhOutWareHouseID", paramsJson.getString("jhOutWareHouseID"));
                                                PushUnits.pushNotifationWithAlias(list.getBhEmployeeName(), partStore.getKfCFAddress() + "您有新的计划", "2", object);
                                            }
                                        }.start();
                                    }
                                    if (paramsJson.getIntValue("jhStatus") == -1) {//库管员确认完成
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                AreaLedIPInfo ledIPInfo = JSONObject.parseObject(RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()), AreaLedIPInfo.class);
                                                logger.info(paramsJson.getString("supplierID") + "-" + paramsJson.getString("partCode") + "库房存放地址:" + partStore.getKfCFAddress() + "," + RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()));
                                                if (ledIPInfo != null)
                                                    LedControl.setLedAreaCode(ledIPInfo);
                                            }
                                        }.start();
                                    }
                                    json = Units.objectToJson(0, "确认成功!", null);
                                } else if (result == 1) {
                                    json = Units.objectToJson(1, "备货未完成, 请填写备注信息!", null);
                                } else if (result == 2) {
                                    json = Units.objectToJson(2, "存在未完成计划!", null);
                                } else {
                                    json = Units.objectToJson(-1, "确认失败!", null);
                                }
                            } else if (employeeType.compareTo("备货员") == 0) {
                                ProcessListController controller = new ProcessListController();
                                int result;
                                synchronized(this) {
                                    result = controller.bhConfirmForBHY(
                                        paramsJson.getString("jhOutWareHouseID"),
                                        paramsJson.getString("partCode"),
                                        paramsJson.getString("supplierID"),
                                        paramsJson.getIntValue("packingNumber"),
                                        paramsJson.getString("inboundBatch"));
                                }
                                if (result == 0) {
                                    json = Units.objectToJson(0, "确认成功!", null);
                                } else if (result == 1) {
                                    JsonObject object = new JsonObject();
                                    GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(paramsJson.getString("supplierID") + "_" + paramsJson.getString("partCode").toLowerCase()), GYSPartContainerInfo.class);
                                    PartCategory partCategory = JSONObject.parseObject(RedisAPI.get("partCategory_" + containerInfo.getPartCategoryName()), PartCategory.class);
                                    PartStore partStore = JSONObject.parseObject(RedisAPI.get("partStore_" + paramsJson.getString("supplierID") + "_" + paramsJson.getString("partCode").toLowerCase()), PartStore.class);
                                    //AreaLedIPInfo ledIPInfo = JSONObject.parseObject(RedisAPI.get("ledIpInfo_" + partStore.getKfCFAddress().toLowerCase()), AreaLedIPInfo.class);
                                    object.addProperty("jhOutWareHouseID", paramsJson.getString("jhOutWareHouseID"));
                                    //logger.info("库管员:" + partCategory.getWareHouseManagerName());
                                    PushUnits.pushNotifationWithAlias(partCategory.getWareHouseManagerName(), partStore.getKfCFAddress() + "备货已完成", "1", object);
                                    /*
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            LedControl.setLedAreaCode(list.getPartCode(), list.getSupplierID());
                                        }
                                    }.start();
                                     */
                                    json = Units.objectToJson(0, "备货完成!", null);
                                } else {
                                    json = Units.objectToJson(-1, "确认失败!", null);
                                }
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
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("LHStaff", "string," + employee.getEmployeeName());
                            StringBuffer buffer = new StringBuffer(Units.returnFileContext(path + "com/cn/json/app/", "ProcessList.json"));
                            List<Object> list = commonController.proceduceQuery("tbGetJHCKLHList", proParams, "com.cn.bean.app.ProcessList", opt.getConnect());
                            if (list != null && list.size() > 0) {
                                list.stream().map((obj) -> (ProcessList) obj).map((processList) -> {
                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + processList.getSupplierID()), Customer.class);
                                    processList.setSupplierName(customer.getCustomerAbbName());
                                    return processList;
                                }).forEachOrdered((processList) -> {
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + processList.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    processList.setPartName(baseInfo.getPartName());
                                    processList.setAutoStylingName(baseInfo.getAutoStylingName());
                                });
                                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                                json = Units.objectToJson(0, "", buffer.toString());
                            } else {
                                json = Units.objectToJson(-1, "数据为空!", null);
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
                                    "com.cn.bean.out.", "LHProgressList", "update", opt.getConnect()).get(0);
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }

                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));

                            System.out.println("json:" + checkObj.toJSONString());
                            commonController.proceduceForUpdate("tbLHJHFinishedCheck", checkObj, opt.getConnect());
                            break;
                        }
                        case "finished": {
                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));

                            System.out.println("json:" + checkObj.toJSONString());
                            commonController.proceduceForUpdate("tbLHJHFinishedCheck", checkObj, opt.getConnect());
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="配送管理">
                case "配送管理": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            proParams.put("SXStaff", "string," + employee.getEmployeeName());
                            StringBuffer buffer = new StringBuffer(Units.returnFileContext(path + "com/cn/json/app/", "ProcessList.json"));
                            List<Object> list = commonController.proceduceQuery("tbGetJHCKSXList", proParams, "com.cn.bean.app.ProcessList", opt.getConnect());
                            if (list != null && list.size() > 0) {
                                list.stream().map((obj) -> (ProcessList) obj).map((processList) -> {
                                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + processList.getSupplierID()), Customer.class);
                                    processList.setSupplierName(customer.getCustomerAbbName());
                                    return processList;
                                }).forEachOrdered((processList) -> {
                                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + processList.getPartCode().toLowerCase()), PartBaseInfo.class);
                                    processList.setPartName(baseInfo.getPartName());
                                    processList.setAutoStylingName(baseInfo.getAutoStylingName());
                                });
                                buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(list, Units.features));
                                json = Units.objectToJson(0, "", buffer.toString());
                            } else {
                                json = Units.objectToJson(-1, "数据为空!", null);
                            }
                            break;
                        }
                        case "confirm": {
                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + paramsJson.getString("partCode").toLowerCase()), PartBaseInfo.class);
                            JSONObject updateObj = new JSONObject();
                            /*
                            if (baseInfo.getAssemblingStation().compareTo("3") == 0) {
                                updateObj.put("fzTime", Units.getNowTime());
                            } else {
                                updateObj.put("sxTime", Units.getNowTime());
                            }
                             */
                            updateObj.put("sxTime", Units.getNowTime());

                            JSONObject whereObj = new JSONObject();
                            whereObj.put("jhOutWareHouseID", paramsJson.getString("jhOutWareHouseID"));
                            whereObj.put("partCode", paramsJson.getString("partCode"));
                            whereObj.put("supplierID", paramsJson.getString("supplierID"));
                            whereObj.put("packingNumber", paramsJson.getIntValue("packingNumber"));
                            int result = commonController.dataBaseOperate("[" + updateObj.toJSONString() + "," + whereObj.toJSONString() + "]",
                                    "com.cn.bean.out.", "SXProgressList", "update", opt.getConnect()).get(0);
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
                            }

                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));
                            commonController.proceduceForUpdate("tbPSJHFinishedCheck", checkObj, opt.getConnect()).get(0);
                            break;
                        }
                        case "finished": {
                            JSONObject checkObj = new JSONObject();
                            checkObj.put("JHOutWareHouseID", "string," + paramsJson.getString("jhOutWareHouseID"));
                            checkObj.put("SupplierID", "string," + paramsJson.getString("supplierID"));
                            checkObj.put("PartCode", "string," + paramsJson.getString("partCode"));
                            checkObj.put("InboundBatch", "string," + paramsJson.getString("inboundBatch"));

                            System.out.println("json:" + checkObj.toJSONString());
                            commonController.proceduceForUpdate("tbPSJHFinishedCheck", checkObj, opt.getConnect()).get(0);
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
            //logger.info("json:" + json);
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
