package cn.hckj.core.common.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by juwuguo on 2020-03-18.
 */
public class VoiceRecordBean {
    private List<WSBean> ws;

    public List<WSBean> getWs() {
        return ws;
    }

    public void setWs(List<WSBean> ws) {
        this.ws = ws;
    }


    public static String parseVoiceContent(String resultString) {
        VoiceRecordBean voiceRecordBean = new Gson().fromJson(resultString, VoiceRecordBean.class);
        StringBuffer stringBuffer = new StringBuffer();
        List<WSBean> wsBeans = voiceRecordBean.getWs();
        for (WSBean ws : wsBeans) {
            stringBuffer.append(ws.getCw().get(0).getW());
        }
        return stringBuffer.toString();
    }

}
