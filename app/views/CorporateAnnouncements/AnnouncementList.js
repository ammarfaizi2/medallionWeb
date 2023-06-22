function Paging(html) {
	if (this instanceof Paging) {

/* =========================================================================== 
 * Variable
 * ========================================================================= */
		var app = html.inject(this, true);
		
/* =========================================================================== 
 * Table Serach Parameter (penamaan harus fix yaitu parameter)
 * ========================================================================= */
		this.parameter = function() {
			var p = new Object();
			p.dateFrom = app.distributionDateFrom.val();
			p.dateTo = app.distributionDateTo.val();
			p.actionCode = app.actionCodeKey.val();
			p.securityType = app.securityTypeId.val();
			p.securityCode = app.securityCodeId.val();
			p.announcementNoOperator = app.announcementNoOperator.val();
			p.announcementNo = app.announcementSearchNo.val();
			p.query = app.query.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.announcementNoOperator.children().eq(0).remove();
		
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		$('#newDataAnnouncement').button();
		
		app.datatable = app.tableannouncement.paging("@{CorporateAnnouncements.search()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		//app.datatable.search.val("");
		app.actionCode.blur(function(){
			if ($(this).val()==''){
				app.actionCode.removeClass('fieldError');
				app.actionCode.val('');
				app.actionCodeKey.val('');
				app.actionCodeDesc.val('');
				app.h_actionCodeDesc.val('');
			}
		});
		app.actionCode.popupActionTemplates("");
		
		app.securityType.blur(function(){
			if ($(this).val()==''){
				app.securityType.removeClass('fieldError');
				app.securityTypeId.val('');
				app.securityTypeName.val('');
				app.h_securityTypeName.val('');
			}
			
			app.securityCode.val('');
			app.securityCodeId.val('');
			app.securityCodeName.val('');
			app.h_securityCodeName.val('');

			attachSecuritiesPaging();
		});
		
		$('#securityType').lookup({
			list:'@{Pick.securityTypes()}',
			get:{
				url:'@{Pick.securityType()}',
				success: function(data){
					app.securityType.removeClass('fieldError');
					app.securityTypeId.val(data.code);
					app.securityTypeName.val(data.description);
					app.h_securityTypeName.val(data.description);
					attachSecuritiesPaging();
				},
				error: function(data){
					app.securityType.addClass('fieldError');
					app.securityType.val('');
					app.securityTypeId.val('');
					app.securityTypeName.val('NOT FOUND');
					app.h_securityTypeName.val('');
					attachSecuritiesPaging();
				}
			},
			description:$('#securityTypeName'),
			help:$('#securityTypeHelp'),
			nextControl:$('#securityCode')
		});
		
		app.securityCode.blur(function(){
			if ($(this).val()==''){
				app.securityCode.removeClass('fieldError');
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('');
				app.h_securityCodeName.val('');
			}
		});
		
		function attachSecuritiesPaging() {
			var securityType = $('#securityType').val();
			var pickName = (securityType == '') ? 'PICK_SC_MASTER' : 'PICK_SC_MASTER_BY_SEC_TYPE';
			$('#securityCode').dynapopup2({key:'securityCodeId', help:'securityCodeHelp', desc:'securityCodeName'}, pickName, securityType, 'distributionDateFrom', function(data){
				app.securityCode.removeClass('fieldError');
				app.securityCodeId.val(data.code);
				app.securityCodeName.val(data.description);
				app.h_securityCodeName.val(data.description);
			},function(data){
				app.securityCode.addClass('fieldError');
				app.securityCode.val('');
				app.securityCodeId.val('');
				app.securityCodeName.val('NOT FOUND');
				app.h_securityCodeName.val('');
			});
		}
		
		function validateSearch(){
			if (app.actionCodeKey.val() == '' || app.securityTypeId.val() == '' || app.securityCodeId.val() == ''){
				if (app.actionCodeKey.val() == '') {
					app.actionCodeError.html('Required');	
				} else {
					app.actionCodeError.html('');
				}
				
				if (app.securityTypeId.val() == '') {
					app.securityTypeError.html('Required');	
				} else {
					app.securityTypeError.html('');
				}
				
				if (app.securityCodeId.val() == '') {
					app.securityCodeError.html('Required');
				} else {
					app.securityCodeError.html('');
				}
				return false;
		}else if($('#distributionDateFrom').val()!='' || $('#distributionDateTo').val()!=''){
			if($('#distributionDateFrom').hasClass('fieldError') || $('#distributionDateTo').hasClass('fieldError'))
				return false;
			else{
				return true;
			}					
		} else {
				return true;
			}
		}
		
		attachSecuritiesPaging();
		
		app.distributionDateFrom.change(function(){
			var dateFrom = $(this).datepicker('getDate');
			var dateTo = $('#distributionDateTo').datepicker('getDate');
			var origin = 'from';
			var id = '#distributionDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#distributionDateTo').val()!=''))
				//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
		});
		
		app.distributionDateTo.change(function(){
			var dateFrom = $('#distributionDateFrom').datepicker('getDate');
			var dateTo = $(this).datepicker('getDate');
			var origin = 'to';
			var id = '#distributionDate';
			if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#distributionDateFrom').val()!=''))
				//compareDateFromTo(dateFrom, dateTo, origin, id);
				compareDateFromToEqual(dateFrom, dateTo, origin, id);
		});
		
		app.search.click(function(){
			if(validateSearch()){
				$('#distributionDateFrom').removeClass('fieldError');
				$('#distributionDateTo').removeClass('fieldError');
				$('#distributionDateFromError').html('');
				$('#distributionDateToError').html('');
				
				$('#result').css('display', '');
				app.datatable.search.val("");
				app.query.val(true);
				app.datatable.fnPageChange("first");
			}
		});

		app.reset.click(function(){
			location.href="@{list()}";	
		});

		$('#newDataAnnouncement').click(function(){
			location.href="@{entry()}";
		});
		
		app.datatable.bind("select", function(event, prop){
			location.href='@{edit()}?id='+prop.bean.corporateAnnouncementKey;
			//alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			for (x in props) {
				//alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});
		
	}else{
		return new Paging(html);
	}
}