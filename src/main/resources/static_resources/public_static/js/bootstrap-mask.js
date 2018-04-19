/**
 * 遮罩层
 * @author Moses
 * @date 2017-12-16
 */

;
jQuery.fn.smask = function(html){
	//移除其他mask
    removeSmask();

	var modalMain = $('<div>').attr('id', 'mask_modal').addClass('modal fade').addClass('text-center'),
		htmlObj = $(html),
		htmlObjWidth = htmlObj.width();

	if (!htmlObjWidth) {
        htmlObj.css('width', '90%');
	}
	htmlObj.appendTo(modalMain);
    htmlObj.css('margin', '50vh auto 0');
    htmlObj.css('transform', 'translateY(-50%)');

	modalMain.modal('show').on('hidden.bs.modal', function (e) {
		modalMain.remove();
	});
	window.maskObj = modalMain;
};

jQuery.fn.removeSmask = function(){
    //移除其他mask
    var maskObj = window.maskObj;
    if (maskObj) {
        maskObj.modal('hide');
    }
};
smask = jQuery.fn.smask;
removeSmask = jQuery.fn.removeSmask;
