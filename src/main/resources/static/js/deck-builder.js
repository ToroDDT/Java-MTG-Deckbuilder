/* ═══════════════════════════════════════════════════
   COMMANDER FORGE — Deck Builder Modal JS
   ═══════════════════════════════════════════════════ */

/* ── Modal Open / Close ── */
function openDeckModal() {
    const overlay = document.getElementById('deckModal');
    overlay.classList.add('is-open');
    document.body.style.overflow = 'hidden';
    // Focus first input
    setTimeout(() => {
        const first = overlay.querySelector('input:not([type=hidden])');
        if (first) first.focus();
    }, 80);
}

function closeDeckModal() {
    const overlay = document.getElementById('deckModal');
    overlay.classList.remove('is-open');
    document.body.style.overflow = '';
}

// Close on backdrop click
document.getElementById('deckModal').addEventListener('click', function(e) {
    if (e.target === this) closeDeckModal();
});

// Close on Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeDeckModal();
});

/* ── Slider outputs ── */
function updateSlider(input) {
    // Find the sibling <output>
    const output = input.nextElementSibling;
    if (output) output.textContent = input.value;
}

// Init all sliders on load
document.querySelectorAll('.slider').forEach(function(slider) {
    const output = slider.nextElementSibling;
    if (output) output.textContent = slider.value;
    slider.addEventListener('input', function() {
        if (output) output.textContent = this.value;
        validateCounts();
    });
});

/* ── Ideal Counts Validation ── */
function getSliderVal(name) {
    const el = document.querySelector('[name="' + name + '"]');
    return el ? parseInt(el.value) || 0 : 0;
}

function validateCounts() {
    const warning = document.getElementById('idealsWarning');
    if (!warning) return;
    const lands = getSliderVal('lands');
    const creatures = getSliderVal('creatures');
    const spells = getSliderVal('ramp') + getSliderVal('removal') + getSliderVal('wipes')
        + getSliderVal('card_advantage') + getSliderVal('protection');
    const estimated = lands + creatures + Math.ceil(spells / 2);
    if (estimated > 99) {
        warning.textContent = '⚠ Estimated total ~' + estimated + ' cards — reduce counts to avoid build issues.';
        warning.style.display = 'block';
        warning.style.borderColor = 'rgba(239,68,68,.4)';
    } else if (estimated > 90) {
        warning.textContent = '⚠ Estimated total ~' + estimated + ' cards — deck may be tight on slots.';
        warning.style.display = 'block';
        warning.style.borderColor = 'rgba(245,158,11,.4)';
        warning.style.color = '#fbbf24';
    } else {
        warning.style.display = 'none';
    }
}
validateCounts();

/* ── Commander Autocomplete (stub — wire to your backend) ── */
let commanderDebounce;
function fetchCommanderCandidates(val) {
    clearTimeout(commanderDebounce);
    const list = document.getElementById('commanderCandidates');
    if (!val || val.length < 2) { list.innerHTML = ''; list.classList.remove('has-items'); return; }
    commanderDebounce = setTimeout(function() {
        // Replace this fetch with your actual Thymeleaf/HTMX endpoint:
        // fetch('/build/new/candidates?q=' + encodeURIComponent(val))
        //   .then(r => r.json()).then(renderCandidates);
        // For demo, show placeholder items:
        const demo = ['Atraxa, Praetors\' Voice', 'Arcades, the Strategist', 'Aragorn, the Uniter'];
        const filtered = demo.filter(c => c.toLowerCase().includes(val.toLowerCase()));
        renderCandidates(filtered);
    }, 220);
}

function renderCandidates(names) {
    const list = document.getElementById('commanderCandidates');
    if (!names.length) { list.innerHTML = ''; list.classList.remove('has-items'); return; }
    list.innerHTML = names.map(function(n) {
        return '<div class="autocomplete-item" role="option" tabindex="0" onclick="selectCommander(\'' +
            n.replace(/'/g, "\\'") + '\')">' + n + '</div>';
    }).join('');
    list.classList.add('has-items');
}

function selectCommander(name) {
    document.getElementById('commander').value = name;
    const list = document.getElementById('commanderCandidates');
    list.innerHTML = '';
    list.classList.remove('has-items');
    // Update preview placeholder text
    const preview = document.getElementById('commanderPreview');
    if (preview) {
        preview.innerHTML = '<div class="preview-placeholder"><span>⟡</span><small style="color:var(--gold-light);">' + name + '</small></div>';
    }
}

/* ── Custom Theme Chips ── */
let customThemes = [];
const MAX_THEMES = 8;

function addCustomTheme() {
    const input = document.getElementById('customThemeInput');
    const val = input.value.trim();
    if (!val) return;
    if (customThemes.length >= MAX_THEMES) { alert('Maximum 8 themes reached.'); return; }
    if (customThemes.includes(val)) { input.value = ''; return; }
    customThemes.push(val);
    input.value = '';
    renderThemeChips();
}

function removeTheme(name) {
    customThemes = customThemes.filter(t => t !== name);
    renderThemeChips();
}

function renderThemeChips() {
    const container = document.getElementById('themeChips');
    const count = document.getElementById('themeCount');
    if (count) count.textContent = customThemes.length + ' / ' + MAX_THEMES;
    container.innerHTML = customThemes.map(function(t) {
        return '<span class="theme-chip">' + t +
            '<button type="button" class="theme-chip-remove" onclick="removeTheme(\'' + t.replace(/'/g, "\\'") + '\')" aria-label="Remove">✕</button>' +
            '<input type="hidden" name="custom_themes" value="' + t + '">' +
            '</span>';
    }).join('');
}

/* ── Include / Exclude card chips ── */
function syncChips(type) {
    const textarea = document.getElementById(type + 'Cards');
    const chips = document.getElementById(type + 'Chips');
    const counter = document.getElementById(type + 'Count');
    const max = type === 'include' ? 10 : 15;
    if (!textarea) return;
    const lines = textarea.value.split('\n').map(l => l.trim()).filter(Boolean);
    const shown = lines.slice(0, max);
    if (counter) counter.textContent = shown.length + ' / ' + max;
    if (chips) {
        chips.innerHTML = shown.map(function(card) {
            return '<span class="ie-chip">' + card + '</span>';
        }).join('');
    }
}

/* ── Combo config toggle ── */
function toggleComboConfig(checked) {
    const config = document.getElementById('comboConfig');
    if (config) config.classList.toggle('is-open', checked);
}

/* ── Skip checkbox mutual exclusivity ── */
(function initSkipExclusivity() {
    const groups = {
        landGroup: ['skip_lands', 'skip_to_misc'],
        landSub:   ['skip_basics', 'skip_staples', 'skip_kindred', 'skip_fetches', 'skip_duals', 'skip_triomes'],
        creatureGroup: ['skip_all_creatures'],
        creatureSub:   ['skip_creature_primary', 'skip_creature_secondary', 'skip_creature_fill'],
        spellGroup: ['skip_all_spells'],
        spellSub:   ['skip_ramp', 'skip_removal', 'skip_wipes', 'skip_card_advantage', 'skip_protection', 'skip_spell_fill'],
    };

    function getChecked(names) {
        return names.some(function(n) {
            const el = document.querySelector('[name="' + n + '"]');
            return el && el.checked;
        });
    }

    function setDisabled(names, disabled) {
        names.forEach(function(n) {
            const el = document.querySelector('[name="' + n + '"]');
            if (el) {
                el.disabled = disabled;
                const label = el.closest('.check-label');
                if (label) label.style.opacity = disabled ? '.4' : '1';
            }
        });
    }

    function applyRules() {
        const landGroupOn = getChecked(groups.landGroup);
        const landSubOn   = getChecked(groups.landSub);
        setDisabled(groups.landSub,   landGroupOn);
        setDisabled(groups.landGroup, landSubOn);

        const creGroupOn = getChecked(groups.creatureGroup);
        const creSubOn   = getChecked(groups.creatureSub);
        setDisabled(groups.creatureSub,   creGroupOn);
        setDisabled(groups.creatureGroup, creSubOn);

        const spellGroupOn = getChecked(groups.spellGroup);
        const spellSubOn   = getChecked(groups.spellSub);
        setDisabled(groups.spellSub,   spellGroupOn);
        setDisabled(groups.spellGroup, spellSubOn);
    }

    document.querySelectorAll('.check-label input[type=checkbox]').forEach(function(cb) {
        cb.addEventListener('change', applyRules);
    });
    applyRules();
})();