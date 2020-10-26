package au.edu.sydney.comp5216.project.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import au.edu.sydney.comp5216.project.LoginActivity;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ResetPasswordActivity;

public class Setting extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button password;
    private Button signOut;

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_setting, container, false);
//        final TextView textView = root.findViewById(R.id.text_profile);
//        password = (Button) root.findViewById(R.id.password);
//        signOut = (Button) root.findViewById(R.id.sign_out);
//
//        //get firebase auth instance
//        auth = FirebaseAuth.getInstance();
//
//        //get current user
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user == null) {
//                    // user auth state is changed - user is null
//                    // launch login activity
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//
//                    startActivity(intent);
//                }
//            }
//        };
//
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Reset password");
//                setPassword();
//            }
//        });
//
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Signed out");
//                signOut();
//            }
//        });
//        return root;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        final TextView textView = findViewById(R.id.text_profile);
        password = (Button) findViewById(R.id.password);
        signOut = (Button) findViewById(R.id.sign_out);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    Intent intent = new Intent(Setting.this, LoginActivity.class);

                    startActivity(intent);
                }
            }
        };

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Reset password");
                auth.signOut();
                Intent intent = new Intent(Setting.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Signed out");
                auth.signOut();
                Intent intent = new Intent(Setting.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }


}

