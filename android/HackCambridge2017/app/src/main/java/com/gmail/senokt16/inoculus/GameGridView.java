package com.gmail.senokt16.inoculus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GameGridView extends View {

    private static final int CHAR_WIDTH = 32;
    private static final int FLOOR_WIDTH = 20;
    private static final int TILE_WIDTH = 64;

    public static final int NO_FLOOR = 0;
    public static final int FLOOR = 10;
    public static final int NO_MONSTER = 100;
    public static final int PLAYER = 200;
    public static final int MONSTER_1 = 300;
    public static final int MONSTER_2 = 400;

    private static final int NO_FLOOR_SPRITE_X = 13 * FLOOR_WIDTH;
    private static final int NO_FLOOR_SPRITE_Y = 1 * FLOOR_WIDTH;
    private static final int FLOOR_SPRITE_X = 9 * FLOOR_WIDTH;
    private static final int FLOOR_SPRITE_Y = 12 * FLOOR_WIDTH;

    private static final int NO_MONSTER_X = 10 * CHAR_WIDTH;
    private static final int NO_MONSTER_Y = 0 * CHAR_WIDTH;
    private static final int MONSTER_1_X = 4 * CHAR_WIDTH;
    private static final int MONSTER_1_Y = 0 * CHAR_WIDTH;
    private static final int MONSTER_2_X = 7 * CHAR_WIDTH;
    private static final int MONSTER_2_Y = 0 * CHAR_WIDTH;
    private static final int PLAYER_X = 0 * CHAR_WIDTH;
    private static final int PLAYER_Y = 0 * CHAR_WIDTH;

    int editMode = 0;

    Rect[] tiles, objects;

    Rect camera;

    Rect currentTile = new Rect();

    Bitmap bitmap;

    int dx, dy;
    boolean isClick = false;

    public GameGridView(Context context) {
        super(context);
        init();
    }

    public GameGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        tiles = new Rect[64 * 64];
        objects = new Rect[64 * 64];
        camera = new Rect(dp(32*TILE_WIDTH) - getWidth()/2, dp(32*TILE_WIDTH) - getHeight()/2, dp(32*TILE_WIDTH) + getWidth()/2, dp(32*TILE_WIDTH) + getHeight()/2);

        for (int i=0; i < tiles.length; i++) {
            setObject(i, NO_MONSTER);
            setTile(i, NO_FLOOR);
        }
        setEditMode(FLOOR);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null)
            bitmap.recycle();
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        for (int i=0; i < tiles.length; i++) {
            currentTile.set(dp((i%64)*TILE_WIDTH), dp(i - (i % 64)), dp((i%64)*TILE_WIDTH + TILE_WIDTH), dp(i - (i % 64) + TILE_WIDTH));
            if (Rect.intersects(currentTile, camera)) {
                currentTile.set(camera.left - currentTile.left, camera.top - currentTile.top, camera.right - currentTile.right, camera.bottom - currentTile.bottom);
                //Draw floor tile.
                canvas.drawBitmap(bitmap, tiles[i], currentTile, null);
                //Draw object.
                if (objects[i] != null)
                    canvas.drawBitmap(bitmap, objects[i], currentTile, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // you may need the x/y location
        int x = (int)event.getX();
        int y = (int)event.getY();

        // put your code in here to handle the event
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = camera.centerX() - x;
                dy = camera.centerY() - y;
                isClick = true;
                Log.d("CanvasOnTouch", "Touch down x:" + x + " y:" + y);
                break;
            case MotionEvent.ACTION_UP:
                if (isClick)
                    doOnClick(x, y);
                Log.d("CanvasOnTouch", "Touch Up x:" + x + " y:" + y);
                break;
            case MotionEvent.ACTION_MOVE:
                camera.set(dx + x - camera.width()/2, dy + y - camera.height()/2, dx + x + camera.width()/2, dy + y + camera.height()/2);
                isClick = false;
                Log.d("CanvasOnTouch", "Touch move x:" + x + " y:" + y);
                break;
        }

        // tell the View to redraw the Canvas
        invalidate();

        // tell the View that we handled the event
        return true;
    }

    private void doOnClick(int x, int y) {
        int rX = x / 64;
        int rY = (y - (y % 64)) / 64;
        int rPos = rY * 64 + rX;
        if (editMode < NO_MONSTER) {
            setTile(rPos, editMode);
        } else {
            setObject(rPos, editMode);
        }
    }

    private int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    public void setTile(int pos, int type) {
        int x, y;
        switch (type) {
            case NO_FLOOR:
                x = NO_FLOOR_SPRITE_X;
                y = NO_FLOOR_SPRITE_Y;
                break;
            case FLOOR:
                x = FLOOR_SPRITE_X;
                y = FLOOR_SPRITE_Y;
                break;
            default: return;
        }
        tiles[pos] = new Rect(x, y, x + FLOOR_WIDTH, y + FLOOR_WIDTH);
    }

    public void setObject(int pos, int type) {
        int x, y;
        switch (type) {
            case NO_MONSTER:
                x = NO_MONSTER_X;
                y = NO_MONSTER_Y;
                break;
            case MONSTER_1:
                x = MONSTER_1_X;
                y = MONSTER_1_Y;
                break;
            case MONSTER_2:
                x = MONSTER_2_X;
                y = MONSTER_2_Y;
                break;
            case PLAYER:
                x = PLAYER_X;
                y = PLAYER_Y;
                break;
            default: return;
        }
        objects[pos] = new Rect(x, y, x + CHAR_WIDTH, y + CHAR_WIDTH);
    }

    public void setEditMode(int type) {
        editMode = type;
    }
}
