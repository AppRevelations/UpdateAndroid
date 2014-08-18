package com.apprevelations.updateandroid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment implements OnClickListener{
	
	RelativeLayout rl1,rl2,rl;
	int a=0;
	LinearLayout mainlayout,expandlayout,horizontalline,verticalline;
	TextView profile, version,addprofile;
	ImageView apply, delete,remove;
	
	public ProfileFragment(){
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		mainlayout= (LinearLayout) rootView.findViewById(R.id.llmainlayout);
		rl1=(RelativeLayout) rootView.findViewById(R.id.rlprofile1);
		addprofile=(TextView) rootView.findViewById(R.id.tvaddprofile);
		expandlayout=(LinearLayout) rootView.findViewById(R.id.llexpand);
		rl1.setOnClickListener(this);
		addprofile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			createdymanic(a++);	
			}
		});
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	//	final Animation fadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
	//	final Animation fadeout = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
		
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
		
		}
		
	}
	
	void createdymanic(int a) {
		rl= new RelativeLayout(getActivity());
		rl.setBackgroundColor(getActivity().getResources().getColor(R.color.background));

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

        rlp.setMargins(15, 15, 15, 15);
        RelativeLayout.LayoutParams hline = new RelativeLayout.LayoutParams(220,1);
        RelativeLayout.LayoutParams vline = new RelativeLayout.LayoutParams(1,50);
        RelativeLayout.LayoutParams profilenumber = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams versionpram = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams imageapply = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams imageremove = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

	        


        // set picture of the person
        horizontalline= new LinearLayout(getActivity());
        hline.addRule(RelativeLayout.CENTER_VERTICAL);
        hline.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        hline.setMargins(18,0,0,0);
        horizontalline.setId(1+a);
        horizontalline.setOrientation(LinearLayout.HORIZONTAL);
        horizontalline.setBackgroundColor(getActivity().getResources().getColor(R.color.backline));



        remove= new ImageView(getActivity());
        imageremove.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageremove.setMargins(0,0,15,0);
        remove.setId(3+a);
        remove.setBackgroundResource(R.drawable.ic_action_remove);
        
	        
        verticalline= new LinearLayout(getActivity());
        vline.addRule(RelativeLayout.CENTER_VERTICAL);
        vline.addRule(RelativeLayout.LEFT_OF,remove.getId());
        vline.setMargins(0,0,14,0);
        verticalline.setOrientation(LinearLayout.VERTICAL);
        verticalline.setId(5+a);
        verticalline.setBackgroundColor(getActivity().getResources().getColor(R.color.backline));
        

        imageremove.addRule(RelativeLayout.BELOW,verticalline.getId());
        
        profile= new TextView(getActivity());
        profilenumber.addRule(RelativeLayout.ALIGN_LEFT,horizontalline.getId());
        profilenumber.addRule(RelativeLayout.ALIGN_TOP,verticalline.getId());
        profile.setId(2+a);
        String name="Profile" +a;
        profile.setText(name);
        profile.setTextColor(Color.rgb(11, 33, 97));
        


	        

        apply= new ImageView(getActivity());
        imageapply.addRule(RelativeLayout.ABOVE,verticalline.getId());
        imageapply.addRule(RelativeLayout.ALIGN_LEFT,remove.getId());
        apply.setId(4+a);
        apply.setBackgroundResource(R.drawable.ic_action_accept);

	        

        version= new TextView(getActivity());
        profilenumber.addRule(RelativeLayout.ALIGN_LEFT,horizontalline.getId());
        profilenumber.addRule(RelativeLayout.ALIGN_BOTTOM,remove.getId());
        profile.setId(6+a);
        profile.setText("4.4.2");
        profile.setTextColor(Color.rgb(11, 33, 97));

	        
	    rl.addView(horizontalline,hline);
	    rl.addView(profile,profilenumber);
	    rl.addView(remove,imageremove);
	    rl.addView(apply,imageapply);
	    rl.addView(verticalline,vline);
	    rl.addView(version,versionpram);
	    mainlayout.addView(rl, 1, rlp);


	}

}
