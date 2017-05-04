/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.move;

import com.cn.bean.FieldDescription;

/**
 *
 * @author Administrator
 */
public class InspectionReportList {
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "备注")
    private String inspectionReportListRemark;
    @FieldDescription(description = "检验时间")
    private String inspectionTime;
    @FieldDescription(description = "检验员")
    private String inspectorName;
    @FieldDescription(description = "检验结论")
    private String inspectionResult;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getInspectionReportListRemark() {
        return inspectionReportListRemark;
    }

    public void setInspectionReportListRemark(String inspectionReportListRemark) {
        this.inspectionReportListRemark = inspectionReportListRemark;
    }

    public String getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(String inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getInspectionResult() {
        return inspectionResult;
    }

    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }
}
