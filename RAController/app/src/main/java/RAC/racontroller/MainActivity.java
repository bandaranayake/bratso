package RAC.racontroller;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import RAC.racontroller.API.APIClient;

public class MainActivity extends AppCompatActivity {
    private DiscreteSeekBar sbClaw;
    private DiscreteSeekBar sbElbow;
    private DiscreteSeekBar sbBase;
    private DiscreteSeekBar sbDelay;

    private Button btnRecord;
    private Button btnRun;

    private boolean pressedOnce = false;
    private boolean recording = false;

    private LinkedList<JSONObject> steps = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbClaw = findViewById(R.id.sbClaw);
        sbElbow = findViewById(R.id.sbElbow);
        sbBase = findViewById(R.id.sbBase);
        sbDelay = findViewById(R.id.sbDelay);

        btnRecord = findViewById(R.id.btnRecord);
        btnRun = findViewById(R.id.btnRun);

        TextView lblIP = findViewById(R.id.lblIPAddress);
        lblIP.setText("IP Address: ".concat(APIClient.getInstance().getIP()));

        initSeekBars();
    }

    private void initSeekBars() {
        sbClaw.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            int progressValue = 0;

            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
            }

            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (!APIClient.postData(getSeekBarValues())) {
                    Toast.makeText(MainActivity.this, "Something went wrong. Check the connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sbElbow.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            int progressValue = 0;

            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
            }

            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (!APIClient.postData(getSeekBarValues())) {
                    Toast.makeText(MainActivity.this, "Something went wrong. Check the connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sbBase.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            int progressValue = 0;

            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
            }

            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (!APIClient.postData(getSeekBarValues())){
                    Toast.makeText(MainActivity.this, "Something went wrong. Check the connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private JSONObject getSeekBarValues(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Claw", sbClaw.getProgress());
            jsonObject.put("Elbow", sbElbow.getProgress());
            jsonObject.put("Base", sbBase.getProgress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String getJSONArray(){
        String arr = "";
        for(JSONObject obj : steps){
            arr += obj.toString() + "\n";
        }
        return arr;
    }

    public void btnRecord_onClick(View view) {
        if (!recording) {
            recording = true;
            initSteps();
            setButtons();
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Claw", sbClaw.getProgress());
                jsonObject.put("Elbow", sbElbow.getProgress());
                jsonObject.put("Base", sbBase.getProgress());
                jsonObject.put("Delay", sbDelay.getProgress()*1000);

                steps.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            btnRecord.setText("Steps:" + steps.size());
        }
    }

    public void btnReset_onClick(View view) {
        recording = false;
        initSteps();
        resetButtons();
    }

    public void btnRun_onClick(View view) {
        if (recording) {
            recording = false;
            resetButtons();
        } else {
            if (steps.size() < 1) {
                Toast.makeText(MainActivity.this, "No steps recorded.", Toast.LENGTH_SHORT).show();
            } else {
                if (!APIClient.postData(getJSONArray())) {
                    Toast.makeText(MainActivity.this, "Something went wrong. Check the connection.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initSteps(){
        steps = new LinkedList<>();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Claw", 0);
            jsonObject.put("Elbow", 0);
            jsonObject.put("Base", 0);
            jsonObject.put("Delay", 20);
            steps.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setButtons() {
        btnRecord.setText("Steps:" + steps.size());
        btnRun.setText("Stop");
        btnRecord.getBackground().setColorFilter(Color.parseColor("#851d41"), PorterDuff.Mode.MULTIPLY);
        btnRun.getBackground().setColorFilter(Color.parseColor("#851d41"), PorterDuff.Mode.MULTIPLY);
    }

    private void resetButtons() {
        btnRecord.setText("Record");
        btnRun.setText("Run");
        btnRecord.getBackground().setColorFilter(Color.parseColor("#4F98CA"), PorterDuff.Mode.MULTIPLY);
        btnRun.getBackground().setColorFilter(Color.parseColor("#4F98CA"), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onBackPressed() {
        if (pressedOnce) {
            super.onBackPressed();
            return;
        }

        this.pressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                pressedOnce = false;
            }
        }, 2000);
    }
}
