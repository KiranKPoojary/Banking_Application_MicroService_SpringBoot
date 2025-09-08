define([
  "knockout",
  "ojs/ojcorerouter",
  "ojs/ojformlayout",
  "ojs/ojinputtext",
  "ojs/ojselectsingle",
  "ojs/ojdialog",
], function (ko, CoreRouter) {
  function SignupViewModel() {
    var self = this;

    self.username = ko.observable("");
    self.firstName = ko.observable("");
    self.lastName = ko.observable("");
    self.password = ko.observable("");
    self.email = ko.observable("");
    self.phoneNumber = ko.observable("");
    self.address = ko.observable("");
    // self.role = ko.observable("");
    self.errorMessage = ko.observable("");

    // const roleData = [
    //   { value: "CUSTOMER", label: "Customer" },
    //   { value: "EMPLOYEE", label: "Employee" },
    //   { value: "ADMIN", label: "Admin" },
    // ];

    // this.roleOptions = new ArrayDataProvider(roleData, {
    //   keyAttributes: "value",
    //   textFilterAttributes: ["label"],
    // });

    self.signup = function () {
      self.errorMessage("");
      const payload = {
        username: self.username(),
        firstName: self.firstName(),
        lastName: self.lastName(),
        password: self.password(),
        email: self.email(),
        phoneNumber: self.phoneNumber(),
        address: self.address(),
        role: "CUSTOMER", // Default role set to CUSTOMER
        status: "ACTIVE",
      };

      fetch("http://localhost:8085/user-service/api/v0/auth/register/user", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      })
        .then((response) => {
          if (response.ok) {
            console.log("Signup successful");
            document.getElementById("successDialog").open();
          } else {
            return response.json().then((data) => {
              self.errorMessage(data.message || "Signup failed");
            });
          }
        })
        .catch((error) => {
          self.errorMessage("Error: " + error.message);
        });
    };

    // Called when user clicks OK on the dialog
    self.goToLogin = function () {
      console.log("Navigating to login page");
      CoreRouter.rootInstance.go("login"); // Navigate to login route
    };

  }

  
  return SignupViewModel;
});

