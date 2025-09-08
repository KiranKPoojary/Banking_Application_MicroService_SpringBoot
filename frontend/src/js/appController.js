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
define(['knockout', 'ojs/ojcontext', 'ojs/ojmodule-element-utils', 'ojs/ojknockouttemplateutils', 'ojs/ojcorerouter', 'ojs/ojmodulerouter-adapter', 'ojs/ojknockoutrouteradapter', 'ojs/ojurlparamadapter', 'ojs/ojresponsiveutils', 'ojs/ojresponsiveknockoututils', 'ojs/ojarraydataprovider',
        'ojs/ojdrawerpopup', 'ojs/ojmodule-element', 'ojs/ojknockout'],
  function(ko, Context, moduleUtils, KnockoutTemplateUtils, CoreRouter, ModuleRouterAdapter, KnockoutRouterAdapter, UrlParamAdapter, ResponsiveUtils, ResponsiveKnockoutUtils, ArrayDataProvider) {

     function ControllerViewModel() {
       this.KnockoutTemplateUtils = KnockoutTemplateUtils;

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
         { path: "login" },
         { path: "signup" },
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
       this.navDataProvider = new ArrayDataProvider(navData.slice(1, 6), {
         keyAttributes: "path",
       });

       // var self = this;
       // let isLoggedIn = ko.observable(false); // Set this after successful login
       // let userRole = ko.observable("user"); // or 'employee'

       // self.navDataProvider = ko.computed(() => {
       //   if (!isLoggedIn()) {
       //     return new ArrayDataProvider(
       //       [
       //         { path: "login", detail: { label: "Login" } },
       //         { path: "register", detail: { label: "Register" } },
       //       ],
       //       { keyAttributes: "path" }
       //     );
       //   }

       //   if (userRole() === "user") {
       //     return new ArrayDataProvider(
       //       [
       //         { path: "dashboard", detail: { label: "Dashboard" } },
       //         { path: "transfer", detail: { label: "Transfer" } },
       //         { path: "deposit", detail: { label: "Deposit" } },
       //         { path: "withdraw", detail: { label: "Withdraw" } },
       //         { path: "profile", detail: { label: "Profile" } },
       //       ],
       //       { keyAttributes: "path" }
       //     );
       //   }

       //   if (userRole() === "employee") {
       //     return new ArrayDataProvider(
       //       [
       //         {
       //           path: "employee-dashboard",
       //           detail: { label: "Employee Dashboard" },
       //         },
       //         {
       //           path: "employee-register",
       //           detail: { label: "Register Employee" },
       //         },
       //         { path: "manage-accounts", detail: { label: "Manage Accounts" } },
       //       ],
       //       { keyAttributes: "path" }
       //     );
       //   }
       // });

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
       this.userLogin = ko.observable("john.hancock@oracle.com");

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
