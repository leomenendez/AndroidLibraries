package com.electapps.fontviewsdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.electapps.fontviews.FontTextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SpinnerAdapter adapter;

    private String selectedFont;
    private String[] fontsArray;

    private Spinner spinner;
    private TextView textView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFont = getString(R.string.font_default);
        fontsArray = getResources().getStringArray(R.array.font_array);

        initViews();

    }

    private void initViews(){
        int layout = getResources().getIdentifier("activity_main_"+selectedFont.replace(" ", "").toLowerCase(), "layout", getPackageName());
        setContentView(layout);

        spinner = (Spinner) findViewById(R.id.font_selector);
        textView = (TextView) findViewById(R.id.text_view_example);
        editText = (EditText) findViewById(R.id.edit_text_example);
        button = (Button) findViewById(R.id.button_example);

        textView.setText(String.format(getString(R.string.text_view_example), selectedFont));
        editText.setText(String.format(getString(R.string.edit_text_example), selectedFont));
        button.setText(String.format(getString(R.string.button_example), selectedFont));


        spinner.setOnItemSelectedListener(null);
        setSpinnerAdapter();

        spinner.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fontsArray.length; i++) {
                    if (selectedFont.equalsIgnoreCase(fontsArray[i])) {
                        spinner.setSelection(i, false);
                        break;
                    }
                }
                spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setOnItemSelectedListener(MainActivity.this);

                    }
                });
            }
        });


    }

    private void setSpinnerAdapter(){
        adapter = new SpinnerAdapter(this, fontsArray);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedFont = fontsArray[position];
        initViews();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class SpinnerAdapter extends BaseAdapter {
        private Context context;
        private String[] fonts;
        private LayoutInflater inflater;

        private String[] fontNames;

        public SpinnerAdapter(Context context, String[] fonts) {
            this.context = context;
            this.fonts = fonts;
            inflater = LayoutInflater.from(context);

            fontNames = context.getResources().getStringArray(R.array.font_names);
        }

        @Override
        public int getCount() {
            return fonts.length;
        }

        @Override
        public Object getItem(int position) {
            return fonts[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_font, parent, false);
            }

            FontTextView textView = (FontTextView) convertView.findViewById(R.id.font_choice);
            textView.setText(fonts[position]);
            if(position>0)
                textView.setCustomFont(context, fontNames[position]);

            return convertView;
        }


    }

}
