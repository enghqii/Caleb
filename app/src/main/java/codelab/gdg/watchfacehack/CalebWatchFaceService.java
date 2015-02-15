package codelab.gdg.watchfacehack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

public class CalebWatchFaceService extends CanvasWatchFaceService {
    /*** 서비스가 시스템에 로드될때 대부분의 리소스를 로드해야함 ***/
    /*** 모드는 3가지 Interactive, Ambient, Unvisible  ***/

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }


    /*** 엔진의 콜백만 잘 만들면 제대로 동작함 ***/
    private class Engine extends CanvasWatchFaceService.Engine {
        final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1); //1초->millis(1000)
        static final int MSG_UPDATE_TIME = 0;   //핸들러 구분 메시지

        /* 타임 객체 */
        Time mTime;

        /* 그래픽 객체 */
        Bitmap mBackgroundBitmap;
        Bitmap mBackgroundScaledBitmap;
        Paint mHourPaint;
        Paint mMinutePaint;
        Paint mSecondPaint;

        /* Interactive 모드일 때, 1초에 한번 시간을 업데이트 하기 위해 사용하는 핸들러 */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        /* Interactive 모드이면 */
                        if (isVisible() && !isInAmbientMode()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        /* Timezone의 변경을 감지하는 리시버 */
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };

        /* Timezone 리시버의 등록 여부를 저장하는 변수 */
        boolean mRegisteredTimeZoneReceiver = false;



        /*** Method ***/
        /* Initialize Custom Timer */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (isVisible() && !isInAmbientMode()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /*** 워치페이스 콜백 ***/
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            /* 워치페이스 초기화시 호출되는 콜백 */

            /* System UI를 설정합니다 (다음 섹션에서 설명) */
            /*  Android Wear에서의 System UI는 주로 알림을 위한 Card나 배터리의 남은 용량 표현이나
                음성인식 가능한 상태를 가리키는 “OK, Google” 마크와 같은 것을 말합니다.
                이러한 System UI는 setWatchFaceStyle() 함수를 통해 설정할 수 있으며,
                이번 예제에서는 Card는 선택시 1줄로 보이도록 SHORT 모드로, Card의 배경은 간략하게 보이도록
                INTERRUPTIVE 모드로, SystemUI의 시간은 보이지 않도록 false 로 설정합니다.
                이 Configure에 대한 자세한 사항은 아래의 URL에서 확인할 수 있습니다. */
            setWatchFaceStyle(new WatchFaceStyle.Builder(CalebWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            /* 배경이미지를 로드합니다 */
            Resources resources = CalebWatchFaceService.this.getResources();
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.bg);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            /* 그래픽 객체를 생성합니다 */
            mHourPaint = new Paint();
            mHourPaint.setARGB(255, 200, 200, 200);
            mHourPaint.setStrokeWidth(5.f);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);

            mMinutePaint = new Paint();
            mMinutePaint.setARGB(255, 200, 200, 200);
            mMinutePaint.setStrokeWidth(3.f);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

            mSecondPaint = new Paint();
            mSecondPaint.setARGB(255, 255, 0, 0);
            mSecondPaint.setStrokeWidth(2.f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

            /* 타임 객체를 생성합니다 */
            mTime = new Time();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* 워치페이스가 보이게 되거나 안보이게될 때 호출되는 콜백 */
            if (visible) {
                /* Timezone 리시버가 등록되지 않았다면 등록 */
                if (mRegisteredTimeZoneReceiver == false) {
                    mRegisteredTimeZoneReceiver = true;
                    IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
                    CalebWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
                }
            } else {
                /* Timezone 리시버가 등록되었다면 등록 해제 */
                if (mRegisteredTimeZoneReceiver == true) {
                    mRegisteredTimeZoneReceiver = false;
                    CalebWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
                }
            }
            updateTimer();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* Ambient 모드에서 1분마다 호출되는 콜백 */
            invalidate(); //custom timer 사용 안함, 1분마다 화면 갱신
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            /* 워치페이스 종료시 호출되는 콜백 */
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* 워치페이스의 모드가 변경될 때 호출되는 콜백 */
        }

        /*** 그림그리는 함수 워치페이스 그리면됨 ***/
        /*
            1. onDraw() 함수가 최초 호출된 상태라면, 배경화면으로 쓰일 이미지를 해당 디바이스의 크기에 맞춰 수정합니다.
            2. 디바이스의 상태가 Ambient 모드인지 Interactive 모드인지 확인합니다.
            3. 필요한 그래픽 연산을 수행합니다. (초침의 각도 등)
            4. Canvas에 배경 Bitmap을 그립니다.
            5.Canvas의 함수를 사용하여 Watch Face를 그립니다.
         */
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            /* 워치페이스를 그리는 곳으로 invalidate가 호출되면 자동으로 콜백이 불림(가장 중요) */
            /* 1. 시간을 갱신합니다. */
            mTime.setToNow();

            int width = bounds.width();
            int height = bounds.height();

            /* 2. 배경 이미지의 크기를 bound에 맞게 수정하고 배경으로 지정합니다. */
            if (mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                        width, height, true /* filter */);
            }
            canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);

            /* 3. 중심 좌표를 구합니다. */
            float centerX = width / 2f;
            float centerY = height / 2f;

            /* 4. 각 침들의 각도와 길이를 계산합니다. */
            float secRot = mTime.second / 30f * (float) Math.PI;
            int minutes = mTime.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((mTime.hour + (minutes / 60f)) / 6f ) * (float) Math.PI;

            float secLength = centerX - 20;
            float minLength = centerX - 40;
            float hrLength = centerX - 80;

            /* 5. Interactive 모드일 때에는, 초침을 그립니다. */
            if (!isInAmbientMode()) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY +
                        secY, mSecondPaint);
            }

            // Draw the minute and hour hands.
            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY,
                    mMinutePaint);
            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY,
                    mHourPaint);
        }
    }
}