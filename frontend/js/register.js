document.getElementById("registerForm").addEventListener("submit", async function (e) {
  e.preventDefault(); //prevents form from reloading page

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorDiv = document.getElementById("errorMessage");
    const successDiv = document.getElementById("successMessage");

    errorDiv.textContent = "";
    successDiv.textContent = "";

    //Validation
    if (!email) {
      errorDiv.textContent = "Email is required!";
      return;
    }
    if (!password) {
      errorDiv.textContent = "Password is required!";
      return;
    }

    //Email format check
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      errorDiv.textContent = "Please enter a valid email address.";
      return;
    }
    if(password.length < 8){
      errorDiv.textContent = "Password must be at least 8 characters long.";
      return;
    }

    try{
      //Call backend API
      const response = await fetch("http://localhost:8080/api/auth/register",{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({email, password}),
      });

      const data = await response.json();

      if(response.ok){
        sessionStorage.setItem("userId", data.userId);
        sessionStorage.setItem("email", data.email);

        successDiv.textContent = "Registration successful! Redirecting...";
        setTimeout(() => {
          window.location.href = "login.html";
        }, 1200);
      } else {
        errorDiv.textContent = data.error || "Registration failed.";
      }
    } catch (err) {
      console.error("Network/server error:", err);
      errorDiv.textContent = "Something went wrong. Check console.";
    }
});
