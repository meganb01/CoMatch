// Get container
const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "cm_token";
const container = document.getElementById("recommendationsList");

//Get current user data (used for AI matching)
function getCurrentUser() {
  return {
    skills: JSON.parse(localStorage.getItem("skills")) || [],
    sector: localStorage.getItem("sector") || "",
    location: localStorage.getItem("country") || ""
  };
}

// AI scoring function: calculates compatibility between users
function calculateScore(currentUser, otherUser) {
  let score = 0;

  if (!currentUser || !otherUser) return 0;

  // Shared skills
  if (currentUser.skills && otherUser.skills) {
    const commonSkills = currentUser.skills.filter(skill =>
      otherUser.skills.includes(skill)
    );
    score += commonSkills.length * 20;
  }

  // Bonus if users are in same sector
  if (currentUser.sector && otherUser.sector) {
    if (currentUser.sector === otherUser.sector) {
      score += 30;
    }
  }

  // Small bonus if users are in same location
  if (currentUser.location && otherUser.location) {
    if (currentUser.location === otherUser.location) {
      score += 10;
    }
  }

  //Cap score at 100
  return Math.min(score, 100);
}

// Fetch users and generate AI-ranked recommendations
async function loadRecommendations() {
  try {
    const token = localStorage.getItem(TOKEN_KEY);
    const currentUser = getCurrentUser();

    if (!token) {
      console.error("No token found");
      return;
    }

    const res = await fetch(`${API_BASE}/api/profiles/discover`, {
      headers: {
        "Authorization": "Bearer " + token
      }
    });

    if (!res.ok) {
      throw new Error("Failed to fetch profiles");
    }

    const users = await res.json();

    // Apply AI scoring to each user
    const scoredUsers = users.map(user => ({
      ...user,
      score: calculateScore(currentUser, user)
    }));

    // Sort users by best match (highest score first)
    scoredUsers.sort((a, b) => b.score - a.score);

    displayRecommendations(scoredUsers);

  } catch (err) {
    console.error("AI error:", err);
  }
}

// Render recommendations to UI
function displayRecommendations(users) {
  if (!container) return;

  container.innerHTML = "";

  if (users.length === 0) {
    container.innerHTML = "<p class='muted'>No recommendations yet</p>";
    return;
  }

  users.forEach(user => {
    const div = document.createElement("div");
    div.classList.add("profile-card");

    div.innerHTML =  `
      <h3>${user.name || "No name"}</h3>
      <p>${user.sector || ""}</p>
      <p>Match Score: ${user.score}%</p>
    `;

    container.appendChild(div);
  });
}

// Run on page load
document.addEventListener("DOMContentLoaded", loadRecommendations);