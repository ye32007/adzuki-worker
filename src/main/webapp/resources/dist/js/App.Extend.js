$.AdminLTE.options = $.extend($.AdminLTE.options, {
	sidebarExpandOnHover:true
});

var FullScreen = {
	init: function(){	    
		$(".fullscreen").bind("click", function() {
		    if (FullScreen.isFull()) {
		    	FullScreen.exitFull();
		    	if($('body.sidebar-collapse').length > 0 ){
		    		$('a[data-toggle="offcanvas"]')[0].click();
		    	}
		    	if($("#tabPanel").is(":hidden")) {
		    		$('a[data-toggle="offtabs"]')[0].click();
		    	}
		    } else {
		    	FullScreen.enterFull();
		    	if($('body.sidebar-collapse').length == 0 ){
		    		$('a[data-toggle="offcanvas"]')[0].click();
		    	}
		    	if(!$("#tabPanel").is(":hidden")) {
		    		$('a[data-toggle="offtabs"]')[0].click();
		    	}
		    }
		});
	},
	isFull: function(){
		if(FullScreen.fullScrollHeight && FullScreen.fullScrollWidth 
				&& document.body.scrollHeight == FullScreen.fullScrollHeight  
				&& document.body.scrollWidth == FullScreen.fullScrollWidth) {
			return true;
		}
		
		return (document.body.scrollHeight == window.screen.height && document.body.scrollWidth == window.screen.width); 
	},
	enterFull: function () {
        var de = document.documentElement;

        if (de.requestFullscreen) {
            de.requestFullscreen();
        } else if (de.mozRequestFullScreen) {
            de.mozRequestFullScreen();
        } else if (de.webkitRequestFullScreen) {
            de.webkitRequestFullScreen();
        } else if (de.msRequestFullscreen) {
            de.msRequestFullscreen();
        }
        else {
            // App.alert({ message: "该浏览器不支持全屏！", type: "danger" });
            alert("当前浏览器不支持全屏！");
        }
        
        setTimeout(function(){
            FullScreen.fullScrollHeight = document.body.scrollHeight;
            FullScreen.fullScrollWidth = document.body.scrollWidth;
        }, 100);
    },
    exitFull: function() {
        if(document.exitFullscreen) {
            document.exitFullscreen();
        } else if(document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if(document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
        }
    }
}

var Menu = {
		init : function(data, CTX){
			if(!(data instanceof Array) || data.length == 0) {
				$('ul.sidebar-menu').html('');
				return;
			}
			
			if(CTX == '/') {
				CTX='';
			}
			
			var root = $('ul.sidebar-menu');
			root.html('');
			
			for (var i = 0; i < data.length; i++) {
				var item = data[i];
				if(item.folder) {
					var folder = $('<li class="treeview"><a href="#"><i class="fa ' + item['icon'] + '"></i><span>' + item['name'] + '</span><span class="pull-right-container icon"></span></a></li>');
					Menu.createNode(item.childrens, folder, CTX);
					root.append(folder);
				} else {
					root.append($('<li><a href="#" data-id="' + item['id'] + '" data-url="' + CTX + item['url'] + '"><i class="fa ' + item['icon'] + '"></i> ' + item['name'] + '</a></li>'));
				}
			}
			
			root.find('a[data-url]').click(function() {
				var id = $(this).data("id");
				var title = $(this).text();
				var url = $(this).data("url");

				//var tab = {'id' : id + '', 'title' : title, 'url' : url};
				var tab = {'title' : title, 'url' : url};
				$('#tabPanel').e7tabs('add', tab);
			});
			
			$('ul.sidebar-menu').click(function(){
				setTimeout(function(){
					$(this).height($(this).height()-1);
					$(this).height($(this).height()+1);
				}, 400);
			});
		},
		createNode : function(data, parentNode, CTX){
			if(!(data instanceof Array) || data.length == 0) {
				return '' ;
			}
			
			var nodes = $('<ul class="treeview-menu"></ul>');
			
			for(var i = 0; i < data.length; i++) {
				var item = data[i];
				if(item.folder) {
					var folder = $('<li><a href="#"><i class="fa ' + item['icon'] + '"></i><span>' + item['name'] + '</span><span class="pull-right-container icon"></span></a></li>');
					Menu.createNode(item.childrens, folder, CTX)
					nodes.append(folder);
				} else {
					nodes.append($('<li><a href="#" data-id="' + item['id'] + '" data-url="' + CTX + item['url'] + '"><i class="fa ' + item['icon'] + '"></i> ' + item['name'] + '</a></li>'));
				}
			}
			
			parentNode.append(nodes);
		}
	}


$(function(){
	// 初始化全屏按钮
	FullScreen.init(); 

	$('a[data-toggle="offtabs"]').bind("click", function(){
		$('#tabPanel').e7tabs('titleToggle');
	});
//
//	$(window).resize(function(){
//		setTimeout(layoutResize, 100);
//	});
//	
//	layoutResize();
//	function layoutResize() {
//		var headHeight = $('header.main-header').height();
//		var tabHeight = $('#tabPanel').height();
//		if($("#tabPanel").is(":hidden")){
//			tabHeight = 0;
//		}
//		
//		$('div.wrapper').height($(window).height()-1);
//		$('.main-sidebar').height($(window).height() - headHeight);
//		$('.tab-content iframe').height( $(window).height() - headHeight - tabHeight - 1);
//	}
});

//document.onkeydown = function(e) {
//	alert(e);
//}

/*修复IE11 bug*/
//(function () {
//    function CustomEvent(event, params) {
//        params = params || { bubbles: false, cancelable: false, detail: undefined };
//        var evt = document.createEvent('CustomEvent');
//        evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
//        return evt;
//    };
//
//    CustomEvent.prototype = window.Event.prototype;
//    window.CustomEvent = CustomEvent;
//})();