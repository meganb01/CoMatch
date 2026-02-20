document.getElementById("loginForm").addEventListener("submit", async function (e){
    e.preventDefault();//Prevent page reload

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorDiv = document.getElementById("errorMessage");

    errorDiv.textContent = ""; //Clear previous error messages

    //Basic client-side validation
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

        const data = await response.json();

        if(response.ok){
            console.log("Logged in:", data);
            //Save userId and email for session
            sessionStorage.setItem("userId", data.userId);
            sessionStorage.setItem("email", data.email);  
            //Redirect to dashboard or profile page   
            window.location.href = "profile.html";        
        }else{
            //Show invalid credentials message from the backend
            errorDiv.textContent = data.error || "Login failed. Check credentials.";
        }
    }catch(err){
        console.error("Network/server error:", err);
        errorDiv.textContent = "Something went wrong. Check console.";
    }
});
