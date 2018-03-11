package com.example.krizianidj.capstone1;


        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Patterns;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.github.nkzawa.socketio.client.IO;
        import com.github.nkzawa.socketio.client.Socket;
        import com.github.nkzawa.socketio.client.SocketIOException;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseAuthUserCollisionException;
        import com.google.firebase.auth.FirebaseUser;

        import java.io.IOException;
        import java.net.SocketException;
        import java.net.URISyntaxException;

public class Registration2 extends AppCompatActivity {
    Socket socket;
    EditText RaspAdd, DeviceName;
    Button Register;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        RaspAdd=(EditText) findViewById(R.id.editTextAddressPi);
        DeviceName=(EditText) findViewById(R.id.editTextDeviceName);
        progressBar=(ProgressBar) findViewById(R.id.progressbar);
        Register=(Button) findViewById(R.id.buttonSave);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterDevice();


            }
        });


    }

    void RegisterDevice() {
        final String raspadd = RaspAdd.getText().toString().trim();
        final String devicename = DeviceName.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (raspadd.isEmpty()) {
            RaspAdd.setError("A raspberry Pi is required");
            RaspAdd.requestFocus();
            return;
        }

        if (devicename.isEmpty()) {
            DeviceName.setError("A Device name is required");
            DeviceName.requestFocus();
            return;
        }

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();
        String monitorid = "monitor" + id;
        try {
            //server url
            server=new Server();
            socket = IO.socket(server.getAddress());

        } catch (URISyntaxException e) {
        }

        socket.connect();


        socket.emit("RegDevice", monitorid, raspadd, devicename, id);
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        finish();
    }




}