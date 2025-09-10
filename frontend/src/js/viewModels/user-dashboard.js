define([
  "knockout",
  "ojs/ojarraydataprovider",
  "utils/dateUtil",
  "ojs/ojchart",
  "ojs/ojtable",
  "ojs/ojselectsingle",
], function (ko, ArrayDataProvider,dateUtil) {
  function UserDashboardViewModel() {
    var self = this;

    self.username = localStorage.getItem("username");
    let userId = localStorage.getItem("Id");
    let token = localStorage.getItem("jwtToken");

    self.hasAccounts = ko.observable(false);

    //format date
    self.formatDate = function (dateStr) {
      console.log(dateStr);
      return dateUtil.formatDateTime(dateStr);
    };

    //for summary
    self.monthlySummary = ko.observableArray();
    self.weeklySummary = ko.observableArray();
    self.debitCreditSummary = ko.observableArray();
    self.dailySummary=ko.observableArray();


    // User accounts
    self.accountDataProvider = ko.observable([]);

    //transaction account id selection
    self.selectedAccountId = ko.observable();
    console.log("Selected Account ID:", self.selectedAccountId());

    self.transactionDataProvider = ko.observable(
      new ArrayDataProvider([], { keyAttributes: "transactionId" }) // ✅ always a valid DataProvider
    );

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

    // 🔹 Fetch transactions
    self.fetchTransactions = function (accountId) {
      let url = `http://localhost:8085/user-service/api/v0/users/${userId}/${accountId}/transactions`;

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
            self.processTransactions(data);

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

            self.transactionDataProvider(
              new ArrayDataProvider(formatted, {
                keyAttributes: "transactionId",
              })
            );
          }
        })
        .catch((err) => {
          console.error("Error fetching transactions:", err);
        });
    };

    // Auto-load on account change
    self.selectedAccountId.subscribe((id) => {
      console.log("Selected Account ID changed to:", id);
      if (id) {
        self.fetchTransactions(id);
      }
    });

    // Process data for charts
    self.processTransactions = function (data) {
      if (!Array.isArray(data)) return;

      //Daily totals
      let daily={};
      // Monthly totals
      let monthly = {};
      // Weekly totals
      let weekly = {};
      // Debit vs Credit
      let debit = 0,
        credit = 0;



      data.forEach((tx) => {
        let d = new Date(tx.entryDate);

        // Daily key (e.g., "2025-09-10")
        // let dayKey = d.toISOString().split("T")[0]; // YYYY-MM-DD
       

        // Format as YYYY-MM-DD in local time
        let dayKey = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
          2,
          "0"
        )}-${String(d.getDate()).padStart(2, "0")}`;

         daily[dayKey] = (daily[dayKey] || 0) + tx.amount;
         console.log("dayKey", dayKey);

        // Month key (e.g., "Sep 2025")
        let monthKey = d.toLocaleString("en-US", {
          month: "short",
          year: "numeric",
        });
        monthly[monthKey] = (monthly[monthKey] || 0) + tx.amount;

        // Week key (e.g., "Week 36")
        let weekKey = `W${getWeekNumber(d)}`;
        weekly[weekKey] = (weekly[weekKey] || 0) + tx.amount;

        // Debit/Credit
        if (tx.entryType === "DEBIT") debit += tx.amount;
        if (tx.entryType === "CREDIT") credit += tx.amount;
      });

      console.log("credit",credit);
            console.log("DEBIT", debit);



      // Convert to ArrayDataProvider-friendly arrays

       self.dailySummary(
         Object.keys(daily).map((k) => ({
           series: "Daily Transactions", // all daily totals belong to same series
           day: dateUtil.formatDateTime(k).slice(0,-10), // group = date string
           value: daily[k],
         }))
       );


       console.log(self.dailySummary());

      self.monthlySummary(
        Object.keys(monthly).map((k) => ({ period: k, amount: monthly[k] }))
      );
      self.weeklySummary(
        Object.keys(weekly).map((k) => ({ week: k, amount: weekly[k] }))
      );
      self.debitCreditSummary([
        { series: "DEBIT", group: "1", value: debit },
        { series: "CREDIT", group: "1", value: credit },
      ]);

      console.log("debitCreditSummary",self.debitCreditSummary());
    };

    // Helper: Week number
    function getWeekNumber(date) {
      let start = new Date(date.getFullYear(), 0, 1);
      let diff =
        date -
        start +
        (start.getTimezoneOffset() - date.getTimezoneOffset()) * 60000;
      let oneWeek = 604800000; // ms in a week
      return Math.ceil(diff / oneWeek + 1);
    }
  }

  return UserDashboardViewModel;
});
