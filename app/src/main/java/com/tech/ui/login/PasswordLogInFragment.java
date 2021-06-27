package com.tech.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.tech.R;
import com.tech.api.UserApi;
import com.tech.databinding.FragmentPasswordLoginBinding;
import com.tech.domain.User;
import com.tech.web.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordLogInFragment extends Fragment implements View.OnClickListener {

    FragmentPasswordLoginBinding binding;
    UserApi userApi;
    NavController navController;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentPasswordLoginBinding.inflate(getLayoutInflater());
        userApi = RetrofitFactory.getInstance().create(UserApi.class);

        binding.login.setOnClickListener(this::onClick);
        binding.navToRegister.setOnClickListener(this::onClick);

        initToolBar();
        setHasOptionsMenu(true);

        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = Navigation.findNavController(binding.getRoot());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.menu_message_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                requireActivity().onBackPressed();
                return true;
            case R.id.message_confirm:
                navController.navigate(R.id.messageLoginFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                User user = new User(binding.loginPasswordEdit.getText().toString(),
                        binding.loginPasswordEdit.getText().toString());
                userApi.confirmLogin(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.body() != null) {
                            Toast.makeText(v.getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("login_state", true);
                            editor.putString("phoneNumber",
                                    binding.loginPasswordEdit.getText().toString());
                            editor.apply();
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(v.getContext(), "手机号或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
                break;
            case R.id.nav_to_register:
                navController.navigate(R.id.registerFragment);
                break;
        }
    }

    private void initToolBar() {
        Toolbar toolbar = binding.loginToolbar;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("登录");
    }
}
