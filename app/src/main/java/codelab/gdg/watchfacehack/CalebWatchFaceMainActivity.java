package codelab.gdg.watchfacehack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by hanter on 2015. 2. 15..
 */
public class CalebWatchFaceMainActivity extends Activity {
    private static final String TAG = "CalebWatchFaceActivity";

    private ImageView btnStart;
    private ImageView btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        btnStart = (ImageView)findViewById(R.id.act_main_btn_start);
        btnSetting = (ImageView)findViewById(R.id.act_main_btn_setting);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send Message
                finish();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CalebWatchFaceMainActivity.this, "go setting", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CalebWatchFaceMainActivity.this, CalebWatchFaceConfigureActivity.class));
            }
        });
    }
}
