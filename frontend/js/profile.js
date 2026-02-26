// profile.js (complete, merged)
// Configure API 
const API_BASE = "http://localhost:8080";
const PROFILE_GET = API_BASE + "/api/profile/me";
const PROFILE_POST = API_BASE + "/api/profile";
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
    if (skills.length >= 10) {
      alert("Maximum 10 skills allowed");
      break;
    }
      if (!skills.includes(p)) skills.push(p);
    }
    skillsInput.value = "";
   renderSkillsEdit();
}

const ALL_SKILLS = [
  "Marketing","Development","Finance","Design","Sales",
  "Product Management","HR","Customer Support","Legal","Operations",
  "Data Science","QA","Marketing Research","Business Development",
  "UI/UX","Content Writing","SEO","Social Media","Analytics","Blockchain"
];

function renderSkillsEdit() {
  if (!skillsContainer) return;
  skillsContainer.innerHTML = "";

  ALL_SKILLS.forEach(skill => {
    const label = document.createElement("label");
    label.className = "tag";

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.value = skill;
    if (skills.includes(skill)) checkbox.checked = true;

    const span = document.createElement("span");
    span.textContent = skill;

    checkbox.addEventListener("change", () => {
      if (checkbox.checked) {
        if (skills.length >= 10) {
          alert("Maximum 10 skills allowed");
          checkbox.checked = false; // undo the check
          return;
        }
        if (!skills.includes(skill)) skills.push(skill);
      } else {
        skills = skills.filter(s => s !== skill);
      }
      renderSkillsView();
    });

    label.appendChild(checkbox);
    label.appendChild(span);
    skillsContainer.appendChild(label);
  });
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

// Get display name from logged-in user's email (e.g. "demo@in.com" → "demo")
function getDisplayNameFromEmail() {
  const email = sessionStorage.getItem("email") || "";
  if (!email) return "User";
  const part = email.split("@")[0];
  return (part && part.trim()) ? part.trim() : "User";
}

// Load profile: when logged in, try API first so we show your name (from email) not old "Alex" from localStorage
async function loadProfile(){
  const token = localStorage.getItem(TOKEN_KEY);

  // Logged in: try backend first so we never show old mock_profile "Alex Chen"
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
      // 400 = profile not found (new user): show name from email
    } catch (err) {
      console.warn("Profile fetch failed:", err);
    }
    // Show logged-in user's name from email (e.g. demo@in.com → "demo")
    const displayName = getDisplayNameFromEmail();
    populateFormAndView({
      name: displayName,
      country: "",
      sector: "",
      skills: [],
      bio: "",
      avatarUrl: "https://i.pravatar.cc/150?img=12"
    });
    return;
  }

  // Not logged in: try localStorage saved profile
  const saved = localStorage.getItem("mock_profile");
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      populateFormAndView(parsed);
      return;
    } catch(e) { /* ignore */ }
  }

  // Final fallback
  const displayName = getDisplayNameFromEmail();
  populateFormAndView({
    name: displayName,
    country: "",
    sector: "",
    skills: [],
    bio: "",
    avatarUrl: "https://i.pravatar.cc/150?img=12"
  });
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
    try {
      const body = {
        name: payload.name,
        country: payload.country,
        industry: payload.sector || "",
        bio: payload.bio,
        skills: payload.skills
      };

      const res = await fetch(PROFILE_POST, {
        method: "POST",
        headers: {
          "Authorization": "Bearer " + token,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
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
    window.location.href = "login.html";
  });
});