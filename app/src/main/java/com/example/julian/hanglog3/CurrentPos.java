package com.example.julian.hanglog3;

import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrentPos {
    public int vwidth = 0;
    public int vheight = 0;

    public double[] sprogpressure = new double[4];
    public double[] sprogpressureDelay = new double[4];

    public double northorient = 0.0, pitch = 0.0, roll = 0.0;
    public int orientcalibration = -1;
    public double northorientA = 0.0, pitchA = 0.0, rollA = 0.0;

    public double lat0 = -9999.0, lng0 = 0.0;
    public double nyfac = 0.0, exfac = 0.0;
    public double xpos = 0.0, ypos = 0.0, alt = -999.0;
    public double xposA = 0.0, yposA = 0.0, altA = -999.0;
    public ArrayMap<Character, Integer> datacount = new ArrayMap<Character, Integer>();
    StringBuilder sb = new StringBuilder();

    public Bitmap cameraview = null;

    //Pattern pZ = Pattern.compile("^Zt[0-9A-F]{8}x[0-9A-F]{4}y[0-9A-F]{4}z[0-9A-F]{4}a[0-9A-F]{4}b[0-9A-F]{4}c[0-9A-F]{4}w([0-9A-F]{4})x([0-9A-F]{4})y([0-9A-F]{4})z([0-9A-F]{4})");
    //Pattern paZ = Pattern.compile("^aZt[0-9A-F]{8}x([0-9A-F]{4})y([0-9A-F]{4})z([0-9A-F]{4})");

    public double s16(String d) {
        int j = Integer.valueOf(d, 16);
        return (j < 32768 ? j : j - 65536);
    }
    public int hexchar(byte d) {
        return Character.digit(d, 16);
        //(d <= '9' ? d - '0' : d - 'A' + 10);
    }
    public int s4(byte[] data, int i) {
        int j = (hexchar(data[i])<<12) + (hexchar(data[i+1])<<8) + (hexchar(data[i+2])<<4) + (hexchar(data[i+3]));
        return (j < 32768 ? j : j - 65536);
    }
    public int s6(byte[] data, int i) {
        int j = (hexchar(data[i])<<20) + (hexchar(data[i+1])<<16) + (hexchar(data[i+2])<<12) + (hexchar(data[i+3])<<8) + (hexchar(data[i+4])<<4) + (hexchar(data[i+5]));
        return (j < 0x800000 ? j : j - 0x1000000);
    }


    public String showdatacount() {
        sb.setLength(0);
        for (ArrayMap.Entry<Character, Integer> e : datacount.entrySet()) {
            e.getKey();
            sb.append(e.getKey());
            sb.append(':');
            sb.append(e.getValue());
            sb.append(' ');
        }
        return sb.toString();
    }


    public void processPos(byte[] data, int leng) {
        if ((leng >= 2) && (data[0] != 'a')) {
            char k = (char)(data[0]);
            datacount.put(k, datacount.getOrDefault(k, 0)+1);
        }

        // "Zt00000000x0000y0000z0000a0000b0000c0000w0000x0000y0000z0000s00\n"
        if (data[0] == 'Z') {
            double q0 = s4(data, 41);
            double q1 = s4(data, 46);
            double q2 = s4(data, 51);
            double q3 = s4(data, 56);
            double riqsq = q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3;
            double iqsq = 1.0 / riqsq;
            double r02 = q0 * q2 * 2 * iqsq, r13 = q1 * q3 * 2 * iqsq;
            double sinpitch = r13 - r02;
            double r01 = q0 * q1 * 2 * iqsq, r23 = q2 * q3 * 2 * iqsq;
            double sinroll = r23 + r01;
            double r00 = q0 * q0 * 2 * iqsq, r11 = q1 * q1 * 2 * iqsq, r03 = q0 * q3 * 2 * iqsq, r12 = q1 * q2 * 2 * iqsq;
            double a00 = r00 - 1 + r11, a01 = r12 + r03;
            double rads = Math.atan2(a00, -a01);

            northorient = 180 - Math.toDegrees(rads);
            pitch = Math.toDegrees(Math.asin(sinpitch));
            roll = Math.toDegrees(Math.asin(sinroll));
            orientcalibration = (hexchar(data[61])<<4) + (hexchar(data[62]));
            Log.i("hhanglogM", "calib "+data[61]+"s"+orientcalibration);
            //Log.i("hhanglogM", "pitch "+pitch+" roll "+roll);
            //Log.i("hhanglogM", (new String(data, 0, leng)) + "  " + q3);
            return;
        }

        // "aZt00000000x0000y0000z0000\n"
        if ((data[0] == 'a') && (data[1] == 'Z')) {
            double q1 = s4(data, 12) / 32768.0;
            double q2 = s4(data, 17) / 32768.0;
            double q3 = s4(data, 22) / 32768.0;
            double q0 = Math.sqrt(Math.max(0.0, 1.0 - (q1 * q1 + q2 * q2 + q3 * q3)));

            double riqsq = q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3;
            double iqsq = 1.0 / riqsq;
            double r02 = q0 * q2 * 2 * iqsq, r13 = q1 * q3 * 2 * iqsq;
            double sinpitch = r13 - r02;
            double r01 = q0 * q1 * 2 * iqsq, r23 = q2 * q3 * 2 * iqsq;
            double sinroll = r23 + r01;
            double r00 = q0 * q0 * 2 * iqsq, r11 = q1 * q1 * 2 * iqsq, r03 = q0 * q3 * 2 * iqsq, r12 = q1 * q2 * 2 * iqsq;
            double a00 = r00 - 1 + r11, a01 = r12 + r03;
            double rads = Math.atan2(a00, -a01);

            northorientA = 180 - Math.toDegrees(rads);
            pitchA = Math.toDegrees(Math.asin(sinpitch));
            rollA = Math.toDegrees(Math.asin(sinroll));
            //Log.i("hhanglogM", "pitch "+pitch+" roll "+roll);
            return;
        }

        // "Qt00000000x00000000y00000000a0000\n"
        if ((data[0] == 'Q') || ((data[0] == 'a') && (data[1] == 'Q'))) {
            int i = (data[0] == 'a' ? 1 : 0);
            double lat = (s4(data, 11 + i) * 0x10000 + (s4(data, 15 + i) & 0xFFFF)) / 600000.0;
            double lng = (s4(data, 20 + i) * 0x10000 + (s4(data, 24 + i) & 0xFFFF)) / 600000.0;
            double lalt = s4(data, 29 + i);
            if (lat0 == -9999.0) {
                lat0 = lat;
                lng0 = lng;
                double earthrad = 6378137;
                nyfac = 2*Math.PI*earthrad/360;
                exfac = nyfac*Math.cos(Math.toRadians(lat0));
            }
            if (data[0] == 'a') {
                xposA = (lng - lng0)*exfac;
                yposA = (lat - lat0)*nyfac;
                altA = lalt;
            } else {
                xpos = (lng - lng0)*exfac;
                ypos = (lat - lat0)*nyfac;
                alt = lalt;
            }
        }

        // "Nt00000000s000000r000000\n"
        if ((data[0] == 'N') || ((data[0] == 'd') && (data[1] == 'N'))) {
            int i = (data[0] == 'd' ? 1 : 0);
            sprogpressure[0 + i*2] = s6(data, 11 + i) / 1024.0;
            sprogpressure[1 + i*2] = s6(data, 18 + i) / 1024.0;
            //Log.i("hhanglogM", "sprog "+sprogpressure[0 + i*2] + ", "+sprogpressure[1 + i*2]);
        }
    }
}
