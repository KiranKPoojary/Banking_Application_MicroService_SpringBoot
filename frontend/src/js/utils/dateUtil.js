define([], function () {
  "use strict";

  const dateUtil = {
    formatDateTime: function (isoDate, pattern = "dd-MMM-yyyy hh:mm a") {
      if (!isoDate) return "";

      let date = new Date(isoDate);

      // Map OJET-like pattern to Intl
      // (dd-MMM-yyyy hh:mm a → 02-Sep-2025 05:43 PM)
      return new Intl.DateTimeFormat("en-GB", {
        day: "2-digit",
        month: "short", // gives "Sep"
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true,
      }).format(date);
    },
  };

  return dateUtil;
});
