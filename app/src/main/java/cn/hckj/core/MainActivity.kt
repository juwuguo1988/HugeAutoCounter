package cn.hckj.core

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.hckj.core.utils.RxPermissionUtils
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.google.gson.Gson
import com.iflytek.cloud.RecognizerResult
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.iflytek.cloud.ui.RecognizerDialog


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 将“xxxxxxx”替换成您申请的 APPID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5e6ede86");
        RxPermissionUtils.getInstance(this)
            .getPowerStatus(
                object : RxPermissionUtils.PowerClickCallBack {
                    override fun onClick() {
                        initSpeech(this@MainActivity)
                    }
                },
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS
            )
    }

    /**
     * 初始化语音识别
     */
    fun initSpeech(context: Context) {
        //1.创建RecognizerDialog对象
        val mDialog = RecognizerDialog(context, null)
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin")
        //3.设置回调接口
        mDialog.setListener(object : RecognizerDialogListener {
            override fun onResult(recognizerResult: RecognizerResult?, isLast: Boolean) {
                if (!isLast) {
                    //解析语音
                    //返回的result为识别后的汉字,直接赋值到TextView上即可
                    val result = parseVoice(recognizerResult!!.getResultString())
                    Log.e("MainActivity", "========result=111=======" + result);
                }
            }

            override fun onError(p0: SpeechError?) {
            }
        })
        //4.显示dialog，接收语音输入
        mDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * 解析语音json
     */
    fun parseVoice(resultString: String): String {
        val gson = Gson()
        val voiceBean = gson.fromJson(resultString, Voice::class.java)

        val sb = StringBuffer()
        val ws = voiceBean.ws
        for (wsBean in ws!!) {
            val word = wsBean.cw!![0].w
            sb.append(word)
        }
        return sb.toString()
    }

    /**
     * 语音对象封装
     */
    inner class Voice {

        var ws: ArrayList<WSBean>? = null

        inner class WSBean {
            var cw: ArrayList<CWBean>? = null
        }

        inner class CWBean {
            var w: String? = null
        }
    }
}
