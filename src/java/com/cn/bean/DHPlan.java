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
public class DHPlan {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "调货计划单号")
    private String dHPlanID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "到货时间")
    private String dhTime;
    @FieldDescription(description = "制单人员姓名")
    private String dhProducerName;
    @FieldDescription(description = "制单时间")
    private String dhProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String dhAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String dhAuditTime;
    @FieldDescription(description = "客户(送货方)确认")
    private String shAcknowledge;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String dHPlanRemark;
}
