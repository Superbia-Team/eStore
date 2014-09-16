(function() {
    define([
      'jquery',
      'backbone',
      'msgbus'
    ], function($, Backbone, msgBus) {
        
        var DataChannel = Backbone.Model.extend({
            
            events: {
                summary: "handleSummary"
            },
            
            initialize:function () {
                this.reconnect();
                
                var self = this;
                $(window).unload(function(){ self.closeConnection(); });
            },
            
            reconnect: function() {
                if (this.isConnected() || this.url().indexOf("fake:") >= 0) return;
                console.log("Re-connect web socket data channel: " + this.url());
                
                var self = this;
                this.ws = this.createChannel();
                $(this.ws)
                    .bind('close', function(){ self.closeConnection(true); })
                    .bind('message', function(e) {
                        var m = $.parseJSON(e.originalEvent.data);
                        var h = self.events[m.type];
                        if (h && self[h]) self[h].call(this, m);
                    });
                
                this.refreshStatus();
            },
            
            url : function() {
                var config = msgBus.reqres.request("app:config");
                return config.get("api").wschannel;
            },
            
            createChannel: function() {
                return WebSocket ? new WebSocket( this.url() ) : {
                    send: function(m){ return false },
                    close: function(){},
                    readyState: WebSocket.CLOSED
                };
            },
            
            closeConnection: function(reconnect) {
                $(this.ws)
                    .removeData()
                    .unbind();
            
                if (this.isConnected()) {
                    try { this.ws.close(); } catch(e){;}
                }
                this.ws = null;
                
                if (reconnect) {
                    var self = this;
                    setTimeout(function(){
                        self.reconnect();
                    }, 5000);
                }
            },
            
            refreshStatus: function() {
                return;
    
                var self = this;
                setTimeout(function() {
                    if (!self.isConnected()) {
                        self.closeConnection(true);
                    } else {
                        self.refreshStatus();
                    }
                }, 5000);
            },
            
            isConnected: function() {
                return this.ws && (
                        this.ws.readyState == WebSocket.OPEN
                        || this.ws.readyState == WebSocket.CONNECTING);
            },
            
            handleSummary: function(event) {
                setTimeout(function(){
                    msgBus.events.trigger("data:new-summary-pkg", event);
                }, 10);
            }
        });
        
        return DataChannel;
    });
}).call(this);