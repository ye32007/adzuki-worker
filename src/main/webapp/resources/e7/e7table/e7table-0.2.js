(function($){
	/***
	 * Description: JS表格控件<br>
	 * e7table控件初始化和控件方法调用
	 * 初始化： $('#tableId').e7table({...}); 			//{...}对象请参照 $.fn.e7table.defaults
	 * 方法调用：$('#tableId').e7table('options')		//获取e7table控件参数
	 *       $('#tableId').e7table('query', 'formId')	//根据表单，查询
	 *       $('#tableId').e7table('reload')			//重新加载远程数据
	 *       $('#tableId').e7table('loadData', data)	//加载本地数据，data格式{rows:[...],page:{total:98,...}}
	 *       $('#tableId').e7table('getChecked')		//返回checkbox选中的所有行记录数据
	 *       $('#tableId').e7table('clearChecked')		//清除checkbox选中状态
	 */
	$.fn.e7table = function(options, param){
		var target = this[0];
		if (typeof options == 'string') {
			switch(options){
				case 'options':
					return this.data('e7table').options;
					
				case 'getRows':
					return this.data('e7table').data.rows;

				case 'query':
					return query(target, param);
					
				case 'reload':
					return request(target, param);
						
				case 'loadData':
					return loadLocalData(target, param);
					
				//废弃：参见 getSelectedRow，getSelectedRows
				case 'getChecked':
					return getCheckedRows(target);
				//废弃：参见 unselectAll
				case 'clearChecked':
					return clearChecked(target, param);
				//废弃：参见 selectById，selectByIndex
				case 'checkRow':
					return checkRow(target, param);
				//废弃：外部循环-参见 selectById，selectByIndex
				case 'checkRows':
					return checkRows(target, param);
					
					
				case 'getSelectedRow':
					return getSelectedRow(target);
					
				case 'getSelectedRows':
					return getSelectedRows(target);
					
				case 'selectByIndex':
					if(param instanceof Array) {
						for(var i=0; i<param.length; i++) {
							selectByIndex(target, param[i]);
						}
					} else {
						selectByIndex(target, param);
					}
					return ;

				case 'selectById':
					if(param instanceof Array) {
						for(var i=0; i<param.length; i++) {
							selectById(target, param[i]);
						}
					} else {
						selectById(target, param);
					}
					return ;
					
				case 'selectAll':
					return selectAll(target);
					
				case 'unselectAll':
					return unselectAll(target);
			}
		}
		
		options = options || {};
		
		//如果之前初始化过，会在原有options的基础上合并覆盖，重新初始化
		var state = $.data(target, 'e7table');
		var opts;
		if (state) {
			opts = $.extend(state.options, options);
			state.options = opts;
		} else {
			$(target).addClass('e7table');
			opts = $.extend({}, $.fn.e7table.defaults, options);
			
			if (opts.checkbox) {
				opts.selectType = 'multiple';
			}
			
			$.data(target, 'e7table', {
				options: opts,
				data: {total: 0, rows: []},
				checkedRows: []
			});
		}
		
		if (opts.columns){
			$('<div id="' + this[0].id + '_loading" class="e7table_loading" style="display: none;"></div>').insertBefore(this);
			var thead = createTHeader(target);
			this.html('');
			this.append(thead);
			if(opts.striped) {
				$(target).addClass('table-striped');
			}
		}
		
		if(opts.wordWrap) {
			$(target).addClass('e7table_wordWrap');
		}

		if (opts.autoLoading && opts.url) {
			$('tbody', target).remove();
			var tbody = createEmptyTbody(target);
			$(target).append(tbody);
			
			request(target);
		}
		
		if(!$(target).data('init')) {
			$(target).data('init', true);
			$(window).resize(function() {
				clearTimeout($.fn.e7table.resizeEvent[target.id]);
				$.fn.e7table.resizeEvent[target.id] = setTimeout(function(){
					resize(target);
				}, 200);
			});
		}
	};
	
	$.fn.e7table.resizeEvent = {};

	/**
	 * 默认国际化为中文
	 */
	$.fn.e7table.i18n = {
		rownumbersTitle: '序号',
		errorMsgTitle: '错误消息',
		loadDataFail: '数据加载失败!',
		noData: '没有匹配的记录',
		pageInfo: '第${pageNum}页 / 共${pageTitle}页 ，共${dataTitle}条记录',
		pageInfoSimple: '${pageNum}/${pageTitle}页,共${dataTitle}条',
		firstPage: '首页',
		lastPage: '尾页',
		nextPage: '下页',
		previousPage: '上页'
	}
	
	/**
	 * 控件默认选项
	 * columns元素选项如下：
	 * 	{
	 *    width: 100,			//列的宽度。如果没有定义，宽度将自动扩充以适应其内容。
	 *    title: '用户名',			//列标题文本
	 *    filed: 'username',	//列字段名称
	 *    align: 'center',		//指明如何对齐列数据。可以使用的值有：'left','right','center'。
	 *    formatter: function(value, row, index)		//单元格格式化函数，带3个参数，value：字段值；row：行记录数据；index: 行索引。
	 *    styler：function(value, row, index)			//单元格样式函数，带3个参数，value：字段值；row：行记录数据；index: 行索引。
	 * 	}
	 */
	$.fn.e7table.defaults = {
		_local: false,	//本地加载数据
		_localData: null,		//本地数据
		columns: [],			//列数组
		idField: 'id',			//主键字段名
		method: 'post',			//该方法类型请求远程数据
		url: null,				//一个URL从远程站点请求数据
		pagination: true,		//如果为true，则在e7table控件底部显示分页工具栏
		paginationConcise: false,	//是否为简单风格的分页工具栏
		rownumbers: true,		//如果为true，则显示一个行号列
		striped: false,			//奇偶行不同背景
		checkbox: false,		//废弃：如果为true，则显示一个复选框列，与singleSelect只能有一个为true
		selectType:'multiple',		//multiple:多选,以checkbox形式展示; single:单选; none:不能选中
		pageNum: 1,				//当前页码
		pageSize: 10,			//每页显示条数
		queryForm: null,		//查询条件表单ID
		queryParams: {},		//在请求远程数据的时候发送额外的参数。
		sortName: null,			//排序列名
		sortOrder: 'asc',		//排序方向
		align:'center',			//默认水平居中对齐
		timeout:30000,			//请求超时时间，默认30秒
		autoLoading: false,		//如果为true，控件初始化后自动请求远程数据	
		authFailErrorCode: -1,		//未登录时返回的错误代码
		rowStyler: function(){},	//行样式:function(row){return {color:'red', backgroupColor:'#f08'};}
		
		onLoadSuccess: function(data){},	//远程数据请求成功事件:function(data){...};
		onLoadError: function(data){
			try { 
				if(typeof bootbox == 'undefined') {
					alert($.fn.e7table.i18n.loadDataFail);
				} else {
					bootbox.alert({'title':$.fn.e7table.i18n.errorMsgTitle, 'message':$.fn.e7table.i18n.loadDataFail});
				}
			}catch (e) {
				alert($.fn.e7table.i18n.loadDataFail);
			}
		},		//远程数据请求失败事件
		onLoadComplete: function(data){},
		onLoadBeforeSend: function(data){},
		onSelect: function(index, row){},
		onUnselect : function(index, row){},
		onClick : function(index, row){},
		onDblclick : function(index, row){}
	};
	
	/***
	 * 创建表头信息
	 */
	function createTHeader(target){
		var opts = $.data(target, 'e7table').options;
		var columns = opts.columns;
		
		var thead = $('<thead></thead>');
		var tr = $('<tr></tr>').appendTo(thead);
		
		if(opts.selectType == 'multiple') {
			var th = $('<th></th>').appendTo(tr);
			th.css({width: 20, textAlign: 'center', verticalAlign:'middle'});
			var ckbox = $('<input type="checkbox"/>').appendTo(th);
			ckbox.attr('_index', '-1');
			ckbox.attr('_for', target.id);
			ckbox.click(checkBoxClick);
		}
		
		if(opts.rownumbers) {
			var th = $('<th>' + $.fn.e7table.i18n.rownumbersTitle + '</th>').appendTo(tr);
			th.css({width: 60, textAlign: 'center', verticalAlign:'middle'});
		}
		
		for(var i=0; i<columns.length; i++) {
			var col = columns[i];

			var th = $('<th></th>').appendTo(tr);
			th.css({verticalAlign:'middle'});
			
			if(col.headerStyler){
				var css = col.headerStyler();
				if(css && css['class']) {
					th.addClass(css['class']);
					delete css['class'];
				}
				if(css){
					th.css(css);
				}
			}
			

			th.append(col.title ? col.title : '&nbsp;');
			
			if(col.field) th.attr('_field', col.field);
			if(col.width) th.css({width : col.width});
			if(col.align) {
				th.css({textAlign : col.align});
			} else if(opts.align) {
				th.css({textAlign : opts.align});
			}
			if(col.sortable) {
				th.addClass('e7table_sortable');
				
				if(opts.sortName == col.field) {
					if(opts.sortOrder.toUpperCase() == 'ASC'){
						th.addClass('e7table_sortable_asc');
					} else {
						th.addClass('e7table_sortable_desc');
					}
				}
				
				th.data('col', {target: target, field: col.field});
				th.click(sort);
			}
			
		}
		
		return thead;
	}
	
	function sort() {
		var col =  $(this).data('col');
		var opts = $.data(col.target, 'e7table').options;

		if(col.field == opts.sortName){
			if(opts.sortOrder.toUpperCase() == 'ASC'){
				opts.sortOrder = 'desc';
			} else {
				opts.sortOrder = 'asc';
			}
		} else {
			opts.sortName = col.field;
			opts.sortOrder = 'asc';
		}
		
		var columns = opts.columns;
		for(var i=0; i<columns.length; i++) {
			var c = columns[i];
			if(c.sortable) {
				th = $($('#'+col.target.id).find('[_field="' + c.field +'"]')[0]);
				th.removeClass('e7table_sortable_asc');
				th.removeClass('e7table_sortable_desc');
				
				if(opts.sortName == c.field) {
					if(opts.sortOrder.toUpperCase() == 'ASC'){
						th.addClass('e7table_sortable_asc');
					} else {
						th.addClass('e7table_sortable_desc');
					}
				}
			}
		}
		
		if(opts._local) {
			sortLocalData(col.target);
		} else {
			request(col.target);
		}
	}
	
	function createEmptyTbody(target, info) {
		var opts = $.data(target, 'e7table').options;
		var columnCount = opts.columns.length;
		if(opts.selectType == 'multiple') columnCount++;
		if(opts.rownumbers) columnCount++;
		
		var tbody = $('<tbody></tbody>');
		var tr = $('<tr></tr>').appendTo(tbody);
		var td = $('<td style="height:40px;" COLSPAN="' + columnCount + '"></td>').appendTo(tr);
		if(info) {
			td.html(info);
		} else{
			td.html('&nbsp;');
		}
		td.css({textAlign: 'center'});
		
		return tbody;
	}
	
	/***
	 * 创建表体
	 */
	function createTbody(target, rows){
		var opts = $.data(target, 'e7table').options;
		var columns = opts.columns;
		
		var ellipsis = false;
		var tbody = $('<tbody></tbody>');
		for(var r=0; r<rows.length; r++) {
			var row = rows[r];
			var tr = $('<tr></tr>').appendTo(tbody);
			if(opts.selectType == 'multiple' || opts.selectType == 'single') {
				tr.attr('_index', r);
				tr.attr('_for', target.id);
				tr.attr('_id', row[opts.idField]);
				tr.click(function(e){
					rowClick(this, e);

					var index = $(this).attr('_index');	
					var target = $('#' + $(this).attr('_for'))[0];
					var rows = $('#'+target.id).e7table('getRows');	
					
					opts.onClick(index, rows[index]);
				});		
				tr.dblclick(function(e){
					rowDblclick(this, e);
					
					var index = $(this).attr('_index');	
					var target = $('#' + $(this).attr('_for'))[0];
					var rows = $('#'+target.id).e7table('getRows');	
					
					opts.onDblclick(index, rows[index]);
				});				
			}
			
			if(opts.rowStyler){
				var rowCss = opts.rowStyler(row);
				if(row && row['class']) {
					tr.addClass(row['class']);
					row['class'] = undefined;
				}
				if(rowCss) tr.css(rowCss);
			}
			
			if (opts.selectType == 'multiple'){
				var td = $('<td></td>').appendTo(tr);
				td.css({textAlign: 'center'});
				var ckbox = $('<input type="checkbox"/>').appendTo(td);
				ckbox.attr('_index', r);
				ckbox.attr('_for', target.id);
				ckbox.attr('_id', row[opts.idField]);
				ckbox.click(checkBoxClick);
			}
			
			if(opts.rownumbers) {
				var rowNumber = (opts.pageNum - 1) * opts.pageSize + 1 + r;
				var th = $('<th>' + rowNumber + '</th>').appendTo(tr);
				th.css({textAlign: 'center'});
			}
			
			for(var i=0; i<columns.length; i++) {
				var col = columns[i];
				
				var fieldStrut = col.field ? col.field.split('.') : [null];
				var fieldValue = row[fieldStrut[0]];
				for(var j=1; j<fieldStrut.length; j++){
					if(!fieldValue){
						fieldValue = null;
						break;
					}
					
					fieldValue = fieldValue[fieldStrut[j]];
				}
				
				var td = $('<td></td>').appendTo(tr);
				
				if(col.styler){
					var css = col.styler(fieldValue, row, r);
					if(css && css['class']) {
						td.addClass(css['class']);
						delete css['class'];
					}
					if(css){
						td.css(css);
					}
				}

				if(col.formatter) {
					fieldValue = col.formatter(fieldValue, row, r);
				} else if(fieldValue){
					var p = $('<p></p>');
					p.text(fieldValue);
					fieldValue = p.html();
				}
				
				if(col.ellipsis) {
					ellipsis = true;
					td.addClass('e7table_td_ellipsis');
					if(fieldValue != null){
						td.attr('title', fieldValue);
					}
				}
				
				if(fieldValue != null){
					td.append(fieldValue);
				} else {
					td.append('&nbsp;');
				}
				
				if(col.field) td.attr('_field', col.field);
				if(col.width) td.css({width : col.width});
				if(col.align) {
					td.css({textAlign : col.align});
				} else if(opts.align) {
					td.css({textAlign : opts.align});
				}
				if(col.sortable) td.css({cursor : 'pointer'});
			}
		}
		
		if(ellipsis) {
			$(target).addClass('e7table_ellipsis');
		}
		
		return tbody;
	}
	
	function createPagination(target, data) {
		var opts = $.data(target, 'e7table').options;
		var pageCount = Math.ceil(data.total/opts.pageSize);
		if(pageCount == 0) pageCount = 1;
		
		var showStartPageNo;
		var showEndPageNo;
		
		if(pageCount <= 5) {
			showStartPageNo = 1;
			showEndPageNo = pageCount;
		} else if(opts.pageNum < 3) {
			showStartPageNo = 1;
			showEndPageNo = 5;
		} else {
			showEndPageNo = pageCount - opts.pageNum >= 2 ? (opts.pageNum + 2) : pageCount;
			showStartPageNo = showEndPageNo - 4;
		}

		var row = $('<div _for="' + target.id + '" class="e7table_page box-footer clearfix"></div>');
		
		if(opts.paginationConcise) {
			row.append($('<span class="e7table_page_total">' 
					+ $.fn.e7table.i18n.pageInfoSimple.replace('${pageNum}', opts.pageNum).replace('${pageTitle}', pageCount).replace('${dataTitle}', data.total)
					+ '</span>'));
			var ul = $('<ul class="e7table_page_button pagination"></ul>').appendTo(row);
			
			ul.append(createPaginationButton(target, '|<', 1));
			ul.append(createPaginationButton(target, '<', opts.pageNum > 1 ? (opts.pageNum - 1) : 1));
			
			for(var i = showStartPageNo; i <= showEndPageNo; i++) {
				ul.append(createPaginationButton(target, i, i));
			}
			
			ul.append(createPaginationButton(target, '>', opts.pageNum < pageCount ? (opts.pageNum + 1) : pageCount));
			ul.append(createPaginationButton(target, '>|', pageCount));
		} else {
			row.append($('<span class="e7table_page_total">' 
					+ $.fn.e7table.i18n.pageInfo.replace('${pageNum}', opts.pageNum).replace('${pageTitle}', pageCount).replace('${dataTitle}', data.total)
					+ '</span>'));
			var ul = $('<ul class="e7table_page_button pagination"></ul>').appendTo(row);
			
			ul.append(createPaginationButton(target, $.fn.e7table.i18n.firstPage, 1));
			ul.append(createPaginationButton(target, '← ' + $.fn.e7table.i18n.previousPage, opts.pageNum > 1 ? (opts.pageNum - 1) : 1));
			
			for(var i = showStartPageNo; i <= showEndPageNo; i++) {
				ul.append(createPaginationButton(target, i, i));
			}
			
			ul.append(createPaginationButton(target, $.fn.e7table.i18n.nextPage + ' →', opts.pageNum < pageCount ? (opts.pageNum + 1) : pageCount));
			ul.append(createPaginationButton(target, $.fn.e7table.i18n.lastPage, pageCount));
		}
		
		return row;
	}

	/***
	 * 创建分页栏上的按钮
	 */
	function createPaginationButton(target, title, pageNumber) {
		var opts = $.data(target, 'e7table').options;
		var li = $('<li></li>');
		
		var button ;
		if(pageNumber == opts.pageNum) {
			if(title == pageNumber) {
				li.addClass('active');
				button = $('<span class="current"></span>');
			} else {
				li.addClass('disabled');
				button = $('<a class="disabled" href="javascript:void(0)"></a>');
			}
			button.html(title);
		} else {
			button = $('<a class="" href="javascript:void(0)"></a>');
			button.html(title);
			button.click(function(e) {
				opts.pageNum = pageNumber;
				if(opts._local) {
					loadLocalData(target, opts._localData);
				} else {
					request(target);
				}
			});
		}
		li.append(button);
		return li;
	}
	
	/***
	 * 异步请求查询
	 */
	function request(target, callback) {
		var opts = $.data(target, 'e7table').options;
		
		if (!opts.url) return;
		
		var param = $.extend({'ajax': true}, opts.queryParams);
		if (opts.pagination){
			$.extend(param, {
				pageNum: opts.pageNum,
				pageSize: opts.pageSize
			});
		}
		if (opts.sortName){
			$.extend(param, {
				sortName: opts.sortName,
				sortOrder: opts.sortOrder
			});
		}

		if(typeof E7 == 'undefined' || typeof E7.post == 'undefined') {
			$('#' + target.id + '_loading').show();
			$.ajax({
				type: opts.method,
				url: opts.url,
				data: param,
				dataType: 'json',
				timeout: opts.timeout,
				success: function(result){
					$('#' + target.id + '_loading').hide();
					if(result.code == opts.authFailErrorCode) {
						window.location = result.data;
					} else if(result.code == '0') {
						loadData(target, result.data);
						if (opts.onLoadSuccess){
							opts.onLoadSuccess(result);
						}
					} else {
						if(typeof bootbox == 'undefined'){
							alert(result.msg);
						} else {
							bootbox.alert({'title':$.fn.e7table.i18n.errorMsgTitle, 'message':result.msg});
						}
					}
					
					try{
						callback();
					}catch(e){}
				},
				error: function(result){	
					$('#' + target.id + '_loading').hide();
					
					$('tbody', target).remove();
					var tbody = createEmptyTbody(target, '<code>' + $.fn.e7table.i18n.loadDataFail + '</code>');
					$(target).append(tbody);
					
					opts.onLoadError(result);
				},
				complete : function(result){
					$('#' + target.id + '_loading').hide();
					opts.onLoadComplete(result);
				},
				beforeSend : function(data){
					opts.onLoadBeforeSend(data);
				}
			});
		} else {
			E7.ajax({
				'url': opts.url,
				'data': param,
				'successCallback': function(result){
					loadData(target, result.data);
					if (opts.onLoadSuccess){
						opts.onLoadSuccess(result);
					}
				},
				'errorCallback': function(result){
					$('#' + target.id + '_loading').hide();
					$('tbody', target).remove();
					var tbody = createEmptyTbody(target, '<code>' + $.fn.e7table.i18n.loadDataFail + '</code>');
					$(target).append(tbody);
					
					opts.onLoadError(result);
				}
			});
		}
	}
	
	/**
	 * 更据表单，查询
	 */
	function query(target, param) {
		var opts = $.data(target, 'e7table').options;
		if(typeof param == 'string') {
			var formId = param.indexOf("#") == 0 ? param : ('#' + param);
			var formItems = $(formId).serializeArray();
			opts.queryParams = {};
			opts.pageNum = 1;
			for(var i=0; i<formItems.length; i++) {
				var item = formItems[i];
				if(item.value != '') {
					opts.queryParams[item.name] = item.value;
				}
			}
		} else if(typeof param == 'object'){
			opts.pageNum = 1;
			opts.queryParams = param;
		}
		
		request(target);
	}
	
	/**
	 * 加载数据
	 */
	function loadData(target, data){
		var opts = $.data(target, 'e7table').options;
		
		var pageData = null
		if(data instanceof Array) {
			pageData = {'rows':data, 'total':data.length};
		} else {
			pageData = data;
		}
		
		$.data(target, 'e7table', {
			options: opts,
			data: pageData,
			checkedRows: []
		});
		
		var rows = pageData.rows;
		
		$('tbody', target).remove();
		var tbody = '';
		if(rows.length > 0) {
			tbody = createTbody(target, rows);
		} else {
			tbody = createEmptyTbody(target, $.fn.e7table.i18n.noData);
		}
		$(target).append(tbody);
		
		if(opts.pagination) {
			$('[_for=' + target.id + '].e7table_page').remove();
			$(target).parent().append(createPagination(target, data));
			resize(target);
		}

		if(opts.selectType == 'multiple') {
			clearChecked(target);
			
			$('#' + target.id + ' button').click(function(e){
				e.stopPropagation();
			});
			$('#' + target.id + ' a').click(function(e){
				e.stopPropagation();
			});
		}
		
	}
	
	/***
	 * 加载本地数据
	 */
	function loadLocalData(target, param){
		var opts = $.data(target, 'e7table').options;
		opts._local = true;
		if(param instanceof Array) {
			opts._localData = {'rows':param, 'total':param.length};
		} else {
			opts._localData = param;
		}
		
		var data = null;
		if(opts.pagination) {
			var startIndex = (opts.pageNum-1) * opts.pageSize;
			var endIndex = startIndex + opts.pageSize;
			if(endIndex > opts._localData.total) {
				endIndex = opts._localData.total;
			}
			
			var rows = [];
			for(var i=startIndex; i<endIndex; i++) {
				rows.push(opts._localData.rows[i]);
			}
			data = {'rows':rows, 'total':opts._localData.total};
		} else {
			data = opts._localData;
		}
		
		loadData(target, data);
		
		opts.onLoadSuccess(opts._localData);
		opts.onLoadComplete(opts._localData);
	}
	
	function sortLocalData(target){
		var opts = $.data(target, 'e7table').options;
		var rows = opts._localData.rows;
		
		rows.sort(function(a, b){
			if(opts.sortOrder.toUpperCase() == 'ASC') {
				return a[opts.sortName] > b[opts.sortName];
			} else {
				return b[opts.sortName] >= a[opts.sortName];
			}
		});
		
		loadLocalData(target, opts._localData);
	}
	
	/**
	 * checkbox点击事件
	 */
	function checkBoxClick(event){
		var target = $('#' + $(this).attr('_for'))[0];
		var index = $(this).attr('_index');
		
		var checked = $(this).prop('checked');
		var opts = $('#'+target.id).e7table('options');
		var rows = $('#'+target.id).e7table('getRows');
		
		if(index == '-1') {
			var cbxList = $('[_for="' + target.id + '"][_index!="-1"]:checkbox');
			for(var i=0; i<cbxList.length; i++) {
				if($(cbxList[i]).prop('checked') != checked){
					if(checked){
						selectByIndex(target, i)
					} else {
						unselectByIndex(target, i)
					}
				}
			}
		} else {			
			if(checked){
				selectByIndex(target, index)
			} else {
				unselectByIndex(target, index)
			}
		}
		
		event.stopPropagation();
	}
	
	/**
	 * 单选行点击事件
	 */
	function rowClick(elm, evt){
		var target = $('#' + $(elm).attr('_for'))[0];
		var index = $(elm).attr('_index');	
		var opts = $('#'+target.id).e7table('options');
		
		var selected = $(elm).hasClass('e7table_selected');
		
		//单选模式		
		if(opts.selectType == 'single') {
			if(!selected) {
				var selectedTrs = $('#'+target.id).find('tr.e7table_selected');
				for(var i=0; i<selectedTrs.length; i++) {
					unselectByIndex(target, $(selectedTrs[i]).attr('_index'));
				}
				
				selectByIndex(target, index);
			}			
		} 
		//多选模式
		if(opts.selectType == 'multiple') {
        	if(selected) {
    			unselectByIndex(target, index);
        	} else {
        		selectByIndex(target, index);
        	}
		}		
	}
	
	function rowDblclick(elm, evt){
		var target = $('#' + $(elm).attr('_for'))[0];
		var index = $(elm).attr('_index');	
		var opts = $('#'+target.id).e7table('options');
		
		var selected = $(elm).hasClass('e7table_selected');

		//如果双击的行是未选中状态，
		if(!selected) {
			//如果是单选，取消之前选中的行
			if(opts.selectType == 'single') {
				var selectedTrs = $('#'+target.id).find('tr.e7table_selected');
				for(var i=0; i<selectedTrs.length; i++) {
					unselectByIndex(target, $(selectedTrs[i]).attr('_index'));
				}
			}
			
        	selectByIndex(target, index);
		}		
	}

	

	/**
	 * 根据数组索引取消选中
	 */
	function unselectByIndex(target, index) {
		var opts = $('#'+target.id).e7table('options');
		var rows = $('#'+target.id).e7table('getRows');
		
		var tr = $('[_for="' + target.id + '"][_index="' + index + '"]tr');
		var checkbox = $('[_for="' + target.id + '"][_index="' + index + '"]:checkbox');
		
		if(tr.hasClass('e7table_selected')){
			tr.removeClass('e7table_selected')
			if(opts.selectType == 'multiple') {
				checkbox.prop('checked', false);
				processHeadCheckbox(target);
			}
			if(opts.onUnselect){
				opts.onUnselect(index, rows[index]);
			}
		}	
	}
	
	/**
	 * 根据ID取消选中
	 */
	function unselectById(target, id) {
		var opts = $('#'+target.id).e7table('options');
		var rows = $('#'+target.id).e7table('getRows');
		for(var i=0; i<rows.length; i++) {
			if(rows[i][opts.idField] == id) {
				unselectByIndex(target, i);
				return;
			}
		}
		
	}
	
	/**
	 * 根据数组索引选中数据
	 */
	function selectByIndex(target, index) {
		var opts = $('#'+target.id).e7table('options');
		var rows = $('#'+target.id).e7table('getRows');
		
		var tr = $('[_for="' + target.id + '"][_index="' + index + '"]tr');
		var checkbox = $('[_for="' + target.id + '"][_index="' + index + '"]:checkbox');
		
		if(!tr.hasClass('e7table_selected')){
			tr.addClass('e7table_selected')
			if(opts.selectType == 'multiple') {
				checkbox.prop('checked', true);
				processHeadCheckbox(target);
			}
			if(opts.onSelect){
				opts.onSelect(index, rows[index]);
			}
		}
	}
	
	/**
	 * 根据ID选中数据
	 */
	function selectById(target, id) {
		var opts = $('#'+target.id).e7table('options');
		var rows = $('#'+target.id).e7table('getRows');
		for(var i=0; i<rows.length; i++) {
			if(rows[i][opts.idField] == id) {
				selectByIndex(target, i);
				return;
			}
		}
	}
	
	/**
	 * 选中所有行
	 */
	function selectAll(target) {
		var opts = $('#'+target.id).e7table('options');

		$('[_for="' + target.id + '"]tr').addClass('e7table_selected');
		$('[_for="' + target.id + '"]:checkbox').prop('checked', true);	
	}
	
	/**
	 * 取消选中所有行
	 */
	function unselectAll(target) {
		var opts = $('#'+target.id).e7table('options');
		
		$('[_for="' + target.id + '"]tr.e7table_selected').removeClass('e7table_selected');
		$('[_for="' + target.id + '"]:checkbox').prop('checked', false);		
	}
	
	/**
	 * 获取选中的第一条数据
	 */
	function getSelectedRow(target) {
		var selectedRows = getSelectedRows(target);
		if(selectedRows.length > 0) {
			return selectedRows[0];
		}
	}
	
	/**
	 * 获取选中数据列表
	 */
	function getSelectedRows(target) {
		var rows = $('#'+target.id).e7table('getRows');
		
		var selectedRows = [];
		$('[_for="' + target.id + '"]tr.e7table_selected').each(function() {
			selectedRows.push(rows[$(this).attr('_index')]);
		});
		return selectedRows;
	}
	
	/**
	 * 处理行头checkbox的选中状态
	 */
	function processHeadCheckbox(target) {
		var checkboxCount = $('[_for="' + target.id + '"][_index!="-1"]:checkbox').length;
		var checkedCount = $('[_for="' + target.id + '"][_index!="-1"]:checked').length;
		$('[_for="' + target.id + '"][_index="-1"]:checkbox').prop('checked', checkboxCount == checkedCount);
	}
	
	
	function resize(target){
		var totalEl = $('.e7table_page[_for="' + target.id + '"] .e7table_page_total');
		var buttonEl = $('.e7table_page[_for="' + target.id + '"] .e7table_page_button');
		
		var width = $('.e7table_page[_for="' + target.id + '"]').width();
		var pageTotalWidth = totalEl.width();
		var pageButtonWidth = buttonEl.width();
		
		if((pageTotalWidth + pageButtonWidth) >= width) {
			totalEl.addClass('e7table_page_total_min');
			buttonEl.addClass('e7table_page_button_min');
		} else {
			totalEl.removeClass('e7table_page_total_min');
			buttonEl.removeClass('e7table_page_button_min');
		}
	}
	
	
	
	
	
	/***
	 * 已废弃，选中id列表里的的所有行
	 */
	function checkRows(target, idArray) {	
		var opts = $('#'+target.id).e7table('options');
		var rows = $('#'+target.id).e7table('getRows');
		
		for(var i=0; i<rows.length; i++) {
			var id = rows[i][idField];
			for(var j=0; j<idArray.length; j++){
				if(id == idArray[j]){
					selectByIndex(target, i);
					break;
				}
			}
		}
		
	}
	
	
	/**
	 * 已废弃，返回checkbox选中的所有数据行
	 */
	function getCheckedRows(target){		
		return getSelectedRows(target);
	}
	
	/**
	 * 已废弃，清除checkbox选中状态
	 */
	function clearChecked(target) {
		unselectAll(target);
	}
	
	/**
	 * 已废弃
	 */
	function checkRow(target, index) {
		selectByIndex(target, index);
	}

})(jQuery);