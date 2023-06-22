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
    	
    	$("input", $(this)).add($("div", $(this))).add($("select", $(this))).add($("p", $(this))).add($("table", $(this))).add($("form", $(this))).add($("button", $(this))).add($("textarea", $(this))).add($("span", $(this))).add($("form", $(this))).add($("a", $(this))).each(function(idx, data){
			var id = $(data).attr("id");
			var oriId = id;
			var memberId = id.replaceAll( "-", "_" );
			id = memberId;
			if (id != "") {
				if ($.inArray(id, array) >= 0 && validate) { alert("Duplicate input ID "+oriId+", Make sure this is fix"); }
				array[array.length] = id;
				parent[id] = $("#"+oriId);
			}
		});
    	array = [];
    	return parent;
    };
    
    jQuery.fn.inject2 = function(parent, validate) {
    	if (validate == undefined) validate = true;
    	
    	var array = [];
    	var id = $(this).attr("id");
    	if (id != "") { 
    		array[array.length] = id;
    		parent[id] = $("#"+id); 
    	}
    	
    	var htmlparent = $(this);
    	
    	$("input", $(this)).add($("div", $(this))).add($("select", $(this))).add($("p", $(this))).add($("table", $(this))).add($("form", $(this))).add($("button", $(this))).add($("textarea", $(this))).add($("span", $(this))).add($("form", $(this))).add($("a", $(this))).each(function(idx, data){
			var id = $(data).attr("id");
			var oriId = id;
			var memberId = id.replaceAll( "-", "_" );
			id = memberId;
			if (id != "") {
				if ($.inArray(id, array) >= 0 && validate) { alert("Duplicate input ID "+oriId+", Make sure this is fix"); }
				array[array.length] = id;
				parent[id] = $("#"+oriId, htmlparent);
			}
		});
    	array = [];
    	return parent;
    };
    
    jQuery.fn.injectComp = function(parent, validate, components) {
    	if (validate == undefined) validate = true;
    	
    	var array = [];
    	var id = $(this).attr("id");
    	if (id != "") { 
    		array[array.length] = id;
    		parent[id] = $("#"+id); 
    	}
    	
    	for (x in components) {
    		var id = components[x];
    		if ($.inArray(id, array) >= 0 && validate) { alert("Duplicate input ID "+id+", Make sure this is fix"); }
			array[array.length] = id;
			parent[id] = $("#"+id);
    	}
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
    	
    	if(mType == "ROUNDING_TYPE-1") { type = 'S';
		}else if(mType == "ROUNDING_TYPE-2") { type = 'U';
		}else { type = 'D'; }

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
    
    jQuery.fn.valueExponen = function(value, autonumeric, mDigit, mType) {
    	//logic disamakan dengan method jQuery.fn.value()
    	if (mDigit == null) mDigit = 2;
    	if(mType == "ROUNDING_TYPE-1") { mType = 'S';
		}else if(mType == "ROUNDING_TYPE-2") { mType = 'U';
		}else { mType = 'D'; }

    	var obj = $(this);
    	if (obj && (value != undefined)) {
    		if (autonumeric) {
    			obj.autoNumericSet(value, {vMin: '-999999999999.99', vMAx:'999999999999.99', mDec: mDigit, mRound : mType, aPad: true});
    		}else{
    			obj.val(value);	
    		}
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
    
    jQuery.fn.isMonth = function() {
    	var months = ["1", "2", "3", "4", "5", "6", "7", "8", "9","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
    	var idx = jQuery.inArray($(this).val(), months);
    	return idx >= 0;
    };
    
    jQuery.fn.isYear = function() {
    	var year =  Number($(this).val());
    	return (year >= 1000 && year <= 9999); 
    };
    
    jQuery.fn.required = function(isEmpty) {
    	if (isEmpty) {
    		$(this).html("Required");
    		$(this).show();
    	}else{
    		$(this).html("");
    		$(this).hide();
    	}
    	
    	return !isEmpty;
    };
    
    jQuery.fn.valid = function(valid, message) {
    	if (valid) {
    		$(this).html("");
    		$(this).hide();
    	}else{
    		$(this).html(message);
    		$(this).show();
    	}
    	
    	return valid;
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
    	var newval = $(this).val();
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
    
    jQuery.fn.popup = function(urlList, urlGet, expression, nextobjid, func, funcerr) {
		var id = $(this).attr("id");
		var htmlHelp = $("#"+id+"Help", $(this).parent());
		var htmlDesc = $("#"+id+"Desc", $(this).parent());
		var htmlKey = $("#"+id+"Key", $(this).parent());
		var htmlId = $("#"+id+"Id", $(this).parent());
		var htmlNext = $("#"+nextobjid);
		
		$(this).lookup({
			list : urlList + expression,
			get : {
				url : urlGet + expression,
				success : function(data) {
					checkRedirect(data);
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
					checkRedirect(data);
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
	
	jQuery.fn.popuppaging2 = function(obj, urlList, urlGet, param, nextobjid, func, funcerr) {
		var id = $(this).attr("id");
		var htmlHelp = $("#"+obj.help);
		var htmlDesc = $("#"+obj.desc);
		var htmlKey = $("#"+obj.key);
		var htmlNext = $("#"+nextobjid);
		
		$(this).data("urlParam", param);
		
		$(this).lookuppaging({
			list : urlList,
			get : {
				url : urlGet,
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
	
	jQuery.fn.popuppaging = function(urlList, urlGet, param, nextobjid, func, funcerr) {
		var id = $(this).attr("id");
		var htmlHelp = $("#"+id+"Help", $(this).parent());
		var htmlDesc = $("#"+id+"Desc", $(this).parent());
		var htmlKey = $("#"+id+"Key", $(this).parent());
		var htmlId = $("#"+id+"Id", $(this).parent());
		var idV = $("#"+id, $(this).parent());
		var htmlNext = $("#"+nextobjid);
		
		$(this).data("urlParam", param);
		
		$(this).lookuppaging({
			list : urlList,
			get : {
				url : urlGet,
				success : function(data) {
					//$("#"+id).removeClass('fieldError');
					idV.removeClass('fieldError');
					htmlDesc.val(data.description);
					$("#h_" + htmlDesc.attr("id")).val(data.description);
					htmlKey.val(data.code);
					//htmlNext.focus();
					htmlId.val(data.id);					

					if (typeof func == 'function') {
						func(data);
					}
				},
				error : function(data) {
					idV.addClass('fieldError');
					idV.val("");
					htmlDesc.val("NOT FOUND");
					$("#h_" + htmlDesc.attr("id")).val("");
					htmlKey.val("");
					htmlId.val("");
					
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
	
	 jQuery.fn.getPopup = function(url, parameter, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get(url, parameter, function(data) {
			checkRedirect(data);
			
			returnData = data;
			if (typeof func == 'function') {
				func(data);
			}
		});
		return returnData;
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
	
    jQuery.fn.getSecurityType = function(parameter, func) {
    	$(this).getPopup('@{Pick.securityType()}', parameter, func);
    };
	
	jQuery.fn.getCprule = function(parameter, func) {
    	$(this).getPopup('@{Pick.cpRule()}', parameter, func);
    };
    
	jQuery.fn.getThirdParties = function(parameter, func) {
    	$(this).getPopup('@{Pick.thirdParty()}', parameter, func);
    };
    
    jQuery.fn.getSecurityForChangeItem2 = function(parameter, func) {
    	$(this).getPopup('@{Pick.securityPickForChargeItem2()}', parameter, func);
    };
    
    jQuery.fn.popupClear = function() {
    	var parent = $(this).parent();
    	$("input[type=text]", parent).add($("input[type=hidden]", parent)).val("");
    	$("input[type=text]", parent).add($("input[type=hidden]", parent)).removeClass("fieldError");
    	return $(this);
    };
    
    jQuery.fn.popupEnabled = function(enabled) {
    	var id = $(this).attr("id");
    	var parent = $(this).parent();
    	
    	if (enabled) {
    		$("#"+id, parent).removeAttr("disabled");
    		$("#"+id+"Help", parent).removeAttr("disabled");
    	}else{
    		$("#"+id, parent).attr("disabled", "disabled");
    		$("#"+id+"Help", parent).attr("disabled", "disabled");
    	}
    	return $(this);
    };
    
    jQuery.fn.popupActionTemplates = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.actionTemplates()}', '@{Pick.actionTemplate()}', expression,nextobjid, func, funcerr);
    };
	
    jQuery.fn.popupThirdParties = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.thirdParties()}', '@{Pick.thirdParty()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupThirdPartiesWithParent = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.thirdPartiesWithParent()}', '@{Pick.thirdParty()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupThirdPartiesWithChild = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.thirdPartiesWithChild()}', '@{Pick.thirdParty()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupLookup = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.lookups()}', '@{Pick.lookup()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupUpprofile = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.udProfiles()}', '@{Pick.udProfileByName()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.securityType = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.securityTypes()}', '@{Pick.securityType()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.securityTypeDeposito = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.securityTypesDeposito()}', '@{Pick.securityTypeDeposito()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.securities = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.securities()}', '@{Pick.securityPickForChargeItem2()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCprule = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.cpRules()}', '@{Pick.cpRule()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccount = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccounts()}', '@{Pick.bankAccount()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccountCustomer = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccounts()}', '@{Pick.bankAccount()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCustomerAccount = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customerAccounts()}', '@{Pick.customerAccount()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCustomerAccountNotInFa = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customerAccountsNotInFa()}', '@{Pick.customerAccount()}', expression, nextobjid, func, funcerr);
    };
    
    // tambahan rizki redmine #817
    jQuery.fn.popupCustomerAccountNotInFa2 = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customerAccountsNotInFa()}', '@{Pick.customerAccount2()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupAccountCustomer = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.accountsByCustomer()}', '@{Pick.accountByCustomer()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupAccountCustomerNotInFa = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.accountsByCustomerNotInFa()}', '@{Pick.accountByCustomer()}', expression, nextobjid, func, funcerr);
    };
    
    // tambahan rizki redmine #815
    jQuery.fn.popupAccountCustomerNotInFa2 = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.accountsByCustomerNotInFa()}', '@{Pick.accountByCustomer2()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCustomer = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customers()}', '@{Pick.customer()}', expression, nextobjid, func, funcerr);
    };

    jQuery.fn.popupCustomerNonRetail = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customersNonRetail()}', '@{Pick.customerNonRetail()}', expression, nextobjid, func, funcerr);
    };

    jQuery.fn.popupCustomerNonRetailBilling = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customersNonRetailBilling()}', '@{Pick.customerNonRetail()}', expression, nextobjid, func, funcerr);
    };

    jQuery.fn.popupCustomerNotInFa = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customersNotInFa()}', '@{Pick.customerPickNotInFA()}?domain=CUSTOMER', expression, nextobjid, func, funcerr);
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
    
    jQuery.fn.popupFaFund = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.faFundSetups()}', '@{Pick.faFundSetup()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupFaFundWithProfile = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.faFundProfileSetups()}', '@{Pick.faFundProfileSetup()}', "", nextobjid, func, funcerr);
    };

    jQuery.fn.popupInvestmentAcct = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgInvestmentAccts()}', '@{Pick.rgInvestmentAcct()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupInvestmentByCustomer = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgInvestmentByCustomers()}', '@{Pick.rgInvestmentByCustomer()}', expression, nextobjid, func, funcerr);
    };
     
    jQuery.fn.popupInvestmentByProduct = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgInvestmentByProducts()}', '@{Pick.rgInvestmentByProduct()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupCustomerInvestment = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customerInvestments()}', '@{Pick.customerInvestment()}', "", nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccountInvt = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgBankAccounts()}', '@{Pick.rgBankAccount()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.popupBankAccountProd = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgProdBankAccounts()}', '@{Pick.rgProdBankAccount()}', expression, nextobjid, func, funcerr);
    };

    jQuery.fn.popupProductByEffLiqDate = function(nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.rgProductByEffDateAndLiqDates()}', '@{Pick.rgProductByEffDateAndLiqDate()}', "", nextobjid, func, funcerr);
    };

    jQuery.fn.popupFundAccount = function(code, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccountsByRgProductBnAccounts()}?productCode='+code, '@{Pick.bankAccountProductByAccNo()}?productCode='+code, "", nextobjid, func, funcerr);
    };

    jQuery.fn.popupFundAccountDomain = function(code, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccountsByRgProductBnAccountsDomain()}?productCode='+code, '@{Pick.bankAccountProductByAccNoAndDomain()}?productCode='+code, "", nextobjid, func, funcerr);
    };

    jQuery.fn.popupInvestorBank = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.bankAccountsByAccountNumberCurrency()}', '@{Pick.bankAccountByInvestmentProductCurrAccNo()}', expression, nextobjid, func, funcerr);
    };
    
    jQuery.fn.dynapopup = function(lookupId, filter, nextobjid, func, funcerr) {
    	var param = {
    		"lookupId" : lookupId,
    		"filter" : filter
    	};
    	
    	$(this).popuppaging('@{Pick.pickGrid()}', '@{Pick.pick()}', param, nextobjid, func, funcerr);
    };
    
    jQuery.fn.dynapopup2 = function(obj, lookupId, filter, nextobjid, func, funcerr) {
    	var param = {
    		"lookupId" : lookupId,
    		"filter" : filter
    	};
    	
    	$(this).popuppaging2(obj, '@{Pick.pickGrid()}', '@{Pick.pick()}', param, nextobjid, func, funcerr);
    };

    
    jQuery.fn.reportdynapopup = function(lookupId, filter, nextobjid, func, funcerr, formId) {
    	var param = {
    		"lookupId" : lookupId,
    		"filter" : filter,
    		"formId" : formId
    	};
    	if( formId ){
    		$( this ).data("formId", formId);
    	}
    	$(this).popuppaging('@{Pick.pickGrid()}', '@{Pick.pick()}', param, nextobjid, func, funcerr);
    };
    
    jQuery.fn.enters = function(components){
    	for (i = 0; i < components.length-1; i++) {
    		var from = $("#"+components[i], $(this));
    		from.data("to", components[i+1]);
    		from.bind("keyup", function(e){
    			if (e.keyCode == 13) {
    				e.preventDefault();
					var to = $("#"+$(this).data("to"));	
					to.focus();
					to.select();
					$(this).hover();
    			}
    		});
    	}
    };
    
    jQuery.fn.loading = function() {
    	$("#parentDiv").attr("class", "screenMask");
    	$("#loadingLogo").attr("style", "display:block");
    	$("div .pane").addClass("screenMask");
    	$("div .pane").parent("div").addClass("screenMask");
    };
    
    // Getter to server
    jQuery.fn.getBankAccountByInvestmentProductCurrAccNo = function(productCode, code, customerKey, func) {
    	return $().fetchSync("@{Pick.bankAccountByInvestmentProductCurrAccNo()}", {'currencyCode':productCode, 'code':code, 'customerKey':customerKey}, func);
    };
    
    jQuery.fn.getRgTax = function(code, func) {
    	return $().fetchSync("@{Pick.taxMaster()}", {'code':code}, func);
    };
    
    jQuery.fn.getRgTaxKey = function(taxKey, func) {
    	return $().fetchSync("@{Pick.taxMasterKey()}", {'taxKey':taxKey}, func);
    };
    
    jQuery.fn.getRgProduct = function(code, func) {
    	return $().fetchSync("@{Pick.rgProduct()}", {'code':code}, func);
    };

    jQuery.fn.getRgPortfolio = function(productCode, accountnumber, holdingdate, func) {
    	return $().fetchSync("@{Pick.rgPortfolio()}", {'productCode':productCode, 'accountnumber':accountnumber, 'holdingdate':holdingdate}, func);
    };
    
    jQuery.fn.getVwFundUnitBal = function(productCode,  holdingdate, func) {
    	return $().fetchSync("@{Pick.getVwFundUnitBal()}", {'productCode':productCode, 'holdingdate':holdingdate}, func);
    };

    jQuery.fn.getPortfolioTotalUnit = function(productCode, holdingdate, func) {
    	return $().fetchSync("@{Pick.rgPortfolioUnit()}", {'productCode':productCode, 'holdingdate':holdingdate}, func);
    };
    
    jQuery.fn.getPortfolioTotalUnitMaturity = function(productCode, postDate, navDate, func) {
    	return $().fetchSync("@{Pick.calcTotalUnitMaturity()}", {'productCode':productCode, 'postDate':postDate, 'navDate':navDate }, func);
    };

    jQuery.fn.getPortfolioTotalUnitAccount = function(accountNumber, holdingdate, func) {
    	return $().fetchSync("@{Pick.rgPortfolioUnitAccount()}", {'accountNumber':accountNumber, 'holdingdate':holdingdate}, func);
    };

    jQuery.fn.getUnitFromDwnBal = function(productCode, tradeDate, func) {
    	return $().fetchSync("@{Pick.getUnitFromDwnBal()}", {'productCode':productCode, 'tradeDate':tradeDate}, func);
    };

    jQuery.fn.getRgInvestmentAcct = function(type, code, func) {
    	return $().fetchSync("@{Pick.rgInvestmentAcct()}", {'type':type, 'code':code}, func);
    };
    
    jQuery.fn.getRgInvestmentByProd = function(productCode, code, func) {
    	return $().fetchSync("@{Pick.bankAccountProductByAccNo()}", {'productCode':productCode, 'code':code}, func);
    };

    jQuery.fn.getRgInvestmentByProdDomain = function(productCode, domain, code, func) {
    	return $().fetchSync("@{Pick.bankAccountProductByAccNoAndDomain()}", {'productCode':productCode, 'domain':domain, 'code':code}, func);
    };

    jQuery.fn.getRgNav = function(navDate, productcode, func) {
    	return $().fetchSync("@{Pick.rgNav()}", {'navDate':navDate, 'productcode':productcode}, func);
    };
    
    jQuery.fn.getPriceByRgNav = function(navDate, productcode, func) {
    	return $().fetchSync("@{Pick.priceByNav()}", {'navDate':navDate, 'productcode':productcode}, func);
    };
    
    jQuery.fn.getRgFeeTier = function(productcode, type, amount, func) {
		return $().fetchSync("@{Pick.rgFeeTier()}", {'productcode':productcode, 'type':type, 'amount':amount}, func);
    };
    
    jQuery.fn.getWorkingDate = function(yyyyMMdd, day, func) {
    	return $().fetchSync("@{Pick.addWorkingDate()}", {'yyyyMMdd':yyyyMMdd, 'day':day+""}, func);
    };
    
    jQuery.fn.getApplicationDate = function(func) {
    	return $().fetchSync("@{Pick.getApplicationDate()}", {}, func); 
    };
    
    jQuery.fn.addCutOfTime = function(yyyyMMdd, hour, func) {
    	return $().fetchSync("@{Pick.addCutOfTime()}", {'yyyyMMdd':yyyyMMdd, 'hour':hour+""}, func);
    };
    
    jQuery.fn.getRgFeeAmount = function(productcode, type, amount, inputBy, func) {
    	return $().fetchSync("@{Pick.rgFeeAmount()}", {'productcode':productcode, 'type':type, 'amount':amount, 'inputBy':inputBy}, func);
    };
    
	jQuery.fn.simpleDialog = function(title, message, func){
		var div = $("<div title='"+title+"'>").appendTo($(this));
		
		var p = $("<p>").appendTo(div);
		
		var span = $("<span style='float:left; margin:0 7px 50px 0;'>").appendTo(div);
		p.append(message);
		
		var dialog = div.dialog({
			autoOpen:false,
			modal:true,
			heigth:'600px',
			width:'465px',
			resizable:false,
			buttons: {
				"Close": function() {
					if (typeof func == 'function') { func(); }
				}	
			}
		});
		
		return dialog;
	};
	
	jQuery.fn.progressDialog = function(parent){
    	if (parent == null || parent == undefined) {
    		parent = 'body';
    	}
    	
		var loading = $('<div id="loading" class="loading"><img src="@@{'/public/images/loading2.gif'}" style="margin:10px" align="middle" /> Proccessing...</div>').appendTo(parent);
		loading.dialog({
			closeOnEscape: false,
			draggable: false,
			modal: true,
			resizable: false,
			open:function() {
				$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
			} 
		}).dialog("close");
		
		setInterval( function(){
			if (loading.dialog("isOpen")) {
//				alert("oooiii");
			} 
		}, 1000);
		
		return loading;
    };
    
    jQuery.fn.loadingDialog = function(parent){
    	if (parent == null || parent == undefined) {
    		parent = 'body';
    	}
    	
		var loading = $('<div id="loading" class="loading"><img src="@@{'/public/images/loading2.gif'}" style="margin:10px" align="middle" /> Proccessing...</div>').appendTo(parent);
		loading.dialog({
			closeOnEscape: false,
			draggable: false,
			modal: true,
			resizable: false,
			open:function() {
				$(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();
			} 
		}).dialog("close");
		return loading;
    };
    
    //Getter Third Party
    jQuery.fn.getGnThdPrty = function(code, func) {
    	var returnData;
    	$.ajaxSetup({ async : false });
		$.get("@{Pick.thirdPartyByKey()}", {'key':code}, function(data) {
			checkRedirect(data);
			
			returnData = data;
			if (typeof func == 'function') {
				func(data);
			}
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
    		return arr[2] + arr[1] + arr[0];
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
        
        //return month + seperator + day + seperator + year;
        return day + seperator + month + seperator + year;
    };
    
    String.prototype.toDate = function(){
        if (this == null || this.length != 10) 
            return "";
        var arr = this.split("/");
        var date = new Date();
        date.setFullYear(Number(arr[2]), Number(arr[0]) - 1, Number(arr[1]));
        return date;
    };
    
    Number.prototype.fmtNumber = function(length){
    	var input = $("<input>").autoNumericSet(this.valueOf(), {vMax: '999999999999999999999.999999999', vMin: '-999999999999999999999.999999999', mDec: length, aPad: true});
    	return $(input).val();
    }
    
    Number.prototype.leadingZero = function(length){
        var loop = (length == null) ? 0 : length - (this.valueOf() + "").length;
        
        var value = this.valueOf();
        for (i = 0; i < loop; i++) {
            value = "0" + value;
        }
        return value;
    };
    
    Number.prototype.noExponents= function(){
        var data= String(this).split(/[eE]/);
        if(data.length== 1) return data[0]; 

        var  z= '', sign= this<0? '-':'',
        str= data[0].replace('.', ''),
        mag= Number(data[1])+ 1;

        if(mag<0){
            z= sign + '0.';
            while(mag++) z += '0';
            return z + str.replace(/^\-/,'');
        }
        mag -= str.length;  
        while(mag--) z += '0';
        return str + z;
    };
     
    Number.prototype.round_half_down = function(precision) {
        var x = this.sliceDigit(precision);
        var y = this.sliceDigit(0);
        if (Number(x) - Number(y) > Number(0.50))
        	return Math.ceil(this);
        else 
        	return Math.floor(this);
    };
    
    Number.prototype.sliceDigit = function(precision) {
    	var num = this.toString();
    	num = num.slice(0, (num.indexOf("."))+precision);
    	return Number(num);
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
        return this.getDate().leadingZero(2) + tag + (this.getMonth() + 1).leadingZero(2) + tag + this.getFullYear();
    };
    
    Date.prototype.fmtDateDMY = function(seperator){
        var tag = (seperator == null) ? "" : seperator;
        return this.getDate().leadingZero(2) + tag + (this.getMonth() + 1).leadingZero(2) + tag + this.getFullYear();
    };
    
    Date.prototype.fmtPattern = function(pattern){
    	var valDate = this.getDate().leadingZero(2);
    	var valMonth = (this.getMonth() + 1).leadingZero(2);
    	var valYear = this.getFullYear();
    	pattern = pattern.replaceAll("dd", valDate);
    	pattern = pattern.replaceAll("MM", valMonth);
    	pattern = pattern.replaceAll("yyyy", valYear);
    	return pattern;
    };
    
    jQuery.fn.calc = function(dispatch, parmeters) {
    	var returnData;
    	$.ajaxSetup({ async : false });
    	
		$.get(dispatch, parmeters, function(data) {
			checkRedirect(data);
			returnData = data;
		});
		return returnData;
    };
    
    jQuery.fn.fetchSync = function(url, param, func) {
    	var returnData = null;
		$.ajaxSetup({ async : false });
    	$.get(url, param, function(data) {
    		checkRedirect(data);
    		
    		returnData = data;
			if (typeof func == 'function') { func(data); }
    	});
    	return returnData;
    }
    
    jQuery.fn.fetchAsync = function(url, param, func) {
    	var returnData = null;
		$.ajaxSetup({ async : true });
    	$.get(url, param, function(data) {
    		checkRedirect(data);
    		
    		returnData = data;
			if (typeof func == 'function') { func(data); }
    	});
    	return returnData;
    }
    
    jQuery.fn.submitAsync = function(url, param, func) {
    	var returnData = null;
		$.ajaxSetup({ async : true });
    	$.post(url, param, function(data) {
    		checkRedirect(data);
    		
    		returnData = data;
			if (typeof func == 'function') { func(data); }
    	});
    	return returnData;
    }
    
    jQuery.fn.fetchAsyncTimeout = function(purl, pparam) {
    	$.ajax({
    		async : true, 
    	    url: purl,
    	    data : pparam,
    	    error: function(){
//    	       console.log('Time out accured');
    	    },
    	    timeout: 3000 // sets timeout to 3 seconds
    	});
    }
    
    jQuery.fn.fetch = function(url, param, func) {
    	var returnData = null;
		$.ajaxSetup({ async : false });
    	$.get(url, param, function(data) {
    		checkRedirect(data);
    		
    		returnData = data;
			if (typeof func == 'function') { func(data); }
    	});
    	return returnData;
    }
    
    jQuery.fn.fetchForm = function(purl, func) {
    	// INI MASIH BLM SUCCESS
    	var returnData = null;
    	
    	$.ajaxSetup({ async : false });
    	$(this).ajaxForm({
			dataType: "json",
			contentType: "application/x-www-form-urlencoded;charset=utf-8",
		    url: purl, 
		    success: function(data, status, jxhr, form) {
		    	alert(data);
		    	returnData = data;	
		    	if (typeof func == 'function') { func(data); }
		    },
		    error: function(jxhr, status){
		    	alert(status);
		    	throw 'Fail To fetch server';
		    }
		}).submit();
    	
    	return returnData;
    }
    
    jQuery.fn.tag = function(name) {
    	if ($(this).data(name) == undefined) {
    		$(this).data(name, $("#"+name, $(this)));	
    	}
    	
    	return $(this).data(name);
    }
    
    jQuery.fn.keep = function(pointer) {
    	var p = $(pointer[0].outerHTML).appendTo($(this)).removeAttr("disabled");
    	return p;
    }
    
    jQuery.fn.local = function() {
    	var tableTag = $(this);
    	tableTag.dataTable({
    		"bJQueryUI": true,
//	        "bPaginate": false,
	        "bLengthChange": true,
	        "bDestroy":true,
			"sPaginationType": "full_numbers",
//	        "bFilter": true,
	        "bSort": true,
//	        "bInfo": false,
//	        "bProcessing": true,
//	        "bAutoWidth": true,
//	        "bServerSide": true,
//	        "aoColumns": sorting,
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true
//			"sAjaxSource": url,
			
			
    	});
    	return tableTag;
    };
    
    jQuery.fn.convertRow = function(data, columns, datepattern, decode, decodeData) {
    	var row = [];
    	var myTable = $(this);
    	
    	for (x in columns) {
			try{
				// bila field component tidak ambil dari kiriman server tapi ambil sample dari header
				if (columns[x].field == "component") {
					var compo = $(columns[x].header);
					compo.removeAttr("id");
					row[x] = compo[0].outerHTML; 
				//}else{ row[x] = eval("data."+columns[x].field); }
				}else{ 
					var tFields = columns[x].field.split(".");
					var evalFilter = "";
					for (z in tFields) {
						evalFilter += "['"+tFields[z]+"']";
					}
					row[x] = eval("data"+evalFilter); 
				}
				
				if (row[x] == undefined) row[x] = null; 
				
				// memformat data bial datanya tidak null
				if (row[x] != null) {
					if (columns[x].type == "string") {
						if (columns[x].format == "decode") { row[x] = eval("decode."+row[x]); } 
						if (columns[x].format == "decodeData") { row[x] = eval("decodeData."+row[x]); }
					}
					
					if (columns[x].type == "number") {
						if (columns[x].format == "decimal0") { row[x] = row[x].fmtNumber(0); }
						if (columns[x].format == "decimal1") { row[x] = row[x].fmtNumber(1); }
						if (columns[x].format == "decimal2") { row[x] = row[x].fmtNumber(2); }
						if (columns[x].format == "decimal3") { row[x] = row[x].fmtNumber(3); }
						if (columns[x].format == "decimal4") { row[x] = row[x].fmtNumber(4); }
					}
					
					if (columns[x].type == "date") {
						row[x] = row[x].replaceAll("Mei", "May");
						row[x] = row[x].replaceAll("Agu", "Aug");
						row[x] = row[x].replaceAll("Okt", "Oct");
						row[x] = row[x].replaceAll("Des", "Dec");
						
						if (columns[x].format == "date") { row[x] = (new Date(row[x])).fmtPattern(datepattern); }
					}
					
					if (columns[x].type == "datetime") {}
					
					if (columns[x].type == "boolean") {
						if (columns[x].format == "yesno") {  row[x] = (row[x]) ? "Yes" : "No"; }
					}
					
					if (columns[x].type == "component") { }
				}
			}catch(err) { row[x] = null; }
		}
    	return row;
    }
    
    jQuery.fn.eventRow = function(beans, rows, columns) {
    	var myTable = $(this);
    	
		$("tbody tr", myTable).each(function(i, e){
			var tr = $(this).data("prop", {"bean" : beans[i], "row" : rows[i], "tr" : $(this)});
			
			$("td", $(this)).each(function(j, e){
				if (columns[j].field != "component") {
					$(this).click(function(){
						if (!$(this).hasClass("dataTables_empty")) {
							myTable.trigger("select", [tr.data("prop")]);
						}
					});
				}else{
					$("input[type=checkbox]", this).change(function(){
						var condition = ($(this).checked() == "false") ? false : true;
						myTable.trigger("checkbox", [tr.data("prop"), condition]);
					});	
				}

				if ($(this).hasClass("dataTables_empty")) {
					$(this).css("text-align","center");
				}else{
					$(this).css("text-align", columns[j].align);	
				}
			})
		});
    }
    
    jQuery.fn.checkboxRow = function(columns) {
    	var myTable = $(this);
    	
		// pointer header type mechanism
		for (x in columns) {
			// checkbox header mechanism
			if (columns[x].field == "component" && columns[x].type == "checkbox") {
				var hcheckbox = columns[x].pointer;
				hcheckbox.data("columnNo", x);
				
				$("tbody tr td:nth-child("+(x+1)+") input[type=checkbox]:enabled", myTable).each(function(i, e){
					$(this).click(function(){
						if ($(this).checked() == "false") { hcheckbox.removeAttr("checked"); }
					})
				});
				
				hcheckbox.change(function(){
					var x = $(this).data("columnNo");
					
					var checkboxs = $("tbody tr td:nth-child("+(x+1)+") input[type=checkbox]:enabled", myTable);
					if ($(this).checked() != "false") { 
						checkboxs.attr("checked", $(this).checked);
					}else { 
						checkboxs.removeAttr("checked");
					}
				});
			}  
		}
    }
    
    jQuery.fn.bindFetch = function(columns, filters) {
		// ambil data berdasarkan kriteria yang di minta oleh user
    	var myTable = $(this);
    	
		myTable.bind("fetch", function(event, column, filter){
			var idx = jQuery.inArray(filter, filters);
    		if (idx == -1) alert("Invalid Filter name "+filter);
    		
			var props = [];
			$("tbody tr", myTable).each(function(i, e){
				var component = $("td:nth-child("+(column+1)+") input[type=checkbox]", $(this));
				var prop = $(this).data("prop");

				if ((component.checked() != "false") && (filter == "checked")) { props.push(prop); }
				if ((component.checked() == "false") && (filter == "unchecked")) { props.push(prop); }
			});
			myTable.trigger("selects", [props]);
		});
		
		$("thead tr th:first input[type=checkbox]", myTable).change(function(){
			var props = [];
			var condition = ($(this).checked() == "false") ? false : true;
			
			$("tbody tr", myTable).each(function(i, e){
				var component = $("td:nth-child(1) input[type=checkbox]", $(this));
				var prop = $(this).data("prop");

				props.push(prop);
			});
			myTable.trigger("checkboxs", [props, condition]);
		});
    }
    
    jQuery.fn.getDecode = function(columns) {
    	var decode = false;
    	for (x in columns) {
    		if (decode == false && columns[x].format == "decode") { decode = true; }
    	}
    	
    	if (decode) {decode = $().fetchSync("@{Pick.getDecode()}");}
    	return decode;
    }
    
    jQuery.fn.getDecodeData = function(columns) {
    	var decodeData = false;
    	
    	for (x in columns) {
    		if (decodeData == false && columns[x].format == "decodeData") { decodeData = true; }
    	}
    	
    	if (decodeData) {decodeData = $().fetchSync("@{Pick.getDecodeData()}");}

    	return decodeData;
    }
    
    function bindTableFunction(myTable) {
    	
    	myTable.findBean = function(property, pvalue) {
    		var result = [];
    		$("tbody tr", myTable).each(function(i, e){
    			var prop = $(this).data("prop");
    			var data = prop.bean;
    			
				var tFields = property.split(".");
				var evalFilter = "";
				for (z in tFields) {
					evalFilter += "['"+tFields[z]+"']";
				}
				var value = eval("data"+evalFilter);
				if (pvalue instanceof Object) {
					var isSame = true;
					for (field in pvalue) {
						var valO = pvalue[field];
						var valB = value[field];
						if (valO != valB) isSame = false;
					}
					if (isSame) { result.push(prop); }
				}else{
					if (value == pvalue) { result.push(prop); }	
				}
    		});
    		return result;
    	};
    	
    	myTable.selects = function(column, filter) {
    		var props = [];
    		$("tbody tr", myTable).each(function(i, e){
    			var component = $("td:nth-child("+(column+1)+") input[type=checkbox]", $(this));
    			var prop = $(this).data("prop");

    			if (filter == 'all') {
    				props.push(prop);
    			}else{
	    			if ((component.checked() != 'false') && (filter == 'checked')) { props.push(prop); }
	    			if ((component.checked() == 'false') && (filter == 'unchecked')) { props.push(prop); }
    			}
    		});
    		return props;
    	}
    }
    
    jQuery.fn.tableInit = function() {
    	// pointer
    	var tableTag = $(this);
    	
    	// variable
    	var columns = [];
    	var sorting = [];
    	var ordering = [];
    	var dateData = false;
    	
    	// array
    	var types = ["string", "number", "date",  "boolean", "checkbox", "button"];
    	var formats = ["none", "date", "datetime", "yesno", "decode","decodeData", "decimal0", "decimal1", "decimal2", "decimal3", "decimal4"];
    	var aligns = ["left", "right", "center"];
    	var filters = ["checked", "unchecked"];
    	var sorts = ["sort", "nosort", "sortdasc", "sortddesc"];
    	
    	// looping per tr
    	$("thead tr", $(this)).children().each(function(z, e){
    		var props = $(this).attr("field").split("|");
    		if (props.length != 5) alert("Invalid Property size");
    		
    		var idx = jQuery.inArray(props[1], types);
    		if (idx == -1) alert("Invalid data type "+props[1]);
    		
    		var idx = jQuery.inArray(props[2], formats);
    		if (idx == -1) alert("Invalid data format "+props[2]);
    		
    		var idx = jQuery.inArray(props[3], aligns);
    		if (idx == -1) alert("Invalid data align "+props[3]);
    		
    		var idx = jQuery.inArray(props[4], sorts);
    		if (idx == -1) alert("Invalid data sort "+props[4]);
    		
    		if (dateData == false && props[1] == "date") { dateData = true; }
    		
    		columns[columns.length] = {
    			"field" : props[0],
    			"type" : props[1],
    			"format" : props[2],
    			"align" : props[3],
    			"sort" : props[4],
    			"header" : $(this).html(),
    			"pointer": $("input", $(this))
    		};
    		
    		sorting.push({ "bSortable": (props[4] != "nosort") });
    		if (props[4] == "sortdasc") ordering.push([z, "asc"]);
    		if (props[4] == "sortddesc") ordering.push([z, "desc"]);
    	});
    	
    	var property = {
    		"columns" : columns,	
    		"sorting" : sorting,
    		"ordering" : ordering,
    		"dateData" : dateData,
    		"types" : types,
    		"formats" : formats,
    		"aligns" : aligns,
    		"filters" : filters,
    		"sorts" : sorts
    	};
    	return property;
    }
    
    jQuery.fn.paging = function(url, js, func, options) {
    	var tableTag = $(this);
    	
    	if (options == null || options == undefined) { options = new Object(); }
    	if (!options.sScrollXInner) options.sScrollXInner = "100%";
    	if (!options.sPaginationType) options.sPaginationType = "full_numbers";
    	if (!options.defaultSearch) options.defaultSearch = '';
    	if (!options.showLoading) options.showLoading = "true"; // ini harus string, kalo boolean gax jalan gax tau juga
    	
    	var props = $(this).tableInit();
    	props.decode = $(this).getDecode(props.columns);
    	props.decodeData = $(this).getDecodeData(props.columns);
    	
    	var showLoading = false;
    	
    	// create data table
    	$(this).hide();
		var myTable = $(this).dataTable({
			"bJQueryUI": true,
//	        "bPaginate": false,
	        "bLengthChange": true,
			"sPaginationType": options.sPaginationType,
//	        "bFilter": true,
	        "bSort": true,
//	        "bInfo": false,
	        "bProcessing": true,
//	        "bAutoWidth": false,
	        "bServerSide": true,
	        "aoColumns": props.sorting,
//			"sScrollX": "100%",
//			"sScrollXInner": options.sScrollXInner,
			"bScrollCollapse": true,
			"sAjaxSource": url,
			"aaSorting":props.ordering,
			"fnDrawCallback": function() {
				$("input[type=checkbox]", $(tableTag)).removeAttr("checked");
				for (x in props.columns) {
					if (props.columns[x].field == "component" && props.columns[x].type == "checkbox") {
						props.columns[x].pointer.removeAttr("checked");
					}					
				}  
			},
			"fnServerData": function(sSource, aoData, fnCallback) {
				// ambil data yang akan di kirim ke server
		    	var page = new Object();
		    	
		    	// set nilai default search untuk pertama kalinya
		    	if (options.defaultSearch != '') {
		    		$("#"+tableTag.attr("id")+"_filter input").val(options.defaultSearch);
		    		options.defaultSearch = '';
		    	}
		    	
		    	for (x in aoData) {
		    		if (aoData[x].name) {
			    		if (aoData[x].name == 'sEcho') page.sEcho = aoData[x].value;
			    		if (aoData[x].name == 'iDisplayStart') page.iDisplayStart = aoData[x].value;
			    		if (aoData[x].name == 'iDisplayLength') page.iDisplayLength = aoData[x].value;
			    		if (aoData[x].name == 'sSearch') page.sSearch = $("#"+tableTag.attr("id")+"_filter input").val();
			    		if (aoData[x].name == 'sSortDir_0') page.sSortDir_0 = aoData[x].value.toUpperCase(); // asc or desc
			    		if (aoData[x].name == 'iSortCol_0') {
			    			page.iSortCol_0 = aoData[x].value;
			    			page.sortName = props.columns[page.iSortCol_0].field;
			    		}
		    		}
		    	}
		    	if (options.showLoading == "true") {
			    	var loadingDialog = $(this).loadingDialog();
			    	if (showLoading) loadingDialog.dialog("open");
		    	}
		    	$.ajaxSetup({ async : false });
				$.get(url, {'page':page, 'param':js.parameter("paging")}, function(data) {
					checkRedirect(data);
					
					if (props.dateData && (data.dateFormat != null && data.dateFormat.length == 0)) { alert("Please provide DateFormat at Server Side"); }
					
					var records = [];
					for (y in data.data) {
						records[y] = $(tableTag).convertRow(data.data[y], props.columns, data.dateFormat, props.decode, props.decodeData);
					}
					
					// tampung hasil dan callback ke datatable
					fnCallback( {"sEcho" : data.sEcho, "iTotalRecords" : data.iTotalRecords, "iTotalDisplayRecords" : data.iTotalDisplayRecords, "aaData" : records });
					
					$(tableTag).eventRow(data.data, records, props.columns);
					$(tableTag).checkboxRow(props.columns);
					if (typeof func == 'function') { func(data); }
					
					if (options.showLoading == "true") {
						loadingDialog.dialog("close");
						showLoading = true;
					}
				});
			}
		});
		
		myTable.show();
		var divWraper = $("<div style='overflow:auto'>");
		
		myTable.after(divWraper);
		divWraper.append(myTable);
		
		myTable.bindFetch(props.columns, props.filters);
		bindTableFunction(myTable);
		
		// hapus all event on search textbox, dan pasang event enter
		myTable.search = $("#"+tableTag.attr("id")+"_filter input");
		myTable.search.addClass("capitalize");
		myTable.search.unbind()
		.keyup(function(event){
			if (event.which == 13) {
				myTable.fnPageChange("first");
			}
		});
		
		return myTable;
    }
    
    jQuery.fn.nopaging = function(url, js, func, options) {
    	var tableTag = $(this);
    	
    	if (options == null || options == undefined) { options = new Object(); }
    	if (!options.sScrollXInner) options.sScrollXInner = "100%";
    	if (!options.defaultSearch) options.defaultSearch = '';
    	if (!options.showLoading) options.showLoading = "true";
    	
    	var props = $(this).tableInit();
    	props.decode = $(this).getDecode(props.columns);
    	props.decodeData = $(this).getDecodeData(props.columns);
    	
    	var showLoading = false;
    	
    	// create data table
//    	$(this).hide();
		var myTable = $(this).dataTable({
			"bJQueryUI": true,
	        "bPaginate": false,
	        "bLengthChange": true,
			//"sPaginationType": "full_numbers",
	        "bFilter": false,
	        "bSort": true,
//	        "bInfo": false,
	        "bProcessing": true,
//	        "bAutoWidth": true,
	        "bServerSide": true,
	        "aoColumns": props.sorting,
			"sScrollX": "100%",
			"sScrollXInner": options.sScrollXInner,
			"bScrollCollapse": true,
			"sAjaxSource": url,
			"sScrollY": "320px",
			"aaSorting":props.ordering,
			"fnDrawCallback": function() {
				$("input[type=checkbox]", $(tableTag)).removeAttr("checked");
				for (x in props.columns) {
					if (props.columns[x].field == "component" && props.columns[x].type == "checkbox") {
						props.columns[x].pointer.removeAttr("checked");
					}					
				}  
			},
			"fnServerData": function(sSource, aoData, fnCallback) {
				// ambil data yang akan di kirim ke server
		    	var page = new Object();
		    	
		    	if (options.defaultSearch != '') {
		    		$("#"+tableTag.attr("id")+"_filter input").val(options.defaultSearch);
		    		options.defaultSearch = '';
		    	}
		    	
		    	for (x in aoData) {
		    		if (aoData[x].name) {
			    		if (aoData[x].name == 'sEcho') page.sEcho = aoData[x].value;
			    		if (aoData[x].name == 'iDisplayStart') page.iDisplayStart = aoData[x].value;
			    		if (aoData[x].name == 'iDisplayLength') page.iDisplayLength = aoData[x].value;
			    		if (aoData[x].name == 'sSearch') page.sSearch = $("#"+tableTag.attr("id")+"_filter input").val();
			    		if (aoData[x].name == 'sSortDir_0') page.sSortDir_0 = aoData[x].value.toUpperCase(); // asc or desc
			    		if (aoData[x].name == 'iSortCol_0') {
			    			page.iSortCol_0 = aoData[x].value;
			    			page.sortName = props.columns[page.iSortCol_0].field;
			    		}
		    		}
		    	}

		    	if (options.showLoading == "true") {
			    	var loadingDialog = $(this).loadingDialog(tableTag);
			    	if (showLoading) loadingDialog.dialog("open");
		    	}
		    	$.ajaxSetup({ async : false });
				$.get(url, {'page':page, 'param':js.parameter("paging")}, function(data) {
					checkRedirect(data);
					
					if (props.dateData && (data.dateFormat != null && data.dateFormat.length == 0)) { alert("Please provide DateFormat at Server Side"); }
					
					var records = [];
					for (y in data.data) {
						records[y] = $(tableTag).convertRow(data.data[y], props.columns, data.dateFormat, props.decode, props.decodeData);
					}
					
					// tampung hasil dan callback ke datatable
					fnCallback( {"sEcho" : data.sEcho, "iTotalRecords" : data.iTotalRecords, "iTotalDisplayRecords" : data.iTotalDisplayRecords, "aaData" : records });
					
					$(tableTag).eventRow(data.data, records, props.columns);
					$(tableTag).checkboxRow(props.columns);
					if (typeof func == 'function') { func(data); }
					
					if (options.showLoading == "true") {
						loadingDialog.dialog("close");
						showLoading = true;
					}
				});
			}
		});
		
		myTable.bindFetch(props.columns, props.filters);
		bindTableFunction(myTable);
		
		// hapus all event on search textbox, dan pasang event enter
		myTable.search = $("#"+tableTag.attr("id")+"_filter input");
		myTable.search.addClass("capitalize");
		myTable.search.unbind()
		.keyup(function(event){
			if (event.which == 13) {
				myTable.fnPageChange("first");
			}
		});
		
		return myTable;
    }
    
    jQuery.fn.offlinepaging = function(options) {
    	
    	var tableTag = $(this);    	
    	if (options == null || options == undefined) { options = new Object(); }
    	//if (!options.sScrollXInner) options.sScrollXInner = "100%";
    	if(!options.dateFormat){
    		options.datFormat = "yyyyMMdd";
    	}
    	
    	var convertRow = {};
    	var eventRow = {};
    	
    	if( options.convertRow ){
        	convertRow = options.convertRow;
    	}
    	if( options.eventRow ){
    		eventRow = options.eventRow;
    	}
    	
    	var bFilter = true;
    	if( options.bFilter != undefined){
    		bFilter = options.bFilter;
    	}	
    	var props = $(this).tableInit();
    	props.decode = $(this).getDecode(props.columns);
    	props.decodeData = $(this).getDecodeData(props.columns);
    	
    	var dataTableOptions = {
    			"bJQueryUI": true,
    	        "bPaginate": true,
    	        "bLengthChange": true,
    			"sPaginationType": "full_numbers",
    			"bFilter": bFilter,
    	        "bSort": true,
    	        "bDestroy":true,
    	        "bProcessing": true,
    	        "bAutoWidth": true,
    	        "aoColumns": props.sorting,
    			"sScrollX": "100%",
    			"sScrollXInner": options.sScrollXInner,
    			"bScrollCollapse": true,
    			"aaSorting":props.ordering,
    			"fnDrawCallback": function(oSettings) {
    				$("input[type=checkbox]", $(tableTag)).removeAttr("checked");
    				for (x in props.columns) {
    					if (props.columns[x].field == "component" && props.columns[x].type == "checkbox") {
    						props.columns[x].pointer.removeAttr("checked");
    					}
    				}
    	    		// then call event row...
    	    		if( typeof eventRow == "function"){
    	    			eventRow.call(this, props.columns);
    				}
    			}
    	};
    	var iDisplayLength = 0;
    	if( options.iDisplayLength != undefined){
    		iDisplayLength = options.iDisplayLength;
    		dataTableOptions["iDisplayLength"] = iDisplayLength;
    	}
    	var bPaginate = true;
    	if( options.bPaginate != undefined){
    		bPaginate = options.bPaginate;
    		dataTableOptions["bPaginate"] = bPaginate;
    	}
    	// create data table
		var myTable = tableTag.dataTable( dataTableOptions );
		
		myTable.bindFetch(props.columns, props.filters);
		bindTableFunction(myTable);		
		
		// reload data with new array data
		myTable.addData = function(data){
			var records = [];
    		if( typeof convertRow == "function"){
    			records = convertRow.call(this, data);    				
			}else{
    			records = $(tableTag).convertRow(data, props.columns, options.dateFormat, props.decode, props.decodeData);    				
			}
			myTable.fnAddData( records, false );			
		}
		
		myTable.reload = function(data){
    		// clearing table
    		myTable.fnClearTable(true);
    		
    		// resetting data
    		var records = [];
    		for (y in data) {
    			if( typeof convertRow == "function"){
        			records[y] = convertRow.call(this, data[y]);    				
    			}else{
        			records[y] = $(tableTag).convertRow(data[y], props.columns, options.dateFormat, props.decode, props.decodeData);    				
    			}
    			myTable.fnAddData( records[y], false );
    		}
    		
    		// redraw please
    		myTable.fnDraw();			
    	};
		
		return myTable;
    }    
    jQuery.fn.radioButton = function(id, isYes) {
    	var yesPointer = $("#"+id+"1", $(this));
		var noPointer = $("#"+id+"2", $(this));
		
		yesPointer.removeAttr("checked");
		noPointer.removeAttr("checked");
		
		if (isYes) {
			yesPointer.attr("checked", "checked");
		}else{
			noPointer.attr("checked", "checked");
		}
		return $(this);
    } 
    
	jQuery.fn.behaviour = function(type, id, disabled) {
		 if (type == "radioButton") {
			 var yesPointer = $("#"+id+"1", $(this));
			 var noPointer = $("#"+id+"2", $(this));
			 
			 var isDisabled = yesPointer.attr("disabled");
			 if (isDisabled == disabled) {
				// do nothing khan sudah diabled 
			 }else{
				 if (disabled) {
					 yesPointer.attr("disabled", "disabled");
					 noPointer.attr("disabled", "disabled");
					 
					 var name = yesPointer.attr("name");
					 var yesCon = yesPointer.attr("checked"); 
					 var noCon = noPointer.attr("checked");
					 
					 var condition = (yesCon) ? true : (noCon ? false : false);
					 $("<input type='hidden' value='"+condition+"' name='"+name+"'>").appendTo(yesPointer.parent());
				 }else{
					 yesPointer.removeAttr("disabled");
					 noPointer.removeAttr("disabled");
					 
					 var hiddenP = $("input[type=hidden]", yesPointer.parent());
					 var name = hiddenP.attr("name");
					 yesPointer.attr("name", name);
					 noPointer.attr("name", name);
					 hiddenP.remove();
				 }
			 }
		 }
		 return $(this);
	};

	jQuery.fn.concat = function(messages, delimiter) {
		var message = "";
		for (x in messages) {
			if (message == "") {
				message = messages[x];
			}else{
				message += (delimiter+" "+messages[x]);	
			}
		}
		return message;
	}
	
	jQuery.fn.equal = function(compare) {
		return $(this).val() == compare;
	}
	
	jQuery.fn.link = function(compare) {
		var parent = $(this);
		$(this).change(function(){
			compare.trigger("link", {"val":$(this).val(), "comp":parent});
		});
		return $(this);
	}
	
	jQuery.fn.getRandomChar = function(charlength) {
		var text = "";
	    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	    for( var i=0; i < charlength; i++ )
	        text += possible.charAt(Math.floor(Math.random() * possible.length));

	    return text;
	};
	
	jQuery.fn.popupCustomerCashProjection = function(expression, nextobjid, func, funcerr) {
    	$(this).popup('@{Pick.customersCashProjection()}', '@{Pick.customerCashProjection()}?domain=CUSTOMER', expression, nextobjid, func, funcerr);
    };
	
	//http://dean.edwards.name/packer/ pack jadi min
})(jQuery);

///==fitriyadi
///untuk convert karakter ', ", > dkk. dikarenakan setvalue menjadi bentrok yang menyebabkan value selanjutnya terpotong atau valuenya rusak
function convertString(str){
	c = {'<':'&lt;', '>':'&gt;', '&':'&amp;', '"':'&quot;', "'":'&#039;',
		       '#':'&#035;' };
	return str.replace( /[<&>'"#]/g, function(s) { return c[s]; } );
}