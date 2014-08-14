package com.apprevelations.updateandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;


public class Profile extends Activity implements OnClickListener{

	RelativeLayout rl1,rl2;
	LinearLayout mainlayout,expandlayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_xml);
		mainlayout= (LinearLayout) findViewById(R.id.llmainlayout);
		rl1=(RelativeLayout) findViewById(R.id.rlprofile1);
		rl2=(RelativeLayout) findViewById(R.id.rlprofile2);
		expandlayout=(LinearLayout) findViewById(R.id.llexpand);
		rl1.setOnClickListener(this);
		rl2.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	//	final Animation fadein = AnimationUtils.loadAnimation(Profile.this, R.anim.fadein);
	//	final Animation fadeout = AnimationUtils.loadAnimation(Profile.this, R.anim.fadeout);
		
		switch(v.getId())
		{
		case R.id.rlprofile1:
		//	rl1.setAnimation(fadeout);
			rl1.setVisibility(View.GONE);
		//	expandlayout.setAnimation(fadein);
			expandlayout.setVisibility(View.VISIBLE);
			expandlayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub


			//		expandlayout.setAnimation(fadeout);
					expandlayout.setVisibility(View.GONE);
			//		rl1.setAnimation(fadein);
					rl1.setVisibility(View.VISIBLE);
				}
			});
			break;
			
		case R.id.rlprofile2:
			
			break;
		}
		
	}
	
   /* void createdymanic(int a)
	{
		rl= new RelativeLayout(Profile.this);
		rl.setBackgroundColor(Profile.this.getResources().getColor(R.color.background));

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

        rlp.setMargins(15, 15, 15, 15);
        //RelativeLayout.LayoutParams ivprofile= new RelativeLayout.LayoutParams(35, 50);
        RelativeLayout.LayoutParams ivprofile = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams textname = new RelativeLayout.LayoutParams(
        				RelativeLayout.LayoutParams.MATCH_PARENT,
        					RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams textdate = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams textbody = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams imagelike = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams imagecomment = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams imageshare = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams editcomment = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams linearlayoutparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams scrolled = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        


        // set picture of the person
        ivprofilepic= new ImageView(getActivity());
        ivprofile.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ivprofile.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ivprofile.setMargins(5,5,0,0);
        ivprofilepic.setId(1+a);
        ivprofilepic.setImageResource(R.drawable.ic_launcher);



        tvname= new TextView(getActivity());
        textname.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        textname.addRule(RelativeLayout.RIGHT_OF, ivprofilepic.getId());
        textname.setMargins(8, 5, 0, 0);
        tvname.setId(2+a);
        tvname.setText("Name");
        tvname.setTextColor(getResources().getColor(R.color.textcolor));
        tvname.setTextSize(18);

        tvdate= new TextView(getActivity());
        textdate.addRule(RelativeLayout.ALIGN_BOTTOM, ivprofilepic.getId());
        textdate.addRule(RelativeLayout.ALIGN_LEFT, tvname.getId());
        textdate.addRule(RelativeLayout.BELOW, tvname.getId());
        tvdate.setId(3+a);
        tvname.setText("Date");


        tvcontent= new TextView(getActivity());
        textbody.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textbody.addRule(RelativeLayout.BELOW, ivprofilepic.getId());
        textbody.setMargins(5,0,0,0);
        tvcontent.setId(4+a);
        tvcontent.setText("Content");


		iblike= new Button(getActivity());

	    imagelike.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    imagelike.addRule(RelativeLayout.BELOW, tvcontent.getId());
	    imagelike.setMargins(5, 7, 0, 0);
	    iblike.setId(5+a);
        iblike.setText("like");


		ibcomment= new Button(getActivity());
	    imagecomment.addRule(RelativeLayout.ALIGN_TOP, iblike.getId());
	    imagecomment.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    ibcomment.setId(6+a);
	    ibcomment.setText("comment");


		ibshare= new Button(getActivity());
	    imageshare.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    imageshare.addRule(RelativeLayout.ALIGN_TOP, iblike.getId());
	    imageshare.setMargins(0,0,0,5);
	    ibshare.setId(7+a);
	    ibshare.setText("share");

		etcommentbody= new EditText(getActivity());
	    editcomment.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    editcomment.addRule(RelativeLayout.BELOW, iblike.getId());
	    editcomment.setMargins(5,8,0,0);
	    etcommentbody.setId(6+a);
		etcommentbody.setHint("Add a comment...");
		etcommentbody.setTextColor(getResources().getColor(R.color.textcolorblack));
		//ab[6+a] = etcommentbody.getText().toString();
		
		commentlayout= new LinearLayout(getActivity());
		linearlayoutparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		linearlayoutparams.addRule(RelativeLayout.BELOW, etcommentbody.getId());
		linearlayoutparams.setMargins(5, 5, 0, 0);
		commentlayout.setId(9+a);

		//scroll= new ScrollView(getActivity());
		//scrolled.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		//scrolled.addRule(RelativeLayout.BELOW, etcommentbody.getId());
		//scrolled.setMargins(5, 5, 0, 0);
		//scroll.setId(9+a);
		

	    rl.addView(ivprofilepic, ivprofile);
	    rl.addView(tvname, textname);
	    rl.addView(tvdate ,textdate);
	    rl.addView(tvcontent, textbody);
	    rl.addView(iblike, imagelike);
	    rl.addView(ibcomment, imagecomment);
	    rl.addView(ibshare, imageshare);
	    rl.addView(etcommentbody, editcomment);
        //scroll.addView(commentlayout, linearlayoutparams);
        rl.addView(commentlayout, linearlayoutparams);
	    mainlayout.addView(rl, 0, rlp);


	}
*/
}
