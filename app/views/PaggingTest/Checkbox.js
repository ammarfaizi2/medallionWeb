function Checkbox(divTag, tag, jaction, cb) {
	if (this instanceof Checkbox) {
		divTag.buttonset();
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag);
		}
	}else{
		return new Checkbox(divTag, tag, jaction, cb);
	}
};