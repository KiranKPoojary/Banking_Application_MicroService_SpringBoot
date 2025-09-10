define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojcorerouter",
  "utils/dateUtil",
  "ojs/ojknockout",
  "ojs/ojformlayout",
  "ojs/ojinputtext",
  "ojs/ojinputnumber",
  "ojs/ojselectsingle",
  "ojs/ojbutton",
  "ojs/ojtable",
], function (ko, ArrayDataProvider, CoreRouter, dateUtil) {
  function DepositViewModel() {
    var self = this;

    let userId = localStorage.getItem("Id");
    let token = localStorage.getItem("jwtToken");

    self.userAccounts = ko.observableArray([]);
    self.userAccountsDP = new ArrayDataProvider(self.userAccounts, {
      keyAttributes: "accountNumber",
    });

    // Form fields
    self.accountNumber = ko.observable();
    self.amount = ko.observable();
    self.description = ko.observable();

    // Response observables
    self.depositResponse = ko.observable({});
    self.depositSuccess = ko.observable(false);

    //hasaccount boolean
    self.hasAccounts = ko.observable(false);
    self.hasAccounts = ko.computed(() => {
      return self.userAccounts() && self.userAccounts().length > 0;
    });

    //format date
    self.formatDate = function (dateStr) {
      return dateUtil.formatDateTime(dateStr);
    };

    // Ledger provider
    self.ledgerEntries = ko.observableArray([]);
    self.ledgerDataProvider = new ArrayDataProvider(self.ledgerEntries, {
      keyAttributes: "entryDate",
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

    // Deposit API call
    self.makeDeposit = () => {
      const body = {
        accountNumber: self.accountNumber(),
        amount: self.amount(),
        description: self.description(),
        transactionSource: "CASH_VAULT", // or other source
        idempotencyKey: crypto.randomUUID(),
        createdBy: localStorage.getItem("username") || "system",
      };

      fetch(
        "http://localhost:8085/account-service/api/v0/transactions/deposit",
        {
          method: "POST",
          headers: {
            Authorization: "Bearer " + token,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(body),
        }
      )
        .then((res) => res.json())
        .then((data) => {
          self.depositResponse(data);
          self.depositSuccess(true);
          self.ledgerEntries(data.ledgerEntries || []);
        })
        .catch((err) => console.error("Error making deposit:", err));
    };

    // Actions
    self.makeAnotherDeposit = () => {
      self.depositSuccess(false);
      self.accountNumber(null);
      self.amount(null);
      self.description(null);
    };

    self.goToAccounts = () => {
      CoreRouter.rootInstance.go({ path: "manageAccountUser" });
    };

    // Load accounts on init
    self.loadAccounts();
  }

  return DepositViewModel;
});
