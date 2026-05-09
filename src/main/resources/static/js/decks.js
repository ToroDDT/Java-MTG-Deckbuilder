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
});
