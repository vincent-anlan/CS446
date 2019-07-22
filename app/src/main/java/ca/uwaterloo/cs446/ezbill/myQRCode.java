package ca.uwaterloo.cs446.ezbill;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class myQRCode extends AppCompatActivity {
    Button saveBtn;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    int numOfTry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.qrcode_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("My QR Code");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        saveBtn = (Button) findViewById(R.id.save);
        generateQRCode();
        numOfTry = 0;
    }


    public void generateQRCode() {
        try {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int dimension = width < height ? width : height;
//            dimension = dimension * 3 / 4;

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String id;
                if (extras.getString("accountBookId") != null) {
                    id = "A" + extras.getString("accountBookId");
                } else {
                    id = "U" + extras.getString("userId");
                }
                Bitmap bitmap = barcodeEncoder.encodeBitmap(id, BarcodeFormat.QR_CODE, dimension, dimension);
                ImageView imageViewQrCode = (ImageView) findViewById(R.id.QR_image);
                imageViewQrCode.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Fail to generate QR code, please try again.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    public void onSave(View view) {
        try {
            ++numOfTry;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            ImageView imageViewQrCode = (ImageView) findViewById(R.id.QR_image);
            BitmapDrawable draw = (BitmapDrawable) imageViewQrCode.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            File dir = new File(Environment.getExternalStorageDirectory() + "/save/");
            dir.mkdirs();

            File file = new File(dir, String.format("%d.png", System.currentTimeMillis()));
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);

            final  AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Saved");
            alertDialog.show();

            // Hide after some seconds
            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            };

            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 1000);


//            Toast.makeText(myQRCode.this, "Saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            if (numOfTry < 2) {
                onSave(view);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Fail to save QR code, please try again.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        }
    }

    public void cancelButtonHandlerBack(View v) {
        finish();
    }

}


