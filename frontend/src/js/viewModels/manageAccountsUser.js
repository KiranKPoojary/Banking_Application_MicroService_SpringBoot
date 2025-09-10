define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojknockout",
  "oj-c/card-view",
], function (ko, ArrayDataProvider) {
  function ManageAccountsUserViewModel() {
    var self = this;

    self.accountDataProvider = ko.observable(); // Will hold the data provider once data is fetched
    self.hasAccounts = ko.observable(false); // To track if accounts exist

    let userId = localStorage.getItem("Id");
    let token = localStorage.getItem("jwtToken");

    let accountUrl = `http://localhost:8085/user-service/api/v0/users/${userId}/accounts`;

    // Fetch data from REST API
    fetch(accountUrl, {
      method: "GET",
      headers: {
        Authorization: "Bearer " + token,
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        // Wrap the response in ArrayDataProvider
        if (Array.isArray(data) && data.length > 0) {
          self.hasAccounts(true);
          self.accountDataProvider(
            new ArrayDataProvider(data, { keyAttributes: "id" })
          );
        } else {
          self.hasAccounts(false);
          console.log("No accounts found for this user.");
        }
      })
      .catch((error) => {
        console.error("Fetch error:", error);
        // Optionally handle UI fallback or error state
      });
  }

  return ManageAccountsUserViewModel;
});
