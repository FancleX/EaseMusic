package edu.northeastern.ease_music_andriod.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.utils.DBHandler;
import edu.northeastern.ease_music_andriod.utils.UserService;

public class LoginFragment extends Fragment {

    // ================ fields ================
    private static final String TAG = "Login Fragment";
    private final UserService userService = UserService.getInstance();

    // ================ views ================
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;


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
        emailTextInput = view.findViewById(R.id.email_input_in_login_page);
        try (DBHandler dbHandler = new DBHandler(requireContext())) {
            String lastLoginUser = dbHandler.getLastLoginUser();

            if (lastLoginUser != null)
                Objects.requireNonNull(emailTextInput.getEditText()).setText(lastLoginUser);
        }

        passwordTextInput = view.findViewById(R.id.password_input_in_login_page);

        loginButton.setOnClickListener(v -> login());

        Button signUpButton = view.findViewById(R.id.sign_up_in_login_page);
        signUpButton.setOnClickListener(v -> switchToSignUpPage());
    }

    private void login() {
        String email = Objects.requireNonNull(emailTextInput.getEditText()).getText().toString();
        String password = Objects.requireNonNull(passwordTextInput.getEditText()).getText().toString();

        if (isValid(email, password)) {
            userService.signIn(email, password);
        }
    }

    private boolean isValid(String email, String password) {

        if (email.length() == 0 || password.length() == 0) {
            if (email.length() == 0) {
                emailTextInput.setError("Email cannot be empty");
            }
            if (password.length() == 0) {
                passwordTextInput.setError("Password cannot be empty");
            }
            return false;
        }

        emailTextInput.setError(null);
        emailTextInput.setErrorEnabled(false);
        passwordTextInput.setError(null);
        passwordTextInput.setErrorEnabled(false);
        return true;
    }

    private void switchToSignUpPage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new SignUpFragment());
        fragmentTransaction.addToBackStack("SignUpFragment");
        fragmentTransaction.commit();
    }
}