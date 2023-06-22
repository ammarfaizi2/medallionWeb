function RegistrySwitch(html) {
	if (this instanceof RegistrySwitch) {

/* =========================================================================== 
 * GUI Variable
 * ========================================================================= */
		var app = html.inject(this, true);

/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.switchNoOperator = app.switchNoOperator.val();
			p.switchingKey = app.switchingKey.val();
			p.switchDateFrom = app.switchDateFrom.val();
			p.switchDateTo = app.switchDateTo.val();
			p.investorNo = app.investorNo.val();
			p.investorKey = app.investorNoKey.val();
			p.query = app.query.val();
			return p;
		};

/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		html.clazz('calendar').datepicker();

		app.root.accordion({collapsible: true});

		app.search.add(app.reset).add(app.newData).button();

		app.switchNoOperator.children().eq(0).remove();

		app.datatable = app.tableSwitching.paging("@{RegistrySwitch.pagingSwitching()}", this);

/* =========================================================================== 
 * Method
 * ========================================================================= */
		function investorLookup() {
			app.investorNo.dynapopup('PICK_CF_MASTER_BY_INVESTOR', app.investorNo.val(), 'switchingKey');
			/*
			var urlInvLook = '@{Pick.customerByInvestors()}';
			app.investorNo.lookup({
				list:urlInvLook,
				get:{
					url:'@{Pick.customerByInvestor()}',
					success: function(data) {
						if(data)
						{
							app.investorNo.removeClass('fieldError');
							app.investorNo.val(data.customerNo);
							app.investorNoKey.val(data.customerKey);
							app.investorNoDesc.val(data.description);
						}
					},
					error : function(data) {
						app.investorNo.addClass('fieldError');
						app.investorNoDesc.val('NOT FOUND');
						app.investorNo.val("");
					}
				},
				description:app.investorNoDesc,
				help:app.investorNoHelp
			});
			*/
		}

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.switchDateFrom.change(function(){
	        var dateFrom = $(this).datepicker('getDate');
	        var dateTo = app.switchDateTo.datepicker('getDate');
	        var origin = "from";
	        var id = "#switchDate";
	        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.switchDateTo.val() != '')) {
	        	//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
	        }

			//validateDate("From");
	        var checkError = $("input").hasClass('fieldError');
	        if (checkError){
	        	app.search.button('disable');
	        }  else {
	        	app.search.button('enable');
	        }
	    });

		app.switchDateTo.change(function(){
	        var dateFrom = app.switchDateFrom.datepicker('getDate');
	        var dateTo = $(this).datepicker('getDate');
	        var origin = "to";
	        var id = "#switchDate";
	        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && (app.switchDateFrom.val() != '')) {
	        	//compareDateFromTo(dateFrom, dateTo, origin, id);
	        	compareDateFromToEqual(dateFrom, dateTo, origin, id);
	        }

			//validateDate("To");
	        var checkError = $("input").hasClass('fieldError');
	        if (checkError){
	        	app.search.button('disable');
	        }  else {
	        	app.search.button('enable');
	        }
	    });

		app.investorNo.bind("keyup change", function() {
			app.investorNoKey.val("");
		});
		
//		var validateDate = function(from) {
//			var DateFrom = new Date($('#switchDateFrom').datepicker('getDate'));
//			var DateTo = new Date($('#switchDateTo').datepicker('getDate'));
//			if (($('#switchDateFrom').val()!='') && ($('#switchDateTo').val()!='')) { 
//				if (DateTo.getTime() < DateFrom.getTime()) {
//					if (from=='From'){
//						$('#switchDateFrom').addClass('fieldError');
//						$("#switchDateFromError").html("Date From must be less or equal than Date To");
//					} else {
//						$('#switchDateTo').addClass('fieldError');
//						$("#switchDateToError").html("Date To must be greather or equal than Date From");
//					}
//				} else {
//					$('#switchDateTo').removeClass('fieldError');
//					$("#switchDateToError").html("");
//					$('#switchDateFrom').removeClass('fieldError');
//					$("#switchDateFromError").html("");
//				}
//			}
//		};

		app.search.click(function() {			
			app.switchDateFromError.html("");
			app.switchDateToError.html("");

			if((app.switchDateFrom.isEmpty()) || (app.switchDateTo.isEmpty()))
			{
				if(app.switchDateFrom.isEmpty())
				{
					app.switchDateFromError.html("Required");
				}
				
				if(app.switchDateTo.isEmpty())
				{
					app.switchDateToError.html("Required");
				}

				return false;
			}
			else
			{
				app.switchDateFromError.html("");
				app.switchDateToError.html("");
				app.query.val(true);
				app.datatable.fnClearTable();
			}
		});

		app.reset.click(function(){
			location.href="@{list()}";
			return false;
		});

		app.datatable.bind("select", function(event, prop) {
			location.href='@{edit()}?id=' + prop.bean.switchingKey;
		});

		app.newData.click(function() {
			location.href="@{entry()}";
		});
		
		investorLookup();
	}
	else
	{
		return new RegistrySwitch(html);
	}
}