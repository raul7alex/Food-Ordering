package com.progandroid.foodordering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.Helper;

public class NewPaymentActivity extends AppCompatActivity {

    private static final String TAG = NewAddressActivity.class.getSimpleName();

    private EditText cardNumber, expirationDate, cvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);

        setTitle(getString(R.string.add_new_card_title));

        cardNumber = (EditText)findViewById(R.id.card_number);
        expirationDate = (EditText)findViewById(R.id.expire_date);
        cvc = (EditText)findViewById(R.id.cvc);

        Button saveNewCard = (Button)findViewById(R.id.save_new_card);
        saveNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cardNumberInput = cardNumber.getText().toString();
                String expirationDateInput = expirationDate.getText().toString();
                String cvcInput = cvc.getText().toString();

                if(TextUtils.isEmpty(cardNumberInput) || TextUtils.isEmpty(expirationDateInput) || TextUtils.isEmpty(cvcInput)){
                    Helper.displayErrorMessage(NewPaymentActivity.this, "All input fields must be filled");
                }
                String cardInformation = cardNumberInput + "," + expirationDateInput + "," + cvcInput;
                ((CustomApplication)getApplication()).getShared().saveCreditCardDetails(cardInformation);
                cardNumber.setText("");
                expirationDate.setText("");
                cvc.setText("");
            }
        });
    }
}
