(function() {

    define([
            "backbone",
            "pages/catalog/show/controller",
            "msgbus",
            "marionette"], function(Backbone, Controller, msgBus, Marionette) {
        
        'use strict';
        
        var CatalogRouter = Marionette.AppRouter.extend({
            
            controller :  new Controller(),
            
            appRoutes : {
                "catalog": "navigateDefaultCategory",
                "catalog?:category": "navigateCategory"
            }
        });
        
        return new CatalogRouter();
    });

}).call(this);