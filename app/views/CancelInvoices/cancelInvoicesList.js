$(function(){
	$("#new").css("display","none");

	$('#invoiceNoOperator').children().eq(0).remove();
	
	$('#invoiceDateFrom').datepicker();
    $('#invoiceDateTo').datepicker();

    $('#customerCode').change(function(){
		if ($(this).val()==''){
			$('#customerCode').removeClass('fieldError');
			$('#customerCode').val('');
			$('#customerCodeId').val('');
			$('#customerDesc').val('');
			$('#h_customerDesc').val('');
		}
	});
    
	$('#customerCode').lookup({
		list : '@{Pick.customersNonRetail()}',
		get : {
			url: '@{Pick.customerNonRetail()}',
			success: function(data) {
				$('#customerCode').removeClass('fieldError');
				$('#customerCodeId').val(data.code);
				$('#customerDesc').val(data.description);
				$('#h_customerDesc').val(data.description);
			},
			error: function(data) {
				$('#customerCode').addClass('fieldError');
				$('#customerCode').val('');
				$('#customerCodeId').val('');
				$('#customerDesc').val('NOT FOUND');
				$('#h_customerDesc').val('');
			}
		},
		description:$('#customerDesc'),
		help : $('#customerCodeHelp')
	});
	
	$('#root').accordion({
		collapsible: true
	});
	
	$('.buttons button:first').button({
		icons: {
			primary: 'ui-icon-search'
		}
	}).next().button();	
	
	$('#invoiceDateFrom').change(function(){
        var dateFrom = $(this).datepicker('getDate');
        var dateTo = $('#invoiceDateTo').datepicker('getDate');
        var origin = 'from';
        var id = '#invoiceDate';
        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#invoiceDateTo').val()!='')) {
            //compareDateFromTo(dateFrom, dateTo, origin, id);
            compareDateFromToEqual(dateFrom, dateTo, origin, id);
        }
        
        var checkError = $("input").hasClass('fieldError');
        if (checkError){
            $('#search').button('disable');
        }  else {
            $('#search').button('enable');
        }
    });
	
	$('#invoiceDateTo').change(function(){
        var dateFrom = $('#invoiceDateFrom').datepicker('getDate');
        var dateTo = $(this).datepicker('getDate');
        var origin = 'to';
        var id = '#invoiceDate';
        if ((($(this).val()!='') || ($(this).hasClass('fieldError'))) && ($('#invoiceDateFrom').val()!='')) {
            //compareDateFromTo(dateFrom, dateTo, origin, id);
        	compareDateFromToEqual(dateFrom, dateTo, origin, id);
        }
        
        var checkError = $("input").hasClass('fieldError');
        if (checkError){
            $('#search').button('disable');
        }  else {
            $('#search').button('enable');
        }
    });
	
	$('#search').click(function() {
		$("#invoiceDateFromError").html("");
		$("#invoiceDateToError").html("");

		if (($('#invoiceDateFrom').val()=='') || ($('#invoiceDateTo').val()=='')) {
			
			if ($('#invoiceDateFrom').val()=='') {
				$("#invoiceDateFromError").html("Required");
			}
			
			if ($('#invoiceDateTo').val()=='') {
				$("#invoiceDateToError").html("Required");
			}
			
			return false;
		}
		else
		{
			$('#result').css("display", "");
		}
	
		if ($('#invoiceDateFrom').val()!=='') {
			$("#invoiceDateFromError").html("");
		}
		
		if ($('#invoiceDateTo').val()!=='') {
			$("#invoiceDateToError").html("");
		}
		//search();
		return false;
	});
	// end of Search
	
	$('#reset').click(function() {
		location.href="@{list()}";	
		return false;
	});
	
//	$('.buttons input:button').button();
	
});

//var search = function() {
//	$("#invoiceDateFromError").html("");
//	$("#invoiceDateToError").html("");
//	$("#errmsgCustomerCode").html("");
//	if (($('#invoiceDateFrom').val()=='') || ($('#invoiceDateTo').val()=='')) {
//		
//		if ($('#invoiceDateFrom').val()=='') {
//			$("#invoiceDateFromError").html("Required");
//		}
//		
//		if ($('#invoiceDateTo').val()=='') {
//			$("#invoiceDateToError").html("Required");
//		}
//		
//		return false;
//	}
//
//	if ($('#invoiceDateFrom').val()!=='') {
//		$("#invoiceDateFromError").html("");
//	}
//	
//	if ($('#invoiceDateTo').val()!=='') {
//		$("#invoiceDateToError").html("");
//	}
//	
//	$('#result .loading').show();
//	$('#result .response').hide();
//	$('#result').show();

//	$.get('@{search()}', $('#searchForm').serialize(), function(data) {
//		$("#navDateError").html("");
//		$('#result .response').html(data);
//		$('#result .loading').hide();
//		$('#result .response').show();
//		setupTable();
//	});
	
//};

function doEdit(data) {
	location.href='@{view()}/?key=' + data[0];
	return false;
}

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
			p.invoiceDateFrom = app.invoiceDateFrom.val();
			p.invoiceDateTo = app.invoiceDateTo.val();
			p.customerCode = app.customerCode.val();
			p.invoiceNoOperator = app.invoiceNoOperator.val();
			p.invoiceSearchNoOperator = app.invoiceSearchNoOperator.val();
			p.query = app.query.val();
			return p;
		};
/* =========================================================================== 
 * Runtime
 * ========================================================================= */
		app.root.accordion({collapsible: true});
		
		app.search.add(app.reset).button();

		app.datatable = app.tableInvoice.paging("@{CancelInvoices.paging()}", this);

/* =========================================================================== 
 * Event
 * ========================================================================= */
		app.search.click(function(){
			app.query.val(true);
			app.datatable.fnPageChange("first");
		});

		app.reset.click(function(){
			app.datatable.trigger("fetch", [0, "checked"]);
		});

		app.datatable.bind("select", function(event, prop){
			//alert("1 " + prop.bean.billingKey +"-"+ prop.row[1] + "-" + prop.tr);
			location.href='@{view()}/?key=' + prop.bean.billingKey;
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