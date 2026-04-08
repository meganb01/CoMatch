// Get container
const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "cm_token";
const container = document.getElementById("recommendationsList");

//Get current user data (used for AI matching)
function getCurrentUser() {
  const data = localStorage.getItem("mock_profile");
  if (!data) return null;

  const user = JSON.parse(data);

  return {
    skills: user.skills || [],
    sector: user.sector || "",
    location: user.country || ""
  };
}

// AI scoring function: calculates compatibility between users
function calculateScore(currentUser, otherUser) {
  let score = 0;
  let reasons = [];

  if (!currentUser || !otherUser){
  return{score: 0, reasons: [] };
  }

  // Shared skills
  if (currentUser.skills && otherUser.skills) {
    const commonSkills = currentUser.skills.filter(skill =>
      otherUser.skills.includes(skill)
    );

    if(commonSkills.length > 0){
      score += commonSkills.length * 20;
      reasons.push(`${commonSkills.length} shared skill(s)`);
    }
  }

  // Bonus if users are in same sector
  if (currentUser.sector && otherUser.sector) {
    if (currentUser.sector === otherUser.sector) {
      score += 30;
      reasons.push("Same sector");
    }
  }

  // Small bonus if users are in same location
  if (currentUser.location && otherUser.location) {
    if (currentUser.location === otherUser.location) {
      score += 10;
      reasons.push("Same location");
    }
  }

  //Cap score at 100
  return{
    score: Math.min(score, 100),
    reasons
  };
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
    const scoredUsers = users.map(user => {
      const result = calculateScore(currentUser, user);
      return{
        ...user,
        score: result.score,
        reasons: result.reasons
      };
    });

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
    div.classList.add("profile-card", "recommendation-card");

    div.innerHTML =  `
      <h3>${user.name || "No name"}</h3>
      <p>${user.sector || ""}</p>
      <p class="match-score">
      ${
        user.score >= 60 ? "🔥 Strong Match" :
        user.score >= 30 ? "👍 Good Match" :
        user.score > 0 ? `Match Score: ${user.score}%` :
        "Low Compatibility"
      }
      (${user.score}%)
      </p>
      <p class="muted">${user.reasons.join(" • ")}</p>
    `;

    //Make clickable (goes to discover page)
  div.style.cursor = "pointer";
  div.addEventListener("click", () => {
    window.location.href = `discover.html?focusUserId=${user.id}`;
  });

    container.appendChild(div);
  });
}

// Run on page load
document.addEventListener("DOMContentLoaded", loadRecommendations);