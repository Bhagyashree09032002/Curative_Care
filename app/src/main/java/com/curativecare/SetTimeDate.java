package com.curativecare;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import com.curativecare.SharedPreferences.SharedPreferenceManager;

public class SetTimeDate extends AppCompatActivity {

    TextView setDate,setTime;
    Button send;
    String url=IPaddress.ip+"user_request.php";
    ProgressDialog progressDialog;
    SharedPreferenceManager sharedPreferenceManager;
    String key="",cdate="",message="";
    String files[];
    OutputStream out=null;
    File path,dir,fl;
    Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time_date);
        setDate = (TextView) findViewById(R.id.date);
        setTime = (TextView) findViewById(R.id.time);
        send = (Button) findViewById(R.id.send);

        final String doctorMobile=getIntent().getExtras().getString("doctorMobileNumber");

        sharedPreferenceManager=new SharedPreferenceManager(SetTimeDate.this);
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

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        cdate = format.format(Calendar.getInstance().getTime());

        setDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SetTimeDate.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SetTimeDate.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setTime.getText().toString().equals("click here to set time")||setDate.getText().toString().equals("click here to set date")){
                    Toast.makeText(SetTimeDate.this, "Select Valid Date/Time", Toast.LENGTH_SHORT).show();
                }else{
                   // UserObject userObject=new UserObject();
                    UserData.appointmentDate=setDate.getText().toString().trim();
                    UserData.appointmentTime=setTime.getText().toString().trim();
                    UserData.choice=UserData.choice.replace("%",",");
                    UserData.choice=UserData.choice.replaceFirst(",","");
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
                    String cdate = format.format(Calendar.getInstance().getTime());
                    key = UUID.randomUUID().toString();
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE,200,200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        //imageView.setImageBitmap(bitmap);
                        path = Environment.getExternalStorageDirectory();
                        dir = new File(path + "/healthcare_qr_code/");
                        dir.mkdirs();
                        fl = new File(dir, UserData.uo.username+cdate+ ".JPEG");

                        out = new FileOutputStream(fl);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        message = "Your qr code for appointment of Dr."+UserData.uo.username+" for date "+cdate;
                        sendMail(message,UserData.email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userRequest();
                }
            }
        });

    }

    private void userRequest() {
        RequestParams params=new RequestParams();
        params.put("username",UserData.name);
        params.put("usermobile",UserData.mobile);
        params.put("adminmobile",UserData.doctorMobile);
        params.put("date",UserData.appointmentDate);
        params.put("time",UserData.appointmentTime);
        params.put("symptoms",UserData.choice);
        params.put("email",UserData.email);
        params.put("qrkey",key);
        progressDialog=new ProgressDialog(SetTimeDate.this);
        progressDialog.setMessage("Sending request...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result=new String(responseBody);
                System.out.println(result);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("success").equals("200")){
                        Toast.makeText(SetTimeDate.this, "Request sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SetTimeDate.this, "JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(SetTimeDate.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLabel() {

            String myFormat = "yy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            setDate.setPaintFlags(setDate.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            setDate.setText(sdf.format(myCalendar.getTime()));
        }

    public void sendMail(final String message, final String mailId){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    SendMailSSL sendMailSSL = new SendMailSSL();
                    //sendMailSSL.EmailSending(mailId, "Reporting mail", message, fl);
                    path = Environment.getExternalStorageDirectory();
                    dir = new File(path + "/healthcare_qr_code/");
                    files = new String[]{path+"/healthcare_qr_code/"+UserData.uo.username+cdate+ ".JPEG"};
                    sendMailSSL.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mailId, "Appointment booked for doctor visit",message,files);
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        //finish();
    }
}
