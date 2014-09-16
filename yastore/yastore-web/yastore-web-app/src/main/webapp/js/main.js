(function() {

    require.config({
        baseURL : 'js',
        paths : {
            jquery : 'lib/jquery/jquery-2.1.1.min',
            //underscore : 'lib/underscore/underscore-min',
            underscore : 'lib/underscore/underscore',
            backbone : 'lib/backbone/backbone',
            'backbone.wreqr' : 'lib/backbone.wreqr/backbone.wreqr.min',
            'backbone.eventbinder' : 'lib/backbone.eventbinder/backbone.eventbinder.min',
            'backbone.babysitter' : 'lib/backbone.babysitter/backbone.babysitter.min',
            'backbone.paginator' : 'lib/backbone.paginator/backbone.paginator.min',
            marionette : 'lib/marionette/backbone.marionette',
            bootstrap : 'lib/bootstrap/bootstrap',
            text : 'lib/requirejs/text',
            templates : '../templates'
        },

        shim : {
            backbone : {
                deps : [ 'underscore', 'jquery' ],
                exports : 'Backbone'
            },
            underscore : {
                exports : '_'
            },
            bootstrap : {
                deps : [ 'jquery' ],
                exports : '$.fn.button'
            }
        }
    }, require([
                "config/_base",
                "config/config",
                "app",
                "pages/home/app",
                "pages/catalog/app"
                ], function(_config, AppConfig, App) {

        return AppConfig.load(function() {
            App.start();
        });
    }));

}).call(this);
