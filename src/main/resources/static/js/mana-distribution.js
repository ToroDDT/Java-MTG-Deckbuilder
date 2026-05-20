/** Chart comes from `/js/chart.min.js` (UMD); bare `chart.js` imports do not work in browsers without a bundler/import map. */
const Chart = globalThis.Chart;

const manaDistribution = document.getElementById('myChart');
const colorProduction = document.getElementById('colorProduction');

const { data: manaCurveData, labels: manaCurveLabels } = globalThis.__MTG_MANA_CURVE__ ?? { data: [], labels: [] };

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
                data: manaCurveData.map(v => v * 1.72),
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




new Chart(colorProduction, {
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
                data: manaCurveData.map(v => v * 1.72),
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