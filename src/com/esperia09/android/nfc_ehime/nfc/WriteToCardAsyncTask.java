package com.esperia09.android.nfc_ehime.nfc;

import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;

public class WriteToCardAsyncTask extends AsyncTask<NdefRecord, Void, String> {

	private Context mContext;
	private Tag mTag;
	private OnResultListener mListener;

	public WriteToCardAsyncTask(Context context, Tag tag) {
		mContext = context;
		mTag = tag;
	}

	@Override
	protected String doInBackground(NdefRecord... params) {
		try {
			writeToCard(params);
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}
	
	public WriteToCardAsyncTask setListener(OnResultListener l) {
		mListener = l;
		return this;
	}

	@Override
	protected void onPostExecute(String errMsg) {
		super.onPostExecute(errMsg);
		
		if (errMsg == null) {
			// エラーメッセージがなかったら成功
			if (mListener != null) {
				mListener.onDone();
			}
		} else {
			// エラーが出たら書き込み失敗
			if (mListener != null) {
				mListener.onFail(errMsg);
			}
		}
	}

	/**
	 * 
	 * @param params
	 */
	private void writeToCard(NdefRecord[] params) throws RuntimeException,
			TagLostException, FormatException, IOException {
		if (mTag == null) {
			throw new RuntimeException("タグが正しく読み取れませんでした。");
		}
		if (Arrays.asList(mTag.getTechList()).contains(
				NdefFormatable.class.getName())) {
			// TechListにNdefFormatable が含まれていたら
			writeNdefFormatable(params);
		} else if (Arrays.asList(mTag.getTechList()).contains(
				Ndef.class.getName())) {
			// TechListにNdef が含まれていたら
			writeNdef(params);
		} else {
			throw new RuntimeException("対応していないタグです。");
		}
	}

	private void writeNdef(NdefRecord[] params) throws TagLostException, FormatException, IOException {
		Ndef ndef = Ndef.get(mTag);
		try {
			// 未接続だったら接続.
			if (!ndef.isConnected()) {
				ndef.connect();
			}
			// 書込可能かチェック
			if (ndef.isWritable()) {
				// 書込
				ndef.writeNdefMessage(new NdefMessage(params));
			} else {
				throw new RuntimeException("書き込みができないタグでした！！");
			}
		} finally {
			ndef.close();
		}
	}

	private void writeNdefFormatable(NdefRecord[] params) throws TagLostException, FormatException, IOException {
		NdefFormatable ndef = NdefFormatable.get(mTag);
		try {
			// 未接続だったら接続.
			if (!ndef.isConnected()) {
				ndef.connect();
			}
			// 書込
			ndef.format(new NdefMessage(params));
		} finally {
			ndef.close();
		}
	}

	public interface OnResultListener {
		/**
		 * 書き込みに成功した時に呼ばれます。
		 */
		public void onDone();
		
		/**
		 * 書き込みに失敗した時に呼ばれます。
		 * @param errMsg
		 */
		public void onFail(String errMsg);
	}

}
