/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author LFeng
 */
public class DJWareHouse {
    
    @JSONField(ordinal = 1)
    public String DJInWareHouseID;
    @JSONField(ordinal = 2)
    public int SupplierID;
    @JSONField(ordinal = 3)
    public String SupplierName;
    @JSONField(ordinal = 4)
    public String InboundBatch;
    @JSONField(ordinal = 5)
    public String DJRKProducerName;
    @JSONField(ordinal = 6)
    public String DJRKProduceTime;
    @JSONField(ordinal = 7)
    public String DJRKAuditStaffName;
    @JSONField(ordinal = 8)
    public String DJRKAuditTime;
    @JSONField(ordinal = 9)
    public String PrintFlag;
    @JSONField(ordinal = 10)
    public String DJINWareHousRemark;
}
