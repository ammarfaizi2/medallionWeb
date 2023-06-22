function Dialog(tagdiv, cb) {
	if (this instanceof Dialog) {
		Form(tagdiv, cb);
		
		tagdiv.dialog({
			autoOpen:false,
			modal:true,
			heigth:'300px',
			width:'700px',
			resizable:false
		});
	}else{
		return new Dialog(tagdiv, cb);
	}
};