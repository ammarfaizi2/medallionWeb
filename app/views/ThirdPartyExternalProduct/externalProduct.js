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
			/*var p = new Object();
			p.customerNoOperator = app.customerNoOperator.val();
			p.customerSearchNo = app.customerSearchNo.val();
			p.customerNameOperator = app.customerNameOperator.val();
			p.customerSearchName = app.customerSearchName.val();
			p.contactNameOperator = app.contactNameOperator.val();
			p.customerSearchContactName = app.customerSearchContactName.val();
			return p;*/
		};
		
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
//		app.root.accordion({collapsible: true});
		
//		app.search.add(app.reset).button();
		app.datatable = app.tableThirdPartyEksternalProduct.paging("@{ThirdPartyExternalProduct.paging()}", this);

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
		
		app.datatable.bind("select", function(event, prop){
			if ((jQuery.trim(prop.bean.recordStatus) == '${recordStatusNew}')|| (jQuery.trim(prop.bean.recordStatus) == '${recordStatusUpdate}') || (jQuery.trim(prop.bean.recordStatus) == "N")|| (jQuery.trim(prop.bean.recordStatus) == "U")){
				location.href='@{view()}/?id=' + prop.bean.thirdPartyKey+'&group=${group}';
			}else{
				location.href='@{edit()}/?id=' + prop.bean.thirdPartyKey+'&group=${group}';
			}
//			
		});
		app.newData.button();
		
		app.newData.click(function(){
			location.href='@{entry()}#{if group}/${group}#{/}';
		});
		
	}else{
		return new Paging(html);
	}
}
$(function(){

	if (('${mode}'==='entry')||(('${mode}'==='edit')&&(('${thirdParty?.recordStatus?.decodeStatus()}'==='Reject')))){
		$('input[name=isActive]').attr("disabled", "disabled");
	}
	
	$('input[name=isActive]').change(function(){
		$("input[name='thirdParty.isActive']").val($("input[name='isActive']:checked").val());
	});
	
	if ($.browser.msie){
		$("#remarks[maxlength]").bind('input propertychange', function() {
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    });
	}
	
	$('#currency').lookup({
		list:'@{Pick.currencies()}',
		get:{
			url:'@{Pick.currency()}',
			success: function(data){
				$('#currency').removeClass('fieldError');
				$('#currencyCode').val(data.code);
				$('#currencyDesc').val(data.description);
				$('#h_currencyDesc').val(data.description);
			},
			error: function(data){
				$('#currency').addClass('fieldError');
				$('#currencyCode').val('');
				$('#currency').val('');
				$('#currencyDesc').val('NOT FOUND');
				$('#h_currencyDesc').val('');
			}
		},
		description:$('#currencyDesc'),
		help:$('#currencyHelp')
	});

	$('#fundManager').dynapopup('PICK_GN_THIRD_PARTY', '${fundManager}', 'custodianBank', 
		function(data){
			$('#fundManager').removeClass('fieldError');
			$('#fundManagerKey').val(data.code);
			$('#fundManagerDesc').val(data.description);
			$('#h_fundManagerDesc').val(data.description);
		},
		function(data){
			$('#fundManager').addClass('fieldError');
			$('#fundManagerKey').val('');
			$('#fundManagerDesc').val('NOT FOUND');
			$('#h_fundManagerDesc').val('');
			$('#fundManager').val('');
		}
	);

//	$('#fundManager').lookup({
//		list:'@{Pick.thirdParties()}?type=${fundManager}',
//		get:{
//			url:'@{Pick.thirdParty()}?type=${fundManager}',
//			success: function(data){
//				$('#fundManager').removeClass('fieldError');
//				$('#fundManagerKey').val(data.code);
//				$('#fundManagerDesc').val(data.description);
//				$('#h_fundManagerDesc').val(data.description);
//			},
//			error: function(data){
//				$('#fundManager').addClass('fieldError');
//				$('#fundManagerKey').val('');
//				$('#fundManagerDesc').val('NOT FOUND');
//				$('#h_fundManagerDesc').val('');
//				$('#fundManager').val('');
//			}
//		},
//		description:$('#fundManagerDesc'),
//		help:$('#fundManagerHelp')
//	});

	$('#bankCode').dynapopup('PICK_GN_THIRD_PARTY', "THIRD_PARTY-BANK", "bankAccountNo", 
			function(data){
				if (data) {
					$('#bankCode').removeClass('fieldError');
					//$('#newBankCode').val(data.name);
				}
			},
			function(data){
				$('#bankCode').addClass('fieldError');
				$('#bankCodeKey').val('');
				$('#bankCode').val('');
				$('#bankCodeDesc').val('NOT FOUND');
				$('#h_bankCodeDesc').val('');
			}
	); 
	
	/*$('#bankCode').popupThirdParties("?type=THIRD_PARTY-BANK", "bankAccountNo", function(data){
		$('#bankCode').removeClass('fieldError');
		//$('#newBankCode').val(data.name);
	}, function(data) {
		$('#bankCode').addClass('fieldError');
	});*/
	
	$('#bankCode').change(function(){
		if ($('#bankCode').val() == ""){
			$('#bankCode').removeClass('fieldError');
			$('#bankCodeName').val("");
			$('#h_bankCodeName').val("");
		}	
	});
	
	$('#currencyBank').change(function(){
		if ($('#currencyBank').val() == ""){
			$('#currencyBank').removeClass('fieldError');
			$('#currencyDescBank').val("");
			$('#h_currencyDescBank').val("");
		}	
	});
	
	$('#currencyBank').popupCurrencies("thirdPartyBranch", function(data){
		$('#currencyBank').removeClass('fieldError');
	}, function(data){
		$('#currencyBank').addClass('fieldError');
		$('#currencyDescBank').val('NOT FOUND');
		$('#h_currencyDescBank').val('');
	});
});