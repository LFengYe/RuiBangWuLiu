/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.ClassDescription;
import com.cn.bean.Customer;
import com.cn.bean.Employee;
import com.cn.bean.FieldDescription;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PlatformRoleRight;
import com.cn.bean.PlatformUserRole;
import com.cn.bean.in.DJInWareHouseList;
import com.cn.bean.out.JHOutWareHouseList;
import com.cn.bean.out.LPKCListInfo;
import com.cn.controller.CommonController;
import com.cn.controller.JHOutWareHouseController;
import com.cn.controller.PlatformUserInfoController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author LFeng
 */
public class DataInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DataInterface.class);

    private CommonController commonController;
    private DatabaseOpt opt;

    @Override
    public void init() throws ServletException {
        super.init();
        commonController = new CommonController();
        opt = new DatabaseOpt();
        try {
            /*导入部品基础信息到Redis中*/
            List<Object> partBaseInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator = partBaseInfo.iterator();
            while (iterator.hasNext()) {
                PartBaseInfo baseInfo = (PartBaseInfo) iterator.next();
                RedisAPI.set("partBaseInfo_" + baseInfo.getPartCode(), JSONObject.toJSONString(baseInfo));
            }

            /*导入客户基础信息到Redis中*/
            List<Object> customerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, opt.getConnect());
            Iterator<Object> iterator1 = customerList.iterator();
            while (iterator1.hasNext()) {
                Customer customer = (Customer) iterator1.next();
                RedisAPI.set("customer_" + customer.getCustomerID(), JSONObject.toJSONString(customer));
            }

            List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator2 = containerInfoList.iterator();
            while (iterator2.hasNext()) {
                GYSPartContainerInfo containerInfo = (GYSPartContainerInfo) iterator2.next();
                RedisAPI.set(containerInfo.getSupplierID() + "_" + containerInfo.getPartCode(), JSONObject.toJSONString(containerInfo));
            }

            logger.info("初始化成功!导入部品信息" + partBaseInfo.size() + "条,导入客户信息" + customerList.size() + "条,导入部品盛具信息" + containerInfoList.size() + "条");
        } catch (Exception e) {
            logger.error("初始化出错!", e);
        }
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
                                    String whereCase = "RoleCode in ('" + employee.getEmployeeTypeCode() + "')";
                                    List<Object> roleRight = commonController.dataBaseQuery("table", "com.cn.bean.", "PlatformRoleRight", "*", whereCase, Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                    if (roleRight != null && roleRight.size() > 0) {
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
                                            json = Units.objectToJson(0, "登陆成功!", menuJson);
                                        }
                                        if (type.compareTo("app") == 0) {
                                            json = Units.objectToJson(0, "登陆成功!", roleRightList);
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="平台管理">
                //<editor-fold desc="注册公司">
                case "注册公司": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformCompanyInfo", "CompanyID", opt.getConnectBase());
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PlatformCompanyInfo", update, add, delete, "base");
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PlatformCompanyInfo", "CompanyID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PlatformCompanyInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PlatformCompanyInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PlatformCompanyInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PlatformCompanyInfo", "CompanyID", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="注册数据库">
                case "注册数据库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatFormDataBaseInfo", "DataBaseID", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PlatFormDataBaseInfo", "DataBaseID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PlatFormDataBaseInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PlatFormDataBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PlatFormDataBaseInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PlatFormDataBaseInfo", "DataBaseID", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("CompanyID") == 0) {
                                String[] keys = {"companyID", "companyName"};
                                String[] keysName = {"公司编号", "公司名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"companyID", "companyName"};
                                System.out.println("datas:" + datas + ",pageSize:" + pageSize + ",pageIndex:" + pageIndex);
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformCompanyInfo", "CompanyID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PlatFormDataBaseInfo", update, add, delete, "base");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="注册平台用户">
                case "注册平台用户": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformUserInfo", "UserLoginAccount", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PlatformUserInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PlatformUserInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PlatformUserInfo", (ArrayList<Object>) queryData("com.cn.bean.", "view", "PlatformUserInfo", "UserLoginAccount", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("UserLoginDBName") == 0) {
                                String[] keys = {"userLoginDBName", "companyName", "companyID"};
                                String[] keysName = {"数据库名", "公司名称", "公司ID"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"masterDataBaseName", "companyName", "companyID"};
                                json = queryOperate(target, "com.cn.bean.", "view", "PlatFormDataBaseInfo", "CompanyID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PlatformUserInfo", update, add, delete, "base");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="定义角色">
                case "定义角色": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformRole", "RoleCode", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PlatformRole", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PlatformRole", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PlatformRole", (ArrayList<Object>) queryData("com.cn.bean.", "view", "PlatformRole", "RoleCode", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PlatformRole", update, add, delete, "base");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="定义角色权限">
                case "定义角色权限": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformRoleRight", "RoleCode", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PlatformRoleRight", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PlatformRoleRight", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PlatformRoleRight", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PlatformRoleRight", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PlatformRoleRight", "RoleCode", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PlatformRoleRight", update, add, delete, "base");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                String[] keys = {"roleCode", "roleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("RightCode") == 0) {
                                String[] keys = {"rightCode", "rightName"};
                                String[] keysName = {"模块代码", "模块名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"rightCode", "rightName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformRight", "RightCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="定义用户角色">
                case "定义用户角色": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformUserRole", "UserLoginAccount", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PlatformUserRole", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PlatformUserRole", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PlatformUserRole", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PlatformUserRole", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PlatformUserRole", "UserLoginAccount", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PlatformUserRole", update, add, delete, "base");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                String[] keys = {"roleCode", "roleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("UserLoginAccount") == 0) {
                                String[] keys = {"userLoginAccount"};
                                String[] keysName = {"用户名"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"userLoginAccount"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

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
                /**
                 * ***************************************部品入库管理**************************************
                 */
                //<editor-fold desc="部品入库管理">
                //<editor-fold desc="待检入库">
                case "待检入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", "DJInWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "DJInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
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
                                String[] keys = {"partCode", "partName", "partID", "inboundContainerName", "inboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品名称", "部品编号", "入库盛具", "入库包装数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partName", "partID", "inboundContainerName", "inboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            /*
                            List<DJInWareHouseList> dJInWareHouseLists = JSONArray.parseArray(details, DJInWareHouseList.class);
                            Iterator<DJInWareHouseList> iterator = dJInWareHouseLists.iterator();
                            while (iterator.hasNext()) {
                                DJInWareHouseList dJInWareHouseList = iterator.next();
                                GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(dJInWareHouseList.getSupplierID() + "_" + dJInWareHouseList.getPartCode()), GYSPartContainerInfo.class);
                                int boxAmount = 0;
                                if (containerInfo != null) {
                                    boxAmount = (dJInWareHouseList.getInboundAmount() % containerInfo.getInboundPackageAmount() == 0)
                                            ? (dJInWareHouseList.getInboundAmount() / containerInfo.getInboundPackageAmount())
                                            : (dJInWareHouseList.getInboundAmount() / containerInfo.getInboundPackageAmount() + 1);
                                }
                                dJInWareHouseList.setInboundBoxAmount(boxAmount);
                            }
                             */
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "DJInWareHouseList", update, "", delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect());
                                if (delResult.get(0) == 0) {
                                    json = Units.objectToJson(-1, "删除操作失败!", null);
                                } else {

                                }
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.in.", "DJInWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "DJInWareHouseList", importPath + fileName);
                            if (importData != null) {
                                Iterator iterator = importData.iterator();
                                while (iterator.hasNext()) {
                                    DJInWareHouseList houseList = (DJInWareHouseList) iterator.next();
                                    GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(houseList.getSupplierID() + "_" + houseList.getPartCode()), GYSPartContainerInfo.class);
                                }
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                        commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect());
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

                //<editor-fold desc="待检审核">
                case "待检审核": {
                    String whereCase = "DJRKAuditTime is null";
                    switch (operation) {
                        case "create": {
                            json = createOperateWithFilter(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", whereCase, "DJInWareHouseID", opt.getConnect());
                            /*
                             json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                             json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                             json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                             json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                             json = Units.insertStr(json, "\\\"部品状态\\", ",@待检");
                             */
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperateWithFilter("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            obj.put("djRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "DJInWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                                System.out.println(json);
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检出库">
                case "送检出库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"送检出库单号\\", ",@SJCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "SJOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"送检出库单号\\", ",@SJCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.", "view", "SJOutWareHouseList", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "inboundAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "入库数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "inboundAmount"};
                                json = queryOperate(target, "com.cn.bean.in.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "addItem": {
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "SJOutWareHouseList", update, "", delete, "data");
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.in.", "SJOutWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "SJOutWareHouseList", importPath + fileName);
                            if (importData != null) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                        commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "delete", opt.getConnect());
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

                //<editor-fold desc="送检返回">
                case "送检返回": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/in/", "com.cn.bean.in.", "SJBackWareHouse", "SJBackWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"送检返回单号\\", ",@SJTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "SJBackWareHouse.json"));
                            json = Units.insertStr(json, "\\\"送检返回单号\\", ",@SJTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.", "table", "SJBackWareHouseList", "SJBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.", "table", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "table", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "sjCKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "入库数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "sjCKAmount"};
                                json = queryOperate(target, "com.cn.bean.in.", "view", "SJOutWareHouseList", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJBackWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJBackWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "SJBackWareHouseList", update, "", delete, "data");
                            }
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.in.", "SJBackWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "SJBackWareHouseList", importPath + fileName);
                            if (importData != null && importData.size() > 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJBackWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData), "com.cn.bean.in.", "SJBackWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                        commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJBackWareHouse", "delete", opt.getConnect());
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

                //<editor-fold desc="终端退库">
                case "终端退库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/in/", "com.cn.bean.in.", "ZDBackWareHouse", "ZDBackWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"终端退库单据号\\", ",@ZDTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "ZDBackWareHouse.json"));
                            json = Units.insertStr(json, "\\\"终端退库单据号\\", ",@ZDTK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.", "table", "ZDBackWareHouseList", "ZDBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.", "table", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "table", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zdCustomerID") == 0) {
                                String[] keys = {"zdCustomerID", "zdCustomerName"};
                                String[] keysName = {"终端客户代码", "终端客户名称"};
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
                                // TODO 需要修改
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "lhAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "领货数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "lhAmount"};
                                json = queryOperate(target, "com.cn.bean.base.", "table", "SXProgressList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "ZDBackWareHouse", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                //??? ---  添加明细时tkPartState(部品状态)不能为空, 前段在哪儿输入
                                result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "ZDBackWareHouseList", "add", opt.getConnect()).get(0);
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
                /**
                 * ***************************************部品移库管理**************************************
                 */
                //<editor-fold desc="部品移库管理">
                //<editor-fold desc="报检信息">
                case "报检信息": {
                    String whereCase1 = "DJInWareHouseID in (select DJInWareHouseID from tblDJInWareHouse where DJRKAuditTime is not null)";
                    String whereCase = "InspectionTime is null and " + whereCase1;
                    switch (operation) {
                        case "create": {
                            json = createOperateWithFilter(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", whereCase, "DJInWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperateWithFilter("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
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

                            //json = Units.objectToJson(0, "检验成功!", null);
                            int result = commonController.dataBaseOperate("[" + obj.toJSONString() + "]", "com.cn.bean.move.", "InspectionReportList", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                commonController.dataBaseOperate(updateStr, "com.cn.bean.in.", "DJInWareHouseList", "update", opt.getConnect());
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
                            json = createOperate(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "FXOutWareHouse", "FXOutWareHouseID", opt.getConnect());
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
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.move.", "table", "FXOutWareHouseList", "FXOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.move.", "table", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "table", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "fxAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "返修数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "lhAmount"};
                                json = queryOperate(target, "com.cn.bean.base.", "table", "SXProgressList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXOutWareHouse", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXOutWareHouseList", "add", opt.getConnect()).get(0);
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

                //<editor-fold desc="返修入库">
                case "返修入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "FXInWareHouse", "FXInWareHouseID", opt.getConnect());
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
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.move.", "table", "FXInWareHouseList", "FXInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.move.", "table", "FXInWareHouse", "FXInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "table", "FXInWareHouse", "FXInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "fxCKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "出库数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "fxCKAmount"};
                                json = queryOperate(target, "com.cn.bean.move.", "table", "FXOutWareHouseList", "FXOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXInWareHouse", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXInWareHouseList", "add", opt.getConnect()).get(0);
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

                //<editor-fold desc="现场调账">
                case "现场调账": {
                    switch (operation) {
                        case "create": {
                            json = createOperateWithFilter(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "AdjustAccount", "TZYMonth in (select TZYMonth from tblXCAdjustAccountList)", "TZYMonth", opt.getConnect());
                            json = Units.insertStr(json, "\\\"调帐编号\\", Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/move/", "AdjustAccount.json"));
                            json = Units.insertStr(json, "\\\"调帐编号\\", Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.move.", "table", "XCAdjustAccountList", "TZYMonth", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "fxCKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "出库数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "fxCKAmount"};
                                json = queryOperate(target, "com.cn.bean.move.", "table", "FXOutWareHouseList", "FXOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXInWareHouse", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXInWareHouseList", "add", opt.getConnect()).get(0);
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
                /**
                 * ***************************************部品出库管理**************************************
                 */
                //<editor-fold desc="部品出库管理">
                //<editor-fold desc="计划出库">
                case "计划出库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", "JHOutWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "JHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"计划出库单号\\", ",@JHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
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
                                String[] keys = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "出库盛具", "出库包装数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "outboundContainerName", "outboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "importDetail": {
                            /**
                             * 导入的数据
                             */
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.out.", "JHOutWareHouseList", importPath + fileName);
                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(importData, item);
//                            JSONObject obj = JSONObject.parseObject(item);
//                            obj.put("bhStaffName", "备货员");
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
                            JSONObject obj = new JSONObject();
                            obj.put("jhConfirm", "true");
                            int result = commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", opt.getConnect()).get(0);
                            if (result == 0) {
                                json = Units.objectToJson(0, "确认成功!", null);
                            } else {
                                json = Units.objectToJson(-1, "确认失败!", null);
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
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="非生产领料">
                case "非生产领料": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "FJHOutWareHouse", "FJHOutWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"出库时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/out/", "FJHOutWareHouse.json"));
                            json = Units.insertStr(json, "\\\"非计划出库单号\\", ",@FJHCK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"出库时间\\", ",@" + Units.getNowTime());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "table", "FJHOutWareHouseList", "FJHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "table", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "table", "FJHOutWareHouse", "FJHOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("fzdCustomerID") == 0) {
                                String[] keys = {"fzdCustomerID", "fzdCustomerName"};
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "autoStyling", "cfAddress"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "车型", "存放地址"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "autoStyling", "psAddress1"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("fhStaffName") == 0) {
                                String[] keys = {"fhStaffName"};
                                String[] keysName = {"发货人名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"userLoginAccount"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "FJHOutWareHouse", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "FJHOutWareHouseList", "add", opt.getConnect()).get(0);
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

                //<editor-fold desc="现场结算">
                case "现场结算": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "XCJS", "XCJSID", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.out.", "table", "XCJSList", "XCJSID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "table", "XCJS", "XCJSID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "table", "XCJS", "XCJSID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "autoStyling", "cfAddress"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "车型", "存放地址"};
                                int[] keysWidth = {20, 20, 20, 20, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "autoStyling", "psAddress1"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "XCJS", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "XCJSList", "add", opt.getConnect()).get(0);
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

                //<editor-fold desc="部品退货">
                case "部品退货": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "BPTHOutWareHouse", "BPTHOutWareHoseID", opt.getConnect());
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
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "table", "BPTHOutWareHouseList", "BPTHOutWareHoseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "table", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.out.", "table", "BPTHOutWareHouse", "BPTHOutWareHoseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "kcAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "库存数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "lhAmount"};
                                json = queryOperate(target, "com.cn.bean.base.", "table", "SXProgressList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "BPTHOutWareHouse", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "BPTHOutWareHouseList", "add", opt.getConnect()).get(0);
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

                //<editor-fold desc="备货确认">
                case "备货确认": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", "JHOutWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = JSONObject.parseObject(datas);
                            obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            obj.put("djRKAuditTime", Units.getNowTime());
                            System.out.println("auditInfo:" + obj.toJSONString());
                            json = Units.objectToJson(0, "审核成功!", null);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="领货确认">
                case "领货确认": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", "JHOutWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = JSONObject.parseObject(datas);
                            obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            obj.put("djRKAuditTime", Units.getNowTime());
                            System.out.println("auditInfo:" + obj.toJSONString());
                            json = Units.objectToJson(0, "审核成功!", null);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="配送确认">
                case "配送确认": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", "JHOutWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "view", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = JSONObject.parseObject(datas);
                            obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            obj.put("djRKAuditTime", Units.getNowTime());
                            System.out.println("auditInfo:" + obj.toJSONString());
                            json = Units.objectToJson(0, "审核成功!", null);
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
                            if (list != null && list.size() > 0) {
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
                /**
                 * ***************************************基础信息管理**************************************
                 */
                //<editor-fold desc="基础信息管理">
                //<editor-fold desc="部品档案">
                case "部品档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBaseInfo", "PartCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PartBaseInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PartBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PartBaseInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("autoStylingName") == 0) {
                                String[] keys = {"autoStylingName"};
                                String[] keysName = {"使用车型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"autoStylingName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "AutoStyling", "AutoStylingName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCategoryName") == 0) {
                                String[] keys = {"partCategoryName"};
                                String[] keysName = {"部品类别"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"partCategoryName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PartBaseInfo", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="客户档案">
                case "客户档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Customer", "CustomerID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "Customer", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "Customer", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "Customer", (ArrayList<Object>) queryData("com.cn.bean.", "table", "Customer", "CustomerID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("customerTypeName") == 0) {
                                String[] keys = {"customerTypeName"};
                                String[] keysName = {"客户类型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"customerTypeName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "CustomerType", "CustomerTypeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("shMethodName") == 0) {
                                String[] keys = {"shMethodName"};
                                String[] keysName = {"送货方式"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"shMethodName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "SHMethod", "SHMethodName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "Customer", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="员工档案">
                case "员工档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Employee", "EmployeeName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "Employee", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "Employee", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "Employee", (ArrayList<Object>) queryData("com.cn.bean.", "table", "Employee", "EmployeeName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "Employee", update, add, delete, "data");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("employeeType") == 0) {
                                String[] keys = {"employeeTypeCode", "employeeType"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品存放地址">
                case "部品存放地址": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartStore", "SupplierID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PartStore", "SupplierID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PartStore", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PartStore", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PartStore", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PartStore", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PartStore", update, add, delete, "data");
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="出入库盛具">
                case "出入库盛具": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "view", "com/cn/json/", "com.cn.bean.", "GYSPartContainerInfo", "SupplierID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "view", "GYSPartContainerInfo", "SupplierID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "GYSPartContainerInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "GYSPartContainerInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "GYSPartContainerInfo", (ArrayList<Object>) queryData("com.cn.bean.", "view", "GYSPartContainerInfo", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "GYSPartContainerInfo", update, add, delete, "data");
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
                            }
                            if (target.compareToIgnoreCase("inboundContainerName") == 0) {
                                String[] keys = {"inboundContainerName"};
                                String[] keysName = {"盛具名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"containerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("outboundContainerName") == 0) {
                                String[] keys = {"outboundContainerName"};
                                String[] keysName = {"盛具名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"ContainerName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="盛具档案">
                case "盛具档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Container", "ContainerName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "Container", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "Container", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "Container", (ArrayList<Object>) queryData("com.cn.bean.", "table", "Container", "ContainerName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "Container", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="部品类别">
                case "部品类别": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartCategory", "PartCategoryName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PartCategory", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PartCategory", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PartCategory", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PartCategory", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="车型档案">
                case "车型档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "AutoStyling", "AutoStylingName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "AutoStyling", "AutoStylingName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "AutoStyling", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "AutoStyling", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "AutoStyling", (ArrayList<Object>) queryData("com.cn.bean.", "table", "AutoStyling", "AutoStylingName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "AutoStyling", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="客户类别">
                case "客户类别": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "CustomerType", "CustomerTypeName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "CustomerType", "CustomerTypeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "CustomerType", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "CustomerType", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "CustomerType", (ArrayList<Object>) queryData("com.cn.bean.", "table", "CustomerType", "CustomerTypeName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "CustomerType", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送货方式">
                case "送货方式": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "SHMethod", "SHMethodName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "SHMethod", "SHMethodName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "SHMethod", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "SHMethod", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "SHMethod", (ArrayList<Object>) queryData("com.cn.bean.", "table", "SHMethod", "SHMethodName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "SHMethod", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="库存安全">
                case "库存安全": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "KCQuota", "SupplierID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "KCQuota", "SupplierID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "KCQuota", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "KCQuota", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "KCQuota", (ArrayList<Object>) queryData("com.cn.bean.", "table", "KCQuota", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "KCQuota", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="免检信息">
                case "免检信息": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "ExemptionInfo", "SupplierID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "ExemptionInfo", "SupplierID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "ExemptionInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "ExemptionInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "ExemptionInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "ExemptionInfo", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "ExemptionInfo", update, add, delete, "data");
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="显示屏档案">
                case "显示屏档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "AreaLedIPInfo", "AddressCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "AreaLedIPInfo", "AddressCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "AreaLedIPInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "AreaLedIPInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "AreaLedIPInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "AreaLedIPInfo", "AddressCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "AreaLedIPInfo", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="备货员档案">
                case "备货员档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBHStaff", "PartCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PartBHStaff", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PartBHStaff", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PartBHStaff", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PartBHStaff", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PartBHStaff", "PartCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PartBHStaff", update, add, delete, "data");
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
                            }
                            if (target.compareToIgnoreCase("employeeName") == 0) {
                                String[] keys = {"employeeName"};
                                String[] keysName = {"备货员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //</editor-fold>
                /**
                 * ***************************************数据报表管理**************************************
                 */
                //<editor-fold desc="部品收发存总表">
                case "部品收发存总表": {
                    switch (operation) {
                        case "create": {
                            json = Units.returnFileContext("com/cn/json/report/", "test.json");
                            //json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "AreaLedIPInfo", "AddressCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "AreaLedIPInfo", "AddressCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "AreaLedIPInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "AreaLedIPInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "AreaLedIPInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "AreaLedIPInfo", "AddressCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "AreaLedIPInfo", update, add, delete, "data");
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
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = Units.returnFileContext(path + jsonPackagePath, tableName + ".json");
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);
        if (result != null) {
            List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", whereCase, pageSize, 1, orderField, 0, conn);
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
        System.out.println("whereSql:" + whereSql);
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

            if (objClass.isAnnotationPresent(ClassDescription.class
            )) {
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
        String json = null;
        Connection conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(update)) {
            ArrayList<Integer> updateResult = commonController.dataBaseOperate(update, beanPackage, tableName, "update", conn);
            if (updateResult.get(0) == -1) {
                json = Units.objectToJson(-1, "修改操作失败!", null);
            }
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(add)) {
            ArrayList<Integer> addResult = commonController.dataBaseOperate(add, beanPackage, tableName, "add", conn);
            if (addResult.get(0) == -1) {
                json = Units.objectToJson(-1, "添加操作失败!", null);
            }
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(delete)) {
            ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, beanPackage, tableName, "delete", conn);
            if (delResult.get(0) == -1) {
                json = Units.objectToJson(-1, "删除操作失败!", null);
            }
        }

        if (json == null) {
            json = Units.objectToJson(0, "操作成功!", null);
        }
        return json;
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
//        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
        ArrayList<Integer> addResult = commonController.dataBaseOperate(JSONObject.toJSONString(result, Units.features), beanPackage, tableName, "add", conn);
        if (addResult.get(0) == 0) {
            json = Units.objectToJson(0, "导入成功" + (addResult.size() - 1) + "条数据!", null);
        } else if (addResult.get(0) == -1) {
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
        } else if (addResult.get(0) == 2) {
            json = Units.objectToJson(2, "导入数据为空!", null);
        } else {
            json = Units.objectToJson(1, "输入参数错误!", null);
        }
        return json;
    }

    private ArrayList<Object> importDetailData(String detail, String beanPackage, String tableName, String fileName) throws Exception {
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
        System.out.println("cells num:" + headerRow.getPhysicalNumberOfCells() + ",des size:" + fieldDes.size());
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != fieldDes.size()) {
//            json = Units.objectToJson(-1, "上传数据格式不正确, 请先下载模板, 按照模板格式录入数据", null);
            return null;
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
            JSONObject jSONObject = JSONObject.parseObject(detail);
            Iterator<String> keysIterator = jSONObject.keySet().iterator();
            while (keysIterator.hasNext()) {
                String key = keysIterator.next();
                for (Field field : fields) {
//                    System.out.println("fieldName:" + field.getName() + ",key:" + key);
                    if (field.getName().compareToIgnoreCase(key) == 0) {
//                        System.out.println("fieldName:" + field.getName() + ",key:" + key + ",key value:" + jSONObject.getString(key));
                        field.setAccessible(true);
                        if (!Units.strIsEmpty(jSONObject.getString(key))) {
                            field.set(object, jSONObject.getString(key));
                        }
                    }
                }
            }

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
        return result;

//        System.out.println("JSONObject.toJSONString(result):" + JSONObject.toJSONString(result));
//        return JSONObject.toJSONString(result);
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
