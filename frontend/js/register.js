document
  .getElementById("registerForm")
  .addEventListener("submit", function (e) {
    e.preventDefault(); //prevents form from reloading page

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    //Basic client-side validation
    if (!email) {
      alert("Email is required!");
      return;
    }
    if (!password) {
      alert("Password is required!");
      return;
    }
    //Simple email format check
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      alert("Please enter a valid email address.");
      return;
    }

    console.log("Email:", email);
    console.log("Password:", password);

    //TO-DO: Connect to backend API later.

    alert("Form Submitted! Check console for values.");
  });
