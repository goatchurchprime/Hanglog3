package com.example.julian.hanglog3;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
//import android.util.Size;
import android.view.Surface;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;

import org.opencv.aruco.Dictionary;
import org.opencv.imgproc.Imgproc;
*/


public class ReadCamera extends Thread {
    int skippedframes = 0;
    int framesread = 0;

    ReadCamera(LLog3 lllog3, Context lapplicationcontext) {
    }

    public void gologpics(boolean isChecked) {
    }
}



/*
class ReceivedImage {
    int framenumber;
    Calendar frametime;
    Mat rgbMat;
    Mat grayMat = null;

    ReceivedImage(int lframenumber, Calendar lframetime, Mat lrgbMat) {
        framenumber = lframenumber;
        frametime = lframetime;
        rgbMat = lrgbMat;
    }

    Mat getgrayMat()
    {
        if (grayMat == null) {
            grayMat = Mat.zeros(rgbMat.height(), rgbMat.width(), CvType.CV_8UC1);
            Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_BGR2GRAY);
Log.i("hhanglogCCgray", "gray: " + rgbMat.height()+","+rgbMat.width());
        }
        return grayMat;
    }

    Bitmap converttobmp()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap bmp = Bitmap.createBitmap(rgbMat.cols(), rgbMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbMat, bmp);
        return bmp;
    }

    File framefilename(File ddir) {
        return new File(ddir, ReadCamera.ddsss.format(frametime.getTime()) + "_" + String.valueOf(framenumber) + ".jpg");
    }



};


public class ReadCamera extends Thread {

    int saveeveryframeinterval = 0;
    boolean bcharucootherwisechess = true;
    //int imagerequestedWidth = 320, imagerequestedHeight = 240;
    int imagerequestedWidth = 640, imagerequestedHeight = 480;
    int skippedframes = 0;
    int framesread = 0;

    TimeZone timeZoneUTC = TimeZone.getTimeZone("UTC");
    SimpleDateFormat ddsdf = new SimpleDateFormat("yyyyMMddHHmmss");
    static SimpleDateFormat ddsss = new SimpleDateFormat("HHmmss.SSS");

    protected CameraDevice cameraDevice = null;
    protected CameraCaptureSession session = null;
    CaptureRequest capturerequest;
    CameraManager cameraManager = null;
    ImageReader imageReader = null;

    String cameraID = null;

    android.util.Size imageDimension = null;

    LLog3 llog3;
    Context applicationcontext;
    BlockingQueue<ReceivedImage> rawcameraimgqueue = new ArrayBlockingQueue<ReceivedImage>(3, true);
    boolean bacquiringimages = false;

    org.opencv.core.Size board_sz = new org.opencv.core.Size(6, 9);
    int mCornersSize = (int)(board_sz.width * board_sz.height);
    float mSquareSize = 0.02488F;
    Mat chessboardcorners = null;

    org.opencv.core.Size winSize = new org.opencv.core.Size(5, 5);
    org.opencv.core.Size zeroZone = new org.opencv.core.Size(-1, -1);
    TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, 30, 0.1);
    org.opencv.core.Size mImageSize = null;


    Mat mCameraMatrix;
    Mat mDistortionCoefficients;

    // imagerequestedWidth = 320, imagerequestedHeight = 240:
    //double[] mCameraMatrixdata = {264.9033392766471, 0.0, 160.20863466317323, 0.0, 263.9151565707493, 120.18598837732736, 0.0, 0.0, 1.0};
    //double[] mDistortionCoefficientsdata = {0.06522344324121337, -0.28901021975336144, 0.003548857636261892, -0.005779355866042926, 0.45972249493447437};
    // imagerequestedWidth = 640, imagerequestedHeight = 480:
    double[] mCameraMatrixdata = {560.2579866820173, 0.0, 320.02303700498317, 0.0, 590.6518491657391, 240.04051419696953, 0.0, 0.0, 1.0};
    double[] mDistortionCoefficientsdata = {0.11009461279226761, -0.5207408831756383, -0.003506107151687262, 0.0012457176202829781, 1.0903450462033453};

    org.opencv.aruco.Dictionary aruco_dict = null;
    org.opencv.aruco.DetectorParameters parameters = null;

    int squaresX = 5, squaresY = 7;
    float markersquareratio = 0.5F;
    float chesssquareLength = 0.025F;
    org.opencv.aruco.CharucoBoard charboard;

    // constructor
    ReadCamera (LLog3 lllog3, Context lapplicationcontext)
    {
        llog3 = lllog3;
        applicationcontext = lapplicationcontext;
        ddsdf.setTimeZone(timeZoneUTC);
        ddsss.setTimeZone(timeZoneUTC);
        chessboardcorners = Mat.zeros(mCornersSize, 1, CvType.CV_32FC3);
        chessboardcorners.create(mCornersSize, 1, CvType.CV_32FC3);
        int cn = 3;
        float positions[] = new float[mCornersSize * cn];
        for (int i = 0; i < board_sz.height; i++) {
            for (int j = 0; j < board_sz.width; j++) {
                positions[(int)(i*board_sz.width*cn + j*cn + 0)] = j*mSquareSize;
                positions[(int)(i*board_sz.width*cn + j*cn + 1)] = i*mSquareSize;
                positions[(int)(i*board_sz.width*cn + j*cn + 2)] = 0;
            }
        }

        chessboardcorners.put(0, 0, positions);
        Log.i("hhanglogCCMC", mattostring(chessboardcorners));

        mCameraMatrix = Mat.zeros(3, 3, CvType.CV_64F);
        mDistortionCoefficients = Mat.zeros(5, 1, CvType.CV_64F);

        mCameraMatrix.put(0, 0, mCameraMatrixdata);
        mDistortionCoefficients.put(0, 0, mDistortionCoefficientsdata);

        aruco_dict = org.opencv.aruco.Dictionary.get(org.opencv.aruco.Aruco.DICT_4X4_50);
        parameters = org.opencv.aruco.DetectorParameters.create();
        charboard = org.opencv.aruco.CharucoBoard.create(squaresX, squaresY, chesssquareLength, chesssquareLength*markersquareratio, aruco_dict);

        Log.i("hhanglogMcameraInit", mattostring(mCameraMatrix));
        Log.i("hhanglogMdistorInit", mattostring(mDistortionCoefficients));

    }

    String mattostring(Mat m)
    {
        //CvType.depth(m.type()) == CvType.CV_32F
        //CvType.depth(m.type()) == CvType.CV_64F
        StringBuilder sb = new StringBuilder();
        sb.append(m.size().toString());
        sb.append(" channels ");
        sb.append(String.valueOf(m.channels()));
        sb.append(": numpy.array([ ");
        if (CvType.depth(m.type()) == CvType.CV_64F) {
            double[] mdata = new double[m.width() * m.height() * m.channels()];
            m.get(0, 0, mdata);
            for (int i = 0; i < mdata.length; i++)
                sb.append((i == 0 ? "" : ", ") + String.valueOf(mdata[i]));
            sb.append("], numpy.float64)");
        } else if (CvType.depth(m.type()) == CvType.CV_32F) {
            float[] mdata = new float[m.width() * m.height() * m.channels()];
            m.get(0, 0, mdata);
            for (int i = 0; i < mdata.length; i++)
                sb.append((i == 0 ? "" : ", ") + String.valueOf(mdata[i]));
            sb.append("], numpy.float32)");
        } else {
            sb.append("unknown type="+String.valueOf(m.type())+" depth="+CvType.depth(m.type()));
        }
        sb.append(".reshape(("+(int)m.size().width+", "+(int)m.size().height+", "+m.channels()+"))");
        return sb.toString();
    }


    void calibratechessboard(ArrayList<Mat> mCornersBuffer)
    {
        ArrayList<Mat> rvecs = new ArrayList<Mat>();
        ArrayList<Mat> tvecs = new ArrayList<Mat>();
        ArrayList<Mat> objectPoints = new ArrayList<Mat>();

        Log.i("hhanglogMCB", "chesscorners: " + mattostring(chessboardcorners));

        while (mCornersBuffer.size() > 40)
            mCornersBuffer.remove(0);

        for (int i = 0; i < mCornersBuffer.size(); i++) {
            objectPoints.add(chessboardcorners);
            Log.i("hhanglogMCC", "chesscbuff"+i+": " + mattostring(mCornersBuffer.get(i)));
        }

        Log.i("hhanglogCCMcal", "calibrating number: " + mCornersBuffer.size());
        TermCriteria ccriteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, 30, 0.02);
        int mFlags = 0; //Calib3d.CALIB_FIX_PRINCIPAL_POINT + Calib3d.CALIB_ZERO_TANGENT_DIST + Calib3d.CALIB_FIX_ASPECT_RATIO + Calib3d.CALIB_FIX_K4 + Calib3d.CALIB_FIX_K5;
        Calib3d.calibrateCamera(objectPoints, mCornersBuffer, mImageSize, mCameraMatrix, mDistortionCoefficients, rvecs, tvecs, mFlags, ccriteria);

        Log.i("hhanglogMcamera", mattostring(mCameraMatrix));
        Log.i("hhanglogMdistor", mattostring(mDistortionCoefficients));

        // calibrations come very different to what it calculates with same inputs on the PC
        // chesscbuffi = numpy.array([ ... ], numpy.float32).reshape(54,2)
        // retval, cameraMatrix, distCoeffs, rvecs, tvecs = cv2.calibrateCamera([chesscorners]*len(imagePoints), imagePoints, (320,240), None, None)
    }

    void detectcharucoboard(Mat rgbMat, Mat rvec, Mat tvec)
    {
        try {
            List<Mat> markerCorners = new ArrayList<>();
            Mat markerIds = new Mat();
            List<Mat> rejectedImgPoints = new ArrayList<>();
            org.opencv.aruco.Aruco.detectMarkers(rgbMat, aruco_dict, markerCorners, markerIds, parameters, rejectedImgPoints, mCameraMatrix, mDistortionCoefficients);
            if (markerCorners.size()>0)
            {
                //org.opencv.aruco.Aruco.drawDetectedMarkers(BGRMat, markerCorners);
                //org.opencv.aruco.Aruco.refineDetectedMarkers(BGRMat, aruco_dict, markerCorners, markerIds, rejectedImgPoints, rejectedImgPoints, mCameraMatrix, mDistortionCoefficients);
                Mat charucoCorners = new Mat();
                Mat charucoIds = new Mat();
                int res2 = org.opencv.aruco.Aruco.interpolateCornersCharuco(markerCorners, markerIds, rgbMat, charboard, charucoCorners, charucoIds, mCameraMatrix, mDistortionCoefficients);
                //Aruco.estimatePoseSingleMarkers(corners, 0.85, cameraMatrix);
                org.opencv.aruco.Aruco.estimatePoseCharucoBoard(charucoCorners, charucoIds, charboard, mCameraMatrix, mDistortionCoefficients, rvec, tvec);
            }
        } catch (org.opencv.core.CvException e) {
            e.printStackTrace();
        }
    }




    void drawcharucoaxes(Mat rgbMat, Mat rvec, Mat tvec)
    {
        assert !rvec.empty();
        //Log.i("hhanglogMrvec", mattostring(rvec)+" tvec: "+mattostring(tvec));
        Mat rotmat = new Mat();
        org.opencv.calib3d.Calib3d.Rodrigues(rvec, rotmat);
        Log.i("hhanglogMrvec", mattostring(rotmat));
        double[] drotmat = new double[9];
        double[] dtvec = new double[3];
        rotmat.get(0, 0, drotmat);
        tvec.get(0, 0, dtvec);
        dtvec[0] = dtvec[0] + drotmat[0]*(squaresX*chesssquareLength/2) + drotmat[1]*(squaresY*chesssquareLength/2);
        dtvec[1] = dtvec[1] + drotmat[3]*(squaresX*chesssquareLength/2) + drotmat[4]*(squaresY*chesssquareLength/2);
        dtvec[2] = dtvec[2] + drotmat[6]*(squaresX*chesssquareLength/2) + drotmat[7]*(squaresY*chesssquareLength/2);
        Mat ctvec = tvec.clone();
        ctvec.put(0, 0, dtvec);
        org.opencv.aruco.Aruco.drawAxis(rgbMat, mCameraMatrix, mDistortionCoefficients, rvec, ctvec, 0.1F);
    }


    @Override
    public void run() {

        try {

            Calendar startNow = Calendar.getInstance(timeZoneUTC);
            File fdir = new File(Environment.getExternalStorageDirectory(), "hanglog");
            File ddir = new File(fdir, "pics" + ddsdf.format(startNow.getTime()));
            if (saveeveryframeinterval != 0)
                ddir.mkdirs();
            ArrayList<Mat> mCornersBuffer = new ArrayList<Mat>();

            Mat rvec = new Mat();
            Mat tvec = new Mat();
            double[] drvec = new double[3];
            double[] dtvec = new double[3];

            // main loop
            while (true) {

                // wait till we are acquiring images
                while (!bacquiringimages)
                    sleep(100);

                // the process and record chessboards/charucoboards
                mCornersBuffer.clear();
                while (bacquiringimages) {
                    long tval = System.currentTimeMillis();
                    ReceivedImage receivedimage = rawcameraimgqueue.take();
Log.d("hhanglogC8", "buffer taken "+receivedimage.framenumber);
                    if ((saveeveryframeinterval != 0) && ((receivedimage.framenumber % saveeveryframeinterval) == 0)) {
                        Bitmap bmp = receivedimage.converttobmp();
                        File filejpg = receivedimage.framefilename(ddir);
                        try {
                            Log.i("hhanglogP", "Pic saved " + filejpg.toString());
                            OutputStream os = new BufferedOutputStream(new FileOutputStream(filejpg));
                            bmp.compress(Bitmap.CompressFormat.JPEG, 30, os);
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mImageSize == null)
                        mImageSize = new org.opencv.core.Size(receivedimage.rgbMat.width(), receivedimage.rgbMat.height());

                    if (bcharucootherwisechess) {
                        detectcharucoboard(receivedimage.rgbMat, rvec, tvec);
                        if (llog3.cpos.cameraview == null) {
                            if (!rvec.empty())
                                drawcharucoaxes(receivedimage.rgbMat, rvec, tvec);
                            llog3.cpos.cameraview = receivedimage.converttobmp();
                        }
                        if (!rvec.empty()) {
                            rvec.get(0, 0, drvec);
                            tvec.get(0, 0, dtvec);
                            llog3.readsensor.outputCharucoPosition(tval, drvec[0], drvec[1], drvec[2], dtvec[0], dtvec[1], dtvec[2]);
                            rvec.release();
                            tvec.release();
                        }
                    } else {
                        MatOfPoint2f chessboardcorners = new MatOfPoint2f();
                        boolean bchessboardfound = Calib3d.findChessboardCorners(receivedimage.rgbMat, board_sz, chessboardcorners, Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FILTER_QUADS);
                        //if (bchessboardfound)
                        //    Imgproc.cornerSubPix(receivedimage.getgrayMat(), chessboardcorners, winSize, zeroZone, criteria);
                        if (!chessboardcorners.empty())
                            mCornersBuffer.add(chessboardcorners);
                        if (llog3.cpos.cameraview == null) {
                            if (bchessboardfound)
                                Calib3d.drawChessboardCorners(receivedimage.rgbMat, board_sz, chessboardcorners, bchessboardfound);
                            llog3.cpos.cameraview = receivedimage.converttobmp();
                        }
                    }
                } // end while (bacquiringimages)

                // then calebrate the chessboards
                if (mCornersBuffer.size() >= 1)
                    calibratechessboard(mCornersBuffer);

            } // while (true) (to enable image acquisition again

        } catch (InterruptedException e) {
            Log.d("hhanglogC8", "buffer interruption while loop");
            e.printStackTrace();
        }
    }

    protected ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //Log.i(TAG, "onImageAvailable");
            Calendar rightNow = Calendar.getInstance(timeZoneUTC);
            framesread++;
            Image img = reader.acquireLatestImage();
            if (bacquiringimages && (img != null)) {
                // https://github.com/quickbirdstudios/yuvToMat/blob/master/lib/src/main/java/com/quickbirdstudios/yuv2mat/YuvN12Image.kt
                ByteBuffer yBuffer = img.getPlanes()[0].getBuffer();
                ByteBuffer uBuffer = img.getPlanes()[1].getBuffer();
                ByteBuffer vBuffer = img.getPlanes()[2].getBuffer();
                int ySize = yBuffer.remaining();
                int uSize = uBuffer.remaining();

                yBuffer.position(0);
                uBuffer.position(0);
                byte[] data = new byte[ySize + uSize];
                yBuffer.get(data, 0, ySize);
                uBuffer.get(data, ySize, uSize);
                Mat yuvMat = Mat.zeros(img.getHeight() + img.getHeight() / 2, img.getWidth(), CvType.CV_8UC1);
                yuvMat.put(0, 0, data);
                Mat rgbMat = Mat.zeros(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
                Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV12);
                yuvMat.release();
                //Log.d("hhanglogC7s", rgbMat.size().toString());

                if ((framesread%100) == 0)
                    Log.d("hhanglogC7", "image buffer "+framesread+" size:"+data.length);
                if (rawcameraimgqueue.remainingCapacity() >= 2)
                    rawcameraimgqueue.add(new ReceivedImage(framesread, rightNow, rgbMat));
                else {
                    skippedframes++;
                    Log.d("hhanglogC7", "dropping frame " + framesread);
                }
            }
            if (img != null)
                img.close();
        }
    };


    protected CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession lsession) {
            Log.i("hhanglogC6", "CameraCaptureSession.StateCallback onConfigured");
            session = lsession;
            try {
                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                builder.addTarget(imageReader.getSurface());
                capturerequest = builder.build();
                session.setRepeatingRequest(capturerequest, null, null);
            } catch (CameraAccessException e) {
                Log.e("hhanglogC6", e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.i("hhanglogC6", "CameraCaptureSession.StateCallback congfigfailed");
            Log.i("hhanglogC6", session.toString());
        }
    };

    protected CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d("hhanglogC5", "CameraDevice.StateCallback onOpened");
            cameraDevice = camera;
            Log.d("hhanglogC55", imageReader.getSurface().toString());

            List<Surface> x = Arrays.asList(imageReader.getSurface());
            for (Surface sx : x)
                Log.d("hhanglogC55h", sx.toString());

            try {
                cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
            } catch (CameraAccessException e){
                Log.e("hhanglogC5", e.getMessage());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d("hhanglogC5", "CameraDevice.StateCallback onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d("hhanglogC5", "CameraDevice.StateCallback onError " + error);
        }
    };


    public void getcameraaccess()
    {
        Log.d("hhanglogC", "Camera isopennn");
        cameraManager = (CameraManager)llog3.getSystemService(Context.CAMERA_SERVICE);
        Log.d("hhanglogC", "Camera isopen");

        CameraCharacteristics characteristics = null;
        try {
            String[] cameraIDlist = cameraManager.getCameraIdList();
            for (int i = 0; i < cameraIDlist.length; i++) {
                String lcameraID = cameraIDlist[i];
                CameraCharacteristics lcharacteristics = cameraManager.getCameraCharacteristics(lcameraID );
                int cOrientation = lcharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                    Log.d("hhanglogC2", i + " front " + lcameraID);
                    cameraID = lcameraID;
                    characteristics = lcharacteristics;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (cameraID == null)
            return;

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        assert map != null;
        android.util.Size[] imageDimensionlist = map.getOutputSizes(SurfaceTexture.class);
        for (int i = 0; i < imageDimensionlist.length; i++)
            Log.d("hhanglogC3", imageDimensionlist[i].toString());
        imageDimension = imageDimensionlist[0];
        Log.d("hhanglogC3 biggest", imageDimension.toString());

        // Add permission for camera and let user grant the permission
        if (ActivityCompat.checkSelfPermission(llog3, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationcontext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(llog3, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            return;
        }

        imageReader = ImageReader.newInstance(imagerequestedWidth, imagerequestedHeight, ImageFormat.YUV_420_888, 2);
        if (imageReader == null)
            imageReader = ImageReader.newInstance(imageDimension.getWidth(), imageDimension.getHeight(), ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(onImageAvailableListener, null);
    }


    public void gologpics(boolean isChecked) throws CameraAccessException {
        if (imageReader == null)
            getcameraaccess();

        if (isChecked) {
            bacquiringimages = true;
            Log.d("hhanglogC33", imageReader.getSurface().toString());
            if (cameraDevice == null) {
                cameraManager.openCamera(cameraID, cameraStateCallback, null);
            } else if (session == null) {
                cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
            } else {
                session.setRepeatingRequest(capturerequest, null, null);
            }

        } else {
            bacquiringimages = false;
            if (session != null) {
                session.stopRepeating();
                rawcameraimgqueue.clear();
            }
        }
        Log.d("hhanglogC3", "imagereadercreated");
    }

}
*/