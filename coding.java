
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ningluo
 */
public class coding {

    static ByteArrayOutputStream  outputStream = new ByteArrayOutputStream();

    public static byte[] encoding(short sh) throws IOException {

        outputStream = new ByteArrayOutputStream();
        ByteBuffer short_buf = ByteBuffer.allocate(2);
        short_buf.putShort(sh);
        outputStream.write(short_buf.array());
        byte[] enc = outputStream.toByteArray();
        return enc;
    }

    public static byte[] encoding(String s) throws IOException {
        outputStream = new ByteArrayOutputStream();
        outputStream.write(s.getBytes());
        byte[] enc = outputStream.toByteArray();
        return enc;
    }

    public static  byte[] encoding(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static byte[] concatenate(byte[] a, byte[] b) throws IOException {
        outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);
        byte[] con = outputStream.toByteArray();
        return con;
    }
    
    public static boolean endsWith(ByteBuffer buf, String suffix) {
    	if (buf.position() < suffix.length()) return false;
    	
    	for (int i=0; i<suffix.length(); i++) {
    		Character sc = suffix.charAt(suffix.length() - i - 1);
    		Character bc = (char) buf.get(buf.position() - i - 1);
    		if (sc != bc) return false; 
    	}
    	
    	return true;
//    	String bufStr;
//		try {
//			bufStr = new String(buf.array(), "ASCII");
//		} catch (UnsupportedEncodingException e) {
//			return false;
//		}
//    	return (bufStr.endsWith(suffix));
    }

}
