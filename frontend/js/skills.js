document.getElementById("skillsForm").addEventListener("submit", function (e){
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
    
    console.log("Selected skills:", selectedSkills);
    successDiv.textContent = "Skills saved successfully!";

    //To do later: send selectedSkills to backend via fetch 
});