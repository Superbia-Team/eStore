(function() {
    
    define([
            "pages/home/show/views",
            "msgbus",
            "marionette",
            'common/entities/catalogModels'], function(Views, msgBus, Marionette, Models) {
        
        var Controller = Marionette.Controller.extend({
            
            home : function() {
                return msgBus.events.trigger("app:show", new Views.HomeView());
            },
            
            about : function() {
                return msgBus.events.trigger("app:show", new Views.AboutView());
            }
        });
        
        return Controller;
    });

}).call(this);
