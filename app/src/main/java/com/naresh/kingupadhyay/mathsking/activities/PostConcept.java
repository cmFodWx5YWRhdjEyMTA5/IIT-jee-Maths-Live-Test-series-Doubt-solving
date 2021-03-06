
package com.naresh.kingupadhyay.mathsking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.captue.ImageCroper;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PostConcept extends AppCompatActivity {

    private String book;
    private String chapter;
    private AdView mAdView;

    private FirebaseFirestore db;
    private List<Link> links;
    private LinkableEditText editTextPostTags;
    private LinkableEditText editTextPostQuestion;
    private LinkableEditText editTextPosttopicnum;
    private LinkableEditText editTextPosttime;
    private String currentUserId;
    private boolean imageset;
    private int attachNumber=0;
    private int topicNum=0;
    private Timestamp uploadingTime=  new Timestamp(new Date());
    private String documentId;
    private String tab;
    private String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_concept);

        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","user");

        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter","chapter");
        String tags=bundle.getString("tags","");
        final String text=bundle.getString("text","");
        tab = bundle.getString("tab","concepts");
        documentId=bundle.getString("documentId","");
        time=bundle.getString("time","");
        topicNum = bundle.getInt("topicNum",0);
        db= FirebaseFirestore.getInstance();
        editTextPostTags = findViewById(R.id.tagsvalLinkableEdit);
        editTextPostQuestion = findViewById(R.id.questionvalLinkableEdit);
        editTextPosttopicnum = findViewById(R.id.topicnumLinkableEdit);
        editTextPosttime = findViewById(R.id.timeLinkableEdit);

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.blue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(PostConcept.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });

        links = new ArrayList<>();
        links.add(linkUsername);
        editTextPostTags.addLinks(links);
        editTextPostTags.setText(tags,TextView.BufferType.EDITABLE);
        editTextPostQuestion.addLinks(links);
        editTextPostQuestion.setText(text,TextView.BufferType.EDITABLE);
        editTextPosttime.setText(time);

        //tags
        editTextPostTags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostTags.getText().toString().isEmpty() ||editTextPostTags.getText().toString()==null)
                    editTextPostTags.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostTags.setPressed(true);
                    editTextPostTags.setSelection(editTextPostTags.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        //question
        editTextPostQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostQuestion.getText().toString().isEmpty() ||editTextPostQuestion.getText().toString()==null)
                    editTextPostQuestion.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostQuestion.setPressed(true);
                    editTextPostQuestion.setSelection(editTextPostQuestion.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });
        //topicnum
        editTextPosttopicnum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPosttopicnum.getText().toString().isEmpty() ||editTextPosttopicnum.getText().toString()==null)
                    editTextPosttopicnum.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPosttopicnum.setPressed(true);
                    editTextPosttopicnum.setSelection(editTextPosttopicnum.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });
        //time
        editTextPosttime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPosttime.getText().toString().isEmpty() ||editTextPosttime.getText().toString()==null)
                    editTextPosttime.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPosttime.setPressed(true);
                    editTextPosttime.setSelection(editTextPosttime.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });


        Button postButton = findViewById(R.id.postButton_post_example);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagstxt = editTextPostTags.getText().toString().trim();
                String questiontxt = editTextPostQuestion.getText().toString().trim();
                String topicnumtxt = editTextPosttopicnum.getText().toString().trim();
                String timetxt = editTextPosttime.getText().toString().trim();
                if(tagstxt.isEmpty() || questiontxt.isEmpty()|| topicnumtxt.isEmpty()|| timetxt.isEmpty()){
                    if (tagstxt.isEmpty()) {
                        editTextPostTags.setError("Write something!");
                    }
                    if (questiontxt.isEmpty()) {
                        editTextPostQuestion.setError("Write something!");
                    }
                    if (topicnumtxt.isEmpty()) {
                        editTextPosttopicnum.setError("Write something!");
                    }
                    if (timetxt.isEmpty()) {
                        editTextPosttime.setError("Write something!");
                    }

                    if (tagstxt.isEmpty()) {
                        editTextPostTags.requestFocus();
                    }else if(questiontxt.isEmpty()){
                        editTextPostQuestion.requestFocus();
                    }else if(timetxt.isEmpty()){
                        editTextPosttime.requestFocus();
                    }else{
                        editTextPosttopicnum.requestFocus();
                    }
                    return;
                }
                topicNum = Integer.parseInt(topicnumtxt);
                try{
                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                    DocumentReference exampleRef;
                    if(!documentId.isEmpty() && documentId != null){
                        exampleRef= db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection(tab).document(documentId);//userId = user
                    }else{
                        exampleRef= db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection(tab).document();//userId = user
                    }

                    //todo upload data

                    Map<String,Object> uploadMap = new HashMap<>();
                    uploadMap.put("topicNum",topicNum);
                    uploadMap.put("title",tagstxt);
                    uploadMap.put("conceptPdfUrl",questiontxt);
                    uploadMap.put("time",timetxt);
                    exampleRef.set(uploadMap, SetOptions.merge());

                    onBackPressed();

                }catch (Exception e){
                }


            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void closeKeyBoard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed(){

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
