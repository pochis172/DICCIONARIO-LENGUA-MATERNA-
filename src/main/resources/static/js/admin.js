let adminUser = null;
let words = [];
let languages = [];
let suggestions = [];
let conversionSuggestionId = null;

document.addEventListener('DOMContentLoaded', async () => {
  adminUser = await App.setupNav();
  if (!adminUser) { location.href = '/login.html?next=/admin.html'; return; }
  if (adminUser.rol !== 'ADMINISTRADOR') { location.href = '/perfil.html'; return; }

  document.querySelectorAll('[data-panel]').forEach(b => b.onclick = () => openPanel(b.dataset.panel));
  document.querySelectorAll('[data-close-word]').forEach(b => b.onclick = closeWordModal);
  newWord.onclick = () => openWordModal();
  wordForm.onsubmit = saveWord;
  adminSearchBtn.onclick = () => loadWords(adminSearch.value.trim());
  adminSearch.onkeydown = e => { if (e.key === 'Enter') { e.preventDefault(); loadWords(adminSearch.value.trim()); } };

  await Promise.all([loadLanguages(), loadWords(), loadSuggestions()]);
});

function openPanel(id) {
  document.querySelectorAll('.panel').forEach(p => p.classList.toggle('active', p.id === id));
  document.querySelectorAll('[data-panel]').forEach(b => b.classList.toggle('active', b.dataset.panel === id));
}

async function loadLanguages() {
  languages = await App.request('/api/diccionario/lenguas');
  wordLanguage.innerHTML = languages.map(l => `<option value="${l.id}">${App.escapeHtml(l.nombre)} — ${App.escapeHtml(l.region)}</option>`).join('');
}

async function loadWords(q = '') {
  words = await App.request('/api/admin/palabras' + (q ? '?q=' + encodeURIComponent(q) : ''));
  wordRows.innerHTML = words.length ? words.map(p => `<tr>
    <td><b>${App.escapeHtml(p.espanol)}</b></td>
    <td>${App.escapeHtml(App.firstTranslation(p))}</td>
    <td>${App.escapeHtml(p.lengua.nombre)}</td>
    <td><span class="tag">${App.escapeHtml(p.categoria)}</span></td>
    <td><div class="actions"><button class="btn btn-outline btn-sm" onclick="editWord(${p.id})">Editar</button><button class="btn btn-danger btn-sm" onclick="deleteWord(${p.id})">Eliminar</button></div></td>
  </tr>`).join('') : '<tr><td colspan="5" class="muted">No hay resultados.</td></tr>';
}

function openWordModal(p = null, suggestion = null) {
  conversionSuggestionId = suggestion?.id || null;
  wordModal.classList.remove('hidden');
  wordModalTitle.textContent = p ? 'Editar palabra' : (suggestion ? 'Convertir sugerencia en palabra' : 'Agregar palabra');
  wordId.value = p?.id || '';
  wordSpanish.value = p?.espanol || suggestion?.palabraSugerida || '';
  wordCategory.value = p?.categoria || (suggestion ? 'Sugerencias' : '');
  wordMeaning.value = p?.significado || suggestion?.descripcion || '';
  wordLanguage.value = p?.lengua?.id || suggestion?.lenguaId || languages[0]?.id || '';
  wordTranslation.value = p ? App.firstTranslation(p) : (suggestion?.posibleTraduccion || '');
  wordExample.value = p?.ejemplos?.[0] || suggestion?.descripcion || '';
  audioUrl.value = p?.audios?.[0]?.urlAudio || '';
  audioFile.value = '';
  hideMessage();
}

function closeWordModal() {
  wordModal.classList.add('hidden');
  conversionSuggestionId = null;
}

function editWord(id) { openWordModal(words.find(w => w.id === id)); }

async function uploadAudioIfNeeded() {
  if (!audioFile.files.length) return audioUrl.value.trim();
  const fd = new FormData();
  fd.append('file', audioFile.files[0]);
  const result = await App.request('/api/admin/audio/upload', {method:'POST', body:fd});
  return result.url;
}

async function saveWord(e) {
  e.preventDefault(); hideMessage();
  try {
    const uploaded = await uploadAudioIfNeeded();
    const langId = Number(wordLanguage.value);
    const payload = {
      espanol: wordSpanish.value.trim(),
      categoria: wordCategory.value.trim(),
      significado: wordMeaning.value.trim(),
      lenguaId: langId,
      traducciones: [{traduccion: wordTranslation.value.trim(), lenguaId: langId}],
      ejemplos: wordExample.value.trim() ? [wordExample.value.trim()] : [],
      audios: uploaded ? [{urlAudio: uploaded, tipo: 'pronunciación'}] : []
    };
    const id = wordId.value;
    await App.request(id ? '/api/admin/palabras/' + id : '/api/admin/palabras', {
      method: id ? 'PUT' : 'POST', body: JSON.stringify(payload)
    });
    const converted = conversionSuggestionId;
    if (converted) {
      await App.request(`/api/admin/sugerencias/${converted}/estado`, {method:'PATCH', body:JSON.stringify({estado:'APROBADA'})});
    }
    closeWordModal();
    App.toast(id ? 'Palabra actualizada.' : (converted ? 'Sugerencia convertida en palabra oficial.' : 'Palabra registrada.'));
    await Promise.all([loadWords(adminSearch.value.trim()), loadSuggestions()]);
  } catch (e) { showMessage(e.message); }
}

async function deleteWord(id) {
  const p = words.find(x => x.id === id);
  if (!confirm(`¿Eliminar “${p?.espanol || 'esta palabra'}” y sus datos relacionados?`)) return;
  try {
    await App.request('/api/admin/palabras/' + id, {method:'DELETE'});
    App.toast('Palabra eliminada.');
    await loadWords(adminSearch.value.trim());
  } catch (e) { App.toast(e.message); }
}

async function loadSuggestions() {
  suggestions = await App.request('/api/admin/sugerencias');
  adminSuggestions.innerHTML = suggestions.length ? suggestions.map(s => `<div class="list-item">
    <div><h4>${App.escapeHtml(s.palabraSugerida)} ${s.posibleTraduccion ? '→ ' + App.escapeHtml(s.posibleTraduccion) : ''}</h4>
    <p>Por ${App.escapeHtml(s.usuario)} • ${App.escapeHtml(s.lengua || 'Lengua no definida')} • <span class="status ${s.estado}">${s.estado}</span></p>
    <p>${App.escapeHtml(s.descripcion || 'Sin descripción')}</p></div>
    <div class="actions"><button class="btn btn-secondary btn-sm" onclick="convertSuggestion(${s.id})">Convertir en palabra</button><button class="btn btn-danger btn-sm" onclick="changeSuggestion(${s.id},'RECHAZADA')">Rechazar</button><button class="btn btn-outline btn-sm" onclick="changeSuggestion(${s.id},'PENDIENTE')">Pendiente</button></div>
  </div>`).join('') : '<div class="empty">No hay sugerencias.</div>';
}

function convertSuggestion(id) {
  const s = suggestions.find(x => x.id === id);
  openWordModal(null, s);
}

async function changeSuggestion(id, estado) {
  try {
    await App.request(`/api/admin/sugerencias/${id}/estado`, {method:'PATCH', body:JSON.stringify({estado})});
    App.toast('Estado actualizado.');
    await loadSuggestions();
  } catch (e) { App.toast(e.message); }
}

function showMessage(msg) { wordMessage.textContent = msg; wordMessage.className = 'form-message error'; }
function hideMessage() { wordMessage.classList.add('hidden'); }
