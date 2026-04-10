const list = document.getElementById("notificationsList")
const badge = document.getElementById("badge")


let notifications = [
  { text: "Alex sent you a message", read: false },
  { text: "Sarah replid to your chat", read: false },
  { text: "Tom vieweed your profile", read: true }
]

function renderNotifications() {
  list.innerHTML = ""

  let unreadCount = 0

  notifications.forEach(n => {
    const div = document.createElement("div")
    div.className = "notification-item"
    div.textContent = n.text

    if (!n.read) {
      unreadCount++
      div.style.border = "1px solid #5b7cfa"
    }

    
    div.addEventListener("click", () => {
      n.read = true
      renderNotifications()
    })

    list.appendChild(div)
  })

  badge.textContent = unreadCount
}

renderNotifications()