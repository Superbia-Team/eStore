requirejs.config({
	baseUrl: "js/lib",
	paths : {
		jquery : 'jquery-1.10.1', // 'jquery-1.10.1.min', 
		underscore : 'underscore-min',
		backbone : 'backbone', // 'backbone-min',
		bootstrap : 'bootstrap', // 'bootstrap.min',
		localstorage : 'backbone.localStorage', // 'backbone.localStorage-min',
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
		'bootstrap': {
			deps: [ 'jquery' ],
			exports: "$.fn.button"
		}
	}
});

// Load the main app module to start the app
requirejs(["store/main"]);
