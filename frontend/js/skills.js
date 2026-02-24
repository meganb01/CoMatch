//Page protection: redirect if not logged in (temporarily commented out to allow testing of skills page first)
//const userId = sessionStorage.getItem("userId");
//if(!userId){
//    alert("You must be logged in to access this page.");
//    window.location.href = "login.html";
//}

document.getElementById("skillsForm").addEventListener("submit", async function (e){
    e.preventDefault();

    const checkboxes = document.querySelectorAll(".skills-list input[type='checkbox']");
    const selectedSkills =[];
    const errorDiv = document.getElementById("errorMessage");
    const successDiv = document.getElementById("successMessage");

    errorDiv.textContent = "";
    successDiv.textContent = "";
    
    checkboxes.forEach(cb =>{
        if(cb.checked) selectedSkills.push(cb.value);
    });

    //Validation: Maximum 10 Skills
    if(selectedSkills.length > 10){
        errorDiv.textContent = "You can select a maximum of 10 skills.";
        return;
    }
    //Validation: At least 1 skill
    if(selectedSkills.length ===0){
        errorDiv.textContent = "Please select at least one skill.";
        return;
    }

    const userId = sessionStorage.getItem("userId");
    if(!userId){
        errorDiv.textContent = "You must be logged in to save skills.";
        return;
    }

    try{
        const response = await fetch(`http://localhost:8080/api/users/${userId}/skills`,{
            method: "POST",
            headers: {"Content-Type" : "application/json"},
            body: JSON.stringify({skills: selectedSkills})
        });

        const data = await response.json();

        if(response.ok){
            successDiv.textContent = "Skills saved successfully!";
            console.log("Saved skills:", data);
        }else{
            errorDiv.textContent = data.error || "Failed to save skills.";
        }
    }catch(err){
        console.error("Network/server error:", err);
        errorDiv.textContent = "Something went wrong. Check console.";
    }
});
    