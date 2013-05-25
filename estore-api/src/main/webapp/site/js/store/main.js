define([
        "jquery",
        "underscore",
        "backbone",
        "bootstrap",
        "localstorage",
        "utils"
], function($, _, Backbone) {
	
	// Tell jQuery to watch for any 401 or 403 errors and handle them appropriately
	$.ajaxSetup({
		statusCode: {
			401: function(){
				// Redirec the to the login page.
				window.location.replace('#login');
			},
			403: function() {
				// 403 -- Access denied
				window.location.replace('#denied');
			}
		},
		beforeSend: function(xhr) {
			if (APP.currentSession && APP.currentSession.securityToken) {
				xhr.setRequestHeader("Authorization", "Basic " + APP.currentSession.securityToken);
			}
		}		
	});	
	
	$.ajaxSetup({
	});
	
	window.Router = Backbone.Router.extend({

		routes: {
			"" : "home",
			"admin" : "admin",
			"login" : "login"
		},

		initialize: function () {
			this.headerView = new HeaderView();
			APP.currentSession = new APP.Session();
		},

		home: function () {
			// Since the home view never changes, we instantiate it and render it only once
			if (!this.homeView) {
				this.homeView = new HomeView();
				this.homeView.render();
			} else {
				this.homeView.delegateEvents(); // delegate events when the view is recycled
			}
			$("#content").html($(this.homeView.el).html());
			this.headerView.select('home-menu');
		},
		
		admin: function () {
			// Since the home view never changes, we instantiate it and render it only once
			if (!this.adminView) {
				this.adminView = new AdminView();
			} else {
				this.adminView.delegateEvents(); // delegate events when the view is recycled
			}
			this.adminView.render();
			this.adminView.select('admin-menu');
		},

		login: function() {
			APP.currentSession.login();
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
