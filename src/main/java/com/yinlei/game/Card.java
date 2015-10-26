package com.yinlei.game;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by yinlei on 2015/9/20.
 */
public class Card extends FrameLayout {
	
	private TextView label;
	
    public Card(Context context) {
        super(context);
        label = new TextView(getContext());
        label.setTextSize(35);
        label.setBackgroundColor(0x33ffffff);
        label.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.setMargins(10,10,0,0); //这个是设置卡片之间的间隔
        addView(label, lp);
        setNum(0);
    }

    private int num = 0;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        if (num <= 0){
            label.setText("");
        } else {
            label.setText(num + "");
        }
    }

    public boolean equals(Card o) {
        return getNum() == o.getNum();
    }

   
}
