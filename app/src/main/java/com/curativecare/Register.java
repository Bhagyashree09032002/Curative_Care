package com.curativecare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class Register extends AppCompatActivity {

    EditText username,mname,lname,password,mobile,mail,problems,relative;
    TextView dob;
    Button register;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year1, month, day;
    Spinner blood,bp,allergy;
    ImageView addpic;

    File path,dir,fl;
    OutputStream out = null;

    DatePickerDialog datepicker;
    Calendar myCalendar = Calendar.getInstance();
    String[] files;
    String url=IPaddress.ip+"stdregister.php";
    String SecretKey="",Encrpted="",dobb="",yea="",rel="",div="",dept="",image="",qrimage="",name1="",mname1="",lname1="",mail1="",mobile1="",problems1="",password1="";
    private static int RESULT_LOAD_IMAGE = 1;
    private final int MY_PERMISSION_READ_EXTERNAL_STORAGE=1;
    private static final int PICK_FROM_GALLERY = 2;
    Bitmap thumbnail = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username= (EditText) findViewById(R.id.editTextRegUser);
        mname= (EditText) findViewById(R.id.editTextRegMUser);
        lname= (EditText) findViewById(R.id.editTextRegLUser);
        dob = (TextView) findViewById(R.id.editTextSRegDOB);
        mail= (EditText) findViewById(R.id.editTextRegMailId);
        mobile= (EditText) findViewById(R.id.editTextRegMobileNo);
        blood = (Spinner) findViewById(R.id.spinnerBlood);
        bp = (Spinner) findViewById(R.id.spinnerBP);
        allergy = (Spinner) findViewById(R.id.spinnerAllergy);
        problems= (EditText) findViewById(R.id.editTextProblems);
        addpic = (ImageView) findViewById(R.id.imgphoto);
        password= (EditText) findViewById(R.id.editTextRegPassword);

        relative= (EditText) findViewById(R.id.editTextRelative);

        register= (Button) findViewById(R.id.buttonSubmit);

        dob.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        calendar = Calendar.getInstance();

        year1 = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        String[] year1 = {"Select Blood Group", "O+", "O-", "A+", "A-","B+","B-","AB+","AB-"};
        final ArrayAdapter adapter1 = new ArrayAdapter(Register.this,android.R.layout.simple_spinner_item,year1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        blood.setAdapter(adapter1);

        String[] div1 = {"Select BP Range","80-100","90-110","100-120"};
        ArrayAdapter a1 = new ArrayAdapter(Register.this,android.R.layout.simple_spinner_item, div1);
        bp.setAdapter(a1);

        String[] dept1 = {"Select Allergy","Skin","Medicine","Dust","None"};
        ArrayAdapter a2 = new ArrayAdapter(Register.this,android.R.layout.simple_spinner_item, dept1);
        allergy.setAdapter(a2);

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

        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Register.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Register.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_READ_EXTERNAL_STORAGE);
                }else {
                    Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(in, RESULT_LOAD_IMAGE);
                }
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datepicker = new DatePickerDialog(Register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datepicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datepicker.show();
            }
        });

        blood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0 : yea = "";
                        break;
                    default:
                        yea = adapterView.getItemAtPosition(i).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0 : div = "";
                        break;
                    default: div = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        allergy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0 : dept ="";
                        break;
                    default:
                        dept = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name1=username.getText().toString().trim();
                mname1=mname.getText().toString().trim();
                lname1= lname.getText().toString().trim();
                mail1=mail.getText().toString().trim();
                mobile1=mobile.getText().toString().trim();
                problems1=problems.getText().toString().trim();
                password1=password.getText().toString().trim();
                rel = relative.getText().toString().trim();

                if(username.getText().toString().isEmpty()||password.getText().toString().isEmpty()
                        || relative.getText().toString().isEmpty()|| mobile.getText().toString().isEmpty() || mail.getText().toString().isEmpty()){
                    Toast.makeText(Register.this,"Vacant Fields", Toast.LENGTH_SHORT).show();
                }else if(!(mobile.getText().length()==10)){
                    Toast.makeText(Register.this,"Enter Valid Number"+mobile.getText().length(), Toast.LENGTH_SHORT).show();
                }else{
                    if(Pattern.matches("^([0-9]){10}$",mobile1)) {
                        if(Pattern.matches("^([A-Za-z0-9_\\.\\$])+\\@([A-Za-z0-9_])+\\.+com$",mail1)) {
                            AESEncrypt a = new AESEncrypt();
                            PasswordAthenticationProtocol RN = new PasswordAthenticationProtocol();
                            SecretKey = RN.generateRandomString();
                            Encrpted = null;
                            try {
                                Encrpted = a.encrypt( mail1, SecretKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("---Encrpted---" + Encrpted);

                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(Encrpted, BarcodeFormat.QR_CODE, 200, 200);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                //imageView.setImageBitmap(bitmap);
                                path = Environment.getExternalStorageDirectory();
                                dir = new File(path + "/CurativeCare/");
                                dir.mkdirs();
                                fl = new File(dir,   mobile1+ ".jpg");

                                out = new FileOutputStream(fl);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                                qrimage = getStringImage(bitmap);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            register();
                        }else{
                            mail.setError("Invalid email ID!");
                        }
                    }else{
                        mobile.setError("Invalid mobile number!");
                    }
                }
            }
        });
    }

    private void register() {
        RequestParams params=new RequestParams();
        params.put("name",name1);
        params.put("mname",mname1);
        params.put("lname",lname1);
        params.put("dob",dobb);
        params.put("mail",mail1);
        params.put("mobile",mobile1);
        params.put("blood",yea);
        params.put("bp",div);
        params.put("allergy",dept);
        params.put("problems",problems1);
        params.put("image",image);
        params.put("pass",password1);
        params.put("seckey",SecretKey);
        params.put("encrypted",Encrpted);
        params.put("qrimage",qrimage);
        params.put("rel",rel);


        progressDialog=new ProgressDialog(Register.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               progressDialog.dismiss();
                String result=new String(responseBody);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("success").equals("200")){
                        sendMail("You have successfully registred.<br> Your Details.<br>"+
                                "Full Name : "+name1+" "+mname1+" "+lname1 +"<br>"+
                                "Email : "+ mail1 + " "+"<br>"+
                                "Password : "+ password1 + " "+"<br>"+
                                "QR Code : "+ Encrpted + " "+"<br>"+
                                "SecretKey :"+ SecretKey,mail1);
                        /*sendEmail("You have successfully registred.<br> Your Details.<br>"+

                                "Full Name : "+name1+" "+mname1+" "+lname1 +"<br>"+
                                "Email : "+ mail1 + " "+"<br>"+
                                "Password : "+ password1 + " "+"<br>"+
                                "QR Code : "+ Encrpted + " "+"<br>"+
                                "SecretKey :"+ SecretKey,mail1);*/

                        Toast.makeText(Register.this,"Registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Register.this,Welcome.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(Register.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Register.this,"JSON Error", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    public void sendMail(final String message, final String mailId){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    SendMailSSL sendMailSSL = new SendMailSSL();
                    //sendMailSSL.EmailSending(mailId, "Reporting mail", message, fl);
                    //files = new String[]{IPaddress.ip+"qr/"+mobile1+".png"};
                    files = new String[]{fl.getAbsolutePath()};
                    sendMailSSL.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mailId, "Registration mail",message,files);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSION_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(in, RESULT_LOAD_IMAGE);

                } else {

                    ActivityCompat.requestPermissions(Register.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_READ_EXTERNAL_STORAGE);
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

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
            addpic.setImageBitmap(thumbnail);
        }
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setPaintFlags(dob.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        dob.setText("Date of Birth: "+sdf.format(myCalendar.getTime()));
        dobb = sdf.format(myCalendar.getTime());
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            return new DatePickerDialog(this, myDateListener, year1, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(1997, arg2+1, arg3);
        }
    };
    private void showDate(int year, int month, int day) {
        dob.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

}
