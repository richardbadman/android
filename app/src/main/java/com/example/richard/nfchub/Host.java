package com.example.richard.nfchub;

import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by richard on 28/05/2017.
 * Concept taken from; https://developer.android.com/samples/CardEmulation/project.html
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Host extends HostApduService {

    private static final String SELECT_APDU_HEADER = "00A40400";
    private String AID = "";
    //private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);
    private final byte[] SELECT_OK = HexStringToByteArray("9000");
    private final byte[] UNKNOWN_CMD = HexStringToByteArray("0000");
    private byte[] SELECT_APDU = buildAPDU(AID);

    public Host(String aid) {
        this.AID = aid;
        Log.i("Setting AID: " , aid);
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {

//        if (Arrays.equals(SELECT_APDU, commandApdu)) {
//            return ConcatArrays(AID.getBytes(), SELECT_OK);
//        }

        Log.i("Incoming read: ", "");

        return ( Arrays.equals(SELECT_APDU, commandApdu)) ? ConcatArrays(AID.getBytes(), SELECT_OK) : UNKNOWN_CMD;

//        return new byte[0];
    }

    @Override
    public void onDeactivated(int reason) {
        Log.i("Deactivated for : ", String.valueOf(reason));
    }

    public byte[] buildAPDU(String aid) {
        //Create HEXStringtoByteArrayFunction
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    /**
     * Taken from the Android Card Emulation example
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Taken from the Android Card Emulation example
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
