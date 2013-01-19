package com.esperia09.android.nfc_ehime.nfc;

import java.util.Locale;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.esperia09.android.nfc_ehime.R;
import com.esperia09.android.utils.NdefRecordUtils;
import com.esperia09.android.utils.NfcUtils;

public class WriteToCardActivity extends Activity {
	
	private static final int DLG_PROGRESS = 0;
	private EditText mEditText;
	private NfcUtils mNfcUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writecard);
		
		mEditText = (EditText) findViewById(R.id.editText);
		
		mNfcUtils = new NfcUtils(getApplicationContext());
		if (!mNfcUtils.isOnBoardNfc()) {
			Toast.makeText(getApplicationContext(), "NFC載ってないよ！！", Toast.LENGTH_SHORT).show();
		} else if (!mNfcUtils.isEnableNfc(this)) {
			Toast.makeText(getApplicationContext(), "NFC無効になってるよ！！", Toast.LENGTH_SHORT).show();
		} else {
			// 必要であればActivity閉じたり。
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mNfcUtils.enable(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mNfcUtils.disable(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (NfcUtils.isNfcIntent(intent)) {
			// タグ情報の受け取り（書き込み時に使用します）
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			// NdefRecordの生成
			NdefRecord textRecord = NdefRecordUtils.createTextRecord(mEditText.getText().toString(), Locale.getDefault());
			
			// プログレスの表示
			showDialog(DLG_PROGRESS);
			new WriteToCardAsyncTask(this, tag)
			.setListener(new WriteToCardAsyncTask.OnResultListener() {
				@Override
				public void onDone() {
					removeDialog(DLG_PROGRESS);
					
					Toast.makeText(getApplicationContext(), "書き込みに成功しました！！", Toast.LENGTH_LONG).show();
				}
				@Override
				public void onFail(String errMsg) {
					removeDialog(DLG_PROGRESS);
					
					String msg = String.format("書き込みに失敗しました…。\n(%1$s)", errMsg);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			}).execute(textRecord);
		}
	}

	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == DLG_PROGRESS) {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("タグを書き込んでいます…。");
			progressDialog.setCancelable(false);
			return progressDialog;
		}

		return super.onCreateDialog(id, args);
	}
}
