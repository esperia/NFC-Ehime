package com.esperia09.android.utils;

import java.util.Formatter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

public class NfcUtils {
	private static final String TAG = NfcUtils.class.getSimpleName();

	private Context mContext;
	private NfcAdapter mNfcAdapter;

	public NfcUtils(Context context) {
		setNfcAdapter(NfcAdapter.getDefaultAdapter(context));
		mContext = context;
	}

	public void enable(Activity activity) {
		if (mNfcAdapter != null) {
			PendingIntent pIntent = createPendingIntent(activity);
			mNfcAdapter.enableForegroundDispatch(activity, pIntent, null, null);
		} else {
			Log.w(TAG, "NFCが使用できません。");
		}
	}

	public void enable(Activity activity, PendingIntent pIntent,
			IntentFilter[] filters, String[][] techLists) {
		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(activity, pIntent, filters,
					techLists);
		} else {
			Log.w(TAG, "NFCが使用できません。");
		}
	}

	public void disable(Activity activity) {
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(activity);
		} else {
			Log.w(TAG, "NFCが使用できません。");
		}
	}

	/**
	 * NFCが搭載されている端末かどうかをチェックします。
	 * 
	 * @param context
	 * @return
	 */
	public boolean isOnBoardNfc() {
		boolean isNfc = mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_NFC);
		return isNfc;
	}

	/**
	 * NFCが有効かどうかをチェックします。
	 * 
	 * @param context
	 * @return
	 */
	public boolean isEnableNfc(Context context) {
		if (getNfcAdapter() != null && getNfcAdapter().isEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@link Intent#getAction()}がNFCのものであるかどうかをチェックします。
	 * Intentがnullであればfalseを返します。
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNfcIntent(Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
					|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
					|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
				return true;
			}
		}
		return false;
	}

	public NfcAdapter getNfcAdapter() {
		return mNfcAdapter;
	}

	public void setNfcAdapter(NfcAdapter mNfcAdapter) {
		this.mNfcAdapter = mNfcAdapter;
	}

	public static PendingIntent createPendingIntent(Activity activity) {
		Intent intent = new Intent(activity, activity.getClass()).addFlags(
				Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				activity.getApplicationContext(), 0, intent, 0);

		return pendingIntent;
	}

	/*
	 * Returns NDEF Messages from the tag
	 */
	public static NdefMessage[] getNdefMessages(Parcelable[] rawMsgs) {
		// Parse the intent
		NdefMessage[] msgs = null;
		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];
			}
		} else {
			// Unknown tag type
			msgs = new NdefMessage[0];
		}
		return msgs;
	}

	public static byte[] reverse(byte[] bytes) {
		byte[] newBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			newBytes[i] = bytes[bytes.length - i - 1];
		}

		return newBytes;
	}

	public static String bytesToHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		byte[] newBytes = reverse(bytes);
		StringBuilder sb = new StringBuilder();

		Formatter formatter = new Formatter(sb);
		for (byte b : newBytes) {
			formatter.format("%02x", b);
		}
		return sb.toString();
	}
}
