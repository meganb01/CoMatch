const filterBtn = document.getElementById("filterBtn")

const countryInput = document.getElementById("countryInput")
const sectorInput = document.getElementById("sectorInput")
const skillInput = document.getElementById("skillInput")

const users = document.querySelectorAll(".user-card")


filterBtn.addEventListener("click", function () {

    const country = countryInput.value.toLowerCase()
    const sector = sectorInput.value.toLowerCase()
    const skill = skillInput.value.toLowerCase()

    users.forEach(function(user){

        const userCountry = user.dataset.country.toLowerCase()
        const userSector = user.dataset.sector.toLowerCase()
        const userSkills = user.dataset.skills.toLowerCase()

        if(
            (country === "" || userCountry.includes(country)) &&
            (sector === "" || userSector.includes(sector)) &&
            (skill === "" || userSkills.includes(skill))
        ){
            user.style.display = "block"
        }
        else{
            user.style.display = "none"
        }

    })

})