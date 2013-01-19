
package com.esperia09.android.utils;

import java.nio.charset.Charset;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.os.Build;
import android.util.Log;

/**
 * Android Beam
 * 
 * @author s_kohno
 */
public class BeamUtil {
    private static final String TAG = BeamUtil.class.getSimpleName();
    private NfcAdapter mNfcAdapter;

    public BeamUtil(Context context) {
        setNfcAdapter(NfcAdapter.getDefaultAdapter(context));
    }

    public boolean isBeamEnabled(Activity activity) {
        // Beamが使えるかどうかチェック。使える場合は
        if (isOnBoardNfc(activity)) {
            if (getNfcAdapter() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    // Beam使える！！
                    return true;
                } else {
                    // バージョン古い
                    return false;
                }
            } else {
                // NFC入ってるけど無効になってる
                return false;
            }
        } else {
            // NFCチップ入ってへん
            return false;
        }
    }
    
    /**
     * NDEFデータが必要とされた時に呼ばれます。(API Level 14以上)
     */
    @TargetApi(14)
    public void setNdefPushMessageCallback(CreateNdefMessageCallback createNdefMessageCallback,
            Activity activity,
            Activity... activities) {
        if (isBeamEnabled(activity)) {
            getNfcAdapter().setNdefPushMessageCallback(createNdefMessageCallback, activity, activities);
        } else {
            Log.e(TAG, "Beam Not Available.");
        }
    }

    /**
     * ビームの転送完了時の処理を記述します。(API Level 14以上)
     */
    @TargetApi(14)
    public void setOnNdefPushCompleteCallback(OnNdefPushCompleteCallback callback,
            Activity activity,
            Activity... activities) {
        if (isBeamEnabled(activity)) {
            getNfcAdapter().setOnNdefPushCompleteCallback(callback, activity, activities);
        } else {
            Log.e(TAG, "Beam Not Available.");
        }
    }

    /**
     * NFCが搭載されている端末かどうかをチェックします。
     * 
     * @param context
     * @return
     */
    public boolean isOnBoardNfc(Context context) {
        boolean isNfc = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
        if (isNfc) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * NFCが有効かどうかをチェックします。
     * 
     * @param context
     * @return
     */
    public boolean isEnableNfc(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (getNfcAdapter() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            // NfcAdapterが実装されてない
            return false;
        }
    }

    public NfcAdapter getNfcAdapter() {
        return mNfcAdapter;
    }

    private void setNfcAdapter(NfcAdapter mNfcAdapter) {
        this.mNfcAdapter = mNfcAdapter;
    }
}
