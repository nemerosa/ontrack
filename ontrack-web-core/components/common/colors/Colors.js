export function numberToColorHsl(num) {
    // Map number to a hue
    const hue = (num * 137.50776405003785) % 360;
    const saturation = 50; // 50%
    const lightness = 60;  // 60%

    return hslToHex(hue, saturation, lightness);
}

function hslToHex(h, s, l) {
    // Convert HSL to values in [0,1]
    s /= 100;
    l /= 100;

    const k = n => (n + h / 30) % 12;
    const a = s * Math.min(l, 1 - l);
    const f = n =>
        l - a * Math.max(-1, Math.min(k(n) - 3, Math.min(9 - k(n), 1)));

    const r = Math.round(f(0) * 255);
    const g = Math.round(f(8) * 255);
    const b = Math.round(f(4) * 255);

    return rgbToHex(r, g, b);
}

function rgbToHex(r, g, b) {
    const toHex = x => x.toString(16).padStart(2, '0');
    return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
}

export function getTextColorForBackground(hexColor) {
    // Parse hex
    const r = parseInt(hexColor.slice(1, 3), 16) / 255;
    const g = parseInt(hexColor.slice(3, 5), 16) / 255;
    const b = parseInt(hexColor.slice(5, 7), 16) / 255;

    // Relative luminance approximation
    const L = 0.299 * r + 0.587 * g + 0.114 * b;

    return (L > 0.5) ? '#000000' : '#FFFFFF';
}