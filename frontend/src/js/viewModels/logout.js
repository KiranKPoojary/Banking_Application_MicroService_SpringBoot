define([
  "knockout",
  "ojs/ojcontext",
  "ojs/ojrouter",
  "ojs/ojdialog",
], function (ko, Context, Router) {
  function LogoutViewModel() {
    var self = this;

    self.logout = function () {
      // Perform logout logic here
      document.getElementById("logoutDialog").open();
     

    //   Router.rootInstance.go("login");
    };


    self.confirmLogout = function () {
     console.log("Logging out...");
      window.app.isLoggedIn(false);
      window.app.userRole(null);
      localStorage.clear();
      console.log("Logged out successfully");

      document.getElementById("logoutDialog").close();

      document.getElementById("loggedOutDialog").open();
    };

    self.cancelLogout = function () {
        console.log("Logout canceled");
        // Router.rootInstance.go("dashboard");
    };

}

  return new LogoutViewModel();
});
