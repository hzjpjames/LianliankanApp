package com.example.lianliankan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameView extends View {
    private static final int ROWS = 8;
    private static final int COLS = 6;
    private static final int PADDING = 8;
    
    private int cellWidth;
    private int cellHeight;
    private int[][] board;
    private boolean[][] selected;
    private Paint paint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint bgPaint;
    
    private int firstRow = -1, firstCol = -1;
    private int secondRow = -1, secondCol = -1;
    
    private List<int[]> path;
    private boolean isAnimating = false;
    
    // 十二生肖动物 emoji
    private String[] zodiacAnimals = {
        "🐭", "🐮", "🐯", "🐰", "🐲", "🐍", "🐴", "🐑", "🐵", "🐔", "🐶", "🐷"
    };
    
    // 每种动物对应的背景颜色
    private int[] animalColors = {
        0xFFE1BEE7, // 鼠 - 淡紫
        0xFFD7CCC8, // 牛 - 淡棕
        0xFFFFE0B2, // 虎 - 橙色
        0xFFF8BBD0, // 兔 - 粉红
        0xFFFFF9C4, // 龙 - 金黄
        0xFFC8E6C9, // 蛇 - 淡绿
        0xFFFFCDD2, // 马 - 淡红
        0xFFFFF3E0, // 羊 - 奶油
        0xFFFFFDE7, // 猴 - 淡黄
        0xFFE1F5FE, // 鸡 - 淡蓝
        0xFFBBDEFB, // 狗 - 天蓝
        0xFFFCE4EC  // 猪 - 淡粉
    };
    
    private OnGameEventListener listener;
    
    public interface OnGameEventListener {
        void onGameComplete();
        void onScoreUpdate(int score);
        void onMatch();
        void onWrong();
        void onSelect();
    }
    
    public void setOnGameEventListener(OnGameEventListener listener) {
        this.listener = listener;
    }
    
    public GameView(Context context) {
        super(context);
        init();
    }
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#FFD700"));
        linePaint.setStrokeWidth(6);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#1B5E20"));
        
        board = new int[ROWS][COLS];
        selected = new boolean[ROWS][COLS];
        path = new ArrayList<>();
        
        initBoard();
    }
    
    private void initBoard() {
        List<Integer> cards = new ArrayList<>();
        int pairs = (ROWS * COLS) / 2;
        
        for (int i = 0; i < pairs; i++) {
            int iconIndex = i % 12; // 12生肖
            cards.add(iconIndex);
            cards.add(iconIndex);
        }
        
        Collections.shuffle(cards);
        
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = cards.get(index++);
            }
        }
        
        while (!hasValidMoves()) {
            Collections.shuffle(cards);
            index = 0;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    board[i][j] = cards.get(index++);
                }
            }
        }
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellWidth = (w - PADDING * 2) / COLS;
        cellHeight = (h - PADDING * 2) / ROWS;
        textPaint.setTextSize(Math.min(cellWidth, cellHeight) * 0.5f);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -1) {
                    drawCell(canvas, i, j);
                }
            }
        }
        
        if (path != null && path.size() > 1) {
            for (int i = 0; i < path.size() - 1; i++) {
                int[] p1 = path.get(i);
                int[] p2 = path.get(i + 1);
                canvas.drawLine(
                    PADDING + p1[1] * cellWidth + cellWidth / 2f,
                    PADDING + p1[0] * cellHeight + cellHeight / 2f,
                    PADDING + p2[1] * cellWidth + cellWidth / 2f,
                    PADDING + p2[0] * cellHeight + cellHeight / 2f,
                    linePaint
                );
            }
        }
    }
    
    private void drawCell(Canvas canvas, int row, int col) {
        float left = PADDING + col * cellWidth + 3;
        float top = PADDING + row * cellHeight + 3;
        float right = left + cellWidth - 6;
        float bottom = top + cellHeight - 6;
        
        RectF rect = new RectF(left, top, right, bottom);
        
        // 选中效果 - 金色边框
        if (selected[row][col]) {
            paint.setColor(Color.parseColor("#FFD700"));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(rect, 10, 10, paint);
        } else {
            // 动物对应的背景色
            paint.setColor(animalColors[board[row][col]]);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(rect, 10, 10, paint);
        }
        
        // 边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#1565C0"));
        paint.setStrokeWidth(2);
        canvas.drawRoundRect(rect, 10, 10, paint);
        paint.setStyle(Paint.Style.FILL);
        
        // 绘制生肖emoji
        textPaint.setColor(Color.BLACK);
        textPaint.setFakeBoldText(false);
        String animal = zodiacAnimals[board[row][col]];
        canvas.drawText(animal, (left + right) / 2f, (top + bottom) / 2f + textPaint.getTextSize() / 3f, textPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isAnimating) {
            int col = (int) ((event.getX() - PADDING) / cellWidth);
            int row = (int) ((event.getY() - PADDING) / cellHeight);
            
            if (row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] != -1) {
                handleCellClick(row, col);
            }
        }
        return true;
    }
    
    private void handleCellClick(int row, int col) {
        if (firstRow == -1) {
            firstRow = row;
            firstCol = col;
            selected[row][col] = true;
            if (listener != null) listener.onSelect();
            invalidate();
        } else if (firstRow == row && firstCol == col) {
            selected[row][col] = false;
            firstRow = -1;
            firstCol = -1;
            invalidate();
        } else if (board[firstRow][firstCol] == board[row][col]) {
            secondRow = row;
            secondCol = col;
            selected[row][col] = true;
            
            path = findPath(firstRow, firstCol, secondRow, secondCol);
            
            if (path != null) {
                isAnimating = true;
                invalidate();
                
                final int fRow1 = firstRow, fCol1 = firstCol;
                final int fRow2 = secondRow, fCol2 = secondCol;
                
                postDelayed(() -> {
                    board[fRow1][fCol1] = -1;
                    board[fRow2][fCol2] = -1;
                    selected[fRow1][fCol1] = false;
                    selected[fRow2][fCol2] = false;
                    firstRow = -1;
                    firstCol = -1;
                    secondRow = -1;
                    secondCol = -1;
                    path = null;
                    isAnimating = false;
                    invalidate();
                    
                    if (listener != null) {
                        listener.onScoreUpdate(10);
                        listener.onMatch();
                    }
                    
                    if (isGameComplete()) {
                        if (listener != null) listener.onGameComplete();
                    } else if (!hasValidMoves()) {
                        shuffleBoard();
                        Toast.makeText(getContext(), "没有可消除的组合，重新洗牌！", Toast.LENGTH_SHORT).show();
                    }
                }, 300);
            } else {
                selected[firstRow][firstCol] = false;
                selected[row][col] = false;
                if (listener != null) listener.onWrong();
                firstRow = -1;
                firstCol = -1;
                invalidate();
            }
        } else {
            selected[firstRow][firstCol] = false;
            firstRow = row;
            firstCol = col;
            selected[row][col] = true;
            if (listener != null) listener.onSelect();
            invalidate();
        }
    }
    
    private List<int[]> findPath(int r1, int c1, int r2, int c2) {
        if (canConnectDirectly(r1, c1, r2, c2)) {
            List<int[]> p = new ArrayList<>();
            p.add(new int[]{r1, c1});
            p.add(new int[]{r2, c2});
            return p;
        }
        
        for (int i = -1; i <= ROWS; i++) {
            for (int j = -1; j <= COLS; j++) {
                if ((i == r1 && j == c1) || (i == r2 && j == c2)) continue;
                if ((i < 0 || i >= ROWS || j < 0 || j >= COLS || board[i][j] == -1) &&
                    canConnectDirectly(r1, c1, i, j) && canConnectDirectly(i, j, r2, c2)) {
                    List<int[]> p = new ArrayList<>();
                    p.add(new int[]{r1, c1});
                    p.add(new int[]{i, j});
                    p.add(new int[]{r2, c2});
                    return p;
                }
            }
        }
        
        for (int i = -1; i <= ROWS; i++) {
            for (int j = -1; j <= COLS; j++) {
                if ((i < 0 || i >= ROWS || j < 0 || j >= COLS || board[i][j] == -1) &&
                    canConnectDirectly(r1, c1, i, j)) {
                    for (int i2 = -1; i2 <= ROWS; i2++) {
                        for (int j2 = -1; j2 <= COLS; j2++) {
                            if ((i2 < 0 || i2 >= ROWS || j2 < 0 || j2 >= COLS || board[i2][j2] == -1) &&
                                canConnectDirectly(i, j, i2, j2) && canConnectDirectly(i2, j2, r2, c2)) {
                                List<int[]> p = new ArrayList<>();
                                p.add(new int[]{r1, c1});
                                p.add(new int[]{i, j});
                                p.add(new int[]{i2, j2});
                                p.add(new int[]{r2, c2});
                                return p;
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    private boolean canConnectDirectly(int r1, int c1, int r2, int c2) {
        if (r1 == r2) {
            int minCol = Math.min(c1, c2);
            int maxCol = Math.max(c1, c2);
            for (int c = minCol + 1; c < maxCol; c++) {
                if (r1 >= 0 && r1 < ROWS && c >= 0 && c < COLS && board[r1][c] != -1) {
                    return false;
                }
            }
            return true;
        } else if (c1 == c2) {
            int minRow = Math.min(r1, r2);
            int maxRow = Math.max(r1, r2);
            for (int r = minRow + 1; r < maxRow; r++) {
                if (r >= 0 && r < ROWS && c1 >= 0 && c1 < COLS && board[r][c1] != -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean isGameComplete() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -1) return false;
            }
        }
        return true;
    }
    
    private boolean hasValidMoves() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -1) {
                    for (int i2 = 0; i2 < ROWS; i2++) {
                        for (int j2 = 0; j2 < COLS; j2++) {
                            if ((i != i2 || j != j2) && board[i2][j2] != -1 && 
                                board[i][j] == board[i2][j2] && findPath(i, j, i2, j2) != null) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void shuffleBoard() {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -1) {
                    remaining.add(board[i][j]);
                }
            }
        }
        
        Collections.shuffle(remaining);
        
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -1) {
                    board[i][j] = remaining.get(index++);
                }
            }
        }
        
        firstRow = -1;
        firstCol = -1;
        secondRow = -1;
        secondCol = -1;
        path = null;
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                selected[i][j] = false;
            }
        }
        
        invalidate();
    }
    
    public void restartGame() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = 0;
                selected[i][j] = false;
            }
        }
        firstRow = -1;
        firstCol = -1;
        secondRow = -1;
        secondCol = -1;
        path = null;
        initBoard();
        invalidate();
    }
    
    public void hint() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != -1) {
                    for (int i2 = 0; i2 < ROWS; i2++) {
                        for (int j2 = 0; j2 < COLS; j2++) {
                            if ((i != i2 || j != j2) && board[i2][j2] != -1 && 
                                board[i][j] == board[i2][j2] && findPath(i, j, i2, j2) != null) {
                                final int fi = i, fj = j, fi2 = i2, fj2 = j2;
                                selected[fi][fj] = true;
                                selected[fi2][fj2] = true;
                                invalidate();
                                postDelayed(() -> {
                                    selected[fi][fj] = false;
                                    selected[fi2][fj2] = false;
                                    invalidate();
                                }, 1000);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}