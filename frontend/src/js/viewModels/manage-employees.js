define([
  "ojs/ojcore",
  "knockout",
  "ojs/ojtable",
  "ojs/ojdialog",
  "ojs/ojinputtext",
  "ojs/ojbutton",
  "ojs/ojarraydataprovider",
], function (oj, ko) {
  function ManageEmployeesViewModel() {
    let self = this;

    self.employees = ko.observableArray([]);
    self.employeeDataProvider = new oj.ArrayDataProvider(self.employees, {
      keyAttributes: "employeeId",
    });

    // Form for new employee
    self.newEmployee = {
      username: ko.observable(""),
      firstName: ko.observable(""),
      lastName: ko.observable(""),
      email: ko.observable(""),
      password: ko.observable(""),
      role: ko.observable(""),
      branch: ko.observable(""),
      status: ko.observable("ACTIVE"),
    };

    // Fetch employees on load
    self.loadEmployees = async function () {
      let token = localStorage.getItem("jwtToken");
      try {
        let response = await fetch(
          "http://localhost:8085/user-service/api/v0/employees",
          {
            method: "GET",
            headers: {
              Authorization: "Bearer " + token,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch employees: " + response.status);
        }

        let data = await response.json();
        self.employees(data);
      } catch (err) {
        console.error("Error fetching employees", err);
      }
    };

    // Open add dialog
    self.openAddDialog = function () {
      document.getElementById("addEmployeeDialog").open();
    };

    // Close dialog
    self.closeAddDialog = function () {
      document.getElementById("addEmployeeDialog").close();
    };

    // Save new employee
    self.saveEmployee = async function () {
      let token = localStorage.getItem("jwtToken");
      let empData = {
        username: self.newEmployee.username(),
        firstName: self.newEmployee.firstName(),
        lastName: self.newEmployee.lastName(),
        email: self.newEmployee.email(),
        password: self.newEmployee.password(),
        role: self.newEmployee.role(),
        branch: self.newEmployee.branch(),
        status: self.newEmployee.status(),
      };

      try {
        let response = await fetch(
          "http://localhost:8085/user-service/api/v0/employees",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + token,
            },
            body: JSON.stringify(empData),
          }
        );

        if (!response.ok) {
          throw new Error("Failed to save employee: " + response.status);
        }

        // Close dialog and reload employees
        self.closeAddDialog();
        document.getElementById("successDialog").open();
        // self.loadEmployees();
      } catch (err) {
        console.error("Error saving employee", err);
      }
    };

    self.closeSuccessDialog = function () {
      document.getElementById("successDialog").close();
      // reload or refresh employee list
      self.loadEmployees();
    };


    // Initial load
    self.loadEmployees();
  }

  return ManageEmployeesViewModel;
});
