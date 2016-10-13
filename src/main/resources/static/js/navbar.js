window.addEventListener('load', function () {
    document.getElementById('navbar-logout-button').addEventListener('click', LoginModule.logout);
    LoginModule.showWhenIn('navbar-logout');
    LoginModule.showWhenOut('navbar-login');
    LoginModule.showUsername('navbar-logout-username');
    LoginModule.refresh();
});
