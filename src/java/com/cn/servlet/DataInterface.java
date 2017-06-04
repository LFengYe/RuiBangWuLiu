/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.*;
import com.cn.bean.in.*;
import com.cn.bean.move.*;
import com.cn.bean.out.*;
import com.cn.bean.pro.*;
import com.cn.bean.report.*;
import com.cn.bean.in.DJInWareHouseList;
/*
import com.cn.bean.AreaLedIPInfo;
import com.cn.bean.ClassDescription;
import com.cn.bean.Customer;
import com.cn.bean.Employee;
import com.cn.bean.FieldDescription;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartCategory;
import com.cn.bean.PartStore;
import com.cn.bean.PlatformRoleRight;
import com.cn.bean.PlatformUserRole;
import com.cn.bean.in.DJInWareHouse;
import com.cn.bean.in.DJInWareHouseList;
import com.cn.bean.in.SJBackWareHouse;
import com.cn.bean.in.SJBackWareHouseList;
import com.cn.bean.in.SJOutWareHouse;
import com.cn.bean.in.SJOutWareHouseList;
import com.cn.bean.in.ZDBackWareHouse;
import com.cn.bean.in.ZDBackWareHouseList;
import com.cn.bean.move.FXInWareHouse;
import com.cn.bean.move.FXInWareHouseList;
import com.cn.bean.move.FXOutWareHouse;
import com.cn.bean.move.FXOutWareHouseList;
import com.cn.bean.out.BPTHOutWareHouse;
import com.cn.bean.out.BPTHOutWareHouseList;
import com.cn.bean.out.JHOutWareHouseList;
import com.cn.bean.out.LPKCListInfo;
import com.cn.bean.out.XCJS;
import com.cn.bean.pro.JPQJCForZDTK;
import com.cn.bean.pro.KFJCBLPForBPTH;
import com.cn.bean.pro.KFJCBLPForFXCK;
import com.cn.bean.pro.KFJCDJPForBPTH;
import com.cn.bean.pro.KFJCDJPForSJCK;
import com.cn.bean.pro.KFJCFXPForBPTH;
import com.cn.bean.pro.KFJCFXPForFXRK;
import com.cn.bean.pro.KFJCLPForBPTH;
import com.cn.bean.pro.KFJCLPForFXCK;
import com.cn.bean.pro.KFJCSJPForSJTK;
import com.cn.bean.pro.XPJCForZDTK;
import com.cn.bean.report.CKListForFxpCK;
import com.cn.bean.report.CKListForJPSX;
import com.cn.bean.report.CKListForZCJHCK;
import com.cn.bean.report.KFJCListForBLp;
import com.cn.bean.report.KFJCListForLp;
import com.cn.bean.report.RKListForBLpRK;
import com.cn.bean.report.RKListForDjpRK;
import com.cn.bean.report.RKListForFxpRK;
import com.cn.bean.report.RKListForLpRK;
import com.cn.bean.report.RKListForSJCK;
import com.cn.bean.report.RKListForSJTK;
import com.cn.bean.report.SFCTotalData;
import com.cn.bean.report.THListForBPTH;
 */
import com.cn.controller.CommonController;
import com.cn.controller.InWareHouseController;
import com.cn.controller.JHOutWareHouseController;
import com.cn.controller.PlatformUserInfoController;
import com.cn.test.LedControl;
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

            /*导入出入库盛具信息到Redis中*/
            List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator2 = containerInfoList.iterator();
            while (iterator2.hasNext()) {
                GYSPartContainerInfo containerInfo = (GYSPartContainerInfo) iterator2.next();
                RedisAPI.set(containerInfo.getSupplierID() + "_" + containerInfo.getPartCode(), JSONObject.toJSONString(containerInfo));
            }

            /*导入存放地址信息到Redis中*/
            List<Object> ledIpInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", Integer.MAX_VALUE, 1, "addressCode", 0, opt.getConnect());
            Iterator<Object> iterator3 = ledIpInfoList.iterator();
            while (iterator3.hasNext()) {
                AreaLedIPInfo ledIpInfo = (AreaLedIPInfo) iterator3.next();
                RedisAPI.set("ledIpInfo_" + ledIpInfo.getAddressCode(), JSONObject.toJSONString(ledIpInfo));
            }

            /*导入出入库盛具信息到Redis中*/
            List<Object> partStoreList = commonController.dataBaseQuery("table", "com.cn.bean.", "PartStore", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator4 = partStoreList.iterator();
            while (iterator4.hasNext()) {
                PartStore partStore = (PartStore) iterator4.next();
                RedisAPI.set("partStore_" + partStore.getSupplierID() + "_" + partStore.getPartCode(), JSONObject.toJSONString(partStore));
            }

            List<Object> partCategory = commonController.dataBaseQuery("table", "com.cn.bean.", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategoryName", 0, opt.getConnect());
            Iterator<Object> iterator5 = partCategory.iterator();
            while (iterator5.hasNext()) {
                PartCategory category = (PartCategory) iterator5.next();
                RedisAPI.set("partCategory_" + category.getPartCategoryName(), JSONObject.toJSONString(category));
            }

            logger.info("初始化成功!导入部品信息" + partBaseInfo.size() + "条,导入客户信息" + customerList.size() + "条,导入部品盛具信息" + containerInfoList.size() + "条");

            //LedControl.setC01Plan();
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
//                                    System.out.println("username:" + session.getAttribute("user"));
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
                /**
                 * ***************************************部品入库管理**************************************
                 */
                //<editor-fold desc="部品入库管理">
                //<editor-fold desc="待检入库">
                case "待检入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", datas, rely, "", "DJInWareHouseID", opt.getConnect());
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
                        case "request_detail": {
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("djInWareHouseID");
                            String mainTabWhereSql = "DJInWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "DJInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                DJInWareHouse djInWareHouse = (DJInWareHouse) list.get(0);

                                Class objClass = Class.forName("com.cn.bean.in." + "DJInWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "DJInWareHouseList", "*", whereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        DJInWareHouseList dj = (DJInWareHouseList) obj;
                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(dj.getSupplierID() + "_" + dj.getPartCode()), GYSPartContainerInfo.class);
                                        dj.setInboundPackageAmount(containerInfo.getInboundPackageAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(djInWareHouse.getDjRKAuditTime())) {
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
                            //json = queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partName", "partID", "autoStylingName", "partUnit", "inboundContainerName", "inboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品名称", "部品编号", "车型", "部品单位", "入库盛具", "入库包装数量"};
                                int[] keysWidth = {20, 20, 20, 10, 10, 10, 10};
                                String[] fieldsName = {"partCode", "partName", "partID", "autoStylingName", "partUnit", "inboundContainerName", "inboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.", "view", "GYSPartContainerInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", queryOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", "", item, true, opt.getConnect(), 10, 1));
                                    } else {
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                        commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect()).get(0);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "DJInWareHouseList", update, add, delete, "data");
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.in.", "DJInWareHouse", "delete", opt.getConnect());
                                //System.out.println("res:" + Arrays.toString(delResult.toArray()));
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
                            json = exportTemplate("com.cn.bean.in.", "DJInWareHouseList", null);
                            break;
                        }
                        case "importDetail": {
                            ArrayList<Object> importData = commonController.importData("com.cn.bean.in.", "DJInWareHouseList", importPath + fileName);
                            //ArrayList<Object> importData = importDetailData(detail, "com.cn.bean.in.", "DJInWareHouseList", importPath + fileName);
                            //System.out.println("import:" + JSONObject.toJSONString(importData));
                            DJInWareHouse dJInWareHouse = JSONObject.parseObject(item, DJInWareHouse.class);
                            if (importData != null) {
                                Iterator iterator = importData.iterator();
                                while (iterator.hasNext()) {
                                    DJInWareHouseList houseList = (DJInWareHouseList) iterator.next();
                                    houseList.setSupplierID(dJInWareHouse.getSupplierID());
                                    houseList.setDjInWareHouseID(dJInWareHouse.getDjInWareHouseID());
                                    houseList.setInboundBatch(dJInWareHouse.getInboundBatch());
                                    houseList.setPartState("待检品");

                                    GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(houseList.getSupplierID() + "_" + houseList.getPartCode()), GYSPartContainerInfo.class);
                                    if (containerInfo != null) {
                                        int packageAmount = containerInfo.getInboundPackageAmount();
                                        int boxAmount = (houseList.getInboundAmount() % packageAmount == 0) ? (houseList.getInboundAmount() / packageAmount) : (houseList.getInboundAmount() / packageAmount + 1);
                                        houseList.setInboundBoxAmount(boxAmount);
                                    } else {
                                        houseList.setInboundBoxAmount(999999);
                                    }
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
                            if (isHistory == 0) {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", whereCase, "DJInWareHouseID", opt.getConnect());
                                json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", datas, rely, whereCase, "DJInWareHouseID", opt.getConnect());
                            } else {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", "", "DJInWareHouseID", opt.getConnect());
                                json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", datas, rely, "", "DJInWareHouseID", opt.getConnect());
                            }
                            break;
                        }
                        case "request_detail": {
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("djInWareHouseID");
                            String mainTabWhereSql = "DJInWareHouseID = '" + djInWareHouseID + "'";
                            Class objClass = Class.forName("com.cn.bean.in." + "DJInWareHouseList");
                            Method method = objClass.getMethod("getRecordCount", null);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "DJInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                DJInWareHouse djInWareHouse = (DJInWareHouse) list.get(0);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "DJInWareHouseList", "*", whereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        DJInWareHouseList dj = (DJInWareHouseList) obj;
                                        GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(dj.getSupplierID() + "_" + dj.getPartCode()), GYSPartContainerInfo.class);
                                        dj.setInboundPackageAmount(containerInfo.getInboundPackageAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(djInWareHouse.getDjRKAuditTime())) {
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
                            /*if (isHistory == 0) {
                                json = queryOperateWithFilter("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOperateWithFilter("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            }*/
                            if (isHistory == 0) {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOnDateOperate("com.cn.bean.in.", "view", "DJInWareHouse", "DJInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "audit": {
                            DJInWareHouse dJInWareHouse = JSONObject.parseObject(datas, DJInWareHouse.class);
                            JSONObject proParams = new JSONObject();
                            //obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            //obj.put("djRKAuditTime", Units.getNowTime());
                            proParams.put("DJInWareHouseID", "string," + dJInWareHouse.getDjInWareHouseID());
                            proParams.put("DJRKAuditStaffName", "string," + session.getAttribute("user"));
                            //String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            //ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "DJInWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            JSONArray jsonParams = new JSONArray();
                            jsonParams.add(proParams);
                            ArrayList<Integer> updateResult = commonController.proceduceForUpdate("tbDJInWareHouseAudit", jsonParams, opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!",null);
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
                            //json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", datas, rely, "", "SJOutWareHouseID", opt.getConnect());
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
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("sjOutWareHouseID");
                            String mainTabWhereSql = "SJOutWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "SJOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJOutWareHouse sJOutWareHouse = (SJOutWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> djpList = commonController.proceduceQuery("spGetKFJCDjpListForSJCK", proParams, "com.cn.bean.pro.KFJCDJPForSJCK", opt.getConnect());
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                for (Object obj : djpList) {
                                    KFJCDJPForSJCK sjck = (KFJCDJPForSJCK) obj;
                                    limitMap.put(sjck.getPartCode(), sjck.getKfJCDjpAmount());
                                }

                                Class objClass = Class.forName("com.cn.bean.in." + "SJOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "SJOutWareHouseList", "*", whereSql, pageSize, pageIndex, "SJOutWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        SJOutWareHouseList sj = (SJOutWareHouseList) obj;
                                        //sj.setInboundAmount(Integer.valueOf(limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                        sj.setInboundAmount(Integer.valueOf((null == limitMap.get(sj.getPartCode())) ? "0" : limitMap.get(sj.getPartCode())) + Integer.valueOf(sj.getSjCKAmount()));
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(sJOutWareHouse.getSjCKAuditTime())) {
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
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "inboundAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "待检品数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCDjpAmount"};
                                //json = queryOperate(target, "com.cn.bean.in.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCDjpListForSJCK", proParams, "com.cn.bean.pro.KFJCDJPForSJCK", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        KFJCDJPForSJCK sjck = (KFJCDJPForSJCK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjck.getPartCode()), PartBaseInfo.class);
                                        sjck.setPartID(baseInfo.getPartID());
                                        sjck.setPartName(baseInfo.getPartName());

//                                        PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                        sjck.setWareHouseManagerName(category.getWareHouseManagerName());
                                    }
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCDJPForSJCK", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.in.", "SJOutWareHouse", "delete", opt.getConnect());
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
                            obj.put("sjCKAuditStaffName", session.getAttribute("user"));
                            obj.put("sjCKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "SJOutWareHouse", "update", opt.getConnect());
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
                            String sjOutWareHouseID = arrayParam.getJSONObject(0).getString("sjOutWareHouseID");
                            String mainTabWhereSql = "SJOutWareHouseID = '" + sjOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJOutWareHouse", "*", mainTabWhereSql, 11, 1, "SJOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJOutWareHouse sJOutWareHouse = (SJOutWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getSjCKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "SJOutWareHouseList", "update", opt.getConnect()).get(0);
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
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "sjOutWareHouseID") + "]", "com.cn.bean.in.", "SJOutWareHouse", "delete", opt.getConnect());
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
                            //json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJBackWareHouse", "SJBackWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "SJBackWareHouse", datas, rely, "", "SJBackWareHouseID", opt.getConnect());
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
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("sjTKAuditStaffName", session.getAttribute("user"));
                            obj.put("sjTKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "SJBackWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.in.", "SJBackWareHouse", "delete", opt.getConnect());
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
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String sjBackWareHouseID = arrayParam.getJSONObject(0).getString("sjBackWareHouseID");
                            String mainTabWhereSql = "SJBackWareHouseID = '" + sjBackWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJBackWareHouse", "*", mainTabWhereSql, 11, 1, "SJBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJBackWareHouse sJOutWareHouse = (SJBackWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getSjTKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "SJBackWareHouseList", "update", opt.getConnect()).get(0);
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
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("sjBackWareHouseID");
                            String mainTabWhereSql = "SJBackWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "SJBackWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "SJBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                SJBackWareHouse sJBackWareHouse = (SJBackWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> sjp = commonController.proceduceQuery("spGetKFJCSjpListForSJTK", proParams, "com.cn.bean.pro.KFJCSJPForSJTK", opt.getConnect());
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                for (Object obj : sjp) {
                                    KFJCSJPForSJTK sjtk = (KFJCSJPForSJTK) obj;
                                    limitMap.put(sjtk.getPartCode(), sjtk.getKfJCSjpAmount());
                                }

                                Class objClass = Class.forName("com.cn.bean.in." + "SJBackWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "SJBackWareHouseList", "*", whereSql, pageSize, pageIndex, "SJBackWareHouseID", 0, opt.getConnect());
                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        SJBackWareHouseList sj = (SJBackWareHouseList) obj;
                                        //sj.setSjCKAmount(Integer.valueOf(limitMap.get(sj.getPartCode())) + sj.getSjTKAmount());
                                        sj.setSjCKAmount(Integer.valueOf((null == limitMap.get(sj.getPartCode())) ? "0" : limitMap.get(sj.getPartCode())) + sj.getSjTKAmount());
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(sJBackWareHouse.getSjTKAuditTime())) {
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
                            //json = queryOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "SJBackWareHouse", "SJBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "sjCKAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "入库数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCSjpAmount"};
                                //json = queryOperate(target, "com.cn.bean.in.", "view", "SJOutWareHouseList", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCSjpListForSJTK", proParams, "com.cn.bean.pro.KFJCSJPForSJTK", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        KFJCSJPForSJTK sjtk = (KFJCSJPForSJTK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + sjtk.getPartCode()), PartBaseInfo.class);
                                        sjtk.setPartID(baseInfo.getPartID());
                                        sjtk.setPartName(baseInfo.getPartName());

//                                        PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                        sjtk.setWareHouseManagerName(category.getWareHouseManagerName());
                                    }
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCSJPForSJTK", keys, keysName, keysWidth, fieldsName, target, rely);
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
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
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "SJBackWareHouseID") + "]", "com.cn.bean.in.", "SJBackWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "SJBackWareHouseList", update, add, delete, "data");
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
                                        commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJBackWareHouse", "delete", opt.getConnect());
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

                //<editor-fold desc="终端退库">
                case "终端退库": {
                    switch (operation) {
                        case "create": {
                            //json = createOperate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "ZDBackWareHouse", "ZDBackWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/in/", "com.cn.bean.in.", "ZDBackWareHouse", datas, rely, "", "ZDBackWareHouseID", opt.getConnect());
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
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String zdBackWareHouseID = arrayParam.getJSONObject(0).getString("zdBackWareHouseID");
                            String mainTabWhereSql = "ZDBackWareHouseID = '" + zdBackWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "ZDBackWareHouse", "*", mainTabWhereSql, 11, 1, "ZDBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                ZDBackWareHouse sJOutWareHouse = (ZDBackWareHouse) list.get(0);
                                JSONArray updateArray = new JSONArray();
                                if (!Units.strIsEmpty(sJOutWareHouse.getZdTKAuditTime())) {
                                    for (int i = 0; i < arrayParam.size(); i++) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("wareHouseManagerName", session.getAttribute("user"));
                                        updateArray.add(obj);
                                        updateArray.add(arrayParam.getJSONObject(i));
                                    }
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "ZDBackWareHouseList", "update", opt.getConnect()).get(0);
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
                            //json = queryOperate("com.cn.bean.in.", "view", "ZDBackWareHouseList", "ZDBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("zdBackWareHouseID");
                            String mainTabWhereSql = "ZDBackWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "ZDBackWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "ZDBackWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                ZDBackWareHouse zDBackWareHouse = (ZDBackWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("ZDCustomerID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("zdCustomerID"));

                                Class objClass = Class.forName("com.cn.bean.in." + "ZDBackWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.in.", "ZDBackWareHouseList", "*", whereSql, pageSize, pageIndex, "ZDBackWareHouseID", 0, opt.getConnect());
                                ArrayList supplierList = new ArrayList();
                                for (Object obj : detailList) {
                                    ZDBackWareHouseList sj = (ZDBackWareHouseList) obj;
                                    if (!supplierList.contains(sj.getSupplierID())) {
                                        supplierList.add(sj.getSupplierID());
                                    }
                                }
                                String supplierStr = Arrays.toString(supplierList.toArray());
                                proParams.put("SupplierIDStr", "string," + supplierStr.substring(1, supplierStr.length() - 1).replace(" ", ""));
                                //proParams.put("SupplierID", "string," + supplierStr.substring(1, supplierStr.length() - 1).replace(" ", ""));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                //System.out.println(proParams.toJSONString());
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("集配区") == 0) {
                                    List<Object> jpqjc = commonController.proceduceQuery("tbGetJPQJCPartListForZDTK_MulSupplier", proParams, "com.cn.bean.pro.JPQJCForZDTK", opt.getConnect());
                                    for (Object obj : jpqjc) {
                                        JPQJCForZDTK zdtk = (JPQJCForZDTK) obj;
                                        limitMap.put(zdtk.getSupplierID() + "_" + zdtk.getPartCode(), zdtk.getJpqJCAmount());
                                    }
                                }

                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("线旁") == 0) {
                                    List<Object> xpjc = commonController.proceduceQuery("tbGetXPJCPartListForXPTK_MulSupplier", proParams, "com.cn.bean.pro.XPJCForZDTK", opt.getConnect());
                                    for (Object obj : xpjc) {
                                        XPJCForZDTK zdtk = (XPJCForZDTK) obj;
                                        limitMap.put(zdtk.getSupplierID() + "_" + zdtk.getPartCode(), zdtk.getXpJCAmount());
                                    }
                                }

                                String result = "{}";
                                if (detailList != null && detailList.size() > 0) {
                                    for (Object obj : detailList) {
                                        ZDBackWareHouseList zdtk = (ZDBackWareHouseList) obj;
                                        int tkAmount = Integer.valueOf((null == limitMap.get(zdtk.getSupplierID() + "_" + zdtk.getPartCode())) ? "0" : limitMap.get(zdtk.getSupplierID() + "_" + zdtk.getPartCode())) + zdtk.getZdTKAmount();
                                        zdtk.setTkAmount(tkAmount);
                                    }
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
                                    if (Units.strIsEmpty(zDBackWareHouse.getZdTKAuditTime())) {
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
                            //json = queryOperate("com.cn.bean.in.", "view", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "view", "ZDBackWareHouse", "ZDBackWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "tkAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "退库数量"};
                                int[] keysWidth = {20, 30, 30, 20};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                proParams.put("ZDCustomerID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("zdCustomerID"));

                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("集配区") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetJPQJCPartListForZDTK", proParams, "com.cn.bean.pro.JPQJCForZDTK", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        InWareHouseController controller = new InWareHouseController();
                                        //HashMap minInboundBatchMap = controller.getSupplierInboundBatch(JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                        for (Object obj : list) {
                                            JPQJCForZDTK zdtk = (JPQJCForZDTK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + zdtk.getPartCode()), PartBaseInfo.class);
                                            zdtk.setPartID(baseInfo.getPartID());
                                            zdtk.setPartName(baseInfo.getPartName());
                                            zdtk.setInboundBatch(controller.getSupplierInboundBatch(proParams.getString("SupplierID")));
                                            /*
                                            if (minInboundBatchMap.containsKey(zdtk.getPartCode())
                                                    && minInboundBatchMap.get(zdtk.getPartCode()) != null) {
                                                zdtk.setInboundBatch((String) minInboundBatchMap.get(zdtk.getPartCode()));
                                            } else {
                                                //TODO 这种生成批次的方式不对
                                                zdtk.setInboundBatch(Units.getNowTimeNoSeparator());
                                            }
                                             */
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            zdtk.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "jpqJCAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.JPQJCForZDTK", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("ycFLocation").compareTo("线旁") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetXPJCPartListForXPTK", proParams, "com.cn.bean.pro.XPJCForZDTK", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        InWareHouseController controller = new InWareHouseController();

                                        for (Object obj : list) {
                                            XPJCForZDTK zdtk = (XPJCForZDTK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + zdtk.getPartCode()), PartBaseInfo.class);
                                            zdtk.setPartID(baseInfo.getPartID());
                                            zdtk.setPartName(baseInfo.getPartName());
                                            zdtk.setInboundBatch(controller.getSupplierInboundBatch(proParams.getString("SupplierID")));
                                            /*
                                            if (minInboundBatchMap.containsKey(zdtk.getPartCode())
                                                    && minInboundBatchMap.get(zdtk.getPartCode()) != null) {
                                                zdtk.setInboundBatch((String) minInboundBatchMap.get(zdtk.getPartCode()));
                                            } else {
                                                //TODO 这种生成批次的方式不对
                                                zdtk.setInboundBatch(Units.getNowTimeNoSeparator());
                                            }
                                             */
//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            zdtk.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "xpJCAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.XPJCForZDTK", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                            }
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("zdTKAuditStaffName", session.getAttribute("user"));
                            obj.put("zdTKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.in.", "ZDBackWareHouse", "update", opt.getConnect());
                            //System.out.println(Arrays.toString(updateResult.toArray()));
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.in.", "ZDBackWareHouse", "delete", opt.getConnect());
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
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "ZDBackWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "ZDBackWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "zdBackWareHouseID") + "]", "com.cn.bean.in.", "ZDBackWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "ZDBackWareHouseList", update, add, delete, "data");
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
                    String whereCase = "(InspectionTime is null) and " + whereCase1;
                    switch (operation) {
                        case "create": {
                            if (isHistory == 0) {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", whereCase, "DJInWareHouseID", opt.getConnect());
                                json = createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", datas, rely, whereCase, "DJInWareHouseID", opt.getConnect());
                            } else {
                                //json = createOperateWithFilter(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", whereCase1, "DJInWareHouseID", opt.getConnect());
                                json = createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "DJInWareHouseList", datas, rely, whereCase1, "DJInWareHouseID", opt.getConnect());
                            }
                            break;
                        }
                        case "request_page": {
                            if (isHistory == 0) {
                                //json = queryOperateWithFilter("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                                json = queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                //json = queryOperateWithFilter("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                                json = queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_on_date": {
                            if (isHistory == 0) {
                                json = queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            } else {
                                json = queryOnDateOperate("com.cn.bean.move.", "view", "DJInWareHouseList", "DJInWareHouseID", datas, rely, whereCase1, true, opt.getConnect(), pageSize, pageIndex);
                            }
                            break;
                        }
                        case "request_detail": {
                            Class objClass = Class.forName("com.cn.bean.move." + "DJInWareHouseList");
                            Method method = objClass.getMethod("getRecordCount", null);
                            String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                            List<Object> list = commonController.dataBaseQuery("view", "com.cn.bean.move.", "DJInWareHouseList", "*", whereSql, pageSize, pageIndex, "DJInWareHouseID", 0, opt.getConnect());
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
                            //json = createOperate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "FXOutWareHouse", "FXOutWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "FXOutWareHouse", datas, rely, "", "FXOutWareHouseID", opt.getConnect());
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
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.move.", "FXOutWareHouse", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.move.", "FXOutWareHouse", "delete", opt.getConnect());
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
                        case "auditItem": {
                            JSONArray arrayParam = JSONArray.parseArray(datas);
                            String fxOutWareHouseID = arrayParam.getJSONObject(0).getString("fxOutWareHouseID");
                            String mainTabWhereSql = "FXOutWareHouseID = '" + fxOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "FXOutWareHouse", "*", mainTabWhereSql, 11, 1, "FXOutWareHouseID", 0, opt.getConnect());
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
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "FXOutWareHouseList", "update", opt.getConnect()).get(0);
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
                            //json = queryOperate("com.cn.bean.move.", "view", "FXOutWareHouseList", "FXOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            String fxOutWareHouseID = JSONObject.parseObject(rely).getString("fxOutWareHouseID");
                            String mainTabWhereSql = "FXOutWareHouseID = '" + fxOutWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.move.", "FXOutWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "FXOutWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                FXOutWareHouse fXOutWareHouse = (FXOutWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("良品") == 0) {
                                    List<Object> lpfxc = commonController.proceduceQuery("spGetKFJCLPListForFXCK", proParams, "com.cn.bean.pro.KFJCLPForFXCK", opt.getConnect());
                                    if (lpfxc != null && lpfxc.size() > 0) {
                                        for (Object obj : lpfxc) {
                                            KFJCLPForFXCK fxck = (KFJCLPForFXCK) obj;
                                            limitMap.put(fxck.getPartCode(), fxck.getKfJCLp());
                                        }
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("不良品") == 0) {
                                    List<Object> blpfxc = commonController.proceduceQuery("spGetKFJCBLPListForFXCK", proParams, "com.cn.bean.pro.KFJCBLPForFXCK", opt.getConnect());
                                    if (blpfxc != null && blpfxc.size() > 0) {
                                        for (Object obj : blpfxc) {
                                            KFJCBLPForFXCK fxck = (KFJCBLPForFXCK) obj;
                                            limitMap.put(fxck.getPartCode(), fxck.getBlPAmount());
                                        }
                                    }
                                }

                                Class objClass = Class.forName("com.cn.bean.move." + "FXOutWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.move.", "FXOutWareHouseList", "*", whereSql, pageSize, pageIndex, "FXOutWareHouseID", 0, opt.getConnect());

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
                            //json = queryOperate("com.cn.bean.move.", "view", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.move.", "view", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "view", "FXOutWareHouse", "FXOutWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "fxAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "返修数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCLPListForFXCK", proParams, "com.cn.bean.pro.KFJCLPForFXCK", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        for (Object obj : list) {
                                            KFJCLPForFXCK fxck = (KFJCLPForFXCK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxck.getPartCode()), PartBaseInfo.class);
                                            fxck.setPartID(baseInfo.getPartID());
                                            fxck.setPartName(baseInfo.getPartName());

//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }

                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "kfJCLp"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCLPForFXCK", keys, keysName, keysWidth, fieldsName, target, rely);
                                    } else {
                                        json = Units.objectToJson(-1, "数据为空!", null);
                                    }
                                }
                                if (JSONObject.parseObject(paramsJson.getString("rely")).getString("partState").compareTo("不良品") == 0) {
                                    List<Object> list = commonController.proceduceQuery("spGetKFJCBLPListForFXCK", proParams, "com.cn.bean.pro.KFJCBLPForFXCK", opt.getConnect());
                                    if (list != null && list.size() > 0) {
                                        for (Object obj : list) {
                                            KFJCBLPForFXCK fxck = (KFJCBLPForFXCK) obj;
                                            PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxck.getPartCode()), PartBaseInfo.class);
                                            fxck.setPartID(baseInfo.getPartID());
                                            fxck.setPartName(baseInfo.getPartName());

//                                            PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                            fxck.setWareHouseManagerName(category.getWareHouseManagerName());
                                        }
                                        String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "blPAmount"};
                                        json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCBLPForFXCK", keys, keysName, keysWidth, fieldsName, target, rely);
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
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXOutWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXOutWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "fxOutWareHouseID") + "]", "com.cn.bean.move.", "FXOutWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.in.", "FXOutWareHouseList", update, add, delete, "data");
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
                            //json = createOperate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "FXInWareHouse", "FXInWareHouseID", opt.getConnect());
                            json = createOperateOnDate(20, "view", "com/cn/json/move/", "com.cn.bean.move.", "FXInWareHouse", datas, rely, "", "FXInWareHouseID", opt.getConnect());
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
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.in.", "FXInWareHouse", "*", mainTabWhereSql, 11, 1, "FXInWareHouseID", 0, opt.getConnect());
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
                                    int result = commonController.dataBaseOperate(updateArray.toJSONString(), "com.cn.bean.in.", "FXInWareHouseList", "update", opt.getConnect()).get(0);
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
                            //json = queryOperate("com.cn.bean.move.", "view", "FXInWareHouseList", "FXInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            String djInWareHouseID = JSONObject.parseObject(rely).getString("fxInWareHouseID");
                            String mainTabWhereSql = "FXInWareHouseID = '" + djInWareHouseID + "'";
                            List<Object> list = commonController.dataBaseQuery("table", "com.cn.bean.move.", "FXInWareHouse", "*", mainTabWhereSql, pageSize, pageIndex, "FXInWareHouseID", 0, opt.getConnect());
                            if (list != null && list.size() > 0) {
                                FXInWareHouse fXInWareHouse = (FXInWareHouse) list.get(0);

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                HashMap<String, String> limitMap = new HashMap<String, String>();
                                List<Object> fxrkList = commonController.proceduceQuery("spGetKFJCFxpListForFXRK", proParams, "com.cn.bean.pro.KFJCFXPForFXRK", opt.getConnect());
                                if (fxrkList != null && fxrkList.size() > 0) {
                                    for (Object obj : fxrkList) {
                                        KFJCFXPForFXRK fxrk = (KFJCFXPForFXRK) obj;
                                        limitMap.put(fxrk.getPartCode(), String.valueOf(fxrk.getFxPAmount()));
                                    }
                                }

                                Class objClass = Class.forName("com.cn.bean.move." + "FXInWareHouseList");
                                Method method = objClass.getMethod("getRecordCount", null);
                                String whereSql = commonController.getWhereSQLStr(objClass, datas, rely, true);
                                List<Object> detailList = commonController.dataBaseQuery("view", "com.cn.bean.move.", "FXInWareHouseList", "*", whereSql, pageSize, pageIndex, "FXInWareHouseID", 0, opt.getConnect());

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
                            //json = queryOperate("com.cn.bean.move.", "view", "FXInWareHouse", "FXInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.move.", "view", "FXInWareHouse", "FXInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "view", "FXInWareHouse", "FXInWareHouseID", datas, rely, "", true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = new JSONObject();
                            obj.put("fxRKAuditStaffName", session.getAttribute("user"));
                            obj.put("fxRKAuditTime", Units.getNowTime());
                            String auditInfo = "[" + obj.toJSONString() + "," + datas + "]";
                            ArrayList<Integer> updateResult = commonController.dataBaseOperate(auditInfo, "com.cn.bean.move.", "FXInWareHouse", "update", opt.getConnect());
                            if (updateResult.get(0) == 0) {
                                json = Units.objectToJson(0, "审核成功!", obj.toJSONString());
                            } else {
                                json = Units.objectToJson(-1, "审核失败!", null);
                            }
                            break;
                        }
                        case "delete": {
                            if (!Units.strIsEmpty(delete)) {
                                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, "com.cn.bean.move.", "FXInWareHouse", "delete", opt.getConnect());
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
                        case "submit": {
                            String operate = paramsJson.getString("operate");
                            if (operate.compareToIgnoreCase("add") == 0) {
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "FXInWareHouse", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "FXInWareHouseList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "fxOutWareHouseID") + "]", "com.cn.bean.move.", "FXInWareHouse", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.move.", "FXInWareHouseList", update, add, delete, "data");
                            }
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
                                String[] keys = {"partCode", "partID", "partName", "inboundBatch", "fxAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "入库批次", "返修数量"};
                                int[] keysWidth = {20, 20, 20, 20, 20};

                                JSONObject proParams = new JSONObject();
                                proParams.put("SupplierID", "string," + JSONObject.parseObject(paramsJson.getString("rely")).getString("supplierID"));
                                List<Object> list = commonController.proceduceQuery("spGetKFJCFxpListForFXRK", proParams, "com.cn.bean.pro.KFJCFXPForFXRK", opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    for (Object obj : list) {
                                        KFJCFXPForFXRK fxrk = (KFJCFXPForFXRK) obj;
                                        PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + fxrk.getPartCode()), PartBaseInfo.class);
                                        fxrk.setPartID(baseInfo.getPartID());
                                        fxrk.setPartName(baseInfo.getPartName());

//                                        PartCategory category = JSONObject.parseObject(RedisAPI.get("partCategory_" + baseInfo.getPartCategoryName()), PartCategory.class);
//                                        fxrk.setWareHouseManagerName(category.getWareHouseManagerName());
                                    }

                                    String[] fieldsName = {"partCode", "partID", "partName", "inboundBatch", "fxPAmount"};
                                    json = getSpecialTableJsonStr(list, "com.cn.bean.pro.KFJCFXPForFXRK", keys, keysName, keysWidth, fieldsName, target, rely);
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
                            //json = createOperateWithFilter(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "AdjustAccount", "TZYMonth in (select TZYMonth from tblXCAdjustAccountList)", "TZYMonth", opt.getConnect());
                            json = createOperateOnDate(20, "table", "com/cn/json/move/", "com.cn.bean.move.", "AdjustAccount", datas, rely, whereCase, "TZYMonth", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.move.", "view", "KFAdjustAccountList", "TZYMonth", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            //json = queryOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            json = queryOnDateOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.move.", "table", "AdjustAccount", "TZYMonth", datas, rely, whereCase, true, opt.getConnect(), pageSize, pageIndex);
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
                                int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.move.", "AdjustAccount", "add", opt.getConnect()).get(0);
                                if (result == 0) {
                                    result = commonController.dataBaseOperate(details, "com.cn.bean.move.", "KFAdjustAccountList", "add", opt.getConnect()).get(0);
                                    if (result == 0) {
                                        json = Units.objectToJson(0, "数据添加成功!", null);
                                    } else {
                                        commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, "tzYMonth") + "]", "com.cn.bean.move.", "AdjustAccount", "delete", opt.getConnect());
                                        json = Units.objectToJson(-1, "明细添加失败!", null);
                                    }
                                } else {
                                    json = Units.objectToJson(-1, "数据添加失败!", null);
                                }
                            }
                            if (operate.compareToIgnoreCase("modify") == 0) {
                                json = submitOperate("com.cn.bean.move.", "KFAdjustAccountList", update, add, delete, "data");
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
                            JSONObject obj = new JSONObject();
                            obj.put("jhConfirm", "true");
                            result = commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", opt.getConnect()).get(0);
                            if (result == 0) {
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
                            JSONObject obj = new JSONObject();
                            obj.put("jhConfirm", "true");
                            int result = commonController.dataBaseOperate("[" + obj.toJSONString() + "," + datas + "]", "com.cn.bean.out.", "JHOutWareHouse", "update", opt.getConnect()).get(0);
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
                            ArrayList<JHOutWareHouseList> result = controller.importData(importData, item);
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
                        case "confirm": {
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
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.out.", "FJHOutWareHouseList", null);
                            break;
                        }
                        case "submit": {
                            ArrayList<Object> submitData = new ArrayList<>();
                            submitData.addAll(JSONObject.parseArray(details, FJHOutWareHouseList.class));

                            JHOutWareHouseController controller = new JHOutWareHouseController();
                            ArrayList<JHOutWareHouseList> result = controller.importData(submitData, item);
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

                //<editor-fold desc="备货确认">
                case "备货确认": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "view", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouseList", "JHOutWareHouseID", opt.getConnect());
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.", "view", "BHProgressList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "audit": {
                            JSONObject obj = JSONObject.parseObject(datas);
                            obj.put("djRKAuditStaffName", session.getAttribute("user"));
                            obj.put("djRKAuditTime", Units.getNowTime());
                            //System.out.println("auditInfo:" + obj.toJSONString());
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
                //<editor-fold desc="部品收发存总表_spGetSFCTotalData">
                case "部品收发存总表": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }
                            json = reportOperate(operateType, "spGetSFCTotalData", "SFCTotalData", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForDjpRK", "RKListForDjpRK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForSJCK", "RKListForSJCK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForSJTK", "RKListForSJTK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForLpRK", "RKListForLpRK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForBLpRK", "RKListForBLpRK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForFxpRK", "RKListForFxpRK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetRKListForFxpRK", "RKListForFxpRK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetCKListForFxpCK", "CKListForFxpCK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetCKListForFxpCK", "CKListForFxpCK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetCKListForZCJHCK", "CKListForZCJHCK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetCKListForLSDHCK", "CKListForZCJHCK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetCKListForJPSX", "CKListForJPSX", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetTHListForBPTH", "THListForBPTH", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetCKListForFJHCK", "CKListForFJHCK", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetKFJCListForLp", "KFJCListForLp", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetKFJCListForBLp", "KFJCListForBLp", proParams, new ReportItemOperate() {
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
                            //TODO spGetKFJCListForDjp存储过程执行出错
                            json = reportOperate(operateType, "spGetKFJCListForDjp", "KFJCListForDjp", proParams, new ReportItemOperate() {
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
                            //TODO spGetKFJCListForDjp存储过程执行出错
                            json = reportOperate(operateType, "spGetKFJCListForSjp", "KFJCListForSjp", proParams, new ReportItemOperate() {
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
                            //TODO spGetKFJCListForDjp存储过程执行出错
                            json = reportOperate(operateType, "spGetKFJCListForFxp", "KFJCListForFxp", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetKFQCFenLuData", "KFQCFenLuData", proParams, new ReportItemOperate() {
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
                            json = reportOperate(operateType, "spGetTKFenLuData", "TKFenLuData", proParams, new ReportItemOperate() {
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

                //<editor-fold desc="部品计划出库分录_spGetCKFenLuData">
                case "部品计划出库分录": {
                    switch (operation) {
                        case "create": {
                            JSONObject proParams = new JSONObject();
                            if (!Units.strIsEmpty(start) && !Units.strIsEmpty(end)) {
                                proParams.put("BeginTime", "string," + start);
                                proParams.put("Endtime", "string," + end);
                            }

                            json = reportOperate(operateType, "spGetCKFenLuData", "CKFenLuData", proParams, new ReportItemOperate() {
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

                                json = reportOperate(operateType, "spGetCKDetailListForZCJHCK", "CKListForZCJHCK", proParams, new ReportItemOperate() {
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

                            json = reportOperate(operateType, "spGetRKFenLuData", "RKFenLuData", proParams, new ReportItemOperate() {
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
