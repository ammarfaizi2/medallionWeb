function Populate() {
	if (this instanceof Populate) {
		
		function createObject(base, names, value) {
		    // If a value is given, remove the last name and keep it for later:
		    var lastName = arguments.length === 3 ? names.pop() : false;

		    // Walk the hierarchy, creating new objects where needed.
		    // If the lastName was removed, then the last object is not set yet:
		    for( var i = 0; i < names.length; i++ ) {
		        base = base[ names[i] ] = base[ names[i] ] || {};
		    }

		    // If a value was given, set it to the last name:
		    if( lastName ) base = base[ lastName ] = value;

		    // Return the last object in the hierarchy:
		    return base;
		}
		
		this.populate = function(elements) {
			var obj = new Object();
			for (x in elements) {
				var id = elements[x].attr("id");
				var attr = elements[x].data("attribute");
				var vTag = $("#"+id);
				var localid = id.split('_');
				var cleanlocalid = [];
				for (i = 3; i < localid.length; i++) {
					cleanlocalid[cleanlocalid.length] = localid[i];
				}
				
				if (attr.type.indexOf("textfield") >= 0) {
					createObject(obj, cleanlocalid, vTag.val());
				}
				if (attr.type.indexOf("textarea") >= 0) {
					createObject(obj, cleanlocalid, vTag.html());
				}
				if (attr.type.indexOf("combobox") >= 0) {
					createObject(obj, cleanlocalid, $("option:selected", vTag).val());
				}
				if (attr.type.indexOf("radio") >= 0) {
					createObject(obj, cleanlocalid, $(":radio:checked", vTag).val());
				}
				if (attr.type.indexOf("checkbox") >= 0) {
					var checkboxval = [];
					$(":checkbox:checked", vTag).each(function(i,e){
						checkboxval[checkboxval.length] = $(e).val();
					});
					createObject(obj, cleanlocalid, checkboxval);
				}
				if (attr.type.indexOf("datepicker") >= 0) {
					// Mapping tipe date di lihat lagi ya, kalo MM/dd/yyyy gax masalah tp kalo lain mungkin masalah
					createObject(obj, cleanlocalid, vTag.val());
				}
				if (attr.type.indexOf("autocomplete") >= 0) {
					createObject(obj, cleanlocalid, vTag.val());
				}
			}
			console.log('Populate '+obj);
			return obj;
		};
		
		function required(label, value) {
//			Di bypass krn bolah balik byk resouece lebih baik langusng di local saja			
//			var msg = "";
//			$.get('/validation/required', { 'data':value },  function(data) {
//				if (data == 0) { msg = label+" is required\n"; }
//			});
//			return msg;
			
			var msg = "";
			if ($().isEmpty(value)) {
				msg = label+" is required\n";
			}
			return msg;
		}
		
		function maxlength(label, value, length) {
//			Di bypass krn bolah balik byk resouece lebih baik langusng di local saja			
//			var msg = "";
//			$.get('/validation/maxLength', { 'data':value, 'length':length },  function(data) {
//				if (data == 0) { msg = "Maximum length for "+label+" is "+length+" character\n"; }
//			});
//			return msg;
			var msg = "";
			if (value.length > length) {
				msg = "Maximum length for "+label+" is "+length+" character\n";
			}
			return msg;
		}
		
		function minlength(label, value, length) {
//			Di bypass krn bolah balik byk resouece lebih baik langusng di local saja			
//			var msg = "";
//			$.get('/validation/minLength', { 'data':value, 'length':length },  function(data) {
//				if (data == 0) { msg = "Minimum length for "+label+" is "+length+" character\n"; }
//			});
//			return msg;
			var msg = "";
			if (value.length < length) {
				msg = "Minimum length for "+label+" is "+length+" character\n";
			}
			return msg;
		}
		
		function between(label, value, min, max) {
			var msg = "";
			if (Number(value) < min || Number(value) > max) {
				msg = label+" must be between "+min+" and "+max+"\n";
			}
			return msg;
		}		
		
		function max(label, value, length) {
			var msg = "";
			if (Number(value) > length) {
				msg = "Maximum value for "+label+" is "+length+"\n";
			}
			return msg;
		}
		
		function min(label, value, length) {
			var msg = "";
			if (Number(value) < length) {
				msg = "Minimum value for "+label+" is "+length+"\n";
			}
			return msg;
		}
		
		function profesi(label, value) {
			var msg = "";
			$.get('/validation/profesi', { 'data':value },  function(data) {
				checkRedirect(data);
				if (data == 0) { msg = label+" ['"+value+"'] is not registered\n"; }
			});
			return msg;
		}
		
		this.validate = function(elements) {
			var messages = [];
			for (x in elements) {
				var attr = elements[x].data("attribute");
				if (attr.validate) {
					var id = elements[x].attr("id");
					var label = $("label[for="+id+"]").html();
					var vTag = $("#"+id);
					var value = "";

					if (attr.type.indexOf("textfield") >= 0) { value = vTag.val(); }
					if (attr.type.indexOf("autocomplete") >= 0) { value = vTag.val(); }
					if (attr.type.indexOf("textarea") >= 0) { value = vTag.val(); }
					if (attr.type.indexOf("combobox") >= 0) { value = $("option:selected", vTag).val(); }
					if (attr.type.indexOf("radio") >= 0) { value = $(":radio:checked", vTag).val(); }
					if (attr.type.indexOf("checkbox") >= 0) { $(":checkbox:checked", vTag).each(function(i,e){ value += $(e).val(); }); }
					
					var array = attr.validate.split(" ");
					for (i = 0; i < array.length; i++) {
						var localmsg = "";
						var valarr = array[i].split("_");
						if (valarr[0] == 'required') { localmsg = required(label, value); }
						if (valarr[0] == 'profesi') { localmsg = profesi(label, value); }
						if (valarr[0] == 'minlength' || valarr[0] == 'minLength' ) { localmsg = minlength(label, value, valarr[1]); }
						if (valarr[0] == 'maxlength' || valarr[0] == 'maxLength' ) { localmsg = maxlength(label, value, valarr[1]); }
						if (valarr[0] == 'min') { localmsg = min(label, value, valarr[1]); }
						if (valarr[0] == 'max') { localmsg = max(label, value, valarr[1]); }
						if (valarr[0] == 'between') { localmsg = between(label, value, valarr[1], valarr[2]); }
						if (localmsg.length > 0) messages[messages.length] = localmsg;
					}
				}
			}
			return messages;
		};
	}else{
		return new Populate();
	}
};