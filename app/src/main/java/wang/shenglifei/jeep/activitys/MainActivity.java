package wang.shenglifei.jeep.activitys;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.baidu.mapapi.SDKInitializer;

import wang.shenglifei.jeep.R;
import wang.shenglifei.jeep.fragments.BaiduMapFragment;
import wang.shenglifei.jeep.fragments.BarcodeScanFragment;
import wang.shenglifei.jeep.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {


    FrameLayout content = null;

    Fragment now = null;
    Fragment first = null;
    Fragment second = null;
    //Fragment third = null;
    ImageButton buttonFirst = null;
    ImageButton buttonSecond = null;
   // ImageButton buttonThird = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        content = (FrameLayout) this.findViewById(R.id.content);
        buttonFirst = (ImageButton) this.findViewById(R.id.first);
        buttonSecond = (ImageButton) this.findViewById(R.id.second);
        //buttonThird = (ImageButton) this.findViewById(R.id.third);

        buttonFirst.setBackgroundColor(Color.GRAY);

        final FragmentManager mFM = this.getFragmentManager();

        first = new BaiduMapFragment();
        second = new BarcodeScanFragment();
        //third = new SettingsFragment();

        mFM.beginTransaction().add(R.id.content, first).add(R.id.content, second).show(first).hide(second).commit();

        now = first;

        buttonFirst.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (now != first) {
                    mFM.beginTransaction().hide(second).show(first).commit();
                    now = first;
                    buttonFirst.setBackgroundColor(Color.GRAY);
                    buttonSecond.setBackgroundColor(Color.TRANSPARENT);
                    //buttonThird.setBackgroundColor(Color.TRANSPARENT);
                }
            }

        });

        buttonSecond.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (now != second) {
                    mFM.beginTransaction().hide(first).show(second).commit();
                    now = second;
                    buttonFirst.setBackgroundColor(Color.TRANSPARENT);
                    buttonSecond.setBackgroundColor(Color.GRAY);
                    //buttonThird.setBackgroundColor(Color.TRANSPARENT);
                }
            }

        });
/*
        buttonThird.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (now != third) {
                    mFM.beginTransaction().hide(first).hide(second).show(third).commit();
                    now = third;
                    buttonFirst.setBackgroundColor(Color.TRANSPARENT);
                    buttonSecond.setBackgroundColor(Color.TRANSPARENT);
                    buttonThird.setBackgroundColor(Color.GRAY);
                }
            }

        });*/
    }
}
