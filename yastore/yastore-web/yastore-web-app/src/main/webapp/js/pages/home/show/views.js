(function() {

    define([
            'pages/home/show/templates',
            'common/_viewsBase',
            'msgbus',
            "marionette",
            "common/entities/catalogModels"], function(Templates, AppViews, msgBus, Marionette, CatalogAPI) {
        
        var _HomeView = Marionette.ItemView.extend({
            template: _.template(Templates.homeView),
            className: "home-root-categories",
            
            initialize: function() {
                var config = msgBus.reqres.request("app:config");
                var homeCategories = config.get("homeCategories");
                if (!homeCategories) {
                    homeCategories = new CatalogAPI.HomeCategories();
                    var _this = this;
                    homeCategories.fetch({
                        success: function(){
                            _this.loadHomeCategories();
                        }
                    });
                    config.set({"homeCategories" : homeCategories});
                }
                
                this.collection = homeCategories;
            },
            
            loadHomeCategories : function() {
                console.log("categories are loaded!");
                
                var container = $(this.el).find(".homeGalary-template");
                this.collection.each(function(model, index) {
                    var title = model.get("name");
                    var className = "a.slide" + (index + 1);
                    container.find(className)
                        .attr("title", title)
                        .attr("categoryCode", model.get("code"));
                    
                    container.find(className + " img")
                        .attr("src", model.get("image"))
                        .attr("alt", title)
                        .attr("title", title);
                });
                $(this.el).find(".homeGalary-main").removeClass("opaque");
                container.addClass("opaque");
            }
        });
        
        var _AboutView = Marionette.ItemView.extend({
            template: _.template(Templates.aboutView),
            className: "about-content",
            
            initialize: function() {
                
            }
        });
        
        return {
            HomeView : _HomeView,
            AboutView : _AboutView
        };
    });

}).call(this);
