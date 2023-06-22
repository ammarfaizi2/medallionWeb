//------Start MAIN Function -------//
$(function(){
	//popUpIssuer();
    //changePercentage();
	
	if (!($('input[name=isTrade]').is(":checked")) && !($('input[name=isAfs]').is(":checked")) && !($('input[name=isHtm]').is(":checked"))){
		$('td[id=tdValuationMethod] label span').html(" ");
		$('td[id=tdMarketPrice] label span').html(" ");
		$('td[id=tdAmortization] label span').html(" ");
	}

    if('${confirming}')
    {
        setChecked();
        $('.isTrade').attr('disabled', 'disabled');
        $('.isAfs').attr('disabled', 'disabled');
        $('.isHtm').attr('disabled', 'disabled');
    }
 
        
    if ('${mode}' == 'view') {
        setChecked();
    }
        
    //if ((('${mode}' == 'edit')) && ('${confirming}' != 'true')) {
    if ((('${mode}' == 'entry') || ('${mode}' == 'edit')) && ('${confirming}' != 'true')) {
        setChecked();
        doChecked();
        valuationMethod();
        //changeCombo(); -- tidak dipakai lagi diganti dengan function dibawahnya
        valueDropdownWhenEdit();
        //changeParPrice();
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
            		$('td[id=tdTrade] label span').html("");
            		$('td[id=tdValuationMethod] label span').html(" *");
            	} else {
            		//$('td[id=tdTrade] label span').html(" *");
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
					$("td[id=tdAmortization] label span").html(" ");
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
			var price = $("input[name='securityType.isPrice']:checked").val();
			var discounted = $("input[name='securityType.isDiscounted']:checked").val();
			var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
			
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
					$("td[id=tdAmortization] label span").html(" ");
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
						//if ('${valueForMarketPriceNA}'!=''){
						//	optionsAmt[optionsAmt.length] = new Option('${valueForMarketPriceNA}', '${marketPriceNA}');
						//}
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
            
				if (discounted == 'false'){
					$("td[id=tdAmortization] label span").html(" ");
				}
				
				if (hasInterest == 'false'){
					$("td[id=tdAmortization] label span").html(" ");
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
					$("td[id=tdAmortization] label span").html(" ");
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
    	var price = $("input[name='securityType.isPrice']:checked").val();
    	var discounted = $("input[name='securityType.isDiscounted']:checked").val();
    	var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
    	
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
            
            if (discounted == 'false'){
	        	$("td[id=tdAmortization] label span").html(" ");
	        }
        	
        	if (hasInterest == 'false'){
        		$("td[id=tdAmortization] label span").html(" ");
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
            
            if (discounted == 'false'){
	        	$("td[id=tdAmortization] label span").html(" ");
	        }
        	
        	if (hasInterest == 'false'){
        		$("td[id=tdAmortization] label span").html(" ");
        	}
        }
        
        //if ($("td[id=tdAmortization] label span").text().includes("*") && (discounted == 'false' || hasInterest == 'false' || price == 'false')){
		//	$("td[id=tdAmortization] label span").html(" ");
		//}
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
    	var price = $("input[name='securityType.isPrice']:checked").val();
    	var discounted = $("input[name='securityType.isDiscounted']:checked").val();
    	var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
    	
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
            	$("td[id=tdAmortization] label span").html(" ");
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
            
            if (discounted == 'false'){
	        	$("td[id=tdAmortization] label span").html(" ");
	        }
        	
        	if (hasInterest == 'false'){
        		$("td[id=tdAmortization] label span").html(" ");
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
	var isPrice = $("input[name='securityType.isPrice']:checked").val();
	var isDiscounted = $("input[name='securityType.isDiscounted']:checked").val();
	var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
	
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
	var isPrice = $("input[name='securityType.isPrice']:checked").val();
	var isDiscounted = $("input[name='securityType.isDiscounted']:checked").val();

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

//function defaultTabValuation(){
//	
//   $('.trade').val("");
//   $('.afs').val("");
//   $('.htm').val("");
//    
//    $('.afs').attr('disabled', 'disabled');
//    $('.htm').attr('disabled', 'disabled');
//    $('td[id=tdValuationMethod] label span').html(" *");
//    $('td[id=tdMarketPrice] label span').html("");
//    $('td[id=tdAmortization] label span').html("");
//    $('td[id=tdTrade] label span').html("");
//    
//    $('#isTrade').attr("checked", true);
//    $('#isAfs').attr("checked", false);
//    $('#isHtm').attr("checked", false);
//    $('#marketPriceTrade').attr("disabled", false);
//    $('#amortizationMethodTrade').attr("disabled", false);
//    defaultValueDropdownOnValuationTrade();
//    defaultValueDropdownOnValuationAfs();
//    defaultValueDropdownOnValuationHtm();
//}

//defaultValueDropdownOnValuationTrade();
//defaultValueDropdownOnValuationAfs();
//defaultValueDropdownOnValuationHtm();

function defaultValueDropdownOnValuationTrade(){
	//if($.browser.msie){
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
		
		
	    
	    //}
}

function defaultValueDropdownOnValuationAfs(){
	//if($.browser.msie){
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
	//}
}

function defaultValueDropdownOnValuationHtm(){
	//if($.browser.msie){
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
	//}
}

function changeValueAmortizationTradeIfHasInterest() {
	//var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
	hasInterest = 'true';
	
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
//var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
	hasInterest = 'true';
	
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
	
	//var hasInterest = $("input[name='securityType.hasInterest']:checked").val();
	var hasInterest = 'true';
	
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
