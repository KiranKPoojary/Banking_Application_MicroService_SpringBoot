define([
   "knockout",
   "ojs/ojcontext",
   "ojs/ojrouter",
  "ojs/ojbootstrap",
  "ojs/ojformlayout",
  "ojs/ojinputtext",
  "ojs/ojbutton"
], function(ko, Context, Router) {
    function LoginViewModel() {
        var self = this;

        self.username = ko.observable('');
        self.password = ko.observable('');
        self.errorMessage = ko.observable('');

        self.login = function() {
            self.errorMessage('');
            if (self.username() && self.password()) {
                fetch('http://localhost:8085/user-service/api/v0/auth/login/user', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: self.username(),
                        password: self.password()
                    })
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Invalid credentials');
                    }
                    return response.json();
                })
                .then(data => {
                    // Handle successful login (e.g., save token, redirect)

                    if(data.token) {
                        console.log('Login successful, token:', data.token);
                        localStorage.setItem('jwtToken', data.token);
                    }
                    Router.rootInstance.go('incidents');
                })
                .catch(error => {
                    self.errorMessage('Login failed: ' + error.message);
                });
            } else {
                self.errorMessage('Please enter username and password.');
            }
        };
    }

    return LoginViewModel;
});