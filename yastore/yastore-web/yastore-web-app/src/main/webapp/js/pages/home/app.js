(function() {

    define([
            "backbone",
            "pages/home/show/controller",
            "msgbus" ], function(Backbone, Controller, msgBus) {
        
        'use strict';
        
        var HomeRouter = Backbone.Marionette.AppRouter.extend({
            
            controller :  new Controller(),
            
            appRoutes : {
                "" : "home",
                "home" : "home",
                "about" : "about"
            }
        });
        
        return new HomeRouter();
    });

}).call(this);
