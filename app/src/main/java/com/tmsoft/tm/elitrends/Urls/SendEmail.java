package com.tmsoft.tm.elitrends.Urls;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tmsoft.tm.elitrends.R;

public class SendEmail {
    private String[] emailAddress;
    private String title, message;
    private Context context;
    private View view;

    public SendEmail(String[] emailAddress, String title, String message, Context context, View view) {
        this.emailAddress = emailAddress;
        this.title = title;
        this.message = message;
        this.context = context;
        this.view = view;
    }

    public void sendEmail(){
        Log.i("email", "Start");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("email", "Email sent");
        } catch (android.content.ActivityNotFoundException ex) {
            // Inform the user that no email clients are installed or provide an alternative
            Toast.makeText(context, "No email clients are installed or provide an alternative", Toast.LENGTH_SHORT).show();
        }
    }
}
