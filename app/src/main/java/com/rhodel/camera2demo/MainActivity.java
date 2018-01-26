package com.rhodel.camera2demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextureView mTextureView;
    SurfaceTexture mSurfaceTexture;
    Activity mMainActivity;
    CameraManager mCameraManager;
    String mCameraId;
    CameraDevice mCamera;
    CameraCaptureSession mCameraCaptureSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainActivity = this;

        mTextureView = (TextureView) findViewById(R.id.textureView);

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                mSurfaceTexture = surfaceTexture;
                mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


                int rawWidth = 0;
                int rawHeight = 0;



                try {
                    mCameraId = mCameraManager.getCameraIdList()[0]; //Get the first camera found.
                    CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] rawSizes = streamConfigurationMap.getOutputSizes(ImageFormat.RAW_SENSOR);
                    //Size[] jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);

                    rawWidth = rawSizes[0].getWidth();
                    rawHeight = rawSizes[0].getHeight();


                }
                catch (Exception e) {
                    Log.d("NO_CAMERAS_FOUND", e.toString());
                }

                ImageReader rawImageReader = ImageReader.newInstance(rawWidth, rawHeight, ImageFormat.RAW_SENSOR, 1);
                rawImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader imageReader) {
                        //save raw
                    }
                }, null);

                final Surface previewSurface = new Surface(mSurfaceTexture);
                Surface rawCaptureSurface = rawImageReader.getSurface();

                if(mMainActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //grant the permission
                    mMainActivity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                }
                else {
                    try {
                        mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                            @Override
                            public void onOpened(@NonNull CameraDevice cameraDevice) {
                                mCamera = cameraDevice;
                                List<Surface> surfaces = Arrays.asList(previewSurface);
                                try {
                                    mCamera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                                        @Override
                                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                            mCameraCaptureSession = cameraCaptureSession;


                                            try {
                                                CaptureRequest.Builder request = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                                request.addTarget(previewSurface);
                                                mCameraCaptureSession.setRepeatingRequest(request.build(), new CameraCaptureSession.CaptureCallback() {
                                                    @Override
                                                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                                        super.onCaptureCompleted(session, request, result);
                                                    }
                                                }, null);
                                            }
                                            catch(Exception e) {

                                            }

                                        }

                                        @Override
                                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                                        }
                                    }, null);
                                }
                                catch (Exception e) {
                                    Log.d("ERROR", e.toString());
                                }
                            }

                            @Override
                            public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                            }

                            @Override
                            public void onError(@NonNull CameraDevice cameraDevice, int error) {

                            }
                        }, null);
                    }
                    catch (Exception e) {

                    }


                }






            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:

                break;
        }
    }
}
