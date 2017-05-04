package com.cn.test;

import com.cn.bean.AreaLedIPInfo;
import com.cn.controller.CommonController;
import com.cn.util.DatabaseOpt;
import com.cn.util.ExportExcel;
import com.cn.util.Units;
import com.listenvision.led;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LedControl {

    public static void main(String[] args) throws Exception {
        System.out.println(Boolean.valueOf("0"));
//        powerOnOffAllLed(0);
//        setAllLed(2, 1);
    }

    public static void setC01Plan() throws Exception {
        AreaLedIPInfo ledIPInfo = new AreaLedIPInfo();
        ledIPInfo.setIpAddress("192.168.10.46");
        ledIPInfo.setAddressCode("C-01");
        LedPlan plan = new LedPlan();
        plan.setSupplierName("四川泛华");
        plan.setSupplierID("2033");
        plan.setPartName("驾驶室线束总成");
        plan.setPartCode("4010010-KA04R-C4D0");
        plan.setInboundBatch("20170110122323");
        plan.setPlanNum("200");
        plan.setContainer("料架");
        plan.setContainerAmount("40");
        plan.setContainerBoxAmount("5");
        setLedPlan(ledIPInfo, plan, 1);
    }

    public static void setC01PartStauts() throws Exception {
        AreaLedIPInfo ledIPInfo = new AreaLedIPInfo();
        ledIPInfo.setIpAddress("192.168.10.46");
        ledIPInfo.setAddressCode("C-01");
        LedPartStatus partStatus = new LedPartStatus();
        partStatus.setPartStatus("禁用");
        partStatus.setSupplierName("成都天兴");
        partStatus.setSupplierID("2026");
        partStatus.setPartName("车速里程表传感器");
        partStatus.setPartCode("3820020-KD01");
        partStatus.setInboundBatch("20170410122323");
        partStatus.setRejectReason("产品不合格");
        setLedPartStatus(ledIPInfo, partStatus, 2);
    }

    /**
     * 设置所有显示屏(测试用)
     *
     * @param type 显示类型:1 -- 显示计划信息, 2 -- 显示区域号, 3 -- 显示部品状态
     * @param picType 图片类型: 1 -- PNG图片, 2 -- BMP图片
     * @throws Exception
     */
    public static void setAllLed(int type, int picType) throws Exception {
        CommonController controller = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", 999, 1, "AddressCode", 0, opt.getConnect());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            AreaLedIPInfo ledIPInfo = (AreaLedIPInfo) iterator.next();
            if (type == 2) {
                setLedAreaCode(ledIPInfo);
            }
            if (type == 1) {
                LedPlan plan = new LedPlan();
                plan.setSupplierName("四川泛华");
                plan.setSupplierID("2033");
                plan.setPartName("驾驶室线束总成");
                plan.setPartCode("4010010-KA04R-C4D0");
                plan.setInboundBatch("20170110122323");
                plan.setPlanNum("200");
                plan.setContainer("料架");
                plan.setContainerAmount("40");
                plan.setContainerBoxAmount("5");
                setLedPlan(ledIPInfo, plan, picType);
            }
            if (type == 3) {
                LedPartStatus partStatus = new LedPartStatus();
                partStatus.setPartStatus("禁用");
                partStatus.setSupplierName("成都天兴");
                partStatus.setSupplierID("2026");
                partStatus.setPartName("车速里程表传感器");
                partStatus.setPartCode("3820020-KD01");
                partStatus.setInboundBatch("20170410122323");
                partStatus.setRejectReason("产品不合格");
                setLedPartStatus(ledIPInfo, partStatus, picType);
            }
        }
    }

    /**
     * 设置LED显示屏显示区域号
     *
     * @param ledIPInfo
     */
    public static void setLedAreaCode(AreaLedIPInfo ledIPInfo) {
        int count = 0;
        while (true) {
            int hProgram;
            hProgram = led.CreateProgram(128, 32, 1);
            led.AddProgram(hProgram, 1, 0, 0);
            led.AddImageTextArea(hProgram, 1, 1, 0, 0, 128, 32, 0);
            led.AddSinglelineTextToImageTextArea(hProgram, 1, 1, 0, ledIPInfo.getAddressCode(), "宋体", 20, 0xff, 1, 0, 0, 0, 5, 1);
            led.SetBasicInfo(ledIPInfo.getIpAddress(), 1, 128, 32);
            led.SetOEDA(ledIPInfo.getIpAddress(), 0, 1);
            int result = led.NetWorkSend(ledIPInfo.getIpAddress(), hProgram);
            led.DeleteProgram(hProgram);
            if (result == 0 || count >= 5) {
                System.out.println(ledIPInfo.getAddressCode() + "设置完成!");
                break;
            }
            count++;
        }
    }

    /**
     * 设置LED显示计划信息
     *
     * @param ledIPInfo led显示屏信息
     * @param plan 计划信息
     * @param picType 图片类型: 1 -- PNG图片, 2 -- BMP图片
     * @throws Exception
     */
    public static void setLedPlan(AreaLedIPInfo ledIPInfo, LedPlan plan, int picType) throws Exception {
        String filePath = "exportFile/";
        String fileName = null;
        if (picType == 1) {
            fileName = createPlanPNG(plan, filePath);
        }
        if (picType == 2) {
            fileName = createPlanBMP(plan, filePath);
        }
        int count = 0;
        while (true) {
            int hProgram;
            hProgram = led.CreateProgram(544, 32, 1);
            led.AddProgram(hProgram, 1, 0, 0);
            led.AddImageTextArea(hProgram, 1, 1, 0, 0, 544, 32, 0);
            led.AddFileToImageTextArea(hProgram, 1, 1, fileName, 6, 5, 15);
            led.SetBasicInfo(ledIPInfo.getIpAddress(), 1, 544, 32);
            led.SetOEDA(ledIPInfo.getIpAddress(), 0, 1);
            int result = led.NetWorkSend(ledIPInfo.getIpAddress(), hProgram);
            led.DeleteProgram(hProgram);
            if (result == 0 || count >= 5) {
                System.out.println(ledIPInfo.getAddressCode() + "设置完成!");
                break;
            }
            count++;
        }
        File file = new File(fileName);
        if (!file.delete()) {
            System.out.println(fileName + "删除失败!");
        }
    }

    /**
     * 设置LED显示部品状态
     *
     * @param ledIPInfo led显示屏信息
     * @param partStatus 部品状态
     * @param picType 图片类型: 1 -- PNG图片, 2 -- BMP图片
     * @throws Exception
     */
    public static void setLedPartStatus(AreaLedIPInfo ledIPInfo, LedPartStatus partStatus, int picType) throws Exception {
        String filePath = "exportFile/";
        String fileName = null;
        if (picType == 1) {
            fileName = createPartStatusPNG(partStatus, filePath);
        }
        if (picType == 2) {
            fileName = createPartStatusBMP(partStatus, filePath);
        }
        int count = 0;
        while (true) {
            int hProgram;
            hProgram = led.CreateProgram(544, 32, 1);
            led.AddProgram(hProgram, 1, 0, 0);
            led.AddImageTextArea(hProgram, 1, 1, 0, 0, 544, 32, 0);
            led.AddFileToImageTextArea(hProgram, 1, 1, fileName, 6, 5, 15);
            led.SetBasicInfo(ledIPInfo.getIpAddress(), 1, 544, 32);
            led.SetOEDA(ledIPInfo.getIpAddress(), 0, 1);
            int result = led.NetWorkSend(ledIPInfo.getIpAddress(), hProgram);
            led.DeleteProgram(hProgram);
            if (result == 0 || count >= 5) {
                System.out.println(ledIPInfo.getAddressCode() + "设置完成!");
                break;
            }
            count++;
        }
        File file = new File(fileName);
        if (!file.delete()) {
            System.out.println(fileName + "删除失败!");
        }
    }

    /**
     * 开启关闭所有显示屏
     *
     * @param onOff 0 为开, 1 为关
     * @throws Exception
     */
    public static void powerOnOffAllLed(int onOff) throws Exception {
        CommonController controller = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", 999, 1, "AddressCode", 0, opt.getConnect());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            AreaLedIPInfo ledIPInfo = (AreaLedIPInfo) iterator.next();
            int count = 0;
            while (true) {
                int result = led.PowerOnOff(ledIPInfo.getIpAddress(), onOff);
                if (result == 0 || count >= 5) {
                    System.out.println(ledIPInfo.getAddressCode() + "设置完成!");
                    break;
                }
                count++;
            }
        }
    }

    /**
     * 将计划信息创建为png图片
     *
     * @param plan
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String createPlanPNG(LedPlan plan, String filePath) throws Exception {
        String[] headers = {plan.getSupplierName(), plan.getPartName(), "入库批次", "计划数量", "换装数量", "换装盛具"};

        LedPlanDisplay display = new LedPlanDisplay();
        display.setSupplierID(plan.getSupplierID());
        display.setPartCode(plan.getPartCode());
        display.setInboundBatch(plan.getInboundBatch());
        display.setPlanNum(plan.getPlanNum());
        display.setContainer(plan.getContainer());
        display.setContainerAmount(plan.getContainerAmount());
//        display.setContainerBoxAmount(plan.getContainerBoxAmount());
//        display.setAreaCode(plan.getAreaCode());
        ArrayList<LedPlanDisplay> datas = new ArrayList<>();
        datas.add(display);

        String nowTime = Units.getNowTimeNoSeparator();
        File file = Units.createNewFile(filePath, "temp.xls");
        OutputStream stream = new FileOutputStream(file);

        ExportExcel exportExcel = new ExportExcel();
        exportExcel.exportExcel("导出", headers, datas, stream, "yyyy-MM-dd HH:mm:ss");
        String pngFilePath = filePath + nowTime + ".png";
        int[] fromIndex = {0, 0};
        int[] toIndex = {1, 5};
        DrawFromExcel.drawExcelToPNG(file.getAbsolutePath(), pngFilePath, fromIndex, toIndex);
        return pngFilePath;
    }

    /**
     * 将计划信息创建为BMP图片
     *
     * @param plan
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String createPlanBMP(LedPlan plan, String filePath) throws Exception {
        String[] headers = {plan.getSupplierName(), plan.getPartName(), "入库批次", "计划数量", "换装数量", "换装盛具"};

        LedPlanDisplay display = new LedPlanDisplay();
        display.setSupplierID(plan.getSupplierID());
        display.setPartCode(plan.getPartCode());
        display.setInboundBatch(plan.getInboundBatch());
        display.setPlanNum(plan.getPlanNum());
        display.setContainer(plan.getContainer());
        display.setContainerAmount(plan.getContainerAmount());
//        display.setContainerBoxAmount(plan.getContainerBoxAmount());
//        display.setAreaCode(plan.getAreaCode());
        ArrayList<LedPlanDisplay> datas = new ArrayList<>();
        datas.add(display);

        String nowTime = Units.getNowTimeNoSeparator();
        File file = Units.createNewFile(filePath, "temp.xls");
        OutputStream stream = new FileOutputStream(file);

        ExportExcel exportExcel = new ExportExcel();
        exportExcel.exportExcel("导出", headers, datas, stream, "yyyy-MM-dd HH:mm:ss");
        String bmpFilePath = filePath + nowTime + ".bmp";
        int[] fromIndex = {0, 0};
        int[] toIndex = {1, 5};
        DrawFromExcel.drawExcelToBMP(file.getAbsolutePath(), bmpFilePath, fromIndex, toIndex);
        return bmpFilePath;
    }

    /**
     * 将部品状态信息创建成png图片
     *
     * @param partStatus
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String createPartStatusPNG(LedPartStatus partStatus, String filePath) throws Exception {
        String[] headers = {"状态", partStatus.getSupplierName(), partStatus.getPartName(), "入库批次", "禁用原因"};

        LedPartStatusDiaplay display = new LedPartStatusDiaplay();
        display.setPartStatus("禁用");
        display.setSupplierID(partStatus.getSupplierID());
        display.setPartCode(partStatus.getPartCode());
        display.setInboundBatch(partStatus.getInboundBatch());
        display.setRejectReason(partStatus.getRejectReason());
        ArrayList<LedPartStatusDiaplay> datas = new ArrayList<>();
        datas.add(display);

        String nowTime = Units.getNowTimeNoSeparator();
        File file = Units.createNewFile(filePath, "temp.xls");
        OutputStream stream = new FileOutputStream(file);

        ExportExcel exportExcel = new ExportExcel();
        exportExcel.exportExcel("导出", headers, datas, stream, "yyyy-MM-dd HH:mm:ss");
        String pngFilePath = filePath + nowTime + ".png";

        int[] fromIndex = {0, 0};
        int[] toIndex = {1, 4};
        DrawFromExcel.drawExcelToPNG(file.getAbsolutePath(), pngFilePath, fromIndex, toIndex);
        return pngFilePath;
    }

    /**
     * 将计划信息创建为bmp图片
     *
     * @param partStatus
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String createPartStatusBMP(LedPartStatus partStatus, String filePath) throws Exception {
        String[] headers = {"状态", partStatus.getSupplierName(), partStatus.getPartName(), "入库批次", "禁用原因"};

        LedPartStatusDiaplay display = new LedPartStatusDiaplay();
        display.setPartStatus("禁用");
        display.setSupplierID(partStatus.getSupplierID());
        display.setPartCode(partStatus.getPartCode());
        display.setInboundBatch(partStatus.getInboundBatch());
        display.setRejectReason(partStatus.getRejectReason());
        ArrayList<LedPartStatusDiaplay> datas = new ArrayList<>();
        datas.add(display);

        String nowTime = Units.getNowTimeNoSeparator();
        File file = Units.createNewFile(filePath, "temp.xls");
        OutputStream stream = new FileOutputStream(file);

        ExportExcel exportExcel = new ExportExcel();
        exportExcel.exportExcel("导出", headers, datas, stream, "yyyy-MM-dd HH:mm:ss");
        String bmpFilePath = filePath + nowTime + ".bmp";

        int[] fromIndex = {0, 0};
        int[] toIndex = {1, 4};
        DrawFromExcel.drawExcelToBMP(file.getAbsolutePath(), bmpFilePath, fromIndex, toIndex);
        return bmpFilePath;
    }
}
