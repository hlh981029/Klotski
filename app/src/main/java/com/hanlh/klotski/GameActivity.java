package com.hanlh.klotski;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.icu.text.AlphabeticIndex;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {
    Block mBlock;
    GameBoard mGameBoard;
    int index;
    TextView gameTitle;
    TypedArray gameBoardList;
    TextView timerView;
    TextView stepView;
    Stack<Record> record;
    long baseTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mGameBoard = findViewById(R.id.game_board_layout);
        record = new Stack<>();
        mGameBoard.record = record;
        Intent intent = getIntent();
        int gameBoardId = intent.getIntExtra("game_board_id", -1);
        index = intent.getIntExtra("game_board_index", -1);
        gameTitle = findViewById(R.id.game_title);
        gameTitle.setText(intent.getStringExtra("game_board_name"));
        mGameBoard.positionList = getResources().getIntArray(gameBoardId);
        gameBoardList = getResources().obtainTypedArray(R.array.game_board_list);
        mGameBoard.gameBoardList = gameBoardList;
        mGameBoard.index = index;
        stepView = findViewById(R.id.step_view);
        mGameBoard.stepView = stepView;
        mGameBoard.init();
        this.baseTimer = SystemClock.elapsedRealtime();
        timerView = this.findViewById(R.id.timer_view);
        final Handler startTimehandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (null != timerView) {
                    if (!mGameBoard.isSuccess) {
                        timerView.setText((String) msg.obj);
                    }
                }
            }
        };
        new Timer("Timer").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int time = (int) ((SystemClock.elapsedRealtime() - GameActivity.this.baseTimer) / 1000);
                String mm = new DecimalFormat("00").format(time / 60);
                String ss = new DecimalFormat("00").format(time % 60);
                String timeFormat = mm + ":" + ss;
                Message msg = new Message();
                msg.obj = timeFormat;
                startTimehandler.sendMessage(msg);
            }

        }, 0, 1000L);
    }

    public void navigateBack(View view) {
        finish();
    }

    public void nextGameBoard() {
        this.baseTimer = SystemClock.elapsedRealtime();
        record.clear();
        index += 1;
        if (index * 2 < gameBoardList.length()) {
            gameTitle.setText(gameBoardList.getString(index * 2));
            mGameBoard.positionList = getResources().getIntArray(gameBoardList.getResourceId(index * 2 + 1, -1));
            mGameBoard.init();
        } else {
            Toast.makeText(this, "更多关卡还在开发中，敬请期待", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void undo(View view) {
        if(!record.empty() && !mGameBoard.isSuccess){
            Record last = record.pop();
            stepView.setText(String.valueOf(record.size()));
            mGameBoard.unmarkBlock(last.index, last.x2, last.y2);
            mGameBoard.moveBlock(last.index, last.x1, last.y1);
        }
    }

    public void restart(View view) {
        record.clear();
        this.baseTimer = SystemClock.elapsedRealtime();
        gameTitle.setText(gameBoardList.getString(index * 2));
        mGameBoard.positionList = getResources().getIntArray(gameBoardList.getResourceId(index * 2 + 1, -1));
        mGameBoard.init();
    }
}
