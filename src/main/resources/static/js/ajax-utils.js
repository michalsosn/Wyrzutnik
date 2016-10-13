var AjaxUtils = (function () {
    var headers = {};

    function applyHeaders(xmlHttp) {
        for (var attr in headers) {
            if (headers.hasOwnProperty(attr)) {
                xmlHttp.setRequestHeader(attr, headers[attr]);
            }
        }
    }

    function setHeaders(newHeaders) {
        for (var attr in newHeaders) {
            if (newHeaders.hasOwnProperty(attr)) {
                if (newHeaders[attr] === undefined) {
                    delete headers[attr];
                } else {
                    headers[attr] = newHeaders[attr];
                }
            }
        }
    }

    function clearHeaders() {
        headers = {};
    }

    function encodeParameters(params) {
        var result = [];
        for (var key in params) {
            if (params.hasOwnProperty(key)) {
                result.push(encodeURIComponent(key) + "=" + encodeURIComponent(params[key]));
            }
        }
        return result.join("&");
    }

    function makeHandler(xmlHttp, callback, handleJson) {
        return function() {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200 && callback) {
                if (handleJson === undefined || handleJson) {
                    var json = JSON.parse(xmlHttp.responseText);
                    callback(json);
                } else {
                    callback(xmlHttp.responseText);
                }
            }
        };
    }

    function getAt(url, params, callback, handleJson) {
        if (params) {
            url = url + '?' + encodeParameters(params);
        }
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = makeHandler(xmlHttp, callback, handleJson);
        xmlHttp.open('GET', url, true);
        applyHeaders(xmlHttp);
        xmlHttp.send(null);
    }

    function deleteAt(url, callback, handleJson) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = makeHandler(xmlHttp, callback, handleJson);
        xmlHttp.open('DELETE', url, true);
        applyHeaders(xmlHttp);
        xmlHttp.send(null);
    }

    function postJson(url, request, callback, handleJson) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = makeHandler(xmlHttp, callback, handleJson);
        xmlHttp.open('POST', url, true);
        applyHeaders(xmlHttp);
        xmlHttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
        var data = JSON.stringify(request);
        xmlHttp.send(data);
    }

    function putJson(url, request, callback, handleJson) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = makeHandler(xmlHttp, callback, handleJson);
        xmlHttp.open('PUT', url, true);
        applyHeaders(xmlHttp);
        xmlHttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
        var data = JSON.stringify(request);
        xmlHttp.send(data);
    }

    return {
        getAt: getAt,
        deleteAt: deleteAt,
        postJson: postJson,
        putJson: putJson,
        setHeaders: setHeaders,
        clearHeaders: clearHeaders
    }
})();

