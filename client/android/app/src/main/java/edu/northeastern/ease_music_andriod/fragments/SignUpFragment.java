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
import edu.northeastern.ease_music_andriod.utils.UserService;


public class SignUpFragment extends Fragment {

    // ================ fields ================
    private static final String TAG = "SignUp Fragment";

    // ================ views ================
    private TextInputLayout usernameTextInput;
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputLayout confirmPasswordCTextInput;


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

        signUpButton.setOnClickListener(v -> signUp());

        Button loginButton = view.findViewById(R.id.login_in_sign_up_page);
        loginButton.setOnClickListener(v -> switchToLoginPage());
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
            UserService.getInstance().signUp(username, email, password);
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
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}