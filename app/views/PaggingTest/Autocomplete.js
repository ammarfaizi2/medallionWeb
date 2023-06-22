function Autocomplete(tag, jaction, cb) {
	if (this instanceof Autocomplete) {
		tag.autocomplete();
		
		var cache = {};
		tag.autocomplete({
			minLength: 2,
			source: function(request, response) {
				var term = request.term;
				if (term in cache) {
					response( cache[ term ] );
					return;
				}
				
				$.getJSON(jaction.sAjaxSource, request, function(data, status, xhr) {
					cache[term] = data;
					response(data);
				});
			}
		 });
		if (cb) {
			tag.data('action', jaction);
			if (cb[jaction.id]) cb[jaction.id](tag); 
		}
	}else{
		return new Autocomplete(tag, jaction, cb);
	}
};