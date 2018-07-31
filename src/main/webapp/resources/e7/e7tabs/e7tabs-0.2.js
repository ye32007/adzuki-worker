(function($){
	/***
	 * 标签控件初始化和方法调用
	 */
	$.fn.e7tabs = function(options, param){
		var target = this[0];
		if (typeof options == 'string') {
			switch(options){
				case 'options':
					return this.data('e7tabs').options;

				case 'getTabById':
					return getTabById(target, param);

				case 'getTabByTitle':
					return getTabByTitle(target, param);
					
				case 'getActiveTab':
					return getActiveTab(target);
					
				case 'add':
					return addTab(target, param);

				case 'close':
					return closeTab(target, param);
						
				case 'closeAll':
					return closeAll(target);
						
				case 'closeOther':
					return closeOther(target, param);
					
				case 'refresh':
					return refresh(target, param);
				
				case 'showScroll':
					return showScroll(target, param);
					
				case 'openInNewWindow':
					return openInNewWindow(target, param);
				
				case 'titleToggle':
					return titleToggle(target, param);
					
				default :
					return;
			}
		}
		
		options = options || {};
		
		//如果之前初始化过，会在原有options的基础上合并覆盖，重新初始化
		var state = $.data(target, 'e7tabs');
		var opts;
		if (state) {
			opts = $.extend(state.options, options);
			state.options = opts;
		} else {
			opts = $.extend({}, $.fn.e7tabs.defaults, options);
			
			$.data(target, 'e7tabs', {
				options: opts
			});
		}
		
		var divContentId = target.id + '_content';
		var content = $('#' + divContentId);
		if(content.length == 0) {
			content = $('<div class="tab-content" id="' + divContentId + '"></div>');
			$(target).after(content);
		}
		
		if (opts.tabs && opts.tabs.length > 0){
			for(var i=0; i<opts.tabs.length; i++) {
				addTab(target, opts.tabs[0], true);
			}
		}
		
		$("body").click(function(e){
			$('#' + target.id + '_menu').hide();
		});
		
		$(window).resize(function() {
			clearTimeout($.fn.e7tabs.resizeEvent[target.id]);
			$.fn.e7tabs.resizeEvent[target.id] = setTimeout(function(){
				resize(target);
			}, 200);
		});
	};
	
	$.fn.e7tabs.resizeEvent = {};
	
	/**
	 * 默认国际化为中文
	 */
	$.fn.e7tabs.i18n = {
		warmPrompt: '温馨提示',
		overMaxTabsMsg: '最多只允许打开${maxTabs}个页面！',
		refresh: '刷新',
		closeSelf: '关闭',
		closeOthers: '关闭其他',
		closeAll: '关闭全部',
		openInNewWindow: '新窗口打开',
		cancel: '取消'
	}

	/***
	 * 标签控件默认选项
	 */
	$.fn.e7tabs.defaults = {
		tabs: [],		
		contextMenus: [],
		maxTabs: 10,
		openInNewWindow: false,
		isIframe: true
	};
	
	/***
	 * 标签控件默认选项
	 */
	$.fn.e7tabs.tabDefaults = {
		id: '',		
		title: '',
		url: '',
		closable: true,
		checkMax: true,
		replace: false
	};
	
	/***
	 * 添加标签
	 */
	function addTab(target, tab, init){
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		var divContentId = target.id + '_content';
		tab = $.extend({},$.fn.e7tabs.tabDefaults, tab);
		
		if(!tab || !tab.title || !tab.url) return;
		
		if(!init) {
			for(var i=0; i<tabs.length; i++) {
				var tabExist = false;
				if(tab.id != undefined && tab.id != null && tab.id.trim() != ''){
					if(tab.id == tabs[i].id){
						tabExist = true;
					}
				}else if(tabs[i].title == tab.title) {
					tabExist = true;
				}
				
				if(tabExist){
					$('#' + target.id + ' .active').removeClass("active");
					$('#' + divContentId + ' .active').removeClass("active");
					$('#t_' + tabs[i].id).addClass("active");
					$('#c_' + tabs[i].id).addClass("active");

					if(tab.replace) {
						tabs[i].title = tab.title;
						tabs[i].url = tab.url;
						tabs[i].closable = tab.closable;
						tabs[i].checkMax = tab.checkMax;
						tabs[i].replace = tab.replace;
						tabs[i]._iframe.attr('src', tabs[i].url);
					}
					return;
				}
			}
			
			if(tab.checkMax && tabs.length >= opts.maxTabs) {
				try { 
					bootbox.alert({'title': $.fn.e7tabs.i18n.warmPrompt, 'message': $.fn.e7tabs.i18n.overMaxTabsMsg.replace('${maxTabs}', opts.maxTabs)});
				}catch (e) { 
					alert('最多只允许打开' + opts.maxTabs + '个页面！');
				}
				return;
			}
		}
		
		//没有id，生成uuid
		if(tab.id.trim() == '') tab.id = Math.uuid();
		
		$('#' + target.id + ' .active').removeClass("active");
		$('#' + divContentId + ' .active').removeClass("active");
		
		//Tab头
		var tabTitle;
		if(tab.closable) {
			tabTitle = $('<a href="#c_' + tab.id +'" role="tab" data-toggle="tab" onclick="$(\'#' + target.id + '\').e7tabs(\'showScroll\',\'' + tab.id + '\')"></a>').html(tab.title + ' <i class="glyphicon glyphicon-remove" style="cursor: pointer;" tabclose="' + tab.id + '" onclick="$(\'#' + target.id + '\').e7tabs(\'close\',\'' + tab.id + '\')"></i>');
		} else {
			tabTitle = $('<a href="#c_' + tab.id +'" role="tab" data-toggle="tab" onclick="$(\'#' + target.id + '\').e7tabs(\'showScroll\',\'' + tab.id + '\')"></a>').text(tab.title);
		}
		var liTab = $('<li id="t_' + tab.id + '" role="presentation" class="active" tabs_id="' + target.id + '"></li>').append(tabTitle);
		liTab.bind('contextmenu',contextMenu);
		$('#' + target.id).append(liTab);
		
		//Tab体
		var tabContent = $('<div id="c_' + tab.id +'" role="tabpanel" class="tab-pane active" tabs_id="' + target.id + '" style="' + (isIPhone()? '-webkit-overflow-scrolling:touch; overflow-y:scroll;z-index:1':'')+ '"></div>');
		var iframe = $('<iframe id="i_' + tab.id +'" tabs_id="' + target.id + '" src="' + tab.url + '" width="100%" height="100%" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="auto" allowtransparency="yes"></iframe>');
		tabContent.append(iframe);
		$('#' + divContentId).append(tabContent);
		
		tab._iframe = iframe;
		if(!init) tabs.push(tab);
		
		resize(target);
	}
	
	/**
	 * 根据id获取Tab
	 */
	function getTabById(target, id){
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		for(var i=0; i<tabs.length; i++) {
			if(tabs[i].id == id) {
				return tabs[i];
			}
		}
		
		return null;
	}
	
	/**
	 * 根据title获取tab
	 */
	function getTabByTitle(target, title){
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		for(var i=0; i<tabs.length; i++) {
			if(tabs[i].title == title) {
				return tabs[i];
			}
		}
		
		return null;
	}
	
	function getActiveTab(target){
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		for(var i=0; i<tabs.length; i++) {
			var active = $('#t_' + tabs[i].id).hasClass('active');
			if(active){
				return tabs[i];
			}
		}
		
		return null;
	}
	
	/**
	 * 关闭tab
	 */
	function closeTab(target, id) {
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		
		for(var i=0; i<tabs.length; i++) {
			if(tabs[i].id == id) {
				var active = $('#t_' + id).hasClass('active');
				$('#t_' + id).remove();
				$('#c_' + id).remove();
				tabs.splice(i, 1);
				if(active && tabs.length > 0) {
					var lastTab = tabs[tabs.length - 1];
					$('#t_' + lastTab.id).addClass("active");
					$('#c_' + lastTab.id).addClass("active");
					showScroll(target, lastTab.id);
				}
				break;
			}
		}

		resize(target);
	}
	
	/**
	 * 关闭所有
	 */
	function closeAll(target) {
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		
		for(var i=tabs.length-1; i>=0; i--) {
			if(tabs[i].closable) {
				closeTab(target, tabs[i].id);
			}
		}
	}
	
	/**
	 * 关闭其他
	 */
	function closeOther(target, id) {
		var opts = $.data(target, 'e7tabs').options;
		var tabs = opts.tabs;
		
		for(var i=tabs.length-1; i>=0; i--) {
			if(id != tabs[i].id && tabs[i].closable) {
				closeTab(target, tabs[i].id);
			}
		}
		select(target, id);
	}
	
	/**
	 * 刷新
	 */
	function refresh(target, id) {
		select(target, id);
		$('#i_' + id).attr('src', $('#i_' + id).attr('src'));
	}
	
	/**
	 * 解决chrome下tab切换时iframe滚动条不显示问题 
	 */
	function showScroll(target, id) {
		var iframeId = '#i_' + id;
		setTimeout(function(){
			$(iframeId).height($(iframeId).height()-1);
			$(iframeId).height($(iframeId).height()+1);
		}, 100);
	}
	
	/** 
	 * 新窗口打开 
	 */
	function openInNewWindow(target, id) {
		$('<a href="' + $('#i_' + id).attr('src') + '" target="_blank"></a>')[0].click();
	}
	
	/** 
	 * 选中Tab 
	 */
	function select(target, id){
		$('[tabs_id="' + target.id + '"].active').removeClass("active");
		$('#t_' + id).addClass("active");
		$('#c_' + id).addClass("active");
	}
	
	/** 
	 * 构建右键菜单 
	 */
	function contextMenu(e) {
		if(e.which != 3) return;
		e.stopPropagation();
		
		var targetId = $(this).attr('tabs_id');
		var opts = $.data($('#' + targetId)[0], 'e7tabs').options;
		var tabs = opts.tabs;
		
		var tabMenuId = targetId + '_menu';
		$('#' + tabMenuId).remove();

		var currentTabId = $(this).attr('id').substr(2);
		var currentTab = null;
		for(var i=0; i<tabs.length; i++) {
			if(currentTabId == tabs[i].id) {
				currentTab = tabs[i];
				break;
			}
		}

		var tabMenu = $('<ul id="' + targetId + '_menu' + '" class="dropdown-menu" role="menu" aria-labelledby="' + 'c_' + currentTabId + '" style="position: absolute; z-index:9999; left: '+ e.clientX +'px; top: '+ e.clientY +'px;"></ul>');
		tabMenu.append($('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:$(\'#' + targetId + '\').e7tabs(\'refresh\',\''+ currentTab.id +'\');">' + $.fn.e7tabs.i18n.refresh + '</a></li>'));
		if(currentTab.closable){
			tabMenu.append($('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:$(\'#' + targetId + '\').e7tabs(\'close\',\''+ currentTab.id +'\');">' + $.fn.e7tabs.i18n.closeSelf + '</a></li>'));
		}
		tabMenu.append($('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:$(\'#' + targetId + '\').e7tabs(\'closeOther\',\''+ currentTab.id +'\');">' + $.fn.e7tabs.i18n.closeOthers + '</a></li>'));
		tabMenu.append($('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:$(\'#' + targetId + '\').e7tabs(\'closeAll\');">' + $.fn.e7tabs.i18n.closeAll + '</a></li>'));
		tabMenu.append($('<li class="divider"></li>'));
		if(opts.openInNewWindow) {
			tabMenu.append($('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:$(\'#' + tabMenuId + '\').e7tabs(\'openInNewWindow\',\''+ currentTab.id +'\');">' + $.fn.e7tabs.i18n.openInNewWindow + '</a></li>'));
		}
		tabMenu.append($('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:$(\'#' + tabMenuId + '\').remove();">' + $.fn.e7tabs.i18n.cancel + '</a></li>'));
		$('body').append(tabMenu);

		tabMenu.show();
		e.preventDefault();
		return false;
	}
	
	function isIPhone(){
		var u = navigator.userAgent;
		return u.indexOf('iPhone') > -1;
	}
	
	function resize(target) {
		$('iframe[tabs_id="' + target.id + '"]').height(0);
		var height =  $('#' + target.id).parent().height()-6;
		if(!$('#' + target.id).is(":hidden")) {
    		height -=  $('#' + target.id).height();
    	}
		$('iframe[tabs_id="' + target.id + '"]').height(height);
	}
	
	function titleToggle(target, param) {
		 $('#' + target.id).toggle();
		 resize(target);
	}
	
})(jQuery);

var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
Math.uuid = function(len, radix) {
	var chars = CHARS, uuid = [], i;
	radix = radix || chars.length;
	if (len) {
		for (i = 0; i < len; i++)
			uuid[i] = chars[0 | Math.random() * radix];
	} else {
		var r;
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';
		for (i = 0; i < 36; i++) {
			if (!uuid[i]) {
				r = 0 | Math.random() * 16;
				uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
			}
		}
	}
	return uuid.join('');
};