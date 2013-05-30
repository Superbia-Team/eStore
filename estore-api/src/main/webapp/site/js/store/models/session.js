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
			401: function() {
				APP.currentSession.login();
			},
			403: function() {
				APP.currentSession.denied();
			}
		},
		beforeSend: function(xhr) {
			if (APP.currentSession) {
				var token = APP.currentSession.get("securityToken");
				if (token) {
					if (APP.currentSession.redirectSSL()) {
						xhr.abort();
					} else {
						xhr.setRequestHeader("Authorization", "Basic " + token);
					}
				}				
			}
		}		
	});	
	
	APP.Session = Backbone.Model.extend({
		defaults: {
			securityToken:	null,
			userProfile:	new UserProfile(),
			sslProtocol:	"https:",
			sslPort:		"8443"
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
			var refresh = false;
			var localSession = this.store.get("session");
			if (!_.isNull(localSession)) {
				// restore from storage all root properties
				var storedJSON = JSON.parse(localSession); 
				this.set(storedJSON);
				
				// restore child objects as soon as they were broken in previos step
				var userProfile = new UserProfile();
				if (!_.isNull(storedJSON["userProfile"])) {
					userProfile.set(storedJSON["userProfile"]);
					refresh = true;
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
			}).bind("parse", function() {
				debugger;
				self.set({refresh : false});
			});
			
	        // fetch all session related objects
	        if (refresh || this.get("refresh")) {
	        	//this.set({refresh:false});
	        	this.fetch();
	        }
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
		login : function() {
			this.set({securityToken : null, "location" : Backbone.history.fragment});
			window.location.replace('#login');
		},
		denied : function() { // 403 -- Access denied
			window.location.replace('#denied');
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
		},
		redirectSSL : function() {
			var s = APP.currentSession;
			var p = s.get("sslProtocol");
			if (p != document.location.protocol.split(0)) {
				window.location = APP.urlHelper.makeSecureUrl(p, s.get("sslPort")); 
				return true;
			}
			
			return false;
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
	        	        
	        	        APP.currentSession.set({refresh : true});
	        	        var location = APP.currentSession.get("location");
	        	        Backbone.history.loadUrl( location || "/#" );
	                }
	            }
	        });
	    }
	});
});
