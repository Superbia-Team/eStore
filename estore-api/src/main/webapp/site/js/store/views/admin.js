window.AdminView = Backbone.View.extend({

    initialize: function () {
        this.searchResults = new ProductCollection();
        this.searchresultsView = new ProductListView({model: this.searchResults, className: 'dropdown-menu'});
    },

    render: function () {
    	this.searchResults.findByName("*");
        $(this.el).html(this.template());        
        $('.navbar-search').append(this.searchresultsView.render().el);
        return this;
    },
    
    select: function(menuItem) {
        $('.nav li').removeClass('active');
        $('.' + menuItem).addClass('active');
    }

});