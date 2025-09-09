define([
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojchart",
  "ojs/ojtable",
], function (ko, ArrayDataProvider) {
  function UserDashboardViewModel() {
    var self = this;

    // Total balance
    self.totalBalance = ko.observable("1,25,000");

    // Last 5 Transactions
    let txnArray = [
      {
        date: "2025-09-01",
        desc: "ATM Withdrawal",
        amount: "-2000",
        type: "Debit",
      },
      {
        date: "2025-08-28",
        desc: "Salary Credit",
        amount: "+50,000",
        type: "Credit",
      },
      {
        date: "2025-08-25",
        desc: "Online Shopping",
        amount: "-4500",
        type: "Debit",
      },
      {
        date: "2025-08-20",
        desc: "Restaurant",
        amount: "-1200",
        type: "Debit",
      },
      {
        date: "2025-08-18",
        desc: "Fund Transfer",
        amount: "-5000",
        type: "Debit",
      },
    ];
    self.txnDataProvider = new ArrayDataProvider(txnArray, {
      keyAttributes: "date",
    });

    // Spending Chart Data
   let chartArray = [
     { category: "Food", value: 2500 },
     { category: "Shopping", value: 4000 },
     { category: "Bills", value: 3500 },
     { category: "Entertainment", value: 2000 },
   ];
   self.chartDataProvider = new ArrayDataProvider(chartArray, {
     keyAttributes: "category",
   });
  }

  return UserDashboardViewModel;
});
