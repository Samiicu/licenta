package com.example.samuel.pentrufacultate.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.example.samuel.pentrufacultate.R;

import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER;
import static com.example.samuel.pentrufacultate.models.StringHelper.RESULT_QR_READER_SUCCESS;

public class DecoderActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {
    private final String TAG = DecoderActivity.class.getSimpleName();

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;

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

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
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
}