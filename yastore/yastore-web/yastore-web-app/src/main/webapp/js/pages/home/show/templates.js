(function() {
    
    define(function(require) {
        return {
            homeView : require("text!templates/home/index.htm"),
            aboutView : require("text!templates/home/about.htm")
        };
    });

}).call(this);
