(function($){
	var _default = {
		titles : [],                                           //表头  
		datas:[],                                              //数据源
		unique:[],                                             //表的元组主码
		
		//maxInPage:4,                                           //一页的数据行数
		clickRowCallBack:function(index,obj){console.log(index)},
		dbclickRowCallBack:function(index,obj){console.log(obj)},
		
		isLocalSearch:true
		
	}
	
	var _funs_ = {
		getDOM :function(){
			

			
			this.html('<div class="wrapper"><div class="jtb-header"><input placeholder="查询条件" /><button class="LocalFilter">查询</button></div><div class="jtb-container"></div></div>');
            
			//加载表头
			var result = '', txt = '', width = '',totalWidth=0;
			for (var i in this.titles) {
				txt = this.titles[i].split(',')[0];
				width = this.titles[i].split(',')[1];
				totalWidth+=parseInt(width);
				result += '<div class="col" name="'+i+'" style="width:'+width+';"><h5>'+txt+'</h5><div class="inner"><ul></ul></div></div>';
			}
			this.$container = this.find(".jtb-container").append(result);
			this.$container.width(totalWidth+2);
			
			
			this.addClass("jtb");
			!this.isLocalSearch && this.$container.prev().hide();
		    
		
			
		    return this;
		},
		getTableDataDOM:function(datas){
			this.$container.find("ul").html("");
            var obj = null;
		    for(var i=0;i<datas.length;i++){
		    	obj = datas[i];
		    	for (var k in obj) {
		    		  this.$container.children("div[name='"+k+"']").find("ul").append("<li>"+obj[k]+"</li>");
		    	}	
		    }
		    return this;
		},
		bindEvt:function(){
			var that = this;
			this.$container.find("ul").on("click","li",function(e){
				var index = $(this).index();
				console.log(that.filterState);
				that.filterState && that.clickRowCallBack(index,that.afterFilter[index]);
				!that.filterState && that.clickRowCallBack(index,that.datas[index]);
				that.$container.find("ul li:nth-child("+(index+1)+")").toggleClass("clicked");
			}).on("dblclick","li",function(e){
				var index = $(this).index();
				that.filterState && that.dbclickRowCallBack(index,that.afterFilter[index]);
				!that.filterState && that.dbclickRowCallBack(index,that.datas[index]);
				that.$container.find("ul li:nth-child("+(index+1)+")").toggleClass("dbclicked");
			});
			this.find(".jtb-header button").click(function(e){
				var keyWord  = $(this).prev().val();
				_funs_.dataFilter.call(that,keyWord);
			}).prev().focus(function(e){
				_funs_.getTableDataDOM.call(that,that.datas);
				that.filterState = false;
			});
		},
		dataFilter:function(data){
        	 this.afterFilter =[];
        	 var str = '';                  //保证元组不重复
             var that = this;
             	this.$container.find('li:contains("'+data+'")').each(function(){ 
	        	 	var eq = $(this).index();
             		if(str.indexOf(eq)<0){
             			 str+=eq;
	        	 	     that.afterFilter.push([eq,that.datas[eq]]);
             		}
	        	   
        	 });   	 
             this.$container.find("ul").html('');
             var arr = this.afterFilter.map(function(it){
             	return it[1];
             })
        	 _funs_.getTableDataDOM.call(this,arr);
        	 this.filterState = true;        //表示数据筛选状态
        	return this; 
       }
	};
	
	var _interface = {
		add:function(index,obj){
			
			if(this.filterState){
				var eq = this.afterFilter[index][0];
				this.datas.splice(0,0,obj);
				this.filterState = false;
			}else{
				this.datas.splice(index,0,obj);
			}
				_funs_.getTableDataDOM.call(this,this.datas);
		},
		del:function(index){
			if(this.filterState){
				var eq = this.afterFilter[index][0];
				this.datas.splice(eq,1);
				this.filterState = false;
			}else{
				this.datas.splice(index,1);
			}
				_funs_.getTableDataDOM.call(this,this.datas);
		},
		del2:function(arr){   		//删除多个   
            var index,set = this.datas;  
			this.datas = [];
			for(var i=0;i<arr.length;i++){
				index = arr[i];
				set[index] = null;
				
			}
			for(var j=0;j<set.length;j++){
				
				set[j] && this.datas.push(set[j]);
			}
			this.render(this.datas);
		},
		update:function(index,obj){
			
			if(this.filterState){
				var eq = this.afterFilter[index][0];
				this.datas.splice(eq,1,obj);
				this.filterState = false;
			}else{
				this.datas.splice(index,1,obj);
			}
				_funs_.getTableDataDOM.call(this,this.datas);
		},
		render:function(data){
			this.datas = data;
			_funs_.getTableDataDOM.call(this,this.datas);
		},
		emptyTable:function(){
			this.datas = [];
			_funs_.getTableDataDOM.call(this,this.datas);
		},
		getAllDatas:function(){
			return this.datas;
		},
		isUnique:function(obj){
			for(var e =0;e < this.unique.length;e++){
				var key = this.unique[e];
				for(var i =0;i<this.datas.length;i++){
					if(this.datas[i][key] == obj[key]){
						alert(key+"不能重复");
						return false;
					}
				}
			}
		  return true;
		}
	};
	$.fn.insertTable = function(options){
		
	$.extend(this,_default,options);
	   _funs_.getDOM.call(this);
	   _funs_.getTableDataDOM.call(this,this.datas);
	   _funs_.bindEvt.call(this);
	$.extend(this,_interface);
	return this;
   }
})(window.jQuery);
