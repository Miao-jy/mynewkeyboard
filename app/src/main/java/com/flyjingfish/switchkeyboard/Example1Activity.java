package com.flyjingfish.switchkeyboard;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.switchkeyboard.databinding.ActivityExample1Binding;
import com.flyjingfish.switchkeyboardlib.MenuModeView;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;

import java.util.ArrayList;
import java.util.List;

public class Example1Activity extends AppCompatActivity {
    private SwitchKeyboardUtil switchKeyboardUtil;
    private ActivityExample1Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExample1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switchKeyboardUtil = new SwitchKeyboardUtil(this);
        switchKeyboardUtil.setMenuViewHeightEqualKeyboard(false);
        switchKeyboardUtil.setUseSwitchAnim(true);
        switchKeyboardUtil.setUseMenuUpAnim(true);
        switchKeyboardUtil.attachLifecycle(this);
        switchKeyboardUtil.setInputEditText(binding.etContent);
        switchKeyboardUtil.setMenuViewContainer(binding.llMenu);
        switchKeyboardUtil.setToggleMenuViews(
                new MenuModeView(binding.tvGift,binding.llGift),
                new MenuModeView(binding.tvWord,binding.llWord)
        );
        switchKeyboardUtil.setOnKeyboardMenuListener(new SwitchKeyboardUtil.OnKeyboardMenuListener() {
            @Override
            public void onScrollToBottom() {
                scrollToBottom();
            }

            @Override
            public void onCallShowKeyboard() {

            }

            @Override
            public void onCallHideKeyboard() {
            }

            @Override
            public void onKeyboardHide(int keyboardHeight) {

            }

            @Override
            public void onKeyboardShow(int keyboardHeight) {
            }



            @Override
            public void onShowMenuLayout(View layoutView) {
            }

            @Override
            public void onHideMenuViewContainer() {
            }
        });
        binding.rv.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                switchKeyboardUtil.hideMenuAndKeyboard();
            }
            return false;
        });
        List<String> msgList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            msgList.add("item="+i);
        }
        MsgAdapter msgAdapter = new MsgAdapter(msgList);
        binding.rv.setAdapter(msgAdapter);
        binding.rv.setLayoutManager(new LinearLayoutManager(this));

        binding.rv.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> scrollToBottom());

    }


    private void scrollToBottom() {
//        if (!binding.rv.canScrollVertically(1)){
        binding.rv.scrollToPosition(binding.rv.getAdapter().getItemCount() - 1);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (switchKeyboardUtil.onKeyDown(keyCode, event)){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}