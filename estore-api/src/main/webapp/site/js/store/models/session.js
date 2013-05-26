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
			var token = APP.currentSession ? APP.currentSession.get("securityToken") : null;
			if (token) {
				xhr.setRequestHeader("Authorization", "Basic " + token);
			}
		}		
	});	
	
	APP.Session = Backbone.Model.extend({
		defaults: {
			securityToken: null,
			userProfile: new UserProfile()
		},
		state : false,
		options : {
			rememberMe : true
		},
		initialize : function(model, options) {
			_.bindAll(this);
			
			options = options || {};
			this.options = _.extend(this.options, options);
			
			if (this.options.rememberMe) {
				this.store = this.localStorage;
			} else {
				this.store = this.sessionStorage;
			}
			
			// try loading the session
			var localSession = this.store.get("session");
			if (!_.isNull(localSession)) {
				// restore from storage all root properties
				var storedJSON = JSON.parse(localSession); 
				this.set(storedJSON);
				
				// restore child objects as soon as they were broken in previos step
				var userProfile = new UserProfile();
				if (!_.isNull(storedJSON["userProfile"])) {
					userProfile.set(storedJSON["userProfile"]);
				}
				this.set({"userProfile" : userProfile});
			}
			
			// event binders
			this.bind("change", this.cache);
			this.bind("error", this.error);
			
			// bind all child objects for changes
			var self = this;
			this.get("userProfile").bind("change", function(){
				self.save();
			});
		},
		sync : function(method, model, options) {
			var self = this;
			switch(method){
				case "read":
					this.get("userProfile").fetch();
					break;
				case "create":
				case "update":
					this.cache();
					break;				
			}
		},
		cache : function() {
			this.store.set("session", JSON.stringify(this.toJSON()));
		},
		logout : function() {
			this.set({ securityToken : null});
			this.get("userProfile").clear();
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
				return sessionStorage.setItem(name, val);
			},
			check : function(name) {
				return (sessionStorage.getItem(name) == null);
			},
			clear : function(name) {
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
	        	        
	        	        // fetch all session related objects
	        	        APP.currentSession.fetch();
	        	        
	        	        // we should store URL and then recover it!!!
	                    Backbone.history.navigate("/#");
	                }
	            }
	        });
	    }
	});
});
