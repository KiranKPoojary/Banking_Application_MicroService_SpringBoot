define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojknockout",
  "oj-c/card-view",
  "ojs/ojdialog",
  "ojs/ojtrain",
], function (ko, ArrayDataProvider) {
  function ManageAccountsUserViewModel() {
    const self = this;

    // Observables
    self.accountDataProvider = ko.observable();
    self.hasAccounts = ko.observable(false);
    self.requestDataProvider = ko.observable();
    self.hasRequests = ko.observable(false);
    self.selectedAccountType = ko.observable();

    // Static options
  const accountType = [
    { value: "SAVINGS", label: "Savings" },
    { value: "CURRENT", label: "Current" },
  ];


  this.accountTypeOptions = new ArrayDataProvider(accountType, {
    keyAttributes: "value",
    textFilterAttributes: ["label"],
  });

  self.reqTrainSteps = ko.observableArray([
    { id: "REQUESTED", label: "Requested" },
    { id: "CREATED", label: "Created" },
    { id: "APPROVED", label: "Approved" },
    { id: "REJECTED", label: "Rejected" }
  ]);

  self.getTrainStepId = function (event,context) {
    const status = context.item.data.status;
    console.log("Getting train step for status:", status);
    const foundStep = self
      .reqTrainSteps()
      .find((step) => step.id.toLowerCase() === status.toLowerCase());
    return foundStep ? foundStep.id : null;
  };

  
    // Auth info
    const userId = localStorage.getItem("Id");
    const token = localStorage.getItem("jwtToken");

    // URLs
    const accountUrl = `http://localhost:8085/user-service/api/v0/users/${userId}/accounts`;
    const requestUrl = `http://localhost:8085/account-service/api/v0/account-request`;

    // Fetch accounts
    self.loadAccounts=async function () {
      fetch(accountUrl, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (!response.ok) throw new Error("Failed to fetch accounts");
          return response.json();
        })
        .then((data) => {
          if (Array.isArray(data) && data.length > 0) {
            self.hasAccounts(true);
            self.accountDataProvider(
              new ArrayDataProvider(data, { keyAttributes: "id" })
            );
          } else {
            self.hasAccounts(false);
            console.log("No accounts found.");
          }
        })
        .catch((error) => console.error("Account fetch error:", error));
    }

    

    // Fetch account requests
    self.loadRequests=async function () {
      console.log("Loading account requests...");
      fetch(requestUrl, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (!response.ok) throw new Error("Failed to fetch requests");
          return response.json();
        })
        .then((data) => {
          if (Array.isArray(data) && data.length > 0) {
            self.hasRequests(true);
            self.requestDataProvider(
              new ArrayDataProvider(data, { keyAttributes: "id" })
            );
            console.log(self.requestDataProvider());
          } else {
            self.hasRequests(false);
          }
        })
        .catch((error) => console.error("Request fetch error:", error));
    };

    // Create new account request
    self.createAccountRequest = function () {
      const selectedType = self.selectedAccountType();
      if (!selectedType) {
        alert("Please select an account type.");
        return;
      }

      const body = { accountType: selectedType };

      fetch(requestUrl, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
      })
        .then((response) => {
          if (!response.ok) throw new Error("Failed to create account request");
          return response.json();
        })
        .then(() => {
          self.selectedAccountType(null);
          document.getElementById("accountrequestsuccess").open();
          self.loadRequests();
        })
        .catch((error) => alert("Error: " + error.message));
    };

    // Close success dialog
    self.closeaccountrequestsuccess = function () {
      document.getElementById("accountrequestsuccess").close();
    };

    // Initial load
    
      self.loadAccounts();
      self.loadRequests();
    

  }

  return ManageAccountsUserViewModel;
});
