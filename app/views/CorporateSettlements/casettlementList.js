function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
/* =========================================================================== 
 * Table Search Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.dateFrom = app.distributionDateFrom.val();
			p.dateTo = app.distributionDateTo.val();
			p.actionCode = app.actionCodeKey.val();
			p.securityType = app.securityTypeKey.val();
			p.securityCode = app.securityCodeKey.val();
			p.announcementNoOperator = app.announcementNoOperator.val();
			p.announcementNo = app.announcementSearchNo.val();
			return p;
		};
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.announcementNoOperator.children().eq(0).remove();
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();

		app.datatable = app.tableInvoice.paging("@{CorporateSettlements.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.distributionDateFrom.change(function(){
			var dateFrom = $(this).datepicker('getDate');
			var dateTo = $('#distributionDateTo').datepicker('getDate');
			var origin = 'from';
			var id = '#distributionDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#distributionDateTo').val()!=''))
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
				//compareDateFromTo(dateFrom, dateTo, origin, id);
		});
		
		app.distributionDateTo.change(function(){
			var dateFrom = $('#distributionDateFrom').datepicker('getDate');
			var dateTo = $(this).datepicker('getDate');
			var origin = 'to';
			var id = '#distributionDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#distributionDateFrom').val()!=''))
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
				//compareDateFromTo(dateFrom, dateTo, origin, id);
		});
		
		app.actionCode.blur(function(){
			if ($(this).val()==''){
				//app.actionCode.removeClass('fieldError');
				app.actionCode.val('');
				app.actionCodeKey.val('');
				app.actionCodeDesc.val('');
				app.h_actionCodeDesc.val('');
			}
		});
		app.actionCode.popupActionTemplates("");
		app.securityType.blur(function(data){
			if ($(this).val()==''){
				//app.securityType.removeClass('fieldError');
				app.securityTypeKey.val('');
				app.securityTypeDesc.val('');
				app.h_securityTypeDesc.val('');
				
				//app.securityCode.removeClass('fieldError');
				app.securityCode.val("");
				app.securityCodeKey.val("");
				app.securityCodeDesc.val("");
				app.h_securityCodeDesc.val("");
			}
		});
		
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
						app.securityType.removeClass('fieldError');
						app.securityTypeKey.val(data.code);
						app.securityTypeDesc.val(data.description);
						app.h_securityTypeDesc.val(data.description);
						
						//app.securityCode.removeClass('fieldError');
						app.securityCode.val("");
						app.securityCodeKey.val("");
						app.securityCodeDesc.val("");
						app.h_securityCodeDesc.val("");
					},
				error: function(data){
					app.securityType.addClass('fieldError');
					app.securityType.val('');
					app.securityTypeKey.val('');
					app.securityTypeDesc.val('NOT FOUND');
					app.h_securityTypeDesc.val('');
				}
			},
			description:$('#securityTypeName'),
			help:$('#securityTypeHelp'),
			nextControl:$('#securityCode')
		});
		
		app.securityCode.blur(function(){
			if ($(this).val()==''){
				//app.securityCode.removeClass('fieldError');
				app.securityCode.val("");
				app.securityCodeKey.val("");
				app.securityCodeDesc.val("");
				app.h_securityCodeDesc.val("");
			}
		});
		
		$('#securityCode').lookup({
			list:'@{Pick.securities()}',
			get:{
				url:'@{Pick.security()}',
				success: function(data){
					app.securityCode.removeClass('fieldError');
					app.securityCodeKey.val(data.code);
					app.securityCodeDesc.val(data.description);
					app.h_securityCodeDesc.val(data.description);
				},
				error: function(data){
					app.securityCode.addClass('fieldError');
					app.securityCode.val('');
					app.securityCodeKey.val('');
					app.securityCodeDesc.val('NOT FOUND');
					app.h_securityCodeDesc.val('');
				}
			},
			description:$('#securityCodeName'),
			filter:$('#securityType'),
			help:$('#securityCodeHelp')
		});
		
		app.search.click(function(){
			app.distributionDateFromError.html("");
			app.distributionDateToError.html("");
			
			if ((app.distributionDateFrom.val()=='') || (app.distributionDateFrom.val()=='')) {
				
				if (app.distributionDateFrom.val()=='') {
					app.distributionDateFromError.html("Required");
				} else {
					app.distributionDateFromError.html("");
				}
				
				if (app.distributionDateTo.val()=='') {
					app.distributionDateToError.html("Required");
				} else {
					app.distributionDateToError.html("");
				}
				
				/*if (app.actionCodeKey.val()=='') {
					app.actionCodeError.html("Required");
				} else {
					app.actionCodeError.html("");
				}
				
				if (app.securityTypeKey.val()=='') {
					app.securityTypeError.html("Required");
				} else {
					app.securityTypeError.html("");
				}
				
				if (app.securityCodeKey.val()=='') {
					app.securityCodeError.html("Required");
				} else {
					app.securityCodeError.html("");
				}*/
				return false;
			} else {
				
				$('#result').css("display", "");
				$('.error').html('');
				app.datatable.fnPageChange("first");
				
				return false;
			}
			
		});

		app.reset.click(function(){
			//app.datatable.trigger("fetch", [0, "checked"]);
			location.href="@{list()}";	
		});

		app.datatable.bind("select", function(event, prop){
			//alert("1 " + prop.bean.billingKey +"-"+ prop.row[1] + "-" + prop.tr);
			location.href='@{edit()}/?id=' + prop.bean.corporateAnnouncementKey;
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				//alert("2 " + props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});

	}else{
		return new Paging(html);
	}
}