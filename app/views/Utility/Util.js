function getWorkingDate(yyyyMMdd, day){
	var returnData;
	$.ajaxSetup({ async : false });
	$.get("@{Pick.addWorkingDate()}", {'yyyyMMdd':yyyyMMdd, 'day':day+""}, function(data) {
		returnData = data;
		
	});
	
	return returnData;
}

function getPaymentDate(yyyyMMdd, day){
	var strDate = getWorkingDate(yyyyMMdd, day) + "";
	var year = strDate.substring(0, 4);
    var month = strDate.substring(4, 6);
    var day = strDate.substring(6, 8);
    var fixDate = month + "/" + day + "/" + year;
    return fixDate;
	
}