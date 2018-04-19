var role = {};
(function($, role) {
	
	/*
	 * 赋予权限按钮
	 */
	role.btnSetPermissions = function(url) {
		var btn = $('<button type="button" class="btn-default" '
				+ 'data-icon="fa-unlock-alt" >赋予权限</button>');
		btn.on('click', function() {
			var table = $.CurrentNavtab.find('#datagrid_role_list');
			var selectedDatas = $(table).data('selectedDatas');
			if (selectedDatas && (selectedDatas.length == 1)) {
				var id = selectedDatas[0].id;
				if (id = 20151106) {
                    BJUI.alertmsg('warn', '超级管理员拥有所有权限，不可修改！');
                    return btn;
				}
				var params = {};
				params.roleId = id;
				BJUI.navtab({
					id : 'role_setPermission',
					url : url,
					title : '赋予角色权限',
					fresh : true,
					data : params
				})
			} else {
				BJUI.alertmsg('error', '请选择要操作的行！');
			}
		});
		return btn;
	};
	
	/*
	 * 勾选ztree
	 */
	role.ztreeChecked = function(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj(treeId), nodes = zTree
				.getCheckedNodes(true);
		var idArr = [], ids = '', names = '', $hidden = $('#hidden_role_permission_edit_permissions');
		
		for (var i = 0; i < nodes.length; i++) {
			var id = nodes[i].id + '';
			if (id.indexOf('m_') == -1)
			idArr.push(id);
		}
		
		ids = idArr.join(',');
		$hidden.val(ids);
	};
})(jQuery, role);