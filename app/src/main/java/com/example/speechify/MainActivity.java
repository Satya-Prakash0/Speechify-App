package com.example.speechify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner,toSpinner;
    private TextInputEditText sourcetext;
    private ImageView mic;
    private Button btn_Translate;
    private TextView translated_text;
    String[] fromLanguage={"From","English","Africans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Belch","Hindi","Urdu"};
    String[] ToLanguage={"Too","English","Africans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Belch","Hindi","Urdu"};
    int languagecode,fromLanguageCode,toLanguageCode=0;
    final int REQUEST_PERMISSION_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner=findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        sourcetext=findViewById(R.id.idEditSource);
        mic=findViewById(R.id.mic);
        translated_text=findViewById(R.id.translated_text);
        btn_Translate=findViewById(R.id.button_translate);
        permissions();

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode=getLanguageCode(fromLanguage[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter=new ArrayAdapter(this,R.layout.spinner_item,fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(ToLanguage[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter=new ArrayAdapter(this,R.layout.spinner_item,ToLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        btn_Translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translated_text.setText("");
                if(sourcetext.getText().toString().isEmpty())
                    Toast.makeText(MainActivity.this, "Nothing to show!!", Toast.LENGTH_SHORT).show();
                else if(fromLanguageCode==0)
                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                else if(toLanguageCode==0)
                    Toast.makeText(MainActivity.this, "Please select the language for the translation", Toast.LENGTH_SHORT).show();
                else{
                    translateText(fromLanguageCode,toLanguageCode,sourcetext.getText().toString());
                }
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"SPeak to convert into text");
                try {
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE);
                }catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourcetext.setText(result.get(0));
            }
        }
    }

    private void permissions(){
     Dexter.withContext(this)
             .withPermissions(permission.RECORD_AUDIO,
                     permission.WRITE_EXTERNAL_STORAGE,
                     permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener(){
         @Override
         public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
          if(!multiplePermissionsReport.areAllPermissionsGranted())
              Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
             permissionToken.continuePermissionRequest();
         }
     }).check();
 }
 private int getLanguageCode(String language){
        int languageCode=0;
        switch (language){
            //"Bulgarian","Bengali","Catalan","Czech","Belch"
            case "English" : languageCode= FirebaseTranslateLanguage.EN;break;
            case "Africans" : languageCode= FirebaseTranslateLanguage.AF;break;
            case "Arabic" : languageCode= FirebaseTranslateLanguage.AR;break;
            case "Belarusian" : languageCode= FirebaseTranslateLanguage.BE;break;
            case "Bulgarian" : languageCode= FirebaseTranslateLanguage.BG;break;
            case "Bengali" : languageCode= FirebaseTranslateLanguage.BN;break;
            case "Catalan" : languageCode= FirebaseTranslateLanguage.CA;break;
            case "Czech" : languageCode= FirebaseTranslateLanguage.CS;break;
            case "Hindi" : languageCode= FirebaseTranslateLanguage.HI;break;
            case "Urdu" : languageCode= FirebaseTranslateLanguage.UR;break;
            default: languageCode=0;
        }
     return languageCode;
 }

 private void translateText(int fromLanguageCode,int toLanguageCode,String source){
    translated_text.setText("Translating...");
    FirebaseTranslatorOptions option=new FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLanguageCode)
            .setTargetLanguage(toLanguageCode)
            .build();

     FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance().getTranslator(option);
     FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
     translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
             translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                 @Override
                 public void onSuccess(String s) {
                     translated_text.setText(s);
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Toast.makeText(MainActivity.this, "Failed to translate "+e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             });
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(MainActivity.this, "Failed to download the model"+e.getMessage(), Toast.LENGTH_SHORT).show();
         }
     });
 }

}