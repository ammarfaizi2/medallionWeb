function Radio(divTag, tag, jaction, cb) {
	if (this instanceof Radio) {
		divTag.buttonset();
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag);
		}
	}else{
		return new Radio(divTag, tag, jaction, cb);
	}
};