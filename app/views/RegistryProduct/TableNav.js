$(function() {
//	if(($("#effectiveDate").val() == "") && ($("#liquidDate").val() == "")){
//		if (!$('#isMaturityDateMax').is(":checked")) {
//			$('#btnNewNavDialogError').html("Effective Date and Maturity Date cannot be empty");
//	    	$("#newNavDialog").attr("disabled", "disabled");
//		} else {
//			$('#btnNewNavDialogError').html("Effective Date cannot be empty");
//	    	$("#newNavDialog").attr("disabled", "disabled");
//		}
//    } else {
//		$('#btnNewNavDialogError').html("");
//		$("#newNavDialog").removeAttr("disabled");
//    }

    $("#effectiveDate, #liquidDate").bind("keyup change", function() {
    	if ($('#isMaturityDateMax').is(":checked")) {
    		if($("#effectiveDate").val() == ""){
    			$('#btnNewNavDialogError').html("Effective Date cannot be empty");
	        	$("#newNavDialog").attr("disabled", true);
    		} else {
    			$('#btnNewNavDialogError').html("");
            	$("#newNavDialog").removeAttr("disabled");
    		}
    	} else {
    		if(($("#effectiveDate").val() == "") || ($("#liquidDate").val() == "")){
    			$('#btnNewNavDialogError').html("Effective Date and Maturity Date cannot be empty");
	        	$("#newNavDialog").attr("disabled", true);
            } else {
            	if(($("#effectiveDate").val() != "") && ($("#liquidDate").val() != "")){
            		$('#btnNewNavDialogError').html("");
                	$("#newNavDialog").removeAttr("disabled");
            	}
            }
    	}
    });

	$("#maxCounter").autoNumeric({vMax: '999'});
	$("#maxCounter").live('blur', function() {
        var el = $(this);
        var id = this.id;
        var stripped = "#" + id + "Stripped";
        if (el.val() == '') {
        	el.siblings(stripped).val('');
            return;
        }
        el.siblings(stripped).val(el.val());
    });

	var closeDialog = function() {
        $("#dialog-message").dialog('close');   
    }

	var data = new Object();
    navMin(data);

	$('#addNavDialog').button();
	$('#cancelNavDialog').button();
	
	$("#dialogNavMin").dialog({
        autoOpen:false,
        modal:true,
        heigth:'350px',
        width:'400px',
        resizable:false
    });

    $("#listNavMin #navMinTable tbody tr #deleteButtonNav[disabled!=true]").live("click", function() {
        var row = $(this).parents('tr');
        var rowNumber = tableNavMin.fnGetPosition(row[0]);
        var deleteNavMin = function(){
            tableNavMin.fnDeleteRow(rowNumber);
            reordering();
            $("#dialog-message").dialog('close');
        }
        messageAlertYesNo("Are you sure to delete data ?", "ui-icon ui-icon-notice", "Confirmation Message", deleteNavMin, closeDialog);
        return false;
    });

    $('#addNavDialog').die("click");
    $('#addNavDialog').live("click", function(){
        setTimeout(function() {
	        var table = $("#listNavMin #navMinTable").dataTable();
	        var rowPosition = $("#dialogNavMin #navMinForm #rowPosition").val();

	        saveNavMin();

	        function saveNavMin(){
	        	var effectiveDate = new Date($("#effectiveDate").datepicker('getDate')).getTime();
	            var liquidDate = new Date($("#liquidDate").datepicker('getDate')).getTime();
	            var effectiveTo = new Date($("#effectiveTo").datepicker('getDate')).getTime();

	        	if(($("#dialogNavMin #navMinForm #isMaxRow").val() == "false") 
	        			&& ($("#dialogNavMin #navMinForm #effectiveTo").val()=="")){
            		$("#dialogNavMin #navMinForm").find("span[id*='Error']").html("");

        			if($("#dialogNavMin #navMinForm #effectiveTo").val() == ""){
	                    $("#effectiveToError").html('Required').show();
	                }

	                if($("#dialogNavMin #navMinForm #maxCounter").val() == ""){
	                    $("#maxCounterError").html('Required').show();
	                }
	        	} else if($("#dialogNavMin #navMinForm #maxCounter").val() == ""){
            		$("#dialogNavMin #navMinForm").find("span[id*='Error']").html("");

	                if ($("#dialogNavMin #navMinForm #maxCounter").val() == ""){
	                    $("#maxCounterError").html('Required').show();
	                }
	        	} else if($("#dialogNavMin #navMinForm #maxCounter").val() == 0){
            		$("#dialogNavMin #navMinForm").find("span[id*='Error']").html("");

            		$("#maxCounterError").html('Max Counter must be greater than 0').show();
	        	} else if(effectiveTo && (effectiveTo < effectiveDate) || (effectiveTo == effectiveDate)){
	        		$("#dialogNavMin #navMinForm #effectiveTo").addClass('fieldError');
	        		$("#errMsgNav").html("Effective To must be greater than Effective Date");
	        	} else if((!$('#isMaturityDateMax').is(":checked")) 
	        			&& (effectiveTo && (effectiveTo > liquidDate))){
        			$("#dialogNavMin #navMinForm #effectiveTo").addClass('fieldError');
	        		$("#errMsgNav").html("Effective To must be less than Maturity Date");
	        	} else {
	            	$("#effectiveTo").removeClass('fieldError');
	            	$("#maxCounterError").removeClass('fieldError');
	            	$("#errMsgNav").html("");

	                var dataNavMins = table.fnGetData(parseFloat(rowPosition));
	                if(rowPosition != ""){
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var rows = table.fnGetNodes().length;

	                    for (i = 0; i < rows; i++){
	                    	var cells = table.fnGetData(i);
	                    	var effectiveTo = cells.effectiveTo;
	                    	var indexDialog = $("#dialogNavMin #navMinForm #rowPosition").val();

	                    	if($("#dialogNavMin #navMinForm #effectiveTo").isEmpty() == false && (i != indexDialog)){
	                    		if($("#dialogNavMin #navMinForm #effectiveTo").val() == effectiveTo){
	                    			$("#dialogNavMin #navMinForm #effectiveToError").html("Effective Date already Exist!");
	                    			found = true;
		                    	}
	                    	}
	                    }

	                    if(!found){
	                        //table.fnUpdate(dataNavMins.minnavKey = $("#dialogNavMin #navMinForm #minnavKey").val(),parseFloat(rowPosition), 0);
 	                        table.fnUpdate(dataNavMins.effectiveTo = $("#dialogNavMin #navMinForm #effectiveTo").val(),parseFloat(rowPosition), 0);
 	                        table.fnUpdate(dataNavMins.maxCounter = $("#dialogNavMin #navMinForm #maxCounterStripped").val(),parseFloat(rowPosition), 1);
 	                        
	                        $('#dialogNavMin').dialog('close');
	                    }
	                } else {
	                    var found = false;
	                    // CHECKED DUPLICATE ROW
	                    var rows = table.fnGetNodes().length;
	                    for (i = 0; i < rows; i++){
	                    	var cells = table.fnGetData(i);
	                    	var effectiveTo = cells.effectiveTo;

	                    	if($("#dialogNavMin #navMinForm #effectiveTo").isEmpty() == false){
		                    	if($("#dialogNavMin #navMinForm #effectiveTo").val() == effectiveTo){
		                    		$("#dialogNavMin #navMinForm #effectiveToError").html("Effective Date already Exist!");
		                    		found = true;
	 	                            break;
		                    	}
	                    	}
	                    }

	                    if(!found){
	                        var dataNavMin = new Object();
	                        //dataNavMin.rgProduct = new Object();
	                        dataNavMin.effectiveTo = $("#dialogNavMin #navMinForm #effectiveTo").val();
	                        dataNavMin.maxCounter = $("#dialogNavMin #navMinForm #maxCounterStripped").val();
	                        dataNavMin.minnavKey = $("#dialogNavMin #navMinForm #minnavKey").val();
	                        //dataNavMin.rgProduct.productCode = $("#productCode").val();
	                        table.fnAddData(dataNavMin);
	                        $('#dialogNavMin').dialog('close');
	                    }
	                }
	                return false;       
	            }
	        }
       }, 500);
    });

    //Button New Data for Nav Min
    $('.buttons #newNavDialog').click(function(){
        selectedRow = null;
        $("#dialogNavMin").dialog('open');
        $('.ui-widget-overlay').css('height',$('body').height());
        $("#dialogNavMin input:text").val(""); 
        $("#dialogNavMin input:hidden").val("");
        $("#dialogNavMin #effectiveTo").val("");
        $("#dialogNavMin #maxCounter").val("");
        $("#dialogNavMin #navMinForm .errmsg").html("");
        $("#dialogNavMin #navMinForm").find("span[id*='Error']").html("");
        $("#dialogNavMin #navMinForm #isMaxRow").val(false);
        $("input[name=nav.effectiveTo]").removeAttr("disabled");
        $("#effectNav label span").show();
        return false;
    });
});

function navMin(data) {
    var navMins;

    if ('${navMins}' != ''){
    	navMins = ${navMins.raw()};
    } else {
    	navMins = new Array();
    }

    var fmtDate = '${appProp.jqueryDateFormat}';
    var stringDate;
	var effectiveTo;

    tableNavMin = 
        $('#form #listNavMin #navMinTable').dataTable({
            aaData: navMins,
            aoColumns: [ {
                            fnRender: function(obj) {
                                var controls;

						 		if(obj.aData.effectiveTo){
						 			stringDate = obj.aData.effectiveTo.toString();
						 			
						 			if (stringDate.substr(2,1) != "/") {
							 			effectiveTo = $.datepicker.formatDate(fmtDate, new Date(obj.aData.effectiveTo));
							 		} else {
							 			effectiveTo =  obj.aData.effectiveTo;
							 		}
						 			
						 			controls = effectiveTo;
	                                controls += '<input type="hidden" name="rgNavMinimums[' + obj.iDataRow + '].effectiveTo" value="' + effectiveTo + '" />';
						 		} else {
						 			controls = "Max";
						 			///controls += '<input type="hidden" name="rgNavMinimums[' + obj.iDataRow + '].effectiveTo" value="' + obj.aData.effectiveTo + '" />';
						 		}
                                return controls;
                             }
                          },{
                              sClass: 'numeric',
                              fnRender: function(obj) {
                                  var controls;
                                  
                                  if((obj.aData.maxCounter == 0) || (obj.aData.maxCounter == null)){
                                	  controls = "";
                                  } else {
                                	  controls = obj.aData.maxCounter;
                                  }

                                  controls += '<input type="hidden" name="rgNavMinimums[' + obj.iDataRow + '].maxCounter" value="' + obj.aData.maxCounter + '" />';
                                  return controls;
                              }
                          },{
                              fnRender: function(obj) {
                            	  var controls;
                                  
                            	  if(obj.aData.effectiveTo){
                            		  controls = '<center><input id="deleteButtonNav" type="button" value="Delete" #{if readOnly}disabled="disabled"#{/if} /></center>';
                            	  } else {
                            		  controls = '<center><input id="deleteButtonNav" type="button" value="Delete" disabled="disabled" /></center>';
                            	  }

                                  controls += '<input type="hidden" name="rgNavMinimums[' + obj.iDataRow + '].minnavKey" value="' + obj.aData.minnavKey + '" />';
                                  //controls += '<input type="hidden" name="rgNavMinimums[' + obj.iDataRow + '].rgProduct.productCode" value="' + obj.aData.rgProduct.productCode + '" />';
                                  
                                  return controls;
                              }
                          }
                        ],
        aaSorting:[[0,'asc']],
        bAutoWidth: false,      
        bDestroy: true,
        bFilter: false,
        bInfo: false,
        //bRetrieve:true,
        //bServerSide: true,
        bJQueryUI: true,
        bPaginate: false,
        bSearch: false,
        bLengthChange: false,
        isDisplayLength:10                                      
    });

    $("#listNavMin #navMinTable").removeAttr('style');
    $("#listNavMin #navMinTable tbody tr td").die('click');
    $("#listNavMin #navMinTable tbody tr td").live('click', function(){
        var rowPos= $(this).parents('tr');
        var rowPosNumber = tableNavMin.fnGetPosition(rowPos[0]);
        if(rowPosNumber != null)
		{
        	var pos = tableNavMin.fnGetPosition(this);
            cell = tableNavMin.fnGetData(this.parentNode);
            if (pos[1] != 2) {
            	dataNavMins = tableNavMin.fnGetData(this.parentNode);
                $('#dialogNavMin #navMinForm').removeClass('fieldError');
                $("#dialogNavMin #navMinForm .errmsg").html("");
                $("#dialogNavMin #navMinForm").find("span[id*='Error']").html("");
                $("#dialogNavMin #navMinForm #rowPosition").val(rowPosNumber);
                $("#dialogNavMin #navMinForm #effectiveTo").val(dataNavMins.effectiveTo);
                $("#dialogNavMin #navMinForm #maxCounter").autoNumericSet(dataNavMins.maxCounter);
                $("#dialogNavMin #navMinForm #maxCounterStripped").val(dataNavMins.maxCounter);
                
                $("#dialogNavMin #navMinForm #isMaxRow").val("false");
                $("input[name=nav.effectiveTo]").removeAttr("disabled");
                //$("#effectNav label span.req").show();
                if((dataNavMins.effectiveTo == null) || (dataNavMins.effectiveTo == '') && (dataNavMins.maxCounter != null))
                {
                	$("#dialogNavMin #navMinForm #isMaxRow").val("true");
                	$("input[name=nav.effectiveTo]").attr("disabled", true);
                	$("#effectNav label span.req").hide();
                	if(dataNavMins.maxCounter < 1)
	    	 		{
                		$("#dialogNavMin #navMinForm #maxCounter").val("");
	    	 		};
                } else {
                	$("#effectNav label span").show();
                }

                if(dataNavMins.effectiveTo){
    	 			stringDate = dataNavMins.effectiveTo.toString();

    	 			if (stringDate.substr(2,1) != "/") {
    		 			effectiveTo = $.datepicker.formatDate(fmtDate, new Date(dataNavMins.effectiveTo));
    		 			$("#dialogNavMin #navMinForm #effectiveTo").val(effectiveTo);
    		 		};
    	 		}
                
                //buat ganti focus dari focus calender ke focus ke maxCounter
//                $("#dialogNavMin #navMinForm #effectiveTo").datepicker("option", "disabled", true);
//                $("#dialogNavMin #navMinForm #effectiveTo").datepicker('disable');
//            	$("#maxCounter").focus(function(){
//            	    $("#dialogNavMin #navMinForm #effectiveTo").datepicker('enable');
//            	});
                
                $("#dialogNavMin").dialog('open');
                $('.ui-widget-overlay').css('height',$('body').height());
            }
		}
    });
}

function reordering() {
    var grid = $("#listNavMin #navMinTable tbody");
    var trs = $("tr", grid);

    $.each(trs, function(idx, data){
        var hiddens = $("[type=hidden]", $(this));

        //$(hiddens).eq(0).attr("name", "rgNavMinimums["+idx+"].minnavKey");
        //$(hiddens).eq(1).attr("name", "rgNavMinimums["+idx+"].productCode");
        $(hiddens).eq(0).attr("name", "rgNavMinimums["+idx+"].effectiveTo");
        $(hiddens).eq(1).attr("name", "rgNavMinimums["+idx+"].maxCounter");
    });
}