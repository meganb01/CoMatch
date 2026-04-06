// Get container
const container = document.getElementById("messagesList");

// Display function
function displayMessages(messages) {
  if (!container) return;

  container.innerHTML = "";

  if (messages.length === 0) {
    container.innerHTML = "<p class='muted'>No messages yet</p>";
    return;
  }

  messages.forEach(msg => {
    const div = document.createElement("div");
    div.textContent = msg.content;

    if (msg.sender === "me") {
      div.classList.add("my-message");
    } else {
      div.classList.add("their-message");
    }

    container.appendChild(div);
  });

  container.scrollTop = container.scrollHeight;
}

// Load from backend
async function loadMessages() {
  try {
    const params = new URLSearchParams(window.location.search);
    const matchId = params.get("matchId");

    if (!matchId) {
      console.error("No matchId provided");
      return;
    }

    const token = localStorage.getItem("token");
    const userId = Number(localStorage.getItem("userId")) || 1;

    if (!token) {
      console.error("No auth token found");
      return;
    }

    const response = await fetch(`http://localhost:8080/api/matches/${matchId}/messages`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error("Failed to fetch messages");
    }

    const data = await response.json();

    const messages = data.content.map(msg => ({
      sender: msg.senderId === userId ? "me" : "them",
      content: msg.body
    }));

    displayMessages(messages);

  } catch (error) {
    console.error("Error loading messages:", error);
  }
}

loadMessages();