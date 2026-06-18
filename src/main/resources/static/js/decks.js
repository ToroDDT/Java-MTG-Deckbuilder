function toggleWhite(element) {
    toggleColorFilter(element, "color-W", "btn-filter-white-selected");
}

function toggleRed(element) {
    toggleColorFilter(element, "color-R", "btn-filter-red-selected");
}

function toggleBlue(element) {
    toggleColorFilter(element, "color-U", "btn-filter-blue-selected");
}

function toggleBlack(element) {
    toggleColorFilter(element, "color-B", "btn-filter-black-selected");
}

function toggleGreen(element) {
    toggleColorFilter(element, "color-G", "btn-filter-green-selected");
}

function toggleColorFilter(element, activeClass, selectedClass) {
    if (!element) {
        return;
    }

    if (element.classList.contains(activeClass)) {
        element.classList.remove(activeClass);
        element.classList.add("btn-filter");
    } else {
        element.classList.toggle(selectedClass);
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const menuToggle = document.querySelector(".mobile-menu-toggle");
    const nav = document.querySelector("nav");

    if (menuToggle && nav) {
        menuToggle.addEventListener("click", function() {
            nav.classList.toggle("active");
        });
    }

    const modal = document.getElementById("newDeckModal");
    const openBtn = document.getElementById("openNewDeckModalBtn");
    const closeBtn = document.getElementById("closeNewDeckModalBtn");
    const cancelBtn = document.getElementById("cancelNewDeckBtn");

    function openNewDeckModal() {
        if (!modal) {
            return;
        }

        modal.classList.add("is-open");
        modal.setAttribute("aria-hidden", "false");
        document.body.style.overflow = "hidden";

        const nameInput = document.getElementById("deckName");
        if (nameInput) {
            nameInput.focus();
        }
    }

    function closeNewDeckModal() {
        if (!modal) {
            return;
        }

        modal.classList.remove("is-open");
        modal.setAttribute("aria-hidden", "true");
        document.body.style.overflow = "";
    }

    if (openBtn) {
        openBtn.addEventListener("click", openNewDeckModal);
    }
    if (closeBtn) {
        closeBtn.addEventListener("click", closeNewDeckModal);
    }
    if (cancelBtn) {
        cancelBtn.addEventListener("click", closeNewDeckModal);
    }
    if (modal) {
        modal.addEventListener("click", function(e) {
            if (e.target === modal) {
                closeNewDeckModal();
            }
        });
    }

    document.addEventListener("keydown", function(e) {
        if (e.key === "Escape" && modal && modal.classList.contains("is-open")) {
            closeNewDeckModal();
        }
    });

    const modeSelect = document.getElementById("existingDeckListMode");
    const panelPaste = document.getElementById("existingListPaste");
    const panelFile = document.getElementById("file");
    const panelUrl = document.getElementById("existingListUrl");

    function syncExistingListPanels() {
        if (!modeSelect || !panelPaste || !panelFile || !panelUrl) {
            return;
        }

        const mode = modeSelect.value;
        panelPaste.hidden = mode !== "PASTE";
        panelFile.hidden = mode !== "FILE";
        panelUrl.hidden = mode !== "URL";
    }

    if (modeSelect) {
        modeSelect.addEventListener("change", syncExistingListPanels);
        syncExistingListPanels();
    }

    const editModal = document.getElementById("editDeckModal");
    const closeEditBtn = document.getElementById("closeEditDeckModalBtn");
    const cancelEditBtn = document.getElementById("cancelEditDeckBtn");
    const editCommanderGroup = document.getElementById("editDeckCommanderGroup");

    function openEditDeckModal(deckId, name, commander, format) {
        if (!editModal) {
            return;
        }

        const idInput = document.getElementById("editDeckId");
        const nameInput = document.getElementById("editDeckName");
        const commanderInput = document.getElementById("editDeckCommander");

        if (idInput) {
            idInput.value = deckId || "";
        }
        if (nameInput) {
            nameInput.value = name || "";
        }
        if (commanderInput) {
            commanderInput.value = commander || "";
        }
        if (editCommanderGroup) {
            editCommanderGroup.hidden = format !== "Commander";
        }

        editModal.classList.add("is-open");
        editModal.setAttribute("aria-hidden", "false");
        document.body.style.overflow = "hidden";

        if (nameInput) {
            nameInput.focus();
        }
    }

    function closeEditDeckModal() {
        if (!editModal) {
            return;
        }

        editModal.classList.remove("is-open");
        editModal.setAttribute("aria-hidden", "true");
        document.body.style.overflow = "";
    }

    if (closeEditBtn) {
        closeEditBtn.addEventListener("click", closeEditDeckModal);
    }
    if (cancelEditBtn) {
        cancelEditBtn.addEventListener("click", closeEditDeckModal);
    }
    if (editModal) {
        editModal.addEventListener("click", function(e) {
            if (e.target === editModal) {
                closeEditDeckModal();
            }
        });
    }

    document.addEventListener("click", function(e) {
        const editBtn = e.target.closest(".edit-deck-btn");
        if (!editBtn) {
            return;
        }

        openEditDeckModal(
            editBtn.dataset.deckId,
            editBtn.dataset.deckName,
            editBtn.dataset.deckCommander,
            editBtn.dataset.deckFormat
        );
    });

    document.addEventListener("keydown", function(e) {
        if (e.key === "Escape" && editModal && editModal.classList.contains("is-open")) {
            closeEditDeckModal();
        }
    });
});
