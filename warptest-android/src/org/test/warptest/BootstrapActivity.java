package org.test.warptest;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class BootstrapActivity extends Activity {
	private TextView mText;
	private ScrollView mScroll;
	private StringBuilder mScrollContent = new StringBuilder();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mText = (TextView) findViewById(R.id.text);
        mScroll = (ScrollView) findViewById(R.id.scroll);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		String assetName = "moorwen_textured.jpg";
		try {
			InputStream is = getAssetInputStream(assetName);
			log("got input stream for asset: " + assetName);
		} catch (IOException e) {
			log("failed to get input stream for asset: " + assetName);
		}
	}

	private InputStream getAssetInputStream(String name) throws IOException {
    	InputStream is = this.getAssets().open(name);
    	return is;
    }
    
    private void log(String msg) {
    	mScrollContent.append(msg);
    	mScrollContent.append("\n");
    	mText.setText(mScrollContent.toString());
    }
}