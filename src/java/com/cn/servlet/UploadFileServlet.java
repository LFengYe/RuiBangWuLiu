/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.cn.util.Units;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class UploadFileServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(UploadFileServlet.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>不支持GET</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>该方法不支持GET方式, 请改用POST上传</h1>");
            out.println("</body>");
            out.println("</html>");
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
        processRequest(request, response);
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
        //processRequest(request, response);
        String temp = getServletContext().getRealPath("/").replace("\\", "/") + "temp/";   //临时目录
//        System.out.println("temp=" + temp);
        String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
//        System.out.println("loadpath=" + loadpath);
//        String[] fileType = new String[]{".jpg", ".gif", ".bmp", ".png", ".jpeg", ".ico"};
        String[] fileType = new String[]{".xls", ".xlsx"};
        String json = null;

        //Servlet初始化时执行,如果上传文件目录不存在则自动创建  
        if (!new File(temp).isDirectory()) {
            new File(temp).mkdirs();
        }
        if (!new File(loadpath).isDirectory()) {
            new File(loadpath).mkdirs();
        }
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(30 * 1024); //最大缓存  
        factory.setRepository(new File(temp));//临时文件目录
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(15 * 1024 * 1024);//文件上传最大3M
        ArrayList<String> filePaths = new ArrayList<>();
        try {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                //获得文件名，这个文件名包括路径  
                if (!item.isFormField()) {
                    //文件名
                    String fileName = item.getName().toLowerCase();
                    if (fileName.endsWith(fileType[0]) || fileName.endsWith(fileType[1])) {
                        String uuid = UUID.randomUUID().toString();
                        //服务器保存的新的文件名
                        fileName = uuid + fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
//                        String filePath = loadpath + uuid + fileName.substring(fileName.lastIndexOf("."));
                        item.write(new File(loadpath + fileName));
                        filePaths.add("{\"fileName\":\"" + fileName + "\"}");
                        //json = Units.jsonStrToJson(0, "上传成功", "{\"loadpath\":\"" + filePath + "\"}");
                    } else {
                        json = Units.objectToJson(-1, "上传失败,请确认上传的文件类型为Excel!", null);
                        filePaths.clear();
                        break;
                    }
                }
            }
            
            if (filePaths.size() > 0) {
                String tmpJson = "[";
                for (String tmp : filePaths) {
                    tmpJson += tmp + ",";
                }
                tmpJson = tmpJson.substring(0, tmpJson.length() - 1);
                tmpJson += "]";
                json = Units.jsonStrToJson(0, "上传成功", tmpJson);
            }
        } catch (FileUploadException ex) {
            logger.error("上传异常", ex);
            request.setAttribute("msg", "上传失败");
            json = Units.objectToJson(-1, "上传失败", null);
        } catch (Exception ex) {
            logger.error("出现异常", ex);
            request.setAttribute("msg", "上传失败");
            json = Units.objectToJson(-1, "上传失败", null);
        }

        //request.setAttribute("msg", json);
        //System.out.println(json);
        //request.getRequestDispatcher("index.jsp").forward(request, response);

        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            out.print(json);
            //response.sendRedirect("index.jsp");
        } finally {
            out.close();
        }
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
