		
/***
 * 查询
 */
function query() {
	$('#tbl_query').e7table('query','#frm_query');
}

function exportQuery() {
	var ifrm = $('#ifr_export');
	if(ifrm.length == 0) {
		ifrm = $('<iframe id="ifr_export" style="display:none;height:0px,width:0px></iframe>');
		$('body').append(ifrm);
		$('#frm_query').attr('action', pageSetting.resetPath + '/export');
		$('#frm_query').attr('target', 'ifr_export');
	}
	$('#frm_query').submit();
}

/***
 * 刷新
 */
function refresh() {
   	$('#tbl_query').e7table('reload');
}

/***
 * 重置
 */
function reset() {
	E7.resetForm('#frm_query');
}	

/***
 * 显示添加对话框
 */
function showAddDialog() {
	E7.resetForm('#frm_detail');
	if($('#frm_detail').data('bootstrapValidator')){
		$('#frm_detail').data('bootstrapValidator').resetForm(true);
	}
	showDetail(pageSetting.modelName + '添加', true);
}

/***
 * 显示编辑对话框
 */
function showEditDialog(disable) {
	var rows = $('#tbl_query').e7table('getSelectedRows');
	if(rows.length == 0) {
		bootbox.alert({'title':'错误消息', 'message':'请选择数据！'});
		return;
	} else if(rows.length > 1) {
		bootbox.alert({'title':'错误消息', 'message':'一次只能编辑一条数据！'});
		return;
	}

	if($('#frm_detail').data('bootstrapValidator')){
		$('#frm_detail').data('bootstrapValidator').resetForm(true);
	}
	
	E7.post(pageSetting.resetPath + '/get', {'id' : rows[0]['id']}, function(result) {
		E7.fillForm('#frm_detail', result.data);
		if(disable){
			showDetail(pageSetting.modelName + '查看', false);
		} else {
			showDetail(pageSetting.modelName + '编辑', true);
		}
	});	
}

/***
 * 显示查看对话框
 */
function showViewDialog() {
	showEditDialog(true);
}

/***
 * 删除
 */
function del(){
	var rows = $('#tbl_query').e7table('getSelectedRows');
	if(rows.length == 0) {
		bootbox.alert({'title':'错误消息', 'message':'请选择数据。'});
		return;
	} else if(rows.length > 1) {
		bootbox.alert({'title':'错误消息', 'message':'一次只能删除一条数据。'});
		return;
	}
	
	bootbox.confirm({
		buttons : {
			confirm : {label : '确定', className : 'btn-info'},
			cancel : {label : '取消', className : 'btn-cancel'}
		},
		className : "del-model",
		message : '确定要删除？',
		callback : function(result) {
			if (!result) return;
			
			E7.post(pageSetting.resetPath + '/del', {'id' : rows[0]['id']}, function(data) {
				bootbox.alert({'title' : "消息",'message' : '删除成功！'});
               	$('#tbl_query').e7table('reload');
			});
		},
		title : "删除确认"
	});			
}

/***
 * 批量删除
 */
function batchDel(){
	var rows = $('#tbl_query').e7table('getSelectedRows');
	if(rows.length == 0) {
		bootbox.alert({'title':'错误消息', 'message':'请选择数据。'});
		return;
	}
	
	var ids = [];
	$(rows).each(function() {
		ids.push(this.id);
	});
	
	bootbox.confirm({
		buttons : {
			confirm : {label : '确定', className : 'btn-info'},
			cancel : {label : '取消', className : 'btn-cancel'}
		},
		className : "del-model",
		message : '确定要删除？',
		callback : function(result) {
			if (!result) return;
			
			E7.post(pageSetting.resetPath + '/del', {'id' : ids.join(',')}, function(data) {
				bootbox.alert({'title' : "消息",'message' : '删除成功！'});
               	$('#tbl_query').e7table('reload');
			});
		},
		title : "删除确认"
	});			
}
/***
 * 保存操作
 */
function save() {
	if($('#frm_detail').data('bootstrapValidator')) {
		$('#frm_detail').bootstrapValidator('validate');
		
		if (!$('#frm_detail').data('bootstrapValidator').isValid()) {
			return;
		}
	}

	var formParams = E7.serialForm('#frm_detail');

	E7.post(pageSetting.resetPath + '/save', formParams, function(data) {
		hideDetail();
		bootbox.alert({'title' : "消息", 'message' : '保存成功！'});
		$('#tbl_query').e7table('reload');
	});
}