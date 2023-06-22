(function(jQuery){
	jQuery.fn.disabled = function() {
		var obj = $(this);
		obj.attr("disabled", "disabled");
		return obj; 
	};
	
	jQuery.fn.enabled = function() {
		var obj = $(this);
		obj.removeAttr("disabled");
		return obj;
	}; 
	
    jQuery.fn.inject = function(parent, validate) {
    	if (validate == undefined) validate = true;
    	
    	var array = [];
    	var id = $(this).attr("id");
    	if (id != "") { 
    		array[array.length] = id;
    		parent[id] = $("#"+id); 
    	}
    	
    	$("input", $(this)).add($("div", $(this))).add($("select", $(this))).add($("p", $(this))).add($("table", $(this))).add($("form", $(this))).add($("button", $(this))).each(function(idx, data){
			var id = $(data).attr("id");
			if (id != "") {
				if ($.inArray(id, array) >= 0 && validate) { alert("Duplicate input ID "+id+", Make sure this is fix"); }
				array[array.length] = id;
				parent[id] = $("#"+id);
			}
		});
    	array = [];
    	return parent;
    };
    
    jQuery.fn.id = function(id, value, autonumeric) {
    	var obj = $("#"+id, $(this));
    	if (obj && (value != undefined)) {
    		if (autonumeric) {
    			obj.autoNumericSet(value, {vMin: '-999999.999999999', mDec: 10});
    		}else{
    			obj.val(value);	
    		}
    		
    		$("#h_"+id, $(obj).parent()).val(value);
        	$("#"+id+"Stripped", $(obj).parent()).val(value);
    	}
    	return obj; 
    };
    
    jQuery.fn.valueRnd = function(value, autonumeric, mdecimal, mType) {
    	var type = "";
    	if (mType == "ROUND"){ type = 'S';
		}else if (mType == "ROUNDUP"){ type = 'U';
		} else { type = 'D'; }
    	
    	var obj = $(this);
    	obj.value(value, autonumeric, mdecimal, type);
    	return obj;
    };
    
    jQuery.fn.value = function(value, autonumeric, mDigit, mType) {
    	if (mDigit == null) mDigit = 2;
    	if (mType == null) mType = "S";

    	var obj = $(this);
    	var id  = $(this).attr("id");
    	if (obj && (value != undefined)) {
    		if (autonumeric) {
    			obj.autoNumericSet(value, {vMin: '-999999999999.99', vMAx:'999999999999.99', mDec: mDigit, mRound : mType, aPad: true});
    		}else{
    			obj.val(value);	
    		}
    		
    		$("#h_"+id, $(obj).parent()).val(value);
        	$("#"+id+"Stripped", $(obj).parent()).val(value);
    	}
    	return obj; 
    };
    
	jQuery.fn.name = function(name, value) { 
    	var obj = $("[name="+name+"]", $(this));
    	if (obj && value) { 
    		obj.val(value);
    		$("#h_"+id, $(obj).parent()).val(value);
        	$("#"+id+"Stripped", $(obj).parent()).val(value);
    	}
    	return obj;
    };
    
	jQuery.fn.tab = function(idx) { 
    	return $("#tab-"+idx, $(this));
    };
    
	jQuery.fn.clazz = function(clazz) {
    	return $("."+clazz, $(this));
    };
    
    jQuery.fn.isEmpty = function() {
    	return ($(this).val() == "");
    };
    
	jQuery.fn.tbody = function(idx) {
    	var tbodys = $("tbody", $(this));
    	if (tbodys) {
	    	if (tbodys.length == 1) {
	    		return tbodys;
	    	}else {
	    		if (idx == null) { return tbodys; }
	    		return tbodys.eq(idx);
	    	}
    	}
    	return tbodys;
    };
    
    jQuery.fn.tr = function(idx) {
    	var trs = $("tr", $(this));
    	if (trs) {
	    	if (trs.length == 1) {
	    		return trs;
	    	}else {
	    		if (idx == null) { return trs; }
	    		return trs.eq(idx);
	    	}
    	}
    	return trs;
    };
    
    jQuery.fn.td = function(idx) {
    	var tds = $("td", $(this));
    	if (tds) {
	    	if (tds.length == 1) {
	    		return tds;
	    	}else {
	    		if (idx == null) { return tds; }
	    		return tds.eq(idx);
	    	}
    	}
    	return tds;
    };

    
    jQuery.fn.bindval = function(value) {
    	var id = $(this).attr("id");
    	$(this).val(value);
    	$("#h_"+id, $(this).parent()).val(value);
    	$("#"+id+"Stripped", $(this).parent()).val(value);
    	
    	return $(this);
    };
    
    jQuery.fn.isChange = function() {
    	var oldval = $(this).data("old");
    	//if (oldval == undefined || newval == '') oldval = $(this).val();
    	var newval = $(this).val();
    	//alert("new: "+newval);
    	$(this).data("old", newval);
    	return (oldval != newval);
    };
    
    jQuery.fn.checked = function(){
        var a = $(this).filter(':checked').val();
        return  (a == undefined) ? "false" : a;
    };
    
    jQuery.fn.rdchecked = function(){
        var a = $(this).filter(':checked').val();
        return a;
    };
    
    jQuery.fn.numeric = function() {
    	$(this).autoNumericSet({aSign: 'Rp. ', wEmpty: 'sign'} );
    	return $(this);
    };
    
    // Popup
//    jQuery.fn.popup = function(urlList, urlGet, expression, nextobjid, func) {
//    	var id = $(this).attr("id");
//    	$(this).lookup({
//			list : urlList + expression,
//			get : urlGet + expression,
//			help : $("#"+id+"Help", $(this).parent()),
//			description : $("#"+id+"Desc", $(this).parent()),
//			key :  $("#"+id+"Key", $(this).parent()),
//			nextControl : $("#"+nextobjid)
//		});
//    	
//    	if (typeof func == 'function') {
//    		$(this).blur(function(){
//    			$.get(urlGet, {'code':$(this).val() }, function(data, status, xhr) {
//    				func(data);
//				});
//    		});    		
//    	}
//    };
    
    
    jQuery.fn.popup = function(urlList, urlGet, expression, nextobjid, func, funcerr) {
		var id = $(this).attr("id");
		var htmlHelp = $("#"+id+"Help", $(this).parent());
		var htmlDesc = $("#"+id+"Desc", $(this).parent());
		var htmlKey = $("#"+id+"Key", $(this).parent());
		var htmlNext = $("#"+nextobjid);
		
		$(this).lookup({
			list : urlList + expression,
			get : {
				url : urlGet + expression,
				success : function(data) {
					$("#"+id).removeClass('fieldError');
					htmlDesc.val(data.description);
					$("#h_" + htmlDesc.attr("id")).val(data.description);
					htmlKey.val(data.code);
					//htmlNext.focus();

					if (typeof func == 'function') {
						func(data);
					}
				},
				error : function(data) {
					$("#"+id).addClass('fieldError');
					$("#"+id).val("");
					htmlDesc.val("NOT FOUND");
					$("#h_" + htmlDesc.attr("id")).val("");
					htmlKey.val("");
					
					if (typeof funcerr == 'function') {
						funcerr(data);
					}
				}
			},
			help : htmlHelp,
			description : htmlDesc,
			key :  htmlKey
			//nextControl : htmlNext
		});
	};
	
	jQuery.fn.blurLookup = function() {
		var id = $(this).attr("id");
		var htmlDesc = $("#"+id+"Desc", $(this).parent());
		var htmlKey = $("#"+id+"Key", $(this).parent());
		$(this).blur(function(){
			if( $("#"+id).val() == "") {
				$("#"+id).removeClass('fieldError');
				$("#"+id).val("");
				htmlDesc.val("");
				$("#h_" + htmlDesc.attr("id")).val("");
				htmlKey.val("");
			}
		});
	};
	
    
    jQuery.fn.popupThirdParties = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.thirdParties()}', '@{Pick.thirdParty()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupLookup = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.lookups()}', '@{Pick.lookup()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccount = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccounts()}', '@{Pick.bankAccount()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccountCustomer = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccounts()}', '@{Pick.bankAccount()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCurrencies = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.currencies()}', '@{Pick.currency()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBranch = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.branches()}', '@{Pick.branch()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupTax = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.taxMasters()}', '@{Pick.taxMaster()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupProduct = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgProducts()}', '@{Pick.rgProduct()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupProductByEffLiqDate = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgProductByEffDateAndLiqDates()}', '@{Pick.rgProductByEffDateAndLiqDate()}', "", nextobjid, func, funcerr);
    };

    jQuery.fn.popupInvestmentAcct = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgInvestmentAccts()}', '@{Pick.rgInvestmentAcct()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupInvestmentByCustomer = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgInvestmentByCustomers()}', '@{Pick.rgInvestmentByCustomer()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCustomer = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customers()}', '@{Pick.customer()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCustomerInvestment = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customerInvestments()}', '@{Pick.customerInvestment()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccountInvt = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgBankAccounts()}', '@{Pick.rgBankAccount()}', expression, nextobjid, func, funcerr);
    };
    
    // Getter to server
    jQuery.fn.getRgTax = function(code, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.taxMaster()}", {'code':code}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getRgTaxKey = function(taxKey, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.taxMasterKey()}", {'taxKey':taxKey}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getRgProduct = function(code, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgProduct()}", {'code':code}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getRgPortfolio = function(productCode, accountnumber, holdingdate, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgPortfolio()}", {'productCode':productCode, 'accountnumber':accountnumber, 'holdingdate':holdingdate}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getPortfolioTotalUnit = function(productCode, holdingdate, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgPortfolioUnit()}", {'productCode':productCode, 'holdingdate':holdingdate}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getPortfolioTotalUnitAccount = function(accountNumber, holdingdate, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgPortfolioUnitAccount()}", {'accountNumber':accountNumber, 'holdingdate':holdingdate}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getRgInvestmentAcct = function(type, code, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgInvestmentAcct()}", {'type':type, 'code':code}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getRgNav = function(navDate, productcode, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgNav()}", {'navDate':navDate, 'productcode':productcode}, function(data) {
			returnData = data;
			func(data);
		});		
		return returnData;
    };
    
    jQuery.fn.getRgFeeTier = function(productcode, type, amount, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgFeeTier()}", {'productcode':productcode, 'type':type, 'amount':amount}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getWorkingDate = function(yyyyMMdd, day, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.addWorkingDate()}", {'yyyyMMdd':yyyyMMdd, 'day':day+""}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.addCutOfTime = function(yyyyMMdd, hour, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.addCutOfTime()}", {'yyyyMMdd':yyyyMMdd, 'hour':hour+""}, function(data) {
			returnData = data;
			func(data);
		});
		return returnData;
    };
    
    jQuery.fn.getRgFeeAmount = function(productcode, type, amount, inputBy, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.rgFeeAmount()}", {'productcode':productcode, 'type':type, 'amount':amount, 'inputBy':inputBy}, function(data) {
			returnData = data;
			func(data);
		});    	
		return returnData;
    };
    
    jQuery.fn.nextValidation = function(productCode, goodFundDate, isProcessAll, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
    	$.get("@{RegistryValidation.next()}", {'productCode':productCode, 'goodFundDate':goodFundDate, 'isProcessAll':isProcessAll}, function(data) {
    		returnData = data;
			func(data);
		});
    	return returnData;
    };
    
    // Prototype    
    String.prototype.isEmpty = function(){
        return (this == "");
    };

    String.prototype.toNumber = function(seperator) {
    	if (seperator == null) 
    		return Number(this.valueOf());
    	else
    		return Number(this.valueOf().replaceAll(seperator, ""));
    };
    
    String.prototype.replaceAll = function(seperator, replacement) {
    	return this.valueOf().replace(new RegExp(seperator, 'g'), replacement);
    };
    
    String.prototype.fmtYYYYMMDD = function(seperator){
    	var arr  = this.valueOf().split(seperator);
    	if (arr.length == 3)
    		return arr[2] + arr[0] + arr[1];
    	return "";
    };
    
    String.prototype.leadingZero = function(length){
        var loop = (length == null) ? 0 : length - this.valueOf().length;
        
        var value = this.valueOf();
        for (i = 0; i < loop; i++) {
            value = "0" + value;
        }
        return value;
    };
    
    String.prototype.toMMDDYYYY = function(seperator){
        if (this == null || this.length != 8) 
            return "";
        
        var year = this.substring(0, 4);
        var month = this.substring(4, 6);
        var day = this.substring(6, 8);
        
        return month + seperator + day + seperator + year;
    };
    
    String.prototype.toDate = function(){
        if (this == null || this.length != 10) 
            return "";
        var arr = this.split("/");
        var date = new Date();
        date.setFullYear(Number(arr[2]), Number(arr[0]) - 1, Number(arr[1]));
        return date;
    };
    
    Number.prototype.leadingZero = function(length){
        var loop = (length == null) ? 0 : length - (this.valueOf() + "").length;
        
        var value = this.valueOf();
        for (i = 0; i < loop; i++) {
            value = "0" + value;
        }
        return value;
    };
    
    Date.prototype.fmtDate = function(seperator){
        var tag = (seperator == null) ? "/" : seperator;
        return (this.getMonth() + 1).leadingZero(2) + tag + this.getDate().leadingZero(2) + tag + this.getFullYear();
    };
    
    Date.prototype.addDate = function(day){
    	var time = this.getTime();
    	var addtime = day * 24 * 60 * 60 * 1000;
    	return new Date(time+addtime);
    };
    
    Date.prototype.fmtDateMDY = function(seperator){
        var tag = (seperator == null) ? "" : seperator;
//        return (this.getMonth() + 1).leadingZero(2) + tag + this.getDate().leadingZero(2) +  tag + this.getFullYear();
        return this.getDate().leadingZero(2) + tag + (this.getMonth() + 1).leadingZero(2) + tag + this.getFullYear();
    };
    
    jQuery.fn.myAutoNumericGet = function(value) {
    	var el = $(this);
    	el.val(value);
    	var splitNumeric =  el.val().split(".");
    	if(splitNumeric.length > 1) {
    		var backNumeric = splitNumeric[1];
    		return el.autoNumericSet(value, { mDec: backNumeric.length, aPad: true}).val();
    	} else {
    		return el.autoNumericSet(value).val();
    	}
    };

    //Function to get the Maximum value in Array
    Array.max = function( array ){
    return Math.max.apply( Math, array );
    };

    // Function to get the Minimum value in Array
    Array.min = function( array ){
    return Math.min.apply( Math, array );
    };
    
})(jQuery);  
