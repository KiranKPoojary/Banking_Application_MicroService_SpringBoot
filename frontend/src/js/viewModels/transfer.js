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
], function (ko, ArrayDataProvider, CoreRouter,dateUtil) {
  function TransferViewModel() {
    var self = this;

    // Observables for form inputs
    self.fromAccount = ko.observable();
    self.toAccount = ko.observable();
    self.transactionSource = ko.observable("UPI"); // default
    self.amount = ko.observable();
    self.description = ko.observable();
    self.hasAccounts = ko.observable(false);

    //format date
    self.formatDate = function (dateStr) {
      return dateUtil.formatDateTime(dateStr);
    };
  

    // API token from localStorage
    let token = localStorage.getItem("jwtToken");
    let userId = localStorage.getItem("Id");

    // Accounts dropdown
    self.userAccounts = ko.observableArray([]);
    self.userAccountsDP = new ArrayDataProvider(self.userAccounts, {
      keyAttributes: "accountNumber",
    });

    self.hasAccounts = ko.computed(() => {
      return self.userAccounts() && self.userAccounts().length > 0;
    });

    // Transfer response
    self.transferResponse = ko.observable(null);
    self.transferSuccess = ko.computed(() => {
      return (
        self.transferResponse() && self.transferResponse().status === "SUCCESS"
      );
    });


    //Trasaction sources
    self.transactionMethods = [
      { value: "UPI", label: "UPI" },
      { value: "NEFT", label: "NEFT" },
    ];
    self.transactionMethodsDP = new ArrayDataProvider(self.transactionMethods, {
      keyAttributes: "value",
    });

    // Ledger entries table
    self.ledgerDataProvider = ko.computed(() => {
      let resp = self.transferResponse();
      if (resp && resp.ledgerEntries) {
        return new ArrayDataProvider(resp.ledgerEntries, {
          keyAttributes: "id",
        });
      }
      return null;
    });

    // Fetch user's accounts (populate dropdown)
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

    // Make transfer
    self.makeTransfer = async () => {
      let body = {
        fromAccountNumber: self.fromAccount(),
        toAccountNumber: self.toAccount(),
        transactionSource: self.transactionSource(),
        amount: self.amount(),
        description: self.description(),
        idempotencyKey: crypto.randomUUID(), // Unique key
        createdBy: localStorage.getItem("username"),
      };

      try {
        let resp = await fetch(
          "http://localhost:8085/account-service/api/v0/transactions/transfer",
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
          self.transferResponse(data);
        } else {
          console.error("Transfer failed", resp.status);
        }
      } catch (err) {
        console.error("Error making transfer", err);
      }
    };

    // Reset form for another transfer
    self.makeAnotherTransfer = () => {
      self.fromAccount(null);
      self.toAccount(null);
      self.transactionSource("UPI");
      self.amount(null);
      self.description(null);
      self.transferResponse(null);
    };

    // Navigate back to accounts (you can route using ojRouter if configured)
    self.goToAccounts = () => {
      CoreRouter.rootInstance.go({path : 'transfer'}); // adjust route if needed
    };

    // Load accounts on init
    self.loadAccounts();
  }

  return TransferViewModel;
});
