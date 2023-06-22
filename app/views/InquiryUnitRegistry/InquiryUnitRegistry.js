function CancelTrade(html) {
	if (this instanceof CancelTrade) {

/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var TYPE_SUBSCRIBE = "Subscribe";
		var TYPE_REDEEM = "Redeem";
		var TYPE_SWITCHING = "Switching";
		var TYPE_DIVIDEND = "Dividend";
		
		var app = html.inject(this, true);

/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.rgProduct = new Object();
			p.rgProduct.productCode = app.fundCode.val();
			p.type = app.type.val();
			p.transactionNoOperator = app.transactionNoOperator.val();
			p.redemRefKey = app.redemRefKey.val();
			p.tradeDateFrom = app.tradeDateFrom.val();
			p.tradeDateTo = app.tradeDateTo.val();
			p.query = app.query.val();
			return p;
		};

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		html.clazz('calendar').datepicker();

		app.root.accordion({collapsible: true});

		app.search.add(app.reset).button();

		app.transactionNoOperator.children().eq(0).remove();		
		app.type.children().eq(0).remove();

		var wek = this;
		app.datatable = app.tblTransactionSubscribeRedeem.paging("@{InquiryUnitRegistry.pagingInquiryRegistryUnitSubscribe()}", wek);

/* =========================================================================== 
 * Method
 * ========================================================================= */		

		function changeLabelTransaction() {
			if(app.type.val() == TYPE_SUBSCRIBE || app.type.val() == TYPE_REDEEM)
			{
				$("label", app.transactionNoP).html("Transaction No.");
			}

			if(app.type.val() == TYPE_SWITCHING)
			{
				$("label", app.transactionNoP).html("Switching No.");
			}

			if(app.type.val() == TYPE_DIVIDEND)
			{
				$("label", app.transactionNoP).html("Dividend No.");
			}
		};
		
		function changeTableTransaction() {
			if(app.type.val() == TYPE_SUBSCRIBE)
			{
				if(app.datatable.attr("id") != app.tblTransactionSubscribeRedeem.attr("id"))
				{
					app.tblTransactionSubscribeRedeem.dataTable().fnDestroy();
					app.datatable = app.tblTransactionSubscribeRedeem.paging("@{InquiryUnitRegistry.pagingInquiryRegistryUnitSubscribe()}", wek);
				}

				app.subscribeRedeem.show();
				app.switching.hide();
				app.dividend.hide();
			}
			
			if(app.type.val() == TYPE_REDEEM)
			{
				if(app.datatable.attr("id") != app.tblTransactionSubscribeRedeem.attr("id"))
				{
					app.tblTransactionSubscribeRedeem.dataTable().fnDestroy();
					app.datatable = app.tblTransactionSubscribeRedeem.paging("@{InquiryUnitRegistry.pagingInquiryRegistryUnitRedeem()}", wek);
				}

				app.subscribeRedeem.show();
				app.switching.hide();
				app.dividend.hide();
			}

			if(app.type.val() == TYPE_SWITCHING)
			{
				if(app.datatable.attr("id") != app.tblTransactionSwitching.attr("id"))
				{
					app.tblTransactionSwitching.dataTable().fnDestroy();
					app.datatable = app.tblTransactionSwitching.paging("@{InquiryUnitRegistry.pagingInquiryRegistryUnitSwitching()}", wek);
				}

				app.subscribeRedeem.hide();
				app.switching.show();
				app.dividend.hide();
			}

			if(app.type.val() == TYPE_DIVIDEND)
			{
				if(app.datatable.attr("id") != app.tblTransactionDividend.attr("id"))
				{
					app.tblTransactionDividend.dataTable().fnDestroy();
					app.datatable = app.tblTransactionDividend.paging("@{InquiryUnitRegistry.pagingInquiryRegistryUnitDividend()}", wek);
				}

				app.subscribeRedeem.hide();
				app.switching.hide();
				app.dividend.show();
			}
		};

/* =========================================================================== 
 * Event
 * ========================================================================= */

		app.tradeDateFrom.change(function(){
	        var dateFrom = $(this).datepicker('getDate');
	        var dateTo = app.tradeDateTo.datepicker('getDate');
	        var origin = "from";
	        var id = "#tradeDate";
	        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.tradeDateTo.val() != '')) {
	        	//compareDateFromTo(dateFrom, dateTo, origin, id);
	        	compareDateFromToEqual(dateFrom, dateTo, origin, id);
	        }

			//validateDate("From");
	        var checkError = $("input").hasClass('fieldError');
	        if (checkError){
	        	//app.search.button('disable');
	        }  else {
	        	//app.search.button('enable');
	        }
	    });

		app.tradeDateTo.change(function(){
	        var dateFrom = app.tradeDateFrom.datepicker('getDate');
	        var dateTo = $(this).datepicker('getDate');
	        var origin = "to";
	        var id = "#tradeDate";
	        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.tradeDateFrom.val() != '')) {
	        	//compareDateFromTo(dateFrom, dateTo, origin, id);
	        	compareDateFromToEqual(dateFrom, dateTo, origin, id);
	        }

			//validateDate("To");
	        var checkError = $("input").hasClass('fieldError');
	        if (checkError){
	        	//app.search.button('disable');
	        }  else {
	        	//app.search.button('enable');
	        }
	    });

//		var validateDate = function(from){
//			var DateFrom = new Date($('#tradeDateFrom').datepicker('getDate'));
//			var DateTo = new Date($('#tradeDateTo').datepicker('getDate'));
//			if (($('#tradeDateFrom').val()!='') && ($('#tradeDateTo').val()!='')) { 
//				if (DateTo.getTime() < DateFrom.getTime()) {
//					if (from=='From'){
//						$('#tradeDateFrom').addClass('fieldError');
//						$("#tradeDateFromError").html("Date From must be less or equal than Date To");
//					} else {
//						$('#tradeDateTo').addClass('fieldError');
//						$("#tradeDateToError").html("Date To must be greather or equal than Date From");
//					}
//				} else {
//					$('#tradeDateTo').removeClass('fieldError');
//					$("#tradeDateToError").html("");
//					$('#tradeDateFrom').removeClass('fieldError');
//					$("#tradeDateFromError").html("");
//				}
//			}
//		};

		app.search.click(function() {
			var checkError = $('input').hasClass('fieldError');
			if(checkError)
			{
				return false;
			}

			//app.fundCodeError.html("");
			//app.tradeDateFromError.html("");
			//app.tradeDateToError.html("");

			if ((app.fundCode.isEmpty()) || (app.tradeDateFrom.isEmpty()) || (app.tradeDateTo.isEmpty())) {
				
				if (app.fundCode.isEmpty()){
					$('#fundCodeError').html('Required');
				}
				
				if (app.tradeDateFrom.isEmpty()) {
					app.tradeDateFromError.html("Required");
				}
				
				if (app.tradeDateTo.isEmpty()) {
					app.tradeDateToError.html("Required");
				}
				
				return false;
			}
			else
			{
				app.fundCodeError.html("");
				app.tradeDateFromError.html("");
				app.tradeDateToError.html("");
				app.query.val(true);
				changeTableTransaction();
				//app.datatable.fnPageChange("first");
				app.datatable.fnClearTable();
			}
		});

		app.reset.click(function(){
			location.href="@{list()}";
			return false;
		});

		app.fundCode.popupProduct("type", function(data){});

		app.type.change(function(){
			changeLabelTransaction();
		});

		app.tblTransactionSubscribeRedeem.dataTable().bind("select", function(event, prop) {
			if(app.type.val() == TYPE_SUBSCRIBE)
			{
				var keySubscribe = prop.bean.tradeKey;
//				location.href = '@{RegistryCancelTrade.cancelSubscription()}?keyId=' + keySubscribe + '&from=cancelTrade';
				location.href = '@{RegistrySubscription.cancel()}?keyId=' + keySubscribe + '&from=unitRegistry';
			}

			if(app.type.val() == TYPE_REDEEM)
			{
				var keyRedeem = prop.bean.tradeKey;
				var liquidationRedeem = prop.bean.liquidation;
//				location.href = '@{RegistryCancelTrade.cancelRedemption()}?keyId=' + keyRedeem + '&from=cancelTrade' + '&liquidation=' + liquidationRedeem;
				location.href = '@{RegistryRedemption.cancel()}?keyId=' + keyRedeem + '&from=unitRegistry' + '&liquidation=' + liquidationRedeem;
			}
		});

		app.tblTransactionSwitching.dataTable().bind("select", function(event, prop) {
			if(app.type.val() == TYPE_SWITCHING)
			{
				var keySwitching = prop.bean.switchingKey;
//				location.href = '@{RegistryCancelTrade.cancelSwitching()}?keyId=' + keySwitching + '&from=cancelSwitching';
				location.href = '@{RegistrySwitch.cancel()}?keyId=' + keySwitching + '&from=unitRegistry';
			}
		});

		app.tblTransactionDividend.dataTable().bind("select", function(event, prop) {
			if(app.type.val() == TYPE_DIVIDEND)
			{
				var keyDividend = prop.bean.fundActionKey;
//				location.href = '@{RegistryCancelTrade.cancelDividend()}?keyId=' + keyDividend + '&from=cancelFundAction';
				location.href = '@{RegistryFundAction.cancel()}?keyId=' + keyDividend + '&from=unitRegistry';
			}
		});

		changeTableTransaction();
		changeLabelTransaction();
	}
	else
	{
		return new CancelTrade(html);
	}
}