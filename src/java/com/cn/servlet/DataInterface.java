/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.AutoStyling;
import com.cn.bean.Customer;
import com.cn.bean.CustomerType;
import com.cn.bean.FieldDescription;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartCategory;
import com.cn.bean.PlatformCompanyInfo;
import com.cn.bean.PlatFormDataBaseInfo;
import com.cn.bean.PlatformRight;
import com.cn.bean.PlatformRole;
import com.cn.bean.PlatformUserInfo;
import com.cn.controller.CommonController;
import com.cn.controller.PlatformUserInfoController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
import com.cn.util.Units;
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
            String rely = paramsJson.getString("rely");
            String target = paramsJson.getString("target");
            String datas = paramsJson.getString("datas");
            String update = paramsJson.getString("update");
            String add = paramsJson.getString("add");
            String delete = paramsJson.getString("del");
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
                            json = createOperate("table", "PlatformCompanyInfo", "CompanyID", opt.getConnectBase());
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PlatformCompanyInfo", update, add, delete, opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PlatformCompanyInfo", "CompanyID", datas, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PlatformCompanyInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PlatformCompanyInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PlatformCompanyInfo", (ArrayList<Object>) queryData("table", "PlatformCompanyInfo", "CompanyID", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
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
                            json = createOperate("view", "PlatFormDataBaseInfo", "DataBaseID", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("view", "PlatFormDataBaseInfo", "DataBaseID", datas, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PlatFormDataBaseInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PlatFormDataBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PlatFormDataBaseInfo", (ArrayList<Object>) queryData("view", "PlatFormDataBaseInfo", "DataBaseID", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("CompanyID") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PlatformCompanyInfo", "*", "", Integer.MAX_VALUE, 1, "CompanyID", 0, opt.getConnectBase());
                                if (null != list && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"companyID\":\"公司编号,50%\",\"companyName\": \"公司名称,50%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PlatformCompanyInfo companyInfo = (PlatformCompanyInfo) it.next();
                                        buffer.append("{");
                                        buffer.append("\"companyID\":").append("\"").append(companyInfo.getCompanyID()).append("\"").append(",");
                                        buffer.append("\"companyName\":").append("\"").append(companyInfo.getCompanyName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PlatFormDataBaseInfo", update, add, delete, opt.getConnectBase());
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
                            json = createOperate("table", "PlatformUserInfo", "UserLoginAccount", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PlatformUserInfo", "UserLoginAccount", datas, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PlatformUserInfo", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PlatformUserInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PlatformUserInfo", (ArrayList<Object>) queryData("view", "PlatformUserInfo", "UserLoginAccount", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("UserLoginDBName") == 0) {
                                List<Object> list = commonController.dataBaseQuery("view", "PlatFormDataBaseInfo", "*", "", Integer.MAX_VALUE, 1, "CompanyID", 0, opt.getConnectBase());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"userLoginDBName\":\"数据库名,50%\",\"companyName\":\"公司名称,50%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PlatFormDataBaseInfo userInfo = (PlatFormDataBaseInfo) it.next();
                                        buffer.append("{");
                                        buffer.append("\"userLoginDBName\":").append("\"").append(userInfo.getMasterDataBaseName()).append("\"").append(",");
                                        buffer.append("\"companyName\":").append("\"").append(userInfo.getCompanyName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PlatformUserInfo", update, add, delete, opt.getConnectBase());
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
                            json = createOperate("table", "PlatformRole", "RoleCode", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PlatformRole", "RoleCode", datas, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PlatformRole", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PlatformRole", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PlatformRole", (ArrayList<Object>) queryData("view", "PlatformRole", "RoleCode", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PlatformRole", update, add, delete, opt.getConnectBase());
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
                            json = createOperate("table", "PlatformRoleRight", "RoleCode", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PlatformRoleRight", "RoleCode", datas, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PlatformRoleRight", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PlatformRoleRight", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PlatformRoleRight", (ArrayList<Object>) queryData("table", "PlatformRoleRight", "RoleCode", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PlatformRoleRight", update, add, delete, opt.getConnectBase());
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PlatformRole", "*", "", Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"roleCode\":\"角色代码,50%\",\"roleName\":\"角色名称,50%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PlatformRole userInfo = (PlatformRole) it.next();
                                        buffer.append("{");
                                        buffer.append("\"roleCode\":").append("\"").append(userInfo.getRoleCode()).append("\"").append(",");
                                        buffer.append("\"roleName\":").append("\"").append(userInfo.getRoleName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("RightCode") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PlatformRight", "*", "", Integer.MAX_VALUE, 1, "RightCode", 0, opt.getConnectBase());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"rightCode\":\"模块代码,20%\",\"rightName\":\"模块名称,40%\",\"righthyperlnk\":\"模块链接,40%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PlatformRight right = (PlatformRight) it.next();
                                        buffer.append("{");
                                        buffer.append("\"rightCode\":").append("\"").append(right.getRightCode()).append("\"").append(",");
                                        buffer.append("\"rightName\":").append("\"").append(right.getRightName()).append("\"").append(",");
                                        buffer.append("\"righthyperlnk\":").append("\"").append(right.getRighthyperlnk()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
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

                //<editor-fold desc="定义用户角色">
                case "定义用户角色": {
                    switch (operation) {
                        case "create": {
                            json = createOperate("table", "PlatformUserRole", "UserLoginAccount", opt.getConnectBase());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PlatformUserRole", "UserLoginAccount", datas, opt.getConnectBase(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PlatformUserRole", importPath + fileName, opt.getConnectBase());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PlatformUserRole", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PlatformUserRole", (ArrayList<Object>) queryData("table", "PlatformUserRole", "UserLoginAccount", datas, opt.getConnectBase(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PlatformUserRole", update, add, delete, opt.getConnectBase());
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PlatformRole", "*", "", Integer.MAX_VALUE, 1, "RoleCode", 0, opt.getConnectBase());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"roleCode\":\"角色代码,50%\",\"roleName\":\"角色名称,50%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PlatformRole userInfo = (PlatformRole) it.next();
                                        buffer.append("{");
                                        buffer.append("\"roleCode\":").append("\"").append(userInfo.getRoleCode()).append("\"").append(",");
                                        buffer.append("\"roleName\":").append("\"").append(userInfo.getRoleName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("UserLoginAccount") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PlatformUserInfo", "*", "", Integer.MAX_VALUE, 1, "UserLoginAccount", 0, opt.getConnectBase());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"userLoginAccount\":\"用户名,100%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PlatformUserInfo userInfo = (PlatformUserInfo) it.next();
                                        buffer.append("{");
                                        buffer.append("\"userLoginAccount\":").append("\"").append(userInfo.getUserLoginAccount()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
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

                //<editor-fold desc="调货计划下达">
                case "调货计划下达": {
                    switch (operation) {
                        case "create": {

                            break;
                        }
                        case "request_table": {
                            String result = Units.returnFileContext(path, "data2.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "query_item": {
                            String result = Units.returnFileContext(path, "data3.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "query_on_date": {
                            String result = Units.returnFileContext(path, "data5.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String result = Units.returnFileContext(path, "data4.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
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
                            String result = Units.returnFileContext(path, "data6.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_table": {
                            String result = Units.returnFileContext(path, "data8.txt");
                            if (target.compareTo("depart") == 0) {
                                result = Units.returnFileContext(path, "data2.txt");
                            } else if (target.compareTo("courseName") == 0) {
                                result = Units.returnFileContext(path, "data7.txt");
                            }
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_detail": {
                            String result = Units.returnFileContext(path, "data8.txt");
                            if (result != null) {
                                System.out.println("result:" + result);
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            String result = Units.returnFileContext(path, "data9.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "query_data": {
                            String result = Units.returnFileContext(path, "data10.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "submit": {
                            String result = Units.returnFileContext(path, "data11.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
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
                            json = createOperate("table", "PartBaseInfo", "PartCode", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PartBaseInfo", "PartCode", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PartBaseInfo", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PartBaseInfo", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PartBaseInfo", (ArrayList<Object>) queryData("table", "PartBaseInfo", "PartCode", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("autoStyling") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "AutoStyling", "*", "", Integer.MAX_VALUE, 1, "AutoStyling", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"autoStyling\":\"使用车型,100%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        AutoStyling userInfo = (AutoStyling) it.next();
                                        buffer.append("{");
                                        buffer.append("\"autoStyling\":").append("\"").append(userInfo.getAutoStyling()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("partCategory") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategory", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"partCategory\":\"部品类别,100%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PartCategory userInfo = (PartCategory) it.next();
                                        buffer.append("{");
                                        buffer.append("\"partCategory\":").append("\"").append(userInfo.getPartCategory()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PartBaseInfo", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "Customer", "CustomerID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "Customer", "CustomerID", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("Customer", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("Customer", null);
                            break;
                        }
                        case "export": {
                            json = exportData("Customer", (ArrayList<Object>) queryData("table", "Customer", "CustomerID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("customerType") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "CustomerType", "*", "", Integer.MAX_VALUE, 1, "CustomerType", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"customerType\":\"客户类型,100%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        CustomerType userInfo = (CustomerType) it.next();
                                        buffer.append("{");
                                        buffer.append("\"customerType\":").append("\"").append(userInfo.getCustomerType()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            break;
                        }
                        case "submit": {
                            json = submitOperate("Customer", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "PartStore", "SupplierID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PartStore", "SupplierID", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PartStore", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PartStore", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PartStore", (ArrayList<Object>) queryData("table", "PartStore", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PartStore", update, add, delete, opt.getConnect());
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"supplierID\":\"供应商代码,50%\",\"supplierName\":\"供应商名称,50%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        Customer userInfo = (Customer) it.next();
                                        buffer.append("{");
                                        buffer.append("\"supplierID\":").append("\"").append(userInfo.getCustomerID()).append("\"").append(",");
                                        buffer.append("\"supplierName\":").append("\"").append(userInfo.getCustomerName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"partCode\":\"部品件号,40%\",\"partID\":\"部品代码,30%\",\"partName\":\"部品名称,30%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PartBaseInfo info = (PartBaseInfo) it.next();
                                        buffer.append("{");
                                        buffer.append("\"partCode\":").append("\"").append(info.getPartCode()).append("\"").append(",");
                                        buffer.append("\"partID\":").append("\"").append(info.getPartID()).append("\"").append(",");
                                        buffer.append("\"partName\":").append("\"").append(info.getPartName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
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

                //<editor-fold desc="盛具档案">
                case "盛具档案": {
                    switch (operation) {
                        case "create": {
                            json = createOperate("table", "Container", "ContainerName", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "Container", "ContainerName", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("Container", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("Container", null);
                            break;
                        }
                        case "export": {
                            json = exportData("Container", (ArrayList<Object>) queryData("table", "Container", "ContainerName", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("Container", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "PartCategory", "PartCategory", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "PartCategory", "PartCategory", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("PartCategory", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("PartCategory", null);
                            break;
                        }
                        case "export": {
                            json = exportData("PartCategory", (ArrayList<Object>) queryData("table", "PartCategory", "PartCategory", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("PartCategory", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "AutoStyling", "AutoStyling", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "AutoStyling", "AutoStyling", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("AutoStyling", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("AutoStyling", null);
                            break;
                        }
                        case "export": {
                            json = exportData("AutoStyling", (ArrayList<Object>) queryData("table", "AutoStyling", "AutoStyling", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("AutoStyling", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "CustomerType", "CustomerType", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "CustomerType", "CustomerType", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("CustomerType", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("CustomerType", null);
                            break;
                        }
                        case "export": {
                            json = exportData("CustomerType", (ArrayList<Object>) queryData("table", "CustomerType", "CustomerType", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("CustomerType", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "SHMethod", "SHMethod", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "SHMethod", "SHMethod", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("SHMethod", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("SHMethod", null);
                            break;
                        }
                        case "export": {
                            json = exportData("SHMethod", (ArrayList<Object>) queryData("table", "SHMethod", "SHMethod", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "submit": {
                            json = submitOperate("SHMethod", update, add, delete, opt.getConnect());
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
                            json = createOperate("table", "KCQuota", "SupplierID", opt.getConnect());
                            break;
                        }
                        case "request_page": {
                            json = queryOperate("table", "KCQuota", "SupplierID", datas, opt.getConnect(), pageSize, pageIndex);
                            break;
                        }
                        case "import": {
                            json = importData("KCQuota", importPath + fileName, opt.getConnect());
                            break;
                        }
                        case "exportTemplate": {
                            json = exportData("KCQuota", null);
                            break;
                        }
                        case "export": {
                            json = exportData("KCQuota", (ArrayList<Object>) queryData("table", "KCQuota", "SupplierID", datas, opt.getConnect(), Integer.MAX_VALUE, 1));
                            break;
                        }
                        case "request_table": {
                            if (target.compareToIgnoreCase("supplierID") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"supplierID\":\"供应商代码,50%\",\"supplierName\":\"供应商名称,50%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        Customer userInfo = (Customer) it.next();
                                        buffer.append("{");
                                        buffer.append("\"supplierID\":").append("\"").append(userInfo.getCustomerID()).append("\"").append(",");
                                        buffer.append("\"supplierName\":").append("\"").append(userInfo.getCustomerName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("partCode") == 0) {
                                List<Object> list = commonController.dataBaseQuery("table", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
                                if (list != null && list.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"partCode\":\"部品件号,40%\",\"partID\":\"部品代码,30%\",\"partName\":\"部品名称,30%\"},\"datas\":[");
                                    for (Iterator<Object> it = list.iterator(); it.hasNext();) {
                                        PartBaseInfo info = (PartBaseInfo) it.next();
                                        buffer.append("{");
                                        buffer.append("\"partCode\":").append("\"").append(info.getPartCode()).append("\"").append(",");
                                        buffer.append("\"partID\":").append("\"").append(info.getPartID()).append("\"").append(",");
                                        buffer.append("\"partName\":").append("\"").append(info.getPartName()).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
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
    private String createOperate(String type, String tableName, String orderField, Connection conn) throws Exception {
        String json;
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = Units.returnFileContext(path, tableName + ".json");
        Class objClass = Class.forName("com.cn.bean." + tableName);
        Method method = objClass.getMethod("getRecordCount", null);
        if (result != null) {
            List<Object> list = commonController.dataBaseQuery(type, tableName, "*", "", 15, 1, orderField, 0, conn);
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
    private String queryOperate(String type, String tableName, String orderField, String keyWord,
            Connection conn, int pageSize, int pageIndex) throws Exception {
        String json;
        String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
        String result = "{}";
        Class objClass = Class.forName("com.cn.bean." + tableName);
        Method method = objClass.getMethod("getRecordCount", null);
        if (result != null) {
            List<Object> list = commonController.dataBaseQuery(type, tableName, "*", commonController.getWhereSQLStr(objClass, keyWord), pageSize, pageIndex, orderField, 0, conn);
            if (list != null && list.size() > 0) {
                StringBuffer buffer = new StringBuffer(result);
                buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(list, Units.features));
                buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + method.invoke(null, null));
                result = buffer.toString();
            }
            System.out.println("result:" + result);
            json = Units.objectToJson(0, "", result);
        } else {
            json = Units.objectToJson(-1, "服务器出错!", null);
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
    private List<Object> queryData(String type, String tableName, String orderField, String keyWord,
            Connection conn, int pageSize, int pageIndex) throws Exception {
        Class objClass = Class.forName("com.cn.bean." + tableName);
        return commonController.dataBaseQuery(type, tableName, "*", commonController.getWhereSQLStr(objClass, keyWord), pageSize, pageIndex, orderField, 0, conn);
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
    private String submitOperate(String tableName, String update, String add, String delete, Connection conn) throws Exception {
        int result = 0;
        String json;
        if (!Units.strIsEmpty(update)) {
            int updateResult = commonController.dataBaseOperate(update, tableName, "update", conn);
            result = (updateResult == -1) ? updateResult : result;
        }
        if (!Units.strIsEmpty(add)) {
            int addResult = commonController.dataBaseOperate(add, tableName, "add", conn);
            result = (addResult == -1) ? addResult : result;
            System.out.println("数据添加结果:" + addResult);
        }
        if (!Units.strIsEmpty(delete)) {
            int delResult = commonController.dataBaseOperate(delete, tableName, "delete", conn);
            result = (delResult == -1) ? delResult : result;
        }

        if (result == 0) {
            json = Units.objectToJson(result, "操作成功!", null);
        } else if (result == -1) {
            json = Units.objectToJson(result, "操作失败!", null);
        } else if (result == 1) {
            json = Units.objectToJson(result, "输入参数错误!", null);
        } else {
            json = Units.objectToJson(result, "服务器出错!", null);
        }
        return json;
    }

    /**
     * 导入Excel数据
     * @param tableName
     * @param fileName
     * @param conn
     * @return
     * @throws Exception 
     */
    private String importData(String tableName, String fileName, Connection conn) throws Exception {
        String json;
        //获取所有设置字段名称的字段
        Class objClass = Class.forName("com.cn.bean." + tableName);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<Field> accessFields = new ArrayList<>();
        ArrayList<String> fieldDes = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class)) {
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
        System.out.println("cells num:" + headerRow.getPhysicalNumberOfCells() + ",des size:" + fieldDes.size());
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
            if (row == null)
                continue;
            
            Object object = objClass.newInstance();
            for (int j = 0; j < accessFields.size(); j++) {
                Field field = accessFields.get(j);
                field.setAccessible(true);
                Cell cell = row.getCell(templateDataIndex[j]);
//                String fieldType = field.getGenericType().toString();
                
                if (field.getType() == int.class || field.getType() == float.class || field.getType() == double.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_NUMERIC);
                        field.set(object, row.getCell(templateDataIndex[j]).getNumericCellValue());
                    }
                } else if (field.getType() == boolean.class) {
                    if (cell == null) {
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
        
        int addResult = commonController.dataBaseOperate(JSONObject.toJSONString(result), tableName, "add", conn);
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
     * @param tableName
     * @return
     * @throws Exception
     */
    private String exportData(String tableName, ArrayList<Object> datas) throws Exception {
        Class objClass = Class.forName("com.cn.bean." + tableName);
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
