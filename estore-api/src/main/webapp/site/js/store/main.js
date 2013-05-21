define([
        "jquery",
        "underscore",
        "backbone",
        "bootstrap",
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
		}
	});	
	
	window.Router = Backbone.Router.extend({

		routes: {
			"": "home",
			"login" : "login"
		},

		initialize: function () {
			this.headerView = new HeaderView();
			$('.header').html(this.headerView.render().el);

			// Close the search dropdown on click anywhere in the UI
			$('body').click(function () {
				$('.dropdown').removeClass("open");
			});
		},

		home: function () {
			// Since the home view never changes, we instantiate it and render it only once
			if (!this.homeView) {
				this.homeView = new HomeView();
				this.homeView.render();
			} else {
				this.homeView.delegateEvents(); // delegate events when the view is recycled
			}
			$("#content").html(this.homeView.el);
			this.headerView.select('home-menu');
		},

		login: function() {
			$('#content').html(new LoginView().render().el);
		}

	});
			
	require(["store/views/header",
	         "store/views/home",
	         "store/views/login"], function(){
		
		templateLoader.load(["HomeView", "HeaderView", "LoginView"], function () {
			app = new Router();
			Backbone.history.start();
		});
	});

	return { };
});
