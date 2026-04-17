// Generate WAV sound effects for 3 themes
const fs = require('fs');
const path = require('path');

const rawDir = path.join(__dirname, 'app', 'src', 'main', 'res', 'raw');

function writeWav(filePath, samples, sampleRate = 22050) {
    const numSamples = samples.length;
    const byteRate = sampleRate * 2; // 16-bit mono
    const blockAlign = 2;
    const dataSize = numSamples * 2;
    const fileSize = 44 + dataSize;
    
    const buffer = Buffer.alloc(fileSize);
    let offset = 0;
    
    // RIFF header
    buffer.write('RIFF', offset); offset += 4;
    buffer.writeUInt32LE(fileSize - 8, offset); offset += 4;
    buffer.write('WAVE', offset); offset += 4;
    
    // fmt chunk
    buffer.write('fmt ', offset); offset += 4;
    buffer.writeUInt32LE(16, offset); offset += 4;
    buffer.writeUInt16LE(1, offset); offset += 2; // PCM
    buffer.writeUInt16LE(1, offset); offset += 2; // mono
    buffer.writeUInt32LE(sampleRate, offset); offset += 4;
    buffer.writeUInt32LE(byteRate, offset); offset += 4;
    buffer.writeUInt16LE(blockAlign, offset); offset += 2;
    buffer.writeUInt16LE(16, offset); offset += 2; // bits per sample
    
    // data chunk
    buffer.write('data', offset); offset += 4;
    buffer.writeUInt32LE(dataSize, offset); offset += 4;
    
    for (let i = 0; i < numSamples; i++) {
        const s = Math.max(-1, Math.min(1, samples[i]));
        buffer.writeInt16LE(Math.round(s * 32767), offset);
        offset += 2;
    }
    
    fs.writeFileSync(filePath, buffer);
}

// Envelope helper
function envelope(t, attack, decay, sustain, release, duration) {
    if (t < attack) return t / attack;
    if (t < attack + decay) return 1 - (1 - sustain) * ((t - attack) / decay);
    if (t < duration - release) return sustain;
    if (t < duration) return sustain * ((duration - t) / release);
    return 0;
}

// ====== THEME 0: Classic (already exists, just placeholder) ======
// We keep the existing files

// ====== THEME 1: Cartoon (bouncy, playful) ======

// Cartoon match - ascending boing
function cartoonMatch(duration = 0.3, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const freq = 400 + 800 * (t / duration); // rising pitch
        const env = envelope(t, 0.01, 0.05, 0.6, 0.1, duration);
        const vib = 1 + 0.05 * Math.sin(2 * Math.PI * 8 * t); // vibrato
        samples[i] = env * 0.5 * Math.sin(2 * Math.PI * freq * vib * t);
    }
    return samples;
}

// Cartoon wrong - descending whoop
function cartoonWrong(duration = 0.4, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const freq = 600 - 400 * (t / duration); // falling pitch
        const env = envelope(t, 0.01, 0.1, 0.4, 0.15, duration);
        samples[i] = env * 0.4 * Math.sin(2 * Math.PI * freq * t);
    }
    return samples;
}

// Cartoon win - fanfare
function cartoonWin(duration = 1.0, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    const notes = [523, 659, 784, 1047]; // C5 E5 G5 C6
    const noteDur = duration / notes.length;
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const noteIdx = Math.min(Math.floor(t / noteDur), notes.length - 1);
        const noteT = t - noteIdx * noteDur;
        const freq = notes[noteIdx];
        const env = envelope(noteT, 0.01, 0.05, 0.7, 0.05, noteDur);
        samples[i] = env * 0.5 * (Math.sin(2 * Math.PI * freq * noteT) + 0.3 * Math.sin(2 * Math.PI * freq * 2 * noteT));
    }
    return samples;
}

// Cartoon select - pop
function cartoonSelect(duration = 0.12, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const freq = 800 + 1200 * Math.exp(-t * 30);
        const env = Math.exp(-t * 25);
        samples[i] = env * 0.4 * Math.sin(2 * Math.PI * freq * t);
    }
    return samples;
}

// ====== THEME 2: Electronic (synthy, digital) ======

// Electro match - blip sweep up
function electroMatch(duration = 0.25, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const freq = 300 + 1500 * (t / duration);
        const env = envelope(t, 0.005, 0.05, 0.5, 0.08, duration);
        // Square wave approximation
        const sq = Math.sin(2 * Math.PI * freq * t) > 0 ? 1 : -1;
        samples[i] = env * 0.4 * sq;
    }
    return samples;
}

// Electro wrong - buzzer
function electroWrong(duration = 0.35, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const freq = 150;
        const env = envelope(t, 0.005, 0.05, 0.4, 0.1, duration);
        // Buzz = square + sawtooth
        const sq = Math.sin(2 * Math.PI * freq * t) > 0 ? 1 : -1;
        const saw = 2 * (freq * t - Math.floor(freq * t + 0.5));
        samples[i] = env * 0.35 * (0.6 * sq + 0.4 * saw);
    }
    return samples;
}

// Electro win - arpeggio synth
function electroWin(duration = 1.2, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    const notes = [261, 329, 392, 523, 659, 784, 1047]; // C4..C6 arpeggio
    const noteDur = duration / notes.length;
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const noteIdx = Math.min(Math.floor(t / noteDur), notes.length - 1);
        const noteT = t - noteIdx * noteDur;
        const freq = notes[noteIdx];
        const env = envelope(noteT, 0.005, 0.02, 0.6, 0.03, noteDur);
        const sq = Math.sin(2 * Math.PI * freq * noteT) > 0 ? 1 : -1;
        samples[i] = env * 0.45 * (0.7 * Math.sin(2 * Math.PI * freq * noteT) + 0.3 * sq);
    }
    return samples;
}

// Electro select - click beep
function electroSelect(duration = 0.08, sr = 22050) {
    const n = Math.floor(sr * duration);
    const samples = new Float64Array(n);
    for (let i = 0; i < n; i++) {
        const t = i / sr;
        const freq = 1000;
        const env = Math.exp(-t * 50);
        const sq = Math.sin(2 * Math.PI * freq * t) > 0 ? 1 : -1;
        samples[i] = env * 0.35 * sq;
    }
    return samples;
}

// Generate cartoon sounds
writeWav(path.join(rawDir, 'match_cartoon.wav'), cartoonMatch());
writeWav(path.join(rawDir, 'wrong_cartoon.wav'), cartoonWrong());
writeWav(path.join(rawDir, 'win_cartoon.wav'), cartoonWin());
writeWav(path.join(rawDir, 'select_cartoon.wav'), cartoonSelect());

// Generate electro sounds
writeWav(path.join(rawDir, 'match_electro.wav'), electroMatch());
writeWav(path.join(rawDir, 'wrong_electro.wav'), electroWrong());
writeWav(path.join(rawDir, 'win_electro.wav'), electroWin());
writeWav(path.join(rawDir, 'select_electro.wav'), electroSelect());

console.log('All sound effects generated!');
console.log('Cartoon: match_cartoon.wav, wrong_cartoon.wav, win_cartoon.wav, select_cartoon.wav');
console.log('Electro: match_electro.wav, wrong_electro.wav, win_electro.wav, select_electro.wav');
