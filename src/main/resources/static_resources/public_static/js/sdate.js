;
var sdate = {};

(function (sdate) {

    sdate.clock = function (option) {
        var options = {
            prefix: ''
        };
        jQuery.fn.extend(options, option);
        var today = new Date(),
            obj = $(this);
        setInterval(function () {
            today = new Date(today.setSeconds(today.getSeconds() + 1));
            obj.html(options.prefix + '' + today.formatDate('HH:mm:ss'))
        }, 1000)
    };

    sdate.getNowDate = function(days, format) {
        var date = new Date();
        date.setDate(date.getDate() + days);
        return date.formatDate(format ? format : "yyyy-MM-dd HH:mm:ss");
    }



})(sdate);

Date.prototype.formatDate = function(fmt) {
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "H+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
};