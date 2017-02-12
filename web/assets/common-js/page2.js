
(function(){
	var $mainTableBox = $(".page2-container .page2-main-table .page2-show-table");
	var $mainInputBox = $(".page2-container .page2-detail-list .page2-main-input-box");
	var $childINputBox = $(".page2-container .page2-detail-list .page2-child-input-box");
	var $chidTableBox = $(".page2-container .page2-detail-list .page2-show-table");
	var $detailList = $(".page2-container .page2-detail-list");
	var $mainTable = $(".page2-container .page2-main-table");
	var $addItem = $(".page2-container .page2-addItem");
	var $pageTurn = $(".page2-container .page2-pagination li a");
	var $search = $(".page2-container .page2-query");
	var $modify =  $("#page2-modify");
	var $cancel = $("#page2-cancel");
	//var $search_on_keyword = $(".page2-container .page2-keyword-query button");
	var OPERATION = {
		CREATE:"create",
		REQUEST_TABLE:"request_table",
		REQUEST_DETAIL:"request_detail",
		REQUEST_PAGE:"request_page",
		QUERY_DATA:"query_data",
		SUBMIT:"submit"
	};
	
	$detailList.hide();
	
	var maxInpage = 20; 
	
	var dataType = "";       
	
	var pageIndex = 1;
	
	var request = {
		  startIndex:1,
		  endIndex:30,
		  dataType:"2013-3-7&2013-3-18"|"关键字"
	};
	
	var modifyRow = null;
	var oriObj = null;
	
	var cancelRows = {};
	
	function bindEvt(){
		$search.click(function(e){
			var $dateInputs = $(".page2-container .wc-page2-form input");
			var key =  $dateInputs.eq(0).val();
			var start = $dateInputs.eq(1).val();
			var end = $dateInputs.eq(2).val();
			if(start!=""&&end!=""){
				request.dataType = start+"&"+end;
			}else{
				request.dataType = '';
			}
			request.dataType += "|"+key; 		
			request.startIndex = 1;
			request.endIndex = maxInpage;
			
			ajaxData(OPERATION.REQUEST_PAGE,request,function(data){
					$mainTableBox.render(data);
				},function(){});
			
		});
		$pageTurn.click(function(e){
		  if($(this).hasClass("page2-left")){
			  if(pageIndex>=2){
				  pageIndex--; 
			  }
		  }else if($(this).hasClass("page2-right")){
			   pageIndex++;
		  }
		   request.startIndex = (pageIndex-1)*maxInpage+1;
		  request.endIndex = request.startIndex + maxInpage - 1;
		  ajaxData(OPERATION.REQUEST_PAGE,request,function(data){
			  $mainTableBox.render(data);
		  },function(){
			  pageIndex--;                  //请求不到数据时,将页数恢复原值
		  });
		});
		
		
		$addItem.click(function(){
			$detailList.fadeIn(500);
	        $mainTable.fadeOut(200);
	    });
        
		$("#page2-return").click(function(){
			$childINputBox.clearInputsArea();
			$mainInputBox.clearInputsArea();
			$chidTableBox.emptyTable();
			$mainInputBox.RemoveformDisable();
			$childINputBox.RemoveformDisable();
			$detailList.hide();
			$mainTable.show();
		    $("#page2-add").removeAttr("disabled");
			$modify.removeAttr("disabled");
	        $cancel.removeAttr("disabled");
	
        });
		
		$("#page2-add").click(function(){
			
			var arr = $chidTableBox.getAllDatas();
			if(arr.length == 0){
				console.log("请将表格添加数据后再提交");
				alert("请将表格添加数据后再提交！");
				return;	
			}
			if(!$mainInputBox.isFinishForm()){
				return;
			}
			var item = $mainInputBox.getInputValObj();
			var obj ={
				item:item,
				details:arr
			};
			ajaxData(OPERATION.SUBMIT,obj,function(data){
				alert("提交成功!");
				$("#page2-return").trigger("click");
			    $mainTableBox.add(0,item);
			},function(){
				alert("提交失败!");
			});
			
		//	console.log($mainInputBox.getInputValObj());
			
      });
		 $modify.on("click",function(e){
			 if(modifyRow >=0&&$childINputBox.isFinishForm()){
				 var obj = $childINputBox.getInputValObj();
				 $.extend(oriObj,obj);
				 console.log(oriObj);
				$chidTableBox.update(modifyRow,oriObj);
				
				modifyRow = null;
				$childINputBox.clearInputsArea();
				$childINputBox.RemoveformDisable();
			}  			
		 });
		 
		 $cancel.on("click",function(e){
			 console.log(cancelRows);
			 var arr = [];
		        for(var index in cancelRows){
			
				if(cancelRows[index]!=null){
					arr.push(cancelRows[index]);
				}
			}
			console.log(arr);
			$chidTableBox.del2(arr);
			 
		 });
	}
	
	function initDOM(){
	ajaxData(OPERATION.CREATE,{},function(data){
		$mainTableBox.insertTable({
				titles: data.titles1,
				datas: data.datas1,
				unique: data.unique1,
				isLocalSearch:false,
				dbclickRowCallBack:function(index,maps){
					$mainInputBox.objInInputs(maps);
					ajaxData(OPERATION.REQUEST_DETAIL,{datas:maps},function(data){
						 $("#page2-add").attr("disabled","disabled");
						 $modify.attr("disabled","disabled");
	                     $cancel.attr("disabled","disabled");
						 $chidTableBox.render(data);
						 $mainInputBox.formDisable();
					     $childINputBox.formDisable();
						 $detailList.show();
					      $mainTable.hide();
					},function(){});
				}				
			});
		$mainInputBox.insertInputForm({
				controls: data.control1,
				mustWrite: data.mustwrite1,
				requesFun: function(data,callback){  //事件选择框  数据加载 的 接口
					ajaxData(OPERATION.REQUEST_TABLE,data,function(data){
						callback(data);
					},function(){});
				}, 
				selectpanel: sp //选择框对象
			});
		$childINputBox.insertInputForm({
				controls: data.control2,
				mustWrite: data.mustwrite2,
				requesFun:function(data,callback){  //事件选择框  数据加载 的 接口
					ajaxData(OPERATION.REQUEST_TABLE,data,function(data){
						callback(data);
					},function(){});
				}, 
				selectpanel: sp, //选择框对象
				lastInputCallBack: function(){ 
				        /*
						if($childINputBox.isFinishForm()&&$mainInputBox.isFinishForm()){
						   var obj = $.extend($childINputBox.getInputValObj(),$mainInputBox.getInputValObj());
						   var obj2 = $childINputBox.getInputValObj();
						   ajaxData(OPERATION.QUERY_DATA,{datas:obj},function(data){
							for (var i=0;i<data.length;i++) {
								$.extend(data[i],obj2);
							}
							 
							  $chidTableBox.render(data);
						   },function(){});
					}	
					*/
				},
				tableInputCallBack:function(arr){
					       var obj2 = $childINputBox.getInputValObj();
						   for (var i=0;i<arr.length;i++) {
								$.extend(arr[i],obj2);
							}
							$chidTableBox.render(arr);
							console.log(arr);
				}
			});
		$chidTableBox.insertTable({
				titles: data.titles2,
//				datas: data.datas2,			
				unique: data.unique2,
				dbclickRowCallBack:function(index,obj){
					$childINputBox.objInInputs(obj);
					$childINputBox.formDisable(data.unique2);
					modifyRow = index;
					oriObj = obj;
				},
				clickRowCallBack:function(index,obj){
					if(cancelRows[index]){
						cancelRows[index] = null;
					}else{
						cancelRows[index] = index;
					}
					console.log(cancelRows);
					
				}
			});
			
			
	},function(){});
	
}
 ajaxPage2 = function(){
	 initDOM();
	 bindEvt();
	console.log(localStorage.getItem("module")+" "+localStorage.getItem("url"));
 } 


})();