/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.AreaLedIPInfo;
import com.cn.bean.Container;
import com.cn.bean.Customer;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartBomInfo;
import com.cn.bean.PartCategory;
import com.cn.bean.PartStore;
import com.cn.controller.CommonController;
import com.cn.controller.InterfaceController;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import com.cn.util.Units;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        InterfaceController interfaceController = new InterfaceController();
        DatabaseOpt opt = new DatabaseOpt();
        String json = null;
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

            //logger.info(Units.getIpAddress(request) + ",accept:" + module + ",time:" + (new Date().getTime()));
            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";
            String filePath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
            String servletPath = getServletContext().getContextPath();

            /*验证是否登陆*/
            if ((!"userLogin".equals(module) || !"版本信息".equals(module))
                    && (session.getAttribute("user") == null || session.getAttribute("loginType") == null)) {
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformCompanyInfo", "CompanyID", DatabaseOpt.BASE);
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PlatformCompanyInfo", update, add, delete, "base");
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PlatformCompanyInfo", "CompanyID", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PlatformCompanyInfo", importPath + fileName, DatabaseOpt.BASE);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PlatformCompanyInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PlatformCompanyInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PlatformCompanyInfo", "CompanyID", datas, DatabaseOpt.BASE, Integer.MAX_VALUE, 1));
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatFormDataBaseInfo", "DataBaseID", DatabaseOpt.BASE);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PlatFormDataBaseInfo", "DataBaseID", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PlatFormDataBaseInfo", importPath + fileName, DatabaseOpt.BASE);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PlatFormDataBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PlatFormDataBaseInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PlatFormDataBaseInfo", "DataBaseID", datas, DatabaseOpt.BASE, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("CompanyID") == 0) {
                                String[] keys = {"companyID", "companyName"};
                                String[] keysName = {"公司编号", "公司名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"companyID", "companyName"};
                                //System.out.println("datas:" + datas + ",pageSize:" + pageSize + ",pageIndex:" + pageIndex);
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformCompanyInfo", "CompanyID", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PlatFormDataBaseInfo", update, add, delete, "base");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformUserInfo", "UserLoginAccount", DatabaseOpt.BASE);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PlatformUserInfo", importPath + fileName, DatabaseOpt.BASE);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PlatformUserInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PlatformUserInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "view", "PlatformUserInfo", "UserLoginAccount", datas, DatabaseOpt.BASE, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("UserLoginDBName") == 0) {
                                String[] keys = {"userLoginDBName", "companyName", "companyID"};
                                String[] keysName = {"数据库名", "公司名称", "公司ID"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"masterDataBaseName", "companyName", "companyID"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "view", "PlatFormDataBaseInfo", "CompanyID", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PlatformUserInfo", update, add, delete, "base");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformRole", "RoleCode", DatabaseOpt.BASE);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PlatformRole", importPath + fileName, DatabaseOpt.BASE);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PlatformRole", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PlatformRole", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "view", "PlatformRole", "RoleCode", datas, DatabaseOpt.BASE, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PlatformRole", update, add, delete, "base");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformRoleRight", "RoleCode", DatabaseOpt.BASE);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PlatformRoleRight", "RoleCode", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PlatformRoleRight", importPath + fileName, DatabaseOpt.BASE);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PlatformRoleRight", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PlatformRoleRight", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PlatformRoleRight", "RoleCode", datas, DatabaseOpt.BASE, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PlatformRoleRight", update, add, delete, "base");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                String[] keys = {"roleCode", "roleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("RightCode") == 0) {
                                String[] keys = {"rightCode", "rightName"};
                                String[] keysName = {"模块代码", "模块名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"rightCode", "rightName"};
                                String whereCase = "RightCode not in (select RightCode from tblPlatformRoleRight where RoleCode = '" + Units.getSubJsonValue(rely, "roleCode") + "')";
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformRight", "RightCode", datas, rely, whereCase, true, DatabaseOpt.BASE, Integer.MAX_VALUE, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformUserRole", "UserLoginAccount", DatabaseOpt.BASE);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PlatformUserRole", "UserLoginAccount", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PlatformUserRole", importPath + fileName, DatabaseOpt.BASE);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PlatformUserRole", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PlatformUserRole", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PlatformUserRole", "UserLoginAccount", datas, DatabaseOpt.BASE, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PlatformUserRole", update, add, delete, "base");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                String[] keys = {"roleCode", "roleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("UserLoginAccount") == 0) {
                                String[] keys = {"userLoginAccount"};
                                String[] keysName = {"用户名"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"userLoginAccount"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBaseInfo", "PartCode", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PartBaseInfo", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品基础信息到Redis中*/
                                        List<Object> partBaseInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PartBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PartBaseInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("autoStylingName") == 0) {
                                String[] keys = {"autoStylingName"};
                                String[] keysName = {"使用车型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"autoStylingName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "AutoStyling", "AutoStylingName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PartBaseInfo", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品基础信息到Redis中*/
                                        List<Object> partBaseInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.createOperate(15, "view", "com/cn/json/", "com.cn.bean.", "Customer", "CustomerID", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "view", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "Customer", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入客户基础信息到Redis中*/
                                        List<Object> customerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "Customer", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "Customer", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "view", "Customer", "CustomerID", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("customerTypeName") == 0) {
                                String[] keys = {"customerTypeName"};
                                String[] keysName = {"客户类型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"customerTypeName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "CustomerType", "CustomerTypeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("shMethodName") == 0) {
                                String[] keys = {"shMethodName"};
                                String[] keysName = {"送货方式"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"shMethodName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "SHMethod", "SHMethodName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "Customer", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入客户基础信息到Redis中*/
                                        List<Object> customerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Employee", "EmployeeName", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "Employee", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "Employee", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "Employee", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "Employee", "EmployeeName", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "Employee", update, add, delete, "data");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("employeeType") == 0) {
                                String[] keys = {"employeeTypeCode", "employeeType"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartStore", "SupplierID", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PartStore", "SupplierID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PartStore", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品存放地址信息到Redis中*/
                                        List<Object> partStoreList = commonController.dataBaseQuery("table", "com.cn.bean.", "PartStore", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PartStore", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PartStore", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PartStore", "SupplierID", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PartStore", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品存放地址信息到Redis中*/
                                        List<Object> partStoreList = commonController.dataBaseQuery("table", "com.cn.bean.", "PartStore", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "view", "com/cn/json/", "com.cn.bean.", "GYSPartContainerInfo", "SupplierID", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "view", "GYSPartContainerInfo", "SupplierID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "GYSPartContainerInfo", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入出入库盛具信息到Redis中*/
                                        List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "GYSPartContainerInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "GYSPartContainerInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "view", "GYSPartContainerInfo", "SupplierID", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "GYSPartContainerInfo", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入出入库盛具信息到Redis中*/
                                        List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Customer", "CustomerID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
                            }
                            if (target.compareToIgnoreCase("inboundContainerName") == 0) {
                                String[] keys = {"inboundContainerName"};
                                String[] keysName = {"盛具名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"containerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("outboundContainerName") == 0) {
                                String[] keys = {"outboundContainerName"};
                                String[] keysName = {"盛具名称"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"ContainerName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCategoryName") == 0) {
                                String[] keys = {"partCategoryName"};
                                String[] keysName = {"部品类别"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"partCategoryName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Container", "ContainerName", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "Container", "ContainerName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "Container", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入盛具信息到Redis中*/
                                        List<Object> containerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Container", "*", "", Integer.MAX_VALUE, 1, "ContainerName", 0, DatabaseOpt.DATA);
                                        Iterator<Object> iterator3 = containerList.iterator();
                                        while (iterator3.hasNext()) {
                                            Container containerInfo = (Container) iterator3.next();
                                            RedisAPI.set("container_" + containerInfo.getContainerName(), JSONObject.toJSONString(containerInfo));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "Container", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "Container", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "Container", "ContainerName", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "Container", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入盛具信息到Redis中*/
                                        List<Object> containerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Container", "*", "", Integer.MAX_VALUE, 1, "ContainerName", 0, DatabaseOpt.DATA);
                                        Iterator<Object> iterator3 = containerList.iterator();
                                        while (iterator3.hasNext()) {
                                            Container containerInfo = (Container) iterator3.next();
                                            RedisAPI.set("container_" + containerInfo.getContainerName(), JSONObject.toJSONString(containerInfo));
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

                //<editor-fold desc="部品类别">
                case "部品类别": {
                    switch (operation) {
                        case "create": {
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartCategory", "PartCategoryName", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PartCategory", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品分类信息到Redis中*/
                                        List<Object> partCategory = commonController.dataBaseQuery("table", "com.cn.bean.", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategoryName", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PartCategory", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PartCategory", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PartCategory", "PartCategoryName", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("wareHouseManagerName") == 0) {
                                String[] keys = {"wareHouseManagerName"};
                                String[] keysName = {"库管员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PartCategory", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入部品分类信息到Redis中*/
                                        List<Object> partCategory = commonController.dataBaseQuery("table", "com.cn.bean.", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategoryName", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "AutoStyling", "AutoStylingName", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "AutoStyling", "AutoStylingName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "AutoStyling", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "AutoStyling", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "AutoStyling", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "AutoStyling", "AutoStylingName", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "AutoStyling", update, add, delete, "data");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "CustomerType", "CustomerTypeName", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "CustomerType", "CustomerTypeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("customerRoleCode") == 0) {
                                String[] keys = {"customerRoleCode", "customerRoleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PlatformRole", "RoleCode", datas, rely, true, DatabaseOpt.BASE, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "CustomerType", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "CustomerType", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "CustomerType", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "CustomerType", "CustomerTypeName", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "CustomerType", update, add, delete, "data");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "SHMethod", "SHMethodName", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "SHMethod", "SHMethodName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "SHMethod", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "SHMethod", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "SHMethod", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "SHMethod", "SHMethodName", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "SHMethod", update, add, delete, "data");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "KCQuota", "SupplierID", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "KCQuota", "SupplierID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "KCQuota", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "KCQuota", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "KCQuota", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "KCQuota", "SupplierID", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "KCQuota", update, add, delete, "data");
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "ExemptionInfo", "SupplierID", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "ExemptionInfo", "SupplierID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "ExemptionInfo", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "ExemptionInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "ExemptionInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "ExemptionInfo", "SupplierID", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "ExemptionInfo", update, add, delete, "data");
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "AreaLedIPInfo", "AddressCode", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "AreaLedIPInfo", "AddressCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "AreaLedIPInfo", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入存放地址信息到Redis中*/
                                        List<Object> ledIpInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", Integer.MAX_VALUE, 1, "addressCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "AreaLedIPInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "AreaLedIPInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "AreaLedIPInfo", "AddressCode", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "AreaLedIPInfo", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入存放地址信息到Redis中*/
                                        List<Object> ledIpInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", Integer.MAX_VALUE, 1, "addressCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBHStaff", "PartCode", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PartBHStaff", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PartBHStaff", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PartBHStaff", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PartBHStaff", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PartBHStaff", "PartCode", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PartBHStaff", update, add, delete, "data");
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
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
                            }
                            if (target.compareToIgnoreCase("bhEmployeeName") == 0) {
                                String[] keys = {"bhEmployeeName"};
                                String[] keysName = {"备货员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                rely = rely.replace("bhEmployeeType", "employeeType");
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("lhEmployeeName") == 0) {
                                String[] keys = {"lhEmployeeName"};
                                String[] keysName = {"领货员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                rely = rely.replace("lhEmployeeType", "employeeType");
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("psEmployeeName") == 0) {
                                String[] keys = {"psEmployeeName"};
                                String[] keysName = {"配送员"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"employeeName"};
                                rely = rely.replace("psEmployeeType", "employeeType");
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "Employee", "EmployeeName", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBomInfo", "ZCPartCode", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "PartBomInfo", "ZCPartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "PartBomInfo", importPath + fileName, DatabaseOpt.DATA);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入总成BOM信息到Redis中*/
                                        List<Object> partBomInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBomInfo", "*", "", Integer.MAX_VALUE, 1, "ZCPartCode", 0, DatabaseOpt.DATA);
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
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "PartBomInfo", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "PartBomInfo", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "PartBomInfo", "ZCPartCode", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "PartBomInfo", update, add, delete, "data");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        /*导入总成BOM信息到Redis中*/
                                        List<Object> partBomInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBomInfo", "*", "", Integer.MAX_VALUE, 1, "ZCPartCode", 0, DatabaseOpt.DATA);
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
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("detailPartCode") == 0) {
                                String[] keys = {"detailPartCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = interfaceController.queryOperate(target, "com.cn.bean.", "table", "PartBaseInfo", "PartCode", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = interfaceController.createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "Version", "VarsionID", DatabaseOpt.DATA);
                            break;
                        }
                        case "request_page": {
                            json = interfaceController.queryOperate("com.cn.bean.", "table", "Version", "VarsionID", datas, rely, true, DatabaseOpt.DATA, pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = interfaceController.importData("com.cn.bean.", "Version", importPath + fileName, DatabaseOpt.DATA);
                            break;
                        }
                        case "exportTemplate": {
                            json = interfaceController.exportTemplate(filePath, servletPath, "com.cn.bean.", "Version", null);
                            break;
                        }
                        case "export": {
                            json = interfaceController.exportData(filePath, servletPath, "com.cn.bean.", "Version", (ArrayList<Object>) interfaceController.queryData("com.cn.bean.", "table", "Version", "VarsionID", datas, DatabaseOpt.DATA, Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = interfaceController.submitOperate("com.cn.bean.", "Version", update, add, delete, "data");
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
