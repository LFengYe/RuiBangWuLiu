$(function(){
	//侧边栏的滑动
	var $collepse = $("#fold");
	var $menu = $(".wc-content .wc-menu ");
	var fold = true;
	$collepse.on("click",function(e){
		if(fold){
			$menu.animate({left:"15px"},1000);
			fold = false;
		}else{
			$menu.animate({left:"-100%"},1000);
			fold = true;
		}
		
	});
});