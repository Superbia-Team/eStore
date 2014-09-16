

/**
 * Usage:
 * 
    require(["jquery", "jquery.image-swap"], function ($) {
        $(function () {
            var instance = ($("img").imageSwap());
            console.log(instance);
    
            instance.imageSwap("swap", 1000, "http://..../new_image.png");
        });
    });
 *
 */
(function() {
    
    define([ "jquery" ], function($) {
    
        // Create the defaults once
        var pluginName = "imageSwap", defaults = {
                opaqueClass : "opaque",
                selectedClass : "selected"
        };
    
        // The actual plugin constructor
        function ImageSwapPlugin(element, options) {
            this.element = element;
            // jQuery has an extend method which merges the contents of two or
            // more objects, storing the result in the first object. The first object
            // is generally empty as we don't want to alter the default options for
            // future instances of the plugin
            this.settings = $.extend({}, defaults, options);
            this._defaults = defaults;
            this._name = pluginName;
            this.init();
        }
    
        // Avoid Plugin.prototype conflicts
        $.extend(ImageSwapPlugin.prototype, {
            init : function() {
                // Place initialization logic here
                // You already have access to the DOM element and
                // the options via the instance, e.g. this.element
                // and this.settings
                // you can add more functions like the one below and
                // call them like so: this.yourOtherFunction(this.element, this.settings).
                console.log("xD");
            },
            yourOtherFunction : function() {
                // some logic
            }
        });
    
        // A really lightweight plugin wrapper around the constructor,
        // preventing against multiple instantiations
        $.fn[pluginName] = function(options) {
            this.each(function() {
                if (!$.data(this, "plugin_" + pluginName)) {
                    $.data(this, "plugin_" + pluginName, new ImageSwapPlugin(this, options));
                }
            });
    
            // chain jQuery functions
            return this;
        };
        
        return ImageSwapPlugin;
        
    });

}).call(this);
