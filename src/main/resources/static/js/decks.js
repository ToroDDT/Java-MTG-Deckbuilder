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

    function toggleWhite(element){
        console.log(element.classList);
       if (element.classList.contains("color-W")){
            element.classList.remove("color-W");
            element.classList.add("btn-filter");
           console.log(element.classList);
        }
        else{
            element.classList.toggle("btn-filter-white-selected");
        }
    }

    function toggleRed(element){
        console.log(element.classList);
        if (element.classList.contains("color-R")){
            element.classList.remove("color-R");
            element.classList.add("btn-filter");
            console.log(element.classList);
        }
        else{
            element.classList.toggle("btn-filter-red-selected");
        }
    }

    function toggleBlue(element){
        console.log(element.classList);
        if (element.classList.contains("color-U")){
            element.classList.remove("color-U");
            element.classList.add("btn-filter");
            console.log(element.classList);
        }
        else{
            element.classList.toggle("btn-filter-blue-selected");
        }
    }

    function toggleBlack(element){
        console.log(element.classList);
        if (element.classList.contains("color-B")){
            element.classList.remove("color-B");
            element.classList.add("btn-filter");
            console.log(element.classList);
        }
        else{
            element.classList.toggle("btn-filter-black-selected");
        }
    }

    function toggleGreen(element){
        console.log(element.classList);
        if (element.classList.contains("color-G")){
            element.classList.remove("color-G");
            element.classList.add("btn-filter");
            console.log(element.classList);
        }
        else{
            element.classList.toggle("btn-filter-green-selected");
        }
    }

    modeSelect.addEventListener('change', syncExistingListPanels);
    syncExistingListPanels();