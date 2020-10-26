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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import au.edu.sydney.comp5216.project.LoginActivity;
import au.edu.sydney.comp5216.project.R;

//public class ProfileFragment extends Fragment{
//
//    private ProfileViewModel profileViewModel;
//    private FirebaseAuth.AuthStateListener authListener;
//    private FirebaseAuth auth;
//    private Button signOut;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        profileViewModel =
//                ViewModelProviders.of(this).get(ProfileViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_setting, container, false);
//        final TextView textView = root.findViewById(R.id.text_profile);
//        signOut = (Button) root.findViewById(R.id.sign_out);
//
//        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
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
//                System.out.println("Signed out");
//                signOut();
//            }
//        });
//        return root;
//    }
//
//
//    //sign out method
//    public void signOut() {
//        auth.signOut();
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        startActivity(intent);
//    }
//
//}
public class ProfileFragment extends Fragment{

    private ProfileViewModel profileViewModel;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private AppCompatButton button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        button = (AppCompatButton)root.findViewById(R.id.btnLayoutBased);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Setting.class);
                startActivity(intent);
            }
        });
        return root;
    }
}