/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSONObject;
import com.cn.controller.UserController;
import com.cn.util.Units;
import java.io.IOException;
import java.io.PrintWriter;
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
public class DataInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DataInterface.class);

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
//            System.out.println("module:" + module + ",operation:" + operation);
            HttpSession session = request.getSession();
            System.out.println("session id:" + session.getId());
            switch (module) {
                //<editor-fold desc="用户登陆模板">
                case "userLogin": {
                    switch (operation) {
                        case "login": {
                            UserController controller = new UserController();
                            String result = controller.userLoginSuccess(paramsJson.getString("username"), paramsJson.getString("password"));
                            if (result != null) {
                                session.setAttribute("user", paramsJson.getString("username"));
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

                //<editor-fold desc="调货计划下达">
                case "调货计划下达": {
                    switch (operation) {
                        case "create": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data1.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_table": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data2.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "query_item": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data3.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "submit": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data4.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "query_on_date": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data5.txt");
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
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data6.txt");
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
                            String result = controller.returnFileContext("data8.txt");
                            if (target.compareTo("depart") == 0) {
                                result = controller.returnFileContext("data2.txt");
                            } else if (target.compareTo("corseName") == 0) {
                                result = controller.returnFileContext("data7.txt");
                            }
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_detail": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data8.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "request_page": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data9.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "query_data": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data10.txt");
                            if (result != null) {
                                json = Units.objectToJson(0, "", result);
                            } else {
                                json = Units.objectToJson(-1, "服务器出错!", null);
                            }
                            break;
                        }
                        case "submit": {
                            UserController controller = new UserController();
                            String result = controller.returnFileContext("data11.txt");
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

                //<editor-fold desc="待检入库">
                case "待检入库": {
                    switch (operation) {
                        case "login": {
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="待检审核">
                case "待检审核": {
                    switch (operation) {
                        case "login": {
                            break;
                        }
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="送检出库">
                case "送检出库": {
                    switch (operation) {
                        case "login": {
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
