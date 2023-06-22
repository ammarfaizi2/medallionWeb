function Lookup(tagdiv, tag, jaction, cb) {
	if (this instanceof Lookup) {
		
		Table(tag, jaction, cb);
		
		tagdiv.dialog({
			autoOpen:false,
			modal:true,
			heigth:'300px',
			width:'700px',
			resizable:false
		});
	}else{
		return new Lookup(tagdiv, tag, jaction, cb);
	}
};