package wang.shenglifei.jeep.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import wang.shenglifei.jeep.R;
import wang.shenglifei.jeep.helper.Helper;

public class SpotsActivity extends AppCompatActivity implements ImageView.OnClickListener {

    ImageButton return_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots);
        return_btn = (ImageButton)findViewById(R.id.return_btn);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return_btn.setBackgroundColor(Color.GRAY);
                finish();
            }
        });
    }
    @Override
    public void onClick(View v) {
        String spot_name = v.getTag().toString();
        Intent intent = new Intent();
        intent.putExtra(Helper.result, spot_name);
        intent.setClass(this, IntroduceActivity.class);
        startActivity(intent);
    }
}
