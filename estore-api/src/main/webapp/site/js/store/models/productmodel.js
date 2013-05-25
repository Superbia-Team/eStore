window.Product = Backbone.Model.extend({

    urlRoot:"../api/products",

    initialize:function () {
        this.reports = new ProductCollection();
        this.reports.url = '../api/products/' + this.id + '/reports';
    }

});

window.ProductCollection = Backbone.Collection.extend({

    model: Product,

    url:"../api/products",

    findByName:function (key) {
        var url = '../api/products?tmp=' + new Date().getTime();// (key == '') ? '../api/products' : "../api/products/" + key;
        console.log('findByName: ' + key);
        var self = this;
        $.ajax({
            url:url,
            dataType:"json",
            success:function (data) {
                console.log("search success: " + data.length);
                self.reset(data);
            }
        });
    }

});
