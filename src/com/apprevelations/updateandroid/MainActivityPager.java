package com.apprevelations.updateandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivityPager extends FragmentActivity implements OnClickListener 
		 {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	TextView skip;
	Button bt1,bt2,bt3,bt4;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainpager);
		bt1=(Button) findViewById(R.id.btn1);
    	bt2=(Button) findViewById(R.id.btn2);
    	bt3=(Button) findViewById(R.id.btn3);
    	skip=(TextView)findViewById(R.id.tskip);
		skip.setOnClickListener(this);
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
	
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				
				btnAction(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	 private void btnAction(int action){
	    	switch(action){
	    	  case 0: 
	    		  setButton(bt2,4,15,R.drawable.rounded_celll2); 
	    	      setButton(bt1,4,15,R.drawable.rounded_celll);
	    	      setButton(bt3,4,15,R.drawable.rounded_celll);
	    	    
	    	      break;
	    	 
	    	  case 1:  setButton(bt1,4,15,R.drawable.rounded_celll2);
	    	           setButton(bt2,4,15,R.drawable.rounded_celll); 
	    	           setButton(bt3,4,15,R.drawable.rounded_celll);
	    	           
	    	           break;
	    	           
	    	  case 2:  setButton(bt3,4,15,R.drawable.rounded_celll2);
	                   setButton(bt2,4,15,R.drawable.rounded_celll); 
	                   setButton(bt1,4,15,R.drawable.rounded_celll);
	                   
	                 
	    	  break;
	    	}
	   }
	 private void setButton(Button btn,int w,int h,int c){
		    	
		    	btn.setWidth(w);
		    	btn.setHeight(h);
		    	btn.setBackgroundResource(c);
		    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.tskip:
				   Intent imain=new Intent(MainActivityPager.this,com.apprevelations.updateandroid.MainActivity.class);
				   startActivity(imain);
				    break;
		}
	}
	

} 


