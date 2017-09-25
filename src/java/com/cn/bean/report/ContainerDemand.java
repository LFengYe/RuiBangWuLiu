/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

/**
 *
 * @author LFeng
 */
public class ContainerDemand {
    private String outboundContainerName;
    private String containerAmount;
    private String containerLPTotal;
    private int shortAmount;
    private String demandStatus;

    public String getOutboundContainerName() {
        return outboundContainerName;
    }

    public void setOutboundContainerName(String outboundContainerName) {
        this.outboundContainerName = outboundContainerName;
    }

    public String getContainerAmount() {
        return containerAmount;
    }

    public void setContainerAmount(String containerAmount) {
        this.containerAmount = containerAmount;
    }

    public String getContainerLPTotal() {
        return containerLPTotal;
    }

    public void setContainerLPTotal(String containerLPTotal) {
        this.containerLPTotal = containerLPTotal;
    }

    public int getShortAmount() {
        return shortAmount;
    }

    public void setShortAmount(int shortAmount) {
        this.shortAmount = shortAmount;
    }

    public String getDemandStatus() {
        return demandStatus;
    }

    public void setDemandStatus(String demandStatus) {
        this.demandStatus = demandStatus;
    }
}
