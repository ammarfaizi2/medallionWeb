function Callback() {
	if (this instanceof Callback) {
		var name;
		var age;
		var job;
		var lastname;
		var comment;
		var agama;
		var code;
		var codedesc;
		var cprofilelist;
		var cprofiledlg;
		var sex;
		var active;
		var music;
		
		this.cprofiledlgClick = function(tag, event, row) {
			code.val(row[0]); 
			codedesc.val(row[1]);
		};
		this.cprofiledlg = function(tag) { cprofiledlg = tag; };
		this.cprofilelist = function(tag) { cprofilelist = tag; };
		this.code = function(tag) { code = tag; };
		this.sex = function(tag) { sex = tag; };
		this.active = function(tag) { active = tag; };
		this.codeDesc = function(tag) { codedesc = tag; };
		this.name = function(tag) { name = tag; };
		this.nameKeyup = function(tag, event) { console.log('keyup name'); };
		this.nameKeydown = function(tag, event) { console.log('keydown name'); };
		this.agama = function(tag) { agama = tag; };
		this.age = function(tag) { age = tag; };
		this.job = function(tag){ job = tag; };
		this.lastname = function(tag) { lastname = tag; };
		this.comment = function(tag) { comment = tag; };
		this.music = function(tag){ music = tag; };
		this.cancel = function(tag){

		};
		this.entry = function(tag){
//			alert('haloo');
		};
		this.entryClick = function(tag){
			$("#DialogTest1").dialog('open');
		};
		this.save = function(tag){
			tag.click(function(){
				var populate = Populate();
				populate.input(name);
				populate.input(age);
				populate.input(job);				
				populate.input(lastname);
				populate.input(comment);
				populate.input(code);
				populate.input(codedesc);
				populate.radio(sex);
				populate.radio(active);
				populate.checkbox(music);
				populate.select(agama);
				var obj = populate.getAll();
				var action = tag.data('action');
				
				$.ajaxSetup({ async : true });
				$.post(action.sAjaxSource, {'test':obj}, function(data) {
		    		checkRedirect(data);
					alert(data);
				});
			});
		};
	}else{
		return new Callback();
	}
};