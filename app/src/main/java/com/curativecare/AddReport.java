package com.curativecare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class AddReport extends AppCompatActivity {

    String addReportURL = IPaddress.ip+"addreport.php";
    EditText ename,ecomment;
    ImageView report;
    Button ok;
    Bitmap thumbnail;
    private DatePicker datePicker;
    private DatePickerDialog datepicker;
    private Calendar myCalendar= Calendar.getInstance();
    private final int MY_PERMISSION_READ_EXTERNAL_STORAGE=1;
    TextView dor;
    String daten="",name="",comment="",image="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        ename = (EditText) findViewById(R.id.editAddReportName);
        ecomment = (EditText) findViewById(R.id.editAddReportComment);
        report = (ImageView) findViewById(R.id.imageAddReportImage);
        dor = (TextView) findViewById(R.id.textAddReportDate);
        ok = (Button) findViewById(R.id.btnAddReportDetailsOk);

        /*if(UserData.name==null){
            Intent i = new Intent(AddReport.this,Welcome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }*/

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datepicker = new DatePickerDialog(AddReport.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //datepicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datepicker.show();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddReport.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddReport.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_READ_EXTERNAL_STORAGE);
                }else {
                    Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(in, 1);
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = ename.getText().toString().trim();
                comment = ecomment.getText().toString().trim();
                if(name.isEmpty() || comment.isEmpty() || daten.isEmpty() || image.isEmpty()){
                    Toast.makeText(AddReport.this, "Fill all details", Toast.LENGTH_SHORT).show();
                }else{
                    addReport();
                }
            }
        });
    }

    public void addReport(){
        RequestParams params = new RequestParams();
        params.put("uid",IPaddress.uid);
        params.put("image",image);
        params.put("date",daten);
        params.put("comment",comment);
        params.put("rname",name);

        final ProgressDialog pDialog = ProgressDialog.show(AddReport.this,"Processing","connecting server...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(addReportURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                if(res.equals("200")){
                    Toast.makeText(AddReport.this, "Report added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(AddReport.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            thumbnail = (BitmapFactory.decodeFile(picturePath));
            thumbnail = Bitmap.createScaledBitmap(thumbnail,200,300,true);
            image = getStringImage(thumbnail);
            report.setImageBitmap(thumbnail);
        }
    }




    private void updateLabel() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dor.setPaintFlags(dor.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        dor.setText("Date of report: "+sdf.format(myCalendar.getTime()));
        daten = sdf.format(myCalendar.getTime());
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
