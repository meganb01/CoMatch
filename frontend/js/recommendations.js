const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "cm_token";
const container = document.getElementById("recommendationsList");

function displayRecommendations(users) {
  if (!container) return;
  container.innerHTML = "";

  if (!users || users.length === 0) {
    container.innerHTML = "<p class='muted'>No recommendations available. Make sure your profile is complete (skills, sector, country).</p>";
    return;
  }

  users.forEach(user => {
    const card = document.createElement("div");
    card.classList.add("rec-card");

    const scoreClass = user.score >= 70 ? "score-high" : user.score >= 40 ? "score-mid" : "score-low";

    const skillsHtml = Array.isArray(user.skills) && user.skills.length
      ? user.skills.map(s => `<span class="skill">${escapeHtml(s)}</span>`).join("")
      : "<span class='muted'>No skills listed</span>";

    card.innerHTML = `
      <div class="rec-header">
        <h3>${escapeHtml(user.name || "Unknown")}</h3>
        <span class="rec-score ${scoreClass}">${user.score}% match</span>
      </div>
      <p class="muted" style="margin:4px 0 8px;font-size:13px;">${escapeHtml(user.country || "")}${user.country && user.industry ? " · " : ""}${escapeHtml(user.industry || user.sector || "")}</p>
      <div class="rec-skills">${skillsHtml}</div>
      ${user.bio ? `<p class="rec-bio">${escapeHtml(user.bio)}</p>` : ""}
      <button class="primary-btn" style="margin-top:12px;padding:8px 16px;font-size:13px;"
        onclick="window.location.href='discover.html?focusUserId=${user.userId}'">
        View Profile
      </button>
    `;

    container.appendChild(card);
  });
}

function escapeHtml(s) {
  if (!s) return "";
  return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
}

async function loadRecommendations() {
  const token = localStorage.getItem(TOKEN_KEY);

  if (!token) {
    if (container) container.innerHTML = "<p class='muted'>Please <a href='login.html'>log in</a> to see recommendations.</p>";
    return;
  }

  if (container) container.innerHTML = "<p class='muted'>Loading recommendations...</p>";

  try {
    const res = await fetch(API_BASE + "/api/recommendations", {
      headers: { "Authorization": "Bearer " + token }
    });

    if (res.status === 401 || res.status === 403) {
      container.innerHTML = "<p class='muted'>Session expired. Please <a href='login.html'>log in again</a>.</p>";
      return;
    }

    if (!res.ok) throw new Error("Failed to load recommendations: " + res.status);

    const users = await res.json();
    displayRecommendations(users);
  } catch (error) {
    console.error("Error loading recommendations:", error);
    if (container) container.innerHTML = "<p class='muted'>Could not load recommendations. Make sure the backend is running.</p>";
  }
}

document.addEventListener("DOMContentLoaded", loadRecommendations);
