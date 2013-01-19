package com.esperia09.android.nfc_ehime.nfc;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esperia09.android.nfc_ehime.R;
import com.esperia09.android.utils.BeamUtil;
import com.esperia09.android.utils.NdefRecordUtils;

public class BeamActivity extends Activity {

	private BeamUtil mBeamUtil;
	private EditText mEditText;
	private TextView mText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		
		mEditText = (EditText) findViewById(R.id.editText);
		mText = (TextView) findViewById(R.id.text);

		// Beamが使えるかどうかを判断します。
		mBeamUtil = new BeamUtil(getApplicationContext());
		if (!mBeamUtil.isBeamEnabled(this)) {
			Toast.makeText(getApplicationContext(), "Android Beamが使用出来ません！！",
					Toast.LENGTH_SHORT).show();
		}

		setBeamCallbacks();
	}

	@SuppressLint("NewApi")
	private void setBeamCallbacks() {
		// 送信するNDEFメッセージを定義します。
		mBeamUtil.setNdefPushMessageCallback(new CreateNdefMessageCallback() {
			@Override
			public NdefMessage createNdefMessage(NfcEvent event) {
				NdefMessage ndefMessage = new NdefMessage(NdefRecordUtils
						.createTextRecord(mEditText.getText().toString(), Locale.getDefault()));
				return ndefMessage;
			}
		}, this);

		// NDEFメッセージの送信に成功した時に呼ばれます。
		mBeamUtil.setOnNdefPushCompleteCallback(
				new OnNdefPushCompleteCallback() {
					@Override
					public void onNdefPushComplete(NfcEvent event) {
						Toast.makeText(getApplicationContext(), "送信完了しました！",
								Toast.LENGTH_LONG).show();
					}
				}, this);
	}

}
