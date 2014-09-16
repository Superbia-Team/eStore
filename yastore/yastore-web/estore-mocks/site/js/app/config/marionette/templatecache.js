// Generated by CoffeeScript 1.6.3
(function() {
  define(["marionette"], function(Marionette) {
    return Marionette.TemplateCache.prototype.loadTemplate = function(templateId) {
      var err, msg, template;
      template = templateId;
      if (!template || template.length === 0) {
        msg = "Could not find template: '" + templateId + "'";
        err = new Error(msg);
        err.name = "NoTemplateError";
        throw err;
      }
      return template;
    };
  });

}).call(this);