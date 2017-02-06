package com.helooleh.mailsender;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private String subject, body, sender, recipients;
    EditText etBody, etMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.main);
        etBody = (EditText) findViewById(R.id.etBody);
        etMail = (EditText) findViewById(R.id.etMail);
        etBody.setOnFocusChangeListener(this);
        etMail.setOnFocusChangeListener(this);

        (findViewById(R.id.btEmailSend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkExist(MainActivity.this)){
                    Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                    return;
                }

                subject = etMail.getText().toString();
                body = getResources().getString(R.string.feedback_text)
                        + ":   " + etBody.getText().toString()
                        + "\n" + getResources().getString(R.string.feedback_email)
                        + ":   " + subject;
                sender = subject;
                recipients = subject;

                MainActivity.SenderMailAsync async_sending = new MainActivity.SenderMailAsync();
                async_sending.execute();
            }
        });
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        fillDefaultValues(v, hasFocus);
    }

    private void fillDefaultValues(View v, boolean hasFocus) {
        String defaultValue = "";
        switch (v.getId()) {
            case R.id.etBody:
                defaultValue = getResources().getString(R.string.feedback_text);
                break;
            case R.id.etMail:
                defaultValue = getResources().getString(R.string.feedback_email);
        }
        if (hasFocus) {
            if (((EditText) v).getText().toString().equals(defaultValue)) {
                ((EditText) v).setText("");
            }
        } else {
            if (((EditText) v).getText().toString().equals("")) {
                ((EditText) v).setText(defaultValue);
            }
        }
    }

    private class SenderMailAsync extends AsyncTask<Object, String, Boolean> {
        ProgressDialog WaitingDialog;

        @Override
        protected void onPreExecute() {
            WaitingDialog = ProgressDialog.show(MainActivity.this,
                    getResources().getString(R.string.feedback_sending),
                    getResources().getString(R.string.sending_message), true);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            WaitingDialog.dismiss();
            Toast.makeText(MainActivity.this, R.string.feedback_sent, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                // Write your email and password
                MailSenderClass mailSenderClass = new MailSenderClass("YourEmail@gmail.com", "YourPassword");
                mailSenderClass.sendMail(subject, body, sender, recipients, "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
    public static boolean isNetworkExist (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return  true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}
