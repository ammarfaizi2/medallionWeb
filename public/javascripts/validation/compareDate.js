(function(jQuery){
	
});

/*function compareDate(dateFrom, dateTo){
	var a = new Date(dateFrom);
	var b = new Date(dateTo);
	
	var msDateA = Date.UTC(a.getFullYear(), a.getMonth()+1, a.getDate());
	var msDateB = Date.UTC(b.getFullYear(), b.getMonth()+1, b.getDate());
	
	if (parseFloat(msDateA) < parseFloat(msDateB)) 
		return -1; // less than
	else if (parseFloat(msDateA) == parseFloat(msDateB))
		return 0;
	else (parseFloat(msDateA) > parseFloat(msDateB))
		return 1;
}*/

function compareDateFromTo(dateFrom, dateTo, origin, id){
	var dateA = new Date(dateFrom);
	var dateB = new Date(dateTo);
	if (dateB.getTime() < dateA.getTime()) {
		if (origin=='from'){
			$(id+'From').addClass('fieldError');
			$(id+'FromError').html('Date From must be less than Date To');
		} else {
			$(id+'To').addClass('fieldError');
			$(id+'ToError').html('Date To must be greater than Date From');
		}
	} else {
		$(id+'From').removeClass('fieldError');
		$(id+'FromError').html('');
		
		$(id+'To').removeClass('fieldError');
		$(id+'ToError').html('');
	}
}

function compareDateFromToEqual(dateFrom, dateTo, origin, id){
	var dateA = new Date(dateFrom);
	var dateB = new Date(dateTo);
	if (dateB.getTime() < dateA.getTime()) {
		if (origin=='from'){
			$(id+'From').addClass('fieldError');
			$(id+'FromError').html('Date From must be less or equal than Date To');
		} else {
			$(id+'To').addClass('fieldError');
			$(id+'ToError').html('Date To must be greater or equal than Date From');
		}
	} else {
		$(id+'From').removeClass('fieldError');
		$(id+'FromError').html('');
		
		$(id+'To').removeClass('fieldError');
		$(id+'ToError').html('');
	}
}

//To compare date on period
function compareDateOnPeriod(dateFrom, dateTo, dateNow) {
	var dateA = new Date(dateFrom);
	var dateB = new Date(dateTo);
	var dateN = new Date(dateNow);
	var validPeriod = false;

	if ((dateN.getTime() >= dateA.getTime()) && (dateN.getTime() <= dateB.getTime())) {
		validPeriod = true;
	}

	return validPeriod;
}