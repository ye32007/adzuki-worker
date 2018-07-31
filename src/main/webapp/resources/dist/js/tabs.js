var addTabs = function (id,title,url,close) {
    $(".active").removeClass("active");
    //如果TAB不存在，创建一个新的TAB
    if (!$("#tab_" + id)[0]) {
        //固定TAB中IFRAME高度
    	//var h = $(".main-sidebar").height() >= $(".content-wrapper").height() ? $(".main-sidebar").height() : $(".content-wrapper").height();
    	//var mainHeight = $(".main-sidebar").height() - 15;
    	var mainHeight = $(window).height() - 100;
        //创建新TAB的title
        title = '<li role="presentation" id="tab_' + id + '" class="tab tab_' + id + '"><a href="#' + id + '" aria-controls="' + id + '" role="tab" data-toggle="tab">' + title;
       /* if(true){
        	title += ' <i class="glyphicon glyphicon-refresh" tabclose="' + id + '" onclick="refreshTab('+id+')"></i>';
        }*/
        //是否允许关闭
        if (close) {
            title += ' <i class="glyphicon glyphicon-remove" tabclose="' + id + '" onclick="closeTab('+id+')"></i>';
        }
        title += '</a></li>';
        //加入TABS
        $(".nav-tabs").append(title);
        $(".tab-content").append('<div role="tabpanel" class="tab-pane" id="tabpanel_' + id + '"><iframe width="100%" height="' + mainHeight + '" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="auto" allowtransparency="yes"></iframe></div>');
        $("#tabpanel_"+id).find("iframe").attr("src",timestamp(url));
    }else{
    	if($("#indexTab li.tab_"+id).length == 0){
			$(".nav-tabs").append($("#tab_" + id));
			 $(".tab-content").append($("#tabpanel_" + id));
			 $(".active").removeClass("active");
			 addTabMenu($("#tab_" + id));
			 $("#tab_" + id).on("click", function () {
				var li = $(this);
				var id = $(li).attr("id").replace("tab_","");
				 $("div.active").removeClass("active");
				 $("#tabpanel_" + id).addClass("active");
		     });
		 }
    	 var href = $("#tabpanel_"+id).find("iframe").attr("src");
	   	 if(href != url){
	   		$("#tabpanel_"+id).find("iframe").attr("src",timestamp(url));
	   	 }else{
	   		 $("#tabpanel_" + id).find("iframe").attr("src",timestamp(href));
	   	 }
    }
    //激活TAB
    $(".active").removeClass("active");
    $("#tab_" + id).addClass('active');
    $("#tabpanel_" + id).addClass("active");
    addTabMenu($("#tab_" + id));
    $("#tab_" + id).on("click", function () {
		var li = $(this);
		var id = $(li).attr("id").replace("tab_","");
		 $("div.active").removeClass("active");
		 $("#tabpanel_" + id).addClass("active");
    });
};
 
var closeTab = function (id) {
    //如果关闭的是当前激活的TAB，激活他的前一个TAB
    if ($("li.active").attr('id') == "tab_" + id) {
        $("#tab_" + id).prev().addClass('active');
        $("#tabpanel_" + id).prev().addClass('active');
    }
    //关闭TAB
    $("#tab_" + id).remove();
    $("#tabpanel_" + id).remove();
    stopPropagation(window.event);
};

var addTabMenu = function(li){
	var id = $(li).attr("id").replace("tab_","");
	if(id == 600){//首页TAB
		$(li).contextMenu('tabMenuMenuFirst',{
			bindings: {
		        'refresh': function(t) {
		        	 $(".active").removeClass("active");
		        	 $(t).addClass('active');
		        	 var id = $(t).attr("id").replace("tab_","");
		        	 $("#tabpanel_" + id).addClass("active");
		        	 var url = $("#tabpanel_" + id).find("iframe").attr("src");
		        	 $("#tabpanel_" + id).find("iframe").attr("src", timestamp(url));
		        	 $("div#tabMenuMenu").hide();
		        },
		        'cancel': function(t) {
		        	 $("div#tabMenuMenu").hide();
		        }
		      }
		});
	}else{
		$(li).contextMenu('tabMenuMenu',{
			bindings: {
		        'refresh': function(t) {
		        	 $(".active").removeClass("active");
		        	 $(t).addClass('active');
		        	 var id = $(t).attr("id").replace("tab_","");
		        	 $("#tabpanel_" + id).addClass("active");
		        	 var href = $("#tabpanel_" + id).find("iframe").attr("src");
		        	 if(href && href.indexOf('/report/reportQuery.html') == -1&&href.indexOf('/Colligate/colligateDisplay.html') == -1){
		        		 var pos = href.indexOf("?");
			        	 if(pos > 0){
			        		 href = href.substring(0,pos);
			        	 } 
		        	 }
		        	
		        	 $("#tabpanel_" + id).find("iframe").attr("src",timestamp(href));
		        	 $("div#tabMenuMenu").hide();
		        },
		        'cancel': function(t) {
		        	 $("div#tabMenuMenu").hide();
		        },
		        'closeSelf':function(t){
		        	closeTab(id);
		        },
		        'closeAll':function(t){
		        	$(".nav-tabs li").each(function(){
		        		var ali = $(this);
		        		var cid = $(ali).attr("id");
		        		var cid = $(ali).attr("id");
		        		if(cid){
		        			cid = cid.replace("tab_","");
		        			if(cid != 600){
			        			closeTab(cid);
			        		}
		        		}
		        	});
		        },
		        'closeOther':function(t){
		        	$(".nav-tabs li").each(function(){
		        		var ali = $(this);
		        		var cid = $(ali).attr("id");
		        		if(cid){
		        			cid = cid.replace("tab_","");
		        			if(cid != 600 && id != cid){
			        			closeTab(cid);
			        		}
		        		}
		        	});
		        }
		      }
		});
	}
}
function refreshTab(id){
	 var url = $("#tabpanel_" + id).find("iframe").attr("src");
	 $("#tabpanel_" + id).find("iframe").attr("src", timestamp(url));
}
function stopPropagation(e) {  
    e = e || window.event;  
    if(e.stopPropagation) { //W3C阻止冒泡方法  
        e.stopPropagation();  
    } else {  
        e.cancelBubble = true; //IE阻止冒泡方法  
    }  
}