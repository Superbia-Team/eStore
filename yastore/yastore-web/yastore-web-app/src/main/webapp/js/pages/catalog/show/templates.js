(function() {
    
    define(function(require) {
        return {
            catalogView : require("text!templates/catalog/catalogView.htm"),
            rightNavigation :  require("text!templates/catalog/rightNavigation.htm"),
            fragments :  require("text!templates/catalog/fragments.htm")
        };
    });

}).call(this);
