package com.flyjingfish.switchkeyboard;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.switchkeyboard.databinding.ActivityExample2Binding;
import com.flyjingfish.switchkeyboardlib.BaseSwitchKeyboardUtil;
import com.flyjingfish.switchkeyboardlib.MenuModeView;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;

import java.util.ArrayList;
import java.util.List;

public class Example2Activity extends AppCompatActivity {
    private SwitchKeyboardUtil baseChatKeyboardUtil;
    private ActivityExample2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseChatKeyboardUtil = new SwitchKeyboardUtil(this);
        baseChatKeyboardUtil.checkSoftMode();
        binding = ActivityExample2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        baseChatKeyboardUtil.setMenuViewHeightEqualKeyboard(true);
        baseChatKeyboardUtil.attachLifecycle(this);
        baseChatKeyboardUtil.setInputEditText(binding.etContent);
        baseChatKeyboardUtil.setAudioBtn(binding.tvAudio);
        baseChatKeyboardUtil.setAudioTouchVIew(binding.tvAudioTouch);
        baseChatKeyboardUtil.setMenuViewContainer(binding.llMenu);
        baseChatKeyboardUtil.setToggleMenuViews(new MenuModeView(binding.tvMore, binding.llMenuBtn),
                new MenuModeView(binding.tvFace, binding.llEmoji, binding.tvFaceBack,true),
                new MenuModeView(binding.tvWord, binding.llWord, binding.tvWordBack,true),
                new MenuModeView(binding.ivFace, binding.llFace)
        );
        baseChatKeyboardUtil.setOnKeyboardMenuListener(new SwitchKeyboardUtil.OnKeyboardMenuListener() {
            @Override
            public void onScrollToBottom() {
                scrollToBottom();
            }

            @Override
            public void onKeyboardHide(int keyboardHeight) {

            }

            @Override
            public void onKeyboardShow(int keyboardHeight) {
                binding.tvAudio.setImageResource(R.drawable.ic_audio);
                binding.ivFace.setImageResource(R.drawable.ic_face);
            }

            @Override
            public void onCallShowKeyboard() {

            }

            @Override
            public void onCallHideKeyboard() {
            }

            @Override
            public void onShowMenuLayout(View layoutView) {
                binding.tvAudio.setImageResource(layoutView == binding.tvAudioTouch?R.drawable.ic_keyboard:R.drawable.ic_audio);
                binding.ivFace.setImageResource(layoutView == binding.llFace?R.drawable.ic_keyboard:R.drawable.ic_face);
            }
        });
        List<String> msgList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            msgList.add("item=" + i);
        }
        MsgAdapter msgAdapter = new MsgAdapter(msgList);
        binding.rv.setAdapter(msgAdapter);
        binding.rv.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && baseChatKeyboardUtil.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void scrollToBottom() {
//        if (!binding.rv.canScrollVertically(1)){
        binding.rv.scrollToPosition(binding.rv.getAdapter().getItemCount() - 1);
//        }
    }
}