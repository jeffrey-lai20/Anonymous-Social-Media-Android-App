package au.edu.sydney.comp5216.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RadioGroup radioGender;
    private String gender = "male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        radioGender=(RadioGroup)findViewById(R.id.radioGrp);


        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);

                    if (btn.getId() == R.id.radioM){
                        btn.setText("Male");
                    } else if (btn.getId() == R.id.radioF) {
                        btn.setText("Female");
                    } else if (btn.getId() == R.id.radioO) {
                        btn.setText("Other");
                    }

                    if (btn.getId() == checkedId) {
                        gender=btn.getText().toString();// here gender will contain M or F.
                    }
                }
                Log.e("Gender", gender);
            }
        });


        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter an email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password is too short. Please enter at least 6 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    //Access number of users in firestore database
                                    db.collection("users")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        int count = 0;
                                                        //Count number of users
                                                        for (DocumentSnapshot document : task.getResult()) {//
                                                            count++;
                                                        }
                                                        count = 10000000 + count;   //User ID will be determined on number of users
                                                        String uri = "https://firebasestorage.googleapis.com/v0/b/comp5216-project.appspot.com/o/default.png?alt=media&token=aaffd36b-ae21-4a8f-bfe0-cd0dc742c892";
                                                        final Map<String, Object> user = new HashMap<>();
                                                        user.put("email", email);
                                                        user.put("myRooms", new ArrayList());
                                                        user.put("savedCollections", new ArrayList());
                                                        user.put("id", count);
                                                        user.put("gender", gender);
                                                        user.put("photo", uri);
                                                        user.put("postLikes", new ArrayList());

                                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(Integer.toString(count))
                                                                .setPhotoUri(Uri.parse(uri))
                                                                .build();

                                                        auth.getCurrentUser().updateProfile(profileUpdates);

                                                        firebaseUser.updateProfile(profileUpdates)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d("TAG", "User profile updated.");
                                                                        }
                                                                    }
                                                                });

                                                        //Sign in is successful

                                                        db.collection("users").document(String.valueOf(count)).set(user);
//                                                        //Add user details to Firestore database
//                                                        db.collection("users").add(user)
//                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                                    @Override
//                                                                    public void onSuccess(DocumentReference documentReference) {
//                                                                    }
//                                                                })
//                                                                .addOnFailureListener(new OnFailureListener() {
//                                                                    @Override
//                                                                    public void onFailure(@NonNull Exception e) {
//                                                                    }
//                                                                });
                                                    } else {
                                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                    Toast.makeText(SignUpActivity.this, "Sign up successful. Welcome to the app!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}