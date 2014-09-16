(function() {

    define([
            "backbone",
            "marionette",
            "msgbus",
            "common/entities/dataChannel",
            "common/components/topNavigation",
            "bootstrap",
            "config/_base" ], function(Backbone, Marionette, msgBus, DataChannel, TopNavigation) {
        
        var app;
        app = new Marionette.Application();
        app.addRegions({
            content : "#content",
            breadcrumb : "#breadcrumb",
            navigationBar : "#top-navigation",
            searchBar : "#global-search",
            modal : Marionette.Region.Dialog.extend({
                el : "#modal"
            })
        });
        
        app.on("initialize:after", function() {
            if (!Backbone.history.started) {
                return Backbone.history.start();
            }
        });
        
        app.addInitializer(function() {
            msgBus.commands.execute("home:route");
            msgBus.commands.execute("catalog:route");
            
            // attach to global search input
            app.searchBar.attachView(new TopNavigation.Search());
            // show breadcrumb bar
            app.breadcrumb.show(new TopNavigation.Breadcrumb());
            // create top navigation bar and load top categories
            app.navigationBar.show(new TopNavigation.TopBar());
            
            app.commonUIInit();
            
            //return app.dataChannels = new DataChannel();
            return this;
        });
        
        msgBus.events.on("app:show:modal", function(view) {
            return app.modal.show(view);
        });
        msgBus.events.on("app:show", function(view) {
            return app.content.show(view);
        });
       
        msgBus.events.on("app:reloadSecured", function(enforce) {
            if (!app.redirectSSL() && enforce) {
                window.location.reload();
            }
        });
        
        app.redirectSSL = function() {
            var config = msgBus.reqres.request("app:config");
            var p = config.get("server").get("serverProtocolSsl");
            if (p != document.location.protocol.split(0)) {
                window.location = config.urlHelper.makeSecureUrl(p, config.get("server").get("serverPortSsl")); 
                return true;
            }
            
            return false;
        }
        app.login = function() {
            if (!app.redirectSSL()) {
                app.modal.show(new LoginView());
            }
            return true;
        }
        app.accessDenied = function() {
            alert("Access not allowed");
            return false;
        }
        
        $("#login-lnk").click(function(){
            app.login();
            return false;
        });
        $("#logout-lnk a").click(function() {
            var self = this;
            var config = msgBus.reqres.request("app:config");
            $.ajax({
                type: "POST", url: config.get("api").logout, data: {},
                success: function(data) {
                    window.location.replace(config.get("api").default); 
                },
                error: function(data) {
                    window.location.replace(config.get("api").default);
                },
                dataType: 'json',
                async: false
            });
            return false;
        });
        
        // Tell jQuery to watch for any 401 or 403 errors and handle them appropriately
        $.ajaxSetup({
            statusCode: {
                401: function() {
                    app.login();
                },
                403: function() {
                    app.accessDenied();
                }
            }
        });
        
        app.commonUIInit = function() {
            $(".jumpTopLink").click(function() {
                $("html, body").animate({ scrollTop: 0 }, "slow");
                return false;
            });
        }
        
        return app;
    });

}).call(this);
