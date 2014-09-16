(function() {

    define([ "backbone", "msgbus", "backbone.paginator", "common/utils" ], function(Backbone, msgBus, PageableCollection, Utils) {

        var SearchContextModel = Backbone.Model.extend({
            defaults : {
                "terms" : "",
                "cat" : "",     // selected category
                "m" : [],       // material
                "c" : [],       // colors
                "p" : []        // price
            },
            
            clear: function(silent) {
                if (silent)
                    this.set(this.defaults, {silent: true});
                else
                    this.set(this.defaults);
            }
        }); 
        
        var CategoryModel = Backbone.Model.extend({
            defaults : {
                "subcategory" : false,
                "code" : "",
                "name" : "",
                "selected": false,

                "subcategories" : []
            }
        });
        
        var CategoriesCollection = Backbone.Collection.extend({
            parentCategory : "",
            model: CategoryModel,

            initialize: function(models, options) {
                this.parentCategory = options.parentCategory;
            },
              
            url: function() {
                var config = msgBus.reqres.request("app:config");
                return config.get("api").searchCategories + "?parentCategory=" + this.parentCategory;
            },
            
            categoryByCode: function(categoryCode) {
                return this.find(function(model) { return model.get('code') == categoryCode; });
            }
        });

        var ProductModel = Backbone.Model.extend({
            defaults : {
                "sku" : "",
                "name" : ""
            }
        });        
        
        var Products = PageableCollection.extend({
            model: ProductModel,

            state: {
              firstPage: 0,
              currentPage: 0,
              totalRecords: 0,
              pageSize: 15,
              sortKey: "name",
              order: 1,
              
              terms: "",
              topCategory: "",     // top category code
              filters : new SearchContextModel()
            },

            queryParams: {
                totalPages : null,
                totalRecords : null,
                currentPage : "page",
                pageSize : "pageSize",
                sortKey : "sort",
                order: "dir",
                directions: {
                  "-1": "asc",
                  "1": "desc"
                }
            },
            
            url: function() {
                var config = msgBus.reqres.request("app:config");
                var filtersQuery = this.state.filters ? Utils.jsonToQuery(this.state.filters.attributes, true) : "";
                
                var category = this.state.filters.get("cat");
                if (!category || category.length == 0) {
                    filtersQuery += (filtersQuery.length > 0 ? "&" : "") + "cat=" + this.state.topCategory;
                }
                
                return config.get("api").searchProducts + (filtersQuery.length > 0 ? "?" + filtersQuery : ""); 
            },
            
            parseRecords: function (resp) {
                return resp.products;
            },
            parseState: function (resp, xhr) {
                return {totalRecords: resp.totalCount};
            }
        });
        
        // Home categories. Need to load only once
        var HomeCategoriesCollection = Backbone.Collection.extend({
            model: CategoryModel,
            url: function() {
                var config = msgBus.reqres.request("app:config");
                return config.get("api").homeCategories;
            }
        });

        return CatalogAPI = {
                Category : CategoryModel,
                Product : ProductModel,
                SearchContext : SearchContextModel,
                HomeCategories : HomeCategoriesCollection,
                Categories : CategoriesCollection,
                
                findCategoryByCode : function(categoryCode) {
                    
                    var subcategories = new CategoriesCollection([], {
                        parentCategory : categoryCode
                    });
                    subcategories.fetch();
                    
                    return new CategoryModel({
                        "categoryCode" : categoryCode,
                        "subcategories" : subcategories
                    });
                },
                
                findHomeCategories: function() {
                    return homeCategories;
                },
                
                search: function(searchTerms, categoryCode, searchFilters) {
                    return new Products([], {
                        state : {
                            terms : searchTerms,
                            topCategory : categoryCode,
                            filters: searchFilters
                        }
                    });
                }
        };
    });

}).call(this);
