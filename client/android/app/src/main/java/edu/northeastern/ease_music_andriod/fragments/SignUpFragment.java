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
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    TextInputLayout usernameTextInput;
    TextInputLayout emailTextInput;
    TextInputLayout passwordTextInput;
    TextInputLayout confirmPasswordCTextInput;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button signUpButton = view.findViewById(R.id.sign_up_in_sign_up_page);
        usernameTextInput = view.findViewById(R.id.username_input_in_sign_up_page);
        emailTextInput = view.findViewById(R.id.email_input_in_sign_up_page);
        passwordTextInput = view.findViewById(R.id.password_input_in_sign_up_page);
        confirmPasswordCTextInput = view.findViewById(R.id.confirm_password_input_in_sign_up_page);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        Button loginButton = view.findViewById(R.id.login_in_sign_up_page);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToLoginPage();
            }
        });
    }

    private void signUp() {
        String username = usernameTextInput.getEditText().getText().toString();
        String email = emailTextInput.getEditText().getText().toString();
        String password = passwordTextInput.getEditText().getText().toString();
        String confirmPassword = confirmPasswordCTextInput.getEditText().getText().toString();

        boolean isUsernameValid = isUsernameValid(username);
        boolean isEmailValid = isEmailValid(email);
        boolean isPasswordValid = isPasswordValid(password);
        boolean isPasswordSame = isPasswordSame(password, confirmPassword);

        if (isUsernameValid && isEmailValid && isPasswordValid && isPasswordSame) {
            switchToUserProfile();
        }
    }

    private Boolean isUsernameValid(String username) {

        if (username.length() == 0) {
            usernameTextInput.setError("Username cannot be empty");
            return false;
        }

        if (username.length() >= 20) {
            usernameTextInput.setError("Username is too long, please limit 20 characters");
            return false;
        }

        usernameTextInput.setError(null);
        usernameTextInput.setErrorEnabled(false);
        return true;
    }

    private Boolean isEmailValid(String email) {

        if (email.length() == 0) {
            emailTextInput.setError("Email cannot be empty");
            return false;
        }

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailTextInput.setError("Invalid email address");
            return false;
        }

        emailTextInput.setError(null);
        emailTextInput.setErrorEnabled(false);
        return true;
    }

    private Boolean isPasswordValid(String password) {

        if (password.length() == 0) {
            passwordTextInput.setError("password cannot be empty");
            return false;
        }

        if (password.length() < 6) {
            passwordTextInput.setError("Password is too short, as least 6 characters");
            return false;
        }

        passwordTextInput.setError(null);
        passwordTextInput.setErrorEnabled(false);
        return true;
    }

    private Boolean isPasswordSame(String password, String confirmPassword) {
        System.out.println("password = " + password);
        System.out.println("confirmPassword = " + confirmPassword);
        if (!password.equals(confirmPassword)) {
            confirmPasswordCTextInput.setError("Confirm Password must be same");
            return false;
        }

        confirmPasswordCTextInput.setError(null);
        confirmPasswordCTextInput.setErrorEnabled(false);
        return true;
    }

    private void switchToUserProfile() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new UserProfileFragment());
        fragmentTransaction.commit();
    }

    private void switchToLoginPage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout, new LoginFragment());
        fragmentTransaction.commit();
    }
}