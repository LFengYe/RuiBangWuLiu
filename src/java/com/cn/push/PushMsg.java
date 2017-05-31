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
public class PushMsg extends BaseMsg {
    
    private String title;
    private String content;

    public PushMsg() {
        super();
        setType(MsgType.PUSH);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
