document.getElementById("loginForm").addEventListener("submit", async function (e){
    e.preventDefault();//Prevent page reload

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorDiv = document.getElementById("errorMessage");
    const successDiv = document.getElementById("successMessage");

    errorDiv.textContent = ""; 
    successDiv.textContent = "";

    //Validation
    if(!email){
        errorDiv.textContent = "Email is required!";
        return;
    }
    if(!password){
        errorDiv.textContent = "Password is required!";
        return;
    }

    try{
        const response = await fetch("http://localhost:8080/api/auth/login",{
            method: "POST",
            headers: {"Content-Type" : "application/json"},
            body: JSON.stringify({email, password}),
        });

        const data = await response.json().catch(() => ({}));

        if(response.ok){
            console.log("Logged in:", data);
            sessionStorage.setItem("userId", data.userId);
            sessionStorage.setItem("email", data.email);
            if (data.token) localStorage.setItem("cm_token", data.token);

            successDiv.textContent = "Login successful! Redirecting...";
            setTimeout(()=> {
                window.location.href = "profile.html";
            }, 1200);
        }else{
            //Show invalid credentials message from the backend
            errorDiv.textContent = data.error || "Login failed. Check credentials.";
        }
    }catch(err){
        console.error("Network/server error:", err);
        errorDiv.textContent = "Something went wrong. Check console.";
    }
});
