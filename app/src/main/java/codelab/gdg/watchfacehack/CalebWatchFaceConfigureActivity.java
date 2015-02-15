package codelab.gdg.watchfacehack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by hanter on 2015. 2. 15..
 */
public class CalebWatchFaceConfigureActivity extends Activity {
    private static final String TAG = "CalebWatchFaceConfigureActivity";

    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_configure);

        btnDone = (Button)findViewById(R.id.act_configure_btn_done);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send Message
                finish();
            }
        });

    }
}
