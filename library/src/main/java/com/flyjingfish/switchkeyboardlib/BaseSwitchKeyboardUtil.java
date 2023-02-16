package com.flyjingfish.switchkeyboardlib;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;


public class BaseSwitchKeyboardUtil {
    protected SystemKeyboardUtils keyboardUtils;
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected boolean isShowMenu;
    protected View audioBtn;
    protected View audioTouchVIew;
    protected EditText etContent;
    protected Activity activity;
    protected ViewGroup menuViewContainer;
    protected boolean menuViewHeightEqualKeyboard;
    protected boolean keyboardIsShow;
    public BaseSwitchKeyboardUtil(Activity activity) {
        this(activity,false);
    }

    /**
     *
     * @param activity
     * @param menuViewHeightEqualKeyboard 是否让菜单高度和键盘高度一样（首次可能会有误差）
     */
    public BaseSwitchKeyboardUtil(Activity activity, boolean menuViewHeightEqualKeyboard) {
        this.activity = activity;
        this.menuViewHeightEqualKeyboard = menuViewHeightEqualKeyboard;
    }

    /**
     *
     * @param etContent 文本消息输入框（不可为空）
     * @param audioBtn 语音消息按钮（可为空）
     * @param audioTouchVIew 语音消息按住说话按钮（可为空）
     * @param menuViewContainer 菜单总包裹布局（不可为空）
     */
    public void setBaseViews(@NonNull EditText etContent, @Nullable View audioBtn,@Nullable View audioTouchVIew,@NonNull ViewGroup menuViewContainer) {
        this.etContent = etContent;
        this.audioBtn = audioBtn;
        this.audioTouchVIew = audioTouchVIew;
        this.menuViewContainer = menuViewContainer;
    }

    protected void saveKeyboardHeight(int value){
        SharedPreferences sp = activity.getApplication().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        sp.edit().putInt("KeyboardHeight", value).apply();
    }

    protected int getKeyboardHeight(){
        SharedPreferences sp = activity.getApplication().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        return sp.getInt("KeyboardHeight", (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP,240,activity.getResources().getDisplayMetrics()));
    }

    /**
     * 根据生命周期来确保不发生内存泄漏
     * @param lifecycleOwner
     */
    public void attachLifecycle(LifecycleOwner lifecycleOwner){
        lifecycleOwner.getLifecycle().addObserver(new MyLifecycleEventObserver());
    }

    protected class MyLifecycleEventObserver implements LifecycleEventObserver{
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_CREATE){
                onCreate(source);
            } else if (event == Lifecycle.Event.ON_DESTROY){
                onDestroy(source);
            }
        }
    }

    protected void onCreate(@NonNull LifecycleOwner owner){
        keyboardUtils = new SystemKeyboardUtils(activity);
        keyboardUtils.setOnKeyBoardListener(onKeyBoardListener);
        setSystemUi();
        int menuHeight = getKeyboardHeight();
        if (menuViewHeightEqualKeyboard){
            ViewGroup.LayoutParams layoutParams = menuViewContainer.getLayoutParams();
            layoutParams.height = menuHeight;
            menuViewContainer.setLayoutParams(layoutParams);
        }

        if (audioBtn != null){
            audioBtn.setOnClickListener(v -> {

                if (keyboardIsShow){
                    hideKeyboard();
                    menuViewContainer.setVisibility(View.GONE);
                    etContent.setVisibility(View.GONE);
                    if (audioTouchVIew != null){
                        audioTouchVIew.setVisibility(View.VISIBLE);
                    }
                }else if (menuViewContainer.getVisibility() == View.VISIBLE){
                    menuViewContainer.setVisibility(View.GONE);
                    etContent.setVisibility(View.GONE);
                    if (audioTouchVIew != null){
                        audioTouchVIew.setVisibility(View.VISIBLE);
                    }
                }else if (audioTouchVIew != null && audioTouchVIew.getVisibility() == View.GONE){
                    etContent.setVisibility(View.GONE);
                    audioTouchVIew.setVisibility(View.VISIBLE);
                }else {
                    etContent.setVisibility(View.VISIBLE);
                    if (audioTouchVIew != null){
                        audioTouchVIew.setVisibility(View.GONE);
                    }
                    showKeyboard();
                }
                isShowMenu = false;
            });
        }
    }

    protected void onDestroy(@NonNull LifecycleOwner owner){
        handler.removeCallbacksAndMessages(null);
        keyboardUtils.onDestroy();
    }


    public void hideKeyboard() {
        if (onKeyboardMenuListener != null){
            onKeyboardMenuListener.onCallHideKeyboard();
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
        etContent.clearFocus();
    }

    protected void showKeyboard() {
        if (onKeyboardMenuListener != null){
            onKeyboardMenuListener.onCallShowKeyboard();
        }
        etContent.setSelection(etContent.getText().length());
        etContent.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etContent, 0);
    }

    public void setSystemUi(){
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int flag = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(flag | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isShowMenu){
            menuViewContainer.setVisibility(View.GONE);
            isShowMenu = false;
            return true;
        }
        return false;
    }

    private final SystemKeyboardUtils.OnKeyBoardListener onKeyBoardListener = new SystemKeyboardUtils.OnKeyBoardListener() {
        @Override
        public void onShow(int height) {
            ViewGroup.LayoutParams layoutParams = menuViewContainer.getLayoutParams();
            layoutParams.height = height;
            menuViewContainer.setLayoutParams(layoutParams);
            menuViewContainer.setVisibility(View.INVISIBLE);
            saveKeyboardHeight(height);
            if (onKeyboardMenuListener != null){
                onKeyboardMenuListener.onKeyboardShow(height);
                onKeyboardMenuListener.onScrollToBottom();
            }
            keyboardIsShow = true;
        }

        @Override
        public void onHide(int height) {
            if (onKeyboardMenuListener != null){
                onKeyboardMenuListener.onKeyboardHide(height);
            }
            keyboardIsShow = false;
        }
    };

    /**
     * 切换键盘和更多菜单
     */
    public void toggleMoreView(){
        if (!isShowMenu){

            isShowMenu = true;

            menuViewContainer.setVisibility(View.VISIBLE);
            if (audioTouchVIew != null){
                audioTouchVIew.setVisibility(View.GONE);
            }
            etContent.setVisibility(View.VISIBLE);
            if (!menuViewHeightEqualKeyboard){
                handler.postDelayed(() -> {
                    ViewGroup.LayoutParams layoutParams = menuViewContainer.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    menuViewContainer.setLayoutParams(layoutParams);
                },200);
            }

            hideKeyboard();

        }else {
            isShowMenu = false;
            etContent.setVisibility(View.VISIBLE);
            if (audioTouchVIew != null){
                audioTouchVIew.setVisibility(View.GONE);
            }
            showKeyboard();
        }
        if (onKeyboardMenuListener != null){
            onKeyboardMenuListener.onScrollToBottom();
        }

    }

    private OnKeyboardMenuListener onKeyboardMenuListener;

    public interface OnKeyboardMenuListener{
        void onScrollToBottom();
        void onKeyboardHide(int keyboardHeight);
        void onKeyboardShow(int keyboardHeight);
        void onCallShowKeyboard();
        void onCallHideKeyboard();
    }

    /**
     * 设置监听
     * @param onKeyboardMenuListener
     */
    public void setOnKeyboardMenuListener(OnKeyboardMenuListener onKeyboardMenuListener) {
        this.onKeyboardMenuListener = onKeyboardMenuListener;
    }
}
