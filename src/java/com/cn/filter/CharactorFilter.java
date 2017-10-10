package com.cn.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

public class CharactorFilter implements Filter {

    private static final boolean debug = false;
    // The filter configuration object we are associated with. If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;
    private String encoding = null;

    public CharactorFilter() {
    }

    private void doBeforeProcessing(RequestWrapper request,
            ResponseWrapper response) throws IOException, ServletException {
        if (debug) {
            log("CharactorFilter:DoBeforeProcessing");
        }
    }

    private void doAfterProcessing(RequestWrapper request,
            ResponseWrapper response) throws IOException, ServletException {
        if (debug) {
            log("CharactorFilter:DoAfterProcessing");
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (debug) {
            log("CharactorFilter:doFilter()");
        }

        // Create wrappers for the request and response objects.
        // Using these, you can extend the capabilities of the
        // request and response, for example, allow setting parameters
        // on the request before sending the request to the rest of the filter
        // chain,
        // or keep track of the cookies that are set on the response.
        //
        // Caveat: some servers do not handle wrappers very well for forward or
        // include requests.
        RequestWrapper wrappedRequest = new RequestWrapper(
                (HttpServletRequest) request);
        ResponseWrapper wrappedResponse = new ResponseWrapper(
                (HttpServletResponse) response);

        if (encoding != null) {
            // ����request�ַ����
            request.setCharacterEncoding(encoding);
            // ����response�ַ����
            response.setContentType("text/html;charset=" + encoding);
        }

        doBeforeProcessing(wrappedRequest, wrappedResponse);

        Throwable problem = null;

        try {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            HttpSession session = servletRequest.getSession();
            //System.out.println(servletRequest.getServletPath());
            // 这里判断目录，后缀名，当然也可以写在web.xml中，用url-pattern进行拦截映射
            if ((!servletRequest.getServletPath().endsWith("login.html"))
                    && (!servletRequest.getServletPath().endsWith("history_list.html"))
                    && (!servletRequest.getServletPath().endsWith(".js"))
                    && (!servletRequest.getServletPath().endsWith(".css"))
                    && (!servletRequest.getServletPath().endsWith(".map"))
                    && (!servletRequest.getServletPath().endsWith(".do"))
                    && (!servletRequest.getServletPath().startsWith("/assets/img"))) {
                if (session.getAttribute("user") == null || session.getAttribute("loginType") == null) {
                    session.invalidate();
                    response.setContentType("text/html;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<script language='javascript' type='text/javascript'>");
                    out.println("alert('未登录或登录已超时!请你重新登录!');window.parent.location.href='" + servletRequest.getContextPath() + "/login.html';");
                    out.println("</script>");
                    //System.out.println("未登录或登录已超时!请你重新登录!");
                } else {
                    chain.doFilter(servletRequest, servletResponse);
                }
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }
//            chain.doFilter(new MyHttpServletRequest(servletRequest), response);
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
        }

        doAfterProcessing(wrappedRequest, wrappedResponse);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    class MyHttpServletRequest extends HttpServletRequestWrapper {

        private HttpServletRequest request = null;			//内置request对象
        private boolean isFirstLoad = true;				//因为在j2ee中request的map对象会被缓存

        public MyHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        public String getParameter(String name) {
            return this.request.getParameterValues(name) == null ? null : getParameterValues(name)[0];
        }

        public String[] getParameterValues(String name) {
            return (String[]) getParameterMap().get(name);
        }

        public Map getParameterMap() {

            Map<String, String[]> map = this.request.getParameterMap();
            try {
                //POST乱码解决
                if (this.request.getMethod().equals("POST")) {
                    request.setCharacterEncoding("UTF-8");
                    return map;
                }
                else if (this.request.getMethod().equals("GET")) //get方式解决乱发
                {
                    if (isFirstLoad) //第一次设置编码
                    {
                        this.isFirstLoad = false;///不设置缓存
                        for (Entry<String, String[]> entry : map.entrySet()) {
                            String[] vs = entry.getValue();
                            for (int i = 0; i < vs.length; i++) {
                                vs[i] = new String(vs[i].getBytes("UTF-8"), "UTF-8");
                            }
                        }
                        return map;
                    }
                } else {
                    return this.request.getParameterMap();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return map;

        }
    }

    /**
     * Return the filter configuration object for this filter.
     *
     * @return
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
        encoding = null;
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            // ��ȡ��ʼ������
            encoding = filterConfig.getInitParameter("encoding");
            if (debug) {
                log("CharactorFilter: Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("CharactorFilter()");
        }
        StringBuilder sb = new StringBuilder("CharactorFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); // NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); // NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    /**
     * This request wrapper class extends the support class
     * HttpServletRequestWrapper, which implements all the methods in the
     * HttpServletRequest interface, as delegations to the wrapped request. You
     * only need to override the methods that you need to change. You can get
     * access to the wrapped request using the method getRequest()
     */
    class RequestWrapper extends HttpServletRequestWrapper {

        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        // You might, for example, wish to add a setParameter() method. To do
        // this
        // you must also override the getParameter, getParameterValues,
        // getParameterMap,
        // and getParameterNames methods.
        protected HashMap localParams = null;

        public void setParameter(String name, String[] values) {
            if (debug) {
                //System.out.println("CharactorFilter::setParameter(" + name + "=" + values + ")" + " localParams = " + localParams);
            }

            if (localParams == null) {
                localParams = new HashMap();
                // Copy the parameters from the underlying request.
                Map wrappedParams = getRequest().getParameterMap();
                Set keySet = wrappedParams.keySet();
                for (Iterator it = keySet.iterator(); it.hasNext();) {
                    Object key = it.next();
                    Object value = wrappedParams.get(key);
                    localParams.put(key, value);
                }
            }
            localParams.put(name, values);
        }

        @Override
        public String getParameter(String name) {
            if (debug) {
                System.out.println("CharactorFilter::getParameter(" + name + ") localParams = " + localParams);
            }
            if (localParams == null) {
                return getRequest().getParameter(name);
            }
            Object val = localParams.get(name);
            if (val instanceof String) {
                return (String) val;
            }
            if (val instanceof String[]) {
                String[] values = (String[]) val;
                return values[0];
            }
            return (val == null ? null : val.toString());
        }

        @Override
        public String[] getParameterValues(String name) {
            if (debug) {
                System.out.println("CharactorFilter::getParameterValues(" + name + ") localParams = " + localParams);
            }
            if (localParams == null) {
                return getRequest().getParameterValues(name);
            }
            return (String[]) localParams.get(name);
        }

        /*
         @Override
         public Enumeration getParameterNames() {
         if (debug) {
         System.out.println("CharactorFilter::getParameterNames() localParams = " + localParams);
         }
         if (localParams == null) {
         return getRequest().getParameterNames();
         }
         return localParams.keys();
         }
         */
        @Override
        public Map getParameterMap() {
            if (debug) {
                System.out.println("CharactorFilter::getParameterMap() localParams = " + localParams);
            }
            if (localParams == null) {
                return getRequest().getParameterMap();
            }
            return localParams;
        }
    }

    /**
     * This response wrapper class extends the support class
     * HttpServletResponseWrapper, which implements all the methods in the
     * HttpServletResponse interface, as delegations to the wrapped response.
     * You only need to override the methods that you need to change. You can
     * get access to the wrapped response using the method getResponse()
     */
    class ResponseWrapper extends HttpServletResponseWrapper {

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
    }
}
