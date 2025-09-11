define([
  "knockout",
  "ojs/ojarraydataprovider",
  "utils/dateUtil",
  "ojs/ojknockout",
  "ojs/ojtable",
  "ojs/ojbutton",
  "ojs/ojdialog",
], function (ko, ArrayDataProvider,dateUtil) {
  function ManageAccountsViewModel() {
    var self = this;

       let token = localStorage.getItem("jwtToken");

    // Observables
    self.accounts = ko.observableArray([]);
    self.accountDataProvider = new ArrayDataProvider(self.accounts, {
      keyAttributes: "id",
    });

    self.selectedAccount = ko.observable(null);
    self.transactions = ko.observableArray([]);
    self.transactionDataProvider = new ArrayDataProvider(self.transactions, {
      keyAttributes: "transactionId",
    });

    // Fetch all accounts
    self.loadAccounts = async function () {
      try {
            const response = await fetch(
            "http://localhost:8085/account-service/api/v0/accounts",
            {
                method: "GET",
                headers: {
                Authorization: "Bearer " + token,
                "Content-Type": "application/json",
                },
            }
            );
            if (!response.ok) throw new Error("Failed to load accounts");

            const data = await response.json();
            console.log("Accounts:", data);

            // Map backend response into observableArray
            self.accounts(data); 
        } catch (error) {
        console.error("Error loading accounts:", error);
      }
    };

    // Fetch transactions for selected account
    self.viewTransactions = async function (event,context) {
        const account=context.item.data;
        if(account){self.selectedAccount(account)};

    // 🔹 Fetch transactions
     
      let url = `http://localhost:8085/user-service/api/v0/users/${account.userId}/${account.id}/transactions`;

      fetch(url, {
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
          if (Array.isArray(data)) {

            let formatted = data.map((txn) => {
              return {
                transactionId: txn.transactionId,
                entryDate: txn.entryDate,
                formattedDate: dateUtil.formatDateTime(txn.entryDate),
                description: txn.description,
                debit: txn.entryType === "DEBIT" ? txn.amount : null,
                credit: txn.entryType === "CREDIT" ? txn.amount : null,
                // keep original if needed:
                entryType: txn.entryType,
                amount: txn.amount,
              };
            });

            self.transactions(formatted);
          }
        })
        .catch((err) => {
          console.error("Error fetching transactions:", err);
        });
        
        document.getElementById("transactionsDialog").open();
    
    };

    // Close dialog
    self.closeTransactions = function () {
      document.getElementById("transactionsDialog").close();
    };

    // Load accounts on init
    self.loadAccounts();
  }

  return ManageAccountsViewModel;
});
