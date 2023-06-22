/*
	Private jQuery For Medallion
	Copyright (c) 2012
*/


function validateEmail(email){
	var emailReg=/^(([a-zA-Z0-9\-\._*]+)@(([a-zA-Z0-9\-_]+\.)+)([a-zA-Z]{2,3})([,;](?!$))?)+$/;
	//var emailReg =/^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    return emailReg.test(email);
}


// validate for compare max 3 compare
function validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError) {
	 if (($(compare1).val() != "") && ($(compare2).val() != "") && ($(compare1+"Error").html() != conditionMessageError) && ($(compare2+"Error").html() != conditionMessageError)) {
			if (compare2Format < compare1Format) {
				$(compare2).addClass('fieldError');
				$(compare2+"Error").html(errorMsg).show();
				//---add
				if (($(compare3).val() != "") && ($(compare3+"Error").html() != conditionMessageError)) {
					if (compare3Format < compare2Format) {
						$(compare3).addClass('fieldError');
						$(compare3+"Error").html(errorMsg).show();
					} else  {
						$(compare3).removeClass('fieldError');
						$(compare3+"Error").html("");
					}
				}
				//----
			} else if ((thisId == compare3) && ($(compare3).val() != "") && ($(compare3+"Error").html() != conditionMessageError)){
				if (compare3Format < compare2Format) {
					$(compare3).addClass('fieldError');
					$(compare3+"Error").html(errorMsg).show();
				}
			} else {
				//$(thisId).removeClass('fieldError');
				//$(thisId+"Error").html("");
				if (thisId == compare2 ) {
					if ($(compare1+"Error").html() != conditionMessageError) {
						$(compare1).removeClass('fieldError');
						$(compare1+"Error").html("");
						if (($(compare3).val() != "") && ($(compare3+"Error").html() != conditionMessageError)) {
							if (compare3Format < compare2Format) {
								$(compare3).addClass('fieldError');
								$(compare3+"Error").html(errorMsg).show();
							} else  {
								$(compare3).removeClass('fieldError');
								$(compare3+"Error").html("");
							}
						}
					}
				} else if (thisId == compare1) {
					if ($(compare2+"Error").html() != conditionMessageError) {
						$(compare2).removeClass('fieldError');
						$(compare2+"Error").html("");
						
						if (($(compare3).val() != "") && ($(compare3+"Error").html() != conditionMessageError)) {
							if (compare3Format < compare2Format) {
								$(compare3).addClass('fieldError');
								$(compare3+"Error").html(errorMsg).show();
							} else  {
								$(compare3).removeClass('fieldError');
								$(compare3+"Error").html("");
							}
						}
					
					} 
				} else if (thisId == compare3) {
					if ($(compare2+"Error").html() != conditionMessageError) {
						$(compare2).removeClass('fieldError');
						$(compare2+"Error").html("");

						if (($(compare1).val() != "") && ($(compare1+"Error").html() != conditionMessageError)) {
							if (compare2Format < compare1Format) {
								$(compare1).addClass('fieldError');
								$(compare1+"Error").html(errorMsg).show();
							} else  {
								//$(compare1).removeClass('fieldError');
								//$(compare1+"Error").html("");
							}
						}
					
					} 
				}
		}
	}
}