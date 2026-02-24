// profile.js (complete, merged)
// Configure API 
const API_BASE = ""; //  "http://localhost:8080"
const PROFILE_GET = API_BASE ? API_BASE + "/api/profile/me" : "";
const PROFILE_PUT = API_BASE ? API_BASE + "/api/profile" : "";
const TOKEN_KEY = "cm_token"; // need change later

// DOM elements (support both naming patterns you might have)
const viewMode = document.getElementById("viewMode");
const editMode = document.getElementById("editMode");

const editBtn = document.getElementById("editBtn") || document.querySelector(".edit-btn");
const saveBtn = document.getElementById("saveBtn");
const cancelBtn = document.getElementById("cancelBtn");
const logoutBtn = document.getElementById("logoutBtn");

// View elements
const viewAvatar = document.getElementById("viewAvatar") || document.getElementById("viewAvatarImg");
const viewName = document.getElementById("viewName");
const viewCountry = document.getElementById("viewCountry");
const viewSector = document.getElementById("viewSector");
const viewSkills = document.getElementById("viewSkills");
const viewBio = document.getElementById("viewBio");

// Edit elements / form
const avatarInput = document.getElementById("avatarInput");
const avatarPreview = document.getElementById("avatarPreview") || document.getElementById("editAvatarPreview");
const nameInput = document.getElementById("nameInput");
const countryInput = document.getElementById("countryInput");
const sectorInput = document.getElementById("sectorInput");
const skillsInput = document.getElementById("skillsInput");
const skillsContainer = document.getElementById("skillsContainer"); // optional enhanced tag UI
const bioInput = document.getElementById("bioInput");
const bioCount = document.getElementById("bioCount");

let avatarFile = null;
let skills = [];

// Helpers
const qs = id => document.getElementById(id);
const escapeHtml = s => (s == null ? "" : String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])));

// UI toggles
function showEditMode(){
  if (viewMode) viewMode.classList.add("hidden");
  if (editMode) editMode.classList.remove("hidden");
}
function showViewMode(){
  if (editMode) editMode.classList.add("hidden");
  if (viewMode) viewMode.classList.remove("hidden");
}

// Avatar preview handler
if (avatarInput) {
  avatarInput.addEventListener("change", (e) => {
    const f = e.target.files[0];
    if (!f) return;
    if (f.size > 5 * 1024 * 1024) {
      alert("Image too large (max 5MB).");
      avatarInput.value = "";
      return;
    }
    avatarFile = f;
    const url = URL.createObjectURL(f);
    if (avatarPreview) avatarPreview.src = url;
    if (viewAvatar) viewAvatar.src = url;
  });
}

// Skills input: support comma-separated input when saving, and optional "Enter" behavior
if (skillsInput) {
  skillsInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter" || e.key === ",") {
      e.preventDefault();
      addSkillFromInput();
    } else if (e.key === "Backspace" && skillsInput.value.trim() === "") {
      skills.pop();
      renderSkillsEdit();
    }
  });
}

function addSkillFromInput(){
  if (!skillsInput) return;
  const raw = skillsInput.value.trim().replace(/,+$/,'');
  if (!raw) return;
  const parts = raw.split(",").map(s => s.trim()).filter(Boolean);
  for (const p of parts) {
    if (!skills.includes(p)) skills.push(p);
  }
  skillsInput.value = "";
  renderSkillsEdit();
}

function renderSkillsEdit(){
  if (!skillsContainer) return;
  skillsContainer.innerHTML = "";
  for (const s of skills) {
    const span = document.createElement("span");
    span.className = "tag";
    span.innerHTML = `${escapeHtml(s)} <button type="button" aria-label="Remove ${escapeHtml(s)}">✕</button>`;
    const btn = span.querySelector("button");
    btn.addEventListener("click", () => {
      skills = skills.filter(x => x !== s);
      renderSkillsEdit();
    });
    skillsContainer.appendChild(span);
  }
}

function renderSkillsView(){
  if (!viewSkills) return;
  viewSkills.innerHTML = "";
  for (const s of skills) {
    const span = document.createElement("span");
    span.className = "skill";
    span.textContent = s;
    viewSkills.appendChild(span);
  }
}

// Bio counter
if (bioInput && bioCount) {
  bioCount.textContent = `${bioInput.value.length}/100`;
  bioInput.addEventListener("input", () => {
    bioCount.textContent = `${bioInput.value.length}/100`;
  });
}

// Populate both view and edit from a user object
function populateFormAndView(u){
  const user = u || {};
  if (nameInput) nameInput.value = user.name || "";
  if (countryInput) countryInput.value = user.country || "";
  if (sectorInput) sectorInput.value = user.sector || "";
  if (bioInput) bioInput.value = user.bio || "";
  if (bioCount) bioCount.textContent = `${(bioInput && bioInput.value.length) || 0}/100`;

  skills = Array.isArray(user.skills) ? user.skills.slice() :
           (typeof user.skills === "string" ? user.skills.split(",").map(s => s.trim()).filter(Boolean) : []);
  if (skillsInput && skillsInput.value.trim() === "") skillsInput.value = skills.join(", ");

  if (avatarPreview && user.avatarUrl) avatarPreview.src = user.avatarUrl;
  if (viewAvatar && user.avatarUrl) viewAvatar.src = user.avatarUrl;

  // view side
  if (viewName) viewName.textContent = user.name || "No name";
  if (viewCountry) viewCountry.textContent = user.country || "";
  if (viewSector) viewSector.textContent = user.sector || "";
  if (viewBio) viewBio.textContent = user.bio || "";
  renderSkillsEdit();
  renderSkillsView();
}

// Load profile: try token/backend or fallback to local mock
async function loadProfile(){
  // try localStorage saved profile first
  const saved = localStorage.getItem("mock_profile");
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      populateFormAndView(parsed);
      return;
    } catch(e) { /* ignore parse errors */ }
  }

  // If API configured and token exists, attempt fetch
  const token = localStorage.getItem(TOKEN_KEY);
  if (API_BASE && token) {
    try {
      const res = await fetch(PROFILE_GET, {
        headers: { "Authorization": "Bearer " + token }
      });
      if (res.ok) {
        const json = await res.json();
        const user = json.user || json;
        populateFormAndView(user);
        return;
      }
    } catch (err) {
      console.warn("Profile fetch failed:", err);
    }
  }

  // fallback mock
  const mock = {
    name: "Alex Chen",
    country: "Ireland",
    sector: "Tech, AI",
    skills: ["Product Management", "Marketing"],
    bio: "Looking for a technical co-founder.",
    avatarUrl: "https://i.pravatar.cc/150?img=12"
  };
  populateFormAndView(mock);
}

// Save handler: update view and persist locally or send to API
async function saveProfile(){
  // simple validation
  const name = (nameInput && nameInput.value.trim()) || "";
  if (!name) {
    alert("Name is required.");
    return;
  }

  const payload = {
    name,
    country: (countryInput && countryInput.value) || "",
    sector: (sectorInput && sectorInput.value) || "",
    skills,
    bio: (bioInput && bioInput.value) || "",
    avatarUrl: null
  };

  // if avatarFile exists and API accepts multipart, send FormData; otherwise persist locally
  const token = localStorage.getItem(TOKEN_KEY);
  if (API_BASE && token) {
    // attempt multipart PUT
    try {
      const form = new FormData();
      form.append("name", payload.name);
      form.append("country", payload.country);
      form.append("sector", payload.sector);
      form.append("bio", payload.bio);
      form.append("skills", JSON.stringify(payload.skills));
      if (avatarFile) form.append("avatar", avatarFile);

      const res = await fetch(PROFILE_PUT, {
        method: "PUT",
        headers: { "Authorization": "Bearer " + token },
        body: form
      });

      if (!res.ok) {
        alert("Save failed: " + res.statusText);
        return;
      }
      const data = await res.json();
      // update view with response if provided
      populateFormAndView(data.user || data);
      showViewMode();
      alert("Profile saved.");
      return;
    } catch (err) {
      console.error("Save failed", err);
      alert("Network error while saving. Saved locally instead.");
    }
  }

  // Local save fallback
  // Use current avatarPreview src if file not uploaded to backend
  payload.avatarUrl = (avatarPreview && avatarPreview.src) || (viewAvatar && viewAvatar.src) || "";
  localStorage.setItem("mock_profile", JSON.stringify(payload));
  populateFormAndView(payload);
  showViewMode();
  alert("Profile saved locally.");
}

// Event wiring (defensive: check elements exist)
document.addEventListener("DOMContentLoaded", () => {
  // load profile data to page
  loadProfile();

  if (editBtn) editBtn.addEventListener("click", showEditMode);
  if (saveBtn) saveBtn.addEventListener("click", (e) => { e.preventDefault(); saveProfile(); });
  if (cancelBtn) cancelBtn.addEventListener("click", (e) => { e.preventDefault(); showViewMode(); loadProfile(); });
  if (logoutBtn) logoutBtn.addEventListener("click", () => {
    // logout behaviour (frontend-only)
    localStorage.removeItem(TOKEN_KEY);
    alert("Logged out");
    // optionally redirect: location.href = "/login.html";
  });
});