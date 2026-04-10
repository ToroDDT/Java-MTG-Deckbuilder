/**
 * Dynamically fans cards based on however many are rendered.
 * Works for 1–N cards. Spreads them symmetrically around center.
 */
(function () {
    const CARD_WIDTH   = 158;   // px — matches w-[158px]
    const MAX_ROTATION = 16;    // degrees max tilt at edges
    const VERTICAL_DIP = 24;    // px — how much edge cards dip down
    const OVERLAP      = 60;    // px — how much cards overlap each other

    const cards = document.querySelectorAll('.card-wrap');
    const total = cards.length;
    if (total === 0) return;

    const container = document.getElementById('hand-container');
    const containerWidth = container.offsetWidth;
    const centerX = containerWidth / 2;

    // Total spread width so the whole hand is centered
    const spreadWidth = (total - 1) * (CARD_WIDTH - OVERLAP);
    const startX = centerX - spreadWidth / 2 - CARD_WIDTH / 2;

    cards.forEach((card, i) => {
        // Normalised position: -1 (leftmost) → 0 (center) → 1 (rightmost)
        const norm = total === 1 ? 0 : (i / (total - 1)) * 2 - 1;

        const rotation  = norm * MAX_ROTATION;
        const dipY      = Math.abs(norm) * VERTICAL_DIP;
        const leftPx    = startX + i * (CARD_WIDTH - OVERLAP);

        // Base transform
        card.style.left      = `${leftPx}px`;
        card.style.transform = `rotate(${rotation}deg) translateY(${dipY}px)`;
        card.style.zIndex    = total - Math.round(Math.abs(norm) * total);

        // Hover: lift straight up regardless of rotation
        card.addEventListener('mouseenter', () => {
            card.style.transform = `rotate(${rotation}deg) translateY(-44px) scale(1.06)`;
            card.style.zIndex    = 100;
        });
        card.addEventListener('mouseleave', () => {
            card.style.transform = `rotate(${rotation}deg) translateY(${dipY}px)`;
            card.style.zIndex    = total - Math.round(Math.abs(norm) * total);
        });
    });
})();
