document.addEventListener('DOMContentLoaded', () => {
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