package com.example.gofree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity{


    enum State{
        Login,SignUp;
    }
    State state;
    Button onetimelogin;
    EditText username,password,driverorpassenger,email;
    RadioButton Driver,Passenger;
    Button signuplogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state=State.SignUp;
        signuplogin=findViewById(R.id.signup);
        onetimelogin=findViewById(R.id.driverorpassenger);
        Driver=findViewById(R.id.driver);
        Passenger=findViewById(R.id.passenger);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        driverorpassenger=findViewById(R.id.dorp);
        email=findViewById(R.id.email);
        if(ParseUser.getCurrentUser()!=null){
           // ParseUser.logOut();
            transitiontopassengeractivity();
        }
        signuplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state==State.SignUp){
                    if(Driver.isChecked()==false && Passenger.isChecked()==false){
                        FancyToast.makeText(MainActivity.this,"Are you a Driver or Passenger?",FancyToast.WARNING,FancyToast.LENGTH_LONG,true).show();
                        return;
                    }
                    ParseUser parseUser=new ParseUser();
                    parseUser.setUsername(username.getText().toString());
                    parseUser.setPassword(password.getText().toString());
                    parseUser.setEmail(email.getText().toString());
                    if(Driver.isChecked()){
                        parseUser.put("as","Driver");
                    }
                    else if(Passenger.isChecked()){
                        parseUser.put("as","Passenger");
                    }
                    final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Signing Up!");
                    progressDialog.show();

                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                FancyToast.makeText(MainActivity.this,"Signed Up Successfully!",FancyToast.SUCCESS,FancyToast.LENGTH_LONG,true).show();
                                transitiontopassengeractivity();
                            }
                            else{
                                FancyToast.makeText(MainActivity.this,e.getMessage(),FancyToast.ERROR,FancyToast.LENGTH_LONG,true).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }else if(state==State.Login){
                    final ProgressDialog progressDialog1=new ProgressDialog(MainActivity.this);
                    progressDialog1.setMessage("Logging In!");
                    progressDialog1.show();
                    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            if(user!=null && e==null){
                                progressDialog1.dismiss();
                                FancyToast.makeText(MainActivity.this,"Logged In Successfully!",FancyToast.SUCCESS,FancyToast.LENGTH_LONG,true).show();

                                transitiontopassengeractivity();
                            }
                        }
                    });
                }

            }
        });
        onetimelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverorpassenger.getText().toString().equals("Driver")||driverorpassenger.getText().toString().equals("Passenger")){
                    if(ParseUser.getCurrentUser()==null){
                        ParseAnonymousUtils.logIn(new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if(user !=null && e==null){
                                    FancyToast.makeText(MainActivity.this,"We have an anonymous user!",FancyToast.INFO,FancyToast.LENGTH_LONG,true).show();
                                    user.put("as",driverorpassenger.getText().toString());
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                transitiontopassengeractivity();
                                            }
                                        }
                                    });
                                }

                            }
                        });
                    }

                }
                else {
                    FancyToast.makeText(MainActivity.this,"Are you a Driver or Passenger?",FancyToast.WARNING,FancyToast.LENGTH_LONG,true).show();
                }
            }

        });

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusignuplogin,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.loginitem:
                if(state==State.SignUp){
                    state=State.Login;
                    item.setTitle("Sign Up");
                    signuplogin.setText("Log In");
                }
                else if (state==State.Login){
                    state=State.SignUp;
                    item.setTitle("Log In");
                    signuplogin.setText("Sign Up");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void transitiontopassengeractivity(){
        if (ParseUser.getCurrentUser()!=null){
            if(ParseUser.getCurrentUser().get("as").equals("Passenger")){
                Intent intent=new Intent(MainActivity.this,Passenger_Activity.class);
                startActivity(intent);
            }
        }

    }
}
