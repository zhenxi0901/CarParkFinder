package com.example.carparkfinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carparkfinder.R;
import com.example.carparkfinder.MainActivity; // ✅ Import MainActivity (written in Kotlin)

public class PaymentSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.successful);

        TextView transactionNumber = findViewById(R.id.transaction_number);
        TextView amountPaid = findViewById(R.id.amount);
        Button homeButton = findViewById(R.id.home_button);

        // Retrieve Data
        String txnId = getIntent().getStringExtra("TRANSACTION_ID");
        String amount = getIntent().getStringExtra("AMOUNT");

        // Display Data
        transactionNumber.setText("Transaction Number: " + txnId);
        amountPaid.setText("Amount Paid: " + amount);

        // Return to Home
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
