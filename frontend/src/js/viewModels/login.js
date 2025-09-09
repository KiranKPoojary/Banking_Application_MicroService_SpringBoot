define([
   "knockout",
   "ojs/ojcontext",
   "ojs/ojcorerouter",
   "utils/authJWT",
  "ojs/ojbootstrap",
  "ojs/ojformlayout",
  "ojs/ojinputtext",
  "ojs/ojbutton"
], function(ko, Context, CoreRouter,authJWT) {
    function LoginViewModel() {
        var self = this;

        self.username = ko.observable('');
        self.password = ko.observable('');
        self.errorMessage = ko.observable('');
        self.router=CoreRouter.rootInstance;
      

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

                        const payload = authJWT.parseJwt(data.token);
                        console.log('JWT Payload:', payload);
                        if (payload && payload.role) {
                            localStorage.setItem("role", payload.role);
                            localStorage.setItem("username", payload.username);
                            localStorage.setItem("Id", payload.Id);
                            console.log("Logged in as:", payload.role);
                            console.log("Username:", payload.username);
                            console.log("User ID:", payload.Id);
                            if(window.app){
                                window.app.isLoggedIn(true);
                                window.app.userRole(payload.role.toLowerCase());
                                window.app.username(payload.username);
                                window.app.Id(payload.Id);
                                console.log("User role set to:", window.app.userRole());
                                console.log("Username set to:", window.app.username());
                                console.log("User ID set to:", window.app.Id());
                                console.log("Islogedin", window.app.isLoggedIn());
                            }

                            //go to dashboard based on role -User dashboard
                            self.router.go({ path: "user-dashboard" });
                        }
                    }
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