package com.example.authentication;




import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText aadhaar;
	Button next;
	ArrayList<String> uuid = new ArrayList<String>();
	ArrayList<String> name = new ArrayList<String>();
	ArrayList<String> status = new ArrayList<String>(); 
	int pos=-2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        aadhaar= (EditText) findViewById(R.id.editText1);
        next=(Button)findViewById(R.id.button1);
        
        next.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
			    	final String aadhar=aadhaar.getEditableText().toString();
			    	Integer length=aadhar.length();
			    	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(v.getContext());
			    
			    	if(length.intValue() == 0 || length.intValue()<2)
			    	{
			    	   dlgAlert.setMessage("Please enter correct Aadhaar Number!");					    
			           dlgAlert.setPositiveButton("OK", null);
			           dlgAlert.setCancelable(true);       
			           dlgAlert.create().show();
			    	}
			    	else
			    	{
			    		   
				           
				        
	            		Intent intent = new Intent(getApplicationContext(),NewAadhar.class);
	            		
						 startActivity(intent);
	            		
			    		
	            	
	            	
			
			}
			}
			
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
}
