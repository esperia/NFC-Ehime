package com.esperia09.android.nfc_ehime.nfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import com.esperia09.android.nfc_ehime.R;
import com.esperia09.android.utils.NdefRecordUtils;
import com.esperia09.android.utils.NfcUtils;

public class ReadActivity extends Activity {

	private TextView mTextView;
	private NfcUtils mNfc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		mTextView = (TextView) findViewById(R.id.text);

		mNfc = new NfcUtils(getApplicationContext());
		if (!mNfc.isOnBoardNfc()) {
			Toast.makeText(getApplicationContext(), "NFC載ってない端末だよ！！",
					Toast.LENGTH_SHORT).show();
		} else if (!mNfc.isEnableNfc(this)) {
			Toast.makeText(getApplicationContext(), "NFC無効になってるよ！！",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent intent = getIntent();
		if (NfcUtils.isNfcIntent(intent)) {
			processIntent(intent);
		}
		mNfc.enable(this);
	}

	@Override
	public void onPause() {
		mNfc.disable(this);
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	void processIntent(Intent intent) {
		StringBuilder nfcLog = getNfcLog(intent);
		
		mTextView.setText(nfcLog);
	}

	private StringBuilder getNfcLog(Intent intent) {
		StringBuilder sb = new StringBuilder();
		
		// IDmの取得
		byte[] nfcIdm = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		sb.append("IDm: ").append(NfcUtils.bytesToHexString(nfcIdm)).append("\n");

		// タグの取得
		sb.append("TechList: ");
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String[] techList = tag.getTechList();
		for (String tech : techList) {
			sb.append(tech).append("\n");
		}
		
		// NDEFデータの取得
		sb.append("NDEF: ");
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage[] ndefMessages = NfcUtils.getNdefMessages(rawMsgs);
		if (ndefMessages != null) {
			for (NdefMessage ndefMessage : ndefMessages) {
				sb.append(NfcUtils.bytesToHexString(ndefMessage.toByteArray())).append("\n");
			}
		} else {
			sb.append("取得出来ませんでした。。");
		}
		
		return sb;
	}
}
