package com.rk.keyboardplayground.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rk.keyboardplayground.R;
import com.rk.keyboardplayground.util.HoldListener;
import com.rk.keyboardplayground.util.SPManager;

public class KeyboardLayout extends LinearLayout {
    private static final String TAG = "KeyboardLayout";
    private KeyboardListener keyboardListener;
    private boolean capsEnabled;
    private boolean isEmojiEnabled;
    private EmojiLayout emojiLayout;
    private View keyboardLayout;
    Button[] characters;
    Size size;

    public KeyboardLayout(Context context) {
        super(context);
        init();
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        capsEnabled = false;
        isEmojiEnabled = false;
        size = SPManager.getSize(getContext());
        setOrientation(VERTICAL);
        keyboardLayout = LayoutInflater.from(getContext()).inflate(R.layout.keyboard, this, false);
        addView(keyboardLayout);
        setButtonListener();
        emojiLayout = new EmojiLayout(LayoutInflater.from(getContext()).inflate(R.layout.emoji_layout, this, false));
    }

    public void setKeyboardListener(KeyboardListener listener) {
        this.keyboardListener = listener;
    }

    public void setEmojiListener(EmojiLayout.EmojiListener listener) {
        emojiLayout.setListener(listener);
    }

    private void setButtonListener() {
        characters = new Button[26];
        int j = 0;
        LinearLayout firstRow = keyboardLayout.findViewById(R.id.first_row);
        for (int i = 0; i < firstRow.getChildCount(); i++) {
            Button tmp = (Button) firstRow.getChildAt(i);
            tmp.setOnClickListener(v -> handleCharacterListener((Button) v));
            characters[j++] = tmp;
        }
        LinearLayout secondRow = keyboardLayout.findViewById(R.id.second_row);
        for (int i = 1; i < secondRow.getChildCount() - 1; i++) {
            Button tmp = (Button) secondRow.getChildAt(i);
            tmp.setOnClickListener(v -> handleCharacterListener((Button) v));
            characters[j++] = tmp;
        }
        LinearLayout thirdRow = keyboardLayout.findViewById(R.id.third_row);
        for (int i = 1; i < thirdRow.getChildCount() - 1; i++) {
            Button tmp = (Button) thirdRow.getChildAt(i);
            tmp.setOnClickListener(v -> handleCharacterListener((Button) v));
            characters[j++] = tmp;
        }
        thirdRow.findViewById(R.id.key_caps).setOnClickListener(v -> handleCaps());
        thirdRow.findViewById(R.id.key_backspace).setOnClickListener(v -> handleBackspace());
        LinearLayout fourthRow = keyboardLayout.findViewById(R.id.fourth_row);
        fourthRow.findViewById(R.id.key_numpad).setOnClickListener(v -> handleNumpad());
        fourthRow.findViewById(R.id.key_space).setOnClickListener(v -> handleSpace());
        fourthRow.findViewById(R.id.key_return).setOnClickListener(v -> handleReturn());
        fourthRow.findViewById(R.id.key_emoji).setOnClickListener(v -> toggleEmoji());
        thirdRow.findViewById(R.id.key_backspace).setOnTouchListener(new HoldListener(200, 50));
    }

    public void toggleEmoji() {
        if (isEmojiEnabled) {
            addView(keyboardLayout);
            removeView(emojiLayout.view);
        } else {
            addView(emojiLayout.view);
            removeView(keyboardLayout);
        }
        isEmojiEnabled = !isEmojiEnabled;
    }

    public void setSize(Size size) {
        this.size = size;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int newWidth = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        int newHeight = MeasureSpec.makeMeasureSpec((int) (widthSize * size.multiplier), MeasureSpec.EXACTLY);
        setMeasuredDimension(newWidth, newHeight);
        measureChildren(newWidth, newHeight);
    }

    private void handleNumpad() {
        //TODO
    }

    private void handleSpace() {
        if (keyboardListener != null)
            keyboardListener.onSpace();
    }

    private void handleReturn() {
        if (keyboardListener != null)
            keyboardListener.onReturn();
    }

    private void handleBackspace() {
        if (keyboardListener != null)
            keyboardListener.onBackspace();
    }

    private void handleCharacterListener(Button view) {
        if (keyboardListener != null) {
            if (capsEnabled)
                keyboardListener.onCharacterKey((char) (view.getText().charAt(0) - 32));
            else keyboardListener.onCharacterKey(view.getText().charAt(0));
        }
    }

    private void handleCaps() {
        capsEnabled = !capsEnabled;
        for (Button b : characters)
            b.setAllCaps(capsEnabled);
        if (keyboardListener != null)
            keyboardListener.onCap();
    }

    public void reset() {
        if (capsEnabled) {
            capsEnabled = false;
            for (Button b : characters)
                b.setAllCaps(capsEnabled);
        }
    }

    public void setQwerty() {
        if (isEmojiEnabled)
            toggleEmoji();
    }

    public interface KeyboardListener {
        void onCharacterKey(char a);

        void onBackspace();

        void onSpace();

        void onCap();

        void onReturn();
    }

    public enum Size {
        SMALL(9 / 21f),
        MEDIUM(9 / 16f),
        LARGE(2 / 3f);

        protected final float multiplier;

        Size(float multiplier) {
            this.multiplier = multiplier;
        }
    }
}


