define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojtable",
  "ojs/ojdialog",
  "ojs/ojinputtext",
], function (ko, ArrayDataProvider) {
  function ManageUsersViewModel() {
    var self = this;

    // self.mappedUsers=ko.observable([]);

    self.userDataProvider = ko.observable(
      new ArrayDataProvider([], { keyAttributes: "id" })
    );

    // ✅ Load all users
    self.loadUsers = function () {
      let token = localStorage.getItem("jwtToken");

      fetch("http://localhost:8085/user-service/api/v0/users", {
        headers: {
          Authorization: "Bearer " + token,
        },
      })
        .then((res) => {
          if (!res.ok) {
            throw new Error("Failed to load users");
          }
          return res.json();
        })
        .then((data) => {
           const users = Array.isArray(data) ? data : [];

           // Map and add actionLabel
           const mappedUsers = users.map((u) => ({
             ...u,
             actionLabel: u.status === "ACTIVE" ? "Deactivate" : "Activate",
           }));


          self.userDataProvider(
            new ArrayDataProvider(mappedUsers, { keyAttributes: "id" })
          );

          console.log("Loaded users:", self.userDataProvider());
        })
        .catch((err) => {
          console.error("Error loading users:", err);
        });
    };

    // ✅ Activate user
    self.activateUser = function (user) {
      let token = localStorage.getItem("jwtToken");

      fetch(
        `http://localhost:8085/user-service/api/v0/users/${user.id}/active`,
        {
          method: "PATCH",
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      )
        .then((res) => {
          if (!res.ok) {
            throw new Error("Failed to activate user");
          }
          return res.json();
        })
        .then(() => {
          self.loadUsers();
          document.getElementById("userSuccessDialog").open();
        })
        .catch((err) => {
          console.error("Error activating user:", err);
        });
    };

    // ✅ Deactivate user
    self.deactivateUser = function (user) {
      let token = localStorage.getItem("jwtToken");

      fetch(
        `http://localhost:8085/user-service/api/v0/users/${user.id}/inactive`,
        {
          method: "PATCH",
          headers: {
            Authorization: "Bearer " + token,
          },
        }
      )
        .then((res) => {
          if (!res.ok) {
            throw new Error("Failed to deactivate user");
          }
          return res.json();
        })
        .then(() => {
          self.loadUsers();
          document.getElementById("userSuccessDialog").open();
        })
        .catch((err) => {
          console.error("Error deactivating user:", err);
        });
    };

    // ✅ Close success dialog
    self.closeUserSuccessDialog = function () {
      document.getElementById("userSuccessDialog").close();
    };

    // Load users on page init
    self.connected = function () {
      self.loadUsers();
    };

    // ✅ Toggle between activate/deactivate
    self.toggleUserStatus = function (event, context) {
      const user = context.item.data;

      if (user.status === "ACTIVE") {
        self.deactivateUser(user);
      } else {
        self.activateUser(user);
      }
    };

    // self.getUserActionLabel = function (event,context) {
    //   const user = context.item.data;
    //   user.status === "ACTIVE" ? "Deactivate" : "Activate";
    //   console.log(user.status);
    //   if(user.status === "ACTIVE"){
    //     self.isactive(true);
    //   }else{
    //     self.isactive(false);
    //   }
    // };
  }

  return ManageUsersViewModel;
});
