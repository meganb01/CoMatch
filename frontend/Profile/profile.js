// profilE.js

// To Configure endpoints / token key to match app:
const API_BASE = ""; // e.g. "http://localhost:8080"
const PROFILE_GET = API_BASE + "/api/profile/me";
const PROFILE_PUT = API_BASE + "/api/profile"; // ex,
const TOKEN_KEY = "cm_token"; // change if  auth uses a different key

// Elements
const avatarInput = document.getElementById("avatarInput");
const avatarPreview = document.getElementById("avatarPreview");
const fullNameEl = document.getElementById("fullName");
const countryEl = document.getElementById("country");
const sectorEl = document.getElementById("sector");
const skillInput = document.getElementById("skillInput");
const skillsContainer = document.getElementById("skillsContainer");
const bioEl = document.getElementById("bio");
const bioCount = document.getElementById("bioCount");
const profileForm = document.getElementById("profileForm");
const messageEl = document.getElementById("message");
const saveBtn = document.getElementById("saveBtn");
const cancelBtn = document.getElementById("cancelBtn");

// local state
let skills = [];
let avatarFile = null;

// helper: escape
function escapeHtml(s){ if(!s) return ""; return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }

// preview image when user chooses file
avatarInput.addEventListener("change", (e) => {
  const file = e.target.files[0];
  if(!file) return;
  if(file.size > 5 * 1024 * 1024){
    showMessage("Image too large (max 5MB).", true);
    avatarInput.value = "";
    return;
  }
  avatarFile = file;
  const url = URL.createObjectURL(file);
  avatarPreview.src = url;
});

// add skill on Enter or comma
skillInput.addEventListener("keydown", (e) => {
  if(e.key === "Enter" || e.key === ","){
    e.preventDefault();
    addSkillFromInput();
  } else if(e.key === "Backspace" && skillInput.value === ""){
    // remove last
    skills.pop();
    renderSkills();
  }
});

function addSkillFromInput(){
  const v = skillInput.value.trim().replace(/,+$/,'');
  if(!v) return;
  if(!skills.includes(v)){
    skills.push(v);
    renderSkills();
  }
  skillInput.value = "";
}

// render skill tags
function renderSkills(){
  skillsContainer.innerHTML = "";
  for(const s of skills){
    const tag = document.createElement("span");
    tag.className = "tag";
    tag.innerHTML = `${escapeHtml(s)} <button aria-label="Remove ${escapeHtml(s)}">✕</button>`;
    const btn = tag.querySelector("button");
    btn.addEventListener("click", () => {
      skills = skills.filter(x => x !== s);
      renderSkills();
    });
    skillsContainer.appendChild(tag);
  }
}

// bio counter
bioEl.addEventListener("input", () => {
  bioCount.textContent = `${bioEl.value.length}/100`;
});

// show message
function showMessage(msg, isError=false){
  messageEl.textContent = msg;
  messageEl.style.color = isError ? "#ffb3b3" : "var(--muted)";
}

// load current profile (fallback if no token / server)
async function loadProfile(){
  showMessage("Loading profile...");
  const token = localStorage.getItem(TOKEN_KEY);
  if(!token){
    showMessage("No token found — loading mock data. (Log in to load real profile.)");
    loadMock();
    return;
  }

  try {
    const res = await fetch(PROFILE_GET, {
      headers: { "Authorization": "Bearer " + token }
    });
    if(!res.ok){
      showMessage("Failed to load profile from server. Loading mock data.");
      loadMock();
      return;
    }
    const json = await res.json();
    const user = json.user || json;
    populateForm(user);
    showMessage("Profile loaded.");
  } catch(err){
    console.error(err);
    showMessage("Network error — loading mock data.");
    loadMock();
  }
}

// mock user if backend not available
function loadMock(){
  const mock = {
    name: "Alex Chen",
    country: "Ireland",
    sector: "Tech, AI",
    skills: ["Product Management", "Marketing"],
    bio: "Looking for a technical co-founder."
  };
  populateForm(mock);
}

// fill form fields
function populateForm(u){
  fullNameEl.value = u.name || "";
  countryEl.value = u.country || "";
  sectorEl.value = u.sector || "";
  skills = Array.isArray(u.skills) ? u.skills.slice() : (u.skills ? u.skills.split(",").map(s => s.trim()).filter(Boolean) : []);
  bioEl.value = u.bio || "";
  bioCount.textContent = `${bioEl.value.length}/100`;
  renderSkills();
  if(u.avatarUrl) avatarPreview.src = u.avatarUrl;
}

// submit handlersend to backend (FormData for file)
profileForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  saveBtn.disabled = true;
  showMessage("Saving...");
  // simple client-side validation
  if(!fullNameEl.value.trim()){
    showMessage("Full name is required.", true);
    saveBtn.disabled = false;
    return;
  }

  // prepare data
  // We'll use FormData so we can include image file if there is one
  const fd = new FormData();
  fd.append("name", fullNameEl.value.trim());
  fd.append("country", countryEl.value);
  fd.append("sector", sectorEl.value);
  fd.append("bio", bioEl.value);
  fd.append("skills", JSON.stringify(skills));
  if(avatarFile) fd.append("avatar", avatarFile);

  const token = localStorage.getItem(TOKEN_KEY);

  // If token missing, save to localStorage as mock
  if(!token){
    // mock save locally so you can test
    localStorage.setItem("mock_profile", JSON.stringify({
      name: fullNameEl.value.trim(),
      country: countryEl.value,
      sector: sectorEl.value,
      skills,
      bio: bioEl.value,
      avatarUrl: avatarPreview.src,
      savedAt: new Date().toISOString()
    }));
    showMessage("No token — profile saved locally (mock).");
    saveBtn.disabled = false;
    return;
  }

  try {
    const res = await fetch(PROFILE_PUT, {
      method: "PUT",
      headers: {
        // NOTE: do NOT set content-type for FormData; browser will set boundary automatically
        "Authorization": "Bearer " + token
      },
      body: fd
    });

    if(!res.ok){
      const text = await res.text();
      console.error("Failed update:", res.status, text);
      showMessage("Save failed: " + res.statusText, true);
      saveBtn.disabled = false;
      return;
    }

    const json = await res.json();
    showMessage("Profile saved successfully.");
   
    if(json.avatarUrl) avatarPreview.src = json.avatarUrl;
  } catch(err){
    console.error(err);
    showMessage("Network error while saving.", true);
  } finally {
    saveBtn.disabled = false;
  }
});

cancelBtn.addEventListener("click", (e) => {
  // reload originaldata (or navigate away)
  loadProfile();
});

// init
document.addEventListener("DOMContentLoaded", () => {
  loadProfile();
});