(function(jQuery){
//    String.prototype.isEmpty = function(){
//        return (this == "");
//    };
//
//    String.prototype.toNumber = function(seperator) {
//    	if (seperator == null) 
//    		return Number(this.valueOf());
//    	else
//    		return Number(this.valueOf().replaceAll(seperator, ""));
//    };
//    
//    String.prototype.replaceAll = function(seperator, replacement) {
//    	return this.valueOf().replace(new RegExp(seperator, 'g'), replacement);
//    };
//    
//    String.prototype.fmtYYYYMMDD = function(seperator){
//    	var arr  = this.valueOf().split(seperator);
//    	if (arr.length == 3)
//    		return arr[2] + arr[0] + arr[1];
//    	return "";
//    };
//    
//    String.prototype.toMMDDYYYY = function(seperator){
//        if (this == null || this.length != 8) 
//            return "";
//        
//        var year = this.substring(0, 4);
//        var month = this.substring(4, 6);
//        var day = this.substring(6, 8);
//        
//        return month + seperator + day + seperator + year;
//    };
//    
//    String.prototype.toDate = function(){
//        if (this == null || this.length != 10) 
//            return "";
//        var arr = this.split("/");
//        var date = new Date();
//        date.setFullYear(Number(arr[2]), Number(arr[0]) - 1, Number(arr[1]));
//        return date;
//    };
//    
//    Number.prototype.leadingZero = function(length){
//        var loop = (length == null) ? 0 : length - (this.valueOf() + "").length;
//        
//        var value = this.valueOf();
//        for (i = 0; i < loop; i++) {
//            value = "0" + value;
//        }
//        return value;
//    };
//    
//    Date.prototype.fmtDate = function(seperator){
//        var tag = (seperator == null) ? "/" : seperator;
//        return (this.getMonth() + 1).leadingZero(2) + tag + this.getDate().leadingZero(2) + tag + this.getFullYear();
//    };
//    
//    Date.prototype.addDate = function(day){
//    	var time = this.getTime();
//    	var addtime = day * 24 * 60 * 60 * 1000;
//    	return new Date(time+addtime);
//    };
//    
//    Date.prototype.fmtDateMDY = function(seperator){
//        var tag = (seperator == null) ? "" : seperator;
//        return (this.getMonth() + 1).leadingZero(2) + tag + this.getDate().leadingZero(2) +  tag + this.getFullYear();
//    };
//	
//	  String.prototype.leadingZero = function(length){
//		  var loop = (length == null) ? 0 : length - this.valueOf().length;
//		  
//		  var value = this.valueOf();
//		  for (i = 0; i < loop; i++) { value = "0" + value; }
//		  return value;
//	  };
	
	jQuery.fn.leadingZero = function(text, length){
		var loop = (length == null) ? 0 : length - text.length;
		  
		var value = text;
		for (i = 0; i < loop; i++) { value = "0" + value; }
		return value;
	};
	
	jQuery.fn.fmtMMDDYYYY = function(date, seperator) {
		var tag = (seperator == null) ? "/" : seperator;
		
		var month = $().leadingZero((date.getMonth()+1)+"", 2);
		var day = $().leadingZero(date.getDate(), 2);
		var fullyear = $().leadingZero(date.getFullYear(), 2);
		
		return month + tag + day + tag + fullyear;
	};
	
	jQuery.fn.parseMMDDYYYY = function(strdate, seperator) {
		if (strdate == null || strdate.length != 10) return "";
		  
		var arr = strdate.split((seperator == null) ? "/" : seperator);
		var date = new Date();
		date.setFullYear(Number(arr[2]), Number(arr[0]) - 1, Number(arr[1]));
		return date;
	};
	
	// Bila butuh readonly bukanya disabled
	//	vTag.datepicker("destroy");
	//	vTag.datepicker();	
	//	vTag.addClass("disabled");
	//	vTag.attr("readOnly", "true");
	
	jQuery.fn.disabled = function() {
		var vTag = $(this);
		var attr = vTag.data("attribute");

		if (attr.type.indexOf("datepicker") >= 0) {
			vTag.attr("disabled", "true");
		}else if (attr.type.indexOf("radio") >= 0 || attr.type.indexOf("checkbox") >= 0) {
			$("input:radio", vTag).add($("input:checkbox", vTag)).button({disabled:true});
		}else if (attr.type.indexOf("combobox") >= 0) {
			vTag.attr("disabled", "true");
		}else if (attr.type.indexOf("textfield") >= 0 || attr.type.indexOf("textarea") >= 0 || attr.type.indexOf("autocomplete") >= 0) {
			vTag.attr("disabled", "true");
		}else if (attr.type.indexOf("button") >= 0) {
			vTag.button({disabled:true});
		}
	};
	
	jQuery.fn.enabled = function() {
		var vTag = $(this);
		var attr = vTag.data("attribute");

		if (attr.type.indexOf("datepicker") >= 0) {
			vTag.removeAttr("disabled");
		}else if (attr.type.indexOf("radio") >= 0 || attr.type.indexOf("checkbox") >= 0) {
			$("input:radio", vTag).add($("input:checkbox", vTag)).button({disabled:false});
		}else if (attr.type.indexOf("combobox") >= 0) {
			vTag.removeAttr("disabled");
		}else if (attr.type.indexOf("textfield") >= 0 || attr.type.indexOf("textarea") >= 0 || attr.type.indexOf("autocomplete") >= 0) {
			vTag.removeAttr("disabled"); 
		}else if (attr.type.indexOf("button") >= 0) {
			vTag.button({disabled:false});
		}
	};
	
	jQuery.fn.isEmpty = function(value) {
		return (value == null || "" == value);
	}; 
})(jQuery);
