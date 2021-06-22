package com.tech.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tech.api.ResponseBody;
import com.tech.api.RetrofitFactory;
import com.tech.api.UserApi;
import com.tech.databinding.FragmentRegisterBinding;
import com.tech.domain.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    FragmentRegisterBinding binding;
    UserApi userApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        userApi = RetrofitFactory.getInstance().create(UserApi.class);
        binding.register.setOnClickListener(this::onClick);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        User user = new User(binding.registerNameEdit.getText().toString(),
                binding.registerPasswordEdit.getText().toString());
        UserApi apiService = RetrofitFactory.getInstance().create(UserApi.class);
        apiService.register(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body().result) {
                    Toast.makeText(v.getContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "用户名已存在", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
