package com.ga_gcs.logintest.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class GAActivity extends AppCompatActivity {

    private View parentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = findViewById(android.R.id.content);
    }

    public View getParentView() {
        return parentView;
    }

    public void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    public String getTILText(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString().trim();
    }

    public void setTILText(TextInputLayout textInputLayout, String text) {
        textInputLayout.getEditText().setText(text);
    }

    public void showSnackBar(String message) {
        Snackbar.make(getParentView(), message, Snackbar.LENGTH_LONG).show();
    }

    public void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
