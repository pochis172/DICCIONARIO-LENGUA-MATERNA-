(() => {
    const rows = document.getElementById("languageRows");
    const modal = document.getElementById("languageModal");
    const form = document.getElementById("languageForm");

    const inputId = document.getElementById("languageId");
    const inputNombre = document.getElementById("languageName");
    const inputRegion = document.getElementById("languageRegionId");
    const inputFamilia = document.getElementById("languageFamily");

    const modalTitle = document.getElementById("languageModalTitle");
    const message = document.getElementById("languageMessage");

    let idiomas = [];

    async function peticion(url, options = {}) {
        const response = await fetch(url, {
            ...options,
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            }
        });

        if (response.status === 204) {
            return null;
        }

        let data = null;

        try {
            data = await response.json();
        } catch {
            data = null;
        }

        if (!response.ok) {
            throw new Error(
                data?.detail ||
                data?.message ||
                data?.error ||
                `Error HTTP ${response.status}`
            );
        }

        return data;
    }

    async function cargarIdiomas() {
        try {
            idiomas = await peticion("/api/admin/lenguas");
            mostrarIdiomas();
        } catch (error) {
            console.error(error);
        }
    }

    function mostrarIdiomas() {
        rows.innerHTML = "";

        if (idiomas.length === 0) {
            rows.innerHTML = `
                <tr>
                    <td colspan="4">No hay idiomas registrados.</td>
                </tr>
            `;
            return;
        }

        idiomas.forEach(idioma => {
            const tr = document.createElement("tr");

            tr.innerHTML = `
                <td>${idioma.nombre}</td>
                <td>${idioma.region}</td>
                <td>${idioma.familiaLinguistica ?? "-"}</td>
                <td>
                    <button
                        class="btn btn-secondary"
                        data-editar-idioma="${idioma.id}">
                        Editar
                    </button>

                    <button
                        class="btn btn-danger"
                        data-eliminar-idioma="${idioma.id}">
                        Eliminar
                    </button>
                </td>
            `;

            rows.appendChild(tr);
        });

        document
            .querySelectorAll("[data-editar-idioma]")
            .forEach(btn => {
                btn.addEventListener("click", () => {
                    editarIdioma(Number(btn.dataset.editarIdioma));
                });
            });

        document
            .querySelectorAll("[data-eliminar-idioma]")
            .forEach(btn => {
                btn.addEventListener("click", () => {
                    eliminarIdioma(Number(btn.dataset.eliminarIdioma));
                });
            });
    }

    function abrirNuevoIdioma() {
        form.reset();

        inputId.value = "";

        modalTitle.textContent = "Agregar idioma";

        message.textContent = "";
        message.classList.add("hidden");

        modal.classList.remove("hidden");
    }

    function editarIdioma(id) {
        const idioma = idiomas.find(i => i.id === id);

        if (!idioma) {
            return;
        }

        inputId.value = idioma.id;
        inputNombre.value = idioma.nombre;
        inputRegion.value = idioma.regionId;
        inputFamilia.value = idioma.familiaLinguistica ?? "";

        modalTitle.textContent = "Editar idioma";

        modal.classList.remove("hidden");
    }

    function cerrarModal() {
        modal.classList.add("hidden");
        form.reset();
        inputId.value = "";
    }

    async function guardarIdioma(event) {
        event.preventDefault();

        const id = inputId.value;

        const datos = {
            nombre: inputNombre.value.trim(),
            regionId: Number(inputRegion.value),
            familiaLinguistica:
                inputFamilia.value.trim() || null
        };

        try {
            if (id) {
                await peticion(`/api/admin/lenguas/${id}`, {
                    method: "PUT",
                    body: JSON.stringify(datos)
                });
            } else {
                await peticion("/api/admin/lenguas", {
                    method: "POST",
                    body: JSON.stringify(datos)
                });
            }

            cerrarModal();
            await cargarIdiomas();

        } catch (error) {
            message.textContent = error.message;
            message.classList.remove("hidden");
        }
    }

    async function eliminarIdioma(id) {
        const idioma = idiomas.find(i => i.id === id);

        if (!idioma) {
            return;
        }

        const confirmar = confirm(
            `¿Deseas eliminar el idioma "${idioma.nombre}"?`
        );

        if (!confirmar) {
            return;
        }

        try {
            await peticion(`/api/admin/lenguas/${id}`, {
                method: "DELETE"
            });

            await cargarIdiomas();

        } catch (error) {
            alert(error.message);
        }
    }

    document
        .getElementById("newLanguage")
        ?.addEventListener("click", abrirNuevoIdioma);

    form?.addEventListener("submit", guardarIdioma);

    document
        .querySelectorAll("[data-close-language]")
        .forEach(btn => {
            btn.addEventListener("click", cerrarModal);
        });

    document
        .querySelector('[data-panel="languages"]')
        ?.addEventListener("click", cargarIdiomas);

    cargarIdiomas();
})();