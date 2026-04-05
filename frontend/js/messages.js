const chatList = document.getElementById("chatList");
const chatMessages = document.getElementById("chatMessages");
const chatName = document.getElementById("chatName");
const chatStatus = document.getElementById("chatStatus");
const messageForm = document.getElementById("messageForm");
const messageInput = document.getElementById("messageInput");

let activeChatId = 1;

const chats = [
  {
    id: 1,
    name: "John Abbey",
    status: "Online",
    messages: [
      { sender: "them", text: "Hi, I saw your profile and liked your idea." },
      { sender: "me", text: "Great! I would love to discuss it." }
    ]
  },
  {
    id: 2,
    name: "Sarah Johnson",
    status: "Last seen 10 min ago",
    messages: [
      { sender: "them", text: "Are you still looking for a co-founder?" }
    ]
  },
  {
    id: 3,
    name: "Tom Lee",
    status: "Online",
    messages: [
      { sender: "them", text: "Your project sounds interesting." }
    ]
  }
];

function saveChats() {
  localStorage.setItem("comatch_chats", JSON.stringify(chats));
}

function loadChats() {
  const saved = localStorage.getItem("comatch_chats");
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      if (Array.isArray(parsed) && parsed.length > 0) {
        chats.length = 0;
        parsed.forEach(chat => chats.push(chat));
      }
    } catch (e) {
      console.log("Could not load saved chats");
    }
  }
}

function renderChatList() {
  chatList.innerHTML = "";

  chats.forEach(chat => {
    const item = document.createElement("div");
    item.className = "chat-item" + (chat.id === activeChatId ? " active" : "");
    item.innerHTML = `
      <h3>${chat.name}</h3>
      <p>${chat.status}</p>
    `;

    item.addEventListener("click", () => {
      activeChatId = chat.id;
      renderChatList();
      renderActiveChat();
    });

    chatList.appendChild(item);
  });
}

function renderActiveChat() {
  const activeChat = chats.find(chat => chat.id === activeChatId);

  if (!activeChat) {
    chatName.textContent = "Select a chat";
    chatStatus.textContent = "Choose a match to start messaging";
    chatMessages.innerHTML = "";
    return;
  }

  chatName.textContent = activeChat.name;
  chatStatus.textContent = activeChat.status;

  chatMessages.innerHTML = "";

  activeChat.messages.forEach(message => {
    const row = document.createElement("div");
    row.className = "message-row " + message.sender;

    const bubble = document.createElement("div");
    bubble.className = "message-bubble";
    bubble.textContent = message.text;

    row.appendChild(bubble);
    chatMessages.appendChild(row);
  });

  chatMessages.scrollTop = chatMessages.scrollHeight;
}

messageForm.addEventListener("submit", function (e) {
  e.preventDefault();

  const text = messageInput.value.trim();
  if (text === "") return;

  const activeChat = chats.find(chat => chat.id === activeChatId);
  if (!activeChat) return;

  activeChat.messages.push({
    sender: "me",
    text: text
  });

  messageInput.value = "";
  saveChats();
  renderActiveChat();
});

loadChats();
renderChatList();
renderActiveChat();