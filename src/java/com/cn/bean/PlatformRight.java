/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

/**
 *
 * @author LFeng
 */
public class PlatformRight {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    private String RightCode;
    private String RightName;
    private String Righthyperlnk;

    public String getRightCode() {
        return RightCode;
    }

    public void setRightCode(String RightCode) {
        this.RightCode = RightCode;
    }

    public String getRightName() {
        return RightName;
    }

    public void setRightName(String RightName) {
        this.RightName = RightName;
    }

    public String getRighthyperlnk() {
        return Righthyperlnk;
    }

    public void setRighthyperlnk(String Righthyperlnk) {
        this.Righthyperlnk = Righthyperlnk;
    }
}
