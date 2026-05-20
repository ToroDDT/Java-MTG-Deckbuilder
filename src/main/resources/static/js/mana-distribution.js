/** Chart comes from `/js/chart.min.js` (UMD); bare `chart.js` imports do not work in browsers without a bundler/import map. */
const Chart = globalThis.Chart;

const manaDistribution = document.getElementById('myChart');
const colorProductionCanvas = document.getElementById('colorProduction');

const { data: manaCurveData, labels: manaCurveLabels } = globalThis.__MTG_MANA_CURVE__ ?? { data: [], labels: [] };
const { data: colorProdData, labels: colorProdLabels } = globalThis.__MTG_COLOR_PRODUCTION__ ?? { data: [], labels: [] };

/** WUBRG + colorless — matches label order from server */
const COLOR_BAR_STYLES = [
    { bg: 'rgba(220, 38, 38, 0.18)', border: 'rgba(220, 38, 38, 0.85)', hover: 'rgba(220, 38, 38, 0.32)' },
    { bg: 'rgba(241, 245, 249, 0.95)', border: 'rgba(148, 163, 184, 0.85)', hover: 'rgba(226, 232, 240, 1)' },
    { bg: 'rgba(22, 163, 74, 0.18)', border: 'rgba(22, 163, 74, 0.85)', hover: 'rgba(22, 163, 74, 0.32)' },
    { bg: 'rgba(30, 41, 59, 0.4)', border: 'rgba(15, 23, 42, 0.9)', hover: 'rgba(30, 41, 59, 0.55)' },
    { bg: 'rgba(37, 99, 235, 0.15)', border: 'rgba(37, 99, 235, 0.85)', hover: 'rgba(37, 99, 235, 0.28)' },
    { bg: 'rgba(148, 163, 184, 0.28)', border: 'rgba(100, 116, 139, 0.85)', hover: 'rgba(148, 163, 184, 0.42)' }
];

new Chart(manaDistribution, {
    type: 'bar',
    data: {
        labels: manaCurveLabels,
        datasets: [
            {
                label: 'Cards',
                data: manaCurveData,
                type: 'bar',
                order: 2,
                backgroundColor: 'rgba(37, 99, 235, 0.12)',
                borderColor: 'rgba(37, 99, 235, 0.8)',
                borderWidth: 1.5,
                borderRadius: 6,
                borderSkipped: false,
                hoverBackgroundColor: 'rgba(37, 99, 235, 0.25)'
            },
            {
                label: 'Distribution',
                data: manaCurveData.map(v => Number(v) * 1.72),
                type: 'line',
                order: 1,
                borderColor: 'rgba(99, 153, 34, 0.85)',
                borderWidth: 2,
                pointRadius: 0,
                pointHoverRadius: 4,
                fill: false,
                tension: 0.45
            }
        ]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                backgroundColor: '#fff',
                borderColor: 'rgba(0,0,0,0.08)',
                borderWidth: 1,
                titleColor: '#0f172a',
                bodyColor: '#64748b',
                titleFont: { family: 'Inter, sans-serif', size: 11, weight: '700' },
                bodyFont: { family: 'Inter, sans-serif', size: 11 },
                padding: 10,
                cornerRadius: 10,
                displayColors: false,
                callbacks: {
                    title: (items) => 'CMC ' + items[0].label,
                    label: (item) => item.raw + ' cards'
                }
            }
        },
        scales: {
            x: {
                grid: { display: false },
                border: { display: false },
                ticks: { color: '#94a3b8', font: { family: 'Inter, sans-serif', size: 11, weight: '600' } }
            },
            y: {
                beginAtZero: true,
                grid: { color: 'rgba(0,0,0,0.04)' },
                border: { display: false },
                ticks: { color: '#94a3b8', font: { family: 'Inter, sans-serif', size: 11 }, stepSize: 5, maxTicksLimit: 5 }
            }
        }
    }
});

const colorBarBg = colorProdLabels.map((_, i) => (COLOR_BAR_STYLES[i] ?? COLOR_BAR_STYLES[COLOR_BAR_STYLES.length - 1]).bg);
const colorBarBorder = colorProdLabels.map((_, i) => (COLOR_BAR_STYLES[i] ?? COLOR_BAR_STYLES[COLOR_BAR_STYLES.length - 1]).border);
const colorBarHover = colorProdLabels.map((_, i) => (COLOR_BAR_STYLES[i] ?? COLOR_BAR_STYLES[COLOR_BAR_STYLES.length - 1]).hover);

new Chart(colorProductionCanvas, {
    type: 'bar',
    data: {
        labels: colorProdLabels,
        datasets: [
            {
                label: 'Color weight',
                data: colorProdData,
                type: 'bar',
                order: 2,
                backgroundColor: colorBarBg,
                borderColor: colorBarBorder,
                borderWidth: 1.5,
                borderRadius: 6,
                borderSkipped: false,
                hoverBackgroundColor: colorBarHover
            },
            {
                label: 'Distribution',
                data: colorProdData.map(v => Number(v) * 1.72),
                type: 'line',
                order: 1,
                borderColor: 'rgba(99, 153, 34, 0.85)',
                borderWidth: 2,
                pointRadius: 0,
                pointHoverRadius: 4,
                fill: false,
                tension: 0.45
            }
        ]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                backgroundColor: '#fff',
                borderColor: 'rgba(0,0,0,0.08)',
                borderWidth: 1,
                titleColor: '#0f172a',
                bodyColor: '#64748b',
                titleFont: { family: 'Inter, sans-serif', size: 11, weight: '700' },
                bodyFont: { family: 'Inter, sans-serif', size: 11 },
                padding: 10,
                cornerRadius: 10,
                displayColors: false,
                callbacks: {
                    title: (items) => String(items[0].label),
                    label: (item) => item.raw + ' (color symbols)'
                }
            }
        },
        scales: {
            x: {
                grid: { display: false },
                border: { display: false },
                ticks: { color: '#94a3b8', font: { family: 'Inter, sans-serif', size: 11, weight: '600' } }
            },
            y: {
                beginAtZero: true,
                grid: { color: 'rgba(0,0,0,0.04)' },
                border: { display: false },
                ticks: { color: '#94a3b8', font: { family: 'Inter, sans-serif', size: 11 }, maxTicksLimit: 8 }
            }
        }
    }
});
