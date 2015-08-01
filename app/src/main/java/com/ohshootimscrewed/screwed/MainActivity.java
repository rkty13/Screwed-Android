package com.ohshootimscrewed.screwed;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by robertkim on 8/1/15.
 */
public class MainActivity extends Activity {

    private TextView mSearchInput;
    private Button mSearchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }
}
