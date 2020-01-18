package com.example.samuel.pentrufacultate.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.example.samuel.pentrufacultate.R;

import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER;
import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER_SUCCESS;

public class DecoderActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {
    private final String TAG = DecoderActivity.class.getSimpleName();

    private QRCodeReaderView qrCodeReaderView;
    private int requestedCode=23;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);
//
//        // Use this function to set front camera preview
//        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(DecoderActivity.this, new String[] {Manifest.permission.CAMERA}, requestedCode);
        }else {
            qrCodeReaderView.startCamera();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Log.i(TAG, "onQRCodeRead: ");
        if (text != null && text.startsWith("[Robo Bucatarul]-qr_recipes/")) {
            Intent result = new Intent();
            text = text.replace("[Robo Bucatarul]-", "");
//            String[] path = text.split("/");
            result.putExtra(RESULT_QR_READER, text);
            setResult(RESULT_QR_READER_SUCCESS, result);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestedCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}