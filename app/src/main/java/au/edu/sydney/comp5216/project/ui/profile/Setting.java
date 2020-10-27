package au.edu.sydney.comp5216.project.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import au.edu.sydney.comp5216.project.LoginActivity;
import au.edu.sydney.comp5216.project.R;
import au.edu.sydney.comp5216.project.ResetPasswordActivity;
import au.edu.sydney.comp5216.mediaaccess.MarshmallowPermission;
public class Setting extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button password, signOut, changePicture, loadPhoto, takePhoto;

    public final String APP_TAG = "AnonymousSocialMedia";

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    public String photoFileName = "photo.jpg";
    private File file;


    //request codes
    private static final int MY_PERMISSIONS_REQUEST_OPEN_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;

    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);

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
        changePicture = (Button) findViewById(R.id.changePicture);
        takePhoto = (Button) findViewById(R.id.takephoto);
        loadPhoto = (Button) findViewById(R.id.loadphoto);
        takePhoto.setVisibility(View.GONE);
        loadPhoto.setVisibility(View.GONE);
//        takePhoto.setVisibility(View.VISIBLE);
//        loadPhoto.setVisibility(View.VISIBLE);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                takePhoto.setVisibility(View.VISIBLE);
                loadPhoto.setVisibility(View.VISIBLE);
//                SelectImage();
//                uploadImage();
            }
        });

    }

    public void onLoadPhotoClick(View view) {
        if (!marshmallowPermission.checkPermissionForReadfiles()) {
            marshmallowPermission.requestPermissionForReadfiles();
        } else {
            // Create intent for picking a photo from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // Bring up gallery to select a photo
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
        }
    }

    public void onTakePhotoClick(View v) {
        // Check permissions
        if (!marshmallowPermission.checkPermissionForCamera()
                || !marshmallowPermission.checkPermissionForExternalStorage()) {
            marshmallowPermission.requestPermissionForCamera();
        }  else {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // set file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            photoFileName = "IMG_" + timeStamp + ".jpg";

            // Create a photo file reference
            Uri file_uri = getFileUri(photoFileName,0);

            // Add extended data to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, MY_PERMISSIONS_REQUEST_OPEN_CAMERA);
            }
        }
    }

    // Returns the Uri for a photo/media stored on disk given the fileName
    public Uri getFileUri(String fileName) {
        // Get safe storage directory for photos
        //File mediaStorageDir = new File(
        //      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),APP_TAG);
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator
                + fileName));
    }

    // Returns the Uri for a photo/media stored on disk given the fileName and type
    public Uri getFileUri(String fileName, int type) {
        Uri fileUri = null;
        try {
            String typestr = "/images/"; //default to images type
            if (type == 1) {
                typestr = "/videos/";
            } else if (type != 0) {
                typestr = "/audios/";
            }

            // Get safe storage directory depending on type
            File mediaStorageDir = new File(this.getExternalFilesDir(null).getAbsolutePath(),
                    typestr+fileName);
            //File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
            //        typestr+fileName);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.getParentFile().exists() && !mediaStorageDir.getParentFile().mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }

            // Create the file target for the media based on filename
            file = new File(mediaStorageDir.getParentFile().getPath() + File.separator + fileName);

            // Wrap File object into a content provider, required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(
                        this.getApplicationContext(),
                        "au.edu.sydney.comp5216.mediaaccess.fileProvider", file);
            } else {
                fileUri = Uri.fromFile(mediaStorageDir);
            }
        } catch (Exception ex) {
            Log.d("getFileUri", ex.getStackTrace().toString());
        }
        return fileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ImageView ivPreview = (ImageView) findViewById(R.id.photopreview);

        ivPreview.setVisibility(View.GONE);

        if (requestCode == MY_PERMISSIONS_REQUEST_OPEN_CAMERA) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());

                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage);
                ivPreview.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivPreview.setImageBitmap(selectedImage);
                    ivPreview.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}

