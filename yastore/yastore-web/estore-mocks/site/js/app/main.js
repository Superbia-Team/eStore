// Generated by CoffeeScript 1.6.3
(function() {
  require.config({
    baseURL: 'js',
    paths: {
      jquery: '../lib/jquery/jquery-2.0.3.min',
      underscore: '../lib/underscore/underscore-min',
      backbone: '../lib/backbone/backbone',
      'backbone.wreqr': '../lib/backbone.wreqr/backbone.wreqr.min',
      'backbone.eventbinder': '../lib/backbone.eventbinder/backbone.eventbinder.min',
      'backbone.babysitter': '../lib/backbone.babysitter/backbone.babysitter.min',
      marionette: '../lib/marionette/backbone.marionette',
      bootstrap: '../lib/bootstrap/bootstrap',
      text: '../lib/requirejs/text',
      templates: '../../templates'
    },
    shim: {
      backbone: {
        deps: ['underscore', 'jquery'],
        exports: 'Backbone'
      },
      underscore: {
        exports: '_'
      },
      bootstrap: {
        deps: ['jquery'],
        exports: '$.fn.button'
      }
    }
  }, require(["config/_base", "app", "pages/book/app", "pages/other/app"], function(_config, App) {
    return App.start();
  }));

}).call(this);