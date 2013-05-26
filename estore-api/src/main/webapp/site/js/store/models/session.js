/**
 * APP.currentSession.get("user");
 * or
 * APP.currentSession.set("user");
 *
 * */

if(typeof APP == "undefined"){ var APP = { }; }

define([
  'underscore',
  'backbone',
  'localstorage',
  'store/models/userProfile'
], function(_, Backbone) {
	
	// Tell jQuery to watch for any 401 or 403 errors and handle them appropriately
	$.ajaxSetup({
		statusCode: {
			401: function(){
				APP.currentSession.set({securityToken : null});
				// Redirec the to the login page.
				window.location.replace('#login');
			},
			403: function() {
				// 403 -- Access denied
				window.location.replace('#denied');
			}
		},
		beforeSend: function(xhr) {
			var token = APP.currentSession ? 
					APP.currentSession.get("securityToken") : null;
			if (token) {
				xhr.setRequestHeader("Authorization", "Basic " + token);
			}
		}		
	});	
	
	APP.Session = Backbone.Model.extend({
		defaults: {
			securityToken: null,
			userProfile: new UserProfile(),
			updated : false
		},
		state : false,
		options : {
			rememberMe : false
		},
		initialize : function(model, options) {
			_.bindAll(this);
			
			// default vars
			options = options || {};
			// parse options
			this.options = _.extend(this.options, options);
			
			if (this.options.rememberMe) {
				this.store = this.localStorage;
			} else {
				this.store = this.sessionStorage;
			}
			
			// try loading the session
			var localSession = this.store.get("session");
			if (!_.isNull(localSession)) {
				this.set(JSON.parse(localSession));
				// reset the updated flag
				this.set({
					updated : false
				});
				// sync with the server
				this.save();
			}
			
			// event binders
			this.bind("change", this.update);
			this.bind("error", this.error);
		},
		sync : function(method, model, options) {
			if ("read" == method) {
				this.get("userProfile").fetch();
			}
			return this.update();
		},
		update : function() {
			// set a trigger
			if (!this.state) {
				this.state = true;
				this.trigger("loaded");
			}
			
			// caching is triggered after every model update (fetch/set)
			if (this.get("updated")) {
				this.cache();
			}
		},
		cache : function() {
			console.log("CACHE!!!!", this.toJSON());
			// update the local session
			this.store.set("session", JSON.stringify(this.toJSON()));
		},
		logout : function() {
			this.set({
				securityToken : null,
				userProfile : new UserProfile(),
				updated : false
			});
		},
		login : function(username, password) {
			$('#content').html(new LoginView().render().el);
		},		
		error : function(model, req, options, error) {
			// consider redirecting based on statusCode
			console.log(req);
		},
		sessionStorage : {
			get : function(name) {
				return sessionStorage.getItem(name);
			},
			set : function(name, val) {
				// validation first?
				return sessionStorage.setItem(name, val);
			},
			check : function(name) {
				return (sessionStorage.getItem(name) == null);
			},
			clear : function(name) {
				// actually just removing the session...
				return sessionStorage.removeItem(name);
			}
		},
		localStorage : {
			get : function(name) {
				return localStorage.getItem(name);
			},
			set : function(name, val) {
				return localStorage.setItem(name, val);
			},
			check : function(name) {
				return (localStorage.getItem(name) == null);
			},
			clear : function(name) {
				// actually just removing the session...
				return localStorage.removeItem(name);
			}
		}		
	});

	window.LoginView = Backbone.View.extend({

	    initialize:function () {
	        console.log('Initializing Login View');
	    },

	    events: {
	        "click #loginButton": "login"
	    },

	    render:function () {
	        $(this.el).html(this.template());
	        return this;
	    },

	    login:function (event) {
	        event.preventDefault(); // Don't let this button submit the form
	        $('.alert-error').hide(); // Hide any errors on a new submit
	        var url = '../api/login';
	        console.log('Loggin in... ');
	        
	        var credentials = {
	            username: $('#inputEmail').val(),
	            password: $('#inputPassword').val()
	        };
	        
	        $.ajax({
	            url:url,
	            type:'POST',
	            data: credentials,
	            success:function (data) {
	                console.log(["Login request details: ", data]);
	               
	                if(data.error) {  // If there is an error, show the error messages
	                    $('.alert-error').text(data.error.text).show();
	                }
	                else {
	        	        APP.currentSession.set({
	        				securityToken : base64.encode(credentials.username + ":" + credentials.password)
	        			});
	        	        APP.currentSession.save();
	        	        APP.currentSession.fetch();
	        	        
	        	        // we should store URL and then recover it!!!
	                    Backbone.history.navigate("/#");
	                }
	            }
	        });
	    }
	});
});
