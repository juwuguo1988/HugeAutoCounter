package cn.hckj.core.utils;

/**
 * Created by juwuguo on 19/6/14.
 */

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;


/**
 * 权限申请
 */

public class RxPermissionUtils {
    private static Context mContext;
    private static RxPermissions mRxPermissions;

    public static RxPermissionUtils getInstance(Context context) {
        mContext = context;
        mRxPermissions = new RxPermissions((Activity) context);
        return new RxPermissionUtils();
    }


    public void getPowerStatus(PowerClickCallBack powerClickCallBack, String... permissions) {
        mRxPermissions.request(permissions)
                .subscribe(isPowerOn -> {
                    if (isPowerOn) {
                        powerClickCallBack.onClick();
                    } else {
                        Toast.makeText(mContext, "请开启权限!!!", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public interface PowerClickCallBack {
        void onClick();
    }


}
