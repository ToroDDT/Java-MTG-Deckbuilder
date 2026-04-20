document.addEventListener('DOMContentLoaded', () => {
    document.body.addEventListener("htmx:afterSwap", function(evt) {
        const lastIdHolder = document.getElementById("lastIdHolder");
        if (!lastIdHolder) return; // guard for other swaps

        const lastId = lastIdHolder.dataset.lastid;
        if (!lastId) return;

        document.getElementById("lastId").value = lastId;
        console.log("lastId updated to:", lastId);
    });

    document.body.addEventListener("click", function(evt) {
        if (evt.target.id === "prevBtn") {
            document.getElementById("operator").value = ">";
            console.log("this is working ")
            htmx.trigger("#librarySearchForm", "submit");
        }
        if (evt.target.id === "nextBtn") {
            document.getElementById("operator").value = "<";
            console.log("this is working ")
            htmx.trigger("#librarySearchForm", "submit");
        }
    });
    document.body.addEventListener("htmx:configRequest", function(evt) {})
    const pageHistory = []; // stack of previous lastIds

    function changePage(direction) {
        const currentLastId = document.getElementById('lastId')?.getAttribute('value') ?? '';

        if (direction === 'next') {
            // Push current cursor onto history before moving forward
            pageHistory.push(document.getElementById('lastIdInput').value);
            document.getElementById('lastIdInput').value = currentLastId;

        } else if (direction === 'prev') {
            // Pop the last cursor off the stack to go back
            const previous = pageHistory.pop() ?? '';
            document.getElementById('lastIdInput').value = previous;
        }

        // Disable prev button if no history
        document.getElementById('btn-prev').disabled = pageHistory.length === 0;

        htmx.trigger('#librarySearchForm', 'change');
    }

// Re-run after every HTMX swap to keep prev button state correct
    document.addEventListener('htmx:afterSwap', () => {
        document.getElementById('btn-prev').disabled = pageHistory.length === 0;
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
});