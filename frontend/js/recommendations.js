// Get container
const container = document.getElementById("recommendationsList");

// Display funcition
function displayRecommendations(users) {
  if (!container) return;

  container.innerHTML = "";

  if (users.length === 0) {
    container.innerHTML = "<p class='muted'>No recommendations yet</p>";
    return;
  }

  users.forEach(user => {
    const card = document.createElement("div");
    card.classList.add("profile-card");

    card.innerHTML = `
      <h3>${user.name}</h3>
      <p class="muted">${user.location || ""}</p>
      <p><strong>Skills:</strong> ${user.skills.join(", ")}</p>
      <p><strong>Sector:</strong> ${user.sector}</p>
      <p><strong>Match Score:</strong> ${user.score}%</p>
      ${user.score > 80 ? "<p><strong>Top Match</strong></p>" : ""}
    `;

    container.appendChild(card);
  });
}

// AI scoring function
function calculateScore(currentUser, otherUser) {
  let score = 0;

  // Shared skills
  const commonSkills = currentUser.skills.filter(skill =>
    otherUser.skills.includes(skill)
  );
  score += commonSkills.length * 20;

  // Same sector
  if (currentUser.sector === otherUser.sector) {
    score += 30;
  }

  // Same location
  if (currentUser.location === otherUser.location) {
    score += 10;
  }

  return Math.min(score, 100);
}

// Load recommendations
async function loadRecommendations() {
  try {
    const token = localStorage.getItem("token");

    if (!token) {
      console.error("No auth token found");
      return;
    }

    // Temp: current user (replacing later)
    const currentUser = {
      skills: ["Frontend", "UI/UX"],
      sector: "EdTech",
      location: "Ireland"
    };

    // Temp: other users (replacing later)
    const users = [
      {
        name: "John Doe",
        location: "Ireland",
        skills: ["Frontend", "UI/UX"],
        sector: "EdTech"
      },
      {
        name: "Sarah Smith",
        location: "UK",
        skills: ["Marketing", "Sales"],
        sector: "FinTech"
      },
      {
        name: "Alex Chen",
        location: "Ireland",
        skills: ["Frontend", "Marketing"],
        sector: "EdTech"
      }
    ];

    // Calculate scores
    const scoredUsers = users.map(user => ({
      ...user,
      score: calculateScore(currentUser, user)
    }));

    // Sort by best match
    scoredUsers.sort((a, b) => b.score - a.score);

    displayRecommendations(scoredUsers);

  } catch (error) {
    console.error("Error loading recommendations:", error);
  }
}

loadRecommendations();