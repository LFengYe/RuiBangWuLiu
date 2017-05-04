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
    
    private String rightCode;
    private String rightName;
    private String righthyperlnk;

    public String getRightCode() {
        return rightCode;
    }

    public void setRightCode(String RightCode) {
        this.rightCode = RightCode;
    }

    public String getRightName() {
        return rightName;
    }

    public void setRightName(String RightName) {
        this.rightName = RightName;
    }

    public String getRighthyperlnk() {
        return righthyperlnk;
    }

    public void setRighthyperlnk(String Righthyperlnk) {
        this.righthyperlnk = Righthyperlnk;
    }
}
