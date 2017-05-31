/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.push;

/**
 *
 * @author LFeng
 */
public class LoginFailMsg extends BaseMsg {
    private String failInfo;

    public LoginFailMsg(String failInfo) {
        super();
        setType(MsgType.LOGIN_FAIL);
    }

    public String getFailInfo() {
        return failInfo;
    }

    public void setFailInfo(String failInfo) {
        this.failInfo = failInfo;
    }
    
    
}
