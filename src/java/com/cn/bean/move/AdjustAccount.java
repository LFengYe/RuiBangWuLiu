/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.move;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class AdjustAccount {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "调帐编号")
    private String tzYMonth;
    @FieldDescription(description = "调帐制单时间", type = "date")
    private String tzProduceTime;
    @FieldDescription(description = "调账备注")
    private String tzRemark;

    public String getTzYMonth() {
        return tzYMonth;
    }

    public void setTzYMonth(String tzYMonth) {
        this.tzYMonth = tzYMonth;
    }

    public String getTzProduceTime() {
        return tzProduceTime;
    }

    public void setTzProduceTime(String tzProduceTime) {
        this.tzProduceTime = tzProduceTime;
    }

    public String getTzRemark() {
        return tzRemark;
    }

    public void setTzRemark(String tzRemark) {
        this.tzRemark = tzRemark;
    }
}
