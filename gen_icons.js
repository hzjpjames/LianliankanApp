// Generate launcher icon PNGs at various resolutions using canvas
// Since we don't have sharp, we'll use a simpler approach - create vector XMLs for mipmap-anydpi
const fs = require('fs');
const path = require('path');

const resDir = path.join(__dirname, 'app', 'src', 'main', 'res');

// Update the adaptive icon files for mipmap-anydpi-v26
const foregroundXml = `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <!-- Horse body -->
    <path
        android:fillColor="#8D6E63"
        android:pathData="M30,68 Q30,54 44,54 L54,54 Q64,54 69,59 Q74,64 74,73 L74,78 L30,78 Z" />
    <!-- Horse neck and head -->
    <path
        android:fillColor="#8D6E63"
        android:pathData="M54,54 L59,39 Q61,29 69,29 Q77,29 79,39 L81,44 Q83,49 79,54 L69,59 Z" />
    <!-- Horse ear -->
    <path
        android:fillColor="#6D4C41"
        android:pathData="M67,32 L69,24 L73,30 Z" />
    <!-- Horse eye -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M71,36 Q71,34 73,34 Q75,34 75,36 Q75,38 73,38 Q71,38 71,36" />
    <path
        android:fillColor="#000000"
        android:pathData="M72,36 Q72,35 73,35 Q74,35 74,36 Q74,37 73,37 Q72,37 72,36" />
    <!-- Horse mane -->
    <path
        android:fillColor="#5D4037"
        android:pathData="M59,39 L57,29 L61,34 L63,26 L67,32 Z" />
    <!-- Horse tail -->
    <path
        android:fillColor="#5D4037"
        android:pathData="M30,64 Q20,59 22,49 Q24,54 28,59 Z" />
    <!-- Horse legs -->
    <path
        android:fillColor="#6D4C41"
        android:pathData="M35,78 L35,88 L41,88 L41,78" />
    <path
        android:fillColor="#6D4C41"
        android:pathData="M47,78 L47,88 L53,88 L53,78" />
    <path
        android:fillColor="#6D4C41"
        android:pathData="M61,78 L61,88 L67,88 L67,78" />
    <path
        android:fillColor="#6D4C41"
        android:pathData="M71,73 L71,83 L77,83 L77,73" />
    <!-- Hooves -->
    <path
        android:fillColor="#3E2723"
        android:pathData="M35,88 L41,88 L41,91 L35,91" />
    <path
        android:fillColor="#3E2723"
        android:pathData="M47,88 L53,88 L53,91 L47,91" />
    <path
        android:fillColor="#3E2723"
        android:pathData="M61,88 L67,88 L67,91 L61,91" />
    <path
        android:fillColor="#3E2723"
        android:pathData="M71,83 L77,83 L77,86 L71,86" />
</vector>`;

const backgroundXml = `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#4CAF50"
        android:pathData="M0,0h108v108h-108z" />
</vector>`;

// Write foreground
fs.writeFileSync(path.join(resDir, 'drawable', 'ic_launcher_foreground.xml'), foregroundXml);

// Write background 
fs.writeFileSync(path.join(resDir, 'drawable', 'ic_launcher_background.xml'), backgroundXml);

// Update mipmap XMLs for adaptive icons (API 26+)
const launcherXml = `<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>`;

const launcherRoundXml = `<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>`;

const anydpiDir = path.join(resDir, 'mipmap-anydpi-v26');
if (!fs.existsSync(anydpiDir)) fs.mkdirSync(anydpiDir, { recursive: true });
fs.writeFileSync(path.join(anydpiDir, 'ic_launcher.xml'), launcherXml);
fs.writeFileSync(path.join(anydpiDir, 'ic_launcher_round.xml'), launcherRoundXml);

console.log('Launcher icons updated!');
