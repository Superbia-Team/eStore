window.HeaderView = Backbone.View.extend({

    initialize: function () {
		// Close the search dropdown on click anywhere in the UI
		/*$('body').click(function () {
			$('.dropdown').removeClass("open");
		});*/
    },

    render: function () {
        $(this.el).html(this.template());
        return this;
    },

    events: {
        "keyup .search-query": "search",
        "keypress .search-query": "onkeypress"
    },

    search: function () {
        var key = $('#searchText').val();
        console.log('search ' + key);
        this.searchResults.findByName(key);
        setTimeout(function () {
            $('.dropdown').addClass('open');
        });
    },

    onkeypress: function (event) {
        if (event.keyCode == 13) {
            event.preventDefault();
        }
    },

    select: function(menuItem) {
        $('.nav li').removeClass('active');
        $('.' + menuItem).addClass('active');
    }

});