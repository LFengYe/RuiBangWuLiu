/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.*;
import com.cn.controller.CommonController;
import com.cn.controller.CommonOperate;
import com.cn.controller.PlatformUserInfoController;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author LFeng
 */
public class DataInterface extends HttpServlet {

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

            Employee curEmployee = null;
            Customer curCustomer = null;
            if (session.getAttribute("employee") != null && session.getAttribute("loginType").toString().compareTo("employeeLogin") == 0) {
                curEmployee = (Employee) session.getAttribute("employee");
            }
            if (session.getAttribute("employee") != null && session.getAttribute("loginType").toString().compareTo("customerLogin") == 0) {
                curCustomer = (Customer) session.getAttribute("employee");
            }

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
                                Employee employee = (Employee) res.get(0);
                                if (employee.getEmployeePassword().compareTo(paramsJson.getString("password")) == 0) {
                                    session.setAttribute("user", paramsJson.getString("username"));
                                    session.setAttribute("loginType", "employeeLogin");
                                    session.setAttribute("employee", employee);
                                    //session.setAttribute("customer", null);
                                    String whereCase = "RoleCode in ('" + employee.getEmployeeTypeCode() + "')";
                                    List<Object> roleRight = commonController.dataBaseQuery("table", "com.cn.bean.", "PlatformRoleRight", "*", whereCase, Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                    if (roleRight != null && roleRight.size() > 0) {
                                        ArrayList<String> roleRightList = new ArrayList<>();
                                        roleRight.stream().map((obj) -> (PlatformRoleRight) obj).forEach((right) -> {
                                            roleRightList.add(right.getRightCode());
                                        });

                                        if (type.compareTo("pc") == 0) {
                                            /*根据角色权限信息生成用户菜单*/
                                            String menuJson = "{";
                                            SAXReader reader = new SAXReader();
                                            Document document = reader.read(new File(path + "menu.xml"));
                                            Element root = document.getRootElement();
                                            Iterator<Element> iterator = root.elementIterator();
                                            while (iterator.hasNext()) {
                                                menuJson += commonController.hasRight(iterator.next(), roleRightList);
                                            }
                                            if (menuJson.length() <= 1) {
                                                menuJson = null;
                                                json = Units.objectToJson(-1, "没有为用户分配权限!", menuJson);
                                            } else {
                                                menuJson = menuJson.substring(0, menuJson.length() - 1) + "}";
                                                json = Units.objectToJson(0, "登陆成功!", menuJson);
                                            }
                                        }
                                        if (type.compareTo("app") == 0) {
                                            /*根据角色权限信息生成用户菜单*/
                                            String menuJson = "{";
                                            SAXReader reader = new SAXReader();
                                            Document document = reader.read(new File(path + "menu.xml"));
                                            Element root = document.getRootElement();
                                            Iterator<Element> iterator = root.elementIterator();
                                            while (iterator.hasNext()) {
                                                menuJson += commonController.hasAppRight(iterator.next(), roleRightList);
                                            }
                                            if (menuJson.length() <= 1) {
                                                menuJson = null;
                                                json = Units.objectToJson(-1, "没有为用户分配权限!", menuJson);
                                            } else {
                                                menuJson = menuJson.substring(0, menuJson.length() - 1) + "}";
                                                JSONObject object = new JSONObject();
                                                object.put("menuJson", menuJson);
                                                object.put("employee", employee);
                                                json = Units.objectToJson(0, "登陆成功!", object);
                                            }
                                        }
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

                        //<editor-fold desc="平台登陆">
                        case "login": {
                            String type = paramsJson.getString("type");
                            PlatformUserInfoController controller = new PlatformUserInfoController();
                            int result = controller.userLogin(paramsJson.getString("username"), paramsJson.getString("password"));
                            switch (result) {
                                case 0:
                                    session.setAttribute("user", paramsJson.getString("username"));
                                    session.setAttribute("loginType", "login");
                                    //session.setAttribute("customer", null);
                                    session.setAttribute("employee", null);
                                    /*获取用户角色信息*/
                                    String whereCase = "UserLoginAccount = '" + paramsJson.getString("username") + "'";
                                    List<Object> userRole = commonController.dataBaseQuery("table", "com.cn.bean.", "PlatformUserRole", "*", whereCase, Integer.MAX_VALUE, 1, "UserLoginAccount", 0, opt.getConnectBase());
                                    if (userRole != null) {
                                        /*根据角色获取用户所有权限代码*/
                                        whereCase = "RoleCode in (";
                                        for (Object obj : userRole) {
                                            PlatformUserRole role = (PlatformUserRole) obj;
                                            whereCase += "'" + role.getRoleCode() + "',";
                                        }
                                        whereCase = whereCase.substring(0, whereCase.length() - 1);
                                        whereCase += ")";
                                        List<Object> roleRight = commonController.dataBaseQuery("table", "com.cn.bean.", "PlatformRoleRight", "*", whereCase, Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                        ArrayList<String> roleRightList = new ArrayList<>();
                                        roleRight.stream().map((obj) -> (PlatformRoleRight) obj).forEach((right) -> {
                                            roleRightList.add(right.getRightCode());
                                        });
                                        /*根据角色权限信息生成用户菜单*/
                                        String menuJson = "{";
                                        SAXReader reader = new SAXReader();
                                        Document document = reader.read(new File(path + "menu.xml"));
                                        Element root = document.getRootElement();
                                        Iterator<Element> iterator = root.elementIterator();
                                        while (iterator.hasNext()) {
                                            menuJson += commonController.hasRight(iterator.next(), roleRightList);
                                        }
                                        menuJson = menuJson.substring(0, menuJson.length() - 1) + "}";

                                        if (type.compareTo("pc") == 0) {
                                            json = Units.objectToJson(result, "登陆成功!", menuJson);
                                        }
                                        if (type.compareTo("app") == 0) {
                                            json = Units.objectToJson(result, "登陆成功!", roleRightList);
                                        }
                                    } else {
                                        json = Units.objectToJson(-1, "没有为用户分配角色!", null);
                                    }
                                    break;
                                case 1:
                                    json = Units.objectToJson(result, "用户名不存在!", null);
                                    break;
                                case 2:
                                    json = Units.objectToJson(result, "用户名或密码错误!", null);
                                    break;
                                case -1:
                                    json = Units.objectToJson(result, "登陆出错!", null);
                                    break;
                                default:
                                    json = Units.objectToJson(result, "服务器出错!", null);
                                    break;
                            }
                            break;
                        }
                        //</editor-fold>

                        //<editor-fold desc="供应商登陆">
                        case "customerLogin": {
                            String whereSql = "CustomerID = '" + paramsJson.getString("username") + "'";
                            List<Object> res = commonController.dataBaseQuery("view", "com.cn.bean.", "Customer", "*", whereSql, 1, 1, "CustomerID", 1, opt.getConnect());
                            String type = paramsJson.getString("type");
                            if (res != null && res.size() > 0) {
                                Customer customer = (Customer) res.get(0);
                                if (customer.getCustomerPassword().compareTo(paramsJson.getString("password")) == 0) {
                                    session.setAttribute("user", paramsJson.getString("username"));
                                    session.setAttribute("loginType", "customerLogin");
                                    //session.setAttribute("customer", customer);
                                    session.setAttribute("employee", customer);
                                    String whereCase = "RoleCode in ('" + customer.getCustomerRoleCode() + "')";
                                    List<Object> roleRight = commonController.dataBaseQuery("table", "com.cn.bean.", "PlatformRoleRight", "*", whereCase, Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                    if (roleRight != null && roleRight.size() > 0) {
                                        ArrayList<String> roleRightList = new ArrayList<>();
                                        roleRight.stream().map((obj) -> (PlatformRoleRight) obj).forEach((right) -> {
                                            roleRightList.add(right.getRightCode());
                                        });

                                        if (type.compareTo("pc") == 0) {
                                            /*根据角色权限信息生成用户菜单*/
                                            String menuJson = "{";
                                            SAXReader reader = new SAXReader();
                                            Document document = reader.read(new File(path + "menu.xml"));
                                            Element root = document.getRootElement();
                                            Iterator<Element> iterator = root.elementIterator();
                                            while (iterator.hasNext()) {
                                                menuJson += commonController.hasRight(iterator.next(), roleRightList);
                                            }
                                            if (menuJson.length() <= 1) {
                                                menuJson = null;
                                                json = Units.objectToJson(-1, "没有为用户分配权限!", menuJson);
                                            } else {
                                                menuJson = menuJson.substring(0, menuJson.length() - 1) + "}";
                                                json = Units.objectToJson(0, "登陆成功!", menuJson);
                                            }
                                        }
                                        if (type.compareTo("app") == 0) {
                                            /*根据角色权限信息生成用户菜单*/
                                            String menuJson = "{";
                                            SAXReader reader = new SAXReader();
                                            Document document = reader.read(new File(path + "menu.xml"));
                                            Element root = document.getRootElement();
                                            Iterator<Element> iterator = root.elementIterator();
                                            while (iterator.hasNext()) {
                                                menuJson += commonController.hasAppRight(iterator.next(), roleRightList);
                                            }

                                            if (menuJson.length() <= 1) {
                                                menuJson = null;
                                                json = Units.objectToJson(-1, "没有为用户分配权限!", menuJson);
                                            } else {
                                                menuJson = menuJson.substring(0, menuJson.length() - 1) + "}";
                                                JSONObject object = new JSONObject();
                                                object.put("menuJson", menuJson);
                                                object.put("employee", customer);
                                                json = Units.objectToJson(0, "登陆成功!", object);
                                            }
                                        }
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

                //<editor-fold desc="修改密码">
                case "修改密码": {
                    String oldPassword = paramsJson.getString("oldPassword");
                    String newPassword = paramsJson.getString("newPassword");
                    switch (session.getAttribute("loginType").toString()) {
                        case "employeeLogin": {
                            if (curEmployee != null && curEmployee.getEmployeePassword().compareTo(oldPassword) == 0) {
                                JSONArray updateParams = new JSONArray();
                                JSONObject setObj = new JSONObject();
                                setObj.put("employeePassword", newPassword);
                                updateParams.add(setObj);
                                JSONObject whereObj = new JSONObject();
                                whereObj.put("employeeName", curEmployee.getEmployeeName());
                                updateParams.add(whereObj);

                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(updateParams.toJSONString(), "com.cn.bean.", "Employee", "update", opt.getConnect());
                                if (updateResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "密码修改成功!", null);
                                } else {
                                    json = Units.objectToJson(-1, "密码修改失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "旧密码不正确!", null);
                            }
                            break;
                        }
                        case "customerLogin": {
                            if (curCustomer != null && curCustomer.getCustomerPassword().compareTo(oldPassword) == 0) {
                                JSONArray updateParams = new JSONArray();
                                JSONObject setObj = new JSONObject();
                                setObj.put("customerPassword", newPassword);
                                updateParams.add(setObj);
                                JSONObject whereObj = new JSONObject();
                                whereObj.put("customerID", curCustomer.getCustomerID());
                                updateParams.add(whereObj);

                                ArrayList<Integer> updateResult = commonController.dataBaseOperate(updateParams.toJSONString(), "com.cn.bean.", "Customer", "update", opt.getConnect());
                                if (updateResult.get(0) == 0) {
                                    json = Units.objectToJson(0, "密码修改成功!", null);
                                } else {
                                    json = Units.objectToJson(-1, "密码修改失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "旧密码不正确!", null);
                            }
                            break;
                        }
                        case "login": {

                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="数据结转">
                case "数据结转": {
                    if (curEmployee != null && curEmployee.getEmployeeName().compareTo("管理员") == 0) {
                        CommonOperate operate = new CommonOperate();
                        json = operate.dataMoveToHistory(curEmployee.getEmployeeName());
                    } else {
                        json = Units.objectToJson(-1, "您没有权限进行该操作", null);
                    }
                    break;
                }
                //</editor-fold>

                /**
                 * ***************************************调货计划管理**************************************
                 */
                //<editor-fold desc="调货计划管理">
                //<editor-fold desc="调货计划下达">
                case "调货计划下达": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/plan/", "com.cn.bean.plan.", "DHPlan", "DHPlanID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"调货计划单号\\", ",@DHJH_" + Units.getNowTimeNoSeparator());
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.plan.", "table", "DHPlanList", "DHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.plan.", "table", "DHPlan", "DHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.plan.", "DHPlan", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.plan.", "DHPlanList", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    json = Units.objectToJson(0, "数据添加成功!", null);
                                } else {
                                    json = Units.objectToJson(-1, "明细添加失败!", null);
                                    commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.plan.", "DHPlan", "delete", opt.getConnect());
                                }
                            } else {
                                json = Units.objectToJson(-1, "数据添加失败!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送货计划查询">
                case "送货计划查询": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/plan/", "com.cn.bean.plan.", "SHPlan", "SHPlanID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"送货计划单号\\", ",@SHJH_" + Units.getNowTimeNoSeparator());
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("dhPlanID") == 0) {
                                String[] keys = {"dhPlanID"};
                                String[] keysName = {"调货计划单号"};
                                int[] keysWidth = {100, 50};
                                String[] fieldsName = {"dhPlanID"};
                                json = queryOperate(target, "com.cn.bean.plan.", "table", "DHPlan", "DHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.plan.", "table", "SHPlanList", "SHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.plan.", "table", "SHPlan", "SHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.plan.", "SHPlan", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.plan.", "SHPlanList", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    json = Units.objectToJson(0, "数据添加成功!", null);
                                } else {
                                    json = Units.objectToJson(-1, "明细添加失败!", null);
                                }
                            } else {
                                json = Units.objectToJson(-1, "数据添加失败!", null);
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
