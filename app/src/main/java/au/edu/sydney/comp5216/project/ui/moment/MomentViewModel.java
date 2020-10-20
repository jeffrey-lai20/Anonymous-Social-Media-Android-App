package au.edu.sydney.comp5216.project.ui.moment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MomentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MomentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is moment fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

