package com.example.carparkfinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carparkfinder.R;

public class PaymentFailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unsuccessful);

        Button tryAgain = findViewById(R.id.try_again);
        tryAgain.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentFailActivity.this, PaymentActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
