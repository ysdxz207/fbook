/**
 * 消息框
 * @author Moses
 * @date 2015-09-10
 */

;
jQuery.fn.salert = function(msg, title, lbtnt, rbtnt, callback){
	if(jQuery.type(title) == 'function'){
		callback = title;
		title = '';
	}
	var titleText = title?title:'提示',
		msgText = msg;
		lbtnText = lbtnt?lbtnt:'确定',
		rbtnText = rbtnt?rbtnt:'取消';
	
	var modalMain = $('<div>').attr('id', 'alert-modal').addClass('modal fade'),
		modalDialog = $('<div>').addClass('modal-dialog').appendTo(modalMain),
		modalContent = $('<div>').addClass('modal-content').appendTo(modalDialog),
		modalHeader = $('<div>').addClass('modal-header').appendTo(modalContent),
		modalBody = $('<div>').addClass('modal-body').appendTo(modalContent),
		modalFooter = $('<div>').addClass('modal-footer').appendTo(modalContent),
		
		buttonClose = $('<button>').addClass('close').attr('data-dismiss', 'modal').attr('aria-label', 'close').appendTo(modalHeader),
		buttonCloseIcon = $('<span>').attr('aria-hidden', 'true').html('&times;').appendTo(buttonClose),
		modalTitle = $('<h4>').addClass('modal-title').text(titleText).appendTo(modalHeader),
		
		modalContentP = $('<p>').text(msgText).appendTo(modalBody),
		
		lbtn = $('<button>').attr('type', 'button').addClass('btn btn-success').text(lbtnText).appendTo(modalFooter),
		rbtn = $('<button>').attr('type', 'button').addClass('btn btn-default').attr('data-dismiss', 'modal').text(rbtnText);
		
	if(callback){
		rbtn.appendTo(modalFooter);
	}
	
	modalMain.modal('show').on('hidden.bs.modal', function (e) {
		modalMain.remove();
	});
	
	lbtn.on('click', function(){
		if(callback){
			callback($(this).hasClass('btn-success'));
			//callback.call(this, true);
			modalMain.modal('hide');
		} else {
			modalMain.modal('hide');
		}
	});
	
}
salert = jQuery.fn.salert;
