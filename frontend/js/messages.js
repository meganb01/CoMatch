const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "cm_token";

const chatList = document.getElementById("chatList");
const chatMessages = document.getElementById("chatMessages");
const chatName = document.getElementById("chatName");
const chatStatus = document.getElementById("chatStatus");
const messageForm = document.getElementById("messageForm");
const messageInput = document.getElementById("messageInput");

let matches = [];
let activeMatchId = null;
let myUserId = Number(sessionStorage.getItem("userId")) || null;
let pollTimer = null;

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function authHeaders() {
  return {
    "Authorization": "Bearer " + getToken(),
    "Content-Type": "application/json"
  };
}

async function loadMatches() {
  const token = getToken();
  if (!token) {
    chatList.innerHTML = '<p class="no-matches-msg">Please <a href="login.html">log in</a> first.</p>';
    return;
  }

  try {
    const res = await fetch(API_BASE + "/api/matches", {
      headers: authHeaders()
    });

    if (!res.ok) throw new Error("Failed to load matches");

    matches = await res.json();

    if (matches.length === 0) {
      chatList.innerHTML = '<p class="no-matches-msg">No matches yet. Go discover founders!</p>';
      return;
    }

    renderChatList();

    if (!activeMatchId && matches.length > 0) {
      activeMatchId = matches[0].matchId;
      renderChatList();
      openChat(activeMatchId);
    }
  } catch (err) {
    console.error("Error loading matches:", err);
    chatList.innerHTML = '<p class="no-matches-msg">Could not load matches.</p>';
  }
}

function renderChatList() {
  chatList.innerHTML = "";

  matches.forEach(function(match) {
    var item = document.createElement("div");
    item.className = "chat-item" + (match.matchId === activeMatchId ? " active" : "");

    var name = (match.profile && match.profile.name) || "Unknown";
    var industry = (match.profile && match.profile.industry) || "";

    item.innerHTML = '<h3>' + escapeHtml(name) + '</h3><p>' + escapeHtml(industry) + '</p>';

    item.addEventListener("click", function() {
      activeMatchId = match.matchId;
      renderChatList();
      openChat(match.matchId);
    });

    chatList.appendChild(item);
  });
}

async function openChat(matchId) {
  var match = matches.find(function(m) { return m.matchId === matchId; });
  var name = (match && match.profile && match.profile.name) || "Chat";
  var industry = (match && match.profile && match.profile.industry) || "";

  chatName.textContent = name;
  chatStatus.textContent = industry;

  await loadMessages(matchId);
  startPolling(matchId);
}

async function loadMessages(matchId) {
  try {
    var res = await fetch(API_BASE + "/api/matches/" + matchId + "/messages?page=0&size=50", {
      headers: authHeaders()
    });

    if (!res.ok) throw new Error("Failed to load messages");

    var data = await res.json();
    var messages = data.content || [];

    renderMessages(messages);
  } catch (err) {
    console.error("Error loading messages:", err);
    chatMessages.innerHTML = '<p class="no-matches-msg">Could not load messages.</p>';
  }
}

function renderMessages(messages) {
  chatMessages.innerHTML = "";

  if (messages.length === 0) {
    chatMessages.innerHTML = '<p class="no-matches-msg">No messages yet. Say hello!</p>';
    return;
  }

  messages.forEach(function(msg) {
    var isMine = msg.senderUserId === myUserId;

    var row = document.createElement("div");
    row.className = "message-row " + (isMine ? "me" : "them");

    var bubble = document.createElement("div");
    bubble.className = "message-bubble";
    bubble.textContent = msg.body;

    var meta = document.createElement("div");
    meta.className = "message-meta";
    var senderLabel = isMine ? "You" : (msg.senderName || "Them");
    var time = msg.createdAt ? formatTime(msg.createdAt) : "";
    meta.textContent = senderLabel + (time ? " · " + time : "");

    row.appendChild(bubble);
    row.appendChild(meta);
    chatMessages.appendChild(row);
  });

  chatMessages.scrollTop = chatMessages.scrollHeight;
}

messageForm.addEventListener("submit", async function(e) {
  e.preventDefault();

  var text = messageInput.value.trim();
  if (!text || !activeMatchId) return;

  messageInput.value = "";

  try {
    var res = await fetch(API_BASE + "/api/matches/" + activeMatchId + "/messages", {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ body: text })
    });

    if (!res.ok) throw new Error("Send failed");

    await loadMessages(activeMatchId);
  } catch (err) {
    console.error("Error sending message:", err);
    alert("Failed to send message.");
  }
});

function startPolling(matchId) {
  if (pollTimer) clearInterval(pollTimer);
  pollTimer = setInterval(function() {
    if (activeMatchId === matchId) {
      loadMessages(matchId);
    }
  }, 5000);
}

function formatTime(isoStr) {
  try {
    var d = new Date(isoStr);
    return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  } catch (e) {
    return "";
  }
}

function escapeHtml(s) {
  if (!s) return "";
  return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
}

document.addEventListener("DOMContentLoaded", loadMatches);
