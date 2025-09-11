define([
  "knockout",
  "ojs/ojarraydataprovider",
  "utils/dateUtil", // optional, if you have it
  "ojs/ojtable",
  "ojs/ojbutton",
  "ojs/ojdialog",
  "ojs/ojinputtext",
], function ( ko, ArrayDataProvider, dateUtil) {
  function ManageNotificationsViewModel() {
    var self = this;

    self.notificationDataProvider = ko.observable(
      new ArrayDataProvider([], { keyAttributes: "id" })
    );
    self.notifications = ko.observableArray([]);
    self.selectedNotification = ko.observable(null);
    self.loading = ko.observable(false);
    self.errorMessage = ko.observable(null);

    // ✅ Load notifications
    self.loadNotifications = async function () {
      self.loading(true);
      self.errorMessage(null);

      const token = localStorage.getItem("jwtToken");
      try {
        const resp = await fetch(
          "http://localhost:8085/notification-service/api/v0/notifications",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: token ? "Bearer " + token : "",
            },
          }
        );

        if (!resp.ok) {
          throw new Error("Failed to load notifications: " + resp.status);
        }

        const data = await resp.json();

        const mapped = (data || []).map((n, idx) => {
          const id = n.id ?? idx;
          const createdAt = n.createdAt ?? null;

          let formattedDate = dateUtil.formatDateTime(createdAt);

          return {
            id,
            userId: n.userId,
            email: n.email,
            subject: n.subject ?? "",
            message: n.message ?? "",
            status: n.status ?? "",
            createdAt,
            formattedDate,
            raw: n,
          };
        });

        self.notifications(mapped);
        self.notificationDataProvider(
          new ArrayDataProvider(mapped, { keyAttributes: "id" })
        );
      } catch (err) {
        console.error("Error loading notifications:", err);
        self.errorMessage(err.message || String(err));
        self.notificationDataProvider(
          new ArrayDataProvider([], { keyAttributes: "id" })
        );
      } finally {
        self.loading(false);
      }
    };



    // Function called when View button is clicked
    self.openNotification = function (event,context) {

    const rowData = context.item.data;
      console.log("Using context",rowData);
      

      // Set it for dialog
      self.selectedNotification(rowData);

      // Open dialog
      document.getElementById("notificationDialog").open();
    };

    // Close dialog
    self.closeNotification = function () {
      document.getElementById("notificationDialog").close();
    };

    // // ✅ Close dialog
    // self.closeNotification = function () {
    //   const dlg = document.getElementById("notificationDialog");
    //   if (dlg) dlg.close();
    //   self.selectedNotification(null);
    // };

    // ✅ Refresh
    self.refresh = function () {
      self.loadNotifications();
    };

    // Load on init
    self.connected = function () {
      self.loadNotifications();
    };

    self.logRow = function (row) {
      console.log("Full row object:", row);
      console.log("Row data:", row.data);
      console.log("Row key:", row.key);
    };

    // return self;
  }

  return ManageNotificationsViewModel;
});
