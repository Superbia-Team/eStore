define([
        "jquery",
        "underscore",
        "backbone",
        "bootstrap",
        "localstorage",
        "utils"
], function($, _, Backbone) {
	
	window.Router = Backbone.Router.extend({

		routes: {
			"" 		: "home",
			"admin"	: "admin",
			"login"	: "login",
			"search/:query/:page": 'search'
		},

		initialize: function () {
			this.headerView = new HeaderView();
			APP.currentSession = new APP.Session();
			APP.urlHelper = new UriHelper();
		},

		home: function () {
			// Since the home view never changes, we instantiate it and render it only once
			if (!this.homeView) {
				this.homeView = new HomeView();
				this.homeView.render();
			} else {
				this.homeView.delegateEvents(); // delegate events when the view is recycled
			}
			this.select('home-menu', $(this.homeView.el).html());
			this.homeView.select();
		},
		
		admin: function () {
			// Since the home view never changes, we instantiate it and render it only once
			if (!this.adminView) {
				this.adminView = new AdminView();
				this.adminView.render();
			} else {
				this.adminView.delegateEvents(); // delegate events when the view is recycled
			}
			this.select('admin-menu', $(this.adminView.el).html());
			this.adminView.select();
		},

		login: function() {
			if (!APP.currentSession.redirectSSL()) {
				this.select(null, $(new LoginView().render().el));
			}
		},
		
		// Will match url: #search/something/p2
		search: function(query, page) {
			alert("Search for '" + query + "' page: " + page);
		},
		
		select: function(menuItem, content) {
			if (menuItem) {
		        $('.nav li').removeClass('active');
		        $('.' + menuItem).addClass('active');
			}
	        
	        $("#content").html(content);
	    }
	});
			
	require(["store/views/header",
	         "store/views/home",
	         "store/views/admin",
	         "store/views/productlist",
	         "store/models/session",
	         "store/models/productmodel"
	         ], function() {
		
		templateLoader.load(["HomeView", "LoginView", "AdminView", "ProductView", "ProductListItemView"], function () {
			app = new Router();
			Backbone.history.start();
		});
	});

	return { };
});
