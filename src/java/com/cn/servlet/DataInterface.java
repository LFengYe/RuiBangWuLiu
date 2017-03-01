/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.ClassDescription;
import com.cn.bean.FieldDescription;
import com.cn.controller.CommonController;
import com.cn.controller.PlatformUserInfoController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
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

    private final CommonController commonController = new CommonController();
    private final DatabaseOpt opt = new DatabaseOpt();

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
            String fileName = paramsJson.getString("fileName");
            int pageIndex = paramsJson.getIntValue("pageIndex");
            int pageSize = paramsJson.getIntValue("pageSize");

            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            String importPath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/";

            switch (module) {
                //<editor-fold desc="用户登陆模板">
                case "userLogin": {
                    switch (operation) {
                        case "login": {
                            PlatformUserInfoController controller = new PlatformUserInfoController();
                            int result = controller.userLogin(paramsJson.getString("username"), paramsJson.getString("password"));
                            if (result == 0) {
                                session.setAttribute("user", paramsJson.getString("username"));
                                String menuJson = "{";

                                SAXReader reader = new SAXReader();
                                Document document = reader.read(new File(path + "menu.xml"));
                                Element root = document.getRootElement();
                                Iterator<Element> iterator = root.elementIterator();
                                while (iterator.hasNext()) {
                                    menuJson += commonController.hasRight(iterator.next());
                                }
                                menuJson = menuJson.substring(0, menuJson.length() - 1) + "}";

                                json = Units.objectToJson(result, "登陆成功!", menuJson);
                            } else if (result == 1) {
                                json = Units.objectToJson(result, "用户名不存在!", null);
                            } else if (result == 2) {
                                json = Units.objectToJson(result, "用户名或密码错误!", null);
                            } else if (result == -1) {
                                json = Units.objectToJson(result, "登陆出错!", null);
                            } else {
                                json = Units.objectToJson(result, "服务器出错!", null);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="注册公司">
                case "注册公司": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PlatformCompanyInfo", "CompanyID", opt.getConnectBase());
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PlatformCompanyInfo", update, add, delete, "base");
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","table", "PlatformCompanyInfo", "CompanyID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PlatformCompanyInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PlatformCompanyInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PlatformCompanyInfo", (ArrayList<Object>) queryData("com.cn.bean.","table", "PlatformCompanyInfo", "CompanyID", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
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
                            json = createOperate(15, "view", "com/cn/json/", "com.cn.bean.", "PlatFormDataBaseInfo", "DataBaseID", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","view", "PlatFormDataBaseInfo", "DataBaseID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PlatFormDataBaseInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PlatFormDataBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PlatFormDataBaseInfo", (ArrayList<Object>) queryData("com.cn.bean.","view", "PlatFormDataBaseInfo", "DataBaseID", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("CompanyID") == 0) {
                                String[] keys = {"companyID", "companyName"};
                                String[] keysName = {"公司编号", "公司名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"companyID", "companyName"};
                                System.out.println("datas:" + datas + ",pageSize:" + pageSize + ",pageIndex:" + pageIndex);
                                json = queryOperate(target, "com.cn.bean.","table", "PlatformCompanyInfo", "CompanyID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PlatFormDataBaseInfo", update, add, delete, "base");
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
                            json = queryOperate("com.cn.bean.","table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PlatformUserInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PlatformUserInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PlatformUserInfo", (ArrayList<Object>) queryData("com.cn.bean.","view", "PlatformUserInfo", "UserLoginAccount", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("UserLoginDBName") == 0) {
                                String[] keys = {"userLoginDBName", "companyName"};
                                String[] keysName = {"数据库名", "公司名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"masterDataBaseName", "companyName"};
                                json = queryOperate(target, "com.cn.bean.","view", "PlatFormDataBaseInfo", "CompanyID", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PlatformUserInfo", update, add, delete, "base");
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
                            json = queryOperate("com.cn.bean.","table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PlatformRole", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PlatformRole", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PlatformRole", (ArrayList<Object>) queryData("com.cn.bean.","view", "PlatformRole", "RoleCode", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PlatformRole", update, add, delete, "base");
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
                            json = queryOperate("com.cn.bean.","table", "PlatformRoleRight", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PlatformRoleRight", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PlatformRoleRight", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PlatformRoleRight", (ArrayList<Object>) queryData("com.cn.bean.","table", "PlatformRoleRight", "RoleCode", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PlatformRoleRight", update, add, delete, "base");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                String[] keys = {"roleCode", "roleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = queryOperate(target, "com.cn.bean.","table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("RightCode") == 0) {
                                String[] keys = {"rightCode", "rightName"};
                                String[] keysName = {"模块代码", "模块名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"rightCode", "rightName"};
                                json = queryOperate(target, "com.cn.bean.","table", "PlatformRight", "RightCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
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
                            json = queryOperate("com.cn.bean.","table", "PlatformUserRole", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PlatformUserRole", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PlatformUserRole", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PlatformUserRole", (ArrayList<Object>) queryData("com.cn.bean.","table", "PlatformUserRole", "UserLoginAccount", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PlatformUserRole", update, add, delete, "base");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                String[] keys = {"roleCode", "roleName"};
                                String[] keysName = {"角色代码", "角色名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"roleCode", "roleName"};
                                json = queryOperate(target, "com.cn.bean.","table", "PlatformRole", "RoleCode", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("UserLoginAccount") == 0) {
                                String[] keys = {"userLoginAccount"};
                                String[] keysName = {"用户名"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"userLoginAccount"};
                                json = queryOperate(target, "com.cn.bean.","table", "PlatformUserInfo", "UserLoginAccount", datas, rely, true, opt.getConnectBase(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

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
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.plan.","table", "DHPlanList", "DHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.plan.","table", "DHPlan", "DHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.plan.", "DHPlan", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.plan.", "DHPlanList", "add", opt.getConnect());
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
                                json = queryOperate(target, "com.cn.bean.plan.","table", "DHPlan", "DHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址"};
                                int[] keysWidth = {20, 20, 20, 20, 20};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.plan.","table", "SHPlanList", "SHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.plan.","table", "SHPlan", "SHPlanID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.plan.", "SHPlan", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.plan.", "SHPlanList", "add", opt.getConnect());
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
                
                //<editor-fold desc="待检入库">
                case "待检入库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", "DJInWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"部品状态\\", ",@待检");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "DJInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"部品状态\\", ",@待检");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress", "autoStyling", "inboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址", "车型", "入库包装数量"};
                                int[] keysWidth = {20, 20, 20, 10, 10, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1", "autoStyling", "inboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("autoStyling") == 0) {
                                String[] keys = {"autoStyling"};
                                String[] keysName = {"使用车型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"autoStyling"};
                                json = queryOperate(target, "com.cn.bean.","table", "AutoStyling", "AutoStyling", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.","table", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.","table", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "table", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect());
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
                
                //<editor-fold desc="待检审核">
                case "待检审核": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/in/", "com.cn.bean.in.", "DJInWareHouse", "DJInWareHouseID", opt.getConnect());
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"部品状态\\", ",@待检");
                            break;
                        }
                        case "add": {
                            json = Units.objectToJson(0, "", Units.returnFileContext(path + "com/cn/json/in/", "DJInWareHouse.json"));
                            json = Units.insertStr(json, "\\\"待检入库单号\\", ",@DJRK-" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"制单人员姓名\\", ",@" + session.getAttribute("user"));
                            json = Units.insertStr(json, "\\\"制单时间\\", ",@" + Units.getNowTime());
                            json = Units.insertStr(json, "\\\"入库批次\\", ",@" + Units.getNowTimeNoSeparator());
                            json = Units.insertStr(json, "\\\"部品状态\\", ",@待检");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress", "autoStyling", "inboundPackageAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址", "车型", "入库包装数量"};
                                int[] keysWidth = {20, 20, 20, 10, 10, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1", "autoStyling", "inboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("autoStyling") == 0) {
                                String[] keys = {"autoStyling"};
                                String[] keysName = {"使用车型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"autoStyling"};
                                json = queryOperate(target, "com.cn.bean.","table", "AutoStyling", "AutoStyling", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.in.","table", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.","table", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.", "table", "DJInWareHouse", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "DJInWareHouse", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "DJInWareHouseList", "add", opt.getConnect());
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

                //<editor-fold desc="送检出库">
                case "送检出库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.in.","table", "SJOutWareHouseList", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.","table", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.","table", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "inboundAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "入库数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "inboundAmount"};
                                json = queryOperate(target, "com.cn.bean.in.","table", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect());
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
                
                //<editor-fold desc="送检返回">
                case "送检返回": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/in/", "com.cn.bean.in.", "SJOutWareHouse", "SJOutWareHouseID", opt.getConnect());
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
                            json = queryOperate("com.cn.bean.in.","table", "SJOutWareHouseList", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.in.","table", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_on_date": {
                            json = queryOnDateOperate("com.cn.bean.in.","table", "SJOutWareHouse", "SJOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "inboundAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "入库批次", "入库数量"};
                                int[] keysWidth = {20, 20, 20, 10, 20, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "inboundBatch", "inboundAmount"};
                                json = queryOperate(target, "com.cn.bean.in.","table", "DJInWareHouseList", "DJInWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.in.", "SJOutWareHouse", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.in.", "SJOutWareHouseList", "add", opt.getConnect());
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
                
                //<editor-fold desc="计划出库">
                case "计划出库": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(20, "table", "com/cn/json/out/", "com.cn.bean.out.", "JHOutWareHouse", "JHOutWareHouseID", opt.getConnect());
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
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName", "partUnit", "cfAddress", "outboundContainer", "containerAmount"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称", "部品单位", "存放地址", "出库盛具", "盛具数量"};
                                int[] keysWidth = {20, 20, 20, 10, 10, 10, 10};
                                String[] fieldsName = {"partCode", "partID", "partName", "partUnit", "psAddress1", "outboundContainer", "outboundPackageAmount"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("autoStyling") == 0) {
                                String[] keys = {"autoStyling"};
                                String[] keysName = {"使用车型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"autoStyling"};
                                json = queryOperate(target, "com.cn.bean.","table", "AutoStyling", "AutoStyling", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
//                            System.out.println("json:" + json);
                            break;
                        }
                        case "request_detail": {
                            json = queryOperate("com.cn.bean.out.","table", "JHOutWareHouseList", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.out.","table", "JHOutWareHouse", "JHOutWareHouseID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "submit": {
                            int result = commonController.dataBaseOperate("[" + item + "]", "com.cn.bean.out.", "JHOutWareHouse", "add", opt.getConnect());
                            if (result == 0) {
                                result = commonController.dataBaseOperate(details, "com.cn.bean.out.", "JHOutWareHouseList", "add", opt.getConnect());
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
                
                //<editor-fold desc="部品档案">
                case "部品档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartBaseInfo", "PartCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PartBaseInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PartBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PartBaseInfo", (ArrayList<Object>) queryData("com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("autoStyling") == 0) {
                                String[] keys = {"autoStyling"};
                                String[] keysName = {"使用车型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"autoStyling"};
                                json = queryOperate(target, "com.cn.bean.","table", "AutoStyling", "AutoStyling", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCategory") == 0) {
                                String[] keys = {"partCategory"};
                                String[] keysName = {"部品类别"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"partCategory"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartCategory", "PartCategory", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PartBaseInfo", update, add, delete, "data");
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
                            json = queryOperate("com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","Customer", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","Customer", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","Customer", (ArrayList<Object>) queryData("com.cn.bean.","table", "Customer", "CustomerID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("customerType") == 0) {
                                String[] keys = {"customerType"};
                                String[] keysName = {"客户类型"};
                                int[] keysWidth = {100};
                                String[] fieldsName = {"customerType"};
                                json = queryOperate(target, "com.cn.bean.","table", "CustomerType", "CustomerType", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","Customer", update, add, delete, "data");
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
                            json = queryOperate("com.cn.bean.","table", "PartStore", "SupplierID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PartStore", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PartStore", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PartStore", (ArrayList<Object>) queryData("com.cn.bean.","table", "PartStore", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PartStore", update, add, delete, "data");
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                                //System.out.println("json:" + json);
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
                            json = queryOperate("com.cn.bean.","table", "Container", "ContainerName", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","Container", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","Container", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","Container", (ArrayList<Object>) queryData("com.cn.bean.","table", "Container", "ContainerName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","Container", update, add, delete, "data");
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
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "PartCategory", "PartCategory", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","table", "PartCategory", "PartCategory", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","PartCategory", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","PartCategory", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","PartCategory", (ArrayList<Object>) queryData("com.cn.bean.","table", "PartCategory", "PartCategory", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","PartCategory", update, add, delete, "data");
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
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "AutoStyling", "AutoStyling", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","table", "AutoStyling", "AutoStyling", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","AutoStyling", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","AutoStyling", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","AutoStyling", (ArrayList<Object>) queryData("com.cn.bean.","table", "AutoStyling", "AutoStyling", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","AutoStyling", update, add, delete, "data");
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
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "CustomerType", "CustomerType", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","table", "CustomerType", "CustomerType", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","CustomerType", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","CustomerType", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","CustomerType", (ArrayList<Object>) queryData("com.cn.bean.","table", "CustomerType", "CustomerType", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","CustomerType", update, add, delete, "data");
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
                            json = createOperate(15, "table", "com/cn/json/", "com.cn.bean.", "SHMethod", "SHMethod", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("com.cn.bean.","table", "SHMethod", "SHMethod", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","SHMethod", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","SHMethod", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","SHMethod", (ArrayList<Object>) queryData("com.cn.bean.","table", "SHMethod", "SHMethod", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("com.cn.bean.","SHMethod", update, add, delete, "data");
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
                            json = queryOperate("com.cn.bean.","table", "KCQuota", "SupplierID", datas, rely, true, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("com.cn.bean.","KCQuota", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("com.cn.bean.","KCQuota", null);
                            break;
                        }
                        case "export": {
                            json = exportData("com.cn.bean.","KCQuota", (ArrayList<Object>) queryData("com.cn.bean.","table", "KCQuota", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                String[] keys = {"supplierID", "supplierName"};
                                String[] keysName = {"供应商代码", "供应商名称"};
                                int[] keysWidth = {50, 50};
                                String[] fieldsName = {"customerID", "customerName"};
                                json = queryOperate(target, "com.cn.bean.","table", "Customer", "CustomerID", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                String[] keys = {"partCode", "partID", "partName"};
                                String[] keysName = {"部品件号", "部品代码", "部品名称"};
                                int[] keysWidth = {40, 30, 30};
                                String[] fieldsName = {"partCode", "partID", "partName"};
                                json = queryOperate(target, "com.cn.bean.","table", "PartBaseInfo", "PartCode", datas, rely, true, opt.getConnect(), pageSize, pageIndex, keys, keysName, keysWidth, fieldsName);
                            }
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>
            }

        } catch (Exception e) {
            logger.info(subUri);
            logger.error("参数错误", e);
            json = Units.objectToJson(-1, "输入参数错误!", e.toString());
        }

        PrintWriter out = response.getWriter();

        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            out.print(json);
//            System.out.println(json);
        } finally {
            if (out != null) {
                out.close();
            }
        }
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
    private String createOperate(int pageSize, String type, String jsonPackagePath, String beanPackage, String tableName, String orderField, Connection conn) throws Exception {
        String json;
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = Units.returnFileContext(path + jsonPackagePath, tableName + ".json");
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);
        if (result != null) {
            List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", "", pageSize, 1, orderField, 0, conn);
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
    private String queryOperate(String beanPackage, String type, String tableName, String orderField, String keyWord, String rely, boolean isAll,
            Connection conn, int pageSize, int pageIndex) throws Exception {
        String json;
        String result = "{}";
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);

        List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", commonController.getWhereSQLStr(objClass, keyWord, rely, isAll), pageSize, pageIndex, orderField, 0, conn);
        if (list != null && list.size() > 0) {
            StringBuffer buffer = new StringBuffer(result);
            buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
            buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
            buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
            result = buffer.toString();
            
            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "数据为空!", null);
        }

        return json;
    }

    /**
     * 包含日期的查询操作
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
    private String queryOnDateOperate(String beanPackage, String type, String tableName, String orderField, String keyWord, String rely, boolean isAll,
            Connection conn, int pageSize, int pageIndex) throws Exception {
        String json;
        String result = "{}";
        Class objClass = Class.forName(beanPackage + tableName);
        Method method = objClass.getMethod("getRecordCount", null);

        List<Object> list = commonController.dataBaseQuery(type, beanPackage, tableName, "*", commonController.getWhereSQLStrWithDate(objClass, keyWord, rely, isAll), pageSize, pageIndex, orderField, 0, conn);
        if (list != null && list.size() > 0) {
            StringBuffer buffer = new StringBuffer(result);
            buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
            buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
            buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
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
            int updateResult = commonController.dataBaseOperate(update, beanPackage, tableName, "update", conn);
            if (updateResult == -1)
                json = Units.objectToJson(-1, "修改操作失败!", null);
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(add)) {
            int addResult = commonController.dataBaseOperate(add, beanPackage, tableName, "add", conn);
            if (addResult == -1)
                json = Units.objectToJson(-1, "添加操作失败!", null);
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(delete)) {
            int delResult = commonController.dataBaseOperate(delete, beanPackage, tableName, "delete", conn);
            if (delResult == -1)
                json = Units.objectToJson(-1, "删除操作失败!", null);
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
            if (field.isAnnotationPresent(FieldDescription.class
            )) {
                FieldDescription description = field.getAnnotation(FieldDescription.class);

                fieldDes.add(description.description());

                accessFields.add(field);
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

            Object object = objClass.newInstance();
            for (int j = 0; j < accessFields.size(); j++) {
                Field field = accessFields.get(j);
                field.setAccessible(true);
                Cell cell = row.getCell(templateDataIndex[j]);
//                String fieldType = field.getGenericType().toString();

                if (field.getType() == int.class
                        || field.getType() == float.class
                        || field.getType() == double.class) {
                    if (cell
                            == null) {
                        field.set(object, 0);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_NUMERIC);
                        field.set(object, row.getCell(templateDataIndex[j]).getNumericCellValue());
                    }
                } else if (field.getType()
                        == boolean.class) {
                    if (cell
                            == null) {
                        field.set(object, false);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_BOOLEAN);
                        field.set(object, row.getCell(templateDataIndex[j]).getBooleanCellValue());
                    }
                } else {
                    if (cell == null) {
                        field.set(object, "");
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_STRING);
                        field.set(object, row.getCell(templateDataIndex[j]).getStringCellValue());
                    }

                }
            }

            result.add(object);
        }

        int addResult = commonController.dataBaseOperate(JSONObject.toJSONString(result), beanPackage, tableName, "add", conn);
        if (addResult == 0) {
            json = Units.objectToJson(addResult, "数据导入成功!", null);
        } else if (addResult == -1) {
            json = Units.objectToJson(addResult, "数据导入失败!", null);
        } else {
            json = Units.objectToJson(addResult, "导入数据格式不正确!", null);
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
