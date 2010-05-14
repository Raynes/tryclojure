$(document).ready(
    function() {
	$("#console").console({
				  promptLabel: 'Clojure> ',
				  commandValidate:function(line){
				      if (line == "") return false;
				      else return true;
				  },
				  commandHandle:function(line){
				      var data;
				      jQuery.ajax({
						      url: "magics" + "?code=" + encodeURIComponent(line),
						      success: function(result) {data = result;},
						      async:   false
						  }); 
				      return [{msg: data,
					       className:"jquery-console-message-value"}];
				  },
				  welcomeMessage:'Enter some Clojure code, and it will be evaluated.',
				  autofocus:true,
				  animateScroll:true,
				  promptHistory:true
			      });
    });
