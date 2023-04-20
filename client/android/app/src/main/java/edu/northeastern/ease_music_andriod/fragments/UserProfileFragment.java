package edu.northeastern.ease_music_andriod.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.userFavoriteItem.FavoriteItemAdapter;
import edu.northeastern.ease_music_andriod.utils.DataCache;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class UserProfileFragment extends Fragment {

    // ================ fields ================
    private final DataCache dataCache = DataCache.getInstance();
    private static final String AUDIO_DIR = "AUDIOS";
    private FavoriteItemAdapter favoriteItemAdapter;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;


    // ================ views ================
    private Button signOutButton;
    private ImageView profileImage;
    private MaterialCardView cardView;
    private TextView email, favoriteCount, downloadCount;
    private RecyclerView favoriteRecycler;
    private Animation top2BotAnim, bot2TopAnim;
    private RelativeLayout headerLayout, bottomLayout;
    private LinearLayout cardsLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);

        signOutButton = root.findViewById(R.id.sign_out);
        profileImage = root.findViewById(R.id.profile_image);
        cardView = root.findViewById(R.id.favorite_card);
        email = root.findViewById(R.id.user_profile_email);
        favoriteCount = root.findViewById(R.id.favorite_count);
        downloadCount = root.findViewById(R.id.download_count);
        favoriteRecycler = root.findViewById(R.id.user_profile_recycler);
        headerLayout = root.findViewById(R.id.user_profile_header_layout);
        bottomLayout = root.findViewById(R.id.user_profile_bottom);
        cardsLayout = root.findViewById(R.id.user_profile_cards_layout);

        signOutButton.setOnClickListener(v -> switchToLoginPage());
        email.setText(dataCache.getUserCache().getUsername());
        favoriteCount.setText(String.valueOf(dataCache.getUserCache().getFavorites().size()));
        downloadCount.setText(String.valueOf(getDownloadCount()));

        // add animations
        top2BotAnim = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_move_animation);
        bot2TopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.botton_to_top_move_animation);

        headerLayout.setAnimation(top2BotAnim);
        cardsLayout.setAnimation(bot2TopAnim);
        bottomLayout.setAnimation(bot2TopAnim);

        favoriteRecycler.setVisibility(View.GONE);
        favoriteRecycler.setHasFixedSize(true);
        favoriteItemAdapter = new FavoriteItemAdapter();
        favoriteRecycler.setAdapter(favoriteItemAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false);
        favoriteRecycler.setLayoutManager(layoutManager);
        favoriteItemAdapter.updateData();

        cardView.setOnClickListener(v -> favoriteRecycler.setVisibility(View.VISIBLE));

        profileImage.setOnClickListener(v -> changeProfileImage());

        // Request camera permission
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission granted, start camera
                takePictureLauncher.launch(createImageFileUri());
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        // Take picture from camera
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), isTaken -> {
            if (isTaken) {
                // Query the latest image file from MediaStore
                String[] projection = { MediaStore.Images.ImageColumns.DATA };
                String selection = MediaStore.Images.ImageColumns.DATE_TAKEN + "=?";
                String[] selectionArgs = { String.valueOf(System.currentTimeMillis()) };
                String sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
                Cursor cursor = requireActivity().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );

                // Get the image file path
                String imagePath = null;
                if (cursor != null && cursor.moveToFirst()) {
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                    cursor.close();
                }

                // Display the image
                if (imagePath != null) {
                    Picasso.get()
                            .load(new File(imagePath))
                            .resize(100, 100)
                            .transform(new CropCircleTransformation())
                            .into(profileImage);
                }
            }
        });

        // Pick image from gallery
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                Picasso.get()
                        .load(result)
                        .resize(100, 100)
                        .transform(new CropCircleTransformation())
                        .into(profileImage);
            }
        });


        return root;
    }

    private void changeProfileImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start camera
                    takePictureLauncher.launch(createImageFileUri());
                } else {
                    // Request camera permission
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            } else if (options[which].equals("Choose from Gallery")) {
                pickImageLauncher.launch("image/*");
            } else if (options[which].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private Uri createImageFileUri() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile;

        try {
            // Create the image file in the external storage directory
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

            // Get the content URI for the image file using FileProvider
            return FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".fileprovider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private int getDownloadCount() {
        File dir = new File(requireContext().getExternalFilesDir(null), AUDIO_DIR);
        File[] files = dir.listFiles((file, name) -> name.endsWith(".mp3"));

        if (files != null)
            return files.length;

        return 0;
    }

    private void switchToLoginPage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout, new LoginFragment());
        fragmentTransaction.commit();

        dataCache.getUserCache().clearUserCache();
    }
}