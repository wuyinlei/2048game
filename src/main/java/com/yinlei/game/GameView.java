package com.yinlei.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinlei on 2015/9/20.
 */
public class GameView extends GridLayout {

    private static final String TGA = "GameView";

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }

    private void initGameView() {
        setColumnCount(4);
        setBackgroundColor(0xffbbada0);
        setOnTouchListener(new View.OnTouchListener() {

            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //这个是事件event，不要对v.getId()监控
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                swipeLeft();
                            } else if (offsetX > 5) {
                                swipeRight();
                            }
                        } else {
                            if (offsetY < -5) {
                                swipeDown();
                            } else if (offsetY > 5) {
                                swipeUp();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 动态的去计算宽和高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //为了保证能够出现一个正方形，这里取长和宽相对较小的那个，然后减去10，是为了和边沿有空隙
        //然后除以4，计算出每个卡片的长
        int cardWidth = (Math.min(w, h) - 10) / 4;
		//给每一个方框设置长和宽
        addCard(cardWidth, cardWidth);
        startGame();
    }

    /**
     * 添加卡片
     */
    private void addCard(int cardWidth, int cardHeight) {
        Card c;     //定义一个Card对象
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                c = new Card(getContext());    //实例化
                c.setNum(0);      //给每个card设值为0
                addView(c, cardWidth, cardHeight);
                cardMaps[x][y] = c; //添加的卡片全部添加到二维数组里面
            }
        }
    }

    /**
     * 添加随机数值
     */
    private void addRandomNum() {
        emptyPoints.clear();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (cardMaps[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }
        Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size()));
        cardMaps[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);  //将随机产生2和4的概率自定义为9:1
    }

	/**
	 *开始游戏
	 */
    private void startGame() {
	
	    //首先把分数清零
        MainActivity.getMainActivity().clearScore();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                cardMaps[x][y].setNum(0);
            }
        }
		//添加两个随机数
        addRandomNum();
        addRandomNum();
    }

	//向左滑动事件的处理
    private void swipeLeft() {

        boolean merge = false;

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                for (int x1 = x + 1; x1 < 4; x1++) {
                    if (cardMaps[x1][y].getNum() > 0) {
                        if (cardMaps[x][y].getNum() <= 0) {
                            cardMaps[x][y].setNum(cardMaps[x1][y].getNum());
                            cardMaps[x1][y].setNum(0);

                            x--;
                            merge = true;
                        } else if (cardMaps[x][y].equals(cardMaps[x1][y])) {
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

	//向右滑动事件处理
    private void swipeRight() {
        boolean merge = false;
        for (int y = 0; y < 4; y++) {
            for (int x = 3; x >= 0; x--) {
                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (cardMaps[x1][y].getNum() > 0) {
                        if (cardMaps[x][y].getNum() <= 0) {
                            cardMaps[x][y].setNum(cardMaps[x1][y].getNum());
                            cardMaps[x1][y].setNum(0);

                            x++;
                            merge = true;
                        } else if (cardMaps[x][y].equals(cardMaps[x1][y])) {
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

	//向上滑动事件处理
    private void swipeUp() {
        boolean merge = false;
        for (int x = 0; x < 4; x++) {
            for (int y = 3; y >= 0; y--) {
                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (cardMaps[x][y1].getNum() > 0) {
                        if (cardMaps[x][y].getNum() <= 0) {
                            cardMaps[x][y].setNum(cardMaps[x][y1].getNum());
                            cardMaps[x][y1].setNum(0);

                            y++;
                            merge = true;
                        } else if (cardMaps[x][y].equals(cardMaps[x][y1])) {
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

	//向下滑动事件处理
    private void swipeDown() {

        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int y1 = y + 1; y1 < 4; y1++) {
                    if (cardMaps[x][y1].getNum() > 0) {
                        if (cardMaps[x][y].getNum() <= 0) {
                            cardMaps[x][y].setNum(cardMaps[x][y1].getNum());
                            cardMaps[x][y1].setNum(0);

                            y--;
                            merge = true;
                        } else if (cardMaps[x][y].equals(cardMaps[x][y1])) {
                            cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
                            cardMaps[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMaps[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }


	//检查游戏是否还可以继续运行
    private void checkComplete() {
        boolean complete = true;
        ALL:
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (cardMaps[x][y].getNum() == 0 ||
                        (x > 0 && cardMaps[x][y].equals(cardMaps[x - 1][y])) ||
                        (x < 3 && cardMaps[x][y].equals(cardMaps[x + 1][y])) ||
                        (y > 0 && cardMaps[x][y].equals(cardMaps[x][y - 1])) ||
                        (y < 3 && cardMaps[x][y].equals(cardMaps[x][y + 1]))) {
                    complete = false;
                    break ALL;
                }
            }
        }
		//如果已经不能再继续滑动（也就是说游戏已经结束）
        if (complete) {
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束").setNegativeButton("重来", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).show();
        }
    }

	//定义一个Card[][]二维数组
    private Card[][] cardMaps = new Card[4][4];
    private List<Point> emptyPoints = new ArrayList<Point>();
}
