define([
  "knockout",
  "ojs/ojbootstrap",
  "ojs/ojarraydataprovider",
  "ojs/ojknockout",
  "ojs/ojfilmstrip",
], function (ko, Bootstrap, ArrayDataProvider) {
  function HomeViewModel() {
    var self = this;

    // 🔹 Arrow Placement & Visibility for oj-film-strip
    self.currentNavArrowPlacement = ko.observable("overlay"); // values: 'adjacent' | 'overlay'
    self.currentNavArrowVisibility = ko.observable("visible"); // values: 'visible' | 'hidden' | 'auto'

    this.imageDataProvider = new ArrayDataProvider(
      [
        { id: "img1", url: "css/images/slide1.png", alt: "Photo 1" },
        { id: "img2", url: "css/images/slide2.png", alt: "Photo 2" },
        { id: "img3", url: "css/images/slide3.png", alt: "Photo 3" },
      ],
      { keyAttributes: "id" }
    );

    // // 🔹 Slider Images for the Bank Application
    // self.sliderImages = ko.observableArray([
    //   { src: "css/images/slide1.png", alt: "ATM Services" },
    //   { src: "css/images/slide2.png", alt: "Mobile Banking" },
    //   { src: "css/images/slide3.png", alt: "Investments" },
    // ]);

    // // 🔹 Current index in the slider
    // self.currentIndex = ko.observable(0);

    // // 🔹 Automatically cycle images every 3 seconds
    // setInterval(function () {
    //   let nextIndex = (self.currentIndex() + 1) % self.sliderImages().length;
    //   self.currentIndex(nextIndex);
    // }, 3000);

    // 🔹 Bank Services Data
    self.services = ko.observableArray([
      {
        title: "Savings Account",
        description: "Secure savings account with high interest rates.",
        icon: "css/images/savings.png",
      },
      {
        title: "Mobile Banking",
        description: "Bank anytime, anywhere with our mobile app.",
        icon: "css/images/mobile.png",
      },
      {
        title: "Loans & Credit",
        description: "Affordable loans and credit options for every need.",
        icon: "css/images/loan.png",
      },
      {
        title: "Investments",
        description: "Grow your wealth with smart investment plans.",
        icon: "css/images/invest.png",
      },
    ]);

    self.servicesDataProvider = new ArrayDataProvider(self.services, {
      keyAttributes: "title",
    });

    // 🔹 Navigate to Login Page
    self.goToLogin = function () {
      // Access the global router instance
      if (window.app && window.app.router) {
        window.app.router.go("login");
      } else {
        window.location.search = "?ojr=login";
      }
    };
  }

  return HomeViewModel;
});
