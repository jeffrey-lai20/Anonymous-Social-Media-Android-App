package au.edu.sydney.comp5216.project.ui.moment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import au.edu.sydney.comp5216.project.R;

public class MomentFragment extends Fragment{

    private MomentViewModel momentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        momentViewModel =
                ViewModelProviders.of(this).get(MomentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_moment, container, false);
        final TextView textView = root.findViewById(R.id.text_moment);
        momentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
