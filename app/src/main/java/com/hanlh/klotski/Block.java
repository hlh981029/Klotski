package com.hanlh.klotski;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.Toast;

import static java.lang.Math.abs;


public class Block extends android.support.v7.widget.AppCompatImageView {
    public GameBoard mGameBoard;
    private int currentX;
    private int currentY;
    private boolean[] movable;
    private int start;
    private int end;
    private int begin;
    private int direction;
    private boolean flag;
    public int spanX, spanY;
    public int x, y;
    public int index;
    public int padding;
    private int temp1, temp2;


    public Block(Context context) {
        super(context);
        init(null, 0);
    }

    public Block(GameBoard gameBoard, int _index, int _spanX, int _spanY, Drawable drawable) {
        super(gameBoard.getContext());
        mGameBoard = gameBoard;
        padding = gameBoard.padding;
        index = _index;
        spanX = _spanX;
        spanY = _spanY;
        setBackgroundDrawable(null);
        setBackground(null);
        setImageDrawable(drawable);
        setPadding(0, 0, 0, 0);
        setScaleType(ScaleType.FIT_XY);
        init(null, 0);
    }

    public Block(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Block(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        movable = new boolean[]{false, false, false, false};
        x = 0;
        y = 0;
    }

    public void checkMovable() {
        int[][] board = mGameBoard.board;
        for (int i = 0; i < 4; i++) {
            movable[i] = true;
        }
        if (x - 1 >= 0) {
            for (int i = 0; i < spanY; i++) {
                if (board[y + i][x - 1] != -1) {
                    movable[0] = false;
                    break;
                }
            }
        } else {
            movable[0] = false;
        }
        if (x + spanX <= 3) {
            for (int i = 0; i < spanY; i++) {
                if (board[y + i][x + spanX] != -1) {
                    movable[2] = false;
                    break;
                }
            }
        } else {
            movable[2] = false;
        }
        if (y - 1 >= 0) {
            for (int i = 0; i < spanX; i++) {
                if (board[y - 1][x + i] != -1) {
                    movable[1] = false;
                    break;
                }
            }
        } else {
            movable[1] = false;
        }
        if (y + spanY <= 4) {
            for (int i = 0; i < spanX; i++) {
                if (board[y + spanY][x + i] != -1) {
                    movable[3] = false;
                    break;
                }
            }
        } else {
            movable[3] = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mGameBoard.isMoving != index && mGameBoard.isMoving != -1 || mGameBoard.isSuccess) {
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        int X = (int) event.getRawX();
        int Y = (int) event.getRawY();
        int deltaX = X - currentX;
        int deltaY = Y - currentY;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                checkMovable();
                mGameBoard.isMoving = index;
                currentX = X;
                currentY = Y;
                direction = 0;
                flag = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (flag) {
                    if ((movable[1] || movable[3]) && (movable[0] || movable[2])) {
                        direction = abs(deltaX) > abs(deltaY) ? 1 : 2;
                    } else if (movable[0] || movable[2]) {
                        direction = 1;
                    } else if (movable[1] || movable[3]) {
                        direction = 2;
                    } else {
                        direction = 0;
                    }
                    if (direction == 1) {
                        begin = currentX;
                        if (movable[0] && movable[2]) {
                            start = -mGameBoard.blockWidth;
                            end = mGameBoard.blockWidth;
                        } else if (movable[0]) {
                            start = -mGameBoard.blockWidth;
                            end = 0;
                        } else {
                            start = 0;
                            end = mGameBoard.blockWidth;
                        }

                    } else if (direction == 2) {
                        begin = currentY;
                        if (movable[1] && movable[3]) {
                            start = -mGameBoard.blockWidth;
                            end = mGameBoard.blockWidth;
                        } else if (movable[1]) {
                            start = -mGameBoard.blockWidth;
                            end = 0;
                        } else {
                            start = 0;
                            end = mGameBoard.blockWidth;
                        }
                    }
                    flag = false;
                }

                if (direction == 1) {
                    int offset = X - begin;
                    if (offset <= end && offset >= start) {
                        layout(getLeft() + deltaX, getTop(), getRight() + deltaX, getBottom());
                        currentX = X;
                    } else if (offset > end) {
                        temp1 = x * mGameBoard.blockWidth + end + padding;
                        temp2 = temp1 + spanX * mGameBoard.blockWidth  + mGameBoard.lineWidth;
                        layout(temp1, getTop(), temp2, getBottom());
                        currentX = begin + end;
                        if (end == 0) {
                            flag = true;
                        }
                    } else {
                        temp1 = x * mGameBoard.blockWidth + start + padding;
                        temp2 = temp1 + spanX * mGameBoard.blockWidth  + mGameBoard.lineWidth;
                        layout(temp1, getTop(), temp2, getBottom());
                        currentX = begin + start;
                        if (start == 0) {
                            flag = true;
                        }
                    }
                } else if (direction == 2) {
                    int offset = Y - begin;
                    if (offset <= end && offset >= start) {
                        layout(getLeft(), getTop() + deltaY, getRight(), getBottom() + deltaY);
                        currentY = Y;
                    } else if (offset > end) {
                        layout(getLeft(), y * mGameBoard.blockWidth + end + padding, getRight(), (y + spanY) * mGameBoard.blockWidth + end + padding + mGameBoard.lineWidth);
                        currentY = begin + end;
                        if (end == 0) {
                            flag = true;
                        }
                    } else {
                        layout(getLeft(), y * mGameBoard.blockWidth + start + padding, getRight(), (y + spanY) * mGameBoard.blockWidth + start + padding + mGameBoard.lineWidth);
                        currentY = begin + start;
                        if (start == 0) {
                            flag = true;
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                int newX = x;
                int newY = y;
                if (direction == 1) {
                    int offset = currentX - begin;
                    if (offset >= end / 2 && offset <= end) {
                        layout(x * mGameBoard.blockWidth + end + padding, getTop(), (x + spanX) * mGameBoard.blockWidth + end + padding + mGameBoard.lineWidth, getBottom());
                        newX += end / mGameBoard.blockWidth;
                    } else if (offset <= start / 2 && offset >= start) {
                        layout(x * mGameBoard.blockWidth + start + padding, getTop(), (x + spanX) * mGameBoard.blockWidth + start + padding + mGameBoard.lineWidth, getBottom());
                        newX += start / mGameBoard.blockWidth;
                    } else {
                        layout(x * mGameBoard.blockWidth + padding, getTop(), (x + spanX) * mGameBoard.blockWidth + padding + mGameBoard.lineWidth, getBottom());
                    }
                } else if (direction == 2) {
                    int offset = currentY - begin;
                    if (offset >= end / 2 && offset <= end) {
                        layout(getLeft(), y * mGameBoard.blockWidth + end + padding, getRight(), (y + spanY) * mGameBoard.blockWidth + end + padding + mGameBoard.lineWidth);
                        newY += end / mGameBoard.blockWidth;
                    } else if (offset <= start / 2 && offset >= start) {
                        layout(getLeft(), y * mGameBoard.blockWidth + start + padding, getRight(), (y + spanY) * mGameBoard.blockWidth + start + padding + mGameBoard.lineWidth);
                        newY += start / mGameBoard.blockWidth;
                    } else {
                        layout(getLeft(), y * mGameBoard.blockWidth + padding, getRight(), (y + spanY) * mGameBoard.blockWidth + padding + mGameBoard.lineWidth);
                    }
                }
                mGameBoard.step(index, x, y, newX, newY);
                x = newX;
                y = newY;
                mGameBoard.isMoving = -1;
                if (index == 0 && x == 1 && y == 3) {
                    mGameBoard.isSuccess = true;
                    setImageResource(R.drawable.image_10);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("游戏成功");
                    builder.setMessage("点击确定前往下一关");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ((GameActivity)getContext()).nextGameBoard();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.setNeutralButton("返回关卡选择", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ((Activity)getContext()).finish();
                        }
                    });
                    builder.show();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
