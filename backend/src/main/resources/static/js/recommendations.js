const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "cm_token";

const container = document.getElementById("recommendationsList");

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function escapeHtml(s) {
  if (!s) return "";
  return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
}

function displayRecommendations(users) {
  if (!container) return;

  container.innerHTML = "";

  if (users.length === 0) {
    container.innerHTML = "<p class='muted'>No recommendations yet. Create your profile first!</p>";
    return;
  }

  users.forEach(function(user) {
    var card = document.createElement("div");
    card.className = "rec-card";

    var skillsHtml = "";
    if (user.skills && user.skills.length > 0) {
      skillsHtml = user.skills.map(function(s) {
        return '<span class="skill">' + escapeHtml(s) + '</span>';
      }).join("");
    }

    var scoreClass = user.score >= 70 ? "score-high" : (user.score >= 40 ? "score-mid" : "score-low");

    card.innerHTML =
      '<div class="rec-header">' +
        '<h3>' + escapeHtml(user.name) + '</h3>' +
        '<span class="rec-score ' + scoreClass + '">' + user.score + '% Match</span>' +
      '</div>' +
      '<p class="muted">' + escapeHtml(user.country || "") +
        (user.industry ? ' · ' + escapeHtml(user.industry) : '') +
        (user.startupStage ? ' · ' + escapeHtml(user.startupStage) : '') +
      '</p>' +
      '<div class="rec-skills">' + skillsHtml + '</div>' +
      '<p class="rec-bio">' + escapeHtml(user.bio || "") + '</p>';

    container.appendChild(card);
  });
}

async function loadRecommendations() {
  var token = getToken();

  if (!token) {
    if (container) container.innerHTML = "<p class='muted'>Please <a href='login.html'>log in</a> to see recommendations.</p>";
    return;
  }

  if (container) container.innerHTML = "<p class='muted'>Loading recommendations...</p>";

  try {
    var res = await fetch(API_BASE + "/api/recommendations", {
      headers: { "Authorization": "Bearer " + token }
    });

    if (!res.ok) throw new Error("Failed to load recommendations");

    var data = await res.json();
    displayRecommendations(data);
  } catch (err) {
    console.error("Error loading recommendations:", err);
    if (container) container.innerHTML = "<p class='muted'>Could not load recommendations.</p>";
  }
}

document.addEventListener("DOMContentLoaded", loadRecommendations);
