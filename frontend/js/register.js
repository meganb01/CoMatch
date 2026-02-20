document
  .getElementById("registerForm")
  .addEventListener("submit", async function (e) {
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
    if(password.length < 8){
      alert("Password must be at least 8 characters long.");
      return;
    }

    try{
      //Call backend API
      const response = await fetch("http://localhost:8080/api/auth/register",{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({email : email, password : password}),
      });

      const data = await response.json();

      if(response.ok){
        alert("Registration successful!");
        console.log("Registered user:", data);
        //Redirect to login page
        window.location.href = "login.html";
      }else{
        //Show error from backend
        document.getElementById("errorMessage").textContent = data.error || "Registration failed.";
        console.log("Error response:", data);
      }
    }catch(err){
      console.error("Network or server error", err);
      alert("Something went wrong. Check console.");
    }
  });
