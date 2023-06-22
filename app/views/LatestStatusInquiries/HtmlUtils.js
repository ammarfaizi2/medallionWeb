(function(jQuery){
    Date.prototype.fmtYYYYMMDDHHMMSSSSS = function(current){
    	if (current) {
    		return this.getFullYear() +""+ (this.getMonth() + 1).leadingZero(2) +""+ this.getDate().leadingZero(2) +""+ this.getHours().leadingZero(2) +""+ this.getMinutes().leadingZero(2) +""+ this.getSeconds().leadingZero(2);	
    	}else{
    		return this.getFullYear() +""+ (this.getMonth() + 1).leadingZero(2) +""+ this.getDate().leadingZero(2) +""+ "000000";
    	}
        
    };

    Date.prototype.fmtDDMMYYYY = function(seperator){
        var tag = (seperator == null) ? "-" : seperator;
        return this.getDate().leadingZero(2) + tag + (this.getMonth() + 1).leadingZero(2) + tag + this.getFullYear();
    };
    
    /* IE FIX TRIM */

    String.prototype.trimIE = function() {
    	return this.replace(/^\s+|\s+$/g, ''); 
    };
      
    String.prototype.trim = function() {
    	return this.replace(/^\s+|\s+$/g, ''); 
    };
    
    //Number
    Number.prototype.leadingZero = function(length){
        var loop = (length == null) ? 0 : length - (this.valueOf() + "").length;
        
        var value = this.valueOf();
        for (var i = 0; i < loop; i++) {
            value = "0" + value;
        }
        return value;
    };
    
})(jQuery);    