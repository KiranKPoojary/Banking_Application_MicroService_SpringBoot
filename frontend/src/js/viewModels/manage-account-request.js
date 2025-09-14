
define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojtable",
  "ojs/ojbutton",
], function (ko, ArrayDataProvider) {
  function ManageAccountRequestsViewModel() {
    var self = this;

    self.userRole = (localStorage.getItem("role") || "").toLowerCase();
    self.accountRequests = ko.observableArray([]);

    let token = localStorage.getItem("jwtToken");

    self.accountRequestSelected = ko.observable(); //use for verify and confirm dialog box

    self.accountRequestsProvider = ko.observable(
      new ArrayDataProvider([], { keyAttributes: "id" })
    );

    self.userDataProvider = ko.observable(
      new ArrayDataProvider([], { keyAttributes: "id" })
    );

    self.loadUserswithId = function (userId) {
      fetch(`http://localhost:8085/user-service/api/v0/users/${userId}`, {
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
          const userArray = Array.isArray(data) ? data : [data];

          self.userDataProvider(
            new ArrayDataProvider(userArray, { keyAttributes: "id" })
          );

          console.log("Loaded users:", self.userDataProvider());
        })
        .catch((err) => {
          console.error("Error loading users:", err);
        });
    };

    //Dialog for user details
    // Open dialog with row data
    self.openCreateDialog = function (event, context) {
      console.log("Row data:", context.item.data);
      console.log("UserId:", context.item.data.userId);
      self.loadUserswithId(context.item.data.userId);

      self.accountRequestSelected(context.item.data);

      document.getElementById("createAccountDialog").open();
    };

    self.confirmCreateAccount = function () {
      console.log("Confirming account creation for:", self.accountRequestSelected());
      var accountRequestSelectedData = self.accountRequestSelected();
      self.createAccount(accountRequestSelectedData);
    };

    // Close dialog
    self.closeCreateDialog = function () {
      document.getElementById("createAccountDialog").close();
      //   self.selectedRequest(null);
    };

    // ✅ Load account requests from API
    self.loadAccountRequests = function () {
      fetch(
        "http://localhost:8085/account-service/api/v0/account-request/all",
        {
          headers: { Authorization: "Bearer " + token },
        }
      )
        .then((res) => res.json())
        .then((data) => {
          let filtered = [];

        const requests = Array.isArray(data) ? data : [data]; // 👈 ensure array

        if (self.userRole === "executive") {
          filtered = requests.filter((req) => req.status === "REQUESTED");
        } else if (self.userRole === "manager") {
          filtered = requests.filter((req) => req.status === "CREATED");
        }

        self.accountRequests(filtered);
        self.accountRequestsProvider(
          new ArrayDataProvider(filtered, { keyAttributes: "id" })
        );
        })
        .catch((err) => console.error("Error loading account requests:", err));
    };

    // ✅ Executive: create account
    self.createAccount = function (req) {
      fetch("http://localhost:8085/account-service/api/v0/accounts", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token,
        },
        body: JSON.stringify({
          userId: req.userId,
          accountType: req.accountType,
        }),
      })
        .then((res) => res.json())
        .then(() => self.loadAccountRequests())
        .catch((err) => console.error("Error creating account:", err));
    };

    // ✅ Manager: approve account
    self.approveAccountOpenDialog = function (event, context) {
      const accountdata = context.item.data; // ✅ row data
      self.accountRequestSelected(accountdata); // store in observable
      document.getElementById("confirmApproveDialog").open();
    };

    // Confirm and call backend
    self.confirmApproveAccount = function () {
      const accountdata = self.accountRequestSelected();
      if (!accountdata) return;

      fetch(
        `http://localhost:8085/account-service/api/v0/accounts/approve/${accountdata.accountId}`,
        {
          method: "POST",
          headers: { Authorization: "Bearer " + token },
        }
      )
        .then((res) => res.json())
        .then(() => {
          self.loadAccountRequests(); // refresh table
          self.closeConfirmDialog(); // close dialog

          
      // ✅ Open success dialog
      document.getElementById("successDialog").open();
        })
        .catch((err) => console.error("Error approving account:", err));
    };

    // Close the dialog
    self.closeConfirmDialog = function () {
      document.getElementById("confirmApproveDialog").close();
      self.accountRequestSelected(null); // reset
    };

    // Close the success dialog
    self.closeSuccessDialog = function () {
      document.getElementById("successDialog").close();
    };

    // ✅ Load on page init
    self.connected = function () {
      self.loadAccountRequests();
    };
  }

  return ManageAccountRequestsViewModel;
});
