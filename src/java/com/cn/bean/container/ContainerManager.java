/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.container;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class ContainerManager {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具名称")
    private String containerName;
    @FieldDescription(description = "盛具编号")
    private String containerCode;
    @FieldDescription(description = "盛具状态")
    private String containerStatus;
    @FieldDescription(description = "操作类型")
    private String operateType;
    @FieldDescription(description = "数量")
    private int operateAmount;
    @FieldDescription(description = "最大数量", operate = "display")
    private int operateMaxAmount;
    @FieldDescription(description = "制单人")
    private String operateProducerName;
    @FieldDescription(description = "制单时间")
    private String operateProducerTime;
    @FieldDescription(description = "备注")
    private String containerManagerRemark;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public int getOperateAmount() {
        return operateAmount;
    }

    public void setOperateAmount(int operateAmount) {
        this.operateAmount = operateAmount;
    }

    public String getContainerManagerRemark() {
        return containerManagerRemark;
    }

    public void setContainerManagerRemark(String containerManagerRemark) {
        this.containerManagerRemark = containerManagerRemark;
    }

    public String getOperateProducerName() {
        return operateProducerName;
    }

    public void setOperateProducerName(String operateProducerName) {
        this.operateProducerName = operateProducerName;
    }

    public String getOperateProducerTime() {
        return operateProducerTime;
    }

    public void setOperateProducerTime(String operateProducerTime) {
        this.operateProducerTime = operateProducerTime;
    }

    public int getOperateMaxAmount() {
        return operateMaxAmount;
    }

    public void setOperateMaxAmount(int operateMaxAmount) {
        this.operateMaxAmount = operateMaxAmount;
    }

    public String getContainerStatus() {
        return containerStatus;
    }

    public void setContainerStatus(String containerStatus) {
        this.containerStatus = containerStatus;
    }
}
