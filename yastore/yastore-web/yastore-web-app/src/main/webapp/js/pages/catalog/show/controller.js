(function() {
    
    define([
            "pages/catalog/show/views",
            "msgbus",
            "marionette"], function(Views, msgBus, Marionette) {
        
        var Controller = Marionette.Controller.extend({
            
            navigateCategory: function(category) {
                var view = new Views.CatalogBrowser({
                    "categoryCode": category
                });
                
                msgBus.events.trigger("app:show", view);
                return this.__notifyOnNavigate(category);
            },
            
            navigateDefaultCategory: function() {
                var rootCategories = msgBus.reqres.request("app:topCategories")
                var categoryCode = rootCategories.first().get("code");
                var view = new Views.CatalogBrowser({
                    "categoryCode": categoryCode
                });
                
                msgBus.events.trigger("app:show", view);
                return this.__notifyOnNavigate(categoryCode);
            },
            
            __notifyOnNavigate: function(categoryCode) {
                return msgBus.events.trigger("app:onNavigate", {
                    "viewType" : "category",
                    "categoryCode" : categoryCode,
                    "name" : this.__findCategoryName(categoryCode)
                });
            },
            
            __findCategoryName: function(categoryCode) {
                var rootCategories = msgBus.reqres.request("app:topCategories");
                var category = rootCategories.find(function(model) { return model.get('code') == categoryCode; });
                return category ? category.get("name") : ""; 
            }
        });
        
        return Controller;
    });

}).call(this);