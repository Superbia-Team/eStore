(function() {

    define([
            "backbone",
            "marionette",
            "msgbus",
            "bootstrap",
            "config/_base" ], function(Backbone, Marionette, msgBus) {

        // ============================================================================
        // Base 64 encoding
        // ============================================================================
        var base64 = {};
        (function () {
          var END_OF_INPUT = -1,
              base64Chars = new Array(
                'A','B','C','D','E','F','G','H',
                'I','J','K','L','M','N','O','P',
                'Q','R','S','T','U','V','W','X',
                'Y','Z','a','b','c','d','e','f',
                'g','h','i','j','k','l','m','n',
                'o','p','q','r','s','t','u','v',
                'w','x','y','z','0','1','2','3',
                '4','5','6','7','8','9','+','/'),
              reverseBase64Chars = new Array(),
              base64Str,
              base64Count;
              
          for (var i=0; i < base64Chars.length; i++){
              reverseBase64Chars[base64Chars[i]] = i;
          }
        
          function setBase64Str(str){
              base64Str = str;
              base64Count = 0;
          }
          function readBase64(){    
              if (!base64Str) return END_OF_INPUT;
              if (base64Count >= base64Str.length) return END_OF_INPUT;
              var c = base64Str.charCodeAt(base64Count) & 0xff;
              base64Count++;
              return c;
          }
          function readReverseBase64(){   
              if (!base64Str) return END_OF_INPUT;
              while (true){      
                  if (base64Count >= base64Str.length) return END_OF_INPUT;
                  var nextCharacter = base64Str.charAt(base64Count);
                  base64Count++;
                  if (reverseBase64Chars[nextCharacter]){
                      return reverseBase64Chars[nextCharacter];
                  }
                  if (nextCharacter == 'A') return 0;
              }
              return END_OF_INPUT;
          }
        
          function ntos(n){
              n=n.toString(16);
              if (n.length == 1) n="0"+n;
              n="%"+n;
              return unescape(n);
          }
        
          base64.encode = function(str){
              setBase64Str(str);
              var result = '';
              var inBuffer = new Array(3);
              var lineCount = 0;
              var done = false;
              while (!done && (inBuffer[0] = readBase64()) != END_OF_INPUT){
                  inBuffer[1] = readBase64();
                  inBuffer[2] = readBase64();
                  result += (base64Chars[ inBuffer[0] >> 2 ]);
                  if (inBuffer[1] != END_OF_INPUT){
                      result += (base64Chars [(( inBuffer[0] << 4 ) & 0x30) | (inBuffer[1] >> 4) ]);
                      if (inBuffer[2] != END_OF_INPUT){
                          result += (base64Chars [((inBuffer[1] << 2) & 0x3c) | (inBuffer[2] >> 6) ]);
                          result += (base64Chars [inBuffer[2] & 0x3F]);
                      } else {
                          result += (base64Chars [((inBuffer[1] << 2) & 0x3c)]);
                          result += ('=');
                          done = true;
                      }
                  } else {
                      result += (base64Chars [(( inBuffer[0] << 4 ) & 0x30)]);
                      result += ('=');
                      result += ('=');
                      done = true;
                  }
                  lineCount += 4;
                  if (lineCount >= 76){
                      result += ('\n');
                      lineCount = 0;
                  }
              }
              return result;
          }
          
          base64.decode = function(str){
              setBase64Str(str);
              var result = "";
              var inBuffer = new Array(4);
              var done = false;
              while (!done && (inBuffer[0] = readReverseBase64()) != END_OF_INPUT
                  && (inBuffer[1] = readReverseBase64()) != END_OF_INPUT){
                  inBuffer[2] = readReverseBase64();
                  inBuffer[3] = readReverseBase64();
                  result += ntos((((inBuffer[0] << 2) & 0xff)| inBuffer[1] >> 4));
                  if (inBuffer[2] != END_OF_INPUT){
                      result +=  ntos((((inBuffer[1] << 4) & 0xff)| inBuffer[2] >> 2));
                      if (inBuffer[3] != END_OF_INPUT){
                          result +=  ntos((((inBuffer[2] << 6)  & 0xff) | inBuffer[3]));
                      } else {
                          done = true;
                      }
                  } else {
                      done = true;
                  }
              }
              return result;
          }
        })()
        
        // ============================================================================
        // URL utils
        // ============================================================================
        function UriHelper() {
            this._regExp = /^((\w+):\/\/\/?)?((\w+):?(\w+)?@)?([^\/\?:]+):?(\d+)?(\/?[^\?#;\|]+)?([;\|])?([^\?#]+)?\??([^#]+)?#?(\w*)/;
        
            this._getVal = function(r, i) {
                if(!r) return null;
                return (typeof(r[i]) == 'undefined' ? "" : r[i]);
            };
            
            this.queryParams = function() {
                return this.parseQueryString(window.location.search.slice(1));
            }
            
            this.parseQueryString = function(queryString){
                var params = {};
                if(queryString) {
                    _.each(
                        _.map(decodeURI(queryString).split(/&/g),function(el,i){
                            var aux = el.split('='), o = {};
                            if(aux.length >= 1){
                                var val = undefined;
                                if(aux.length == 2) val = aux[1];
                                o[aux[0]] = val;
                            }
                            return o;
                        }),
                        function(o){
                            _.extend(params,o);
                        }
                    );
                }
                return params;
            };
        
            this.parse = function(uri) {
                var r = this._regExp.exec(uri);
                return results = {
                    url: this._getVal(r,0),
                    protocol: this._getVal(r,2),
                    username: this._getVal(r,4),
                    password: this._getVal(r,5),
                    host: this._getVal(r,6),
                    port: this._getVal(r,7),
                    pathname: this._getVal(r,8),
                    urlparamseparator: this._getVal(r,9),
                    urlparam: this._getVal(r,10),
                    querystring: this._getVal(r,11),
                    params: this.parseQueryString(this._getVal(r,11)),
                    fragment: this._getVal(r,12)
                };
            };
            
            this.makeSecureUrl = function(protocol, port) {
                var r = this.parse(window.location.href);
                var url = protocol + "//" + r.host +
                    (port && port !== "80" && port !== "443" ? (":" + port) : "") +
                    r.pathname + 
                    (r.fragment && r.fragment.length > 0 ? ("#" + r.fragment) : "") +
                    (r.querystring && r.querystring.length > 0 ? ("?" + r.querystring) : "");
                return url;
            };
        }
        
        function CookiesHelper() {
            this.get = function(cookieName) {
                if (document.cookie.length > 0) {
                    begin = document.cookie.indexOf(cookieName + "=");
                    if (begin != -1) {
                        begin += cookieName.length + 1;
                        end = document.cookie.indexOf(";", begin);
                        if (end == -1)
                            end = document.cookie.length;
                        return unescape(document.cookie.substring(begin, end));
                    }
                }
                return null;
            };
            
            this.set = function(cookieName, cookieValue, expireDays, sPath, sDomain, bSecure) {
                var expireDate = new Date ();
                expireDate.setTime(expireDate.getTime() + (expireDays * 24 * 3600 * 1000));
        
                document.cookie = cookieName + "=" + escape(cookieValue) +
                    ((expireDays == null) ? "" : "; expires=" + expireDate.toGMTString()) + (sDomain ? "; domain=" + sDomain : "") + (sPath ? "; path=" + sPath : "") + (bSecure ? "; secure" : "");
            };
            
            this.remove = function(cookieName){
                if (get(cookieName)) {
                    document.cookie = cookieName + "=" + "; expires=Thu, 01-Jan-70 00:00:01 GMT";
                }
            };
        }
        
        var dateFormat = function () {
            var token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
                timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
                timezoneClip = /[^-+\dA-Z]/g,
                pad = function (val, len) {
                    val = String(val);
                    len = len || 2;
                    while (val.length < len) val = "0" + val;
                    return val;
                };
        
            // Regexes and supporting functions are cached through closure
            return function (date, mask, utc) {
                var dF = dateFormat;
        
                // You can't provide utc if you skip other args (use the "UTC:" mask prefix)
                if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
                    mask = date;
                    date = undefined;
                }
        
                // Passing date through Date applies Date.parse, if necessary
                date = date ? new Date(date) : new Date;
                if (isNaN(date)) throw SyntaxError("invalid date");
        
                mask = String(dF.masks[mask] || mask || dF.masks["default"]);
        
                // Allow setting the utc argument via the mask
                if (mask.slice(0, 4) == "UTC:") {
                    mask = mask.slice(4);
                    utc = true;
                }
        
                var _ = utc ? "getUTC" : "get",
                    d = date[_ + "Date"](),
                    D = date[_ + "Day"](),
                    m = date[_ + "Month"](),
                    y = date[_ + "FullYear"](),
                    H = date[_ + "Hours"](),
                    M = date[_ + "Minutes"](),
                    s = date[_ + "Seconds"](),
                    L = date[_ + "Milliseconds"](),
                    o = utc ? 0 : date.getTimezoneOffset(),
                    flags = {
                        d:    d,
                        dd:   pad(d),
                        ddd:  dF.i18n.dayNames[D],
                        dddd: dF.i18n.dayNames[D + 7],
                        m:    m + 1,
                        mm:   pad(m + 1),
                        mmm:  dF.i18n.monthNames[m],
                        mmmm: dF.i18n.monthNames[m + 12],
                        yy:   String(y).slice(2),
                        yyyy: y,
                        h:    H % 12 || 12,
                        hh:   pad(H % 12 || 12),
                        H:    H,
                        HH:   pad(H),
                        M:    M,
                        MM:   pad(M),
                        s:    s,
                        ss:   pad(s),
                        l:    pad(L, 3),
                        L:    pad(L > 99 ? Math.round(L / 10) : L),
                        t:    H < 12 ? "a"  : "p",
                        tt:   H < 12 ? "am" : "pm",
                        T:    H < 12 ? "A"  : "P",
                        TT:   H < 12 ? "AM" : "PM",
                        Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
                        o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
                        S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
                    };
        
                return mask.replace(token, function ($0) {
                    return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
                });
            };
        }();
        
        // Some common format strings
        dateFormat.masks = {
            "default":      "ddd mmm dd yyyy HH:MM:ss",
            shortDate:      "m/d/yy",
            mediumDate:     "mmm d, yyyy",
            longDate:       "mmmm d, yyyy",
            fullDate:       "dddd, mmmm d, yyyy",
            shortTime:      "h:MM TT",
            mediumTime:     "h:MM:ss TT",
            longTime:       "h:MM:ss TT Z",
            isoDate:        "yyyy-mm-dd",
            isoTime:        "HH:MM:ss",
            isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
            isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
        };
        
        // Internationalization strings
        dateFormat.i18n = {
            dayNames: [
                "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
            ],
            monthNames: [
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
            ]
        };
        
        // For convenience...
        Date.prototype.format = function (mask, utc) {
            return dateFormat(this, mask, utc);
        };
        Date.prototype.adjustTime = function (hours, minutes, seconds, meridian) {
            var h = parseInt(hours);
            if (h < 12 && "PM" == meridian) h += 12;
            if ("AM" == meridian && h == 12) h = 0;
            this.setHours(h);
            this.setMinutes(parseInt(minutes));
            this.setSeconds(parseInt(seconds));
            this.setMilliseconds(0);
            return this;
        };
        
        /** Default format: h:MM:ss TT  **/
        Date.prototype.parseDefault = function (timeString) {
            var d1 = timeString.split(" ");
            if (d1.length == 2) {
                var d2 = d1[0].split(":");
                if (d2.length == 3) {
                    var h = parseInt(d2[0]);
                    if (h < 12 && "PM" == meridian) h += 12;
                    if ("AM" == meridian && h == 12) h = 0;
                    this.setHours(h);
                    this.setMinutes(parseInt(d2[1]));
                    this.setSeconds(parseInt(d2[2]));
                    this.setMilliseconds(0);
                }
            }
            
            return this;
        };
        
        return Utils = {
                Base64 : base64,
                UriHelper : new UriHelper(),
                CookiesHelper : new CookiesHelper(),
                
                isObjectEmpty : function(ob){
                    for(var i in ob){ return false;}
                    return true;
                },
                
                isTrue : function(param) {
                    if (param && (param == true || param == "true"))
                        return true;
                
                    return false;
                },
                
                textMetrics : function(el) {
                    var h = 0, w = 0;
                
                    var div = document.createElement('div');
                    document.body.appendChild(div);
                    $(div).css({
                        position: 'absolute',
                        left: -1000,
                        top: -1000,
                        display: 'none'
                    });
                
                    $(div).html($(el).text());
                    var styles = ['font-size', 'font-style', 'font-weight', 'font-family', 'line-height', 'text-transform', 'letter-spacing'];
                    $(styles).each(function() {
                        var s = this.toString();
                        $(div).css(s, $(el).css(s));
                    });
                
                    h = $(div).outerHeight();
                    w = $(div).outerWidth();
                
                    $(div).remove();
                
                    return {
                        height: h,
                        width: w
                    };
                },
                
                jsonToQuery: function(a, clean) {
                    var s = [];

                    // If an array was passed in, assume that it is an array
                    // of form elements
                    if ( a.constructor == Array || a.jquery ) {
                        // Serialize the form elements
                        jQuery.each( a, function(){
                            if (!clean || $.trim(this.value)) s.push( encodeURIComponent(this.name) + "=" + encodeURIComponent( this.value ) );
                        });
                    }

                    // Otherwise, assume that it's an object of key/value pairs
                    else
                        // Serialize the key/values
                        for ( var j in a )
                            // If the value is an array then the key names need to be repeated
                            if ( a[j] && a[j].constructor == Array )
                                jQuery.each( a[j], function(){
                                    if (!clean || $.trim(this)) s.push( encodeURIComponent(j) + "=" + encodeURIComponent( this ) );
                                });
                            else
                                if (!clean || $.trim(a[j])) s.push( encodeURIComponent(j) + "=" + encodeURIComponent( a[j] ) );

                    // Return the resulting serialization
                    return s.join("&");
                }
        };
    });

}).call(this);
