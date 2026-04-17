package com.example.lianliankan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView scoreText;
    private TextView timeText;
    private Button btnRestart;
    private Button btnShuffle;
    private Button btnHint;
    private ToggleButton btnMusic;
    private Button btnSoundTheme;
    
    private int score = 0;
    private int timeLeft = 180;
    private Handler timerHandler = new Handler();
    private boolean isGameRunning = false;
    
    // 音乐系统
    private MediaPlayer bgmPlayer;
    private SoundPool soundPool;
    private int[][] soundThemes; // [theme][soundType]
    private int currentTheme = 0; // 0=经典, 1=卡通, 2=电子
    private final String[] themeNames = {"🎵经典", "🎈卡通", "⚡电子"};
    private boolean musicEnabled = true;
    
    // 音效类型常量
    private static final int SOUND_MATCH = 0;
    private static final int SOUND_WRONG = 1;
    private static final int SOUND_WIN = 2;
    private static final int SOUND_SELECT = 3;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        gameView = findViewById(R.id.gameView);
        scoreText = findViewById(R.id.scoreText);
        timeText = findViewById(R.id.timeText);
        btnRestart = findViewById(R.id.btnRestart);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnHint = findViewById(R.id.btnHint);
        btnMusic = findViewById(R.id.btnMusic);
        btnSoundTheme = findViewById(R.id.btnSoundTheme);
        
        // 初始化音乐
        initSounds();
        
        gameView.setOnGameEventListener(new GameView.OnGameEventListener() {
            @Override
            public void onGameComplete() {
                playWinSound();
                gameWin();
            }
            
            @Override
            public void onScoreUpdate(int points) {
                score += points;
                updateUI();
            }
            
            @Override
            public void onMatch() {
                playMatchSound();
            }
            
            @Override
            public void onWrong() {
                playWrongSound();
            }
            
            @Override
            public void onSelect() {
                playSelectSound();
            }
        });
        
        btnRestart.setOnClickListener(v -> restartGame());
        btnShuffle.setOnClickListener(v -> {
            gameView.shuffleBoard();
            score = Math.max(0, score - 5);
            updateUI();
        });
        btnHint.setOnClickListener(v -> {
            gameView.hint();
            score = Math.max(0, score - 3);
            updateUI();
        });
        
        btnMusic.setChecked(musicEnabled);
        btnMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            musicEnabled = isChecked;
            if (musicEnabled) {
                startBGM();
            } else {
                stopBGM();
            }
        });
        
        btnSoundTheme.setOnClickListener(v -> {
            currentTheme = (currentTheme + 1) % 3;
            btnSoundTheme.setText(themeNames[currentTheme]);
            Toast.makeText(this, "音效切换为: " + themeNames[currentTheme], Toast.LENGTH_SHORT).show();
        });
        
        startGame();
    }
    
    private void initSounds() {
        // 初始化SoundPool
        AudioAttributes attrs = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();
        
        soundPool = new SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build();
        
        // 3套音效主题 [theme][type]
        soundThemes = new int[3][4];
        
        // Theme 0: 经典音效
        soundThemes[0][SOUND_MATCH] = soundPool.load(this, R.raw.match, 1);
        soundThemes[0][SOUND_WRONG] = soundPool.load(this, R.raw.wrong, 1);
        soundThemes[0][SOUND_WIN] = soundPool.load(this, R.raw.win, 1);
        soundThemes[0][SOUND_SELECT] = soundPool.load(this, R.raw.select, 1);
        
        // Theme 1: 卡通音效
        soundThemes[1][SOUND_MATCH] = soundPool.load(this, R.raw.match_cartoon, 1);
        soundThemes[1][SOUND_WRONG] = soundPool.load(this, R.raw.wrong_cartoon, 1);
        soundThemes[1][SOUND_WIN] = soundPool.load(this, R.raw.win_cartoon, 1);
        soundThemes[1][SOUND_SELECT] = soundPool.load(this, R.raw.select_cartoon, 1);
        
        // Theme 2: 电子音效
        soundThemes[2][SOUND_MATCH] = soundPool.load(this, R.raw.match_electro, 1);
        soundThemes[2][SOUND_WRONG] = soundPool.load(this, R.raw.wrong_electro, 1);
        soundThemes[2][SOUND_WIN] = soundPool.load(this, R.raw.win_electro, 1);
        soundThemes[2][SOUND_SELECT] = soundPool.load(this, R.raw.select_electro, 1);
        
        // 初始化BGM MediaPlayer
        bgmPlayer = MediaPlayer.create(this, R.raw.bgm);
        bgmPlayer.setLooping(true);
        bgmPlayer.setVolume(0.5f, 0.5f);
    }
    
    private void startBGM() {
        if (bgmPlayer != null && musicEnabled && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }
    
    private void stopBGM() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }
    
    private void playMatchSound() {
        if (soundPool != null && musicEnabled) {
            soundPool.play(soundThemes[currentTheme][SOUND_MATCH], 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }
    
    private void playWrongSound() {
        if (soundPool != null && musicEnabled) {
            soundPool.play(soundThemes[currentTheme][SOUND_WRONG], 0.8f, 0.8f, 1, 0, 1.0f);
        }
    }
    
    private void playWinSound() {
        if (soundPool != null && musicEnabled) {
            soundPool.play(soundThemes[currentTheme][SOUND_WIN], 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }
    
    private void playSelectSound() {
        if (soundPool != null && musicEnabled) {
            soundPool.play(soundThemes[currentTheme][SOUND_SELECT], 0.6f, 0.6f, 1, 0, 1.0f);
        }
    }
    
    private void startGame() {
        score = 0;
        timeLeft = 180;
        isGameRunning = true;
        gameView.restartGame();
        updateUI();
        startTimer();
        startBGM();
    }
    
    private void restartGame() {
        stopTimer();
        startGame();
    }
    
    private void startTimer() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isGameRunning && timeLeft > 0) {
                    timeLeft--;
                    updateUI();
                    if (timeLeft == 0) {
                        gameOver();
                    } else {
                        timerHandler.postDelayed(this, 1000);
                    }
                }
            }
        }, 1000);
    }
    
    private void stopTimer() {
        isGameRunning = false;
        timerHandler.removeCallbacksAndMessages(null);
    }
    
    private void updateUI() {
        scoreText.setText("得分: " + score);
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timeText.setText(String.format("时间: %02d:%02d", minutes, seconds));
    }
    
    private void gameWin() {
        stopTimer();
        stopBGM();
        new AlertDialog.Builder(this)
            .setTitle("🎉 恭喜通关！")
            .setMessage("你的得分: " + score + "\n剩余时间: " + timeLeft + "秒")
            .setPositiveButton("再玩一次", (dialog, which) -> restartGame())
            .setNegativeButton("退出", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }
    
    private void gameOver() {
        stopTimer();
        stopBGM();
        new AlertDialog.Builder(this)
            .setTitle("⏰ 时间到！")
            .setMessage("你的得分: " + score)
            .setPositiveButton("再试一次", (dialog, which) -> restartGame())
            .setNegativeButton("退出", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        stopBGM();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (!isGameRunning && timeLeft > 0) {
            isGameRunning = true;
            startTimer();
            startBGM();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}