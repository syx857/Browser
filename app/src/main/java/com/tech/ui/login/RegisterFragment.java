package com.tech.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.tech.R;
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
    EventHandler eventHandler;
    String phoneNumber;
    private String vcode;
    MyHandler handler = new MyHandler();
    NavController navController;
    boolean isVerified = false;
    SharedPreferences sharedPreferences;

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

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getContext(), "验证码已发送",
                            Toast.LENGTH_LONG).show();
                }
            } else if (result == SMSSDK.RESULT_ERROR) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
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
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        userApi = RetrofitFactory.getInstance().create(UserApi.class);
        binding.register.setOnClickListener(this::onClick);
        binding.registerSendCord.setOnClickListener(this::onClick);

        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SMSSDK.registerEventHandler(eventHandler);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = Navigation.findNavController(binding.getRoot());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                if (confirmVcode()) {
                    SMSSDK.submitVerificationCode("86", phoneNumber, vcode);
                }
                if (!confirm() || !isVerified) {
                    break;
                }
                User user = new User(binding.registerNameEdit.getText().toString(),
                        binding.registerPasswordEdit.getText().toString(), phoneNumber);
                UserApi apiService = RetrofitFactory.getInstance().create(UserApi.class);
                apiService.register(user).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                            Response<ResponseBody> response) {
                        if (response.body().result) {
                            Toast.makeText(v.getContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                            //SharedPreferences.Editor editor=sharedPreferences.edit();
                            //editor.putBoolean("login_state",true);
                            //editor.putString("userName",binding.registerNameEdit.getText().toString());
                            navController.navigate(R.id.userCenterFragment);
                        } else {
                            Toast.makeText(v.getContext(), "该手机号已注册", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                isVerified = false;
                break;
            case R.id.register_send_cord:
                if (confirmPhoneNumber()) {
                    SMSSDK.getVerificationCode("86", phoneNumber);
                    binding.registerPhoneNumberEdit.requestFocus();
                }
                break;
        }

    }

    private boolean confirmPhoneNumber() {
        EditText numberEdit = binding.registerPhoneNumberEdit;
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
        EditText vcodeEdit = binding.registerVcodeEdit;
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
            vcode = code;
            return true;
        }
    }

    private boolean confirm() {
        if (TextUtils.isEmpty(binding.registerNameEdit.getText())) {
            Toast.makeText(getContext(), "请输入昵称", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(binding.registerPasswordEdit.getText())) {
            Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
