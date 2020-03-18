package cn.hckj.core.ui.counter.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

import cn.hckj.core.R;
import cn.hckj.core.common.bean.UserGoodBean;
import cn.hckj.core.common.bean.VoiceRecordBean;
import cn.hckj.core.ui.counter.adapter.GoodDisplayAdapter;
import cn.hckj.core.utils.RxPermissionUtils;

/**
 * Created by juwuguo on 2020-03-11.
 */
public class InputCounterActivity extends AppCompatActivity {
    private static final String TAG = "InputCounterActivity";
    private EditText et_good_name, et_good_dose, et_good_unit;
    private Button btn_add, btn_create_counter, btn_start_record;
    private ListView lv_good_display;
    private ArrayList<UserGoodBean> userGoodBeans = new ArrayList<>();
    private GoodDisplayAdapter mGoodDisplayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_counter_layout);
        // 将“xxxxxxx”替换成您申请的 APPID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5e6ede86");
        findViewById();
        setListener();
    }

    private void findViewById() {
        et_good_name = findViewById(R.id.et_good_name);
        et_good_dose = findViewById(R.id.et_good_dose);
        et_good_unit = findViewById(R.id.et_good_unit);
        btn_add = findViewById(R.id.btn_add);
        btn_start_record = findViewById(R.id.btn_start_record);
        btn_create_counter = findViewById(R.id.btn_create_counter);
        lv_good_display = findViewById(R.id.lv_good_display);
        mGoodDisplayAdapter = new GoodDisplayAdapter(this, userGoodBeans, true);
        lv_good_display.setAdapter(mGoodDisplayAdapter);
    }


    private void setListener() {
        btn_add.setOnClickListener(v -> {
            String name = et_good_name.getText().toString().trim();
            String dosage = et_good_dose.getText().toString().trim();
            String unit = et_good_unit.getText().toString().trim();
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dosage) && !TextUtils.isEmpty(unit)) {
                UserGoodBean userGoodBean = new UserGoodBean();
                userGoodBean.setGoodName(name);
                userGoodBean.setGoodDosage(dosage);
                userGoodBean.setGoodUnit(unit);
                userGoodBeans.add(userGoodBean);
                mGoodDisplayAdapter.notifyDataSetChanged();
                et_good_name.setText("");
                et_good_dose.setText("");
                et_good_unit.setText("");
            } else {
                Toast.makeText(this, "请正确填写!!!", Toast.LENGTH_LONG).show();
            }

        });

        btn_create_counter.setOnClickListener(v -> {
            Intent intent = new Intent(this, DisplayCounterActivity.class);
            intent.putParcelableArrayListExtra("dataList", userGoodBeans);
            startActivity(intent);
        });

        btn_start_record.setOnClickListener(v -> {
            RxPermissionUtils.getInstance(this).getPowerStatus(() -> {
                        startSpeechDialog();
                    },
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS
            );
        });

    }


    /**
     * 初始化语音识别
     */
    private void startSpeechDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    //返回的result为识别后的汉字,直接赋值到TextView上即可
                    String result = VoiceRecordBean.parseVoiceContent(recognizerResult.getResultString());
                    Log.e(TAG, "=======result========>" + result);
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                Toast.makeText(InputCounterActivity.this, speechError.getErrorDescription(), Toast.LENGTH_LONG).show();
            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }


}
