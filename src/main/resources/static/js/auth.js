document.addEventListener('DOMContentLoaded', async () => {
  const user = await App.currentUser();
  if (user) location.href = ['ADMINISTRADOR','SUPER_USUARIO'].includes(user.rol) ? '/admin.html' : '/perfil.html';

  const login = document.getElementById('loginForm');
  if (login) login.addEventListener('submit', async e => {
    e.preventDefault(); showMessage('');
    try {
      const u = await App.request('/api/auth/login',{method:'POST',body:JSON.stringify({correo:correo.value.trim(),contrasena:contrasena.value})});
      const next = new URLSearchParams(location.search).get('next');
      location.href = next || (['ADMINISTRADOR','SUPER_USUARIO'].includes(u.rol) ? '/admin.html' : '/perfil.html');
    } catch(err){ showMessage(err.message,true); }
  });

  const register = document.getElementById('registerForm');
  if (register) register.addEventListener('submit', async e => {
    e.preventDefault(); showMessage('');
    if (contrasena.value !== confirmar.value) return showMessage('Las contraseñas no coinciden.',true);
    try {
      await App.request('/api/auth/register',{method:'POST',body:JSON.stringify({nombre:nombre.value.trim(),correo:correo.value.trim(),contrasena:contrasena.value,aceptaTerminos:terminos.checked})});
      location.href='/perfil.html';
    } catch(err){ showMessage(err.message,true); }
  });
});
function showMessage(text,error=false){const el=document.getElementById('message');if(!el)return;if(!text){el.classList.add('hidden');return;}el.textContent=text;el.className='form-message '+(error?'error':'success');}
