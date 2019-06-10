package com.hanlh.klotski;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameSelectActivity extends Activity {
    TypedArray mGameBoardList;
    LinearLayout mGameTable;
    View.OnClickListener mOnClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);
        mGameBoardList = getResources().obtainTypedArray(R.array.game_board_list);
        mGameTable = findViewById(R.id.game_table);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GameActivity.class);
                int id = mGameBoardList.getResourceId((int) view.getTag() * 2 + 1, R.array.game_board_0);
                intent.putExtra("game_board_index", (int) view.getTag());
                intent.putExtra("game_board_id", id);
                intent.putExtra("game_board_name", ((Button)view).getText());
                startActivity(intent);
            }
        };
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = util.px2dp(this, dm.widthPixels);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(util.dp2px(this, 250), util.dp2px(this, 50));
        int margin_y = util.dp2px(this, 10);
        int margin_x = util.dp2px(this, (width - 250) / 2);
        for (int i = 0; i < mGameBoardList.length(); i += 2) {
            Button button = new Button(this);
            button.setTag(i / 2);
            button.setText(mGameBoardList.getString(i));
            button.setBackgroundResource(R.drawable.button_default);
            button.setOnClickListener(mOnClickListener);
            button.setTextColor(getResources().getColor(R.color.colorTextLight));
            button.setTextSize(20);
            layoutParams.setMargins(margin_x, margin_y, margin_x, margin_y);
            mGameTable.addView(button, layoutParams);
        }
    }

    public void navigateBack(View view) {
        finish();
    }
}
