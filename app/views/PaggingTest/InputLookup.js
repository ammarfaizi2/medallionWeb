function InputLookup(tag, tagDesc, jaction, cb) {
	if (this instanceof InputLookup) {
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag);
			if (cb[jaction.id+"Desc"]) cb[jaction.id+"Desc"](tagDesc);
			
			if (cb[jaction.id+"Keyup"]) {
				tag.bind('keyup', function(e){ cb[jaction.id+"Keyup"](tag, e); });
			}
			if (cb[jaction.id+"Keydown"]) {
				tag.bind('keydown', function(e){ cb[jaction.id+"Keydown"](tag, e); });
			}
			
			tag.keyup(function(e){
				 var code = (e.keyCode ? e.keyCode : e.which);
				 if(code == 13) {
					 $.ajaxSetup({ async : true });
					 $.get(jaction.sAjaxSource, {'code':tag.val()}, function(data) {
						checkRedirect(data);
						 tagDesc.val(data);
					 });
				 }
			});
		}
	}else{
		return new InputLookup(tag, tagDesc, jaction, cb);
	}
};