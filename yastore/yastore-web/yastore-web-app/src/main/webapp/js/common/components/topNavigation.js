define([
    "marionette",
    "msgbus",
    "common/entities/catalogModels"], function(Marionette, msgBus, CatalogAPI) {
    
    var BreadcrumbView = Marionette.ItemView.extend({
        tagName: 'ol',
        className: 'breadcrumb',
        template: '#top-navigation-breadcrumb-template',
        
        events: {      
            "click li > a": "navigate"
        },

        initialize: function() {
            var _this = this;
            msgBus.events.on("app:onNavigate", function(navigationPoint) {
                console.log("app:navigate event (breadcrumb): " + navigationPoint.name);
                _this.render();
            });
        },
        
        navigate: function(e) {
            console.log("Navigate to the breadcrumb");
            
            var lnk = $(e.currentTarget);
            var navigationPoint = {
                    "viewType" : "breadcrumb",
                    "link" : lnk.attr("href")
            };
            var category = lnk.attr("data-category");
            if (category) navigationPoint["categoryCode"] = category; 
            
            msgBus.events.trigger("app:onNavigate", navigationPoint);
        }
    });
    
    var SearchView = Marionette.ItemView.extend({
        el: $("#global-search"),
        events: {      
            "keypress #main-search": "search"
        },
        searchTerms: "",
        search: function(e) {
            // if enter pressed
            if ( e.which === 13 ) { 
                var searchTerms = $("#main-search").val();
                if(searchTerms === this.searchTerms) return false;

                this.searchTerms = searchTerms;
                msgBus.events.trigger("app:search", searchTerms);
                return false;
            }
        }
    });    
  
    var TopNavigationItemView = Marionette.ItemView.extend({
        tagName: "li",
        template: '#top-navigation-item-template'
    });
    var TopNavigationView = Marionette.CompositeView.extend({
        template: '#top-navigation-template',
        itemView: TopNavigationItemView,
        itemViewContainer: "ul",
        
        initialize: function() {
            var _this = this;
            
            this.collection = new CatalogAPI.Categories([], { parentCategory : "ROOT" });
            msgBus.reqres.setHandler("app:topCategories", function() {
                return _this.collection;
            });
            this.collection.fetch({
                success: function() {
                    if (_this.selectedCategory) {
                        var category = _this.collection.categoryByCode(_this.selectedCategory);
                        if (category) {
                            category.set({"selected" : true});
                        }
                        _this.render();
                    }
                }
            });
            
            msgBus.events.on("app:onNavigate", function(navigationPoint) {
                console.log("app:navigate event (top): " + navigationPoint.name);
                if (navigationPoint.viewType == "category") {
                    _this.selectedCategory = navigationPoint.categoryCode;
                    
                    _(_this.collection.models).each(function(category) {
                        category.set({"selected" : false});            
                    });
                    
                    var category = _this.collection.categoryByCode(navigationPoint.categoryCode);
                    if (category) {
                        category.set({"selected" : true});
                    }                   
                }
                
                _this.render();
            });
            
            this.categoryFilters = [];
            msgBus.reqres.setHandler("app:categoryFilters", function(categoryCode) {
                var filter = _this.categoryFilters[categoryCode];
                if (!filter) {
                    filter = new CatalogAPI.SearchContext();
                    _this.categoryFilters[categoryCode] = filter;
                }
                return filter;
            });
        }/*,
        
        onRender: function() {
            console.log("render to navigation. Selected: " + this.selectedCategory);
            
            $(this.el).find(".top-navigation-item").removeClass("selected");
            if (this.selectedCategory) {
                var _this = this;
                setTimeout(function(){
                    var item = $(_this.el).find(".top-navigation-item[data-code='" + _this.selectedCategory + "']"); 
                    item.addClass("selected");
                    console.log("found to be selected: " + item.length);
                }, 10);
            }
        }*/
    });
    
    return {
        TopBar : TopNavigationView,
        Breadcrumb : BreadcrumbView,
        Search : SearchView
    }
});
