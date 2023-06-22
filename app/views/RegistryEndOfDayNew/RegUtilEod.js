$(document).ready(function() {
//	$('#buttonTest').click(function(){
//
//		$.get('@{StringToCalender()}', function(data){
//			alert(data.result_00);
//			$('#date').val(data.result_00);
//		});
//		
//		return false;
//	});
	
//	$('#new').remove();
	$('#buttonTest').remove();
	$('.buttons input:button').button();
	
	$('#changeDateForm').dialog({
	    autoOpen:false,
	    modal:true,
	    heigth:'600px',
	    width:'400px',
	    resizable:false
	});        
	
	$('#rollbackDateForm').dialog({
	    autoOpen:false,
	    modal:true,
	    heigth:'600px',
	    width:'400px',
	    resizable:false
	});
	    
	$('#rollBackEOD').dialog({
	    autoOpen:false,
	    modal:true,
	    heigth:'800px',
	    width:'500px',
	    resizable:false
	});        
	    
	$('#rollBackEODConfirm').dialog({
	    autoOpen:false,
	    modal:true,
	    heigth:'800px',
	    width:'500px',
	    resizable:false
	});        
	
	$('#runAllEod').dialog({
	    autoOpen:false,
	    modal:true,
	    heigth:'800px',
	    width:'500px',
	    resizable:false
	});
	
	$('#buttonChangeDate').click(function(){
		
		$.get('@{buttonChangeDate()}', function(data){
			checkRedirect(data);
			
			if (data.countResult == "0")
				$.get('@{pGnApplicationDateUpd()}', function(data){
					checkRedirect(data);
					
					{
						$('#date').val(data.fundDateString);
						$('#date2').val(data.nextDateString);
					}
					
				});
				
			else
				{
					$("#changeDateForm").dialog('open');
					$('.ui-widget-overlay').css('height',$('body').height());
				}
			
		});
		
		return false;
	});
	
	$("#tblTransaction").click(function(){
		return false;
	});	
	
	$('#buttonRunAll').click(function(){
//		var table = $("#tblTransaction");
//		var dataGetEodDate = "";
//		var dataGetStarts = "";
//		var dataGetEnds = "";
//		var dataGetMessage = "";
//			
//		$("tbody tr", table).each(function(){
//			var getProductCode = $(this).children().eq(1).html();
//				
//			$.ajax({ 
//			   type: "GET", 
//			   async: false,
//			   url: "@{buttonRun()}?productCode=" + getProductCode, 
//			   success: function(data){
//						   dataGetEodDate = data.getEodDate;
//						   dataGetLastEod = data.getLastEod;						   
//						   dataGetStarts = data.getStarts;
//						   dataGetEnds = data.getEnds;
//						   dataGetMessage = data.getMessage;
//					   } 
//			});
//				
//			$(this).children().eq(3).html(dataGetEodDate);
//			$(this).children().eq(4).html(dataGetLastEod);
//			$(this).children().eq(5).html(dataGetStarts);
//			$(this).children().eq(6).html(dataGetEnds);
//			$(this).children().eq(7).html(dataGetMessage);
//				
//		});
		$("#runAllEod").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		$.get('@{buttonRunAll()}?productCode=', function(data){
			checkRedirect(data);
		});
		
		$("#runAllEod").dialog('close');
		location = "@{list()}";
				
		return false;
	});					
	
	$('#buttonRollback').click(function(){	
		
		$("#fundCode").val("");
		$("#fundCodeKey").val("");
		$("#fundCodeDesc").val("");
		$("#transactionDate").val("");
		$('#fundDate').val("");
		$('#lastEod').val("");
		$('#fundDateNew').val("");
		$('#lastEodNew').val("");
		
		$("#rollBackEOD").dialog('open');
		$('.ui-widget-overlay').css('height',$('body').height());
		
//		$("#fundDate").val($('#date2').val());
//		$("#lastEod").val($('#date').val());;
		
		return false;
	});
	
	
	$('#buttonCloseRollBackEOD').click(function(){			
		$("#rollBackEOD").dialog('close');
		return false;
	});
	
	$('#changeDateClose').click(function(){			
		$("#changeDateForm").dialog('close');
		return false;
	});
	
	$('#rollbackDateClose').click(function(){			
		$("#rollbackDateForm").dialog('close');
		return false;
	});
	
	$('#transactionDate').datepicker({
		beforeShowDay : $.datepicker.noWeekends,
		onSelect : function() {
			$.get('@{getProcessDate()}?paramDate='+$('#transactionDate').val(), function(data){
				checkRedirect(data);
				
				$('#fundDateNew').val(data.fundDateString);
				$('#lastEodNew').val(data.lastEodString);
				
			});
		}
	});
	
//	$('#fundDateNew').datepicker({
//		beforeShowDay : $.datepicker.noWeekends,
//		onSelect : function() {
//			
//		}
//	});
	
//	$('#lastEodNew').datepicker({
//		beforeShowDay : $.datepicker.noWeekends,
//		onSelect : function() {
//			
//		}
//	});
	
	$('#buttonProcessRollBackEOD').click(function(){
		var d = $.datepicker.parseDate("mm/dd/yy", $('#transactionDate').val());
		var datestrInNewFormat = $.datepicker.formatDate( "mm/dd/yy", d);
		var dateStart = formatDateMMDDYYYYHHMISS();
		
		$.get('@{buttonRollbackProcess()}?productCode='+$('#fundCode').val()+'&fundDate='+datestrInNewFormat, function(data){
			
			if (data.countResult == "1")
				{
					$('#fundDateNewSpan').html($("#fundDateNew").val());
					$('#fundDateNewSpan1').html($("#fundDateNew").val());
					$('#lastEodNewSpan').html($("#lastEodNew").val());	
					$("#rollBackEODConfirm").dialog('open');
				}
			else
				{
					$("#rollbackDateForm").dialog('open');
				}
			
		});
		
		return false;
	});
	
//	$('#buttonProcessRollBackEOD').click(function(){
//		
//		$('#fundDateNewSpan').html($("#fundDateNew").val());
//		$('#lastEodNewSpan').html($("#lastEodNew").val());
//		
//		$("#rollBackEODConfirm").dialog('open');
//		
//		return false;
//	});
	
	$('#buttonOkRollBackEOD').click(function(){
		
		var d = $.datepicker.parseDate("mm/dd/yy", $('#transactionDate').val());
		var datestrInNewFormat = $.datepicker.formatDate( "mm/dd/yy", d);
		var dateStart = formatDateMMDDYYYYHHMISS();
		
		$.get('@{buttonProcessRollback()}?productCode='+$('#fundCode').val()+'&fundDate='+datestrInNewFormat, function(data){
			checkRedirect(data);
						
			});
		
		$('#tblTransaction tr').each(function() {
			var fundCode01 = $('#fundCode').val();
			var fundCode02 = $(this).find("td").eq(1).html();
			
			var dateEnd = formatDateMMDDYYYYHHMISS();
			
		    if (fundCode01 == fundCode02)
		    {
		    	$(this).find("td").eq(3).html($('#fundDateNew').val());
		    	$(this).find("td").eq(4).html($('#lastEodNew').val());
		    	$(this).find("td").eq(5).html(dateStart);
		    	$(this).find("td").eq(6).html(dateEnd);
		    }
		});		
		
		$("#rollBackEODConfirm").dialog('close');
		$("#rollBackEOD").dialog('close');
		
		location = "@{list()}";
		
		return false;
	});
	
	$('#buttonNoRollBackEOD').click(function(){
		
		$("#rollBackEODConfirm").dialog('close');
		
		return false;
	});
	
	$('#fundCode').lookup({
		list:'@{Pick.listRgProdEodAsSelectitems()}',
		get:{
			url:'@{Pick.getRgProdEodAsSelectitem()}',
			success: function(data){
				$('#fundCode').removeClass('fieldError');
				$('#fundCodeDesc').val(data.name);
				$('#h_fundCodeDesc').val(data.name);
				$('#transactionDate').val('');
				$('#fundDate').val(data.eodDate);
				$('#lastEod').val(data.lastEod);
				$('#fundDateNew').val('');
				$('#lastEodNew').val('');
				
//				$('#tblTransaction tr').each(function() {
//					var fundCode01 = $('#fundCode').val();
//					var fundCode02 = $(this).find("td").eq(1).html();
//					
//				    if (fundCode01 == fundCode02)
//				    {
//				    	$('#fundDate').val($(this).find("td").eq(3).html());
//				    	$('#lastEod').val($(this).find("td").eq(4).html());
//				    }
//				});				
				
			},
			error: function(data){
				$('#fundCode').addClass('fieldError');
				$('#fundCodeKey').val('');
				$('#fundCode').val('');
				$('#fundCodeDesc').val('NOT FOUND');
				$('#h_fundCodeDesc').val('');
				$('#transactionDate').val('');
				$('#fundDate').val('');
				$('#lastEod').val('');
				$('#fundDateNew').val('');
				$('#lastEodNew').val('');
			}
		},
		description:$('#fundCodeDesc'),
		help:$('#fundCodeHelp')
		
	});
	
	function formatDateMMDDYYYYHHMISS () 
	{
		now = new Date();
		year = "" + now.getFullYear();
		month = "" + (now.getMonth() + 1); 
		
		if (month.length == 1) 
		{ 
			month = "0" + month; 
		}
		
		day = "" + now.getDate(); if (day.length == 1) { day = "0" + day; }
		
		hour = "" + now.getHours(); 
		if (hour.length == 1) 
		{ 
			hour = "0" + hour; 
		}
		
		minute = "" + now.getMinutes(); 
		if (minute.length == 1) 
		{ 
			minute = "0" + minute; 
		}
		
		second = "" + now.getSeconds(); 
		if (second.length == 1) 
		{ 
			second = "0" + second; 
		}
		
		return month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second;
	}
	
	function assignToButton() {
		$("input[name='run']").unbind();
		$("input[name='run']").click(function(){
			var myInput =$(this);			
			var productcode = myInput.attr("productcode");
				
			$.get('@{buttonRun()}?productCode='+productcode, function(data){
				checkRedirect(data);
				myInput.parent().siblings().eq(2).html(data.getEodDate);
				myInput.parent().siblings().eq(3).html(data.getLastEod);			
				myInput.parent().siblings().eq(4).html(data.getStarts);
				myInput.parent().siblings().eq(5).html(data.getEnds);
				myInput.parent().siblings().eq(6).html(data.getMessage);
			});
			
			return false;
		});
	}
	
	
	$('#tblTransaction').dataTable().fnSettings().aoDrawCallback.push( {
	    "fn": function () {
	    	$('.buttons input:button').button();
	    	
	    	assignToButton();
	    }
	} );
	assignToButton();
	
});
	