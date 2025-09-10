/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your application specific code will go here
 */
define(['knockout', 'ojs/ojcontext', 'ojs/ojmodule-element-utils', 'ojs/ojknockouttemplateutils', 'ojs/ojcorerouter', 'ojs/ojmodulerouter-adapter', 'ojs/ojknockoutrouteradapter', 'ojs/ojurlparamadapter', 'ojs/ojresponsiveutils', 'ojs/ojresponsiveknockoututils', 'ojs/ojarraydataprovider','utils/authJWT',
        'ojs/ojdrawerpopup', 'ojs/ojmodule-element', 'ojs/ojknockout','ojs/ojdialog','oj-c/avatar'],
  function(ko, Context, moduleUtils, KnockoutTemplateUtils, CoreRouter, ModuleRouterAdapter, KnockoutRouterAdapter, UrlParamAdapter, ResponsiveUtils, ResponsiveKnockoutUtils, ArrayDataProvider, AuthJWT) {

     function ControllerViewModel() {
       this.KnockoutTemplateUtils = KnockoutTemplateUtils;

       var self = this;
       // Handle announcements sent when pages change, for Accessibility.
       this.manner = ko.observable("polite");
       this.message = ko.observable();
       announcementHandler = (event) => {
         this.message(event.detail.message);
         this.manner(event.detail.manner);
       };

       document
         .getElementById("globalBody")
         .addEventListener("announce", announcementHandler, false);

       // Media queries for responsive layouts
       const smQuery = ResponsiveUtils.getFrameworkQuery(
         ResponsiveUtils.FRAMEWORK_QUERY_KEY.SM_ONLY
       );
       this.smScreen =
         ResponsiveKnockoutUtils.createMediaQueryObservable(smQuery);
       const mdQuery = ResponsiveUtils.getFrameworkQuery(
         ResponsiveUtils.FRAMEWORK_QUERY_KEY.MD_UP
       );
       this.mdScreen =
         ResponsiveKnockoutUtils.createMediaQueryObservable(mdQuery);

       let navData = [
         { path: "", redirect: "dashboard" },
         {
           path: "dashboard",
           detail: { label: "Dashboard", iconClass: "oj-ux-ico-bar-chart" },
         },
         {
           path: "home",
           detail: { label: "Home", iconClass: "oj-ux-ico-bar-chart" },
         },
         {
           path: "incidents",
           detail: { label: "Incidents", iconClass: "oj-ux-ico-fire" },
         },
         {
           path: "customers",
           detail: { label: "Customers", iconClass: "oj-ux-ico-contact-group" },
         },
         {
           path: "about",
           detail: { label: "About", iconClass: "oj-ux-ico-information-s" },
         },
         { path: "employee-login", detail: { label: "Employee Login" } },
         { path: "login", detail: { label: " User Login" } },
         { path: "signup" },
         { path: "user-dashboard", detail: { label: "user dashbaord" } },
         { path: "manageAccountsUser", detail: { label: "My Accounts" } },
         { path: "transfer", detail: { label: "Transfer" } },
         { path: "withdraw", detail: { label: "Withdraw" } },
         { path: "deposit", detail: { label: "Deposit" } },
       ];

       // Router setup
       let router = new CoreRouter(navData, {
         urlAdapter: new UrlParamAdapter(),
       });
       router.sync();

       CoreRouter.rootInstance = router; // ✅ This is crucial

       this.moduleAdapter = new ModuleRouterAdapter(router);

       this.selection = new KnockoutRouterAdapter(router);

       // Setup the navDataProvider with the routes, excluding the first redirected
       // route.
       this.navDataProvider = new ArrayDataProvider(navData.slice(1, 7), {
         keyAttributes: "path",
       });

       let isLoggedIn = ko.observable(false); // Observable for login state
       let userRole = ko.observable(null); // Observable for user role
       let username = ko.observable(null); // Observable for username
       let Id = ko.observable(null); // Observable for user ID

       self.isLoggedIn = isLoggedIn;
       self.userRole = userRole;
       self.username = username;
       self.Id = Id;

       // Make the observables globally accessible
       window.app = self;

       // Check token validity on app load
       let token = localStorage.getItem("jwtToken");

       console.log("Token from localStorage:", token);

       if (token && AuthJWT.isTokenValid(token)) {
         isLoggedIn(true);
         console.log("Token is valid");
         userRole(
           localStorage.getItem("role")
             ? localStorage.getItem("role").toLowerCase()
             : null
         );
         username(
           localStorage.getItem("username")
             ? localStorage.getItem("username")
             : null
         );
         Id(
          localStorage.getItem('Id')? localStorage.getItem('Id'): null
         );
         console.log("User role from localStorage:", userRole());
         console.log("Username from localStorage:", username());
          console.log("User ID from localStorage:", Id());
       } else {
         isLoggedIn(false);
         userRole(null);
         username(null);
         Id(null);
         localStorage.clear();
       }

       console.log("User role in nav:", userRole());

       self.navDataProvider = ko.computed(() => {
         if (!isLoggedIn()) {
           return new ArrayDataProvider(
             [
               { path: "home", detail: { label: "Home" } },
               { path: "employee-login", detail: { label: "Employee Login" } },
               { path: "login", detail: { label: " User Login" } },
             ],
             { keyAttributes: "path" }
           );
         }

         if (userRole() === "customer" && isLoggedIn()) {
           return new ArrayDataProvider(
             [
               { path: "user-dashboard", detail: { label: "User Dashboard" } },
               { path: "manageAccountsUser", detail: { label: "My Accounts" } },
               { path: "transfer", detail: { label: "Transfer" } },
               { path: "deposit", detail: { label: "Deposit" } },
               { path: "withdraw", detail: { label: "Withdraw" } },
               //  { path: "profile", detail: { label: "Profile" } },
             ],
             { keyAttributes: "path" }
           );
         }

          if (userRole() === "admin") {
            return new ArrayDataProvider(
              [
                {
                  path: "#",
                  detail: { label: "Admin Dashboard" },
                },
                {
                  path: "#",
                  detail: { label: "Manage Employee" },
                },
                { path: "#", detail: { label: "Manage Accounts" } },
                { path: "#", detail: { label: "Manage User" } },
                { path: "#", detail: { label: "Manage Notification" } },
              ],
              { keyAttributes: "path" }
            );
          }

          if (userRole() === "manager") {
            return new ArrayDataProvider(
              [
                {
                  path: "#",
                  detail: { label: "Manager Dashboard" },
                },
                {
                  path: "#",
                  detail: { label: "Approve Account" },
                },
                { path: "#", detail: { label: "Manage User" } },
              ],
              { keyAttributes: "path" }
            );
          }

          if (userRole() === "executive") {
            return new ArrayDataProvider(
              [
                {
                  path: "#",
                  detail: { label: "Executive Dashboard" },
                },
                {
                  path: "#",
                  detail: { label: "Manage User" },
                },
                { path: "#", detail: { label: "Manage Accounts" } },
              ],
              { keyAttributes: "path" }
            );
          }
       });

       // Drawer
       self.sideDrawerOn = ko.observable(false);

       // Close drawer on medium and larger screens
       this.mdScreen.subscribe(() => {
         self.sideDrawerOn(false);
       });

       // Called by navigation drawer toggle button and after selection of nav drawer item
       this.toggleDrawer = () => {
         self.sideDrawerOn(!self.sideDrawerOn());
       };

       // Header
       // Application Name used in Branding Area
       this.appName = ko.observable("KK's Bank");
       // User Info used in Global Navigation area
       this.userLogin = userRole();

       //  this.username= username();

       //Header Signout

       // self.confirmLogout = function () {
       //   console.log("Logging out...");
       //   window.app.isLoggedIn(false);
       //   window.app.userRole(null);
       //   window.app.username(null);
       //   localStorage.clear();
       //   console.log("Logged out successfully");

       //   document.getElementById("logoutGlobalDialog").close();

       //   CoreRouter.rootInstance.go({path: 'home'});
       // };

       self.confirmLogout = function () {
         console.log("Logging out...");

         // Get user id & JWT token from local storage (or window.app if stored there)
         let userId = localStorage.getItem("Id"); // adjust if you store id somewhere else
         let token = localStorage.getItem("jwtToken"); // assuming JWT saved at login

         if (!userId || !token) {
           console.error("User ID or token missing");
           return;
         }

         // Backend logout API
         let url = `http://localhost:8085/user-service/api/v0/auth/logout/${userId}`;

         fetch(url, {
           method: "POST",
           headers: {
             "Content-Type": "application/json",
             "Authorization" : `Bearer ${token}`,
           },
         })
           .then((response) => {
             if (!response.ok) {
               throw new Error("Logout API failed");
             }
             return response.text();
           })
           .then((data) => {
             console.log("Backend logout success:", data);

             // Clear session after backend success
             window.app.isLoggedIn(false);
             window.app.userRole(null);
             window.app.username(null);
             window.app.Id(null);
             localStorage.clear();

             console.log("Logged out successfully");

             document.getElementById("logoutGlobalDialog").close();

             CoreRouter.rootInstance.go({ path: "home" });
           })
           .catch((error) => {
             console.error("Error during logout:", error);

             // Still clear local session to avoid stuck state
             window.app.isLoggedIn(false);
             window.app.userRole(null);
             window.app.username(null);
             window.app.Id(null);
             localStorage.clear();

             document.getElementById("logoutGlobalDialog").close();
             CoreRouter.rootInstance.go({ path: "home" });
           });
       };

       self.cancelLogout = function () {
         console.log("Logout canceled");
         // Router.rootInstance.go("dashboard");
       };

       self.openLogoutDialog = function () {
         console.log("Opening logout dialog...");
         const dialog = document.getElementById("logoutGlobalDialog");
         if (dialog) {
           setTimeout(() => {
             dialog.open();
           }, 0); // Delay execution to allow upgrade
         } else {
           console.warn("logoutDialog not found in DOM.");
         }
       };

       this.handleUserMenuAction = (event) => {
         const action = event.detail.selectedValue;
         console.log("User menu action:", action);
         if (action === "out") {
           self.openLogoutDialog();
         } else if (action === "pref") {
           // Handle Preferences action
           console.log("Preferences clicked");
         }
       };

       // Footer
       this.footerLinks = [
         {
           name: "About Oracle",
           linkId: "aboutOracle",
           linkTarget:
             "http://www.oracle.com/us/corporate/index.html#menu-about",
         },
         {
           name: "Contact Us",
           id: "contactUs",
           linkTarget: "http://www.oracle.com/us/corporate/contact/index.html",
         },
         {
           name: "Legal Notices",
           id: "legalNotices",
           linkTarget: "http://www.oracle.com/us/legal/index.html",
         },
         {
           name: "Terms Of Use",
           id: "termsOfUse",
           linkTarget: "http://www.oracle.com/us/legal/terms/index.html",
         },
         {
           name: "Your Privacy Rights",
           id: "yourPrivacyRights",
           linkTarget: "http://www.oracle.com/us/legal/privacy/index.html",
         },
       ];
     }



     // release the application bootstrap busy state
     Context.getPageContext().getBusyContext().applicationBootstrapComplete();

     return new ControllerViewModel();
  }
);
