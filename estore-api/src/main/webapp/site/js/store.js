requirejs.config({
	baseUrl: "js/lib",
	paths : {
		jquery : 'http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.0/jquery.min',
		underscore : 'http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.4/underscore-min',
		backbone : 'http://cdnjs.cloudflare.com/ajax/libs/backbone.js/1.0.0/backbone-min',
		bootstrap : 'http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.1/js/bootstrap.min',
		store : '../store',
		utils : 'utils'
	},
	shim : {
		'backbone' : {
			// These script dependencies should be loaded before loading backbone.js
			deps : [ 'underscore', 'jquery' ],
			// Once loaded, use the global 'Backbone' as the module value.
			exports : 'Backbone'
		},
		'underscore' : {
			exports : '_'
		},
	}
});

// Load the main app module to start the app
requirejs(["store/main"]);
