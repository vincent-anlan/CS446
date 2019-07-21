package ca.uwaterloo.cs446.ezbill;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextRecognitionActivity extends AppCompatActivity {
    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;

    String saveAmount;
    Model model;
    private TextView toolbar_title;
    Toolbar toolbar;

    private static final int requestPermissionID = 101;

    public void setToolbar() {
        toolbar = findViewById(R.id.group_toolbar_add_new_expense);
        setSupportActionBar(toolbar);
        toolbar_title = findViewById(R.id.add_new_trans_toolbar_title);
        toolbar_title.setText("Find Expense");
    }

    public void cancelButtonHandler(View v) {
        finish();
    }

    public void saveButtonHandler(View v) {
        model.cameraUpdateExpense(saveAmount);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pivot_use_camer);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);

        startCameraSource();
        model = Model.getInstance();
        setToolbar();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {
        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {

        } else {
            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();
        }

        /**
         * Add call back to SurfaceView and check if camera permission is granted.
         * If permission is granted we can start our cameraSource and pass it to surfaceView
         */
        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(TextRecognitionActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                requestPermissionID);
                        return;
                    }
                    mCameraSource.start(mCameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });

        //Set the TextRecognizer's Processor.
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            /**
             * Detect all the text from camera using TextBlock and the values into a stringBuilder
             * which will then be set to the textView.
             * */
            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0 ){

                    mTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i=0; i<items.size();){
                                TextBlock item = items.valueAt(i);
                                if(item.getValue().equals("Total") || item.getValue().equals("TOTAL")){
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("  ");
                                    if((i+1) < items.size()){
                                        Log.i("here",Integer.toString(items.size()));
                                        TextBlock item_num = items.valueAt(i+1);
                                        stringBuilder.append(item_num.getValue());
//                                        if(item_num.getValue().matches("[0-9]*\\.?[0-9]+")){
//                                            Log.i("here","float problem");
//                                            ArrayList<String> collectRaw =  parseIntsAndFloats(item_num.getValue());
//                                            for(int k = 0; k<collectRaw.size(); k++){
//                                                saveAmount = collectRaw.get(k);
//                                            }
//                                        }
                                        i++;
                                    }
                                }
                                i++;
                            }
                            mTextView.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });

    }

    private ArrayList<String> parseIntsAndFloats(String raw) {
        ArrayList<String> listBuffer = new ArrayList<String>();
        Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(raw);
        while (m.find()) {
            listBuffer.add(m.group());
        }
        return listBuffer;
    }

}
