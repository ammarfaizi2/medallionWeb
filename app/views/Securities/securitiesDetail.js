//------Start MAIN Function -------//
$(function(){
	popUpIssuer();
    changePercentage();

    $('#depositoryCode').children().eq(0).remove();

    $('#depositoryCode').change( function() {
    	if($('#depositoryCode').val() == 'DEPOSITORY_CODE-N/A')
    	{
    		$(".externalCodeArea .req").remove();
    		//$("#externalCodeError").remove();
    	}
    	else
    	{
    		if($(".externalCodeArea .req").length < 1)
    		{
    			$(".externalCodeArea label").append($("<span class='req'>*</span>"));
    		}
    	}
    });

    if($('#depositoryCode').val() == 'DEPOSITORY_CODE-N/A')
	{
		$(".externalCodeArea .req").remove();
		//$("#externalCodeError").remove();
	}
	else
	{
		if($(".externalCodeArea .req").length < 1)
		{
			$(".externalCodeArea label").append($("<span class='req'>*</span>"));
		}
	}
    
    var gridCoupon;
    $('input.numericNoMin').autoNumeric({vMAx:'999999999999.', mDec:10});

    $('input.numericNoMin').live('blur', function() {
        var el = $(this);
        var id = this.id;
        var stripped = "#" + id + "Stripped";
        if (el.val() == '') {
            el.siblings(stripped).val('');
            return;
        }
        el.siblings(stripped).val(el.autoNumericGet());
    });
    
    $('input.percentIntRate').autoNumeric({vMax: '100.000000'});
    $('input.percentIntRate').live('blur', function() {
        var el = $(this);
        var id = this.id;
        var stripped = "#" + id + "Stripped";
        if (el.val() == '') {
            el.siblings(stripped).val('');
            return;
        }
        el.siblings(stripped).val(el.autoNumericGet());
    });
    
    $('input.numericNoMin18').autoNumeric({vMAx:'99999999999999999999', mDec:'0'});

    $('input.numericNoMin18').live('blur', function() {
        var el = $(this);
        var id = this.id;
        var stripped = "#" + id + "Stripped";
        if (el.val() == '') {
            el.siblings(stripped).val('');
            return;
        }
        el.siblings(stripped).val(el.autoNumericGet());
    });
    
//    $('#listedShare').keyup(function(e){
//    	if (/\D/g.test(this.value)) this.value = this.value.replace(/\D/g,'');
//    });
    
    $('#listedShare').css('text-align','right');
//    $('#listedShare').attr('maxlength','20');
    
    $('#listedShare').blur(function(){
    	if ($(this).val() == "") {
    		$('#listedShare').addClass('fieldError');
    		$('#listedShareError').html('Required');
    		$('#listedShare').focus();
    	}else{
    		$('#listedShare').removeClass('fieldError');
    		$('#listedShareError').html('');
//    		$('#listedShareStripped').val($('#listedShare').val());
    	}
    });
	
	$('#maturityDateMm').blur(function(){
		$('#maturityDateMm').removeClass('fieldError');
	});

//    $('input.numerics').autoNumeric();
    if (('${mode}'=='entry') || (('${mode}'=='edit') &&(('${security?.recordStatus?.decodeStatus()}'=='Reject')|| ($("#status").val() == 'R' )))) {
        $('input[name=isActive]').attr("disabled", "disabled");
    }
    
    
    if (('${mode}'=='entry') || ('${mode}'=='edit') ) {
    	var secTypeDeposit = '${securityTypesDeposit}'.split("|");
        for(var i = 0 ; i < secTypeDeposit.length; i++) {

        	if (secTypeDeposit[i] == $('#securityType').val()) {
        		$('input[name=isScript]').attr('disabled', false);
        		break;
        		
        	}else {
        		$('input[name=isScript]').attr('disabled', 'disabled');
        	}
        }
    }
    
    $('#apoloTypeId').lookup({
		list:'@{Pick.lookups()}?group=JENIS_EFEK',
		get:{
			url:'@{Pick.lookup()}?group=JENIS_EFEK',
			success: function(data) {
				$('#apoloTypeId').removeClass('fieldError');
				$('#apoloTypeKey').val(data.code);
				$('#apoloTypeDesc').val(data.description);
				$('#h_apoloTypeDesc').val(data.description);
				
			},
			error: function(data) {
				$('#apoloTypeId').addClass('fieldError');
				$('#apoloTypeId').val('');
				$('#apoloTypeDesc').val('NOT FOUND');
				$('#apoloTypeKey').val('');
				$('#h_apoloTypeDesc').val('');
			}
		},
		description:$('#apoloTypeDesc'),
		help:$('#apoloTypeHelp')
	});
	
	$('#apoloTypeId').change(function(){
		if ($(this).val()==''){
			$('#apoloTypeId').val('');
			$('#apoloTypeDesc').val('');
			$('#apoloTypeKey').val('');
			$('#h_apoloTypeDesc').val('');
		}
	});
    
    $("input[name='isActive']").change(function() {
        $("input[name='security.isActive']").val($("input[name='isActive']:checked").val());
    });

    if(('${security?.isFraction}'== false) || ('${confirming}') || ('${mode}'=='view'))
    {
        $(".fraction").attr("disabled","disabled");
    }
    else
    {
    	if ($("input[name='security.isFraction']:checked").val() == "true"){
            $("#fractionRatioSource").attr('disabled', false);
            $("#fractionRatioTarget").attr('disabled', false);
            $(".isFractionFi label span").html("*");
            //$("#fractionRatioSourceStripped").val($("#fractionRatioSource").val());
            //$("#fractionRatioTargetStripped").val($("#fractionRatioTarget").val());
        }else{
            $(".isFractionFi label span").html("");
            $("#fractionRatioSource").attr('disabled', 'disabled');
            $("#fractionRatioTarget").attr('disabled', 'disabled');
            $("#fractionRatioSource").val("");
            $("#fractionRatioTarget").val("");      
            $("#fractionRatioSourceStripped").val("");
            $("#fractionRatioTargetStripped").val("");  
            $("#fractionRatioSource").removeClass('fieldError');
            $("#fractionRatioSourceError").html('');
            $("#fractionRatioTarget").removeClass('fieldError');
            $("#fractionRatioTargetError").html('');
        } 
        //$(".fraction").attr("disabled", false);
    }

    valuationMethod();  

    if('${confirming}')
    {
        setChecked();
        $('.isTrade').attr('disabled', 'disabled');
        $('.isAfs').attr('disabled', 'disabled');
        $('.isHtm').attr('disabled', 'disabled');
    }

    $('#accordionAdditional').accordion({
        collapsible: false,
        autoHeight: false 
    });

    //for set default value
    $('.buttons button').button();
    $('#tabs').tabs();
    $('#tabs').css('height','500' );
    //$('#tabs').tabs("disable", "6");
    //changeTabs 21-03-2014
    $('#tabs').tabs("disable", "4");

    $('#generateButton').button();
    $("#generateButton").button("disable");

    if($('#h_securityClass').val() == 'MM')
    {
        $("#frequencyMmHidden").attr('name', 'security.frequency.lookupId');
        $("#frequencyMmHidden").val($('#frequencyMm').val());
        $("#frequencyMm").attr('disabled', 'disabled');
    }

        //Lookup Security Type
        $('#securityType').lookup({
            list:'@{Pick.securityTypes()}',
            get:{
                url:'@{Pick.securityType()}',
                success: function(data){
                        $('#securityType').removeClass('fieldError');
                        $('#securityTypeDesc').val(data.description);
                        $('#h_securityTypeDesc').val(data.description);
                        $('#securityClass').val(data.securityClassCode);
                        $('#securityClassHidden').val($('#securityClass').val());
                        $('#securityClassDesc').val(data.securityClassDesc);
                        $('#securityClassDescription').val(data.securityClassDesc);
                        $('#isPrice').val(data.isPrice);
                        $('#isDiscounted').val(data.isDiscounted);
                        $('#priceUnit').val(data.priceUnit);
                        $('#hasInterest').val(data.hasInterest);
                                                
                        if ($('#priceUnit').val() == '0.01') {
                        	$('#parPrice').val(100);
                        	$('#parPriceStripped').val(100);
                        }else {
                        	$('#parPrice').val(1);
                        	$('#parPriceStripped').val(1);
                        }
                        
                        //jika securityType sama dengan yg ada di DT001 maka enable, default yes,. else? yes disabled
                        var secTypeDeposit = '${securityTypesDeposit}'.split("|");
                        for(var i = 0 ; i < secTypeDeposit.length; i++) {

                        	if (secTypeDeposit[i] == $('#securityType').val()) {
                        		$('input[name=isScript]').attr('disabled', false);
                        		$("#isScript").val('true');
                        		$('#isScript1').attr('checked',"true");
                     			$('#isScript2').attr('checked',"");
                     			$('#isScriptHidden').val(true);
                        		break;
                        		
                        	}else {
                        		$('input[name=isScript]').attr('disabled', 'disabled');
                        		$('#isScriptHidden').val(false);
                        		$("#isScript").val('false');
                        		$('#isScript1').attr('checked',"");
                     			$('#isScript2').attr('checked',"true");
                        	}
                        }
                        
                        
                        
                            if($("#securityClass").val()=="EQ"){
                                $("#DI").css("display", "none");
                                $("#EQ").css("display", "");
                                $("#FI").css("display", "none");
                                $("#DP").css("display", "none");
                                $("#MM").css("display", "none");
                                //$('#tabs').tabs("disable", "6");
                                //changeTabs 21-03-2014
                                $('#tabs').tabs("disable", "4");
                                
                                $("#issueDateEq").attr('disabled', false);
                                $("#issueDateFi").attr('disabled', 'disabled');
                                $("#issueDateMm").attr('disabled', 'disabled');

                                if($("input[name='security.isExpire']:checked").val() == 'true') {
                                    $("#expiredDateEq").attr('disabled', false);
                                    $(".expiredDate label span").html("*");
                                } else {
                                    $("#expiredDateFi").attr('disabled', 'disabled');
                                    $("#expiredDateMm").attr('disabled', 'disabled');
                                    $('.expired').val("");
                                    $(".expiredDate label span").html("");
                                }
                                
                            }
                            else if ($("#securityClass").val() == "MM") {
                                $("#MM").css("display", "");
                                $("#EQ").css("display", "none");
                                $("#FI").css("display", "none");
                                //$('#tabs').tabs("disable", "6");
                                //changeTabs 21-03-2014
                                $('#tabs').tabs("disable", "4");

                                $("#serialCodeFi").attr('disabled', 'disabled');
                                $("#serialNumberFi").attr('disabled', 'disabled');
                                $("#maturityDateMm").attr('disabled', false);
                                $("#maturityDateFi").attr('disabled', 'disabled');

                                $("#frequencyMm").val("FREQUENCY-Y");
                                $("#frequencyMmHidden").attr('name', 'security.frequency.lookupId');
                                $("#frequencyMmHidden").val($('#frequencyMm').val());
                                $("#frequencyMm").attr('disabled', 'disabled');
                                //set lookupId
                                
                                $("#frequencyFi").attr('disabled', 'disabled');
                                
                                $("#issueDateEq").attr('disabled', 'disabled');
                                $("#issueDateFi").attr('disabled', 'disabled');
                                $("#issueDateMm").attr('disabled', false);
                                
                                if($("input[name='security.isExpire']:checked").val() == 'true') {
                                    $("#expiredDateMm").attr('disabled', false);
                                    $(".expiredDate label span").html("*");
                                } else {
                                    $("#expiredDateEq").attr('disabled', 'disabled');
                                    $("#expiredDateFi").attr('disabled', 'disabled');
                                    $('.expired').val("");
                                    $(".expiredDate label span").html("");
                                }

                                $("#accrualBaseMm").attr('disabled', false);
                                $("#accrualBaseFi").attr('disabled', 'disabled');
                                $("#yearBaseMm").attr('disabled', false);
                                $("#yearBaseFi").attr('disabled', 'disabled');
                            }           
                            else {
                                $("#DI").css("display", "none");
                                $("#EQ").css("display", "none");
                                $("#FI").css("display", "");
                                $("#DP").css("display", "none");
                                $("#MM").css("display", "none");
                                //$('#tabs').tabs("enable", "6");
                                //changeTabs 21-03-2014
                                $('#tabs').tabs("enable", "4");
                                
                                $("#serialCodeFi").attr('disabled', false);
                                $("#serialNumberFi").attr('disabled', false);
                                
                                $("#maturityDateFi").attr('disabled', false);
                                $("#maturityDateMm").attr('disabled', 'disabled');
                                
                                $("#accrualBaseFi").attr('disabled', false);
                                $("#accrualBaseMm").attr('disabled', 'disabled');
                                
                                $("#yearBaseFi").attr('disabled', false);
                                $("#yearBaseMm").attr('disabled', 'disabled');

                                $("#frequencyFi").attr('disabled', false);
                                $("#frequencyMm").attr('disabled', 'disabled');
                                
                                $("#issueDateFi").attr('disabled', false);
                                $("#issueDateEq").attr('disabled', 'disabled');
                                $("#issueDateMm").attr('disabled', 'disabled');
                                
                                
                                if($("input[name='security.isExpire']:checked").val() == 'true') {
                                    $("#expiredDateFi").attr('disabled', false);
                                    $(".expiredDate label span").html("*");
                                } else {
                                    $("#expiredDateEq").attr('disabled', 'disabled');
                                    $("#expiredDateMm").attr('disabled', 'disabled');
                                    $('.expired').val("");
                                    $(".expiredDate label span").html("");
                                }
                            }
                            $("#securityId").focus();
                            
                            //changeCombo();
                            changeParPrice();
                            defaultTabValuation();
                            
                            changeValueAmortizationTradeIfHasInterest();
                    },
                    error: function(data){
                        $('#securityType').addClass('fieldError');
                        $('#securityType').val('');
                        $('#securityTypeDesc').val('NOT FOUND');
                        $('#h_securityTypeDesc').val('');
                        $('#securityClass').val('');
                        $('#securityClassDesc').val('');
                        $('#h_securityClassDesc').val('');
                    }
            },
            
            //filter:$('#securityType'),
            description:$('#securityTypeDesc'),
            help:$('#securityTypeHelp')
        });
        
        $('input[name=isScript]').change(function(){
        	if ($(this).val() == "true") {
        		$('#isScriptHidden').val(true);
        	}else {
        		$('#isScriptHidden').val(false);
        	}
        });
        
        
        //$('#securityType').blur(function(){
        //  $('#securityClassHidden').val($('#securityClass').val());
        //});
        //--Lookup Issuer Code

//        $('#issuer').dynapopup('PICK_GN_THIRD_PARTY', 'THIRD_PARTY-ISSUER', 'currency', 
//			function(data){
//	        	$('#issuer').removeClass('fieldError');
//	            $('#issuerKey').val(data.code);
//	            $('#issuerDesc').val(data.description);
//	            $('#h_issuerDesc').val(data.description);
//			},
//			function(){
//				$('#issuer').addClass('fieldError');
//                $('#issuer').val('');
//                $('#issuerKey').val('');
//                $('#issuerDesc').val('NOT FOUND');
//                $('#h_issuerDesc').val('');
//			}
//		);

//        $('#issuer').lookup({
//            list:'@{Pick.thirdParties()}?type=THIRD_PARTY-ISSUER',
//            //get:'@{Pick.thirdParty()}?type=THIRD_PARTY-ISSUER',
//            get:{
//                url:'@{Pick.thirdParty()}?type=THIRD_PARTY-ISSUER',
//                success: function(data){
//                    $('#issuer').removeClass('fieldError');
//                    $('#issuerKey').val(data.code);
//                    $('#issuerDesc').val(data.description);
//                    $('#h_issuerDesc').val(data.description);
//                },
//                error: function(data){
//                    $('#issuer').addClass('fieldError');
//                    $('#issuer').val('');
//                    $('#issuerKey').val('');
//                    $('#issuerDesc').val('NOT FOUND');
//                    $('#h_issuerDesc').val('');
//                }
//            },
//            //key:$('#issuerKey'),
//            description:$('#issuerDesc'),
//            help:$('#issuerHelp')
//        });

        //--Lookup currency
        $('#currency').lookup({
            list:'@{Pick.currencies()}',
            get:{
                url:'@{Pick.currency()}',
                success: function(data){
                    $('#currency').removeClass('fieldError');
                    $('#currencyDesc').val(data.description);
                    $('#h_currencyDesc').val(data.description);
                },
                error: function(data){
                    $('#currency').addClass('fieldError');
                    $('#currency').val('');
                    $('#currencyDesc').val('NOT FOUND');
                    $('#h_currencyDesc').val('');
                }
            },
            description:$('#currencyDesc'),
            help:$('#currencyHelp')
        });
        
        //--Lookup sub sector
        $('#subSector').lookup({
            list:'@{Pick.lookups()}?group=SUB_SECTOR',
            get:{
                url:'@{Pick.lookup()}?group=SUB_SECTOR',
                success: function(data){
                    $('#subSector').removeClass('fieldError');
                    $('#subSectorId').val(data.code);
                    $('#subSectorDesc').val(data.description);
                    $('#h_subSectorDesc').val(data.description);
                },
                error: function(data){
                    $('#subSector').addClass('fieldError');
                    $('#subSector').val('');
                    $('#subSectorId').val('');
                    $('#subSectorDesc').val('NOT FOUND');
                    $('#h_subSectorDesc').val('');
                }
            },
            description:$('#subSectorDesc'),
            help:$('#subSectorHelp')
        });
        
        //--Lookup Market Risk
        $('#marketRisk').lookup({
            list:'@{Pick.lookups()}?group=COUNTRY',
            get:{
                url:'@{Pick.lookup()}?group=COUNTRY',
                success: function(data){
                    $('#marketRisk').removeClass('fieldError');
                    $('#marketRiskId').val(data.code);
                    $('#marketRiskDesc').val(data.description);
                    $('#h_marketRiskDesc').val(data.description);
                },
                error: function(data){
                    $('#marketRisk').addClass('fieldError');
                    $('#marketRisk').val('');
                    $('#marketRiskId').val('');
                    $('#marketRiskDesc').val('NOT FOUND');
                    $('#h_marketRiskDesc').val('');
                }
            },
            description:$('#marketRiskDesc'),
            help:$('#marketRiskHelp')
        });

        $('#registrarId').val('${registrarNA.lookupId}');
        $('#registrarDesc').val('${registrarNA.lookupDescription}');
        $('#h_registrarDesc').val('${registrarNA.lookupDescription}');
        
        if ($("#listedShare").val() == "") {
        	$("#listedShare").val(0);
        	$("#listedShareStripped").val(0);
        }
        
        //--Lookup Registrar
//        $('#registrar').lookup({
//            list:'@{Pick.lookups()}?group=REGISTRAR',
//            get:{
//                url:'@{Pick.lookup()}?group=REGISTRAR',
//                success: function(data){
//                    $('#registrar').removeClass('fieldError');
//                    $('#registrarId').val(data.code);
//                    $('#registrarDesc').val(data.description);
//                    $('#h_registrarDesc').val(data.description);
//                },
//                error: function(data){
//                    $('#registrar').addClass('fieldError');
//                    $('#registrar').val('');
//                    $('#registrarId').val('');
//                    $('#registrarDesc').val('NOT FOUND');
//                    $('#h_registrarDesc').val('');
//                }
//            },
//            description:$('#registrarDesc'),
//            help:$('#registrarHelp')
//        });

        //--lookup Cap Gain Tax
        $('#capGainTax').lookup({
            list:'@{Pick.taxMasters()}',
            get:{
                url:'@{Pick.taxMaster()}',
                success: function(data){
                    $('#capGainTax').removeClass('fieldError');
                    $('#capGainTaxDesc').val(data.description);
                    $('#h_capGainTaxDesc').val(data.description);
                },
                error: function(data){
                    $('#capGainTax').addClass('fieldError');
                    $('#capGainTax').val('');
                    $('#capGainTaxDesc').val('NOT FOUND');
                    $('#h_capGainTaxDesc').val('');
                }
            },
            description:$('#capGainTaxDesc'),
            help:$('#capGainTaxHelp')
        });

        //--lookup Cash Div Tax
//      $('#cashDivTax').lookup({
//          list:'@{Pick.taxMasters()}',
//          get:{
//              url:'@{Pick.taxMaster()}',
//              success: function(data){
//                  $('#cashDivTax').removeClass('fieldError');
//                  $('#cashDivTaxDesc').val(data.description);
//                  $('#h_cashDivTaxDesc').val(data.description);
//              },
//              error: function(data){
//                  $('#cashDivTax').addClass('fieldError');
//                  $('#cashDivTax').val('');
//                  $('#cashDivTaxDesc').val('NOT FOUND');
//                  $('#h_cashDivTaxDesc').val('');
//              }
//          },
//          description:$('#cashDivTaxDesc'),
//          help:$('#cashDivTaxHelp')
//      });

        //--lookup interest Tax
//      $('#interestTax').lookup({
//          list:'@{Pick.taxMasters()}',
//          get:{
//              url:'@{Pick.taxMaster()}',
//              success: function(data){
//                  $('#interestTax').removeClass('fieldError');
//                  $('#interestTaxDesc').val(data.description);
//                  $('#h_interestTaxDesc').val(data.description);
//              },
//              error: function(data){
//                  $('#interestTax').addClass('fieldError');
//                  $('#interestTax').val('');
//                  $('#interestTaxDesc').val('NOT FOUND');
//                  $('#h_interestTaxDesc').val('');
//              }
//          },
//          description:$('#interestTaxDesc'),
//          help:$('#interestTaxHelp')
//      });

        $('input.lookup').change( function(){
            var el = this;
            var id = this.id;
            if($(el).val()==""){
                $("#"+id+"").removeClass('fieldError');
                $("#"+id+"Desc").val('');
                $("#h_"+id+"Desc").val('');
            }
            if (id == "securityType") {
                $('#securityClass').val('');
                $('#securityClassHidden').val('');
                $('#securityClassDesc').val('');
                $('#securityClassDescription').val('');
                $('#isPrice').val('');
                $('#isDiscounted').val('');
                $('#hasInterest').val('');
                defaultTabValuation();
            }
        });
        
        $('.expired').attr("disabled", "disabled");
        
        if ($("#checkIsExpire").val()== 'true') {
            $('.expired').attr("disabled", "disabled");
        }

        $(".expiredDate label span").html("");
        if($("input[name='security.isExpire']:checked").val() == 'true') {
            $('.expired').attr("disabled", false);
            $(".expiredDate label span").html("*");
        } 
        
        $("#issueDateEq").change(function() {
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 =  thisId ;
            var compare2 = "#expiredDateEq";
            var compare3 = "";
            var compare1Format =  new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format = "";
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });
        
        $("#expiredDateEq").change(function() {
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 = "#issueDateEq" ;
            var compare2 = thisId;
            var compare3 = "";
            var compare1Format =  new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format = "";
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });

        $("#issueDateMm").change(function() {
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 =  thisId ;
            var compare2 = "#expiredDateMm";
            var compare3 = "";
            var compare1Format =  new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format = "";
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });
        
        $("#expiredDateMm").change(function() {
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 = "#issueDateMm" ;
            var compare2 = thisId;
            var compare3 = "";
            var compare1Format =  new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format = "";
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });
        
        $("#issueDateFi").change(function(){
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 =  thisId ;
            var compare2 = "#maturityDate";
            var compare3 = "#expiredDateFi";
            var compare1Format =  new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format =  new Date($(compare3).datepicker('getDate')).getTime();
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            
            var couponDateFormat = new Date($("#couponDate").datepicker('getDate')).getTime();
            
            if ($(thisId+"Error").html()!= "Invalid date format") { 
                if (($("#couponDate").val() != "") && ($("#couponDateError").html() != conditionMessageError)) {
                    $("#couponDate").removeClass("fieldError");
                    $("#couponDateError").html("");
                    if ((couponDateFormat < compare1Format) || (couponDateFormat > compare2Format)) {
                        $("#couponDate").addClass("fieldError");
                        $("#couponDateError").html("Invalid date value").show();
                    } 
                }
            }

            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });
        
        $("#maturityDate").change(function() {
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 = "#issueDateFi";
            var compare2 = thisId;
            var compare3 = "#expiredDateFi";
            var compare1Format = new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format = new Date($(compare3).datepicker('getDate')).getTime();
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            
            var couponDateFormat = new Date($("#couponDate").datepicker('getDate')).getTime();
            
            if (($("#couponDate").val() != "") && ($("#couponDateError").html() != conditionMessageError)) {
                $("#couponDate").removeClass("fieldError");
                $("#couponDateError").html("");
                if ((couponDateFormat < compare1Format) || (couponDateFormat > compare2Format)) {
                    $("#couponDate").addClass("fieldError");
                    $("#couponDateError").html("Invalid date value").show();
                } 
            }
            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });

        $("#expiredDateFi").change(function() {
            var id = this.id;
            var thisId = "#" + id;
            var errorId = "#" + id + "Error";
            var compare1 = "#issueDateFi" ;
            var compare2 =  "#maturityDate";
            var compare3 = thisId;
            var compare1Format = new Date($(compare1).datepicker('getDate')).getTime();
            var compare2Format = new Date($(compare2).datepicker('getDate')).getTime();
            var compare3Format = new Date($(compare3).datepicker('getDate')).getTime();
            var conditionMessageError = "Invalid date format";
            var errorMsg = "Invalid date value";
            validateCompare(thisId, errorId, compare1, compare2, compare3, errorMsg, compare1Format, compare2Format, compare3Format, conditionMessageError);
        });

    /*  function validateCompare(thisId, errorId, issueDate, expireDate, maturityDate, errorMsg) {
             if (($(expireDate).val() != "") && ($(issueDate).val() != "") && ($(issueDate+"Error").html() != "Invalid date") && ($(expireDate+"Error").html() != "Invalid date")) {
                var expireNewDate = new Date($(expireDate).val());
                var issueNewDate = new Date($(issueDate).val());
                var maturityNewDate = new Date($(maturityDate).val());
                
                if(maturityNewDate == "") {
                    if (expireNewDate.getTime() < issueNewDate.getTime()) {
                        $(thisId).addClass('fieldError');
                        $(errorId).html(errorMsg).show();
                    } else {
                        if (thisId == issueDate ) {
                            if ($(expireDate+"Error").html() != "Invalid date") {
                                $(expireDate).removeClass('fieldError');
                                $(expireDate+"Error").html("");
                            }
                        } else if (thisId == expireDate) {
                            if ($(issueDate+"Error").html() != "Invalid date") {
                                $(issueDate).removeClass('fieldError');
                                $(issueDate+"Error").html("");
                            }
                        }   
                    }
                }
            }
        }
    */  
        
        $("input[name='security.isExpire']").click(function(){
            if($("input[name='security.isExpire']:checked").val() == 'true') {
                $('.expired').attr("disabled", false);
                $(".expiredDate label span").html("*");
            } else {
                $('.expired').attr("disabled", "disabled");
                $('.expired').val("");
                $(".expiredDate label span").html("");
            }
        });

        //--view security type default by value
        if($("#securityClass").val()=="EQ"){
            $("#MM").css("display", "none");
            $("#EQ").css("display", "");
            $("#FI").css("display", "none");
            //$('#tabs').tabs("disable", "6");
            //changeTabs 21-03-2014
            $('#tabs').tabs("disable", "4");
    
        }       
        else if ($("#securityClass").val() == "MM") {
            $("#MM").css("display", "");
            $("#EQ").css("display", "none");
            $("#FI").css("display", "none");
            //$('#tabs').tabs("disable", "6");
            //changeTabs 21-03-2014
            $('#tabs').tabs("disable", "4");

            $("#serialCodeFi").attr('disabled', 'disabled');
            $("#serialNumberFi").attr('disabled', 'disabled');
            $("#maturityDateFi").attr('disabled', 'disabled');
            $("#accrualBaseFi").attr('disabled', 'disabled');
            $("#yearBaseFi").attr('disabled', 'disabled');
        }           
        else {
            $("#MM").css("display", "none");
            $("#EQ").css("display", "none");
            $("#FI").css("display", "");
            //$('#tabs').tabs("enable", "6");
            //changeTabs 21-03-2014
            $('#tabs').tabs("enable", "4");
            
            $("#maturityDateMm").attr('disabled', 'disabled');
            $("#issueDateEq").attr('disabled', 'disabled');
            $("#accrualBaseMm").attr('disabled', 'disabled');
            $("#yearBaseMm").attr('disabled', 'disabled');
        }

        //--call function gridCoupon
        gridCoupons();
    
        //--Even when change
        $("#issueDateFi").change(function() {
            populate();
            $("#issueDateEq").attr('disabled', 'disabled');
            $("#issueDateMm").attr('disabled', 'disabled');
        });
        var freq;
        
        if($("#frequencyFi").val() == "FREQUENCY-M") {
            freq = 1;
        } else if($("#frequencyFi").val() == "FREQUENCY-Q") {
            freq = 3;
        } else if($("#frequencyFi").val() == "FREQUENCY-H") {
            freq = 6;
        } else if($("#frequencyFi").val() == "FREQUENCY-Y") {
            freq = 12;
        }
        
        $("#frequencyFi").change(function() {
            if($("#frequencyFi").val() == "FREQUENCY-M") {
                freq = 1;
            } else if($("#frequencyFi").val() == "FREQUENCY-Q") {
                freq = 3;
            } else if($("#frequencyFi").val() == "FREQUENCY-H") {
                freq = 6;
            } else if($("#frequencyFi").val() == "FREQUENCY-Y") {
                freq = 12;
            }
            
            $("#frequencyMm").attr('disabled', 'disabled');
            $("#frequencyMmHidden").attr('name', 'frequencyHidden');
            if ((($("#maturityDate").val() != "") || ($("#maturityDate").val() != null)) && (($("#couponDate").val() != "") || ($("#couponDate").val() != null))) {
                var maturityDateCal = new Date($("#maturityDate").datepicker('getDate'));
                var monthMaturity = maturityDateCal.getMonth();
                var yearMaturity = maturityDateCal.getFullYear();
                
                var couponDateCal = new Date($("#couponDate").datepicker('getDate'));
                var monthCoupon = couponDateCal.getMonth();
                var yearCoupon = couponDateCal.getFullYear();
                
                var result = ((((yearMaturity-yearCoupon)*12+(monthMaturity-monthCoupon))/freq)+1);
                $("#totalCoupon").valueRnd(result, true, 0, "ROUNDUP");
                //$("#totalCoupon").val(result);
            }
            populate();
        });

        $("#couponDate").change(function() {    
            if ((($("#issueDateFi").val() != "") || ($("#issueDateFi").val() != null)) && (($("#maturityDate").val() != "") || ($("#maturityDate").val() != null)) && (($("#frequencyFi").val() != "") || ($("#frequencyFi").val() != null))) {
                var maturityDateCal = new Date($("#maturityDate").datepicker('getDate'));
                var monthMaturity = maturityDateCal.getMonth();
                var yearMaturity = maturityDateCal.getFullYear();
                
                var couponDateCal = new Date($("#couponDate").datepicker('getDate'));
                var monthCoupon = couponDateCal.getMonth();
                var yearCoupon = couponDateCal.getFullYear();
                
                var issueDateDateCal = new Date($("#issueDateFi").datepicker('getDate'));
                
                if(((maturityDateCal.getTime()) <  (couponDateCal.getTime())) || ((issueDateDateCal.getTime()) >  (couponDateCal.getTime()))) {
                    //alert("Coupon Date must be less than maturity date !");
                    $("#couponDate").addClass('fieldError');
                    $("#couponDateError").html('Invalid date Value').show();
                    //$("#couponDate").val("");
                    $("#totalCoupon").val("");
                //  $("#generateButton").css("display", "none");
                } else {
                    var result = ((((yearMaturity-yearCoupon)*12+(monthMaturity-monthCoupon))/freq)+1);
                    $("#totalCoupon").valueRnd(result, true, 0, "ROUNDUP");
                    //$("#totalCoupon").val(result);
                }
            }
            populate();
            
        });
        $("#interestRate").change(function() {
            populate();
        });
        $("#totalCoupon").change(function() {
            populate();
        });
        $("#fractionRatioSource").change(function() {
        	populate();
        });
        $("#fractionRatioTarget").change(function() {
            populate();
        });
        $("input[name='security.isFloatingRate']").change(function() {
            populate();
        });
        $("input[name='security.isFraction']").change(function() {
            populate();
        });
        $("#maturityDate").change(function(){
            if ((($("#couponDate").val() != "") || ($("#couponDate").val() != null)) && (($("#frequencyFi").val() != "") || ($("#frequencyFi").val() != null))) {
                var maturityDateCal = new Date($("#maturityDate").datepicker('getDate'));
                var monthMaturity = maturityDateCal.getMonth();
                var yearMaturity = maturityDateCal.getFullYear();
                
                var couponDateCal = new Date($("#couponDate").datepicker('getDate'));
                var monthCoupon = couponDateCal.getMonth();
                var yearCoupon = couponDateCal.getFullYear();
                
                
                if((maturityDateCal.getTime()) <  (couponDateCal.getTime())) {
                    //$("#couponDate").addClass('fieldError');
                    //$("#couponDateError").html('Invalid date Value').show();
                    //alert("Maturity Date must be greater than coupon date !")
                    //$("#maturityDate").val("");
                    $("#totalCoupon").val("");
                    //$("#generateButton").css("display", "none");
                } else {
                    
                    var result = ((((yearMaturity-yearCoupon)*12+(monthMaturity-monthCoupon))/freq)+1);
                    $("#totalCoupon").valueRnd(result, true, 0, "ROUNDUP");
                    //$("#totalCoupon").val(result);
                }
            }
            populate();
        })
        
        // ---------------------
        
        //--for fraction
        //$("#fractionRatioSource").attr('disabled', 'disabled');
        //$("#fractionRatioTarget").attr('disabled', 'disabled');
        $(".isFractionFi label span").html("");
        $("input[name='security.isFraction']").change(function() {          
            if ($("input[name='security.isFraction']:checked").val() == "true"){
                $("#fractionRatioSource").attr('disabled', false);
                $("#fractionRatioTarget").attr('disabled', false);
                $(".isFractionFi label span").html("*");
                //$("#fractionRatioSourceStripped").val($("#fractionRatioSource").val());
                //$("#fractionRatioTargetStripped").val($("#fractionRatioTarget").val());
            }else{
                $(".isFractionFi label span").html("");
                $("#fractionRatioSource").attr('disabled', 'disabled');
                $("#fractionRatioTarget").attr('disabled', 'disabled');
                $("#fractionRatioSource").val("");
                $("#fractionRatioTarget").val("");      
                $("#fractionRatioSourceStripped").val("");
                $("#fractionRatioTargetStripped").val("");  
                $("#fractionRatioSource").removeClass('fieldError');
                $("#fractionRatioSourceError").html('');
                $("#fractionRatioTarget").removeClass('fieldError');
                $("#fractionRatioTargetError").html('');
                
                $(".fraction").attr('disabled', 'disabled');
                $(".isFractionFi label span").html("");
                $(".fraction").val("");
            }   
            populate();
        });
        $("#fractionRatioSource").change(function() {
            $("#fractionRatioSource").removeClass('fieldError');
            $("#fractionRatioSourceError").html('');
            $("#fractionRatioSourceStripped").val($("#fractionRatioSource").val());
            if (parseFloat($("#fractionRatioSourceStripped").val()) <= 0) {
                $("#fractionRatioSource").addClass('fieldError');
                $("#fractionRatioSourceError").html('Invalid value').show();
            } 
            populate();
        });
        $("#fractionRatioTarget").change(function() {
            $("#fractionRatioTarget").removeClass('fieldError');
            $("#fractionRatioTargetError").html('');
            $("#fractionRatioTargetStripped").val($("#fractionRatioTarget").val());
            if (parseFloat($("#fractionRatioTargetStripped").val()) < 0) {
                $("#fractionRatioTarget").addClass('fieldError');
                $("#fractionRatioTargetError").html('Invalid value').show();
            } 
            populate();
        });
        

        $("#generateButton").click(function() {
            
            if ((($("#fractionRatioTarget").val()=="") || ($("#fractionRatioSource").val()=="")) && ($("input[name='security.isFraction']:checked").val() == "true")) {
                if ($("#fractionRatioTarget").val()=="") {
                    $("#fractionRatioTargetError").html('Required').show();
                } 
                if ($("#fractionRatioSource").val()=="") {
                    $("#fractionRatioSourceError").html('Required').show();
                }
            } else {
                $("#frequencyMm").attr('disabled', 'disabled');
                $("#frequencyMmHidden").attr('name', 'frequencyHidden');
                $('#coupon').hide();
                $('#result .loading').show();
                $('#result').show();
                $.post('@{populateCS()}', $('#form').serialize(), function(data) {
    	    		checkRedirect(data);
                    $('#coupon').html(data);
                    $('#result .loading').hide();
                    $('#coupon').show();
                    
                    if ($("input[name='security.isFloatingRate']:checked").val() == "false"){
                        $(".floating").attr('disabled', 'disabled');
                    } else {
                        $(".floating").attr('disabled',false);
                    }
                    
                    if ($("input[name='security.isFraction']:checked").val() == "false"){
                        $(".fraction").attr('disabled', 'disabled');
                        $(".isFractionFi label span").html("");
                    } else {
                        $(".fraction").attr('disabled',false);
                        $(".isFractionFi label span").html("*");
                        
                    }
                    
                    isCheckCoupon();
                    //var checkClass = $("#gridCoupon tbody tr").find("input[id*='nextPaymentDate']").hasClass('fieldError');
                    //if (checkClass){
                    //  alert(" Error Found ! some 'End Date' has passed 'Marurity Date' ");
                    //  return false;
                    //}
                });     
                //$('#tabs').tabs("select", "5");
                //changeTabs 21-03-2014
                $('#tabs').tabs("select", "4");
                
                $("#generateButton").removeClass("visible");
                $("#generateButton").button("disable");
            }
            $("#generateButtonError").html("");
        });
        
        
//      if ('${mode}'=='entry') {
//          $('input[name=isTrade]').attr('checked', true);
//          $('.afs').attr('disabled', 'disabled');
//          $('.htm').attr('disabled', 'disabled');
//      } 
        
        if ('${mode}' == 'view') {
            setChecked();
        }
        
        if ((('${mode}' == 'entry') || ('${mode}' == 'edit')) && ('${confirming}' != 'true')) {
            setChecked();
            doChecked();
            valuationMethod();
            //changeCombo(); -- tidak dipakai lagi diganti dengan function dibawahnya
            valueDropdownWhenEdit();
            changeParPrice();
        }
        valuationMethodNA();
        
        // EVENT RADIO BUTTON TRADE, AFS & HTM FEATURES #1375
        $("input[name=isTrade]").click(function(){
           
            //$('input[name=isTrade]').attr('checked', false);
            if ($(this).is(":checked")){
            	$('td[id=tdValuationMethod] label span').html(" *");
            	$('td[id=tdTrade] label span').html("");
            	$('.trade').attr("disabled", false);
            	changeValueAmortizationTradeIfHasInterest();
            } else {
            	
            	if ($('#valuationMethodAFS').val()=='${valuationMethodFairValue}' || $('#valuationMethodAFS').val()=='${valuationMethodFairValue}'){
            		$('td[id=tdMarketPrice] label span').html(" *");
            	}
            	else if ($('#valuationMethodAFS').val()=='${valuationMethodAmortized}' || $('#valuationMethodAFS').val()=='${valuationMethodAmortized}'){
            		$('td[id=tdMarketPrice] label span').html(" *");
            		$('td[id=tdAmortization] label span').html(" *");
            	} else {
            		$('td[id=tdMarketPrice] label span').html(" ");
            		$('td[id=tdAmortization] label span').html(" ");
            	}
            	
            	
            	if ($('#isAfs').is(":checked") || $('#isHtm').is(":checked")){
            		$('td[id=tdTrade] label span').html("")
            		$('td[id=tdValuationMethod] label span').html(" *");;
            	} else {
            		$('td[id=tdTrade] label span').html(" *");
            		$('td[id=tdValuationMethod] label span').html("");
            	}
            	$('.trade').val(""); 
            	$('.trade').attr("disabled", "disabled");
            	defaultValueDropdownOnValuationTrade();
            	 
            }
            
            /*doChecked();
            valuationMethod();
            valuationMethodNA();*/
            
        });
        $("input[name=isAfs]").click(function(){
           
            if ($(this).is(":checked")){
            	$('.afs').attr("disabled", false);
            	if (!$('#isTrade').is(":checked") && !$('#isHtm').is(":checked")){
             		$('td[id=tdValuationMethod] label span').html(" *");
             		$('td[id=tdTrade] label span').html(" ");
             	}
            	changeValueAmortizationAfsIfHasInterest();
            } else {
            	if ($('#valuationMethodTrade').val()=='${valuationMethodFairValue}' || $('#valuationMethodHTM').val()=='${valuationMethodFairValue}'){
            		$('td[id=tdMarketPrice] label span').html(" *");
            	}
            	else if ($('#valuationMethodTrade').val()=='${valuationMethodAmortized}' || $('#valuationMethodHTM').val()=='${valuationMethodAmortized}'){
            		$('td[id=tdMarketPrice] label span').html(" *");
            		$('td[id=tdAmortization] label span').html(" *");
            	} else {
            		$('td[id=tdMarketPrice] label span').html(" ");
            		$('td[id=tdAmortization] label span').html(" ");
            	}
            	 $('.afs').val("");
            	 $('.afs').attr("disabled", "disabled");
            	 if (!$('#isTrade').is(":checked") && !$('#isHtm').is(":checked")){
             		$('td[id=tdTrade] label span').html(" *");
             		$('td[id=tdValuationMethod] label span').html(" ");
             	} else {
             		$('td[id=tdTrade] label span').html(" ");
             	}
            	 defaultValueDropdownOnValuationAfs();
            }
            //$('input[name=isAfs]').attr('checked', false);
           /* doChecked();
            valuationMethod();
            valuationMethodNA();*/
        });
        $("input[name=isHtm]").click(function(){
            
            if ($(this).is(":checked")){
            	$('.htm').attr("disabled", false);
            	if (!$('#isTrade').is(":checked") && !$('#isAfs').is(":checked")){
             		$('td[id=tdValuationMethod] label span').html(" *");
             		$('td[id=tdTrade] label span').html(" ");
             	}
            	changeValueAmortizationHtmIfHasInterest();
            } else {
            	if ($('#valuationMethodAFS').val()=='${valuationMethodFairValue}' || $('#valuationMethodHTM').val()=='${valuationMethodFairValue}'){
            		$('td[id=tdMarketPrice] label span').html(" *");
            	}
            	else if ($('#valuationMethodAFS').val()=='${valuationMethodAmortized}' || $('#valuationMethodHTM').val()=='${valuationMethodAmortized}'){
            		$('td[id=tdMarketPrice] label span').html(" *");
            		$('td[id=tdAmortization] label span').html(" *");
            	} else {
            		$('td[id=tdMarketPrice] label span').html(" ");
            		$('td[id=tdAmortization] label span').html(" ");
            	}
            	 $('.htm').val("");
            	 $('.htm').attr("disabled", "disabled");
            	 if (!$('#isTrade').is(":checked") && !$('#isAfs').is(":checked")){
              		$('td[id=tdTrade] label span').html(" *");
              		$('td[id=tdValuationMethod] label span').html(" ");
              	} else {
              		$('td[id=tdTrade] label span').html(" ");
              	}
            	 defaultValueDropdownOnValuationHtm();
            }
            //$('input[name=isHtm]').attr('checked', false);
            /*doChecked();
            valuationMethod();
            valuationMethodNA();*/
        });
        // ======================================================================================================================
        if ($('#securityType').val() == "") {
    		$('input[name=isScript]').attr('disabled', 'disabled');
    		$('#isScriptHidden').val(false);
    		$("#isScript").val('false');
    		$('#isScript1').attr('checked',"");
    		$('#isScript2').attr('checked',"true");
    	}
        
    });
    //------END OF MAIN Function -------//

    function gridCouponDataTables() {
        var gridCoupon = $('#gridCoupon').dataTable({
            aoColumns: [
                        //null,
                        {bVisible:false},
                        {sType: "numeric" },
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null
                       ],
                       
            aaSorting:[[1,'asc']],
            //aoColumnDefs: [{asSorting:[""], aTargets:[8]}, {asSorting:[""], aTargets:[0,1,2,3,4,5,6,7]}],
            //sScrollY :"360px",
            //bScrollCollapse : true,
            bAutoWidth: false,  
            bJQueryUI:true,
            bLengthChange:false,
            bFilter:false,
            bPaginate:false,
            bRetrieve:true,
            //bSort:false,
            //iDisplayLength:10,
            bInfo:false
        });
    }

	//function for grid coupon schedule  
	function gridCoupons(){
	    $('.calendar').datepicker();

	    if ($("input[name='security.isFraction']:checked").val() == "false")
	    {
            $(".fraction").attr('disabled', 'disabled');
            $(".isFractionFi label span").html("");
            $(".fraction").val("");
        }
	    
	    gridCouponDataTables();
	    $('#gridCoupon').removeAttr('style');
	
	    onChangeNextCpnDate();
	    
	    $('input.numerics').live('blur', function() {
	    	var el = $(this);
	        var id = this.id;
	        var stripCoupon= $("input[id='" + id +"StrippedCoupon']");
	        stripCoupon.val($.fn.autoNumeric.Strip(id));
	    });
	    $('input.securityNumOnlyCS').live('blur', function() {
	    	var el = $(this);
	        var id = this.id;
	        var stripCoupon= $("input[id='" + id +"StrippedCoupon']");
	        stripCoupon.val($.fn.autoNumeric.Strip(id));
	    });
	    $('input.securityNumericCS').live('blur', function() {
	    	var el = $(this);
	        var id = this.id;
	        var stripCoupon= $("input[id='" + id +"StrippedCoupon']");
	        stripCoupon.val($.fn.autoNumeric.Strip(id));
	    });
	    //$('input.numerics').autoNumeric();
	}
	//--------------------------//
    
	//function when change Next Coupon Date  
	function onChangeNextCpnDate(){
	    $("input.coupon").mask("${appProp?.dateMask}", { placeholder:" " });
	    $('input.coupon').datepicker({dateFormat:'${appProp?.jqueryDateFormat}'});
	    $('input.coupon').change( function(){
	    	
	        var el = $(this);
	        var dateVal = el.val();
	        var id = this.id;
	        var errorId= "#couponError";
	        //el.removeClass("fieldError");
	        $(errorId).html("");
	        //$(el.next()).html("");

	        if(dateVal == "00/00/0000")
	        {
	        	el.val("");
	        }

	        var elementCouponDate = $(this);
	        var rowCouponDate = elementCouponDate.parents("tr");
	        var newRowCoupon = elementCouponDate.parents("tr").next();
	        
	        var indexRowTable = elementCouponDate.parents("tr").index();
	        var indexTable = id.split("[")[1].split("]")[0];
	        var idPaymentDate = "input[id*='paymentDate[" + indexTable + "]']";
	        
	        var dateFormatCompare = "dd/mm/yyyy";
	        var dateFormatCompareCoupon = "yyyy/mm/dd";
	        
	        var dtFrmSelectNextDate = new Date(el.datepicker('getDate')).format(dateFormatCompare);

	        /* sample cek date */
//	        var dtFrmSelectNextDateCompare = new Date(el.datepicker('getDate')).format(dateFormatCompareCoupon);
	        
//	        var childCouponRowNode = Number(1);

//	        var currentSelectedCouponDate = $("input[id*='lastPaymentDate']", rowCouponDate.children(childCouponRowNode));
//	        var nextSelectedCouponDate = $("input[id*='lastPaymentDate']", newRowCoupon.children(childCouponRowNode));

//	        var frmtCurrentSelectedCouponDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', currentSelectedCouponDate.val(), null).format(dateFormatCompareCoupon);
//	        var frmtNextSelectedCouponDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', nextSelectedCouponDate.val(), null).format(dateFormatCompareCoupon);

//	        if ((dtFrmSelectNextDateCompare < frmtCurrentSelectedCouponDate) || (dtFrmSelectNextDateCompare > frmtNextSelectedCouponDate)) {
//	            $("input[id*='nextPaymentDate']", rowCouponDate.children(childCouponRowNode)).addClass('fieldError');
//	        } else {
//	        	$("input[id*='nextPaymentDate']", rowCouponDate.children(childCouponRowNode)).removeClass('fieldError');
//	        }
	        /* end sample cek date */

	        $.post('@{onChangeNextPaymentDate()}?date='+dtFrmSelectNextDate, $('#form').serialize(), function(data) {
	    		checkRedirect(data);
	        	//$(".nextDate").parents("tr").find(idPaymentDate).val(data);
	        	
	        	var total = parseInt($("#totalCoupon").val());
	        	var idx = parseInt(indexRowTable) + 1;
	        	var d = data.split("/")[0];
	        	var m = data.split("/")[1];
	        	var y = data.split("/")[2];

	        	var d2 = Number(data.split("/")[0]) + 1;

	        	var lastDate = m + "/" + d + "/" + y;
	        	var dt = new Date(lastDate);

	        	var freq;

	            if($("#frequencyFi").val() == "FREQUENCY-M") {
	                freq = 1;
	            } else if($("#frequencyFi").val() == "FREQUENCY-Q") {
	                freq = 3;
	            } else if($("#frequencyFi").val() == "FREQUENCY-H") {
	                freq = 6;
	            } else if($("#frequencyFi").val() == "FREQUENCY-Y") {
	                freq = 12;
	            }

	            $("#gridCoupon tbody tr:nth-child(" + (idx) +")").find("input[id*='paymentDate']").val(dt.format(dateFormatCompare));
	            
	        	for (x = idx; x <= total; x++) {
	        		//dt.setMonth(dt.getMonth());
	        		dt.setDate(dt.getDate() + 1);
	        	    $("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='lastPaymentDate']").val(dt.format(dateFormatCompare));
	        	    dt.setDate(dt.getDate() - 1);
	        	    dt.setMonth(dt.getMonth() + freq);
	        	    $("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='nextPaymentDate']").val(dt.format(dateFormatCompare));
	        	    
	        	    var pyDate = $.datepicker.formatDate("yymmdd", dt);
	        	    var fixDate = getPaymentDate(pyDate, 0);
        	        var fixPaymentDate = new Date(fixDate);

        	    	$("#gridCoupon tbody tr:nth-child(" + (x+1) +")").find("input[id*='paymentDate']").val(fixPaymentDate.format(dateFormatCompare));
	        	    
	        	}
            });

	        try {
	            $.datepicker.parseDate('${appProp?.jqueryDateFormat}', dateVal, null);
	        } catch(error) {
	            //alert("error");
	            el.addClass("fieldError");
	            $(errorId).html("* Invalid date format").show();
	            //$(el.next()).html("Invalid date").show();
	        }
	});

    function processNextPaymentDate(nextPaymentRow, selectedPaymentRow) {
        var childPaymentRowNode = Number(1);
        var dateFormatCompare = "yyyy/mm/dd";
        
        var currentSelectedPaymentDate = $("input[id*='paymentDate']", selectedPaymentRow.children(childPaymentRowNode));
        var frmtCurrentSelectedPaymentDate = new Date(currentSelectedPaymentDate.datepicker('getDate')).format(dateFormatCompare);
        
        var advancePaymentDate = $("input[id*='paymentDate']", nextPaymentRow.children(childPaymentRowNode));
        var frmtAdvancePaymentDate = new Date(advancePaymentDate.datepicker('getDate')).format(dateFormatCompare);
        
        if (frmtCurrentSelectedPaymentDate > frmtAdvancePaymentDate) {
            $(advancePaymentDate).addClass('fieldError');
        } else {
            $(advancePaymentDate).removeClass('fieldError');
        }
    }

    $(".payDate").change(function(){
        var dateFormatCompare = "yyyy/mm/dd";
        var elementPaymentDate = $(this);
        var rowPaymentDate = elementPaymentDate.parents("tr");
        var newRowPayment = elementPaymentDate.parents("tr");
        var currentPayment = $(rowPaymentDate).children(0).html();
        var childNodePayment = Number(1);

        var selectPayment = $(rowPaymentDate).find("input[id*='paymentDate']");
        var dtFrmSelectPayment = new Date(selectPayment.datepicker('getDate')).format(dateFormatCompare);

        // compare current changed payment date with all next payment date
        while (true) {
            newRowPayment = newRowPayment.next();
            if (newRowPayment.children().length == 0) {
                break;
            } else {
                processNextPaymentDate(newRowPayment, rowPaymentDate);
            }
        }

        // compare changed payment date with same row end date
        var currentNextPaymentDate = $(rowPaymentDate).find("input[id*='nextPaymentDate']");
        var dtFrmNextPaymentDate = new Date(currentNextPaymentDate.datepicker('getDate')).format(dateFormatCompare);
        if (dtFrmSelectPayment < dtFrmNextPaymentDate) {
            $(selectPayment).addClass('fieldError');
        } else {
            $(selectPayment).removeClass('fieldError');
        }
    });

    function processRow(nextRow, selectRow) {
        var childProcessRowNode = Number(1);
        var dateFormatCompare = "yyyy/mm/dd";
        
        var maturityDateVal = new Date($("#maturityDate").datepicker('getDate'));
        var dtFrmtMaturityDateVal = maturityDateVal.format(dateFormatCompare);
        
        var currentNextPaymentDate = $("input[id*='nextPaymentDate']", selectRow.children(childProcessRowNode));
        var dtCurrentNextPayment = new Date(currentNextPaymentDate.datepicker('getDate'));
        var frmtCurrentNextPayment = dtCurrentNextPayment.format(dateFormatCompare);

        var advNextPaymentDate = $("input[id*='nextPaymentDate']", nextRow.children(childProcessRowNode));
        var dtAdvNextPayment = new Date(advNextPaymentDate.datepicker('getDate'));
        var frmtAdvNextPayment = dtAdvNextPayment.format(dateFormatCompare);

        if ((frmtCurrentNextPayment > frmtAdvNextPayment) || (frmtAdvNextPayment > dtFrmtMaturityDateVal)) {
        	$(advNextPaymentDate).addClass('fieldError');
        } else {
        	$(advNextPaymentDate).removeClass('fieldError');
        }
    }

    //$(".nextDate").change(function(){
    $(".nextDate").bind("keyup change", function(e){
        var dateFormatCompare = "yyyy/mm/dd";
        var element = $(this);
        var row = element.parents("tr");
        var newRow = element.parents("tr");
        var current = $(row).children(0).html();
        var childNode = Number(1);

        while(true) {
            newRow = newRow.next();
            if (newRow.children().length == 0) {
                break;
            }else{
                processRow(newRow, row);
            }
        }

        var selectDate = $(row).find("input[id*='nextPaymentDate']");
        var dtSelect = new Date(selectDate.datepicker('getDate'));
        var dtFrmtSelect = dtSelect.format(dateFormatCompare);
        
        var maturityDateVal = new Date($("#maturityDate").datepicker('getDate'));
        var dtFrmtMaturityDateVal = maturityDateVal.format(dateFormatCompare);

        // compare current end date with next start date
        var nextRow = row.next();
        var lastPaymentDate = undefined;
        var dtLastPayment = undefined;
        var dtFrmtLastPayment = undefined;

        if (nextRow != undefined && nextRow.length > 0) {
            lastPaymentDate = $("input", nextRow.children(childNode));
            //dtLastPayment = new Date(lastPaymentDate.datepicker('getDate'));
            dtLastPayment =  $.datepicker.parseDate('${appProp?.jqueryDateFormat}', lastPaymentDate.val(), null);
            dtFrmtLastPayment = dtLastPayment.format(dateFormatCompare);
        }

        // compare current end date with same row start date
        var currentLastPaymentDate = $(row).find("input[id*='lastPaymentDate']");
        //var dtFrmtCurrentLastPaymentDate = new Date(currentLastPaymentDate.datepicker('getDate')).format(dateFormatCompare);
        var dtFrmtCurrentLastPaymentDate = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', currentLastPaymentDate.val(), null).format(dateFormatCompare);

        if ((dtFrmtSelect < dtFrmtCurrentLastPaymentDate) || (dtFrmtSelect > dtFrmtLastPayment) || (dtFrmtSelect > dtFrmtMaturityDateVal)) {
            $(selectDate).addClass('fieldError');
        } else {
            $(selectDate).removeClass('fieldError');
        }
    });

         /* $(".nextDate").change(function(){

            //var appDateFormat = '${appProp.javascriptDateFormat}';
            var appDateFormat = 'mm/dd/yyyy';
            console.log('---------------------------------------------------------');
            //console.log('appDateFormat');
            //console.log(appDateFormat);
            var element = $(this);
            var row = element.parents("tr");
            var current = $(row).children(0).html();
            var length = $('#gridCoupon').dataTable().fnGetNodes().length;
            var selectDate = $(row).find("input[id*='nextPaymentDate']");
            
            if ($("#couponError").html()!= "* Invalid date format") {
                $.post('@{onChangeNextPaymentDate()}?date='+selectDate.val(), $('#form').serialize(), function(data) {
                    var payDate = $(row).find("input[id*='paymentDate']");  
                    payDate.val(data);
                }); 
                
                //var lastDt = new Date(selectDate.val());
                var lastDt = new Date(selectDate.datepicker('getDate'));
                //console.log('selectDate');
                //console.log(typeof selectDate);
                //console.log(selectDate.datepicker('getDate'));
                //console.log('lastDt');
                //console.log(typeof lastDt);
                //console.log(lastDt);
                var lastDate=dateAdd("d", lastDt, 1);
                //console.log('lastDate');
                //console.log(typeof lastDate);
                //console.log(lastDate);
                //console.log(lastDate.format(appDateFormat));
                var a=((current)*1)+1;
                var last = $("#gridCoupon tbody tr:nth-child(" +a+")").find("input[id*='lastPaymentDate']");
                //last.val(lastDate.format(appDateFormat));
                last.val(lastDate.format("dd/mm/yyyy"));
                //console.log('last');
                //console.log(typeof last);
                //console.log(last);
                
                errorCount = 0;
                errorMinCount = 0;
                
                var b= current*1;
                //var currentDate= new Date(selectDate.val());
                var currentDate= selectDate.datepicker('getDate');
                //var currentDateFormat = currentDate.format(appDateFormat);
                //var currentDateFormat = currentDate.getTime();
                var currentDateFormat = currentDate.format("yyyy/mm/dd"); 
                //console.log('currentDateFormat');
                //console.log(currentDateFormat);
                
                //var maturityDate = new Date($("#maturityDate").val());
                var maturityDate = new Date($("#maturityDate").datepicker('getDate'));
                //var maturityDateFormat = maturityDate.format(appDateFormat);
                //var maturityDateFormat = maturityDate.getTime();
                var maturityDateFormat = maturityDate.format("yyyy/mm/dd");

                if ( b == 1) {
                  nextA();
                } 
                else if ( b == length) {
                    prev();
                } else {
                    nextA();
                    prev();         
                }
                //isCheckCoupon();
                
                var start = $("#gridCoupon tbody tr:nth-child(" + (b) +")").find("input[id*='lastPaymentDate']");
                //var startFromCurrentDate = new Date(start.val());
                var startFromCurrentDate = start.datepicker('getDate');
                //var startFromCurrentDateFormat = startFromCurrentDate.format(appDateFormat);
                //var startFromCurrentDateFormat = startFromCurrentDate.getTime();
                var startFromCurrentDateFormat = startFromCurrentDate.format("yyyy/mm/dd");
                //console.log('startFromCurrentDateFormat');
                //console.log(startFromCurrentDateFormat);
                //console.log('maturityDateFormat');
                //console.log(maturityDateFormat);
                //console.log('currentDateFormat > maturityDateFormat');
                //console.log(currentDateFormat > maturityDateFormat);
                
                if ( currentDateFormat > maturityDateFormat ) {
                    //alert("error1");
                    console.log('-------------');
                    console.log('fieldError for currentDateFormat > maturityDateFormat');
                    console.log(currentDateFormat > maturityDateFormat);
                    console.log('-------------');
                    var errorCurrentDate= $(selectDate).addClass('fieldError');
                } else {
                    //console.log('startFromCurrentDateFormat > currentDateFormat');
                    //console.log(startFromCurrentDateFormat > currentDateFormat);
                    if(startFromCurrentDateFormat > currentDateFormat) {
                    //  alert("error2");
                        console.log('-------------');
                        console.log('fieldError for startFromCurrentDateFormat > currentDateFormat');
                        console.log(startFromCurrentDateFormat > currentDateFormat);
                        console.log('-------------');
                        var errorCurrentDate= $(selectDate).addClass('fieldError');
                    } else {
                        try {
                            //$.datepicker.parseDate('${appProp?.jqueryDateFormat}', selectDate.val(), null);
                            console.log('-------------');
                            console.log('remove fieldError for startFromCurrentDateFormat > currentDateFormat');
                            console.log(startFromCurrentDateFormat > currentDateFormat);
                            console.log('-------------');
                            //$.datepicker.parseDate('mm/dd/yy', selectDate.val(), null);
                            selectDate.val(selectDate.format(appDateFormat));
                            var errorCurrentDate= $(selectDate).removeClass('fieldError');
                        } catch(error) {
                            //alert("error");
                            console.log('catch exception for remove fieldError for startFromCurrentDateFormat > currentDateFormat');
                            console.log(error);
                            $(selectDate).addClass("fieldError");
                            $("#couponError").html("* Invalid date format").show();
                            //$(el.next()).html("Invalid date").show();
                        }
                    //  alert("error3");
                        
                    }
                        
                }
            }
   
            function prev() {
                var previous = $("#gridCoupon tbody tr:nth-child(" + (b-1) +")").find("input[id*='nextPaymentDate']");
                //var previousDate = new Date(previous.val());
                var previousDate = previous.datepicker('getDate');
                //var previousDateFormat = previousDate.format('${appProp.javascriptDateFormat}');
                //var previousDateFormat = previousDate.getTime();
                var previousDateFormat = previousDate.format("yyyy/mm/dd");
                //console.log('previousDateFormat');
                //console.log(previousDateFormat);
                //console.log('currentDateFormat <= previousDateFormat');
                //console.log(currentDateFormat <= previousDateFormat);
                if ( currentDateFormat <= previousDateFormat ) {
                    //console.log('-------------');
                    //console.log('fieldError for currentDateFormat <= previousDateFormat');
                    //console.log(currentDateFormat <= previousDateFormat);
                    //console.log('-------------');
                    var errorCurrentDate= $(selectDate).addClass('fieldError');
                } else {
                    alert('in');
                    //console.log('for else currentDateFormat <= previousDateFormat');
                    var errorCurrentDate= $(selectDate).removeClass('fieldError');
                }
            }
            function nextA() {
                
                var next = $("#gridCoupon tbody tr:nth-child(" + (b+1) +")").find("input[id*='nextPaymentDate']");
                //var nextFromCurrentDate = new Date(next.val());
                var nextFromCurrentDate = next.datepicker('getDate');
                
                //var nextFromCurrentDateFormat = nextFromCurrentDate.format('${appProp.javascriptDateFormat}');
                //var nextFromCurrentDateFormat = nextFromCurrentDate.getTime();
                var nextFromCurrentDateFormat = nextFromCurrentDate.format("yyyy/mm/dd");
                
                //console.log('nextFromCurrentDateFormat');
                //console.log(nextFromCurrentDateFormat);
                //console.log('(nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat)');
                //console.log((nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat));
                //alert((nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat));
                //alert(nextFromCurrentDateFormat + '<=' + currentDateFormat  + '-' +  nextFromCurrentDateFormat  + '>' +  maturityDateFormat);
                if ((nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat)) {
                    //console.log('-------------');
                    //console.log('fieldError for (nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat)');
                    //console.log((nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat));
                    //console.log('-------------');
                    var errorNextDate= $(next).addClass('fieldError');
                    //alert('next');
                } else {
                    //alert('next else');
                    //console.log('-------------');
                    //console.log('remove fieldError for (nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat)');
                    //console.log((nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat));
                    //console.log('-------------');
                    
                    var errorNextDate= $(next).removeClass('fieldError');
                    //if (nextFromCurrentDateFormat > maturityDateFormat) {
                    //   $(next).addClass('fieldError');
                    //}
                    //alert('try');
                    try {
                        //$.datepicker.parseDate('${appProp?.jqueryDateFormat}', next.val(), null);
                        $.datepicker.parseDate('mm/dd/yy', next.val(), null);
                        //$.datepicker.parseDate('dd/mm/yyyy', next.val(), null);
                        //next.val(next.format("dd/mm/yyyy"));
                       //next.val(next.format(appDateFormat));
                       
                    } catch(error) {
                        alert('next' + error);
                        //console.log('catch exception for remove fieldError for (nextFromCurrentDateFormat <= currentDateFormat) || (nextFromCurrentDateFormat > maturityDateFormat)');
                        //console.log(error);
                        $(next).addClass("fieldError");
                        $("#couponError").html("* Invalid date format").show();
                        //$(el.next()).html("Invalid date").show();
                    }
                }
            }
        }) */
}
// End Of function when change Next Coupon Date -----//
    
//--function for show populate generate button 
function populate() {
	/*console.debug("frequencyFi = " +$("#frequencyFi").val());
	console.debug("issueDateFi = " +$("#issueDateFi").val());
	console.debug("couponDate = " +$("#couponDate").val());
	console.debug("maturityDate = " +$("#maturityDate").val());
	console.debug("interestRate = " +$("#interestRate").val());
	console.debug("totalCoupon = " +$("#totalCoupon").val());*/

    if (!($(".setCoupon").hasClass("fieldError"))) {
    	if (($("#frequencyFi").val()!='')==true && ($("#issueDateFi").val()!='')==true && ($("#couponDate").val()!='')==true && ($("#maturityDate").val()!='')==true && ($("#interestRate").val()!='')==true && ($("#totalCoupon").val()!='')==true && ($("input[name='security.isFloatingRate']").is(":checked"))) {
    		$("#generateButton").button("enable");
            $("#generateButton").addClass("visible");
        }else{
            $("#generateButton").removeClass("visible");
            $("#generateButton").button("disable");
        }
    } else {
        $("#generateButton").removeClass("visible");
        $("#generateButton").button("disable");
    }
    $("#generateButtonError").html("");
}
//--------------------------//

//--function for add date
function dateAdd(option, source, diff) {
    var days = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];
    var result = new Date();
    var d = source.getDate();
    var m = source.getMonth();
    var y = source.getFullYear();
    switch (option) {
    case "d":
        d += diff;
        
        if ((new Date(y,1,29).getDate() == 29)) {
            days[1] = 29;
        }
        while ((d <= 0) || (d > days[m])) {
            if (d > days[m]) {
                d -= days[m];
                m += 1;
                if (m > 11) {
                    m -= 12;
                    y += 1;
                }
            }
            if (d <= 0) {
                m -= 1;
                if (m < 0) {
                    y -= 1;
                    m += 12;
                }
                d += days[m];
            }
        }
        break;
    case "m":
        m += diff;
        while ((m < 0) || (m >= 12)) {
            if (m >= 12) {
                m -= 12;
                y += 1;
            }
            if (m < 0) {
                m += 12;
                y -= 1;
            }
        }
        if ((new Date(y,1,29).getDate() == 29)) {
            days[1] = 29;
        }
        if (d > days[m]) {
            d = days[m];
        }
        break;
    case "y":
        y += diff;
        if ((new Date(y,1,29).getDate() == 29)) {
            days[1] = 29;
        }
        if (d > days[m]) {
            d = days[m];
        }
        break;
    }
    result.setYear(y);
    result.setMonth(m);
    result.setDate(d);
    return result;
}

function valuationMethod() {
   //START For Valuation Method Trade
    
    if ($("#fairValueTrade").val()== 'true') {
        $("#marketPriceTrade").attr('disabled', 'disabled');
    }
    if ($("#amortizedTrade").val()== 'true') {
        $("#amortizationMethodTrade").attr('disabled', 'disabled');
    }

    console.log("[TRADE] Valuation method = " +$("#valuationMethodTrade").val());
    if($("#valuationMethodTrade").val() == '${valuationMethodFairValue}') {
    	$("#amortizationMethodTrade").val('');
        $("#amortizationMethodTrade").attr('disabled', 'disabled');
        
        if ($('#isTradeHidden').val()=='true'){
        	$("td[id=tdMarketPrice] label span").html(" *");
        	if ($('#valuationMethodeAFS').val()!='${valuationMethodAmortized}' || $('#valuationMethodeHTM').val()!='${valuationMethodAmortized}'){
        		$("td[id=tdAmortization] label span").html("");
        	}
        }
        
    } else if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}') {
        if ($('#isTradeHidden').val()=='true'){
    		$("td[id=tdMarketPrice] label span").html(" *");
    		$("td[id=tdAmortization] label span").html(" *");
    	}
    	
    } else {
        $("#amortizationMethodTrade").val('');
        if ($('#isTradeHidden').val()=='true'){
        	if ($('#valuationMethodAFS').val()=='${valuationMethodFairValue}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} 
        	else if ($('#valuationMethodAFS').val()=='${valuationMethodAmortized}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
		        $("td[id=tdAmortization] label span").html(" *");
        	}
        	else if ($('#valuationMethodHTM').val()=='${valuationMethodFairValue}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} 
        	else if ($('#valuationMethodHTM').val()=='${valuationMethodAmortized}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
		        $("td[id=tdAmortization] label span").html(" *");
        	}
        	else {
        		$("td[id=tdMarketPrice] label span").html(" ");
		        $("td[id=tdAmortization] label span").html(" ");
        	}
        }
        //$("#marketPriceTrade").attr('disabled', 'disabled');
        //$("#amortizationMethodTrade").attr('disabled', 'disabled');
    } 

    $("#marketPriceTrade").change(function(){
        $("#marketPriceTradeHidden").val($("#marketPriceTrade").val());
    });
    
    $("#amortizationMethodTrade").change(function(){
        $("#amortizationMethodTradeHidden").val($("#amortizationMethodTrade").val());
    });

    $("#valuationMethodTrade").change(function(){
    	var price = $('#isPrice').val();
    	var discounted = $('#isDiscounted').val();
    	var hasInterest = $('#hasInterest').val();
    	
        if($("#valuationMethodTrade").val() == '${valuationMethodFairValue}') {
        	
        	$('#valuationMethodTrade').removeClass('fieldError');
            $("#valuationMethodTradeErrorMessage").html("");
        	$("#marketPriceTrade").attr('disabled', false);
        	$("#amortizationMethodTrade").val('');
            $("#amortizationMethodTrade").attr('disabled', 'disabled');

            $("td[id=tdMarketPrice] label span").html(" *");
            if (($('#valuationMethodAFS').val() == '${valuationMethodAmortized}') || ($('#valuationMethodHTM').val() == '${valuationMethodAmortized}')){
            	$("td[id=tdAmortization] label span").html(" *");	
            } else {
            	$("td[id=tdAmortization] label span").html("");
            }
            
            if (price == 'true') {
            	$('#marketPriceTrade option[value="${marketPriceNA}"]').hide();
            }
            
            if (price == 'false') {
            	$('#marketPriceTrade option[value="${marketPriceClose}"]').hide();
            	$('#marketPriceTrade option[value="${marketPriceLow}"]').hide();
            	$('#marketPriceTrade option[value="${marketPriceHigh}"]').hide();
            }
            
            console.log("Price = " +price);
            if($.browser.msie){
                $('#marketPriceTrade option').remove();
                var optionsAmt = $('#marketPriceTrade').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                if (price == ''){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                if (price == 'true'){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                }
                
                if (price == 'false'){
                	if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                    
            }
            
        } else if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
        	
            $("#marketPriceTrade").attr('disabled', false);
            $("#marketPriceTrade").val('');
            $("#amortizationMethodTrade").attr('disabled', false);
            $('#valuationMethodTrade').removeClass('fieldError');
            $("#valuationMethodTradeErrorMessage").html("");
            $('#marketPriceTrade option[value="${marketPriceNA}"]').show();
            $("td[id=tdMarketPrice] label span").html(" *");
	        $("td[id=tdAmortization] label span").html(" *");
            
	        if (discounted == 'false'){
	        	$('#amortizationMethodTrade option[value="${amortizationSL}"]').hide();
	        	$('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
	        	$('#amortizationMethodTrade option[value="${amortizationNPV}"]').hide();
	        }
	        
	        if (hasInterest == 'false'){
	        	$('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
	        }
	        
            if($.browser.msie){
                $('#marketPriceTrade option').remove();
                var optionsAmt = $('#marketPriceTrade').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                
                if (price == '' || price == 'true'){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                if (price == 'false'){
                	 if ('${valueForMarketPriceNA}'!=''){
                     	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                     }
                }
                    
                console.log("discounted = " +discounted);
                console.log("hasInterest = " +hasInterest);
                var optionsAmtMd = $('#amortizationMethodTrade').attr('options');
                 
                if ((discounted == '') || (discounted == 'true')){
                	$('#amortizationMethodTrade option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                	if ('${valueForAmortizationSL}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
                	}
                	if ('${valueForAmortizationEIR}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
                	}
                	if ('${valueForAmortizationNPV}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
                	}
                }
                
                if (hasInterest == 'false'){
	            	$('#amortizationMethodTrade option').remove();
	            	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
	            	if ('${valueForAmortizationSL}'!=''){
	            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
	            	}
	            	if ('${valueForAmortizationNPV}'!=''){
	            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
	            	}
                 }
                
                if (discounted == 'false'){
                	$('#amortizationMethodTrade option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                }
                
               
            }
            
            
        } else if ($("#valuationMethodTrade").val() == '${valuationMethodNA}'){
            
            //$("#marketPriceTrade").val('');
        	if($.browser.msie){
                $('#marketPriceTrade option').remove();
                var optionsAmt = $('#marketPriceTrade').attr('options');
                  	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            }
        	
            $("#marketPriceTrade").val('${marketPriceNA}');
            $("#marketPriceTradeHidden").val($("#marketPriceTrade").val());
            $("#marketPriceTrade").attr('disabled', 'disabled');
            $("#amortizationMethodTrade").val('');
            $("#amortizationMethodTrade").attr('disabled', 'disabled');
            $('#valuationMethodTrade').removeClass('fieldError');
            $("#valuationMethodTradeErrorMessage").html("");
            if (($('#valuationMethodAFS').val()=='${valuationMethodFairValue}') || ($('#valuationMethodHTM').val()=='${valuationMethodFairValue}')) {
            	$("td[id=tdMarketPrice] label span").html(" *");
            	
            } else if (($('#valuationMethodAFS').val()=='${valuationMethodAmortized}') || ($('#valuationMethodHTM').val()=='${valuationMethodAmortized}')) {
            	
            	$("td[id=tdMarketPrice] label span").html(" *");
	        	$("td[id=tdAmortization] label span").html(" *");
            }
            else {
            	$("td[id=tdMarketPrice] label span").html("");
	        	$("td[id=tdAmortization] label span").html(" ");
            } 
            
        } else {
        	if (($('#valuationMethodAFS').val()=='${valuationMethodFairValue}') || ($('#valuationMethodHTM').val()=='${valuationMethodFairValue}')){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} else if (($('#valuationMethodAFS').val()=='${valuationMethodAmortized}') || ($('#valuationMethodHTM').val()=='${valuationMethodAmortized}')) {
        		
        		$("td[id=tdMarketPrice] label span").html(" *");
            	$("td[id=tdAmortization] label span").html(" *");	
        	} else {
        		$("td[id=tdMarketPrice] label span").html("");
            	$("td[id=tdAmortization] label span").html("");
        	}
        	
        	$("#marketPriceTrade").val('');
            $("#amortizationMethodTrade").val('');
            $("#marketPriceTrade").attr('disabled', false);
            $("#amortizationMethodTrade").attr('disabled', false);
            
            $('#amortizationMethodTrade option[value="${amortizationSL}"]').show();
            if (hasInterest == 'true'){
            	$('#amortizationMethodTrade option[value="${amortizationEIR}"]').show();
            } else {
            	$('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
            }
        	$('#amortizationMethodTrade option[value="${amortizationNPV}"]').show();
            
        	if($.browser.msie){
        		//  Market Price
                $('#marketPriceTrade option').remove();
                var optionsAmt = $('#marketPriceTrade').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                if ('${valueForMarketPriceClose}'!=''){
            		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
            	}
                if ('${valueForMarketPriceLow}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                }
                if ('${valueForMarketPriceHigh}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                }
                if ('${valueForMarketPriceNA}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                }
                
                // Amortization
                $('#amortizationMethodTrade option').remove();
                var optionsAmtMd = $('#amortizationMethodTrade').attr('options');
            	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
            	if ('${valueForAmortizationSL}'!=''){
            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
            	}
            	if (hasInterest=='true'){
	            	if ('${valueForAmortizationEIR}'!=''){
	            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
	            	}
            	}
            	if ('${valueForAmortizationNPV}'!=''){
            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            	}
            }
        }
    });

    // END For Valuation Method Trade

    //START For Valuation Method AFS
    if ($("#fairValueAFS").val()== 'true') {
        $("#marketPriceAFS").attr('disabled', 'disabled');
    }
    if ($("#amortizedAFS").val()== 'true') {
        $("#amortizationMethodAFS").attr('disabled', 'disabled');
    }
    console.log("[AFS] Valuation method = " +$("#valuationMethodAFS").val());
    if($("#valuationMethodAFS").val() == '${valuationMethodFairValue}') {
    	
        $("#amortizationMethodAFS").val('');
        $("#amortizationMethodAFS").attr('disabled', 'disabled');
        if ($('#isAfsHidden').val()=='true'){
        	$("td[id=tdMarketPrice] label span").html(" *");
        	if ($('#valuationMethodeTrade').val()!='${valuationMethodAmortized}' || $('#valuationMethodeHTM').val()!='${valuationMethodAmortized}'){
        		$("td[id=tdAmortization] label span").html("");
        	}
        }
       
    } else if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
        //$("#marketPriceAFS").val('');
        //$("#marketPriceAFS").attr('disabled', 'disabled');
        //$("#marketPriceAFSHidden").val($("#marketPriceAFS").val());
    	
    	if ($('#isAfsHidden').val()=='true'){
    		
    		$("td[id=tdMarketPrice] label span").html(" *");
        	$("td[id=tdAmortization] label span").html(" *");
        }
    } else {
        //$("#marketPriceAFS").val('');
        $("#amortizationMethodAFS").val('');
        if ($('#isAfsHidden').val()=='true'){
        	if ($('#valuationMethodTrade').val()=='${valuationMethodFairValue}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} 
        	else if ($('#valuationMethodTrade').val()=='${valuationMethodAmortized}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
		        $("td[id=tdAmortization] label span").html(" *");
        	}
        	else if ($('#valuationMethodHTM').val()=='${valuationMethodFairValue}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} 
        	else if ($('#valuationMethodHTM').val()=='${valuationMethodAmortized}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
		        $("td[id=tdAmortization] label span").html(" *");
        	}
        	else {
        		$("td[id=tdMarketPrice] label span").html(" ");
		        $("td[id=tdAmortization] label span").html(" ");
        	}
        }
        //$("#marketPriceAFS").attr('disabled', 'disabled');
        //$("#amortizationMethodAFS").attr('disabled', 'disabled');
    } 

    $("#marketPriceAFS").change(function(){
        $("#marketPriceAFSHidden").val($("#marketPriceAFS").val());
    });
    $("#amortizationMethodAFS").change(function(){
        $("#amortizationMethodAFSHidden").val($("#amortizationMethodAFS").val());
    });
    
    $("#valuationMethodAFS").change(function(){
    	var price = $('#isPrice').val();
    	var discounted = $('#isDiscounted').val();
    	var hasInterest = $('#hasInterest').val();
    	
        if($("#valuationMethodAFS").val() == '${valuationMethodFairValue}') {
            
        	$('#valuationMethodAFS').removeClass('fieldError');
            $("#valuationMethodAFSErrorMessage").html("");
            $("#marketPriceAFS").attr('disabled', false);
            $("#amortizationMethodAFS").val('');
            $("#amortizationMethodAFS").attr('disabled', 'disabled');
            
            $("td[id=tdMarketPrice] label span").html(" *");
            if (($('#valuationMethodTrade').val() == '${valuationMethodAmortized}') || ($('#valuationMethodHTM').val() == '${valuationMethodAmortized}')){
            	
            	$("td[id=tdAmortization] label span").html(" *");	
            } else {
            	$("td[id=tdAmortization] label span").html("");
            }
            
           if (price == 'true') {
	            $('#marketPriceAFS option[value="${marketPriceNA}"]').hide();
            }
            
            if (price == 'false') {
	            $('#marketPriceAFS option[value="${marketPriceClose}"]').hide();
	            $('#marketPriceAFS option[value="${marketPriceLow}"]').hide();
	            $('#marketPriceAFS option[value="${marketPriceHigh}"]').hide();
            }
            
            if($.browser.msie){
                $('#marketPriceAFS option').remove();
                var optionsAmt = $('#marketPriceAFS').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                if (price == ''){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                if (price == 'true'){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                }
                
                if (price == 'false'){
                	if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
            }
        } else if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
            $("#marketPriceAFS").attr('disabled', false);
            $("#marketPriceAFS").val('');
//            $("#amortizationMethodAFS").val('${amortizationSL}');
//            $("#amortizationMethodAFSHidden").val($("#amortizationMethodAFS").val());
            $("#amortizationMethodAFS").attr('disabled', false);
            $('#valuationMethodAFS').removeClass('fieldError');
            $("#valuationMethodAFSErrorMessage").html("");
            $('#marketPriceAFS option[value="${marketPriceNA}"]').show();
            
            $("td[id=tdMarketPrice] label span").html(" *");
        	$("td[id=tdAmortization] label span").html(" *");
        	
        	if (discounted == 'false'){
	        	$('#amortizationMethodAFS option[value="${amortizationSL}"]').hide();
	        	$('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
	        	$('#amortizationMethodAFS option[value="${amortizationNPV}"]').hide();
	        }
        	
        	if (hasInterest == 'false'){
        		$('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
        	}
            
            if($.browser.msie){
                $('#marketPriceAFS option').remove();
                var optionsAmt = $('#marketPriceAFS').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                
                if (price == '' || price == 'true'){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                if (price == 'false'){
                	if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
	                
	            var optionsAmtMd = $('#amortizationMethodAFS').attr('options');  
                if ((discounted == '') || (discounted == 'true')){
                	$('#amortizationMethodAFS option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                	if ('${valueForAmortizationSL}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
                	}
                	if ('${valueForAmortizationEIR}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
                	}
                	if ('${valueForAmortizationNPV}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
                	}
                }
                
                if (hasInterest == 'false') {
                	 $('#amortizationMethodAFS option').remove();
                	 optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                 	if ('${valueForAmortizationSL}'!=''){
                 		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
                 	}
                 	if ('${valueForAmortizationNPV}'!=''){
                 		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
                 	}
                 	
                }
                
                if (discounted == 'false'){
                	$('#amortizationMethodAFS option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                } 
                
                
            }
        } else if ($("#valuationMethodAFS").val() == '${valuationMethodNA}'){
            
            //$("#marketPriceAFS").val('');
        	if($.browser.msie){
                $('#marketPriceAFS option').remove();
                var optionsAmt = $('#marketPriceAFS').attr('options');
                  	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            }
        	
            $("#marketPriceAFS").val('${marketPriceNA}');
            $("#marketPriceAFSHidden").val($("#marketPriceAFS").val());
            $("#marketPriceAFS").attr('disabled', 'disabled');
            $("#amortizationMethodAFS").val('');
            $("#amortizationMethodAFS").attr('disabled', 'disabled');
            $('#valuationMethodAFS').removeClass('fieldError');
            $("#valuationMethodAFSErrorMessage").html("");
            
            if (($('#valuationMethodTrade').val()=='${valuationMethodFairValue}') || ($('#valuationMethodHTM').val()=='${valuationMethodFairValue}')) {
            	$("td[id=tdMarketPrice] label span").html(" *");
            	
            } else if (($('#valuationMethodTrade').val()=='${valuationMethodAmortized}') || ($('#valuationMethodHTM').val()=='${valuationMethodAmortized}')) {
            	
            	$("td[id=tdMarketPrice] label span").html(" *");
	        	$("td[id=tdAmortization] label span").html(" *");
            }
            else {
            	$("td[id=tdMarketPrice] label span").html("");
	        	$("td[id=tdAmortization] label span").html(" ");
            } 
        
        } else {
        	if (($('#valuationMethodTrade').val()=='${valuationMethodFairValue}') || ($('#valuationMethodHTM').val()=='${valuationMethodFairValue}')){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} else if (($('#valuationMethodTrade').val()=='${valuationMethodAmortized}') || ($('#valuationMethodHTM').val()=='${valuationMethodAmortized}')) {
        		
        		$("td[id=tdMarketPrice] label span").html(" *");
            	$("td[id=tdAmortization] label span").html(" *");	
        	} else {
        		
        		$("td[id=tdMarketPrice] label span").html(" ");
            	$("td[id=tdAmortization] label span").html(" ");
        	}
        	
            $("#marketPriceAFS").val('');
            $("#amortizationMethodAFS").val('');
            $("#marketPriceAFS").attr('disabled', false);
            $("#amortizationMethodAFS").attr('disabled', false);
            
            $('#amortizationMethodAFS option[value="${amortizationSL}"]').show();
            if (hasInterest == 'true'){
            	$('#amortizationMethodAFS option[value="${amortizationEIR}"]').show();
            } else {
            	$('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
            }
        	$('#amortizationMethodAFS option[value="${amortizationNPV}"]').show();
        	
            if($.browser.msie){
            	// Market Price
                $('#marketPriceAFS option').remove();
                var optionsAmt = $('#marketPriceAFS').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                if ('${valueForMarketPriceClose}'!=''){
            		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
            	}
                if ('${valueForMarketPriceLow}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                }
                if ('${valueForMarketPriceHigh}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                }
                if ('${valueForMarketPriceNA}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                }
                
                // Amortization
                $('#amortizationMethodAFS option').remove();
                var optionsAmtMd = $('#amortizationMethodAFS').attr('options');
            	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
            	if ('${valueForAmortizationSL}'!=''){
            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
            	}
            	if (hasInterest=='true'){
	            	if ('${valueForAmortizationEIR}'!=''){
	            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
	            	}
            	}
            	if ('${valueForAmortizationNPV}'!=''){
            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            	}
            }
        }
    });

    // END For Valuation Method AFS
    
    //START For Valuation Method HTM
    if ($("#fairValueHTM").val()== 'true') {
        $("#marketPriceHTM").attr('disabled', 'disabled');
    }
    if ($("#amortizedHTM").val()== 'true') {
        $("#amortizationHTM").attr('disabled', 'disabled');
    }
    console.log("[HTM] Valuation method = " +$("#valuationMethodHTM").val());
    if($("#valuationMethodHTM").val() == '${valuationMethodFairValue}') {
    	
        $("#amortizationMethodHTM").val('');
        $("#amortizationMethodHTM").attr('disabled', 'disabled');
        if ($('#isHtmHidden').val()=='true'){
        	$("td[id=tdMarketPrice] label span").html(" *");
        	if ($('#valuationMethodeAFS').val()!='${valuationMethodAmortized}' || $('#valuationMethodeTrade').val()!='${valuationMethodAmortized}'){
        		$("td[id=tdAmortization] label span").html("");
        	}
        }
    } else if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
    	
        //$("#marketPriceHTM").val('');
        //$("#marketPriceHTM").attr('disabled', 'disabled');
        //$("#marketPriceHTMHidden").val($("#marketPriceHTM").val());
    	if ($('#isHtmHidden').val()=='true'){
    		
    		$("td[id=tdMarketPrice] label span").html(" *");
        	$("td[id=tdAmortization] label span").html(" *");
        }
    } else {
        //$("#marketPriceHTM").val('');
        $("#amortizationMethodHTM").val('');
        if ($('#isHtmHidden').val()=='true'){
        	if ($('#valuationMethodAFS').val()=='${valuationMethodFairValue}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} 
        	else if ($('#valuationMethodAFS').val()=='${valuationMethodAmortized}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
		        $("td[id=tdAmortization] label span").html(" *");
        	}
        	else if ($('#valuationMethodTrade').val()=='${valuationMethodFairValue}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
        	} 
        	else if ($('#valuationMethodTrade').val()=='${valuationMethodAmortized}'){
        		$("td[id=tdMarketPrice] label span").html(" *");
		        $("td[id=tdAmortization] label span").html(" *");
        	}
        	else {
        		$("td[id=tdMarketPrice] label span").html(" ");
		        $("td[id=tdAmortization] label span").html(" ");
        	}
        }
        //$("#marketPriceHTM").attr('disabled', 'disabled');
        //$("#amortizationMethodHTM").attr('disabled', 'disabled');
    } 

    $("#marketPriceHTM").change(function(){
        $("#marketPriceHTMHidden").val($("#marketPriceHTM").val());
    });
    
    $("#amortizationMethodHTM").change(function(){
        $("#amortizationMethodHTMHidden").val($("#amortizationMethodHTM").val());
    });

    $("#valuationMethodHTM").change(function(){
    	var price = $('#isPrice').val();
    	var discounted = $('#isDiscounted').val();
    	var hasInterest = $('#hasInterest').val();
    	if($("#valuationMethodHTM").val() == '${valuationMethodFairValue}') {
        	
        	$('#valuationMethodHTM').removeClass('fieldError');
            $("#valuationMethodHTMErrorMessage").html("");
        	$("#marketPriceHTM").attr('disabled', false);
            $("#amortizationMethodHTM").val('');
            $("#amortizationMethodHTM").attr('disabled', 'disabled');
            
            $("td[id=tdMarketPrice] label span").html(" *");
            if (($('#valuationMethodAFS').val() == '${valuationMethodAmortized}') || ($('#valuationMethodTrade').val() == '${valuationMethodAmortized}')){
            	
            	$("td[id=tdAmortization] label span").html(" *");	
            } else {
            	$("td[id=tdAmortization] label span").html("");
            }
          	 
            
    		if (price == 'true') {
            	$('#marketPriceHTM option[value="${marketPriceNA}"]').hide();
            }
            
            if (price == 'false') {
	            $('#marketPriceHTM option[value="${marketPriceClose}"]').hide();
	            $('#marketPriceHTM option[value="${marketPriceLow}"]').hide();
	            $('#marketPriceHTM option[value="${marketPriceHigh}"]').hide();
            }
            
            if($.browser.msie){
                $('#marketPriceHTM option').remove();
                var optionsAmt = $('#marketPriceHTM').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                if (price == ''){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                if (price == 'true'){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                }
                
                if (price == 'false'){
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
            }
        } else if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
            $("#marketPriceHTM").attr('disabled', false);
            $("#marketPriceHTM").val('');
//            $("#amortizationMethodHTM").val('${amortizationSL}');
//            $("#amortizationMethodHTMHidden").val($("#amortizationMethodHTM").val());
            $("#amortizationMethodHTM").attr('disabled', false);
            $('#valuationMethodHTM').removeClass('fieldError');
            $("#valuationMethodHTMErrorMessage").html("");
            $('#marketPriceHTM option[value="${marketPriceNA}"]').show();
            
            $("td[id=tdMarketPrice] label span").html(" *");
        	$("td[id=tdAmortization] label span").html(" *");
        	
        	if (discounted == 'false'){
	        	$('#amortizationMethodHTM option[value="${amortizationSL}"]').hide();
	        	$('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
	        	$('#amortizationMethodHTM option[value="${amortizationNPV}"]').hide();
	        }
        	
        	if (hasInterest == 'false'){
        		$('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
        	}
        	
            if($.browser.msie){
                $('#marketPriceHTM option').remove();
                var optionsAmt = $('#marketPriceHTM').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                
                if (price == '' || price == 'true'){
                	if ('${valueForMarketPriceClose}'!=''){
                		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
                	}
                    if ('${valueForMarketPriceLow}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                    }
                    if ('${valueForMarketPriceHigh}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                    }
                    if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                if (price == 'false'){
                	if ('${valueForMarketPriceNA}'!=''){
                    	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                    }
                }
                
                
                var optionsAmtMd = $('#amortizationMethodHTM').attr('options');  
                if ((discounted == '') || (discounted == 'true')){
                	$('#amortizationMethodHTM option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                	if ('${valueForAmortizationSL}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
                	}
                	if ('${valueForAmortizationEIR}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
                	}
                	if ('${valueForAmortizationNPV}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
                	}
                }
                
                if (hasInterest == 'false'){
                	$('#amortizationMethodHTM option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                	if ('${valueForAmortizationSL}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
                	}
                	if ('${valueForAmortizationNPV}'!=''){
                		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
                	}
                }
                
                if (discounted == 'false'){
                	$('#amortizationMethodHTM option').remove();
                	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
                }
            }
        } else if ($("#valuationMethodHTM").val() == '${valuationMethodNA}'){
            
            //$("#marketPriceHTM").val('');
        	if($.browser.msie){
                $('#marketPriceHTM option').remove();
                var optionsAmt = $('#marketPriceHTM').attr('options');
                  	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            }
        	
            $("#marketPriceHTM").val('${marketPriceNA}');
            $("#marketPriceHTMHidden").val($("#marketPriceHTM").val());
            $("#marketPriceHTM").attr('disabled', 'disabled');
            $("#amortizationMethodHTM").val('');
            $("#amortizationMethodHTM").attr('disabled', 'disabled');
            $('#valuationMethodHTM').removeClass('fieldError');
            $("#valuationMethodHTMErrorMessage").html("");
            
            if (($('#valuationMethodAFS').val()=='${valuationMethodFairValue}') || ($('#valuationMethodTrade').val()=='${valuationMethodFairValue}')) {
            	$("td[id=tdMarketPrice] label span").html(" *");
            	
            } else if (($('#valuationMethodAFS').val()=='${valuationMethodAmortized}') || ($('#valuationMethodTrade').val()=='${valuationMethodAmortized}')) {
            	
            	$("td[id=tdMarketPrice] label span").html(" *");
	        	$("td[id=tdAmortization] label span").html(" *");
            }
            else {
            	$("td[id=tdMarketPrice] label span").html("");
	        	$("td[id=tdAmortization] label span").html(" ");
            } 
        } else {
        	if (($('#valuationMethodAFS').val()=='${valuationMethodFairValue}') || ($('#valuationMethodTrade').val()=='${valuationMethodFairValue}')) {
            	$("td[id=tdMarketPrice] label span").html(" *");
            	
            } else if (($('#valuationMethodAFS').val()=='${valuationMethodAmortized}') || ($('#valuationMethodTrade').val()=='${valuationMethodAmortized}')) {
            	
            	$("td[id=tdMarketPrice] label span").html(" *");
	        	$("td[id=tdAmortization] label span").html(" *");
            }
            else {
            	$("td[id=tdMarketPrice] label span").html("");
	        	$("td[id=tdAmortization] label span").html(" ");
            } 
            $("#marketPriceHTM").val('');
            $("#amortizationMethodHTM").val('');
            $("#marketPriceHTM").attr('disabled', false);
            $("#amortizationMethodHTM").attr('disabled', false);
            
            $('#amortizationMethodHTM option[value="${amortizationSL}"]').show();
            if (hasInterest == 'true'){
            	$('#amortizationMethodHTM option[value="${amortizationEIR}"]').show();
            } else {
            	$('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
            }
        	$('#amortizationMethodHTM option[value="${amortizationNPV}"]').show();
        	
            if($.browser.msie){
            	// Market Price
                $('#marketPriceHTM option').remove();
                var optionsAmt = $('#marketPriceHTM').attr('options');
                optionsAmt[optionsAmt.length] = new Option('', '');
                if ('${valueForMarketPriceClose}'!=''){
            		optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
            	}
                if ('${valueForMarketPriceLow}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
                }
                if ('${valueForMarketPriceHigh}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                }
                if ('${valueForMarketPriceNA}'!=''){
                	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
                }
                
                // Amortization
                $('#amortizationMethodHTM option').remove();
                var optionsAmtMd = $('#amortizationMethodHTM').attr('options');
            	optionsAmtMd[optionsAmtMd.length] = new Option('', '');
            	if ('${valueForAmortizationSL}'!=''){
            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
            	}
            	if (hasInterest=='true'){
	            	if ('${valueForAmortizationEIR}'!=''){
	            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
	            	}
            	}
            	if ('${valueForAmortizationNPV}'!=''){
            		optionsAmtMd[optionsAmtMd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            	}
            }
        }
    });

    // END For Valuation Method HTM
}

function doSave() {

	$('#isTradeHidden').val(($('input[name=isTrade]').is(":checked")) ? true :false);
	$('#isAfsHidden').val(($('input[name=isAfs]').is(":checked")) ? true :false);
	$('#isHtmHidden').val(($('input[name=isHtm]').is(":checked")) ? true :false);
	
	if($("#marketPriceTrade").val() != '') $('#marketPriceTradeHidden').attr("disabled", false);
	if($("#marketPriceAFS").val() != '') $('#marketPriceAFSHidden').attr("disabled", false);
	if($("#marketPriceHTM").val() != '') $('#marketPriceHTMHidden').attr("disabled", false);
	
	if($("amortizationMethodTrade").val() != '') $("#amortizationMethodTradeHidden").attr("disabled", false);
	if($("amortizationMethodAFS").val() != '') $("#amortizationMethodAFSHidden").attr("disabled", false);
	if($("amortizationMethodHTM").val() != '') $("#amortizationMethodHTMHidden").attr("disabled", false);
	
	var listedError = $('#listedShare').hasClass('fieldError');
	if (listedError){
		return false;
	}
   
	if ($("#securityClass").val()=="EQ") {
        $("div .FI_only").remove();
        $("div .MM_only").remove();
        var checkError = $("div .EQ_only input").hasClass('fieldError');
        if (checkError){
            return false;
        } else {
            return true;
        }
    } else if ($("#securityClass").val() == "MM") {
        $("div .EQ_only").remove();
        $("div .FI_only").remove();
        var checkError = $("div .MM_only input").hasClass('fieldError');
        if (checkError){
            return false;
        } else {
        	
                return true;
           
        }
    } else if ($("#securityClass").val() == "FI") {
    	var isReload = $("#isreload").val();
    	if (isReload == false || isReload == 'false') {
    		if ($("#generateButton").hasClass("visible")) {
                $("#generateButtonError").html("* populate coupon schedule").show();
                return false;
            }
    	}
        
        var checkClass = $("#gridCoupon tbody tr").find("input[id*='nextPaymentDate']").hasClass('fieldError');
        var checkPaymentClass = $("#gridCoupon tbody tr").find("input[id*='paymentDate']").hasClass('fieldError');
        if ((checkClass) || (checkPaymentClass)) {
            return false;
        } else {
            $("div .EQ_only").remove();
            $("div .MM_only").remove();
            var checkError = $("div .FI_only input").hasClass('fieldError');
            if (checkError){
                return false;
            } else {
            	return true;
            }
        }
    }
    else {
       return true;
    }
}

function doConfirm(){   
    if($("#securityClass").val()=="EQ"){
        $("div .FI_only").remove();
        $("div .MM_only").remove();
        return true;
    }       
    else if ($("#securityClass").val() == "MM") {
        $("div .EQ_only").remove();
        $("div .FI_only").remove();
        return true;
    }           
    else {
        //if ($("#generateButton").hasClass("visible")) {
        //  alert(" Error ! please populate coupon schedule ");
        //  return false;
        //}
        //var checkClass = $("#gridCoupon tbody tr").find("input[id*='nextPaymentDate']").hasClass('fieldError');
        //if (checkClass){
        //  alert(" Error Found ! Please Check your 'End Date' ");
        //  return false;
        //} else {
            $("div .EQ_only").remove();
            $("div .MM_only").remove();
            return true;
        //}
    }
}
//--------------------------//

// EVENT DO CHECK TRADE, AFS, & HTM =========================================================================================
function doChecked(){
    if (($("input[name=isTrade]").is(':checked'))&&(!($("input[name=isAfs]").is(':checked')))&&(!($("input[name=isHtm]").is(':checked')))) {
        $('.trade').attr('disabled', false);
        $('.afs').attr('disabled', 'disabled');
        $('.htm').attr('disabled', 'disabled');
        $('.afs, .htm').removeClass('fieldError');
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if (($("input[name=isAfs]").is(':checked'))&&(!($("input[name=isTrade]").is(':checked')))&&(!($("input[name=isHtm]").is(':checked')))) {
        $('.trade, .htm').removeClass('fieldError');
        $('.trade').attr('disabled', 'disabled');
        $('.afs').attr('disabled', false);
        $('.htm').attr('disabled', 'disabled');
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if (($("input[name=isHtm]").is(':checked'))&&(!($("input[name=isTrade]").is(':checked')))&&(!($("input[name=isAfs]").is(':checked')))) {
        $('.trade, .afs').removeClass('fieldError');
        $('.trade').attr('disabled', 'disabled');
        $('.afs').attr('disabled', 'disabled');
        $('.htm').attr('disabled', false);
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if (($("input[name=isTrade]").is(':checked'))&&($("input[name=isAfs]").is(':checked'))&&(!($("input[name=isHtm]").is(':checked')))) {
        $('.htm').removeClass('fieldError');
        $('.trade').attr('disabled', false);
        $('.afs').attr('disabled', false);
        $('.htm').attr('disabled', 'disabled');
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if (($("input[name=isTrade]").is(':checked'))&&($("input[name=isHtm]").is(':checked'))&&(!($("input[name=isAfs]").is(':checked')))) {
        $('.afs').removeClass('fieldError');
        $('.trade').attr('disabled', false);
        $('.afs').attr('disabled', 'disabled');
        $('.htm').attr('disabled', false);
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if (($("input[name=isAfs]").is(':checked'))&&($("input[name=isHtm]").is(':checked'))&&(!($("input[name=isTrade]").is(':checked')))) {
        $('.trade').removeClass('fieldError');
        $('.trade').attr('disabled', 'disabled');
        $('.afs').attr('disabled', false);
        $('.htm').attr('disabled', false);
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if (($("input[name=isTrade]").is(':checked'))&&($("input[name=isAfs]").is(':checked'))&&($("input[name=isHtm]").is(':checked'))) {
        $('.trade').attr('disabled', false);
        $('.afs').attr('disabled', false);
        $('.htm').attr('disabled', false);
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    if ((!($("input[name=isTrade]").is(':checked')))&&(!($("input[name=isAfs]").is(':checked')))&&(!($("input[name=isHtm]").is(':checked')))) {
        $('.trade, .afs, .htm').removeClass('fieldError');
        $('.trade').attr('disabled', 'disabled');
        $('.afs').attr('disabled', 'disabled');
        $('.htm').attr('disabled', 'disabled');
        $('td[id=tdValuationMethod] label span').html(" *");
        $('td[id=tdTrade] label span').html("");
    }
    
    if ((!$("input[name=isTrade]").is(':checked'))&&(!$("input[name=isAfs]").is(':checked'))&&(!$("input[name=isHtm]").is(':checked'))){
    	$('td[id=tdValuationMethod] label span').html("");
    	$('td[id=tdTrade] label span').html(" *");
    }
}
// ==========================================================================================================================
    
// SET CHECKED TRADE, AFS, & HTM =========================================================================================
function setChecked(){
    
	if ($('#isTradeHidden').val()=='true'){
        $('input[name=isTrade]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }
    
	if ($('#isAfsHidden').val()=='true'){
        $('input[name=isAfs]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }
   
	if ($('#isHtmHidden').val()=='true'){
        $('input[name=isHtm]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }

	if (($('#isTradeHidden').val()=='true') && ($('#isAfsHidden').val()=='true')){
        $('input[name=isTrade]').attr('checked', true);
        $('input[name=isAfs]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }
    
	if (($('#isTradeHidden').val()=='true') && ($('#isHtmHidden').val()=='true')){
        $('input[name=isTrade]').attr('checked', true);
        $('input[name=isHtm]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }
    
	if (($('#isAfsHidden').val()=='true') && ($('#isHtmHidden').val()=='true')){
        $('input[name=isAfs]').attr('checked', true);
        $('input[name=isHtm]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }
   
	if (($('#isTradeHidden').val()=='true') && ($('#isAfsHidden').val()=='true') && ($('#isHtmHidden').val()=='true')){
        $('input[name=isTrade]').attr('checked', true);
        $('input[name=isAfs]').attr('checked', true);
        $('input[name=isHtm]').attr('checked', true);
        $('td[id=tdTrade] label span').html("");
    }
    
	if (($('#isTradeHidden').val()=='false') && ($('#isAfsHidden').val()=='false') && ($('#isHtmHidden').val()=='false')){
        $('input[name=isAfs]').attr('checked', false);
        $('input[name=isHtm]').attr('checked', false);
    }
}
// ==========================================================================================================================

function isCheckCoupon() {
    var length = parseInt($("#totalCoupon").val());
    var b = 2;
    var dateFormatCompare = "yyyy/mm/dd";
    for (i=0; i< length; i++) {
        var next = $("#gridCoupon tbody tr:nth-child(" + (i+1) +")").find("input[id*='nextPaymentDate']");
        var nextFromCurrentDate = new Date(next.datepicker('getDate'));
        var nextFromCurrentDateFormat = nextFromCurrentDate.format("yyyy/mm/dd");
        
        var start = $("#gridCoupon tbody tr:nth-child(" + (i+1) +")").find("input[id*='h_lastPaymentDate']");
        //var startFromCurrentDate = new Date(start.datepicker('getDate'));
        //var startFromCurrentDateFormat = startFromCurrentDate.format("yyyy/mm/dd");
        
        var startFromCurrentDateFormat = $.datepicker.parseDate('${appProp?.jqueryDateFormat}', start.val(), null).format(dateFormatCompare);
        //var maturityDate = new Date($("#maturityDate").val());
        //var maturityDateFormat = maturityDate.format("yyyy/mm/dd");
        
        if ( startFromCurrentDateFormat > nextFromCurrentDateFormat) {
            var errorNextDate= $(next).addClass('fieldError');
            } else {
            var errorNextDate= $(next).removeClass('fieldError');
        }   
    }
}

function valuationMethodNA() {
	if ($("#valuationMethodTrade").val() == '${valuationMethodNA}') {
        $('#marketPriceTrade').attr("disabled", "disabled");
        $('#amortizationMethodTrade').attr("disabled", "disabled");
    }
	
    if ($("#valuationMethodAFS").val() == '${valuationMethodNA}') {
        $('#marketPriceAFS').attr("disabled", "disabled");
        $('#amortizationMethodAFS').attr("disabled", "disabled");
    } 
    
    if ($("#valuationMethodHTM").val() == '${valuationMethodNA}') {
        $('#marketPriceHTM').attr("disabled", "disabled");
        $('#amortizationMethodHTM').attr("disabled", "disabled");
    }
}

function valueDropdownWhenEdit(){
	var isPrice = $('#isPrice').val();
	var isDiscounted = $('#isDiscounted').val();
	var hasInterest = $('#hasInterest').val();
	
//	console.log("edit mode >> is price = " +isPrice);
//	console.log("edit mode >> is discounted = " +isDiscounted);
//	console.log("edit mode >> has interest = "+hasInterest);
	
	// FOR IE
	if ($.browser.msie){
		if (isPrice == 'true') {
			if ($("#valuationMethodTrade").val() == '${valuationMethodFairValue}'){
				$('#marketPriceTrade option[value="${marketPriceNA}"]').remove();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodFairValue}'){
				$('#marketPriceAFS option[value="${marketPriceNA}"]').remove();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodFairValue}'){
				$('#marketPriceHTM option[value="${marketPriceNA}"]').remove();
			}
		}
		
		if (isPrice == 'false'){
			if ($("#valuationMethodTrade").val() == '${valuationMethodFairValue}'){
				$('#marketPriceTrade option[value="${marketPriceClose}"]').remove();
				$('#marketPriceTrade option[value="${marketPriceLow}"]').remove();
				$('#marketPriceTrade option[value="${marketPriceHigh}"]').remove();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodFairValue}'){
				$('#marketPriceAFS option[value="${marketPriceClose}"]').remove();
				$('#marketPriceAFS option[value="${marketPriceLow}"]').remove();
				$('#marketPriceAFS option[value="${marketPriceHigh}"]').remove();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodFairValue}'){
				$('#marketPriceHTM option[value="${marketPriceClose}"]').remove();
				$('#marketPriceHTM option[value="${marketPriceLow}"]').remove();
				$('#marketPriceHTM option[value="${marketPriceHigh}"]').remove();
			}
			
			if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
				$('#marketPriceTrade option[value="${marketPriceClose}"]').remove();
				$('#marketPriceTrade option[value="${marketPriceLow}"]').remove();
				$('#marketPriceTrade option[value="${marketPriceHigh}"]').remove();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
				$('#marketPriceAFS option[value="${marketPriceClose}"]').remove();
				$('#marketPriceAFS option[value="${marketPriceLow}"]').remove();
				$('#marketPriceAFS option[value="${marketPriceHigh}"]').remove();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
				$('#marketPriceHTM option[value="${marketPriceClose}"]').remove();
				$('#marketPriceHTM option[value="${marketPriceLow}"]').remove();
				$('#marketPriceHTM option[value="${marketPriceHigh}"]').remove();
			}
		}
		
		if (hasInterest == 'false'){
			if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodTrade option[value="${amortizationEIR}"]').remove();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodAFS option[value="${amortizationEIR}"]').remove();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodHTM option[value="${amortizationEIR}"]').remove();
			}
		}
		
		if (isDiscounted == 'false'){
			if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodTrade option[value="${amortizationSL}"]').remove();
				$('#amortizationMethodTrade option[value="${amortizationEIR}"]').remove();
				$('#amortizationMethodTrade option[value="${amortizationNPV}"]').remove();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodAFS option[value="${amortizationSL}"]').remove();
				$('#amortizationMethodAFS option[value="${amortizationEIR}"]').remove();
				$('#amortizationMethodAFS option[value="${amortizationNPV}"]').remove();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodHTM option[value="${amortizationSL}"]').remove();
				$('#amortizationMethodHTM option[value="${amortizationEIR}"]').remove();
				$('#amortizationMethodHTM option[value="${amortizationNPV}"]').remove();
			}
		}
	} else {
		if (isPrice == 'true') {
			if ($("#valuationMethodTrade").val() == '${valuationMethodFairValue}'){
				$('#marketPriceTrade option[value="${marketPriceNA}"]').hide();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodFairValue}'){
				$('#marketPriceAFS option[value="${marketPriceNA}"]').hide();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodFairValue}'){
				$('#marketPriceHTM option[value="${marketPriceNA}"]').hide();
			}
		}
		
		if (isPrice == 'false'){
			if ($("#valuationMethodTrade").val() == '${valuationMethodFairValue}'){
				$('#marketPriceTrade option[value="${marketPriceClose}"]').hide();
				$('#marketPriceTrade option[value="${marketPriceLow}"]').hide();
				$('#marketPriceTrade option[value="${marketPriceHigh}"]').hide();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodFairValue}'){
				$('#marketPriceAFS option[value="${marketPriceClose}"]').hide();
				$('#marketPriceAFS option[value="${marketPriceLow}"]').hide();
				$('#marketPriceAFS option[value="${marketPriceHigh}"]').hide();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodFairValue}'){
				$('#marketPriceHTM option[value="${marketPriceClose}"]').hide();
				$('#marketPriceHTM option[value="${marketPriceLow}"]').hide();
				$('#marketPriceHTM option[value="${marketPriceHigh}"]').hide();
			}
			
			if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
				$('#marketPriceTrade option[value="${marketPriceClose}"]').hide();
				$('#marketPriceTrade option[value="${marketPriceLow}"]').hide();
				$('#marketPriceTrade option[value="${marketPriceHigh}"]').hide();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
				$('#marketPriceAFS option[value="${marketPriceClose}"]').hide();
				$('#marketPriceAFS option[value="${marketPriceLow}"]').hide();
				$('#marketPriceAFS option[value="${marketPriceHigh}"]').hide();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
				$('#marketPriceHTM option[value="${marketPriceClose}"]').hide();
				$('#marketPriceHTM option[value="${marketPriceLow}"]').hide();
				$('#marketPriceHTM option[value="${marketPriceHigh}"]').hide();
			}
		}
		
		if (hasInterest == 'false') {
			if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
			}
		}
		
		if (isDiscounted == 'false'){
			if ($("#valuationMethodTrade").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodTrade').val('');
				$('#amortizationMethodTrade option[value="${amortizationSL}"]').hide();
				$('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
				$('#amortizationMethodTrade option[value="${amortizationNPV}"]').hide();
			}
			
			if ($("#valuationMethodAFS").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodAFS').val('');
				$('#amortizationMethodAFS option[value="${amortizationSL}"]').hide();
				$('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
				$('#amortizationMethodAFS option[value="${amortizationNPV}"]').hide();
			}
			
			if ($("#valuationMethodHTM").val() == '${valuationMethodAmortized}'){
				$('#amortizationMethodHTM').val('');
				$('#amortizationMethodHTM option[value="${amortizationSL}"]').hide();
				$('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
				$('#amortizationMethodHTM option[value="${amortizationNPV}"]').hide();
			}
		}
	}
	
}

// not use
function changeCombo(){
	var isPrice = $('#isPrice').val();
	var isDiscounted = $('#isDiscounted').val();

	console.log("Price = " +isPrice);
	console.log("is Discounted = " +isDiscounted);
	if ((isPrice=='false')&&(isDiscounted=='true')) {
		if($.browser.msie){
			var value = $("#valuationMethodTrade").val();
//			$('#valuationMethodTrade option').remove();
//            var options = $('#valuationMethodTrade').attr('options');
//            	options[options.length] = new Option('', '');
//                options[options.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
//                options[options.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodTrade").val(value);
                
            value = $("#marketPriceTrade").val();
//            $('#marketPriceTrade option').remove();
//            var optionsAmt = $('#marketPriceTrade').attr('options');
//                optionsAmt[optionsAmt.length] = new Option('', '');
//                optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceTrade").val(value);
                    
            value = $("#amortizationMethodTrade").val();
//            $('#amortizationMethodTrade option').remove();
//            var optionsAmo = $('#amortizationMethodTrade').attr('options');
//                optionsAmo[optionsAmo.length] = new Option('', '');
//                optionsAmo[optionsAmo.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
//                optionsAmo[optionsAmo.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
//                optionsAmo[optionsAmo.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodTrade").val(value);   
                
            value = $("#valuationMethodAFS").val();
//            $('#valuationMethodAFS option').remove();
//            var options1 = $('#valuationMethodAFS').attr('options');
//                options1[options1.length] = new Option('', '');
//                options1[options1.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
//                options1[options1.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodAFS").val(value);
                
            value = $("#marketPriceAFS").val();
//            $('#marketPriceAFS option').remove();
//            var optionsAmt1 = $('#marketPriceAFS').attr('options');
//                optionsAmt1[optionsAmt1.length] = new Option('', '');
//                optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceAFS").val(value);    
                
            value = $("#amortizationMethodAFS").val();
//            $('#amortizationMethodAFS option').remove();
//            var optionsAmo1 = $('#amortizationMethodAFS').attr('options');
//                optionsAmo1[optionsAmo1.length] = new Option('', '');
//                optionsAmo1[optionsAmo1.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
//                optionsAmo1[optionsAmo1.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
//                optionsAmo1[optionsAmo1.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodAFS").val(value); 
                
            value = $("#valuationMethodHTM").val();
//            $('#valuationMethodHTM option').remove();
//            var options2 = $('#valuationMethodHTM').attr('options');
//                options2[options2.length] = new Option('', '');
//                options2[options2.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
//                options2[options2.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodHTM").val(value);        
                
            value = $("#marketPriceHTM").val();
//            $('#marketPriceHTM option').remove();
//            var optionsAmt2 = $('#marketPriceHTM').attr('options');
//                optionsAmt2[optionsAmt2.length] = new Option('', '');
//                optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceHTM").val(value);
                
            value = $("#amortizationMethodHTM").val();
//            $('#amortizationMethodHTM option').remove();
//            var optionsAmo2 = $('#amortizationMethodHTM').attr('options');
//                optionsAmo2[optionsAmo2.length] = new Option('', '');
//                optionsAmo2[optionsAmo2.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
//                optionsAmo2[optionsAmo2.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
//                optionsAmo2[optionsAmo2.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodHTM").val(value);
		} else {
            $('#valuationMethodTrade option[value="${valuationMethodFairValue}"]').hide();
            $('#valuationMethodTrade option[value="${valuationMethodAmortized}"]').show();
            $('#valuationMethodTrade option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceTrade option[value="${marketPriceClose}"]').hide();
            $('#marketPriceTrade option[value="${marketPriceLow}"]').hide();
            $('#marketPriceTrade option[value="${marketPriceHigh}"]').hide();
            $('#marketPriceTrade option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodTrade option[value="${amortizationSL}"]').show();
            $('#amortizationMethodTrade option[value="${amortizationEIR}"]').show();
            $('#amortizationMethodTrade option[value="${amortizationNPV}"]').show();
            
            $('#valuationMethodAFS option[value="${valuationMethodFairValue}"]').hide();
            $('#valuationMethodAFS option[value="${valuationMethodAmortized}"]').show();
            $('#valuationMethodAFS option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceAFS option[value="${marketPriceClose}"]').hide();
            $('#marketPriceAFS option[value="${marketPriceLow}"]').hide();
            $('#marketPriceAFS option[value="${marketPriceHigh}"]').hide();
            $('#marketPriceAFS option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodAFS option[value="${amortizationSL}"]').show();
            $('#amortizationMethodAFS option[value="${amortizationEIR}"]').show();
            $('#amortizationMethodAFS option[value="${amortizationNPV}"]').show();
            
            $('#valuationMethodHTM option[value="${valuationMethodFairValue}"]').hide();
            $('#valuationMethodHTM option[value="${valuationMethodAmortized}"]').show();
            $('#valuationMethodHTM option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceHTM option[value="${marketPriceClose}"]').hide();
            $('#marketPriceHTM option[value="${marketPriceLow}"]').hide();
            $('#marketPriceHTM option[value="${marketPriceHigh}"]').hide();
            $('#marketPriceHTM option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodHTM option[value="${amortizationSL}"]').show();
            $('#amortizationMethodHTM option[value="${amortizationEIR}"]').show();
            $('#amortizationMethodHTM option[value="${amortizationNPV}"]').show();
		}
	}else if ((isPrice=='false')&&(isDiscounted=='false')) {
        if($.browser.msie){
            var value = $("#valuationMethodTrade").val();
//            $('#valuationMethodTrade option').remove();
//            var options = $('#valuationMethodTrade').attr('options');
//                options[options.length] = new Option('', '');
//                options[options.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodTrade").val(value);
            
            value = $("#marketPriceTrade").val();
//            $('#marketPriceTrade option').remove();
//            var optionsAmt = $('#marketPriceTrade').attr('options');
//                optionsAmt[optionsAmt.length] = new Option('', '');
//                optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceTrade").val(value);
            
            value = $("#amortizationMethodTrade").val();
//            $('#amortizationMethodTrade option').remove();
//            var optionsAmo = $('#amortizationMethodTrade').attr('options');
//                optionsAmo[optionsAmo.length] = new Option('', '');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodTrade").val(value);
            
            value = $("#valuationMethodAFS").val();
//            $('#valuationMethodAFS option').remove();
//            var options1 = $('#valuationMethodAFS').attr('options');
//                options1[options1.length] = new Option('', '');
//                options1[options1.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodAFS").val(value);
            
            value = $("#marketPriceAFS").val();
//            $('#marketPriceAFS option').remove();
//            var optionsAmt1 = $('#marketPriceAFS').attr('options');
//                optionsAmt1[optionsAmt1.length] = new Option('', '');
//                optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceAFS").val(value);
            
            value = $("#amortizationMethodAFS").val();
//            $('#amortizationMethodAFS option').remove();
//            var optionsAmo1 = $('#amortizationMethodAFS').attr('options');
//                optionsAmo1[optionsAmo1.length] = new Option('', '');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodAFS").val(value);
            
            value = $("#valuationMethodHTM").val();
//            $('#valuationMethodHTM option').remove();
//            var options2 = $('#valuationMethodHTM').attr('options');
//                options2[options2.length] = new Option('', '');
//                options2[options2.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodHTM").val(value);
            
            value = $("#marketPriceHTM").val();
//            $('#marketPriceHTM option').remove();
//            var optionsAmt2 = $('#marketPriceHTM').attr('options');
//                optionsAmt2[optionsAmt2.length] = new Option('', '');
//                optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceHTM").val(value);
            
            value = $("#amortizationMethodHTM").val();
//            $('#amortizationMethodHTM option').remove();
//            var optionsAmo2 = $('#amortizationMethodHTM').attr('options');
//                optionsAmo2[optionsAmo2.length] = new Option('', '');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodHTM").val(value);
        }else{
//            $('#valuationMethodTrade option[value="${valuationMethodFairValue}"]').hide();
//            $('#valuationMethodTrade option[value="${valuationMethodAmortized}"]').hide();
//            $('#valuationMethodTrade option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceTrade option[value="${marketPriceClose}"]').hide();
            $('#marketPriceTrade option[value="${marketPriceLow}"]').hide();
            $('#marketPriceTrade option[value="${marketPriceHigh}"]').hide();
            $('#marketPriceTrade option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodTrade option[value="${amortizationSL}"]').hide();
            $('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
            $('#amortizationMethodTrade option[value="${amortizationNPV}"]').hide();
            
//            $('#valuationMethodAFS option[value="${valuationMethodFairValue}"]').hide();
//            $('#valuationMethodAFS option[value="${valuationMethodAmortized}"]').hide();
//            $('#valuationMethodAFS option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceAFS option[value="${marketPriceClose}"]').hide();
            $('#marketPriceAFS option[value="${marketPriceLow}"]').hide();
            $('#marketPriceAFS option[value="${marketPriceHigh}"]').hide();
            $('#marketPriceAFS option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodAFS option[value="${amortizationSL}"]').hide();
            $('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
            $('#amortizationMethodAFS option[value="${amortizationNPV}"]').hide();
            
//            $('#valuationMethodHTM option[value="${valuationMethodFairValue}"]').hide();
//            $('#valuationMethodHTM option[value="${valuationMethodAmortized}"]').hide();
//            $('#valuationMethodHTM option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceHTM option[value="${marketPriceClose}"]').hide();
            $('#marketPriceHTM option[value="${marketPriceLow}"]').hide();
            $('#marketPriceHTM option[value="${marketPriceHigh}"]').hide();
            $('#marketPriceHTM option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodHTM option[value="${amortizationSL}"]').hide();
            $('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
            $('#amortizationMethodHTM option[value="${amortizationNPV}"]').hide();
        }
	}else if ((isPrice=='true')&&(isDiscounted=='false')) {
        if($.browser.msie){
            var value = $("#valuationMethodTrade").val();
//            $('#valuationMethodTrade option').remove();
//            var options = $('#valuationMethodTrade').attr('options');
//                options[options.length] = new Option('', '');
//                options[options.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
                
            if( ('${mode}' == 'edit'))
                $("#valuationMethodTrade").val(value);
            
            value = $("#marketPriceTrade").val();
//            $('#marketPriceTrade option').remove();
//            var optionsAmt = $('#marketPriceTrade').attr('options');
//                optionsAmt[optionsAmt.length] = new Option('', '');
//                optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
//                optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
//                optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                
            if( ('${mode}' == 'edit'))
                $("#marketPriceTrade").val(value);
            
            value = $("#amortizationMethodTrade").val();
//            $('#amortizationMethodTrade option').remove();
//            var optionsAmo = $('#amortizationMethodTrade').attr('options');
//                optionsAmo[optionsAmo.length] = new Option('', '');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodTrade").val(value);
            
            value = $("#valuationMethodAFS").val();
//            $('#valuationMethodAFS option').remove();
//            var options1 = $('#valuationMethodAFS').attr('options');
//                options1[options1.length] = new Option('', '');
//                options1[options1.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
                
            if( ('${mode}' == 'edit'))
                $("#valuationMethodAFS").val(value);
            
            value = $("#marketPriceAFS").val();
//            $('#marketPriceAFS option').remove();
//            var optionsAmt1 = $('#marketPriceAFS').attr('options');
//                optionsAmt1[optionsAmt1.length] = new Option('', '');
//                optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
//                optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
//                optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                
            if( ('${mode}' == 'edit'))
                $("#marketPriceAFS").val(value);
            
            value = $("#amortizationMethodAFS").val();
//            $('#amortizationMethodAFS option').remove();
//            var optionsAmo1 = $('#amortizationMethodAFS').attr('options');
//                optionsAmo1[optionsAmo1.length] = new Option('', '');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodAFS").val(value);
            
            value = $("#valuationMethodHTM").val();
//            $('#valuationMethodHTM option').remove();
//            var options2 = $('#valuationMethodHTM').attr('options');
//                options2[options2.length] = new Option('', '');
//                options2[options2.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
                
            if( ('${mode}' == 'edit'))
                $("#valuationMethodHTM").val(value);
            
            value = $("#marketPriceHTM").val();
//            $('#marketPriceHTM option').remove();
//            var optionsAmt2 = $('#marketPriceHTM').attr('options');
//                optionsAmt2[optionsAmt2.length] = new Option('', '');
//                optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
//                optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
//                optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
                
            if( ('${mode}' == 'edit'))
                $("#marketPriceHTM").val(value);    
            
            value = $("#amortizationMethodHTM").val();
//            $('#amortizationMethodHTM option').remove();
//            var optionsAmo2 = $('#amortizationMethodHTM').attr('options');
//                optionsAmo2[optionsAmo2.length] = new Option('', '');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodHTM").val(value); 
        }else{
//            $('#valuationMethodTrade option[value="${valuationMethodFairValue}"]').show();
//            $('#valuationMethodTrade option[value="${valuationMethodAmortized}"]').hide();
//            $('#valuationMethodTrade option[value="${valuationMethodNA}"]').hide();
            
            $('#marketPriceTrade option[value="${marketPriceClose}"]').show();
            $('#marketPriceTrade option[value="${marketPriceLow}"]').show();
            $('#marketPriceTrade option[value="${marketPriceHigh}"]').show();
            $('#marketPriceTrade option[value="${marketPriceNA}"]').hide();
            
            $('#amortizationMethodTrade option[value="${amortizationSL}"]').hide();
            $('#amortizationMethodTrade option[value="${amortizationEIR}"]').hide();
            $('#amortizationMethodTrade option[value="${amortizationNPV}"]').hide();
            
//            $('#valuationMethodAFS option[value="${valuationMethodFairValue}"]').show();
//            $('#valuationMethodAFS option[value="${valuationMethodAmortized}"]').hide();
//            $('#valuationMethodAFS option[value="${valuationMethodNA}"]').hide();
            
            $('#marketPriceAFS option[value="${marketPriceClose}"]').show();
            $('#marketPriceAFS option[value="${marketPriceLow}"]').show();
            $('#marketPriceAFS option[value="${marketPriceHigh}"]').show();
            $('#marketPriceAFS option[value="${marketPriceNA}"]').hide();
            
            $('#amortizationMethodAFS option[value="${amortizationSL}"]').hide();
            $('#amortizationMethodAFS option[value="${amortizationEIR}"]').hide();
            $('#amortizationMethodAFS option[value="${amortizationNPV}"]').hide();
            
//            $('#valuationMethodHTM option[value="${valuationMethodFairValue}"]').show();
//            $('#valuationMethodHTM option[value="${valuationMethodAmortized}"]').hide();
//            $('#valuationMethodHTM option[value="${valuationMethodNA}"]').hide();
            
            $('#marketPriceHTM option[value="${marketPriceClose}"]').show();
            $('#marketPriceHTM option[value="${marketPriceLow}"]').show();
            $('#marketPriceHTM option[value="${marketPriceHigh}"]').show();
            $('#marketPriceHTM option[value="${marketPriceNA}"]').hide();
            
            $('#amortizationMethodHTM option[value="${amortizationSL}"]').hide();
            $('#amortizationMethodHTM option[value="${amortizationEIR}"]').hide();
            $('#amortizationMethodHTM option[value="${amortizationNPV}"]').hide();    
        }
	}else{
		
		if($.browser.msie){
			var value = $("#valuationMethodTrade").val();
                //24-03-2014 bug in method back IE
//                 $('#valuationMethodTrade option').remove();
//                 var options = $('#valuationMethodTrade').attr('options');
//                     options[options.length] = new Option('', '');
//                     options[options.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
//                     options[options.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');

            if( ('${mode}' == 'edit'))
                $("#valuationMethodTrade").val(value);

            value = $("#marketPriceTrade").val();
//                 $('#marketPriceTrade option').remove();
//                 var optionsAmt = $('#marketPriceTrade').attr('options');
//                     optionsAmt[optionsAmt.length] = new Option('', '');
//                     optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
//                     optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
//                     optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
//                     optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceTrade").val(value);

            value = $("#amortizationMethodTrade").val();
//                 $('#amortizationMethodTrade option').remove();
//                 var optionsAmo = $('#amortizationMethodTrade').attr('options');
//                     optionsAmo[optionsAmo.length] = new Option('', '');
//                     optionsAmo[optionsAmo.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
//                     optionsAmo[optionsAmo.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
//                     optionsAmo[optionsAmo.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodTrade").val(value);

            value = $("#valuationMethodAFS").val();
//                 $('#valuationMethodAFS option').remove();
//                 var options1 = $('#valuationMethodAFS').attr('options');
//                     options1[options1.length] = new Option('', '');
//                     options1[options1.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
//                     options1[options1.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');

            if( ('${mode}' == 'edit'))
                $("#valuationMethodAFS").val(value);

            value = $("#marketPriceAFS").val();
//                 $('#marketPriceAFS option').remove();
//                 var optionsAmt1 = $('#marketPriceAFS').attr('options');
//                     optionsAmt1[optionsAmt1.length] = new Option('', '');
//                     optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
//                     optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
//                     optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
//                     optionsAmt1[optionsAmt1.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceAFS").val(value);

            value = $("#amortizationMethodAFS").val();
//                 $('#amortizationMethodAFS option').remove();
//                 var optionsAmo1 = $('#amortizationMethodAFS').attr('options');
//                     optionsAmo1[optionsAmo1.length] = new Option('', '');
//                     optionsAmo1[optionsAmo1.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
//                     optionsAmo1[optionsAmo1.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
//                     optionsAmo1[optionsAmo1.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodAFS").val(value);

            value = $("#valuationMethodHTM").val();
//                 $('#valuationMethodHTM option').remove();
//                 var options2 = $('#valuationMethodHTM').attr('options');
//                     options2[options2.length] = new Option('', '');
//                     options2[options2.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
//                     options2[options2.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
            if( ('${mode}' == 'edit'))
                $("#valuationMethodHTM").val(value);

            value = $("#marketPriceHTM").val();
//                 $('#marketPriceHTM option').remove();
//                 var optionsAmt2 = $('#marketPriceHTM').attr('options');
//                     optionsAmt2[optionsAmt2.length] = new Option('', '');
//                     optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
//                     optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
//                     optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
//                     optionsAmt2[optionsAmt2.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
            if( ('${mode}' == 'edit'))
                $("#marketPriceHTM").val(value);

            value = $("#amortizationMethodHTM").val();
//                 $('#amortizationMethodHTM option').remove();
//                 var optionsAmo2 = $('#amortizationMethodHTM').attr('options');
//                     optionsAmo2[optionsAmo2.length] = new Option('', '');
//                     optionsAmo2[optionsAmo2.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
//                     optionsAmo2[optionsAmo2.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
//                     optionsAmo2[optionsAmo2.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
            if( ('${mode}' == 'edit'))
                $("#amortizationMethodHTM").val(value);
        }else{
            $('#valuationMethodTrade option[value="${valuationMethodFairValue}"]').show();
            $('#valuationMethodTrade option[value="${valuationMethodAmortized}"]').show();
//            $('#valuationMethodTrade option[value="${valuationMethodNA}"]').hide();
            $('#valuationMethodTrade option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceTrade option[value="${marketPriceClose}"]').show();
            $('#marketPriceTrade option[value="${marketPriceLow}"]').show();
            $('#marketPriceTrade option[value="${marketPriceHigh}"]').show();
            $('#marketPriceTrade option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodTrade option[value="${amortizationSL}"]').show();
            $('#amortizationMethodTrade option[value="${amortizationEIR}"]').show();
            $('#amortizationMethodTrade option[value="${amortizationNPV}"]').show();
            
            $('#valuationMethodAFS option[value="${valuationMethodFairValue}"]').show();
            $('#valuationMethodAFS option[value="${valuationMethodAmortized}"]').show();
//            $('#valuationMethodAFS option[value="${valuationMethodNA}"]').hide();
            $('#valuationMethodAFS option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceAFS option[value="${marketPriceClose}"]').show();
            $('#marketPriceAFS option[value="${marketPriceLow}"]').show();
            $('#marketPriceAFS option[value="${marketPriceHigh}"]').show();
            $('#marketPriceAFS option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodAFS option[value="${amortizationSL}"]').show();
            $('#amortizationMethodAFS option[value="${amortizationEIR}"]').show();
            $('#amortizationMethodAFS option[value="${amortizationNPV}"]').show();
            
            $('#valuationMethodHTM option[value="${valuationMethodFairValue}"]').show();
            $('#valuationMethodHTM option[value="${valuationMethodAmortized}"]').show();
//            $('#valuationMethodHTM option[value="${valuationMethodNA}"]').hide();
            $('#valuationMethodHTM option[value="${valuationMethodNA}"]').show();
            
            $('#marketPriceHTM option[value="${marketPriceClose}"]').show();
            $('#marketPriceHTM option[value="${marketPriceLow}"]').show();
            $('#marketPriceHTM option[value="${marketPriceHigh}"]').show();
            $('#marketPriceHTM option[value="${marketPriceNA}"]').show();
            
            $('#amortizationMethodHTM option[value="${amortizationSL}"]').show();
            $('#amortizationMethodHTM option[value="${amortizationEIR}"]').show();
            $('#amortizationMethodHTM option[value="${amortizationNPV}"]').show();
        }
    }
}

function defaultTabValuation(){
	
   $('.trade').val("");
   $('.afs').val("");
   $('.htm').val("");
    
    $('.afs').attr('disabled', 'disabled');
    $('.htm').attr('disabled', 'disabled');
    $('td[id=tdValuationMethod] label span').html(" *");
    $('td[id=tdMarketPrice] label span').html("");
    $('td[id=tdAmortization] label span').html("");
    $('td[id=tdTrade] label span').html("");
    
    $('#isTrade').attr("checked", true);
    $('#isAfs').attr("checked", false);
    $('#isHtm').attr("checked", false);
    $('#marketPriceTrade').attr("disabled", false);
    $('#amortizationMethodTrade').attr("disabled", false);
    defaultValueDropdownOnValuationTrade();
    defaultValueDropdownOnValuationAfs();
    defaultValueDropdownOnValuationHtm();
}

function defaultValueDropdownOnValuationTrade(){
	if($.browser.msie){
		// ============ TRADE =================//
		// valuation method
        $('#valuationMethodTrade option').remove();
        var optionsValTrd = $('#valuationMethodTrade').attr('options');
        optionsValTrd[optionsValTrd.length] = new Option('', '');
        if ('${valueForValuationFairValue}' != ''){
        	optionsValTrd[optionsValTrd.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
        }
        if ('${valueForValuationAmortization}' != ''){
        	optionsValTrd[optionsValTrd.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
        }
        if ('${valueForValuationNA}'!==''){
        	optionsValTrd[optionsValTrd.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
        }
            
        // market price
        $('#marketPriceTrade option').remove();
        var optionsPriTrd = $('#marketPriceTrade').attr('options');
        optionsPriTrd[optionsPriTrd.length] = new Option('', '');
        if ('${valueForMarketPriceClose}'!=''){
        	optionsPriTrd[optionsPriTrd.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
    	}
        if ('${valueForMarketPriceLow}'!=''){
        	optionsPriTrd[optionsPriTrd.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
        }
        if ('${valueForMarketPriceHigh}'!=''){
        	optionsPriTrd[optionsPriTrd.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
        }
        if ('${valueForMarketPriceNA}'!=''){
        	optionsPriTrd[optionsPriTrd.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
        }
        
        // amortization
		$('#amortizationMethodTrade option').remove();
		var optionsAmtTrd = $('#amortizationMethodTrade').attr('options');
		optionsAmtTrd[optionsAmtTrd.length] = new Option('', '');
		console.log("SL " + '${valueForAmortizationSL}');
		console.log("EIR " + '${valueForAmortizationEIR}');
		console.log("NPV " + '${valueForAmortizationNPV}');
		if ('${valueForAmortizationSL}'!=''){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		}
		if ('${valueForAmortizationEIR}'){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
		}
		if ('${valueForAmortizationNPV}'){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		}
		
		
	    
	    }
}

function defaultValueDropdownOnValuationAfs(){
	if($.browser.msie){
		// =========== AFS ============//
		$('#valuationMethodAFS option').remove();
		var optionsValAfs = $('#valuationMethodAFS').attr('options');
		optionsValAfs[optionsValAfs.length] = new Option('', '');
		if ('${valueForValuationFairValue}' != ''){
			optionsValAfs[optionsValAfs.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
		}
		if ('${valueForValuationAmortization}' != ''){
			optionsValAfs[optionsValAfs.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
		}
		if ('${valueForValuationNA}'!==''){
			optionsValAfs[optionsValAfs.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
		}
		
		$('#marketPriceAFS option').remove();
	    var optionsPriAfs = $('#marketPriceAFS').attr('options');
	    optionsPriAfs[optionsPriAfs.length] = new Option('', '');
	    if ('${valueForMarketPriceClose}'!=''){
	    	optionsPriAfs[optionsPriAfs.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
	    }
	    if ('${valueForMarketPriceLow}'!=''){
	    	optionsPriAfs[optionsPriAfs.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
	    }
	    if ('${valueForMarketPriceHigh}'!=''){
	    	optionsPriAfs[optionsPriAfs.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
	    }
	    if ('${valueForMarketPriceNA}'!=''){
	    	optionsPriAfs[optionsPriAfs.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
	    }
	    
	    $('#amortizationMethodAFS option').remove();
	    var optionsAmtAfs = $('#amortizationMethodAFS').attr('options');
	    optionsAmtAfs[optionsAmtAfs.length] = new Option('', '');
	    if ('${valueForAmortizationSL}'!=''){
	    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
	    }
	    if ('${valueForAmortizationEIR}'!=''){
	    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
	    }
	    if ('${valueForAmortizationNPV}'!=''){
	    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
	    }
	}
}

function defaultValueDropdownOnValuationHtm(){
	if($.browser.msie){
		// ============== HTM ============= //
		$('#valuationMethodHTM option').remove();
		  var optionsValHtm = $('#valuationMethodHTM').attr('options');
		  optionsValHtm[optionsValHtm.length] = new Option('', '');
		  if ('${valueForValuationFairValue}' != ''){
			  optionsValHtm[optionsValHtm.length] = new Option('${valueForValuationFairValue}', '${valuationMethodFairValue}');
		  }
		  if ('${valueForValuationAmortization}' != '') {
			  optionsValHtm[optionsValHtm.length] = new Option('${valueForValuationAmortization}', '${valuationMethodAmortized}');
		  }
		  if ('${valueForValuationNA}' != '') {
			  optionsValHtm[optionsValHtm.length] = new Option('${valueForValuationNA}', '${valuationMethodNA}');
		  }
		
		$('#marketPriceHTM option').remove();
		 var optionsPriHtm = $('#marketPriceHTM').attr('options');
		 optionsPriHtm[optionsPriHtm.length] = new Option('', '');
		 if ('${valueForMarketPriceClose}'!=''){
			 optionsPriHtm[optionsPriHtm.length] = new Option('${valueForMarketPriceClose}', '${marketPriceClose}');
		 }
		 if ('${valueForMarketPriceLow}'!=''){
			 optionsPriHtm[optionsPriHtm.length] = new Option('${valueForMarketPriceLow}', '${marketPriceLow}');
		 }
		 if ('${valueForMarketPriceHigh}'!=''){
			 optionsPriHtm[optionsPriHtm.length] = new Option('${valueForMarketPriceHigh}', '${marketPriceHigh}');
		 }
		 if ('${valueForMarketPriceNA}'!=''){
			 optionsPriHtm[optionsPriHtm.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
		 }
		
		$('#amortizationMethodHTM option').remove();
		var optionsAmtHtm = $('#amortizationMethodHTM').attr('options');
		optionsAmtHtm[optionsAmtHtm.length] = new Option('', '');
		if ('${valueForAmortizationSL}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		}
		if ('${valueForAmortizationEIR}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
		}
		if ('${valueForAmortizationNPV}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		}
	}
}
function changeParPrice(){
	var isPrice = $('#isPrice').val();
    var priceUnit = parseFloat($('#priceUnit').val());
    var price = Number(1)/Number(priceUnit);
    
    
    if((isPrice == 'false') && (priceUnit == '1')){
    	if (!('${mode}' == 'edit')){
    		$("#parPrice").val(price);
    		$("#parPriceStripped").val(price);
    	}
    	$("#parPrice").attr('disabled', true);
    }else{
//      if (!('${mode}' == 'edit')){
//              $("#parPrice").val('');
//              $("#parPriceStripped").val('');
//      }
    	$("#parPrice").attr('disabled', false);
    }      
        
    changePercentage();     
}


function changePercentage(){
    if ($('#priceUnit').val() == '0.01'){
        $('#percentage').show();
    }else{
        $('#percentage').hide();
    }
}

function changeValueAmortizationTradeIfHasInterest() {
	var hasInterest = $('#hasInterest').val();
	
	if (hasInterest == 'false'){
		$('#amortizationMethodTrade option').remove();
		var optionsAmtTrd = $('#amortizationMethodTrade').attr('options');
		optionsAmtTrd[optionsAmtTrd.length] = new Option('', '');
		if ('${valueForAmortizationSL}'!=''){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		}
		if ('${valueForAmortizationNPV}'){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		}
	} else {
		$('#amortizationMethodTrade option').remove();
		var optionsAmtTrd = $('#amortizationMethodTrade').attr('options');
		optionsAmtTrd[optionsAmtTrd.length] = new Option('', '');
		if ('${valueForAmortizationSL}'!=''){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		}
		if ('${valueForAmortizationEIR}'){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
		}
		if ('${valueForAmortizationNPV}'){
			optionsAmtTrd[optionsAmtTrd.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		}
	}
	
}

function changeValueAmortizationAfsIfHasInterest(){
var hasInterest = $('#hasInterest').val();
	
	if (hasInterest == 'false'){
		 $('#amortizationMethodAFS option').remove();
		    var optionsAmtAfs = $('#amortizationMethodAFS').attr('options');
		    optionsAmtAfs[optionsAmtAfs.length] = new Option('', '');
		    if ('${valueForAmortizationSL}'!=''){
		    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		    }
		    if ('${valueForAmortizationNPV}'!=''){
		    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		    }
	} else {
		console.log("[afs] bukan has interest");
		 $('#amortizationMethodAFS option').remove();
		    var optionsAmtAfs = $('#amortizationMethodAFS').attr('options');
		    optionsAmtAfs[optionsAmtAfs.length] = new Option('', '');
		    if ('${valueForAmortizationSL}'!=''){
		    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		    }
		    if ('${valueForAmortizationEIR}'!=''){
		    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
		    }
		    if ('${valueForAmortizationNPV}'!=''){
		    	optionsAmtAfs[optionsAmtAfs.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		    }
	}
}

function changeValueAmortizationHtmIfHasInterest(){
	
var hasInterest = $('#hasInterest').val();
	
	if (hasInterest == 'false'){
		$('#amortizationMethodHTM option').remove();
		var optionsAmtHtm = $('#amortizationMethodHTM').attr('options');
		optionsAmtHtm[optionsAmtHtm.length] = new Option('', '');
		if ('${valueForAmortizationSL}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		}
		if ('${valueForAmortizationNPV}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		}
	} else {
		$('#amortizationMethodHTM option').remove();
		var optionsAmtHtm = $('#amortizationMethodHTM').attr('options');
		optionsAmtHtm[optionsAmtHtm.length] = new Option('', '');
		if ('${valueForAmortizationSL}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationSL}', '${amortizationSL}');
		}
		if ('${valueForAmortizationEIR}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationEIR}', '${amortizationEIR}');
		}
		if ('${valueForAmortizationNPV}'!=''){
			optionsAmtHtm[optionsAmtHtm.length] = new Option('${valueForAmortizationNPV}', '${amortizationNPV}');
		}
	}
}
