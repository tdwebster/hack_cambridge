package com.gmail.senokt16.inoculus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GameGridView extends View {

    private static final int CHAR_WIDTH = 32;
    private static final int FLOOR_WIDTH = 20;
    private static final int TILE_WIDTH = 64;

    private static final int FLOOR = 0;
    private static final int PLAYER = 1;
    private static final int MONSTER_1 = 2;
    private static final int MONSTER_2 = 3;

    private static final int FLOOR_SPRITE_X = 13 * FLOOR_WIDTH;
    private static final int FLOOR_SPRITE_Y = 1 * FLOOR_WIDTH;
    private static final int MONSTER_1__X = 4 * CHAR_WIDTH;
    private static final int MONSTER_1_Y = 0 * CHAR_WIDTH;
    private static final int MONSTER_2__X = 7 * CHAR_WIDTH;
    private static final int MONSTER_2_Y = 0 * CHAR_WIDTH;
    private static final int PLAYER__X = 0 * CHAR_WIDTH;
    private static final int PLAYER_Y = 0 * CHAR_WIDTH;




    Rect[] tiles, objects;

    Rect camera;

    Rect currentTile = new Rect();

    public GameGridView(Context context) {
        super(context);
        tiles = new Rect[64 * 64];
        objects = new Rect[64 * 64];
        camera = new Rect(dp(32*TILE_WIDTH) - getWidth()/2, dp(32*TILE_WIDTH) - getHeight()/2, dp(32*TILE_WIDTH) + getWidth()/2, dp(32*TILE_WIDTH) + getHeight()/2);
    }

    public GameGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tiles = new Rect[64 * 64];
        objects = new Rect[64 * 64];
        camera = new Rect(dp(32*TILE_WIDTH) - getWidth()/2, dp(32*TILE_WIDTH) - getHeight()/2, dp(32*TILE_WIDTH) + getWidth()/2, dp(32*TILE_WIDTH) + getHeight()/2);
    }

    public GameGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tiles = new Rect[64 * 64];
        objects = new Rect[64 * 64];
        camera = new Rect(dp(32*TILE_WIDTH) - getWidth()/2, dp(32*TILE_WIDTH) - getHeight()/2, dp(32*TILE_WIDTH) + getWidth()/2, dp(32*TILE_WIDTH) + getHeight()/2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
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

    public void setTile(int pos, int type) {
        int x, y;
        switch (type) {
            case FLOOR:
                x = FLOOR_SPRITE_X;
                y = FLOOR_SPRITE_Y;
                break;
            default: return;
        }
        tiles[pos] = new Rect(x, y, x + TILE_WIDTH, y + TILE_WIDTH);
    }

    public void setObject(int pos, int type) {
        switch (type) {

        }
    }

    int dx, dy;

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
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                camera.set(dx + x - camera.width()/2, dy + y - camera.height()/2, dx + x + camera.width()/2, dy + y + camera.height()/2);
                break;
        }

        // tell the View to redraw the Canvas
        invalidate();

        // tell the View that we handled the event
        return true;
    }

    private int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
