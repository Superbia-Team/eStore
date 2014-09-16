(function() {

    define([
            'pages/catalog/show/templates',
            'common/_viewsBase',
            'msgbus',
            'marionette',
            "common/entities/catalogModels",
            "common/utils",
            "lib/bootstrap/bootstrap-multiselect",
            "lib/jquery-ui/jquery-ui.min"], function(Templates, AppViews, msgBus, Marionette, CatalogAPI, Utils) {
        
        var PaginatorView = Marionette.ItemView.extend({
            template: "#category-search-paginator-template",
            
            events: {
                "click .paginator-page": "gotoPage"
            },
            
            initialize: function() {
                this.updateModel();
                this.collection.on("sync", this.onSearch, this);
            },
            
            onClose: function() {
                this.collection.off("sync", this.onSearch, this);
            },
            
            onSearch: function() {
                this.updateModel();
                this.render();
            },
            
            updateModel: function() {
                var startIndex = this.collection.state.currentPage * this.collection.state.pageSize;
                var endIndex = Math.min(startIndex + this.collection.state.pageSize, this.collection.state.totalRecords);
                
                var pages = [];
                for(var i = 0; i < this.collection.state.totalPages; i++) pages.push(i+1);
                if (pages.length == 0) pages.push(1);
                
                this.model = new Backbone.Model({
                    "pageFrom": startIndex,
                    "pageTo": endIndex,
                    "currentPage": this.collection.state.currentPage,
                    "pages": pages,
                    "total": this.collection.state.totalRecords
                });
            },
            
            gotoPage: function(e) {
                var page = parseInt($(e.currentTarget).text());
                this.collection.getPage(page - 1);
            }
        });
        
        var RightNavigationView = Marionette.ItemView.extend({
            template: _.template(Templates.rightNavigation),
            tagName: "ol",
            className: "tree",
            templateHelpers: function() {
                var _this = this;
                return {
                    recursive: function(category){
                        return _this.template(category);
                    }
                }
            },
            
            events: {
                "click .tree-item-label" : "selectCategory"
            },
            
            onRender: function() {
                if (this.options.selected) {
                    $(this.el).find("label[data-code='" + this.options.selected + "']").addClass("active");
                }
            },
            
            selectCategory: function(e) {
                var label = $(e.currentTarget);
                var select = !label.hasClass("active");
                this.options.selected = select ? label.attr("data-code") : "";
                
                var _this = this;
                setTimeout(function() {
                    $(_this.el).find(".tree-item-label").removeClass("active");
                    if (select) label.addClass("active");
                    
                    // msgBus.events.trigger("app:onNavigate", {"name" : label.attr("data-name")});
                }, 10);
                               
                if (this.options.onSelect) {
                    this.options.onSelect(this.options.selected);
                }
            }
        });
        
        var ThumbnailsItemView = Marionette.ItemView.extend({
            tagName : "div",
            className: "thumbnail-container col-6 col-sm-6 col-lg-4",            
            template: "#thunmbnails-list-item-template",
            
            onRender: function() {
                var _this = this;
                var timer = null;
                var item = null;
                $(_this.el)
                    .click(function() {
                        if (item !== this) {
                            if (timer) {
                                clearTimeout(timer);
                                timer = null;
                            };
                            item = this;
                            var popup = $("#productDetailsPopup");
                            popup.hide();
                            popup.appendTo($(item));
                            popup.show({
                                effect: "fade",
                                duration: 300,
                                easing: "easeInCirc" // easeInBack,easeInCirc, easeInExpo
                            });
                        }
                    })
                    .mouseover(function(){
                        if (item !== this) {
                            if (timer) {
                                clearTimeout(timer);
                                timer = null;
                            }
                            item = this;
                            timer = setTimeout(function(){
                                var popup = $("#productDetailsPopup");
                                popup.hide();
                                popup.appendTo($(item));
                                popup.show({
                                    effect: "fade",
                                    duration: 300,
                                    easing: "easeInCirc" // easeInBack,easeInCirc, easeInExpo
                                });
                            }, 500);
                        }
                        return false;
                    })
                    .mouseleave(function(){
                        clearTimeout(timer);
                        timer = null;
                        item = null;
                        $("#productDetailsPopup").hide();
                        return false;
                    });
            },
            
            onClose : function() {
                $(this.el).unbind( "click" );
                $(this.el).unbind( "mouseover" );
                $(this.el).unbind( "mouseleave" );
                $("#productDetailsPopup").appendTo("body").hide();
            }
            
        });

        var ThumbnailsListView = Marionette.CollectionView.extend({
            itemView : ThumbnailsItemView,
            className: "row",
        });
        
        
        var DetailsItemView = Marionette.ItemView.extend({
            tagName : "div",
            className: "thumbnail-container col-6 col-sm-6 col-lg-4",            
            template: "#details-list-item-template",
            
            initialize: function() {
                
            },
            
            onRender: function() {
                var _this = this;
                var timer = null;
                var item = null;
                $(_this.el)
                    .click(function() {
                        if (item !== this) {
                            if (timer) {
                                clearTimeout(timer);
                                timer = null;
                            };
                            item = this;
                            var popup = $("#productDetailsPopup");
                            popup.hide();
                            popup.appendTo($(item));
                            popup.show({
                                effect: "fade",
                                duration: 300,
                                easing: "easeInCirc" // easeInBack,easeInCirc, easeInExpo
                            });
                        }
                    })
                    .mouseover(function(){
                        if (item !== this) {
                            if (timer) {
                                clearTimeout(timer);
                                timer = null;
                            }
                            item = this;
                            timer = setTimeout(function(){
                                var popup = $("#productDetailsPopup");
                                popup.hide();
                                popup.appendTo($(item));
                                popup.show({
                                    effect: "fade",
                                    duration: 300,
                                    easing: "easeInCirc" // easeInBack,easeInCirc, easeInExpo
                                });
                            }, 500);
                        }
                        return false;
                    })
                    .mouseleave(function(){
                        clearTimeout(timer);
                        timer = null;
                        item = null;
                        $("#productDetailsPopup").hide();
                        return false;
                    });
            },
            
            onClose : function() {
                $(this.el).unbind( "click" );
                $(this.el).unbind( "mouseover" );
                $(this.el).unbind( "mouseleave" );
                $("#productDetailsPopup").appendTo("body").hide();
            }
            
        });

        var DetailsListView = Marionette.CollectionView.extend({
            itemView : DetailsItemView,
            className: "row",
        });

        var _CatalogBrowser = Marionette.Layout.extend({
            
            template: _.template(Templates.catalogView),
            className: "catalog-browser-container",
            
            regions: {
                catalogView: '#catalogView',
                rightNavigation: '#rightNavigation',
                paginator: '#category-search-paginator'
            },
            
            initialize: function (options) {

                if(!document.getElementById("category-search-paginator-template")) {
                    $("body").append(Templates.fragments);
                }
                
                this.searchFilters = new CatalogAPI.SearchContext();
                this.options = options;
                
                var _this = this;
                var topCategories = msgBus.reqres.request("app:topCategories");
                var category = null;
                if (topCategories.size() > 0) {
                    category = topCategories.categoryByCode(options.categoryCode);
                } else {
                    category = new CatalogAPI.Category();
                    this.topCategories = topCategories;
                    this.topCategories.on("sync", this.categoriesLoaded, this);
                }
                
                this.model = category;
                this.searchFilters = msgBus.reqres.request("app:categoryFilters", options.categoryCode);
                this.searchFilters.on("change", this.applyFilter, this);
                
                this.products = CatalogAPI.search("", options.categoryCode, this.searchFilters);
                this.products.getFirstPage();
                
                this.createViews(category);
                
                msgBus.events.on("app:search", function(text) {
                    _this.searchFilters.clear(true);
                    return _this.searchFilters.set({"terms": text});
                });
            },
            
            categoriesLoaded: function() {
                var cat = this.topCategories.categoryByCode(this.options.categoryCode);
                if (cat) {
                    this.model.set(cat.attributes);
                }
                if (this.rightNavigationView) this.rightNavigationView.render();
            },

            createViews: function (category) {
                var _this = this;
                this.rightNavigationView = new RightNavigationView({
                    model: category,
                    selected: this.searchFilters.get("cat"),
                    onSelect: function(categoryCode){
                        _this.searchFilters.set({"cat" : categoryCode});
                    }});
                this.views = {
                    thumbnails: new ThumbnailsListView({collection: this.products}),
                    details: new DetailsListView({collection: this.products})
                };
                
                this.paginatorView = new PaginatorView({collection: this.products});
            },
            
            onClose: function() {
                if (this.searchFilters) this.searchFilters.off("change", this.applyFilter, this);
                if (this.topCategories) this.topCategories.off("sync", this.categoriesLoaded, this);
            },

            showItemView: function (viewId) {
                this.catalogView.show(this.views[viewId]);
                
                for (var view in this.views) {
                    $("#" + view + "-selector").removeClass("active");
                }
                $("#" + viewId + "-selector").addClass("active");
                
                Utils.CookiesHelper.set("searchResultView", viewId, null);
            },
            
            onRender: function() {
                var _this = this;
                
                $(_this.el).find(".catalog-view-selector").click(function() {
                    var id = $(this).attr("id");
                    id = id.substr(0, id.indexOf("-selector"));
                    _this.showItemView(id);
                });

                var initMultiselect = function(selector, label, modelProp, currSelection) {
                    var obj = $(_this.el).find(selector); 
                    obj.multiselect({
                        nonSelectedText: label,
                        numberDisplayed: 5,
                        buttonClass: 'btn btn-default btn-xs',
                        onDropdownHide: function(event) {
                            if (_this.searchFilters) {
                                var selected = this.getSelected();
                                var values = [];
                                $(selected).each(function(){
                                    values.push($(this).val());
                                });
                                var val = {}; val[modelProp] = values;
                                _this.searchFilters.set(val);
                            }
                        }
                    });
                    
                    $(currSelection).each(function(){
                        obj.multiselect('select', this, false);
                    });
                };
                
                initMultiselect('#priceRangeFilter', 'Filter by price', 'p', this.searchFilters.get("p"));
                initMultiselect('#materialFilter', 'Filter by material', 'm', this.searchFilters.get("m"));
                initMultiselect('#colorFilter', 'Filter by color', 'c', this.searchFilters.get("c"));

                this.on("show", function() {
                    _this.rightNavigation.show(_this.rightNavigationView);
                    _this.paginator.show(_this.paginatorView);
                    
                    var view = Utils.CookiesHelper.get("searchResultView");
                    return _this.showItemView(view ? view : "thumbnails");
                });
            },
            
            applyFilter: function() {
                this.products.state.firstPage = 0;
                this.products.state.currentPage = 0;
                this.products.fetch();
            }
        });
        
        return {
            CatalogBrowser : _CatalogBrowser
        };
    });

}).call(this);
