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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Bytes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    private FirebaseFirestore db;

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
                changePicture.setVisibility(View.GONE);
//                SelectImage();
//                uploadImage();
            }
        });


        ImageView ivPreview = (ImageView) findViewById(R.id.photopreview);

        Uri uri = auth.getCurrentUser().getPhotoUrl();
        Bitmap bmp = null;

//        ivPreview.setImageBitmap(bmp);
//        // by this point we have the camera photo on disk
//        Bitmap takenImage = BitmapFactory.decodeFile(uri.getEncodedPath());
        Glide.with(ivPreview.getContext())
            .load(uri)
            .into(ivPreview);
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            ivPreview.setImageBitmap(bitmap);
//            ivPreview.setVisibility(View.VISIBLE);

//        // Load the taken image into a preview

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
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, MY_PERMISSIONS_REQUEST_OPEN_CAMERA);
            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ImageView ivPreview = (ImageView) findViewById(R.id.photopreview);

//        ivPreview.setVisibility(View.GONE);

        if (requestCode == MY_PERMISSIONS_REQUEST_OPEN_CAMERA) {
            // by this point we have the camera photo on disk
//            Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            ivPreview.setImageBitmap(photo);

//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            takenImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            String path = MediaStore.Images.Media.insertImage(ivPreview.getContentResolver(), inImage, "Title", null);
//            return Uri.parse(path);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(file.getAbsolutePath()))
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
            Glide.with(ivPreview.getContext())
                    .load(file.getAbsolutePath())
                    .into(ivPreview);
            // Load the taken image into a preview
//                ivPreview.setImageBitmap(takenImage);
//                ivPreview.setVisibility(View.VISIBLE);
//                if(takenImage != null) {
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//                    final ProgressDialog progressDialog = new ProgressDialog(this);
//                    progressDialog.setTitle("Uploading...");
//                    progressDialog.show();
//                    takenImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] bytes = baos.toByteArray();
//
//                    StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
//
//                    //Adds the URI as a reference for Firebase Storage
//                    ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(Setting.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(Setting.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                        }
//                    });
//                }

        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(photoUri)
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
                Glide.with(ivPreview.getContext())
                        .load(photoUri)
                        .into(ivPreview);
                // Do something with the photo based on Uri
//                if(photoUri != null) {
//                    //Upload to firebase
//                    final ProgressDialog progressDialog = new ProgressDialog(this);
//                    progressDialog.setTitle("Uploading...");
//                    progressDialog.show();
//                    String uri = "images/"+ UUID.randomUUID().toString();
//                    StorageReference ref = storageReference.child(uri);
//
//                    //Adds the URI as a reference for Firebase Storage
//                    ref.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(Setting.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(Setting.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                        }
//                    });
//
//                }


//                Bitmap selectedImage;
//                try {
//                    selectedImage = MediaStore.Images.Media.getBitmap(
//                            this.getContentResolver(), photoUri);
//
//                    // Load the selected image into a preview
//                    ivPreview.setImageBitmap(selectedImage);
//                    ivPreview.setVisibility(View.VISIBLE);
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
        }
    }
}

