document.addEventListener('DOMContentLoaded', () => {
    // This script adds csrf token to htmx requests headers
    document.body.addEventListener('htmx:configRequest', (event) => {
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        if (token && header) {
            event.detail.headers[header] = token;
        }
    });


    const clearBtn = document.getElementById("clearBtn");
    clearBtn.addEventListener("click", () => {
        window.location.reload();
    })
    let isPagingRequest = false;
    const searchForm = document.getElementById("librarySearchForm");

    function updateVisibleCardCounts() {
        const cardGrid = document.getElementById("cardGrid");
        const visibleCount = document.getElementById("visibleCount");
        const totalCount = document.getElementById("totalCount");

        if (!cardGrid || !visibleCount || !totalCount) {
            return;
        }

        const shownCards = cardGrid.querySelectorAll(".cb-card").length;
        visibleCount.textContent = String(shownCards);
        totalCount.textContent = String(shownCards);
    }

    function resetPaginationState() {
        const lastIdInput = document.getElementById("lastId");
        const operatorInput = document.getElementById("operator");
        const pageInput = document.getElementById("page");

        if (lastIdInput) {
            lastIdInput.value = "";
        }
        if (operatorInput) {
            operatorInput.value = "";
        }
        if (pageInput) {
            pageInput.value = "0";
        }
    }

    document.body.addEventListener("htmx:afterSwap", function(evt) {
        const target = evt.detail.target;
        if (!target) {
            return;
        }
        if (target.id === "personal-cards" || target.classList.contains("cb-card")) {
            updateVisibleCardCounts();
        }
    });

    document.body.addEventListener("click", function(evt) {
        const pageInput = document.getElementById("page");
        if (!pageInput) {
            return;
        }

        if (evt.target.id === "prevBtn") {
            isPagingRequest = true;
            pageInput.value = String(Math.max((parseInt(pageInput.value, 10) || 0) - 1, 0));
            htmx.trigger("#librarySearchForm", "submit");
        }
        if (evt.target.id === "nextBtn") {
            isPagingRequest = true;
            pageInput.value = String((parseInt(pageInput.value, 10) || 0) + 1);
            htmx.trigger("#librarySearchForm", "submit");
        }
    });
    document.body.addEventListener("htmx:configRequest", function(evt) {
        const source = evt.detail.elt;
        if (!searchForm || !source || (source !== searchForm && !searchForm.contains(source))) {
            return;
        }

        if (!isPagingRequest) {
            resetPaginationState();
            evt.detail.parameters.dateAdded = "";
            evt.detail.parameters.operator = "";
            evt.detail.parameters.page = 0;
        }
    })

    document.body.addEventListener("htmx:afterRequest", function(evt) {
        const source = evt.detail.elt;
        if (searchForm && source && (source === searchForm || searchForm.contains(source))) {
            isPagingRequest = false;
        }
    });

    document.addEventListener('htmx:afterSwap', (evt) => {
        const target = evt.detail.target;
        if (!target) {
            return;
        }

        if (target.id === "personal-cards") {
            const prevButton = document.getElementById('prevBtn');
            if (prevButton) {
                const currentPage = parseInt(document.getElementById('page')?.value ?? '0', 10) || 0;
                prevButton.disabled = currentPage === 0;
            }
        }

        if (target.id === "personal-cards" || target.classList.contains("cb-card")) {
            updateVisibleCardCounts();
        }
    });


    const cmcMin = document.getElementById('cmcMin');
    const cmcMax = document.getElementById('cmcMax');
    const cmcMinVal = document.getElementById('cmcMinVal');
    const cmcMaxVal = document.getElementById('cmcMaxVal');

    // set initial positions
    cmcMin.value = 0;
    cmcMax.value = 16;
    cmcMinVal.textContent = '0';
    cmcMaxVal.textContent = '∞';

    cmcMin.addEventListener('input', () => {
        if (parseInt(cmcMin.value) > parseInt(cmcMax.value)) {
            cmcMin.value = cmcMax.value;
        }
        cmcMinVal.textContent = cmcMin.value;
    });

    cmcMax.addEventListener('input', () => {
        if (parseInt(cmcMax.value) < parseInt(cmcMin.value)) {
            cmcMax.value = cmcMin.value;
        }
        cmcMaxVal.textContent = parseInt(cmcMax.value) === 16 ? '∞' : cmcMax.value;
    });

    document.querySelectorAll('.pip').forEach(btn => {
        btn.addEventListener('click', () => toggle(btn));
    });

    function toggle(btn) {
        const isActive = btn.dataset.active === 'true';
        btn.dataset.active = !isActive;
        btn.style.opacity = isActive ? '0.4' : '1';
        btn.classList.toggle('ring-offset-2', !isActive);
        const checkbox = document.getElementById(btn.dataset.color);
    }

    updateVisibleCardCounts();
});
