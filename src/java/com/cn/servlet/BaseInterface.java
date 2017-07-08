/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.AreaLedIPInfo;
import com.cn.bean.ClassDescription;
import com.cn.bean.Customer;
import com.cn.bean.FieldDescription;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartBomInfo;
import com.cn.bean.PartCategory;
import com.cn.bean.PartStore;
import com.cn.controller.CommonController;
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
public class BaseInterface extends HttpServlet {

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
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        String json = null;
        //logger.info(Units.getIpAddress(request) + "accept:" + subUri + ",time:" + (new Date().getTime()));
        
        try {
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
            String operateType = paramsJson.getString("type");
            String start = paramsJson.getString("start");
            String end = paramsJson.getString("end");
            int isHistory = paramsJson.getIntValue("isHistory");
            int pageIndex = paramsJson.getIntValue("pageIndex");
            int pageSize = paramsJson.getIntValue("pageSize");
            
            //logger.info(Units.getIpAddress(request) + ",accept:" + module + ",time:" + (new Date().getTime()));
            
            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";
            
            /*验证是否登陆*/
            if ((!"userLogin".equals(module) || !"版本信息".equals(module)) && session.getAttribute("user") == null) {
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
                 * ***************************************平台管理**************************************
                 */
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
                                //System.out.println("datas:" + datas + ",pageSize:" + pageSize + ",pageIndex:" + pageIndex);
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品基础信息到Redis中*/
                                        List<Object> partBaseInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                        Iterator<Object> iterator = partBaseInfo.iterator();
                                        while (iterator.hasNext()) {
                                            PartBaseInfo baseInfo = (PartBaseInfo) iterator.next();
                                            RedisAPI.set("partBaseInfo_" + baseInfo.getPartCode().toLowerCase(), JSONObject.toJSONString(baseInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PartBaseInfo", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品基础信息到Redis中*/
                                        List<Object> partBaseInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                        Iterator<Object> iterator = partBaseInfo.iterator();
                                        while (iterator.hasNext()) {
                                            PartBaseInfo baseInfo = (PartBaseInfo) iterator.next();
                                            RedisAPI.set("partBaseInfo_" + baseInfo.getPartCode().toLowerCase(), JSONObject.toJSONString(baseInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入客户基础信息到Redis中*/
                                        List<Object> customerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, opt.getConnect());
                                        Iterator<Object> iterator1 = customerList.iterator();
                                        while (iterator1.hasNext()) {
                                            Customer customer = (Customer) iterator1.next();
                                            RedisAPI.set("customer_" + customer.getCustomerID(), JSONObject.toJSONString(customer));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入客户基础信息到Redis中*/
                                        List<Object> customerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, opt.getConnect());
                                        Iterator<Object> iterator1 = customerList.iterator();
                                        while (iterator1.hasNext()) {
                                            Customer customer = (Customer) iterator1.next();
                                            RedisAPI.set("customer_" + customer.getCustomerID(), JSONObject.toJSONString(customer));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品存放地址信息到Redis中*/
                                        List<Object> partStoreList = commonController.dataBaseQuery("table", "com.cn.bean.", "PartStore", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                        Iterator<Object> iterator4 = partStoreList.iterator();
                                        while (iterator4.hasNext()) {
                                            PartStore partStore = (PartStore) iterator4.next();
                                            RedisAPI.set("partStore_" + partStore.getSupplierID() + "_" + partStore.getPartCode().toLowerCase(), JSONObject.toJSONString(partStore));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品存放地址信息到Redis中*/
                                        List<Object> partStoreList = commonController.dataBaseQuery("table", "com.cn.bean.", "PartStore", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                        Iterator<Object> iterator4 = partStoreList.iterator();
                                        while (iterator4.hasNext()) {
                                            PartStore partStore = (PartStore) iterator4.next();
                                            RedisAPI.set("partStore_" + partStore.getSupplierID() + "_" + partStore.getPartCode().toLowerCase(), JSONObject.toJSONString(partStore));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入出入库盛具信息到Redis中*/
                                        List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                        Iterator<Object> iterator2 = containerInfoList.iterator();
                                        while (iterator2.hasNext()) {
                                            GYSPartContainerInfo containerInfo = (GYSPartContainerInfo) iterator2.next();
                                            RedisAPI.set(containerInfo.getSupplierID() + "_" + containerInfo.getPartCode().toLowerCase(), JSONObject.toJSONString(containerInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入出入库盛具信息到Redis中*/
                                        List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                        Iterator<Object> iterator2 = containerInfoList.iterator();
                                        while (iterator2.hasNext()) {
                                            GYSPartContainerInfo containerInfo = (GYSPartContainerInfo) iterator2.next();
                                            RedisAPI.set(containerInfo.getSupplierID() + "_" + containerInfo.getPartCode().toLowerCase(), JSONObject.toJSONString(containerInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            if (target.compareToIgnoreCase("partCategoryName") == 0) {
                                String[] keys = {"partCategoryName"};
                                String[] keysName = {"部品类别"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"partCategoryName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品分类信息到Redis中*/
                                        List<Object> partCategory = commonController.dataBaseQuery("table", "com.cn.bean.", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategoryName", 0, opt.getConnect());
                                        Iterator<Object> iterator5 = partCategory.iterator();
                                        while (iterator5.hasNext()) {
                                            PartCategory category = (PartCategory) iterator5.next();
                                            RedisAPI.set("partCategory_" + category.getPartCategoryName(), JSONObject.toJSONString(category));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品分类信息到Redis中*/
                                        List<Object> partCategory = commonController.dataBaseQuery("table", "com.cn.bean.", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategoryName", 0, opt.getConnect());
                                        Iterator<Object> iterator5 = partCategory.iterator();
                                        while (iterator5.hasNext()) {
                                            PartCategory category = (PartCategory) iterator5.next();
                                            RedisAPI.set("partCategory_" + category.getPartCategoryName(), JSONObject.toJSONString(category));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入存放地址信息到Redis中*/
                                        List<Object> ledIpInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", Integer.MAX_VALUE, 1, "addressCode", 0, opt.getConnect());
                                        Iterator<Object> iterator3 = ledIpInfoList.iterator();
                                        while (iterator3.hasNext()) {
                                            AreaLedIPInfo ledIpInfo = (AreaLedIPInfo) iterator3.next();
                                            RedisAPI.set("ledIpInfo_" + ledIpInfo.getAddressCode().toLowerCase(), JSONObject.toJSONString(ledIpInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入存放地址信息到Redis中*/
                                        List<Object> ledIpInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", Integer.MAX_VALUE, 1, "addressCode", 0, opt.getConnect());
                                        Iterator<Object> iterator3 = ledIpInfoList.iterator();
                                        while (iterator3.hasNext()) {
                                            AreaLedIPInfo ledIpInfo = (AreaLedIPInfo) iterator3.next();
                                            RedisAPI.set("ledIpInfo_" + ledIpInfo.getAddressCode().toLowerCase(), JSONObject.toJSONString(ledIpInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
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
                            if (target.compareToIgnoreCase("bhEmployeeName") == 0) {
                                String[] keys = {"bhEmployeeName"};
                                String[] keysName = {"备货员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                rely = rely.replace("bhEmployeeType", "employeeType");
                                json = queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("lhEmployeeName") == 0) {
                                String[] keys = {"lhEmployeeName"};
                                String[] keysName = {"领货员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                rely = rely.replace("lhEmployeeType", "employeeType");
                                json = queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("psEmployeeName") == 0) {
                                String[] keys = {"psEmployeeName"};
                                String[] keysName = {"配送员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                rely = rely.replace("psEmployeeType", "employeeType");
                                json = queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="总成BOM信息">
                case "总成BOM信息": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBomInfo", "ZCPartCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "PartBomInfo", "ZCPartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "PartBomInfo", importPath + fileName, opt.getConnect());
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入总成BOM信息到Redis中*/
                                        List<Object> partBomInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBomInfo", "*", "", Integer.MAX_VALUE, 1, "ZCPartCode", 0, opt.getConnect());
                                        RedisAPI.delKeys("bomInfo_*");
                                        Iterator<Object> iterator6 = partBomInfo.iterator();
                                        while (iterator6.hasNext()) {
                                            PartBomInfo bomInfo = (PartBomInfo) iterator6.next();
                                            RedisAPI.push("bomInfo_" + bomInfo.getZcPartCode().toLowerCase(), JSONObject.toJSONString(bomInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "PartBomInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "PartBomInfo", (ArrayList<Object>) queryData("com.cn.bean.", "table", "PartBomInfo", "ZCPartCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "PartBomInfo", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入总成BOM信息到Redis中*/
                                        List<Object> partBomInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBomInfo", "*", "", Integer.MAX_VALUE, 1, "ZCPartCode", 0, opt.getConnect());
                                        RedisAPI.delKeys("bomInfo_*");
                                        Iterator<Object> iterator6 = partBomInfo.iterator();
                                        while (iterator6.hasNext()) {
                                            PartBomInfo bomInfo = (PartBomInfo) iterator6.next();
                                            RedisAPI.push("bomInfo_" + bomInfo.getZcPartCode().toLowerCase(), JSONObject.toJSONString(bomInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("zcPartCode") == 0) {
                                String[] keys = {"zcPartCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("detailPartCode") == 0) {
                                String[] keys = {"detailPartCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            //System.out.println("json:" + json);
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="版本信息">
                case "版本信息": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Version", "VarsionID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.", "table", "Version", "VarsionID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.", "Version", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportTemplate("com.cn.bean.", "Version", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.", "Version", (ArrayList<Object>) queryData("com.cn.bean.", "table", "Version", "VarsionID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.", "Version", update, add, delete, "data");
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //</editor-fold>
            }
            //logger.info(Units.getIpAddress(request) + ",response:" + module + ",time:" + (new Date().getTime()));
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
        CommonController commonController = new CommonController();
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
        CommonController commonController = new CommonController();
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
                        if (Units.strIsEmpty(Units.getStringCellValue(cell))) {
                            field.set(object, 0);
                        } else {
                            field.set(object, Double.valueOf(Units.getStringCellValue(cell)).intValue());
                        }
                    }
                } else if (field.getType() == float.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        if (Units.strIsEmpty(Units.getStringCellValue(cell))) {
                            field.set(object, 0);
                        } else {
                            field.set(object, Double.valueOf(Units.getStringCellValue(cell)).floatValue());
                        }
                    }
                } else if (field.getType() == double.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        if (Units.strIsEmpty(Units.getStringCellValue(cell))) {
                            field.set(object, 0);
                        } else {
                            field.set(object, Double.valueOf(Units.getStringCellValue(cell)));
                        }
                    }
                } else if (field.getType() == boolean.class) {
                    if (cell == null) {
                        field.set(object, false);
                    } else {
                        if (Units.strIsEmpty(Units.getCellValue(cell))) {
                            field.set(object, false);
                        } else {
                            field.set(object, Boolean.valueOf(Units.getStringCellValue(cell)));
                        }
                    }
                } else {
                    if (cell == null) {
                        field.set(object, null);
                    } else {
                        field.set(object, Units.getStringCellValue(cell));
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
