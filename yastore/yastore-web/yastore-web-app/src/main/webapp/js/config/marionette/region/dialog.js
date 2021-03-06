// Generated by CoffeeScript 1.7.1
(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["backbone", "marionette"], function(Backbone, Marionette) {
    return Marionette.Region.Dialog = (function(_super) {
      __extends(Dialog, _super);

      function Dialog() {
        _.extend(this, Backbone.Events);
      }

      Dialog.prototype.onShow = function(view) {
        var options;
        this.setupBindings(view);
        options = this.getDefaultOptions(_.result(view, "dialog"));
        return this.$el.modal(options, {
          close: (function(_this) {
            return function(e, ui) {
              return _this.closeDialog();
            };
          })(this)
        });
      };

      Dialog.prototype.getDefaultOptions = function(options) {
        if (options == null) {
          options = {};
        }
        return _.defaults(options, {
          show: true,
          keyboard: true
        });
      };

      Dialog.prototype.setupBindings = function(view) {
        return this.listenTo(view, "dialog:close", this.closeDialog);
      };

      Dialog.prototype.closeDialog = function() {
        console.log("Marionette.Region.Dialog>> calling in the cleaner!");
        this.$el.modal("hide");
        this.stopListening();
        return this.close();
      };

      return Dialog;

    })(Marionette.Region);
  });

}).call(this);
