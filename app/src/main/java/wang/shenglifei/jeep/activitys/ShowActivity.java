package wang.shenglifei.jeep.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import wang.shenglifei.jeep.R;
import wang.shenglifei.jeep.helper.Helper;

public class ShowActivity extends AppCompatActivity {

    TextView tv_show_result;
    ImageButton return_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        tv_show_result = (TextView)findViewById(R.id.show_result);
        return_btn = (ImageButton)findViewById(R.id.return_btn);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return_btn.setBackgroundColor(Color.GRAY);
                finish();
            }
        });
        Intent intent = getIntent();
        String str_query_result = intent.getStringExtra(Helper.result);
        tv_show_result.setText(str_query_result);
    }
}
