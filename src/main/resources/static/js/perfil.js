let user=null, favorites=[], historyRows=[], suggestions=[], languages=[];
document.addEventListener('DOMContentLoaded', async()=>{
  user=await App.setupNav(); if(!user){location.href='/login.html?next=/perfil.html';return;}
  profileName.value=user.nombre; profileEmail.value=user.correo;
  document.querySelectorAll('[data-panel]').forEach(b=>b.onclick=()=>openPanel(b.dataset.panel));
  document.querySelectorAll('[data-close]').forEach(b=>b.onclick=closeSuggestionModal);
  newSuggestion.onclick=()=>openSuggestionModal();
  clearHistory.onclick=clearHistoryAction;
  profileForm.onsubmit=saveProfile; passwordForm.onsubmit=changePassword; suggestionForm.onsubmit=saveSuggestion;
  await Promise.all([loadLanguages(),loadFavorites(),loadHistory(),loadSuggestions()]);
});
function openPanel(id){document.querySelectorAll('.panel').forEach(p=>p.classList.toggle('active',p.id===id));document.querySelectorAll('[data-panel]').forEach(b=>b.classList.toggle('active',b.dataset.panel===id));}
async function loadLanguages(){languages=await App.request('/api/diccionario/lenguas');suggestedLanguage.innerHTML='<option value="">Seleccionar…</option>'+languages.map(l=>`<option value="${l.id}">${App.escapeHtml(l.nombre)}</option>`).join('');}
async function loadFavorites(){favorites=await App.request('/api/usuario/favoritos');favCount.textContent=favorites.length;favoritesList.innerHTML=favorites.length?favorites.map(f=>`<article class="card word-card"><div class="word-top"><span class="tag">${App.escapeHtml(f.palabra.categoria)}</span><button class="icon-btn active" onclick="removeFavorite(${f.palabra.id})">♡</button></div><h3>${App.escapeHtml(f.palabra.espanol)}</h3><div class="translation">${App.escapeHtml(App.firstTranslation(f.palabra))}</div><p>${App.escapeHtml(f.palabra.significado)}</p><a class="btn btn-outline btn-sm" href="/?buscar=${encodeURIComponent(f.palabra.espanol)}#diccionario">Buscar de nuevo</a></article>`).join(''):'<div class="empty">Todavía no has guardado favoritos.</div>';}
async function removeFavorite(id){await App.request('/api/usuario/favoritos/'+id,{method:'DELETE'});App.toast('Favorito eliminado.');await loadFavorites();}
async function loadHistory(){historyRows=await App.request('/api/usuario/historial');historyCount.textContent=historyRows.length;historyList.innerHTML=historyRows.length?historyRows.map(h=>`<div class="list-item"><div><h4>${App.escapeHtml(h.palabra)} → ${App.escapeHtml(h.traduccion)}</h4><p>${new Date(h.fecha).toLocaleString('es-CO')}</p></div><a class="btn btn-outline btn-sm" href="/?buscar=${encodeURIComponent(h.palabra)}#diccionario">Consultar</a></div>`).join(''):'<div class="empty">Tu historial está vacío.</div>';}
async function clearHistoryAction(){if(!confirm('¿Deseas eliminar todo tu historial?'))return;await App.request('/api/usuario/historial',{method:'DELETE'});App.toast('Historial eliminado.');await loadHistory();}
async function loadSuggestions(){suggestions=await App.request('/api/sugerencias/mias');suggestCount.textContent=suggestions.length;suggestionsList.innerHTML=suggestions.length?suggestions.map(s=>`<div class="list-item"><div><h4>${App.escapeHtml(s.palabraSugerida)} ${s.posibleTraduccion?'→ '+App.escapeHtml(s.posibleTraduccion):''}</h4><p>${App.escapeHtml(s.lengua||'Lengua sin definir')} • <span class="status ${s.estado}">${s.estado}</span></p><p>${App.escapeHtml(s.descripcion||'Sin descripción')}</p></div><div class="actions">${s.estado==='PENDIENTE'?`<button class="btn btn-outline btn-sm" onclick="editSuggestion(${s.id})">Editar</button><button class="btn btn-danger btn-sm" onclick="deleteSuggestion(${s.id})">Eliminar</button>`:''}</div></div>`).join(''):'<div class="empty">Aún no has enviado sugerencias.</div>';}
function openSuggestionModal(s=null){suggestionModal.classList.remove('hidden');suggestionTitle.textContent=s?'Editar sugerencia':'Nueva sugerencia';suggestionId.value=s?.id||'';suggestedWord.value=s?.palabraSugerida||'';suggestedTranslation.value=s?.posibleTraduccion||'';suggestedLanguage.value=s?.lenguaId||'';suggestedDescription.value=s?.descripcion||'';}
function closeSuggestionModal(){suggestionModal.classList.add('hidden');}
function editSuggestion(id){openSuggestionModal(suggestions.find(s=>s.id===id));}
async function saveSuggestion(e){e.preventDefault();const id=suggestionId.value;const payload={palabraSugerida:suggestedWord.value.trim(),posibleTraduccion:suggestedTranslation.value.trim()||null,lenguaId:suggestedLanguage.value?Number(suggestedLanguage.value):null,descripcion:suggestedDescription.value.trim()||null};try{await App.request(id?'/api/sugerencias/'+id:'/api/sugerencias',{method:id?'PUT':'POST',body:JSON.stringify(payload)});closeSuggestionModal();App.toast(id?'Sugerencia actualizada.':'Sugerencia enviada.');await loadSuggestions();}catch(e){App.toast(e.message);}}
async function deleteSuggestion(id){if(!confirm('¿Eliminar esta sugerencia?'))return;await App.request('/api/sugerencias/'+id,{method:'DELETE'});App.toast('Sugerencia eliminada.');await loadSuggestions();}
async function saveProfile(e) {
  e.preventDefault();

  const payload = {
    nombre: profileName.value.trim(),
    correo: profileEmail.value.trim()
  };

  try {
    user = await App.request('/api/usuario/perfil', {
      method: 'PUT',
      body: JSON.stringify(payload)
    });

    profileName.value = user.nombre;
    profileEmail.value = user.correo;

    document
      .querySelectorAll('[data-user-name]')
      .forEach(el => el.textContent = user.nombre);

    App.toast('Perfil actualizado correctamente.');

  } catch (e) {
    App.toast(e.message);
  }
}
async function changePassword(e){e.preventDefault();try{await App.request('/api/usuario/password',{method:'PUT',body:JSON.stringify({actual:oldPassword.value,nueva:newPassword.value})});oldPassword.value='';newPassword.value='';App.toast('Contraseña actualizada.');}catch(e){App.toast(e.message);}}
