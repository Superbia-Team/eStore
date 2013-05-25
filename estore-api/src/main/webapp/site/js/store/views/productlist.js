window.ProductView = Backbone.View.extend({

    tagName:"div", // Not required since 'div' is the default if no el or tagName specified

    initialize:function () {
    	this.template = templates['Product'];
    },

    render: function () {
        $(this.el).html(this.template(this.model.toJSON()));
        $('#details', this.el).html(new ProductSummaryView({model:this.model}).render().el);
        this.model.reports.fetch({
            success:function (data) {
                if (data.length == 0)
                    $('.no-reports').show();
            }
        });
        $('#reports', this.el).append(new ProductListView({model:this.model.reports}).render().el);
        return this;
    }
});

window.ProductListView = Backbone.View.extend({

    tagName:'ul',

    className:'nav nav-list',

    initialize:function () {
        var self = this;
        this.model.bind("reset", this.render, this);
        this.model.bind("add", function (product) {
            $(self.el).append(new ProductListItemView({model:product}).render().el);
        });
    },

    render:function () {
        $(this.el).empty();
        _.each(this.model.models, function (product) {
            $(this.el).append(new ProductListItemView({model:product}).render().el);
        }, this);
        return this;
    }
});

window.ProductListItemView = Backbone.View.extend({

    tagName:"li",

    initialize:function () {
        this.model.bind("change", this.render, this);
        this.model.bind("destroy", this.close, this);
    },

    render:function () {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }

});