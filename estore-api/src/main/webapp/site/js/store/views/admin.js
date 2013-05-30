window.AdminView = Backbone.View.extend({

    initialize: function () {
        this.searchResults = new ProductCollection();
        this.searchresultsView = new ProductListView({model: this.searchResults, className: 'dropdown-menu'});
    },

    render: function () {
        $(this.el).html(this.template());        
        $('.navbar-search').append(this.searchresultsView.render().el);
        return this;
    },
    
    select: function() {
       	this.searchResults.findByName("*");
    }

});