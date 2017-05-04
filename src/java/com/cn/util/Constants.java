/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.util;

/**
 *
 * @author LFeng
 */
public class Constants {
    
    /** 英辰科技服务器*/
//    public static final String REDIS_HOST = "222.169.224.148";
    /** 北京刘总服务器 */
//    public static final String REDIS_HOST = "210.72.225.153";
    /** 北京刘总服务器 */
//    public static final String REDIS_HOST = "120.92.9.117";
    
//    public static final String REDIS_HOST = "127.0.0.1";
//    public static final int REDIS_PORT = 6379;
    
//    public static final double factoryLatitude = 43.851625;
//    public static final double factoryLongitude = 125.226824;
    public static final boolean DEBUG_MODE = true;
    public static final int MAX_RECORD = 999999;
    
    /**抢单成功*/
    public static final int GRAB_ORDER_STATUS_SUCCESS = 0;
    /**抢单异常*/
    public static final int GRAB_ORDER_STATUS_EXCEPTION = -1;
    /**司机或车辆有正在进行的订单*/
    public static final int GRAB_ORDER_STATUS_GRABED = 1;
    /**数量不足(按重量计算时, 剩余数量小于抢单时给出的重量)*/
    public static final int GRAB_ORDER_STATUS_TONNAGE_LACK = 2;
    /**已被他人抢完(按车辆数量计算时, 车辆数量已满)*/
    public static final int GRAB_ORDER_STATUS_GRABED_OTHER = 3;
    /**超过出价次数(已经出价两次)*/
    public static final int GRAB_ORDER_STATUS_PASS_TIMES = 4;
    /**第一次出价成功*/
    public static final int GRAB_ORDER_STATUS_FIRST_PASS_SUCCESS = 5;
    /**第二次出价成功*/
    public static final int GRAB_ORDER_STATUS_SECOND_PASS_SUCCESS = 6;
    /**车辆类型不对*/
    public static final int GRAB_ORDER_STATUS_CARTYPE_ERROR = 7;
    /**订单已失效*/
    public static final int GRAB_ORDER_STATUS_EXPIRED = 8;
    /**App版本过时, 请更新app*/
//    public static final int GRAB_ORDER_STATUS_OLD_APP = 9;
    
    /**
     * ****************************************短信发送平台********************************************
     */
    /**
     * 短信发送平台接口URL（开发）
     */
    public static final String SMS_PLATFORM_URL_DEVELOPER = "sandboxapp.cloopen.com";
    /**
     * 短信发送平台接口URL（发布）
     */
    public static final String SMS_PLATFORM_URL_DISTRIBUTION = "app.cloopen.com";
    /**
     * 短信发送平台接口端口
     */
    public static final String SMS_PLATFORM_PORT = "8883";
    /**
     * 短信发送平台主帐号（ACCOUNT SID）
     */
    public static final String SMS_PLATFORM_ACCOUNT_SID = "8a48b551544cd73f01545f7b485e1509";
    /**
     * 短信发送平台主帐号令牌（AUTH TOKEN）
     */
    public static final String SMS_PLATFORM_AUTH_TOKEN = "36dd1c84547f4426997cd3dc1c5f270b";
    /**
     * 短信发送平台应用ID（APP ID）
     */
    public static final String SMS_PLATFORM_APP_ID = "aaf98f89544cd9d901547ed3222b2daf";
    /**
     * 短信发送平台短信模板ID
     */
    public static final String SMS_PLATFORM_TEMPLATE_ID = "84111";
    /**
     * 短信发送平台过期分钟数
     */
    public static final String SMS_EXPIRED_MINUTE = "5";
}
