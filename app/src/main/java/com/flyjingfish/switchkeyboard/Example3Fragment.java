package com.flyjingfish.switchkeyboard;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.switchkeyboard.databinding.ActivityExample1Binding;
import com.flyjingfish.switchkeyboardlib.MenuModeView;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;

import java.util.ArrayList;
import java.util.List;

public class Example3Fragment extends Fragment {

    private com.flyjingfish.switchkeyboard.databinding.ActivityExample1Binding binding;
    private SwitchKeyboardUtil switchKeyboardUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityExample1Binding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchKeyboardUtil = new SwitchKeyboardUtil(requireActivity());
        switchKeyboardUtil.setMenuViewHeightEqualKeyboard(false);
        switchKeyboardUtil.setUseSwitchAnim(true);
        switchKeyboardUtil.setInputEditText(binding.etContent);
        switchKeyboardUtil.setMenuViewContainer(binding.llMenu);
        switchKeyboardUtil.setToggleMenuViews(
                new MenuModeView(binding.tvGift,binding.llGift),
                new MenuModeView(binding.tvWord,binding.llWord));
        switchKeyboardUtil.attachLifecycle(this);
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
        binding.rv.setLayoutManager(new LinearLayoutManager(requireActivity()));

        binding.rv.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> scrollToBottom());

        binding.rv.postDelayed(() -> scrollToBottom(),200);
    }

    private void scrollToBottom() {
        binding.rv.scrollToPosition(binding.rv.getAdapter().getItemCount() - 1);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return switchKeyboardUtil.onKeyDown(keyCode, event);
    }
}
