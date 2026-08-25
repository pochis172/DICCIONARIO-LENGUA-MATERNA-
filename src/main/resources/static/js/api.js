window.App = (() => {
  async function request(url, options = {}) {
    const headers = {...(options.headers || {})};
    if (options.body && !(options.body instanceof FormData)) headers['Content-Type'] = 'application/json';
    const res = await fetch(url, {...options, headers});
    if (res.status === 204) return null;
    const type = res.headers.get('content-type') || '';
    const data = type.includes('application/json') ? await res.json() : await res.text();
    if (!res.ok) throw new Error(data?.error || data?.message || `Error ${res.status}`);
    return data;
  }
  function toast(message) {
    const old = document.querySelector('.toast'); if (old) old.remove();
    const el = document.createElement('div'); el.className = 'toast'; el.textContent = message;
    document.body.appendChild(el); setTimeout(() => el.remove(), 3200);
  }
  async function currentUser() {
    try { return await request('/api/auth/me'); } catch { return null; }
  }
  function firstTranslation(p) { return p?.traducciones?.[0]?.traduccion || 'Sin traducción'; }
  function escapeHtml(s='') { return String(s).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#039;','"':'&quot;'}[c])); }
  async function setupNav() {
    const user = await currentUser();
    document.querySelectorAll('[data-user-only]').forEach(el => el.classList.toggle('hidden', !user));
    document.querySelectorAll('[data-guest-only]').forEach(el => el.classList.toggle('hidden', !!user));
    document.querySelectorAll('[data-admin-only]').forEach(el => el.classList.toggle('hidden', user?.rol !== 'ADMINISTRADOR'));
    document.querySelectorAll('[data-user-name]').forEach(el => el.textContent = user?.nombre || 'Perfil');
    document.querySelectorAll('[data-logout]').forEach(el => el.onclick = async () => { await request('/api/auth/logout',{method:'POST'}); location.href='/'; });
    return user;
  }
  return {request, toast, currentUser, firstTranslation, escapeHtml, setupNav};
})();
