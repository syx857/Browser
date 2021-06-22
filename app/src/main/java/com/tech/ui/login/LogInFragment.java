package com.tech.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.tech.R;
import com.tech.api.ResponseBody;
import com.tech.api.RetrofitFactory;
import com.tech.api.UserApi;
import com.tech.databinding.FragmentLoginBinding;
import com.tech.domain.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInFragment extends Fragment implements View.OnClickListener {

    FragmentLoginBinding binding;
    UserApi userApi;
    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        userApi = RetrofitFactory.getInstance().create(UserApi.class);

        binding.login.setOnClickListener(this::onClick);
        binding.navToRegister.setOnClickListener(this::onClick);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = Navigation.findNavController(binding.getRoot());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login:
                User user = new User(binding.loginNameEdit.getText().toString(),
                        binding.loginPasswordEdit.getText().toString());
                userApi.confirmLogin(user).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.body().result) {
                            Toast.makeText(v.getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                break;
            case R.id.nav_to_register:
                navController.navigate(R.id.registerFragment);
                break;
        }



    }
}
