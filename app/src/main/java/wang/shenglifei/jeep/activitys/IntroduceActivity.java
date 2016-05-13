package wang.shenglifei.jeep.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import wang.shenglifei.jeep.R;
import wang.shenglifei.jeep.helper.Helper;

public class IntroduceActivity extends AppCompatActivity {

    SpeechSynthesizer mTts;
    TextView tv_introduction;
    ImageButton return_btn;
    ImageButton spot_voice_btn;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        SpeechUtility.createUtility(getBaseContext(), SpeechConstant.APPID + "=57135bbd");
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(getBaseContext(), null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        tv_introduction = (TextView)findViewById(R.id.introduction);
        imageView = (ImageView)findViewById(R.id.imageView);


        Intent intent = getIntent();
        String spot_name = intent.getStringExtra(Helper.result);

        int nameResourceId = getResources().getIdentifier(spot_name, "string", getPackageName());
        TextView tv_spot_title = (TextView)findViewById(R.id.spot_title);
        assert tv_spot_title != null;
        tv_spot_title.setText(getString(nameResourceId));
        switch (nameResourceId) {
            case R.string.baiquanxuan:
                tv_introduction.setText(getString(R.string.baiquanxuan_introduce));
                imageView.setImageResource(R.drawable.baiquanxuan);
                break;
            case R.string.hexitai:
                tv_introduction.setText(getString(R.string.hexitai_introduce));
                break;
            case R.string.fengyuting_yinmachi:
                tv_introduction.setText(R.string.fengyuting_yinmachi_introduce);
                break;
            default:
                break;
        }
        return_btn = (ImageButton)findViewById(R.id.return_btn);
        //返回
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return_btn.setBackgroundColor(Color.GRAY);
                finish();
            }
        });

        spot_voice_btn = (ImageButton)findViewById(R.id.spot_voice_btn);
        spot_voice_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    spot_voice_btn.setBackgroundColor(Color.GRAY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    spot_voice_btn.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });
        spot_voice_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
                //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
                //如果不需要保存合成音频，注释该行代码
                mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
                //3.开始合成
                mTts.startSpeaking(tv_introduction.getText().toString(), mSynListener);
            }
        });


    }

    @Override
    protected void onDestroy() {
        mTts.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mTts.stopSpeaking();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mTts.pauseSpeaking();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mTts.resumeSpeaking();
        super.onResume();
    }
    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

}
