/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.DJWareHouse;
import com.cn.bean.PlatformCompanyInfo;
import com.cn.bean.PlatFormDataBaseInfo;
import com.cn.bean.PlatformRight;
import com.cn.bean.PlatformRole;
import com.cn.bean.PlatformUserInfo;
import com.cn.controller.CommonController;
import com.cn.controller.DJInWareHouseController;
import com.cn.controller.PlatformCompanyInfoController;
import com.cn.controller.PlatFormDataBaseInfoController;
import com.cn.controller.PlatformRightController;
import com.cn.controller.PlatformRoleController;
import com.cn.controller.PlatformUserInfoController;
import com.cn.controller.UserController;
import com.cn.util.Units;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
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

    ArrayList<String> roleCodeList = new ArrayList<>();

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
        
        try {
            System.out.println(subUri + ",params:" + params);
            JSONObject paramsJson = JSONObject.parseObject(params);
            String module = paramsJson.getString("module");
            String operation = paramsJson.getString("operation");
            HttpSession session = request.getSession();
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");

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
                            String result = Units.returnFileContext(path, "PlatformCompanyInfo.json");
                            if (result != null) {
                                PlatformCompanyInfoController companyInfoController = new PlatformCompanyInfoController();
                                ArrayList<PlatformCompanyInfo> infos = companyInfoController.getPlatformCompanyInfoData();
                                if (null != infos && infos.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(infos, Units.features));
                                    result = buffer.toString();
                                }
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "add": {

                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="注册数据库">
                case "注册数据库": {
                    switch (operation) {
                        case "create": {
                            String result = Units.returnFileContext(path, "PlatFormDataBaseInfo.json");
                            if (result != null) {
                                PlatFormDataBaseInfoController controller = new PlatFormDataBaseInfoController();
                                ArrayList<PlatFormDataBaseInfo> infos = controller.getPlatformUserInfoData();
                                if (infos != null && infos.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(infos, Units.features));
                                    result = buffer.toString();
                                }
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_table": {
                            String target = paramsJson.getString("target");
                            if (target.compareToIgnoreCase("CompanyID") == 0) {
                                PlatformCompanyInfoController controller = new PlatformCompanyInfoController();
                                ArrayList<PlatformCompanyInfo> result = controller.getPlatformCompanyInfoData();
                                if (null != result && result.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"CompanyID\":\"公司编号,50%\",\"CompanyName\": \"公司名称,50%\"},\"datas\":[");
                                    for (PlatformCompanyInfo companyInfo : result) {
                                        buffer.append("{");
                                        buffer.append("\"CompanyID\":").append("\"").append(companyInfo.CompanyID).append("\"").append(",");
                                        buffer.append("\"CompanyName\":").append("\"").append(companyInfo.CompanyName).append("\"");
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

                //<editor-fold desc="注册平台用户">
                case "注册平台用户": {
                    switch (operation) {
                        case "create": {
                            String result = Units.returnFileContext(path, "PlatformUserInfo.json");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_table": {
                            String target = paramsJson.getString("target");
                            if (target.compareToIgnoreCase("UserLoginDBName") == 0) {
                                PlatFormDataBaseInfoController controller = new PlatFormDataBaseInfoController();
                                ArrayList<PlatFormDataBaseInfo> result = controller.getPlatformUserInfoData();
                                if (result != null && result.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"UserLoginDBName\":\"数据库名,50%\",\"CompanyName\":\"公司名称,50%\"},\"datas\":[");
                                    for (PlatFormDataBaseInfo userInfo : result) {
                                        buffer.append("{");
                                        buffer.append("\"UserLoginDBName\":").append("\"").append(userInfo.MasterDataBaseName).append("\"").append(",");
                                        buffer.append("\"CompanyName\":").append("\"").append(userInfo.CompanyName).append("\"");
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

                //<editor-fold desc="定义角色">
                case "定义角色": {
                    switch (operation) {
                        case "create": {
                            String result = Units.returnFileContext(path, "PlatformRole.json");
                            if (result != null) {
                                PlatformRoleController controller = new PlatformRoleController();
                                ArrayList<PlatformRole> roles = controller.getPlatformRoleData();
                                if (roles != null && roles.size() > 0) {
                                    StringBuffer buffer = new StringBuffer(result);
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(roles, Units.features));
                                    result = buffer.toString();
                                }
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

                //<editor-fold desc="定义角色权限">
                case "定义角色权限": {
                    switch (operation) {
                        case "create": {
                            String result = Units.returnFileContext(path, "PlatformRoleRight.json");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_table": {
                            String target = paramsJson.getString("target");
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                PlatformRoleController controller = new PlatformRoleController();
                                ArrayList<PlatformRole> result = controller.getPlatformRoleData();
                                if (result != null && result.size() > 0) {
//                                    String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
                                    StringBuffer buffer = new StringBuffer(Units.returnFileContext(path, "PlatformRoleData.json"));
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(result, Units.features));

                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("RightCode") == 0) {
                                PlatformRightController controller = new PlatformRightController();
                                ArrayList<PlatformRight> result = controller.getPlatformRightData();
                                if (result != null && result.size() > 0) {
//                                    String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
                                    StringBuffer buffer = new StringBuffer(Units.returnFileContext(path, "PlatformRightData.json"));
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(result, Units.features));

                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="定义用户角色">
                case "定义用户角色": {
                    switch (operation) {
                        case "create": {
                            String result = Units.returnFileContext(path, "PlatformUserRole.json");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_table": {
                            String target = paramsJson.getString("target");
                            if (target.compareToIgnoreCase("RoleCode") == 0) {
                                PlatformRoleController controller = new PlatformRoleController();
                                ArrayList<PlatformRole> result = controller.getPlatformRoleData();
                                if (result != null && result.size() > 0) {
//                                    String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
                                    StringBuffer buffer = new StringBuffer(Units.returnFileContext(path, "PlatformRoleData.json"));
                                    buffer.insert(buffer.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(result, Units.features));

                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                            if (target.compareToIgnoreCase("UserLoginAccount") == 0) {
                                PlatformUserInfoController controller = new PlatformUserInfoController();
                                ArrayList<PlatformUserInfo> result = controller.getPlatformUserInfoData();
                                if (result != null && result.size() > 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("{\"titles\":{\"UserLoginAccount\":\"用户名,100%\"},\"datas\":[");
                                    for (PlatformUserInfo userInfo : result) {
                                        buffer.append("{");
                                        buffer.append("\"UserLoginAccount\":").append("\"").append(userInfo.UserLoginAccount).append("\"");
                                        buffer.append("},");
                                    }
                                    buffer.deleteCharAt(buffer.length() - 1);
                                    buffer.append("]}");
                                    json = Units.objectToJson(0, "", buffer.toString());
                                } else {
                                    json = Units.objectToJson(-1, "数据为空!", null);
                                }
                            }
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="调货计划下达">
                case "调货计划下达": {
                    switch (operation) {
                        case "create": {
                            DJInWareHouseController controller = new DJInWareHouseController();
                            String result = controller.getInWareHouseData();
                            if (result != null) {
//                                System.out.println("result:" + result);
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
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
                        case "add": {
                            String datas = paramsJson.getString("datas");
                            DJInWareHouseController controller = new DJInWareHouseController();
                            int result = controller.addInWareHouseData(JSONObject.parseObject(datas, DJWareHouse.class));
                            if (result == 0) {
                                json = Units.objectToJson(result, "添加成功!", null);
                            } else if (result == -1) {
                                json = Units.objectToJson(result, "数据库执行出错!", null);
                            } else {
                                json = Units.objectToJson(result, "服务器出错!", null);
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
                            String target = paramsJson.getString("target");
                            UserController controller = new UserController();
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

    private String hasRight(Element element) {
        String menuJson = "";
        String roleCode = element.attributeValue("id");
        if (roleCodeList.contains(roleCode) || true) {
            if (element.elementIterator().hasNext()) {
                menuJson += "\"" + element.attributeValue("text") + "\":{";
                Iterator<Element> iterator = element.elementIterator();
                while (iterator.hasNext()) {
                    menuJson += hasRight(iterator.next());
                }
                menuJson = menuJson.substring(0, menuJson.length() - 1);
                menuJson += "},";
            } else {
                menuJson += "\"" + element.attributeValue("text") + "\":";
                menuJson += "\"" + element.attributeValue("hypelnk") + ",action.do\",";
            }
        }
        return menuJson;
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
