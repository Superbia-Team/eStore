define([
  'underscore',
  'backbone'
], function(_, Backbone){
	
	window.UserProfile = Backbone.Model.extend({

	    urlRoot:"../api/userProfile",
	    
	    defaults: {
			fn: "",
			ln: "",
			mn: "",
			e: ""
		},

	    initialize:function () {
	        // do nothing
	    }
	});
});