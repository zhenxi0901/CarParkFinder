package com.example.carparkfinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carparkfinder.R;

import java.util.Random;

public class PaymentActivity extends AppCompatActivity {
    private EditText cardNumber, expiryDate, cvv;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize UI Components
        cardNumber = findViewById(R.id.editCardNumber);
        expiryDate = findViewById(R.id.editExpiryDate);
        cvv = findViewById(R.id.editCvv);
        payButton = findViewById(R.id.btnPay);

        // Payment Button Click
        payButton.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        String cardNum = cardNumber.getText().toString().trim();
        String expiry = expiryDate.getText().toString().trim();
        String cvvCode = cvv.getText().toString().trim();

        // Basic Validation
        if (cardNum.isEmpty() || expiry.isEmpty() || cvvCode.isEmpty()) {
            Toast.makeText(this, "Please fill all card details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate Payment Success (Randomly decide success/fail)
        boolean paymentSuccess = new Random().nextBoolean();

        if (paymentSuccess) {
            Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
            intent.putExtra("TRANSACTION_ID", "TXN" + System.currentTimeMillis());
            intent.putExtra("AMOUNT", "$3.00");
            startActivity(intent);
        } else {
            Intent intent = new Intent(PaymentActivity.this, PaymentFailActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
