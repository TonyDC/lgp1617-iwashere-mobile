package com.teamc.mira.iwashere.presentation.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teamc.mira.iwashere.R;



public class Photo_video extends Activity{

    ImageButton photoButton, videoButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_video);
        photoButton = (ImageButton) this.findViewById(R.id.photoBtn);
        videoButton = (ImageButton) this.findViewById(R.id.videoBtn);

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Photo_video.this, CameraInit.class);
                Bundle b = new Bundle();
                b.putString("key","photo");
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Photo_video.this, CameraInit.class);
                Bundle b = new Bundle();
                b.putString("key","video");
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();            }
        });
    }
}
