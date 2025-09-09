define([], function () {
  function parseJwt(token) {
    try {
      const base64Payload = token.split(".")[1];
      const payload = atob(base64Payload);
      console.log("Decoded JWT Payload:", payload);
      return JSON.parse(payload);
    } catch (e) {
      console.error("Invalid JWT:", e);
      return null;
    }
  }

  function isTokenValid(token) {
    const decoded = parseJwt(token);
    if (!decoded || !decoded.exp) return false;

    const now = Math.floor(Date.now() / 1000);
    return decoded.exp > now;
  }

  return {
    parseJwt: parseJwt,
    isTokenValid: isTokenValid,
  };
});
