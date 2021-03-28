package ba.affanzukic.autoweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Search extends AppCompatActivity {

    EditText cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cityName = (EditText) findViewById(R.id.cityInput);
    }

    public void update(View view)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("cityname", cityName.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}