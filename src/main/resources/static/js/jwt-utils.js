const JwtUtils = (function (ajaxUtils) {
    const usernameKey = 'jwt.username';
    const tokenKey = 'jwt.token';

    function isTokenSet() {
        return getToken() !== null;
    }

    function getUsername() {
        return localStorage.getItem(usernameKey);
    }

    function getToken() {
        return localStorage.getItem(tokenKey);
    }

    function setHeader(token) {
        ajaxUtils.setHeaders({
            Authentication: token
        });
    }

    function storeToken(username, token) {
        localStorage.setItem(usernameKey, username);
        localStorage.setItem(tokenKey, token);
        setHeader('Bearer ' + token);
    }

    function clearToken() {
        localStorage.removeItem(usernameKey);
        localStorage.removeItem(tokenKey);
        setHeader(undefined);
    }

    function initToken() {
        const token = getToken();
        if (token !== null) {
            setHeader('Bearer ' + token);
        }
    }
    initToken();

    return {
        isTokenSet: isTokenSet,
        getUsername: getUsername,
        getToken: getToken,
        storeToken: storeToken,
        clearToken: clearToken
    }
})(AjaxUtils);

const LoginModule = (function (jwtUtils) {
    var showIds = {};
    var usernameIds = {};

    function showWhenIn(id) {
        if (showIds[id] === undefined) {
            showIds[id] = true;
        }
    }

    function showWhenOut(id) {
        if (showIds[id] === undefined) {
            showIds[id] = false;
        }
    }

    function showUsername(id) {
        usernameIds[id] = true;
    }

    function refresh() {
        const isTokenSet = jwtUtils.isTokenSet();
        for (var id in showIds) {
            if (showIds.hasOwnProperty(id)) {
                if ((isTokenSet && showIds[id]) || (!isTokenSet && !showIds[id])) {
                    document.getElementById(id).classList.remove('hidden');
                } else {
                    document.getElementById(id).classList.add('hidden');
                }
            }
        }

        const username = jwtUtils.getUsername();
        for (var id in usernameIds) {
            if (usernameIds.hasOwnProperty(id)) {
                document.getElementById(id).innerText = username;
            }
        }
    }

    function login(username, token) {
        jwtUtils.storeToken(username, token);
        refresh();
    }

    function logout() {
        jwtUtils.clearToken();
        refresh();
    }

    return {
        showWhenIn: showWhenIn,
        showWhenOut: showWhenOut,
        showUsername: showUsername,
        refresh: refresh,
        login: login,
        logout: logout
    }
})(JwtUtils);
