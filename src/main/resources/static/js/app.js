let currentUser = null;
let currentResults = [];

document.addEventListener('DOMContentLoaded', async () => {
  currentUser = await App.setupNav();
  await Promise.all([loadLanguages(), loadCategories(), loadRecent()]);
  document.getElementById('searchForm').addEventListener('submit', e => { e.preventDefault(); search(); });
  const initial = new URLSearchParams(location.search).get('buscar');
  if (initial) { document.getElementById('searchInput').value = initial; await search(); }
});

async function loadLanguages() {
  const list = await App.request('/api/diccionario/lenguas');
  const select = document.getElementById('languageSelect');
  select.innerHTML = '<option value="">Todas las lenguas</option>' + list.map(l => `<option value="${l.id}">${App.escapeHtml(l.nombre)}</option>`).join('');
}

async function loadCategories() {
  const list = await App.request('/api/diccionario/categorias');
  const container = document.getElementById('categoryList');
  container.innerHTML = list.map(c => `<button class="category" data-category="${App.escapeHtml(c)}">${App.escapeHtml(c)}</button>`).join('') || '<span class="muted">Aún no hay categorías.</span>';
  container.querySelectorAll('[data-category]').forEach(btn => btn.onclick = () => loadCategory(btn.dataset.category));
}

async function loadRecent() {
  const list = await App.request('/api/diccionario/palabras');
  document.getElementById('recentWords').innerHTML = list.length ? list.map(wordCard).join('') : '<div class="empty">Aún no hay palabras registradas.</div>';
  bindWordActions(document.getElementById('recentWords'), list);
}

async function loadCategory(category) {
  document.querySelectorAll('.category').forEach(b => b.classList.toggle('active', b.dataset.category === category));
  const list = await App.request('/api/diccionario/palabras?categoria=' + encodeURIComponent(category));
  currentResults = list;
  const section = document.getElementById('searchResults'); section.classList.remove('hidden');
  document.getElementById('resultCount').textContent = `${list.length} palabra(s) en ${category}`;
  document.getElementById('resultDetail').innerHTML = '';
  document.getElementById('resultList').innerHTML = list.length ? list.map(wordCard).join('') : '<div class="empty">No hay palabras en esta categoría.</div>';
  bindWordActions(document.getElementById('resultList'), list);
  section.scrollIntoView({behavior:'smooth'});
}

async function search() {
  const q = document.getElementById('searchInput').value.trim(); if (!q) return;
  const lang = document.getElementById('languageSelect').value;
  try {
    const list = await App.request(`/api/diccionario/buscar?q=${encodeURIComponent(q)}${lang ? '&lenguaId='+lang : ''}`);
    currentResults = list;
    const section = document.getElementById('searchResults'); section.classList.remove('hidden');
    document.getElementById('resultCount').textContent = list.length ? `${list.length} coincidencia(s) para “${q}”` : `No encontramos “${q}”`;
    document.getElementById('resultDetail').innerHTML = list[0] ? detailCard(list[0]) : '<div class="empty">No hay resultados. Si tienes una cuenta, puedes sugerir esta palabra desde Mi espacio.</div>';
    document.getElementById('resultList').innerHTML = list.slice(1).map(wordCard).join('');
    bindWordActions(section, list);
    section.scrollIntoView({behavior:'smooth'});
  } catch(e) { App.toast(e.message); }
}

function wordCard(p) {
  return `<article class="card word-card" data-word-id="${p.id}">
    <div class="word-top"><span class="tag">${App.escapeHtml(p.categoria)}</span><button class="icon-btn ${p.favorito?'active':''}" data-fav="${p.id}" title="Favorito">♡</button></div>
    <h3>${App.escapeHtml(p.espanol)}</h3>
    <div class="translation">${App.escapeHtml(App.firstTranslation(p))}</div>
    <p>${App.escapeHtml(p.significado)}</p>
    <button class="btn btn-outline btn-sm" data-open="${p.id}">Ver palabra</button>
  </article>`;
}

function detailCard(p) {
  const examples = p.ejemplos?.length ? p.ejemplos.map(x=>`<div class="detail"><label>Ejemplo de uso</label>${App.escapeHtml(x)}</div>`).join('') : '<div class="detail"><label>Ejemplo</label><span class="muted">Sin ejemplo registrado.</span></div>';
  const audio = p.audios?.[0] ? `<audio class="audio-player" controls src="${App.escapeHtml(p.audios[0].urlAudio)}"></audio>` : '<p class="muted">Aún no hay audio cargado para esta palabra.</p>';
  return `<div class="result-card">
    <article class="result-main">
      <h3>Palabra buscada</h3>
      <div class="result-word"><strong>${App.escapeHtml(p.espanol)}</strong><button class="icon-btn ${p.favorito?'active':''}" data-fav="${p.id}" title="Guardar favorito">♡</button></div>
      <div class="result-translation"><span class="tag">${App.escapeHtml(p.lengua.nombre)}</span><br><b>${App.escapeHtml(App.firstTranslation(p))}</b></div>
      <div class="details"><div class="detail"><label>Significado</label>${App.escapeHtml(p.significado)}</div>${examples}</div>
    </article>
    <aside class="result-side">
      <div class="info-box"><h4>${App.escapeHtml(p.lengua.nombre)}</h4><p>${App.escapeHtml(p.lengua.region)}${p.lengua.familiaLinguistica ? ' • Familia: '+App.escapeHtml(p.lengua.familiaLinguistica) : ''}</p></div>
      <div class="card"><h3 style="margin-top:0">Pronunciación</h3>${audio}</div>
      <div class="card"><span class="tag">${App.escapeHtml(p.categoria)}</span><p class="muted">Puedes explorar más palabras de esta categoría.</p><button class="btn btn-secondary btn-sm" onclick="loadCategory('${String(p.categoria).replace(/'/g,"\\'")}')">Ver categoría</button></div>
    </aside>
  </div>`;
}

function bindWordActions(root, words) {
  root.querySelectorAll('[data-open]').forEach(btn => btn.onclick = async () => {
    const p = words.find(x=>String(x.id)===btn.dataset.open) || await App.request('/api/diccionario/palabras/'+btn.dataset.open);
    document.getElementById('searchResults').classList.remove('hidden');
    document.getElementById('resultDetail').innerHTML = detailCard(p);
    bindWordActions(document.getElementById('resultDetail'), [p]);
    document.getElementById('searchResults').scrollIntoView({behavior:'smooth'});
  });
  root.querySelectorAll('[data-fav]').forEach(btn => btn.onclick = () => toggleFavorite(btn.dataset.fav, words));
}

async function toggleFavorite(id, words) {
  if (!currentUser) { location.href='/login.html?next=/'; return; }
  const p = words.find(x=>String(x.id)===String(id));
  const isFav = p?.favorito;
  try {
    await App.request('/api/usuario/favoritos/'+id,{method:isFav?'DELETE':'POST'});
    if (p) p.favorito = !isFav;
    App.toast(isFav ? 'Eliminado de favoritos.' : 'Guardado en favoritos.');
    document.querySelectorAll(`[data-fav="${id}"]`).forEach(b=>b.classList.toggle('active',!isFav));
  } catch(e){ App.toast(e.message); }
}
