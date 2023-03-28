package edu.northeastern.ease_music_andriod.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import edu.northeastern.ease_music_andriod.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputLayout usernameTextInput;
    TextInputLayout passwordTextInput;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button loginButton = view.findViewById(R.id.login_in_login_page);
        usernameTextInput = view.findViewById(R.id.username_input_in_login_page);
        passwordTextInput = view.findViewById(R.id.password_input_in_login_page);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button signUpButton = view.findViewById(R.id.sign_up_in_login_page);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignUpPage();
            }
        });
    }

    private void login() {
        String username = usernameTextInput.getEditText().getText().toString();
        String password = passwordTextInput.getEditText().getText().toString();
        if (isMatch(username, password)) {
            switchToUserProfile();
        }
    }

    private Boolean isMatch(String username, String password) {

        if (username.length() == 0 || password.length() == 0) {
            if (username.length() == 0) {
                usernameTextInput.setError("Username cannot be empty");
            }
            if (password.length() == 0) {
                passwordTextInput.setError("Password cannot be empty");
            }
            return false;
        }


        // look up in the database and check if match
        // please add you code

        usernameTextInput.setError(null);
        usernameTextInput.setErrorEnabled(false);
        passwordTextInput.setError(null);
        passwordTextInput.setErrorEnabled(false);
        return true;
    }

    private void switchToUserProfile() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new UserProfileFragment());
        fragmentTransaction.commit();
    }

    private void switchToSignUpPage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new SignUpFragment());
        fragmentTransaction.commit();
    }
}