define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojcorerouter",
  "utils/dateUtil",
  "ojs/ojknockout",
  "ojs/ojbutton",
  "ojs/ojinputtext",
  "ojs/ojinputnumber",
  "ojs/ojselectsingle",
  "ojs/ojformlayout",
  "ojs/ojtable",
  "ojs/ojdialog",
], function (ko, ArrayDataProvider, CoreRouter, dateUtil) {
  function WithdrawViewModel() {
    var self = this;

    // Observables for form inputs
    self.accountNumber = ko.observable();
    self.amount = ko.observable();
    self.description = ko.observable();
    self.hasAccounts = ko.observable(false);

    self.transactionSource = ko.observable("ATM"); // default

    // API token + user
    let token = localStorage.getItem("jwtToken");
    let userId = localStorage.getItem("Id");
    let username = localStorage.getItem("username");

    // User accounts
    self.userAccounts = ko.observableArray([]);
    self.userAccountsDP = new ArrayDataProvider(self.userAccounts, {
      keyAttributes: "accountNumber",
    });

    // Check if user has accounts
    self.hasAccounts = ko.computed(() => {
      return self.userAccounts() && self.userAccounts().length > 0;
    });

    // Withdraw response
    self.withdrawResponse = ko.observable(null);
    self.withdrawSuccess = ko.computed(() => {
      return (
        self.withdrawResponse() && self.withdrawResponse().status === "SUCCESS"
      );
    });

    // Ledger entries table
    self.ledgerDataProvider = ko.computed(() => {
      let resp = self.withdrawResponse();
      if (resp && resp.ledgerEntries) {
        return new ArrayDataProvider(resp.ledgerEntries, {
          keyAttributes: "id",
        });
      }
      return null;
    });

    // Fetch accounts
    self.loadAccounts = async () => {
      try {
        let resp = await fetch(
          `http://localhost:8085/user-service/api/v0/users/${userId}/accounts`,
          {
            method: "GET",
            headers: {
              Authorization: "Bearer " + token,
              "Content-Type": "application/json",
            },
          }
        );
        if (resp.ok) {
          let accounts = await resp.json();
          self.userAccounts(accounts);
        } else {
          console.error("Failed to load accounts", resp.status);
        }
      } catch (err) {
        console.error("Error fetching accounts", err);
      }
    };

    // Make withdrawal
    self.makeWithdrawal = async () => {
      let body = {
        accountNumber: self.accountNumber(),
        amount: self.amount(),
        description: self.description(),
        transactionSource: self.transactionSource(),
        idempotencyKey: crypto.randomUUID(),
        createdBy: username,
      };

      try {
        let resp = await fetch(
          "http://localhost:8085/account-service/api/v0/transactions/withdraw",
          {
            method: "POST",
            headers: {
              Authorization: "Bearer " + token,
              "Content-Type": "application/json",
            },
            body: JSON.stringify(body),
          }
        );

        if (resp.ok) {
          let data = await resp.json();
          self.withdrawResponse(data);

        } else {
          console.error("Withdrawal failed", resp.status);
        }
      } catch (err) {
        console.error("Error making withdrawal", err);
      }
    };

    // Reset form
    self.makeAnotherWithdrawal = () => {
      self.accountNumber(null);
      self.amount(null);
      self.description(null);
      self.transactionSource("ATM");
      self.withdrawResponse(null);


    };

    // Navigate back (adjust route if needed)
    self.goToAccounts = () => {
    
  
      CoreRouter.rootInstance.go({ path: "manageAccountsUser" });
    };

    //format date
    self.formatDate = function (dateStr) {
      return dateUtil.formatDateTime(dateStr);
    };

    // Load accounts on init
    self.loadAccounts();
  }

  return WithdrawViewModel;
});
