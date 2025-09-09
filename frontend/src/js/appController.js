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
        'ojs/ojdrawerpopup', 'ojs/ojmodule-element', 'ojs/ojknockout','ojs/ojdialog'],
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
         { path: "employee-login",detail: { label: "Employee Login" } },
         { path: "login" },
         { path: "signup" },
         {path : "logout" }
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

        self.isLoggedIn = isLoggedIn;
        self.userRole = userRole;
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
       } else {
         isLoggedIn(false);
         userRole(null);
         localStorage.clear();
       }

       

       console.log("User role in nav:", userRole());

       self.navDataProvider = ko.computed(() => {
         if (!isLoggedIn()) {
           return new ArrayDataProvider(
             [
               { path: "home", detail: { label: "Home" } },
               { path: "employee-login", detail: { label: "Employee Login" } },
               { path: "login", detail: { label: "Login" } },
             ],
             { keyAttributes: "path" }
           );
         }

         if (userRole() === "customer" && isLoggedIn()) {
           return new ArrayDataProvider(
             [
               { path: "dashboard", detail: { label: "Dashboard" } },
               { path: "login", detail: { label: "Login" } },
               { path: "logout", detail: { label: "Logout" }},
              //  { path: "transfer", detail: { label: "Transfer" } },
              //  { path: "deposit", detail: { label: "Deposit" } },
              //  { path: "withdraw", detail: { label: "Withdraw" } },
              //  { path: "profile", detail: { label: "Profile" } },
             ],
             { keyAttributes: "path" }
           );
         }

        //  if (userRole() === "employee") {
        //    return new ArrayDataProvider(
        //      [
        //        {
        //          path: "employee-dashboard",
        //          detail: { label: "Employee Dashboard" },
        //        },
        //        {
        //          path: "employee-register",
        //          detail: { label: "Register Employee" },
        //        },
        //        { path: "manage-accounts", detail: { label: "Manage Accounts" } },
        //      ],
        //      { keyAttributes: "path" }
        //    );
        //  }
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
       this.appName = ko.observable("App Name");
       // User Info used in Global Navigation area
       this.userLogin = userRole();


       //Header Signout

              self.confirmLogout = function () {
                console.log("Logging out...");
                window.app.isLoggedIn(false);
                window.app.userRole(null);
                localStorage.clear();
                console.log("Logged out successfully");

                document.getElementById("logoutGlobalDialog").close();
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

        // self.logout = function () {
        //   // Perform logout logic here
        //   this.logoutDialog.open();

        //   //   Router.rootInstance.go("login");
        // };

      

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
