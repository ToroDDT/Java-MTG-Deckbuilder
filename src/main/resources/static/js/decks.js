    // Existing mobile nav toggle
    document.querySelector('.mobile-menu-toggle').addEventListener('click', function() {
    document.querySelector('nav').classList.toggle('active');
});

    // New Deck modal logic
    const modal = document.getElementById('newDeckModal');
    const openBtn = document.getElementById('openNewDeckModalBtn');
    const closeBtn = document.getElementById('closeNewDeckModalBtn');
    const cancelBtn = document.getElementById('cancelNewDeckBtn');

    function openNewDeckModal() {
    modal.classList.add('is-open');
    modal.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    const nameInput = document.getElementById('deckName');
    if (nameInput) nameInput.focus();
}

    function closeNewDeckModal() {
    modal.classList.remove('is-open');
    modal.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
}

    openBtn.addEventListener('click', openNewDeckModal);
    closeBtn.addEventListener('click', closeNewDeckModal);
    cancelBtn.addEventListener('click', closeNewDeckModal);

    modal.addEventListener('click', (e) => {
    if (e.target === modal) closeNewDeckModal();
});

    document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && modal.classList.contains('is-open')) closeNewDeckModal();
});

    // Existing Deck List mode toggles
    const modeSelect = document.getElementById('existingDeckListMode');
    const panelPaste = document.getElementById('existingListPaste');
    const panelFile = document.getElementById('existingListFile');
    const panelUrl = document.getElementById('existingListUrl');

    function syncExistingListPanels() {
    const mode = modeSelect.value;
    panelPaste.hidden = mode !== 'PASTE';
    panelFile.hidden = mode !== 'FILE';
    panelUrl.hidden = mode !== 'URL';
}

    modeSelect.addEventListener('change', syncExistingListPanels);
    syncExistingListPanels();