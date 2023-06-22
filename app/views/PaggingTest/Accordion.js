function Accordion(tagdiv, cb) {
	if (this instanceof Accordion) {
		Form(tagdiv, cb);
		
		tagdiv.accordion({
			 collapsible: true
		});
	}else{
		return new Accordion(tagdiv, cb);
	}
};