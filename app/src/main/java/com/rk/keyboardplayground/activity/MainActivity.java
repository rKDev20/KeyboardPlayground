package com.rk.keyboardplayground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rk.keyboardplayground.R;
import com.touchtalent.bobblekeyboard.dynamicsupport.BobbleImeSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button install;
    EditText tryOut;
    ProgressBar progressBar;
    TextView progress;
    int status;
    Runnable checkEnabled;
    Handler checkEnabledHandler;
    private boolean isVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bobble);
        install = findViewById(R.id.install);
        install.setOnClickListener(this);
        tryOut = findViewById(R.id.tryOut);
        progress = findViewById(R.id.progress);
        progressBar = findViewById(R.id.progressBar);
        checkEnabled = () -> {
            int status = BobbleImeSDK.getStatus(this);
            if (this.status != status) {
                checkEnabledHandler.removeCallbacks(checkEnabled);
                if (isVisible)
                    setStatus();
                else {
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            } else checkEnabledHandler.postDelayed(checkEnabled, 300);
        };
        checkEnabledHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        setStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    public void setStatus() {
        status = BobbleImeSDK.getStatus(this);
        progressBar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        install.setFocusable(true);
        install.setClickable(true);
        switch (status) {
            case BobbleImeSDK.NOT_INSTALLED:
                install.setText("Install Bobble Keyboard");
                install.setVisibility(View.VISIBLE);
                tryOut.setVisibility(View.GONE);
                break;
            case BobbleImeSDK.DISABLED:
                install.setText("Enable Keyboard Playground(Step-1)");
                install.setVisibility(View.VISIBLE);
                tryOut.setVisibility(View.GONE);
                break;
            case BobbleImeSDK.ENABLED:
                install.setText("Enable Keyboard Playground(Step-2)");
                install.setVisibility(View.VISIBLE);
                tryOut.setVisibility(View.GONE);
                break;
            case BobbleImeSDK.ACTIVE:
                install.setVisibility(View.GONE);
                tryOut.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (status) {
            case BobbleImeSDK.NOT_INSTALLED:
                install();
                break;
            case BobbleImeSDK.DISABLED:
                BobbleImeSDK.launchEnableKeyboardPage(this);
                checkEnabledHandler.postDelayed(checkEnabled, 300);
                break;
            case BobbleImeSDK.ENABLED:
                BobbleImeSDK.showIMEChooser(this);
                checkEnabledHandler.postDelayed(checkEnabled, 300);
                break;
        }
    }

    private String getSize(long current, long total) {
        return String.format("%.2f MB", current / 1024f / 1024f) + "/" + String.format("%.2f MB", total / 1024f / 1024f);
    }

    private void install() {
        install.setText("Installing...");
        install.setFocusable(false);
        install.setClickable(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        if (status == BobbleImeSDK.NOT_INSTALLED) {
            BobbleImeSDK.install(this, new BobbleImeSDK.InstallCompleteListener() {
                long downloadSize = 0;
                long currentlyDownloaded = 0;

                @Override
                public void onDownloadStarted(long downloadSize) {
                    this.downloadSize = downloadSize;
                    progressBar.setIndeterminate(false);
                    progress.setVisibility(View.VISIBLE);
                    progress.setText(getSize(0, downloadSize));
                    progressBar.setMax((int) downloadSize);
                }

                @Override
                public void onProgress(long downloaded) {
                    this.currentlyDownloaded = downloaded;
                    progressBar.setProgress((int) downloaded);
                    progress.setText(getSize(downloaded, downloadSize));
                }

                @Override
                public void onFailure() {
                    setStatus();
                    Toast.makeText(MainActivity.this, "Failed to download keyboard", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess() {
                    setStatus();
                    Toast.makeText(MainActivity.this, "Keyboard downloaded successfuly", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
