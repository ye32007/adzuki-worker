function E7() {}

E7.i18n = {
	SUN: '日',
	MON: '一',
	TUE: '二',
	WED: '三',
	THU: '四',
	FRI: '五',
	SAT: '六',
	select: '-请选择-',
	errorMessage: '错误信息',
	serverRequestFailed: '服务器请求失败！'	
}


//***日期操作*****************************************************************************

//日期格式化
Date.prototype.format = function(format) {
	return E7.dateFormat(this, format);
}

/***
 * 获取对象类型
 * @return 	'string'、'number'、'date'、'boolean'、'array'、'object'、'function'、'null'、'undefined' 
 */
E7.getType = function(obj) {
	return Object.prototype.toString.call(obj).replace('[object ','').replace(']','').toLowerCase();
}

//字符串替换全部
E7.replaceAll = function(value, oldStr, newStr){
	if(E7.getType(value) !='string' || E7.getType(oldStr) !='string' || value=='' || oldStr=='') {
		return value;
	}
	var index = value.indexOf(oldStr);
	if(index < 0) {
		return value;
	}
	
	return value.substring(0, index) + newStr + E7.replaceAll(value.substr(index + oldStr.length), oldStr, newStr);
}

/***
 * 日期格式化
 * @val 日期（支持Date 和 number）
 * @format 日期格式（默认：'yyyy-MM-dd HH:mm:ss'）
 * @returns 字符串
 */
E7.dateFormat = function(val, format) {
	if(!val) return null;
	if(typeof val == 'number') val = new Date(val);
	if(!format) format = 'yyyy-MM-dd HH:mm:ss';
	
	var Week = [ E7.i18n.SUN, E7.i18n.MON, E7.i18n.TUE, E7.i18n.WED, E7.i18n.THU, E7.i18n.FRI, E7.i18n.SAT ];
	var str = format;
	str = str.replace(/yyyy|YYYY/, val.getFullYear());
	str = str.replace(/yy|YY/, (val.getYear() % 100) > 9 ? (val.getYear() % 100).toString() : '0' + (val.getYear() % 100));
	str = str.replace(/MM/, val.getMonth() > 8 ? (val.getMonth() + 1) : '0' + (val.getMonth() + 1));
	str = str.replace(/M/g, val.getMonth() + 1);
	str = str.replace(/w|W/g, Week[val.getDay()]);

	str = str.replace(/dd|DD/, val.getDate() > 9 ? val.getDate().toString() : '0' + val.getDate());
	str = str.replace(/d|D/g, val.getDate());

	str = str.replace(/hh|HH/, val.getHours() > 9 ? val.getHours().toString() : '0' + val.getHours());
	str = str.replace(/h|H/g, val.getHours());
	str = str.replace(/mm/, val.getMinutes() > 9 ? val.getMinutes().toString() : '0' + val.getMinutes());
	str = str.replace(/m/g, val.getMinutes());

	str = str.replace(/ss|SS/, val.getSeconds() > 9 ? val.getSeconds().toString() : '0' + val.getSeconds());
	str = str.replace(/s|S/g, val.getSeconds());

	return str;   
}

/***
 * 日期转换
 * @str 日期字符串
 * @format 日期格式（默认：'yyyy-MM-dd HH:mm:ss'）
 * @returns 日期对象
 */
E7.dateParse = function (str, format) {
	if(E7.isEmpty(str)) return '';
	if(!format) format = 'yyyy-MM-dd HH:mm:ss';
	var year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
	var start = -1, len = str.length;
	if ((start = format.indexOf('yyyy')) > -1 && start < len) { year = str.substr(start, 4); }
	if ((start = format.indexOf('MM')) > -1 && start < len) {month = parseInt(str.substr(start, 2)) - 1;	}
	if ((start = format.indexOf('dd')) > -1 && start < len) {day = parseInt(str.substr(start, 2));}
	if (((start = format.indexOf('HH')) > -1 || (start = format.indexOf('hh')) > 1)&& start < len) {hour = parseInt(str.substr(start, 2));}
	if ((start = format.indexOf('mm')) > -1 && start < len) {minute = str.substr(start, 2);}
	if ((start = format.indexOf('ss')) > -1 && start < len) {second = str.substr(start, 2);}
	
	return new Date(year, month, day, hour, minute, second);
}

//***数值操作*****************************************************************************
/**  
* 数值格式化  
* @param int or float number  		要格式化的数值
* @param int          precision		小数保留位数，四舍五入
* @param object       defaultValue	不是有效数值时的默认值,默认为""
* @param string       thousandsSep  千位分隔符,默认为","
* @param string       decPoint  	小数点，默认为"."
* @return 字符串  
*/  
E7.numberFormat = function(number, precision, defaultValue, thousandsSep, decPoint) {
	if(isNaN(parseFloat(number))){
		if(defaultValue == undefined ){
			return '';
		}else{
			return defaultValue;
		}
	}
	
	var n = !isFinite(+number) ? 0 : +number, 
		prec = !isFinite(+precision) ? 0 : Math.abs(precision), 
		sep = (typeof thousandsSep === 'undefined') ? ',' : thousandsSep, 
		dec = (typeof decPoint === 'undefined') ? '.' : decPoint, 
		s = '', 
		toFixedFix = function(n, prec) {
			var k = Math.pow(10, prec);
			return '' + Math.round(n * k) / k;
		};
	// Fix for IE parseFloat(0.55).toFixed(0) = 0;   
	s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
	if (s[0].length > 3) {
		s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
	}
	if ((s[1] || '').length < prec) {
		s[1] = s[1] || '';
		s[1] += new Array(prec - s[1].length + 1).join('0');
	}
	return s.join(dec);
}

/***
 * 字符串转数值
 * @param str 字符串
 * @param defaultValue 如果str无效时，返回的默认值
 * @returns 数值对象
 */
E7.numberParse = function (str, defaultValue) {
	if(E7.isNumber(str)) {
		return new Number(str);
	} else {
		return defaultValue == undefined ? null : defaultValue;
	}
}

/***
 * 数值上取整
 * @param val			数值或数值字符串
 * @param precision		保留小数点位数，例如：2保留2位小数，-2保留到百位；
 * @param defaultValue	数值无效时的默认值
 * @returns 数值
 */
E7.numberCeil = function(val, precision, defaultValue) {
	if(!E7.isNumber(val)) {		
		return defaultValue==undefined ? null : defaultValue;
	}

	precision = precision == undefined ? 0 : precision; 
	var multiple = Math.pow(10,precision);
	return Math.ceil(val * multiple)/multiple;
}

/***
 * 数值四舍五入
 * @param val			数值或数值字符串
 * @param precision		保留小数点位数，例如：2保留2位小数，-2保留到百位；
 * @param defaultValue	数值无效时的默认值
 * @returns 数值
 */
E7.numberRound = function(val, precision, defaultValue) {
	if(!E7.isNumber(val)) {		
		return defaultValue==undefined ? null : defaultValue;
	}

	precision = precision == undefined ? 0 : precision; 
	var multiple = Math.pow(10,precision);
	return Math.round(val * multiple)/multiple;
}

/***
 * 数值下取整
 * @param val			数值或数值字符串
 * @param precision		保留小数点位数，例如：2保留2位小数，-2保留到百位；
 * @param defaultValue	数值无效时的默认值
 * @returns 数值
 */
E7.numberFloor = function(val, precision, defaultValue) {
	if(!E7.isNumber(val)) {		
		return defaultValue==undefined ? null : defaultValue;
	}

	precision = precision == undefined ? 0 : precision; 
	var multiple = Math.pow(10,precision);
	return Math.floor(val * multiple)/multiple;
}

/***
 * 生成uuid
 */
E7.uuid = function(len, radix) {
	var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split(''), uuid = [], i;
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
}

/***
 * 根据已有文件，生成随机文件名
 */
E7.randomFileName = function(oldfile) {
	var fileName = E7.uuid();
	if(oldfile) {
		var last = oldfile.lastIndexOf(".");
		if(last >= 0) {
			fileName += oldfile.substr(last);
		}
	}
	return fileName;
}

//****校验操作****************************************************************************

/***
 * 是否为空字符串
 */
E7.isEmpty = function(str) {
	if(E7.getType(str) == 'undefined' || str == null || (E7.getType(str) == 'string' && str.trim() == '')) {
		return true;
	} 
	
	return false;
}

/***
 * 是否为手机号码
 */
E7.isMobile = function(str) {
	return /^1[34578]\d{9}$/.test(str);
}

/***
 * 是否为邮箱地址
 */
E7.isEmail = function(str) {
	return /^[a-zA-Z0-9_\.]+@[a-zA-Z0-9-]+[\.a-zA-Z]+$/.test(str);
}

/***
 * 是否为数值
 */
E7.isNumber = function(str) {
	if(str == undefined || str == null){
		return false;
	}
	if(typeof str == 'number'){
		return true;
	}
	if(typeof str == 'string' && str.trim() == ''){
		return false;
	}

	return !isNaN(str);
}

//****表单操作***************************************************************************

/***
 * 重置表单
 */
E7.resetForm = function(frm) {
	$(frm)[0].reset();
	$(frm + ' input[type="hidden"]').val('');
}

/***
 * 表单数据序列化成JSON对象
 * @param frm 	表单选择器，例如：'#formId' 
 */
E7.serialForm = function(frm) {
	var array = $(frm).serializeArray();
	var disabledItems = $(frm + ' :disabled[name]');
	var obj = {};
	
	for(var i=0; i<disabledItems.length; i++){
		obj[$(disabledItems[i]).attr('name')] = $(disabledItems[i]).val();
	}
	
	for(var i=0; i<array.length; i++) {
		var item = array[i];
		if(item.name in obj) {
			var oldValue = obj[item.name];
			if(oldValue instanceof Array) {
				obj[item.name].push(item.value);
			} else {
				var arr = [oldValue, item.value];
				obj[item.name] = arr;
			}
		} else {
			obj[item.name] = item.value;
		}
	}
	return obj;
}

/***
 * 表单数据序列化成JSON对象, 表单元素名称包含"."的，会转换成对象；
 * 例如：<input name="user.info.name" value="a"/> => {'user':{'info':{'name':'a'}}}
 * @param frm 	表单选择器，例如：'#formId' 
 */
E7.serialDepthForm = function(frm) {
	var data = E7.serialForm(frm);
	for(key in data) {
		if(key.indexOf('.') > 0) {
			var arr = key.split('.');
			var depthIndex = 0;
			var curData = data;
			while(depthIndex < arr.length) {
				var propertyName = arr[depthIndex];
				var propertyObj = curData[propertyName];
				if(depthIndex == arr.length-1) {
					curData[propertyName] = data[key];
				} else {
					if(E7.getType(propertyObj) == 'undefined'){
						curData[propertyName] = {};
					} 
					curData = curData[propertyName];
				}
				depthIndex = depthIndex + 1;
			}
			delete data[key];
		}
	}
	return data;
}

E7.serialDepthForm = function(frm) {
	var data = E7.serialForm(frm);
	for(key in data) {
		if(key.indexOf('.') > 0) {
			var arr = key.split('.');
			var depthIndex = 0;
			var curData = data;
			while(depthIndex < arr.length) {
				var propertyName = arr[depthIndex];
				var propertyObj = curData[propertyName];
				if(depthIndex == arr.length-1) {
					curData[propertyName] = data[key];
				} else {
					if(E7.getType(propertyObj) == 'undefined'){
						curData[propertyName] = {};
					} 
					curData = curData[propertyName];
				}
				depthIndex = depthIndex + 1;
			}
			delete data[key];
		}
	}
	return data;
}

/***
 * JSON数据填充到表单里
 */
E7.fillForm = function(frm, data, reset, parent) {
	if(E7.isEmpty(parent) && (E7.getType(reset)=='undefined' || reset)) E7.resetForm(frm);
	
	for (var key in data) {
		var elName = E7.isEmpty(parent) ? key : (parent + '.' + key);
		var elValue = data[key];

		if(elValue == null) continue;
		
		if(E7.getType(elValue) == 'object') {
			E7.fillForm(frm, elValue, false, elName);
			continue;
		}
		
		var els = $(frm + ' [name="' + elName + '"]');
		if(els.length == 0) continue;
		
		if(els[0].type == 'radio') {
			$(frm + ' [name="' + elName + '"][value="' + elValue + '"]').prop("checked",true); 
		} else if(els[0].type == 'select-multiple') {
			if(elValue instanceof Array) {
				els.val(elValue);
			} else if(typeof elValue == 'string') {
				els.val(elValue.split(','));
			} else if(typeof elValue == 'number') {
				els.val(elValue + '');
			} else {
				els.val(elValue);
			}
		} else if(els[0].type == 'checkbox') {
			if(els.length ==1) {
				if(els.val() == elValue) {
					els.prop('checked', true);
				}
			} else {
				if(elValue instanceof Array) {
					els.val(elValue);
				} else if(typeof elValue == 'string') {
					els.val(elValue.split(','));
				} else if(typeof elValue == 'number') {
					els.val(elValue + '');
				} else {
					els.val(elValue);
				}
			}
		} else {
			els.val(elValue + '');
		}
	}
}

/***
 * select控件初始化默认选项
 */
E7.selectDefault = {
	selector: null,			//select控件选择器, 例如：'#selectId'
	valueField: 'id',		//value字段
	textField: 'text',		//text字段
	titleField: 'title',	//title字段
	items: null,			//数据项, 如果有url，则为url返回的数据
	addEmpty: true,			//是填加空项
	defaultValue: null,		//默认选项值
	url: null,				//远程获取数据的url
	queryParams:{},			//远程获取数据的查询参数
	callback:null			//远程初始化完成后，回调函数： function(option)
}

/***
 * 填充数据
 */
E7.fillSelect = function(option) {
	var option = $.extend({}, E7.selectDefault, option);
	
	if(option.url == null) {
		E7._initSelect(option);
		return;
	}
	
	
	E7.get(option.url, option.queryParams, function(result){
		var dataArr = [];
		if(result.data instanceof Array) {
			dataArr = result.data;
		}

		option.items = dataArr;
		E7._initSelect(option);
		
		if(typeof option.callback == "function") {
			option.callback(option);
		}
	});
}

/***
 * 初始化select控件选项
 * @param selector		select控件对象：$('#selector')
 * @param items			选项列表:[{'id':'1', 'text':'北京'}, {'id':'2', 'text':'上海'}]
 * @param addEmpty		是否添加空元素
 * @param defaultValue	默认选项
 */
E7.initSelect = function(selector, items, addEmpty, defaultValue) {
	E7.fillSelect({
		selector:selector,
		items:items,
		addEmpty:addEmpty,
		defaultValue:defaultValue
	});
}

/***
 * 远程获取数据，初始化select控件选项
 * @param url			获取数据的url
 * @param params		查询参数
 * @param selector		select控件对象选择器, 例如：'#selectId'
 * @param textField		名称对应的字段
 * @param valueField	值对应的字段
 * @param addEmpty		是否添加空元素
 * @param defaultValue	默认选项
 * @param callback 		回调函数
 */
E7.initSelectRemote = function(url, params, selector, textField, valueField, addEmpty, defaultValue, callback) {
	E7.fillSelect({
		url: url,
		queryParams: params,
		selector: selector,
		textField: textField,
		valueField: valueField,
		addEmpty: addEmpty,
		defaultValue: defaultValue,
		callback: callback
	});
}


/***
 * 获取selector控件当前选中项的数据，
 * @param selector select控件对象选择器, 例如：'#selectId'
 * 返回JSON对象，{'id':1,'text':'xxxx',...}
 */
E7.getSelectedItem = function(selector) {
	var option = $(selector).data('option');
	var value = $(selector).val();
	
	if(value == '' || !option.items || !option.valueField) {
		return null;
	}
	
	for(var i = 0; i < option.items.length; i++) {
		if(option.items[i][option.valueField] == value) {
			return option.items[i];
		}
	}
}


E7._initSelect = function(option){
	$(option.selector).empty();
	if(option.addEmpty) {
		$(option.selector).append(new Option(E7.i18n.select, ''));
	}

	$(option.selector).data('option', option);
	
	for (var i = 0; i < option.items.length; i++) {
		var item = option.items[i];
		var value = item[option.valueField];
		var text = item[option.textField];
		var title = option.titleField ? item[option.titleField] : null;
		
		var op = new Option(text, value);
		if(option.defaultValue != undefined && option.defaultValue != null && option.defaultValue == value){
			op.selected = true;
		}
		if(title != null) {
			op.title = title;
		}
		
		$(option.selector).append(op);
	}
}

/***
 * Html字符串转义
 * @param htmlStr 需要转义的字符串
 * @returns 转义后的字符串
 */
E7.htmlEncode = function (htmlStr) {
	var p = $('<p></p>');
	p.text(htmlStr);
	return p.html();
}


//****AJAX操作***************************************************************************
/***
 * AJAX默认选项
 */
E7.ajaxDefault={
	type: "POST",
	dataType: 'json',		//数据格式json
	timeout: 30000,	 		//超时时间30秒
	maskable: true,			//是否显示蒙版
	successCallback: null,	//成功回调函数
	errorCallback: null,	//失败回调函数
	success: function(result){
		//返回结果不是json格式，直接传给回调函数
		if(E7.getType(result) != 'object') {
			if(E7.getType(this.successCallback) == 'function') this.successCallback(result);
			return;
		}
		
		//未登陆，跳转到登陆页面
		if(result.code == 100) {
			window.location = result.data;
			return;
		}
		
		try{
			//操作成功，调用成功回调函数
			if(result.code == 0) {
				if(this.successCallback != null) this.successCallback(result);
			//操作失败, 调用失败回调函数或弹出错误信息
			} else {
				if(this.errorCallback == null) {
					bootbox.alert({'title':E7.i18n.errorMessage, 'message':result.msg});
				} else {
					this.errorCallback(result);
				}
			}
		} catch(e){
			console.log('E7.ajax请示sucess处理异常！');
		}
	},
	error: function(XMLHttpRequest, textStatus, errorThrown){
		try{
			if(this.errorCallback == null) {
				bootbox.alert({'title':E7.i18n.errorMessage, 'message' : E7.i18n.serverRequestFailed});
			} else{
				this.errorCallback({'code':1, 'msg':E7.i18n.serverRequestFailed});
			}
		}catch(e){
			console.log('E7.ajax请示error！');
		}
	},
	complete : function(data){
		if(this.maskable) E7.mask.hide();
	},
	beforeSend : function(data){
		if(this.maskable) E7.mask.show();
	}
}

/***
 * ajax请求，在jquery.ajax基础上添加如下选项：
 * maskable			是否显示加载中蒙版
 * successCallback	成功回调函数
 * errorCallback	失败回调函数
 */
E7.ajax = function(option) {
	var opts = $.extend({}, E7.ajaxDefault, option);
	$.ajax(opts);
}

E7.post = function(url, params, successCallback) {
	var opts = $.extend({}, E7.ajaxDefault, {
		'url':url, 
		'type': 'POST', 
		'data': $.extend({'ajax': true}, params), 
		'successCallback':successCallback});
	$.ajax(opts);
}

E7.get = function(url, params, successCallback) {
	var opts = $.extend({}, E7.ajaxDefault, {
		'url':url, 
		'type': 'GET', 
		'data': $.extend({'ajax': true}, params), 
		'successCallback':successCallback});
	$.ajax(opts);
}

/***
 * 把参数以JSON字符串的形式上传，@RequestBody
 */
E7.postJSON = function(url, params, successCallback) {
	var opts = $.extend({}, E7.ajaxDefault, {
		'url':url, 
		'type': 'POST', 
		'contentType': 'application/json; charset=UTF-8', 
		'data': JSON.stringify($.extend({'ajax': true}, params)), 
		'successCallback':successCallback});
	$.ajax(opts);
}

/***
 * AJAX异步文件上传，例如：
 * 		var formData = new FormData();
 * 		formData.append("file", document.getElementById("file").files[0]);   
 * 		E7.postMultipart('/card/importData', formData, function(result){
 *			alert(result);
 *		});
 * @param url				请求地址
 * @param formData			请求表单数据
 * @param successCallback	请求成功回调方法
 * @param timeout			请求超时时间
 */
E7.postMultipart = function(url, formData, successCallback, timeout) {
	formData.append("ajax",true);
	var opts = $.extend({}, E7.ajaxDefault, {
		'url':url, 
		'type': 'POST', 
		'data': formData,
		'contentType': false,		//必须false才会自动加上正确的Content-Type
		'processData': false, 		//必须false才会避开jQuery对 formdata 的默认处理XMLHttpRequest会对 formdata 进行正确的处理
		'timeout': timeout,
		'successCallback':successCallback});
	$.ajax(opts);
}

//-----蒙版---------------------------------
E7.mask = function() {}
E7.mask.itemTotal = 0;
E7.mask.show = function(obj) {
	E7.mask.itemTotal += 1;
	if(E7.mask.itemTotal == 1) {
		var mask = $('#e7_mask');
		if(mask.length == 0) {
			E7.mask.create();
		} else {
			mask.css('display', 'block');
		}
	}
}
E7.mask.hide = function(obj) {	
	E7.mask.itemTotal -= 1;
	if(E7.mask.itemTotal == 0) {
		$('#e7_mask').css('display', 'none');
	}
}
E7.mask.create = function() {
	$('body').append($('<div class="modal fade in" id="e7_mask" tabindex="-1" role="dialog" style="display: block; padding-right: 17px;"><div class="e7-loading"></div></div>'));
}
