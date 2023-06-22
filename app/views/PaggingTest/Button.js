function Button(tag, jaction, cb) {
	if (this instanceof Button) {
		tag.button();
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag);
			
			if (cb[jaction.id+"Click"]) { tag.bind('click', function(e){ cb[jaction.id+"Click"](tag, e); }); }
		}
	}else{
		return new Button(tag, jaction, cb);
	}
};