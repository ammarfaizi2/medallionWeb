(function($) {
	$.extend($.fn, {
		validateForm : function(options){
			if( !options ){
				options = {};
			}
			var valid  = false;
			var validator = new $.validator( options, this[0] );
			
			if( validator.settings.debug ){
				this.submit(function(event){
					event.preventDefault();
					validator.beforeSubmit();
				});
			}else {
				this.submit(function(event){
					event.preventDefault();
					valid = validator.beforeSubmit();
					if( !valid ){
						event.preventDefault();
					}
				});
			}
			return validator;
		}
	});
	
	// constructor for validator
	$.validator = function( options, form ) {
		this.settings = $.extend( {}, $.validator.defaults, options );
		this.currentForm = form;
		this.elementRules = {};
		this.rules = {
			required: function(value, element){
				if( value.length == 0 ){
					return false;
				}
				return true;
			},
			date: function isDate(value, element) {
			    try {
			        //Change the below values to determine which format of date you wish to check. It is set to dd/mm/yyyy by default.
			        var DayIndex = 0;
			        var MonthIndex = 1;
			        var YearIndex = 2;
			 
			        value = value.replace(/-/g, "/").replace(/\./g, "/"); 
			        var SplitValue = value.split("/");
			        var OK = true;
			        if (!(SplitValue[DayIndex].length == 1 || SplitValue[DayIndex].length == 2)) {
			            OK = false;
			        }
			        if (OK && !(SplitValue[MonthIndex].length == 1 || SplitValue[MonthIndex].length == 2)) {
			            OK = false;
			        }
			        if (OK && SplitValue[YearIndex].length != 4) {
			            OK = false;
			        }
			        if (OK) {
			            var Day = parseInt(SplitValue[DayIndex], 10);
			            var Month = parseInt(SplitValue[MonthIndex], 10);
			            var Year = parseInt(SplitValue[YearIndex], 10);
			 
			            if (OK = ((Year > 1900) && (Year < new Date().getFullYear()))) {
			                if (OK = (Month <= 12 && Month > 0)) {
			                    var LeapYear = (((Year % 4) == 0) && ((Year % 100) != 0) || ((Year % 400) == 0));
			 
			                    if (Month == 2) {
			                        OK = LeapYear ? Day <= 29 : Day <= 28;
			                    }
			                    else {
			                        if ((Month == 4) || (Month == 6) || (Month == 9) || (Month == 11)) {
			                            OK = (Day > 0 && Day <= 30);
			                        }
			                        else {
			                            OK = (Day > 0 && Day <= 31);
			                        }
			                    }
			                }
			            }
			        }
			        return OK;
			    }
			    catch (e) {
			        return false;
			    }
			}
		};		
		
		if( this.settings.rules ) {
			for(rule in this.settings.rules){
				this.rules[ rule ] = this.settings.rules[rule];
			}
		}		
		if( this.settings.elementRules ) {
			for(rule in this.settings.elementRules){
				this.elementRules[ rule ] = this.settings.elementRules[rule];
			}
		}		
		if( this.settings.messages ) {
			for(msg in this.settings.messages){
				this.messages[ msg ] = this.settings.messages[msg];
			}
		}
	};

	$.extend($.validator, {
		defaults: {
			onsubmit: {},
			debug:false,
			messages: {}
		},
		prototype: {
			beforeSubmit: function(){
				validForm = true;
				this.prepareForm();
				elements = this.currentForm.elements;
				for( i = 0; i < elements.length; i++){
					validForm = this.check( $(elements[i]) );
				}
				return false;
			},
			messages : {
				required: "Fill this field",
				date: "Invalid date",
			},
			check: function(element){
				isGood = true;
				elementType = element.attr("type").toLowerCase();
				elementId =  element.attr("id");
				elementValue =  element.val();
				isRequired = element.hasClass( "required" );
				if( elementType == "submit" || elementType == "reset" || elementType == "hidden" || elementId == ""){
					return true;
				}
				if( isRequired ){
					if( !this.rules["required"].call(this, elementValue, element ) ){
						element.after( "<label class=\"error\">"+this.getMessage( "required" )+"</label>" );
						isGood = false;
					}
					
				}else if( this.elementRules[elementId] ){
					var currentRules = this.elementRules[elementId];
					
					for( tmpRule in currentRules ){
						if( this.elementRules[elementId][tmpRule].call(this, elementValue, element) != currentRules[tmpRule] ){
							element.after( "<label class=\"error\">"+this.getMessage( tmpRule )+"</label>" );
							isGood = false;
						}			
					}
				}
				
				return isGood;
			},
			getMessage: function(idmsg){
				if( this.messages[idmsg] == null ){
					return this.messages["required"];
				}else {
					return this.messages[idmsg];
				}
			},
			prepareForm: function(){
				$( this.currentForm ).find( "label.error" ).remove();
			},
			addMethod: function(name, method, message){

				this.rules[name] = method;
				this.messages[name] = message;
			},
			applyMethodTo: function(id, name, compareTo){
				if( !compareTo ){
					compareTo = true;
				}				
				if( this.elementRules[id] == undefined ){
					this.elementRules[id] = {};
				}
				this.elementRules[id][name] = compareTo;
			}
		}
	});
	
})(jQuery);