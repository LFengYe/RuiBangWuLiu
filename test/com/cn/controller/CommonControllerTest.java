/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.util.DatabaseOpt;
import java.sql.CallableStatement;
import java.sql.Connection;
import org.dom4j.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LFeng
 */
public class CommonControllerTest {
    
    public CommonControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of dataBaseOperate method, of class CommonController.
     */
    @Test
    public void testDataBaseOperate() throws Exception {
        System.out.println("dataBaseOperate");
        String datas = "[{\"PartID\":\"部品编号,\",\"PartName\":\"部品名称,string,50\",\"PartCode\":\"部品件号,string,@\",\"AutoStyling\":\"330\",\"PartUnit\":\"部品\",\"DCAmount\":\"6\",\"InboundContainer\":\"入库盛具\",\"InboundPackageAmount\":\"0\",\"OutboundContainer\":\"出库盛具\",\"OutboundPackageAmount\":\"0\",\"Disabled\":\"0\",\"PinyinAbb\":\"拼音助计\",\"PartCategory\":\"标件类\",\"PartPicture\":\"部品图片,string,100\",\"PartBaseInfoRemark\":\"备注,string,500\",\"PSAddress1\":\"配送地址\",\"PSAddress2\":\"配送地址\",\"PSAddress3\":\"配送地址\"},{\"PartCode\":\"部品件号,str1ing,50\"},{\"PartID\":\"部品编,\",\"PartName\":\"部品名称,string,@\",\"PartCode\":\"部品件号,str1ing,50\",\"AutoStyling\":\"C07\",\"PartUnit\":\"部品\",\"DCAmount\":\"6\",\"InboundContainer\":\"入库盛具\",\"InboundPackageAmount\":\"0\",\"OutboundContainer\":\"出库盛具\",\"OutboundPackageAmount\":\"0\",\"Disabled\":\"0\",\"PinyinAbb\":\"拼音助计,string,100\",\"PartCategory\":\"标件类\",\"PartPicture\":\"部品图片,string,100\",\"PartBaseInfoRemark\":\"备注,string,500\",\"PSAddress1\":\"配送地址1\",\"PSAddress2\":\"配送地址2update\",\"PSAddress3\":\"配送地址3\"},{\"PartCode\":\"部品件号,string,@\"}]";
        String tableName = "PartBaseInfo";
        String operate = "update";
        DatabaseOpt opt = new DatabaseOpt();
        Connection conn = opt.getConnect();
        CommonController instance = new CommonController();
        int expResult = 0;
        int result = instance.dataBaseOperate(datas, tableName, operate, conn);
    }

    /**
     * Test of setFieldValue method, of class CommonController.
     */
    @Test
    public void testSetFieldValue() throws Exception {
        System.out.println("setFieldValue");
        Class objClass = null;
        String fieldName = "";
        String fieldValue = "";
        CallableStatement statement = null;
        int fieldIndex = 0;
        CommonController instance = new CommonController();
        instance.setFieldValue(objClass, fieldName, fieldValue, statement, fieldIndex);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFieldSQLStr method, of class CommonController.
     */
    @Test
    public void testGetFieldSQLStr() throws Exception {
        System.out.println("getFieldSQLStr");
        Class objClass = null;
        String fieldName = "";
        String fieldValue = "";
        CommonController instance = new CommonController();
        String expResult = "";
        String result = instance.getFieldSQLStr(objClass, fieldName, fieldValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasRight method, of class CommonController.
     */
    @Test
    public void testHasRight() {
        System.out.println("hasRight");
        Element element = null;
        CommonController instance = new CommonController();
        String expResult = "";
        String result = instance.hasRight(element);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
