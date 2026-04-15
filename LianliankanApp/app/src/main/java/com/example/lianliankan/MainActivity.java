package com.example.lianliankan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView scoreText;
    private TextView timeText;
    private Button btnRestart;
    private Button btnShuffle;
    private Button btnHint;
    
    private int score = 0;
    private int timeLeft = 180; // 3 minutes
    private Handler timerHandler = new Handler();
    private boolean isGameRunning = false;
    
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
        
        gameView.setOnGameCompleteListener(new GameView.OnGameCompleteListener() {
            @Override
            public void onGameComplete() {
                gameWin();
            }
            
            @Override
            public void onScoreUpdate(int points) {
                score += points;
                updateUI();
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
        
        startGame();
    }
    
    private void startGame() {
        score = 0;
        timeLeft = 180;
        isGameRunning = true;
        gameView.restartGame();
        updateUI();
        startTimer();
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
        new AlertDialog.Builder(this)
            .setTitle("恭喜通关！")
            .setMessage("你的得分: " + score + "\n剩余时间: " + timeLeft + "秒")
            .setPositiveButton("再玩一次", (dialog, which) -> restartGame())
            .setNegativeButton("退出", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }
    
    private void gameOver() {
        stopTimer();
        new AlertDialog.Builder(this)
            .setTitle("时间到！")
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
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (!isGameRunning && timeLeft > 0) {
            isGameRunning = true;
            startTimer();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
