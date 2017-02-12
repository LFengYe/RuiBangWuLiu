(function(){
	var $inputBox = $(".page1-container .page1-input-box");
	var $buttonArea = $(".page1-container .page1-button-area");
	var $tableBox = $(".page1-container .page1-show-table");
	var $dateQueryBtn =$(".wc-page1-form .page1-query");
	var $dateInputs = $(".wc-page1-form input.wc-control");
	
	var OPERATION = {
		CREATE:"create",
		REQUEST_TABLE:"request_table",
		QUERY_ITEM:"query_item",
		SUBMIT:"submit"
	};
	var submitDatas = {
		 update:[],
		 del:[]
	};
	var modifyRow = null;
	var cancelSet = {};
	
	function initDOM(){
		    ajaxData(OPERATION.CREATE,{},function(data){
		    	$inputBox.insertInputForm({
				controls: data.control,
				mustWrite: data.mustwrite,
				requesFun: function(data,callback){  //事件选择框  数据加载 的 接口
					ajaxData(OPERATION.REQUEST_TABLE,data,function(data){
						callback(data);
					},function(){});
				},   
				selectpanel: sp  //选择框对象
			});
			$tableBox.insertTable({
				titles: getLength(data.control),
				datas: data.datas,
				unique: data.unique,
				dbclickRowCallBack:function(index,obj){
					$inputBox.objInInputs(obj);
					modifyRow = index;
				},
				clickRowCallBack:function(index,obj){
					if(cancelSet[index]){
						cancelSet[index] = null;
					}else{
						cancelSet[index] = obj;
					}
					//console.log(cancelSet);
				}
			});
		    },function(){});			
    } 
function bindEvt(){
	
	   
		$("#page1-add").on("click",function(e){
			if($inputBox.isFinishForm()&& (!modifyRow)){
				
				 var obj = $inputBox.getInputValObj();
				 if(!$tableBox.isUnique(obj)){
					return false;
				 }
				$tableBox.add(0,obj);
		         cancelSet = {};
				$inputBox.clearInputsArea();
			}  
		});
		$("#page1-modify").on("click",function(e){
		     if(modifyRow >=0&&$inputBox.isFinishForm()){
				var obj = $inputBox.getInputValObj();
				$tableBox.update(modifyRow,obj);
		
				modifyRow = null;
				$inputBox.clearInputsArea();
			}  
		});
		$("#page1-cancel").on("click",function(e){
				var arr = [];
		    for(var index in cancelSet){
			
				if(cancelSet[index]){
					arr.push(index);
			        submitDatas.del.push(cancelSet[index]);
				}
			}
			
			$tableBox.del2(arr);
			
		});
		$("#page1-query").on("click",function(e){ 
		  
		    var data = $inputBox.getInputValObj();
		    ajaxData(OPERATION.QUERY_ITEM,{datas:data},function(data){
				$tableBox.render(data);
			},function(){
				alert("未查询到任何结果");
			});
		});
		$("#page1-submit").on("click",function(e){
			 submitDatas.update= $tableBox.getAllDatas();
			
			if(submitDatas.del.length==0 && submitDatas.update.length==0){
				alert("您当前没有新增任何信息");
				return;
			}
		     ajaxData(OPERATION.SUBMIT,submitDatas,function(data){alert("提交成功");},function(){});
		});
	}

//	ajax(module,url,operation,data,successCallBack,failCallBack)
ajaxPage1 = function(){
	initDOM();
    bindEvt();
}	
})();

//点一个菜单选项   显示对应的page  
//          根据对应的URL   module请求数据     初始化页面
//          绑定事件