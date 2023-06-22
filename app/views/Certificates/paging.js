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
			p.customerCodeId = app.customerCodeId.val();
			p.accountNo = app.customerCode.val();
			p.securityType = app.securityType.val();
			p.securityCode = app.securityCode.val();
			p.securityKey = app.securityKey.val();
			p.certificateNoOperator = app.certificateNoOperator.val();
			p.csCertificateCertificateId = app.csCertificateCertificateId.val();
			return p;
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();
		
		app.result.hide();
		
		app.datatable = app.tblCertificates.paging("@{Certificates.paging()}", this);
		
		app.certificateNoOperator.children().eq(0).remove();

/* =========================================================================== 
 * Event
 * ========================================================================= */
		
		/*app.search.click(function(){
			app.datatable.fnPageChange("first");
		});

		app.reset.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
			app.datatable.search.val("");
		});

		app.datatable.bind("select", function(event, prop){
			alert(prop.bean.customerNo+"-"+prop.row[1]+"-"+prop.tr);
		});
		
		app.datatable.bind("selects", function(event, props){
			alert("a");
			for (x in props) {
				alert(x);
				alert(props.length+"  "+props[x].bean.customerNo+"-"+props[x].row[1]);
			}
		});*/
		
		
		app.search.click(function(){
			if (app.customerCode.hasClass('fieldError')){
				app.customerCode.removeClass('fieldError');
				app.customerDesc.val('');
			}
			if (app.securityType.hasClass('fieldError')){
				app.securityType.removeClass('fieldError');
				app.securityTypeDesc.val('');
			}
			if (app.securityCode.hasClass('fieldError')){
				app.securityCode.removeClass('fieldError');
				app.securityCodeDesc.val('');
			}
			
			if (($('#csCertificateFrom').val()=='') || ($('#csCertificateTo').val()=='')){
				if ($('#csCertificateFrom').val()==''){
					$('#csCertificateFromError').html("Required");
				}
				if ($('#csCertificateTo').val()==''){
					$('#csCertificateToError').html("Required");
				}
				return false;
			}else if ($('#csCertificateFrom').hasClass('fieldError') || $('#csCertificateTo').hasClass('fieldError') ){
				return false;
			}
			else{
				app.datatable.fnPageChange("first");
				app.result.show();
				app.datatable.fnAdjustColumnSizing();
			}
			
		});
		
		app.reset.click(function(){
			location.href="@{list()}";	
			
			
		});
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}') || (jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")){
				location.href='@{view()}/?id=' + prop.bean.certificateKey;
			}else{
				location.href='@{edit()}/?id=' + prop.bean.certificateKey;
			}
//			
		});
		/*app.newData.button();
		
		app.newData.click(function(){
			location.href='@{entry()}#{if group}/${group}#{/}';
		});*/
		
	}else{
		return new Paging(html);
	}
}
