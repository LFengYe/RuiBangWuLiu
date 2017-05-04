/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.base;

import com.cn.bean.out.LPKCListInfo;
import com.cn.util.Constants;
import com.google.common.base.Predicate;

/**
 *
 * @author LFeng
 */
public class CustomPredicate implements Predicate<LPKCListInfo>{
    private static int kcCount;

    public static int getKcCount() {
        return kcCount;
    }

    public static void setKcCount(int aKcCount) {
        kcCount = aKcCount;
    }
    private String filterStr;

    public CustomPredicate(String filterStr) {
        this.filterStr = filterStr;
    }
    
    @Override
    public boolean apply(LPKCListInfo input) {
        if ((input.getSupplierID() + "," + input.getPartCode()).compareToIgnoreCase(filterStr) == 0) {
            kcCount += input.getLpAmount();
            return true;
        }
        return false;
    }

    public String getFilterStr() {
        return filterStr;
    }

    public void setFilterStr(String filterStr) {
        this.filterStr = filterStr;
    }
    
}
