define(['backbone','msgbus'], function(Backbone, msgBus) {
    
    var ServerConfig = Backbone.Model.extend({
        defaults : {
            "serverProtocol" : "http:",
            "serverProtocolSsl" : "https:",
            "serverPortSsl" : "8443",
            "serverPort" : "8080",
            "serverStartTime": "" + new Date().getTime()
        },
        
        url: function() {
            var config = msgBus.reqres.request("app:config");
            return config.get("api").serverConfig;
        }
    });
    
    var Credentials = Backbone.Model.extend({
        defaults : {
            "username" : "",
            "password" : ""
        }
    });
    
    var SecurityContext = Backbone.Model.extend({
        defaults : {
            "authenticated": false,
            "principal" : "",
            "userName" : "",
            "firstName" : "",
            "lastName" : "",
            "roles" : []
        },
        
        url: function() {
            var config = msgBus.reqres.request("app:config");
            return config.get("api").securityContext;
        },
        
        initialize: function() {
            this.on('change', this.updateSecurityContext, this);
        },
        
        updateSecurityContext: function() {
            if (isTrue(this.get("authenticated"))) {
                $("#login-lnk").hide();
                $("#logout-lnk a").text(this.get("userName"));
                $("#logout-lnk").show();
            }
        }
    });
    
    return SystemModels = {
            ServerConfig : ServerConfig,
            Credentials : Credentials,
            SecurityContext : SecurityContext
    };
});