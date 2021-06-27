package com.tech.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.tech.R;
import com.tech.api.UserApi;
import com.tech.databinding.FragmentMessageLoginBinding;
import com.tech.domain.User;
import com.tech.web.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageLogInFragment extends Fragment implements View.OnClickListener {

    FragmentMessageLoginBinding binding;
    UserApi userApi;
    NavController navController;
    SharedPreferences sharedPreferences;
    boolean isVerified = false;
    MyHandler handler = new MyHandler();
    private String cord;
    EventHandler eventHandler;
    String phoneNumber;

    class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    isVerified = true;
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getContext(), "验证码已发送",
                            Toast.LENGTH_LONG).show();
                }
            } else if (result == SMSSDK.RESULT_ERROR) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    isVerified = false;
                    Toast.makeText(getContext(), "验证码错误",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), ((Throwable) data).getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(getContext(), "其他失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageLoginBinding.inflate(getLayoutInflater());
        userApi = RetrofitFactory.getInstance().create(UserApi.class);

        binding.login.setOnClickListener(this::onClick);
        binding.navToRegister.setOnClickListener(this::onClick);
        binding.loginSendCord.setOnClickListener(this::onClick);

        initToolBar();
        setHasOptionsMenu(true);
        isVerified = false;

        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
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
        requireActivity().getMenuInflater().inflate(R.menu.menu_password_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                requireActivity().onBackPressed();
                return true;
            case R.id.password_confirm:
                navController.navigate(R.id.passwordLoginFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_send_cord:
                if (confirmPhoneNumber()) {
                    SMSSDK.getVerificationCode("86", phoneNumber);
                }
                break;
            case R.id.login:
                if (confirmVcode()) {
                    SMSSDK.submitVerificationCode("86", phoneNumber, cord);
                }
                if (!isVerified) {
                    break;
                }
                User user = new User(binding.loginPhoneNumberEdit.getText().toString());
                userApi.getUser(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (response.body() != null) {
                            Toast.makeText(v.getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("login_state", true);
                            editor.putString("phoneNumber", phoneNumber);
                            editor.apply();
                            requireActivity().onBackPressed();
                        } else {
                            //Toast.makeText(v.getContext(), "手机号或密码不正确", Toast.LENGTH_SHORT).show();
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

    private boolean confirmPhoneNumber() {
        EditText numberEdit = binding.loginPhoneNumberEdit;
        String number = numberEdit.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(getContext(), "请输入手机号码", Toast.LENGTH_LONG).show();
            numberEdit.requestFocus();
            return false;
        } else if (number.length() != 11) {
            Toast.makeText(getContext(), "请输入正确的手机号码", Toast.LENGTH_LONG).show();
            numberEdit.requestFocus();
            return false;
        } else {
            phoneNumber = number;
            String num = "[1][3456789]\\d{9}";
            if (phoneNumber.matches(num)) {
                return true;
            } else {
                Toast.makeText(getContext(), "请输入正确的手机号码", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    private boolean confirmVcode() {
        EditText vcodeEdit = binding.loginCordEdit;
        String code = vcodeEdit.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getContext(), "请输入您的验证码", Toast.LENGTH_LONG).show();
            vcodeEdit.requestFocus();
            return false;
        } else if (code.length() != 6) {
            Toast.makeText(getContext(), "您的验证码不正确", Toast.LENGTH_LONG).show();
            vcodeEdit.requestFocus();
            return false;
        } else {
            cord = code;
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }


    private void initToolBar() {
        Toolbar toolbar = binding.loginToolbar;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("登录");
    }
}
