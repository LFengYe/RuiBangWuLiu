package com.cn.bean.app;

public class FunctionItem {

	private String title;
	private String viewType;
	private String imageName;
	private String actionName;
	private int unFinishedNum;
	
	public FunctionItem() {
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public int getUnFinishedNum() {
		return unFinishedNum;
	}

	public void setUnFinishedNum(int unFinishedNum) {
		this.unFinishedNum = unFinishedNum;
	}
}
