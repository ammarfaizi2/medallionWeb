function Select(tag, jaction, cb) {
	if (this instanceof Select) {
		tag.autocomplete();
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag); 
		}
	}else{
		return new Select(tag, jaction, cb);
	}
};