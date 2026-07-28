const API = "/api/tasks";
const AUTH = "/api/auth";

const taskList = document.getElementById("taskList");
const form = document.getElementById("taskForm");
const resetBtn = document.getElementById("resetBtn");

const authForm = document.getElementById("authForm");
const registerBtn = document.getElementById("registerBtn");
const loginBtn = document.getElementById("loginBtn");
const logoutBtn = document.getElementById("logoutBtn");
const clearTokenBtn = document.getElementById("clearTokenBtn");
const tokenBox = document.getElementById("tokenBox");
const tokenMeta = document.getElementById("tokenMeta");
const useTokenChk = document.getElementById("useTokenChk");

const reqBox = document.getElementById("reqBox");
const resBox = document.getElementById("resBox");

function getToken() {
  return localStorage.getItem("jwt") || "";
}

function setToken(token) {
  if (token) {
    localStorage.setItem("jwt", token);
  } else {
    localStorage.removeItem("jwt");
  }
  renderToken();
}

function parseJwtExp(token) {
  try {
    const payload = token.split(".")[1];
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    const data = JSON.parse(json);
    if (!data.exp) return null;
    return new Date(data.exp * 1000);
  } catch {
    return null;
  }
}

function renderToken() {
  const token = getToken();
  tokenBox.textContent = token ? token : "(sin token)";
  const exp = token ? parseJwtExp(token) : null;
  tokenMeta.textContent = exp
    ? `exp: ${exp.toLocaleString()}`
    : "(sin exp)";
}

function headersToObj(headers) {
  const obj = {};
  for (const [k, v] of headers.entries()) obj[k] = v;
  return obj;
}

function showRequest(method, url, headers, body) {
  reqBox.textContent = JSON.stringify({ method, url, headers, body }, null, 2);
}

function showResponse(status, headers, body) {
  resBox.textContent = JSON.stringify({ status, headers, body }, null, 2);
}

async function apiFetch(method, url, body) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token && useTokenChk.checked) headers["Authorization"] = `Bearer ${token}`;
  showRequest(method, url, headers, body ?? null);

  const res = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  const resHeaders = headersToObj(res.headers);
  let data = null;

  const text = await res.text();
  try { data = text ? JSON.parse(text) : null; }
  catch { data = text; }

  showResponse(res.status, resHeaders, data);
  if (!res.ok) throw { status: res.status, data };

  return data;
}

function toLocalInputValue(dateStr) {
  const d = new Date(dateStr);
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function getFormData() {
  const id = document.getElementById("taskId").value.trim();
  const title = document.getElementById("title").value;
  const description = document.getElementById("description").value;
  const dueDateLocal = document.getElementById("dueDate").value;
  const status = document.getElementById("status").value;

  const dueDate = dueDateLocal ? new Date(dueDateLocal).toISOString() : null;

  return { id, title, description, dueDate, status };
}

function getAuthData() {
  const username = document.getElementById("authUsername").value.trim();
  const email = document.getElementById("authEmail").value.trim();
  const password = document.getElementById("authPassword").value;

  return { username, email, password };
}

async function register() {
  const { username, email, password } = getAuthData();
  const payload = { username, email, password };
  const result = await apiFetch("POST", `${AUTH}/register`, payload);
  setToken(result.token);
}

async function login() {
  const { email, password } = getAuthData();
  const payload = { email, password };
  const result = await apiFetch("POST", `${AUTH}/login`, payload);
  setToken(result.token);
}

async function logout() {
  try {
    await apiFetch("POST", `${AUTH}/logout`);
  } catch (err) {
    console.error(err);
  }
  setToken("");
}

function fillForm(task) {
  document.getElementById("taskId").value = task.id;
  document.getElementById("title").value = task.title;
  document.getElementById("description").value = task.description ?? "";
  document.getElementById("dueDate").value = toLocalInputValue(task.dueDate);
  document.getElementById("status").value = task.status;
}

function renderTasks(tasks) {
  taskList.innerHTML = "";
  if (!tasks.length) {
    taskList.innerHTML = "<li>(sin tareas)</li>";
    return;
  }

  for (const t of tasks) {
    const li = document.createElement("li");
    li.innerHTML = `
      <div>
        <strong>#${t.id}</strong> ${t.title}
        <div style="font-size: 0.9em; opacity: 0.8;">
          ${t.status} · Due: ${new Date(t.dueDate).toLocaleString()}
        </div>
      </div>
      <div>
        <button data-id="${t.id}" class="detailBtn">Detalle</button>
        <button data-id="${t.id}" class="editBtn">Editar</button>
        <button data-id="${t.id}" class="deleteBtn">Borrar</button>
      </div>
    `;
    taskList.appendChild(li);
  }

  document.querySelectorAll(".detailBtn").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = btn.getAttribute("data-id");
      await apiFetch("GET", `${API}/${id}`);
    });
  });

  document.querySelectorAll(".editBtn").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = btn.getAttribute("data-id");
      const task = await apiFetch("GET", `${API}/${id}`);
      fillForm(task);
    });
  });

  document.querySelectorAll(".deleteBtn").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = btn.getAttribute("data-id");
      if (!confirm("¿Borrar esta tarea?")) return;
      await apiFetch("DELETE", `${API}/${id}`);
      await refresh();
    });
  });
}

async function refresh() {
  const tasks = await apiFetch("GET", API);
  renderTasks(tasks);
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const { id, title, description, dueDate, status } = getFormData();
  const payload = { title, description, dueDate, status };

  try {
    if (id) {
      await apiFetch("PUT", `${API}/${id}`, payload);
    } else {
      await apiFetch("POST", API, payload);
    }
    form.reset();
    document.getElementById("taskId").value = "";
    await refresh();
  } catch (err) {
    console.error(err);
  }
});

resetBtn.addEventListener("click", () => {
  form.reset();
  document.getElementById("taskId").value = "";
});

registerBtn.addEventListener("click", async () => {
  try {
    await register();
  } catch (err) {
    console.error(err);
  }
});

loginBtn.addEventListener("click", async () => {
  try {
    await login();
  } catch (err) {
    console.error(err);
  }
});

logoutBtn.addEventListener("click", async () => {
  await logout();
});

clearTokenBtn.addEventListener("click", () => {
  setToken("");
});

authForm.addEventListener("submit", (e) => {
  e.preventDefault();
});

useTokenChk.addEventListener("change", () => {
  // Solo cambia el comportamiento de headers; no toca el token almacenado.
});

renderToken();
refresh().catch(console.error);
