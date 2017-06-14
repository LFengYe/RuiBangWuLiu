/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Customer;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartBomInfo;
import com.cn.bean.base.CustomPredicate;
import com.cn.bean.out.FJHOutWareHouse;
import com.cn.bean.out.FJHOutWareHouseList;
import com.cn.bean.out.JHOutWareHouse;
import com.cn.bean.out.JHOutWareHouseList;
import com.cn.bean.out.LPKCListInfo;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class JHOutWareHouseController {

    private static final Logger logger = Logger.getLogger(JHOutWareHouseController.class);
    
    /**
     * 导入计划明细
     *
     * @param importData 导入的计划明细
     * @param jhInfo 计划信息
     * @return
     */
    public ArrayList<JHOutWareHouseList> importData(List<Object> importData, String jhInfo) {
        try {
            JHOutWareHouse jhOutWareHouse = JSONObject.parseObject(jhInfo, JHOutWareHouse.class);
            /**
             * 当前库存列表
             */
            ArrayList<LPKCListInfo> kcAmount = getLPKCData(null, null, null);
            /**
             * 处理之后的计划明细
             */
            ArrayList<JHOutWareHouseList> importResult = new ArrayList<>();
            /**
             * 分解完成的计划明细
             */
            ArrayList<JHOutWareHouseList> completeResult = new ArrayList<>();

            if (importData != null && importData.size() > 0) {
                /**
                 * 是否满足全部计划
                 */
                boolean isAllEnough = true;
                Iterator<Object> it = importData.iterator();
                while (it.hasNext()) {
                    /**
                     * 将导入的数据转成计划明细对象
                     */
                    JHOutWareHouseList item = (JHOutWareHouseList) it.next();
                    /**
                     * 获取该计划明细对应的部品基础信息
                     */
                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + item.getPartCode()), PartBaseInfo.class);
                    if (baseInfo == null) {
                        item.setListNumber(-2);//没有对应件号
                        item.setFailedReason("没有该件号");
                        importResult.add(item);
                        isAllEnough = false;
                        continue;
                    }
                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + item.getSupplierID()), Customer.class);
                    if (customer == null) {
                        item.setListNumber(-3);//没有供应商信息
                        item.setFailedReason("没有该供应商");
                        importResult.add(item);
                        isAllEnough = false;
                        continue;
                    }
                    /**
                     * 获取该计划明细对应的部品盛具信息
                     */
                    GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(item.getSupplierID() + "_" + item.getPartCode()), GYSPartContainerInfo.class);
                    if (containerInfo == null) {
                        item.setListNumber(-4);//没有出库盛具信息
                        item.setFailedReason("没有出库盛具信息");
                        importResult.add(item);
                        isAllEnough = false;
                        continue;
                    }
                    /**
                     * 该计划明细的筛选字符串(供应商ID + 逗号 + 件号)
                     */
                    String filterStr = item.getSupplierID() + "," + item.getPartCode();
                    /**
                     * 谓语对象, 用户筛选库存列表
                     */
                    CustomPredicate predicate = new CustomPredicate(filterStr);
                    CustomPredicate.setKcCount(0);//部件编号对应库存总量初始化
                    //Predicate<LPKCList> predicate = (LPKCList input) -> (input.getSupplierID() + "," + input.getPartCode()).compareToIgnoreCase(filterStr) == 0;
                    /**
                     * 符合筛选条件的库存列表
                     */
                    int kcCount = 0;
                    ArrayList<LPKCListInfo> subKCAmount = new ArrayList<>();
                    kcCount = getSubLPKCList(kcAmount, item.getSupplierID(), item.getPartCode(), subKCAmount);
//                    ArrayList<LPKCListInfo> subKCAmount = (ArrayList<LPKCListInfo>) Collections2.filter(kcAmount, predicate);
                    /**
                     * 符合筛选条件库存列表的总库存数量是否满足计划出库数量
                     */
                    int ckAmount = item.getJhCKAmount();
                    //System.out.println("kc:" + kcCount + ",ckAmount:" + ckAmount);
                    if (kcCount >= ckAmount) {
                        //库存能够满足计划
                        item.setListNumber(0);
                        
                        Iterator<LPKCListInfo> iterator = subKCAmount.iterator();
                        while (ckAmount > 0) {
                            LPKCListInfo lpkcl = iterator.next();
                            JHOutWareHouseList detail = new JHOutWareHouseList();
                            detail.setListNumber(completeResult.size() + 1);
                            detail.setSupplierID(item.getSupplierID());
                            detail.setSupplierName(customer.getCustomerAbbName());
                            detail.setPartCode(item.getPartCode());
                            detail.setPartID(baseInfo.getPartID());
                            detail.setPartName(baseInfo.getPartName());
                            detail.setAutoStylingName(baseInfo.getAutoStylingName());
                            detail.setInboundBatch(lpkcl.getInboundBatch());
                            if (ckAmount > lpkcl.getLpAmount()) {
                                detail.setJhCKAmount(lpkcl.getLpAmount());
                                detail.setContainerAmount((lpkcl.getLpAmount() % containerInfo.getOutboundPackageAmount() == 0) ? (lpkcl.getLpAmount() / containerInfo.getOutboundPackageAmount()) : (lpkcl.getLpAmount() / containerInfo.getOutboundPackageAmount() + 1));//containerAmount当一个批次不满一个盛具如何处理, 待解决
                            } else {
                                detail.setJhCKAmount(ckAmount);
                                detail.setContainerAmount((ckAmount % containerInfo.getOutboundPackageAmount() == 0) ? (ckAmount / containerInfo.getOutboundPackageAmount()) : (ckAmount / containerInfo.getOutboundPackageAmount() + 1));//containerAmount当一个批次不满一个盛具如何处理, 待解决
//                                break;
                            }
                            detail.setOutboundContainerName(containerInfo.getOutboundContainerName());
                            detail.setJhOutWareHouseListRemark(item.getJhOutWareHouseListRemark());
                            detail.setJhOutWareHouseID(jhOutWareHouse.getJhOutWareHouseID());

                            completeResult.add(detail);
                            ckAmount -= lpkcl.getLpAmount();
                        }
//                        return completeResult;
                    } else {
                        item.setListNumber(-1);//库存不能够满足计划
                        item.setFailedReason("库存不足");
                        isAllEnough = false;
                    }
                    item.setSupplierName(customer.getCustomerAbbName());
                    item.setPartID(baseInfo.getPartID());
                    item.setPartName(baseInfo.getPartName());
                    item.setAutoStylingName(baseInfo.getAutoStylingName());
                    item.setOutboundContainerName(containerInfo.getOutboundContainerName());
                    item.setJhOutWareHouseID(jhOutWareHouse.getJhOutWareHouseID());
                    importResult.add(item);
                }
                
                if (isAllEnough) {
                    RedisAPI.set(jhOutWareHouse.getJhOutWareHouseID(), JSONObject.toJSONString(completeResult));
                    return completeResult;
                } else {
                    return importResult;
                }
            }
        } catch (Exception ex) {
            logger.error("计划导入异常!", ex);
        }
        return null;
    }

    public ArrayList<FJHOutWareHouseList> importFJHData(List<Object> importData, String jhInfo) {
        try {
            FJHOutWareHouse fjhOutWareHouse = JSONObject.parseObject(jhInfo, FJHOutWareHouse.class);
            /**
             * 当前库存列表
             */
            ArrayList<LPKCListInfo> kcAmount = getLPKCData(null, null, null);
            /**
             * 处理之后的计划明细
             */
            ArrayList<FJHOutWareHouseList> importResult = new ArrayList<>();
            /**
             * 分解完成的计划明细
             */
            ArrayList<FJHOutWareHouseList> completeResult = new ArrayList<>();

            if (importData != null && importData.size() > 0) {
                /**
                 * 是否满足全部计划
                 */
                boolean isAllEnough = true;
                Iterator<Object> it = importData.iterator();
                while (it.hasNext()) {
                    /**
                     * 将导入的数据转成计划明细对象
                     */
                    FJHOutWareHouseList item = (FJHOutWareHouseList) it.next();
                    /**
                     * 获取该计划明细对应的部品基础信息
                     */
                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + item.getPartCode()), PartBaseInfo.class);
                    if (baseInfo == null) {
                        item.setListNumber(-2);//没有对应件号
                        item.setFailedReason("没有该件号");
                        importResult.add(item);
                        isAllEnough = false;
                        continue;
                    }
                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + item.getSupplierID()), Customer.class);
                    if (customer == null) {
                        item.setListNumber(-3);//没有供应商信息
                        item.setFailedReason("没有该供应商");
                        importResult.add(item);
                        isAllEnough = false;
                        continue;
                    }
                    /**
                     * 获取该计划明细对应的部品盛具信息
                     */
                    GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(item.getSupplierID() + "_" + item.getPartCode()), GYSPartContainerInfo.class);
                    if (containerInfo == null) {
                        item.setListNumber(-4);//没有出库盛具信息
                        item.setFailedReason("没有出库盛具信息");
                        importResult.add(item);
                        isAllEnough = false;
                        continue;
                    }
                    /**
                     * 该计划明细的筛选字符串(供应商ID + 逗号 + 件号)
                     */
//                    String filterStr = item.getSupplierID() + "," + item.getPartCode();
                    /**
                     * 谓语对象, 用户筛选库存列表
                     */
//                    CustomPredicate predicate = new CustomPredicate(filterStr);
//                    CustomPredicate.setKcCount(0);//部件编号对应库存总量初始化
                    //Predicate<LPKCList> predicate = (LPKCList input) -> (input.getSupplierID() + "," + input.getPartCode()).compareToIgnoreCase(filterStr) == 0;
                    /**
                     * 符合筛选条件的库存列表
                     */
                    int kcCount = 0;
                    ArrayList<LPKCListInfo> subKCAmount = new ArrayList<>();
                    kcCount = getSubLPKCList(kcAmount, item.getSupplierID(), item.getPartCode(), subKCAmount);
//                    ArrayList<LPKCListInfo> subKCAmount = (ArrayList<LPKCListInfo>) Collections2.filter(kcAmount, predicate);
                    /**
                     * 符合筛选条件库存列表的总库存数量是否满足计划出库数量
                     */
                    int ckAmount = item.getFjhCKAmount();
                    //System.out.println("kc:" + kcCount + ",ckAmount:" + ckAmount);
                    if (kcCount >= ckAmount) {
                        //库存能够满足计划
                        item.setListNumber(0);
                        
                        Iterator<LPKCListInfo> iterator = subKCAmount.iterator();
                        while (ckAmount > 0) {
                            LPKCListInfo lpkcl = iterator.next();
                            FJHOutWareHouseList detail = new FJHOutWareHouseList();
                            detail.setListNumber(completeResult.size() + 1);
                            detail.setSupplierID(item.getSupplierID());
                            detail.setSupplierName(customer.getCustomerAbbName());
                            detail.setPartCode(item.getPartCode());
                            detail.setPartID(baseInfo.getPartID());
                            detail.setPartName(baseInfo.getPartName());
                            detail.setAutoStylingName(baseInfo.getAutoStylingName());
                            detail.setInboundBatch(lpkcl.getInboundBatch());
                            if (ckAmount > lpkcl.getLpAmount()) {
                                detail.setFjhCKAmount(lpkcl.getLpAmount());
                                //detail.setContainerAmount((lpkcl.getLpAmount() % containerInfo.getOutboundPackageAmount() == 0) ? (lpkcl.getLpAmount() / containerInfo.getOutboundPackageAmount()) : (lpkcl.getLpAmount() / containerInfo.getOutboundPackageAmount() + 1));//containerAmount当一个批次不满一个盛具如何处理, 待解决
                            } else {
                                detail.setFjhCKAmount(ckAmount);
                                //detail.setContainerAmount((ckAmount % containerInfo.getOutboundPackageAmount() == 0) ? (ckAmount / containerInfo.getOutboundPackageAmount()) : (ckAmount / containerInfo.getOutboundPackageAmount() + 1));//containerAmount当一个批次不满一个盛具如何处理, 待解决
//                                break;
                            }
                            //detail.setOutboundContainerName(containerInfo.getOutboundContainerName());
                            detail.setFjhOutWareHouseListRemark(item.getFjhOutWareHouseListRemark());
                            detail.setFjhOutWareHouseID(fjhOutWareHouse.getFjhOutWareHouseID());

                            completeResult.add(detail);
                            ckAmount -= lpkcl.getLpAmount();
                        }
//                        return completeResult;
                    } else {
                        item.setListNumber(-1);//库存不能够满足计划
                        item.setFailedReason("库存不足");
                        isAllEnough = false;
                    }
                    item.setSupplierName(customer.getCustomerAbbName());
                    item.setPartID(baseInfo.getPartID());
                    item.setPartName(baseInfo.getPartName());
                    item.setAutoStylingName(baseInfo.getAutoStylingName());
                    //item.setOutboundContainerName(containerInfo.getOutboundContainerName());
                    item.setFjhOutWareHouseID(fjhOutWareHouse.getFjhOutWareHouseID());
                    importResult.add(item);
                }
                
                if (isAllEnough) {
                    RedisAPI.set(fjhOutWareHouse.getFjhOutWareHouseID(), JSONObject.toJSONString(completeResult));
                    return completeResult;
                } else {
                    return importResult;
                }
            }
        } catch (Exception ex) {
            logger.error("计划导入异常!", ex);
        }
        return null;
    }
    
    /**
     * 导入总成计划, 将总成计划根据BOM信息分解成明细计划
     * @param importData
     * @param jhInfo
     * @return 
     */
    public ArrayList<Object> importZCData(List<Object> importData) {
        try {
            /**
             * 分解完成的计划明细
             */
            ArrayList<Object> completeResult = new ArrayList<>();

            if (importData != null && importData.size() > 0) {
                Iterator<Object> it = importData.iterator();
                while (it.hasNext()) {
                    /**
                     * 将导入的数据转成计划明细对象
                     */
                    JHOutWareHouseList item = (JHOutWareHouseList) it.next();
                    /**
                     * 获取该总成的Bom信息
                     */
                    String bomRedisKey = "bomInfo_" + item.getSupplierID() + "_" + item.getPartCode();
                    List<String> bomInfos = RedisAPI.getSet(bomRedisKey);
                    for (String str : bomInfos) {
                        PartBomInfo bomInfo = JSONObject.parseObject(str, PartBomInfo.class);
                        JHOutWareHouseList list = new JHOutWareHouseList();
                        list.setSupplierID(bomInfo.getSupplierID());
                        list.setPartCode(bomInfo.getDetailPartCode());
                        list.setJhCKAmount(item.getJhCKAmount() * bomInfo.getDcAmount());
                        list.setJhOutWareHouseListRemark(item.getJhOutWareHouseListRemark());
                        completeResult.add(list);
                    }
                }
                return completeResult;
            }
        } catch (Exception ex) {
            logger.error("计划导入异常!", ex);
        }
        return null;
    }
    
    /**
     * 将总成计划写入分装表(分装入库和分装出库)
     * @param houseList 
     */
    public void writeZCJHToFZ(JHOutWareHouseList houseList) {
        
    }
    
    /**
     * 获取给定的厂家与产品的良品库存列表
     * @param kcAmount
     * @param supplierID
     * @param partCode
     * @param subKCAmount
     * @return 
     */
    private int getSubLPKCList(ArrayList<LPKCListInfo> kcAmount, String supplierID, String partCode, ArrayList<LPKCListInfo> subKCAmount) {
        int kcCount = 0;
        if (subKCAmount == null) {
            subKCAmount = new ArrayList<>();
        }

        for (LPKCListInfo data : kcAmount) {

            if (data.getSupplierID().compareToIgnoreCase(supplierID) == 0
                    && data.getPartCode().compareToIgnoreCase(partCode) == 0) {
                kcCount += data.getLpAmount();
                subKCAmount.add(data);
            }
        }
        return kcCount;
    }

    /**
     * 获取良品库存列表
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param lastJZYMonth 最近结转时间
     * @return
     * @throws java.lang.Exception
     */
    public ArrayList<LPKCListInfo> getLPKCData(String beginDate, String endDate, String lastJZYMonth) throws Exception {
        CallableStatement statement = null;
        Connection conn = null;
        DatabaseOpt opt = new DatabaseOpt();
//        Class objClass = Class.forName("com.cn.bean.base.LPKCList");
        try {
            conn = opt.getConnect();
            statement = conn.prepareCall("{call spGetKFJCLpListForJHCK(?, ?, ?)}");
            statement.setString("BeginTime", beginDate);
            statement.setString("EndTime", endDate);
            statement.setString("LastJZYMonth", lastJZYMonth);
            ResultSet set = statement.executeQuery();
//            Method[] methods = objClass.getMethods();
            ArrayList<LPKCListInfo> result = new ArrayList<>();
            while (set.next()) {
                LPKCListInfo object = new LPKCListInfo();
                object.setSupplierID(set.getString("SupplierID"));
                object.setPartCode(set.getString("PartCode"));
                object.setLpAmount(set.getInt("KFJCLp"));
                object.setInboundBatch(set.getString("InboundBatch"));
                PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + object.getPartCode()), PartBaseInfo.class);
                Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + object.getSupplierID()), Customer.class);
                object.setSupplierName(customer.getCustomerAbbName());
                object.setPartID(baseInfo.getPartID());
                object.setPartName(baseInfo.getPartName());
                /*
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.startsWith("set") && !Modifier.isStatic(method.getModifiers())) {
                        // 根据方法名字得到数据表格中字段的名字
                        String columnName = methodName.substring(3, methodName.length());
                        // 得到方法的参数类型
                        Class[] parmts = method.getParameterTypes();
                        if (parmts[0] == int.class) {
                            method.invoke(object, set.getInt(columnName));
                        } else if (parmts[0] == boolean.class) {
                            method.invoke(object, set.getBoolean(columnName));
                        } else if (parmts[0] == float.class) {
                            method.invoke(object, set.getFloat(columnName));
                        } else if (parmts[0] == double.class) {
                            method.invoke(object, set.getDouble(columnName));
                        } else {
                            method.invoke(object, set.getString(columnName));
                        }
                    }
                }
                 */
                result.add(object);
            }
            return result;
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("数据库关闭连接错误", ex);
            }
        }
        return null;
    }
    
    /**
     * 计划分包
     * @param jhOutWareHouseID 
     * @return  
     * @throws java.lang.Exception 
     */
    public ArrayList<Integer> jhPartitionPackage(String jhOutWareHouseID) throws Exception {
        JSONArray params = new JSONArray();
        DatabaseOpt opt = new DatabaseOpt();
        CommonController commonController = new CommonController();

        List<JHOutWareHouseList> res = JSONObject.parseArray(RedisAPI.get(jhOutWareHouseID), JHOutWareHouseList.class);
        /*
        if (res == null || res.isEmpty()) {
            JSONObject params1 = new JSONObject();
            params1.put("JHOutWareHouseID", "string," + jhOutWareHouseID);
            res = commonController.proceduceQuery("tbGetJHOutWareListSum", params1, "com.cn.bean.out.JHOutWareHouseList", opt.getConnect());
        }
        */
        if (res != null && !res.isEmpty()) {
            Iterator<JHOutWareHouseList> iterator = res.iterator();
            JHOutWareHouseList oldList = null;
            int oldPackingNum = 0;
            boolean isFull = true;
            
            while (iterator.hasNext()) {
                JHOutWareHouseList list = iterator.next();
                GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(list.getSupplierID() + "_" + list.getPartCode()), GYSPartContainerInfo.class);
                
                //新计划初始化参数
                if (oldList != null && (!(oldList.getSupplierID().compareTo(list.getSupplierID()) == 0)
                        || !(oldList.getPartCode().compareTo(list.getPartCode()) == 0))) {
                    isFull = true;
                    oldPackingNum = 0;
                }
                int containerAmount;
                int tmpJhCkAmount;
                //计算当前批次需要分包的计划数量
                if (isFull) {
                    tmpJhCkAmount = list.getJhCKAmount();
                } else {
                    //同一计划, 不同批次, 若上一批次最后一箱不满, 将上一批次最后一箱填满, 本批次计划数量减去上一个批次最后一包空余数量
                    //先取出上批次最后一箱数据, 再增加本批次填满数据
                    JSONObject preLastObj = params.getJSONObject(params.size() - 1);
                    tmpJhCkAmount = list.getJhCKAmount() - containerInfo.getOutboundPackageAmount() + Integer.valueOf(preLastObj.getString("PackingAmount").split(",")[1]);
                    
                    JSONObject object = new JSONObject();
                    object.put("SupplierID", "string," + list.getSupplierID());
                    object.put("PartCode", "string," + list.getPartCode());
                    object.put("JHOutWareHouseID", "string," + jhOutWareHouseID);
                    object.put("PackingNumber", "int," + oldPackingNum);
                    object.put("InBoundBatch", "string," + list.getInboundBatch());
                    object.put("PackingAmount", "int," + (containerInfo.getOutboundPackageAmount() - Integer.valueOf(preLastObj.getString("PackingAmount").split(",")[1])));
                    params.add(object);
                }
                //计算当前批次计划是否能完全分包
                if (tmpJhCkAmount % containerInfo.getOutboundPackageAmount() == 0) {
                    containerAmount = tmpJhCkAmount / containerInfo.getOutboundPackageAmount();
                    isFull = true;
                } else {
                    containerAmount = (tmpJhCkAmount / containerInfo.getOutboundPackageAmount() + 1);
                    isFull = false;
                }
                //计划明细分包
                for (int i = 0; i < containerAmount; i++) {
                    JSONObject object = new JSONObject();
                    object.put("SupplierID", "string," + list.getSupplierID());
                    object.put("PartCode", "string," + list.getPartCode());
                    object.put("JHOutWareHouseID", "string," + jhOutWareHouseID);
                    object.put("PackingNumber", "int," + (i + 1 + oldPackingNum));
                    object.put("InBoundBatch", "string," + list.getInboundBatch());
                    
                    if (i == containerAmount - 1 && !isFull) {
                        int packAmount = list.getJhCKAmount() - (containerInfo.getOutboundPackageAmount() * i);
                        object.put("PackingAmount", "int," + packAmount);
                    } else {
                        object.put("PackingAmount", "int," + containerInfo.getOutboundPackageAmount());
                    }
                    params.add(object);
                }
                
                oldPackingNum += containerAmount;
                oldList = list;
            }
            //System.out.println("part parasm:" + params.toJSONString());
            return commonController.proceduceForUpdate("tbAddJHPartitionPackageInfo", params, opt.getConnect());
        }
        return null;
    }
    
    public ArrayList<Integer> jhPartitionPackageHasNoBatch(String jhOutWareHouseID) throws Exception {
        JSONArray params = new JSONArray();
        DatabaseOpt opt = new DatabaseOpt();
        CommonController commonController = new CommonController();
        JSONObject params1 = new JSONObject();
        params1.put("JHOutWareHouseID", "string," + jhOutWareHouseID);
        List<Object> res = commonController.dataBaseQuery("table", "com.cn.bean.out.", "JHOutWareHouseList", "*", "", 0, Integer.MAX_VALUE, "JHOutWareHouseID", 0, opt.getConnect());
        if (res != null && !res.isEmpty()) {
            Iterator iterator = res.iterator();
            while (iterator.hasNext()) {
                JHOutWareHouseList list = (JHOutWareHouseList) iterator.next();
                GYSPartContainerInfo containerInfo = JSONObject.parseObject(RedisAPI.get(list.getSupplierID() + "_" + list.getPartCode()), GYSPartContainerInfo.class);
//                System.out.println("containerInfo:" + JSONObject.toJSONString(containerInfo));
                int containerAmount; boolean isFull = false;
                if (list.getJhCKAmount() % containerInfo.getOutboundPackageAmount() == 0) {
                    containerAmount = list.getJhCKAmount() / containerInfo.getOutboundPackageAmount();
                    isFull = true;
                } else {
                    containerAmount = (list.getJhCKAmount() / containerInfo.getOutboundPackageAmount() + 1);
                }
                //计划明细分包
                for (int i = 0; i < containerAmount; i++) {
                    JSONObject object = new JSONObject();
                    object.put("SupplierID", "string," + list.getSupplierID());
                    object.put("PartCode", "string," + list.getPartCode());
                    object.put("JHOutWareHouseID", "string," + jhOutWareHouseID);
                    object.put("PackingNumber", "int," + (i + 1));

                    if (i == containerAmount - 1 && !isFull) {
                        int packAmount = list.getJhCKAmount() - (containerInfo.getOutboundPackageAmount() * i);
                        object.put("PackingAmount", "int," + packAmount);
                    } else {
                        object.put("PackingAmount", "int," + containerInfo.getOutboundPackageAmount());
                    }
                    params.add(object);
//                    System.out.println("json params:" + object.toJSONString());
                }
            }
            return commonController.proceduceForUpdate("tbAddJHPartitionPackageInfo", params, opt.getConnect());
        }
        return null;
    }
}
