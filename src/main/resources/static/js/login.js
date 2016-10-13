const LoginFormModule = (function (ajaxUtils, loginModule) {
    function clearSigninForm() {
        document.getElementById('signin-username').value = "";
        document.getElementById('signin-password').value = "";
    }

    function createRequest() {
        const username = document.getElementById('signin-username').value;
        const password = document.getElementById('signin-password').value;

        return {
            username: username, password: password
        };
    }

    function acceptToken(response) {
        loginModule.login(response.username, response.token);
    }

    function notifyRegistration() {
        alert('Ok');
    }

    function performLogin() {
        const request = createRequest();
        clearSigninForm();
        ajaxUtils.postJson('/rest/login', request, acceptToken);
    }

    function performRegistration() {
        const request = createRequest();
        clearSigninForm();
        ajaxUtils.postJson('/rest/account', request, notifyRegistration, null);
    }

    return {
        performLogin: performLogin,
        performRegistration: performRegistration
    }
})(AjaxUtils, LoginModule);

window.addEventListener('load', function () {
    document.getElementById('signin-signin').addEventListener('click', LoginFormModule.performLogin);
    document.getElementById('signin-register').addEventListener('click', LoginFormModule.performRegistration);
    document.getElementById('signout-logout').addEventListener('click', LoginModule.logout);
    LoginModule.showWhenOut('form-signin');
    LoginModule.showWhenIn('form-signout');
    LoginModule.showUsername('form-signin-username');
    LoginModule.refresh();
});
