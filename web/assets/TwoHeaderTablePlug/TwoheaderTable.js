
(function($){
	
	var _default = {
		title0:{},
		title1:{},
		datas  : [],
		
		clickRowCallBack:function(index,obj){console.log(index)},
		dbclickRowCallBack:function(index,obj){console.log(obj)}
		
		
	}
	var _style = {
		lineHeight:33
		
	}
	
	var _funs_ = {
		getDOM :function(){			
			this.html('<div class="wrapper"><div class="tht-header"></div><div class="tht-container"></div></div>');
            
			//加载表头
			var title0 = this.title0;
		    var title0_str = '';
			var colspans, txt,width;
			this.arr = [];
			this.public_obj = {};
			for(var i in title0) {
				txt = title0[i].split(',')[0];
				colspans =parseInt(title0[i].split(',')[1]);
				width = title0[i].split(',')[2];
				if(colspans > 1) {
					for(var k=0;k<colspans;k++){
						this.arr.push(i);
					}
					title0_str += "<td style='width:"+width+"'  colspan='" + colspans + "'>" + txt + "</td>";
				} else {
					this.arr.push(i);
					this.public_obj[i] = true;
					title0_str += "<td style='width:"+width+"' class='cols'  rowspan='2'>" + txt + "</td>";
				}
			}
			title0_str = "<tr>" + title0_str + "</tr>";

			var title1_str = '',title1 = this.title1;
			for(var i in title1) {
				title1_str += "<td>" + title1[i] + "</td>";
			}
			title1_str = "<tr>" + title1_str + "</tr>";
			this.$container = this.find(".tht-container").append(title0_str + title1_str );
		
			this.addClass("tht");
		    console.log(this.public_obj);
		    
		    return this;
		},
		getTableDataDOM:function(datas){
		
			var data_str = '',
				result = '';
			
			for(var i = 0; i < datas.length; i++) {
				var k = 0;
				for(var j in datas[i]) {
					data_str += "<td index = "+i+" name="+this.arr[k]+">" + datas[i][j] + "</td>";
					k++;
				}
				result += "<tr>" + data_str + "</tr>";
				data_str = '';
			}
			this.$container.find("tr:gt(1)").remove();
			this.$container.append(result);
			_funs_.bindEvt.call(this);
		},
		bindEvt:function(){
			var that = this;
			this.$container.find("tr:gt(1)").children("td").dblclick(function(e){
				var name = $(this).attr("name");
				
				if(that.public_obj[name]){
					
				   return;	
				}
				var index = $(this).attr("index");
				
				var obj = that.datas[index];
			
				var obj2 = {};
				for(var i in  that.public_obj){
					obj2[i] = obj[i];
					
				}
				//console.log(obj[name]);
				obj2[name] = $(this).text();
				console.log(obj2);
				that.dbclickRowCallBack(index,obj2);
				
		});
		},
		getRowData:function(index){
			
		}
		
	};
	
	var _interface = {
		render:function(data){
			this.datas = data;
			_funs_.getTableDataDOM.call(this,this.datas);
		}
	};
	$.fn.insertTwoHeaderTable = function(options){
	$.extend(this,_default,options);
	   _funs_.getDOM.call(this);
	   _funs_.getTableDataDOM.call(this,this.datas);
	$.extend(this,_interface);
	return this;
   }
})(window.jQuery);
