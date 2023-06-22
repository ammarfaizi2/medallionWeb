function Datepicker(tag, jaction, cb) {
	if (this instanceof Datepicker) {
		tag.datepicker();
	}else{
		return new Datepicker(tag, jaction, cb);
	}
};