/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class FZOutWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "分装入库单号")
    private String fzOutWareHouseID;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "入库数量")
    private int outboundAmount;
    @FieldDescription(description = "入库箱数")
    private int outboundBoxAmount;
    @FieldDescription(description = "制单人员姓名")
    private String fzOutProducerName;
    @FieldDescription(description = "制单时间", type = "date")
    private String fzOutProduceTime;
    @FieldDescription(description = "备注")
    private String fzOutRemark;

    public String getFzOutWareHouseID() {
        return fzOutWareHouseID;
    }

    public void setFzOutWareHouseID(String fzOutWareHouseID) {
        this.fzOutWareHouseID = fzOutWareHouseID;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getOutboundAmount() {
        return outboundAmount;
    }

    public void setOutboundAmount(int outboundAmount) {
        this.outboundAmount = outboundAmount;
    }

    public int getOutboundBoxAmount() {
        return outboundBoxAmount;
    }

    public void setOutboundBoxAmount(int outboundBoxAmount) {
        this.outboundBoxAmount = outboundBoxAmount;
    }

    public String getFzOutProducerName() {
        return fzOutProducerName;
    }

    public void setFzOutProducerName(String fzOutProducerName) {
        this.fzOutProducerName = fzOutProducerName;
    }

    public String getFzOutProduceTime() {
        return fzOutProduceTime;
    }

    public void setFzOutProduceTime(String fzOutProduceTime) {
        this.fzOutProduceTime = fzOutProduceTime;
    }

    public String getFzOutRemark() {
        return fzOutRemark;
    }

    public void setFzOutRemark(String fzOutRemark) {
        this.fzOutRemark = fzOutRemark;
    }

    
}
