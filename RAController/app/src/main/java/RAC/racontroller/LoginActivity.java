package RAC.racontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import RAC.racontroller.API.APIClient;

public class LoginActivity extends AppCompatActivity {
    private EditText txtIP;
    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtIP = findViewById(R.id.txtIP);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        loadSession();
    }

    public void btnLogin_onClick(View view) {
        String IP = txtIP.getText().toString();
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String address = (IP.startsWith("http://")) ? IP : "http://" + IP;

        if (!validateIP(IP)) {
            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        } else if (username.length() < 1) {
            Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_LONG).show();
        } else if (password.length() < 1) {
            Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
        } else if (APIClient.login(address + "/api", username, password)) {
            saveSession(IP, username, password);

            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            APIClient.getInstance().setIP(IP);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
        }
    }

    private void saveSession(String IP, String username, String password) {
        SharedPreferences session = getApplicationContext().getSharedPreferences("Session", 0);
        SharedPreferences.Editor editor = session.edit();
        editor.putString("IP", IP);
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.apply();
    }

    private void loadSession() {
        SharedPreferences session = getApplicationContext().getSharedPreferences("Session", 0);
        txtIP.setText(session.getString("IP", ""));
        txtUsername.setText(session.getString("Username", ""));
        txtPassword.setText(session.getString("Password", ""));
    }

    private boolean validateIP(String ip) {
        String PATTERN = "(?:[0-9]{1,3}[.]){3}[0-9]{1,3}";
        return ip.matches(PATTERN);
    }
}
