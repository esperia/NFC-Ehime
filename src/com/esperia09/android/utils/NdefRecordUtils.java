package com.esperia09.android.utils;

import java.nio.charset.Charset;
import java.util.Locale;

import android.nfc.NdefRecord;

public class NdefRecordUtils {

    /**
     * Creates a custom MIME type encapsulated in an NDEF record. この関数は API
     * Level 16からの{@link NdefRecord#createMime(String, byte[])}との互換を持っています。
     * 
     * @param mimeType
     */
    public static NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);

        return mimeRecord;
    }

    /**
     * テキストのNDEFレコードを生成します。
     * @param text 
     * @param locale
     * @return
     */
    public static NdefRecord createTextRecord(String text, Locale locale) {
        return createTextRecord(text, locale, true);
    }

    /**
     * テキストのNDEFレコードを生成します。
     * @param text 
     * @param locale
     * @return
     */
    public static NdefRecord createTextRecord(String text, Locale locale, boolean encodeInUtf8) {
    	if (text == null) {
    		throw new NullPointerException("text is MUST require!!");
    	}
    	if (locale == null) {
    		throw new NullPointerException("locale is MUST require!!");
    	}

        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        text = convertToCrlf(text);
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = bytesConcat(new byte[] { (byte) status }, langBytes, textBytes);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }
    
    /**
     * 
     * http://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays
     * @param bytes
     */
    public static byte[] bytesConcat(byte[]... bytes) {
    	if (bytes == null || bytes.length <= 0) {
    		return new byte[0];
    	}
    	
    	byte[] base = new byte[0];
    	for (byte[] b : bytes) {
        	byte[] c = new byte[base.length + b.length];
        	System.arraycopy(base, 0, c, 0, base.length);
        	System.arraycopy(b, 0, c, base.length, b.length);
        	
        	base = c;
		}
    	
    	return base;
    }

    /**
     * Convert to CRLF
     * @param text
     * @return
     */
    private static String convertToCrlf(String text) {
        String repCrlf = text.replaceAll("\r\n", "\n");
        String repLf = repCrlf.replaceAll("\r", "\n");
        String rep = repLf.replaceAll("\n", "\r\n");
        return rep;
    }

    /**
     * Convert to LF
     * @param text
     * @return
     */
    public static String convertToLf(String text) {
        String repCrlf = text.replaceAll("\r\n", "\n");
        String repLf = repCrlf.replaceAll("\r", "\n");
        return repLf;
    }

}
