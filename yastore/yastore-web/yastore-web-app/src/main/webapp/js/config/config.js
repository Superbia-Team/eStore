(function() {
	
    define([
        "backbone", 
        "marionette", 
        "msgbus",
        "common/utils",
        "common/entities/system"
        ], function(Backbone, Marionette, msgBus, Utils, SystemModels) {
    	
    	var urlInfo = Utils.UriHelper.parse(window.location.href);
    	var websocketsUrl = (urlInfo.protocol == 'http' ? 'ws' : 'wss') + '://' + urlInfo.host + ((80 === urlInfo.port || '' == urlInfo.port) 
                ? '' : (':' + urlInfo.port)) + '/channel'
    	
    	var AppConfig = Backbone.Model.extend({
	        defaults: {
	            testMode: false,
	            optimizeLoad: true,
	            jobsPoolSize: 10,
	        	api: {
	        		'default': '/index.html',
	        		'wschannel': websocketsUrl, // for test use: 'fake://nowhere'
	        		'login': '../api/security/login',
	        		'logout': '../api/security/logout',
	        		'securityContext': '../api/security/context',
	        		"serverConfig" : '../api/config',
	        		'searchProducts': '../api/search/products',
	        		'searchCategories': '../api/search/categories',
	        		'homeCategories': '../api/search/homeCategories'
	        	},
	        	server: new SystemModels.ServerConfig()
	        },
	        
	        init: function() {
	            // I don't like PHP or ruby style with square breakets for url query string formatting
	            // So, disable it!
	            jQuery.ajaxSettings.traditional = true;
	            
	            var _this = this;
	            msgBus.reqres.setHandler("app:config", function() {
	                return _this;
	            });
	        },
	        
	        load: function(callback) {
	        	this.init();

	            this.get("server").fetch({
	            	success: function(){
	            		if(callback) callback();
	            	}
	            });
	        }
	    });
    	
        return new AppConfig();
    });

}).call(this);
