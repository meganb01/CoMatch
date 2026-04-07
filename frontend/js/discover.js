const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "cm_token";

let profiles = []; // list of profiles from backend
let currentIndex = 0;

// Get focus user from URL (if coming from recommendations)
const params = new URLSearchParams(window.location.search);
const focusUserId = params.get("focusUserId");

// DOM elements
const discoverCard = document.getElementById("discoverCard");
const founderAvatar = document.getElementById("founderAvatar");
const founderName = document.getElementById("founderName");
const founderSector = document.getElementById("founderSector");
const founderSkills = document.getElementById("founderSkills");
const founderBio = document.getElementById("founderBio");
const noMoreProfiles = document.getElementById("noMoreProfiles");

const passBtn = document.getElementById("passBtn");
const likeBtn = document.getElementById("likeBtn");

// Match modal
const matchModal = document.getElementById("matchModal");
const matchAvatar = document.getElementById("matchAvatar");
const matchName = document.getElementById("matchName");
const closeModalBtn = document.getElementById("closeModalBtn");

// Helpers
function renderProfile(profile) {
    if (!profile) {
        discoverCard.style.display = "none";
        noMoreProfiles.style.display = "block";
        return;
    }
    discoverCard.style.display = "block";
    noMoreProfiles.style.display = "none";

    founderAvatar.src = profile.avatarUrl || "https://i.pravatar.cc/150";
    founderName.textContent = profile.name || "No name";
    founderSector.textContent = profile.sector || "";
    founderBio.textContent = profile.bio || "";

    founderSkills.innerHTML = "";
    if (Array.isArray(profile.skills)) {
        profile.skills.forEach(skill => {
            const span = document.createElement("span");
            span.textContent = skill;
            founderSkills.appendChild(span);
        });
    }
}

// Fetch all discoverable profiles (excluding self)
async function loadProfiles() {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
        alert("Please log in.");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/profiles/discover`, {
            headers: { "Authorization": "Bearer " + token }
        });
        if (!res.ok) throw new Error("Failed to fetch profiles");
        profiles = await res.json();

        // If coming from recommendations, move that user to the front
        if (focusUserId) {
            const index = profiles.findIndex(p => p.id == focusUserId);

            if (index !== -1) {
                const [focusedUser] = profiles.splice(index, 1);
                profiles.unshift(focusedUser);
            }
        }
        
        currentIndex = 0;
        renderProfile(profiles[currentIndex]);
    } catch (err) {
        console.error(err);
        alert("Could not load profiles.");
    }
}

// Handle swipe actions
async function swipe(action) {
    if (currentIndex >= profiles.length) return;
    const profile = profiles[currentIndex];
    const token = localStorage.getItem(TOKEN_KEY);

    try {
        const res = await fetch(`${API_BASE}/api/profiles/swipe`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                targetUserId: profile.id,
                action // "like" or "pass"
            })
        });
        if (!res.ok) throw new Error("Swipe failed");

        const data = await res.json();
        if (data.match) showMatchModal(profile); // If mutual match

        currentIndex++;
        renderProfile(profiles[currentIndex]);
    } catch (err) {
        console.error(err);
        alert("Error processing swipe.");
    }
}

function showMatchModal(profile) {
    matchAvatar.src = profile.avatarUrl || "";
    matchName.textContent = profile.name || "";
    matchModal.style.display = "block";
}

closeModalBtn.addEventListener("click", () => {
    matchModal.style.display = "none";
});

// Button events
passBtn.addEventListener("click", () => swipe("pass"));
likeBtn.addEventListener("click", () => swipe("like"));

// Initialize
document.addEventListener("DOMContentLoaded", loadProfiles);