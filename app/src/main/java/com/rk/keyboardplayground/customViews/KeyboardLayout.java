package com.rk.keyboardplayground.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.rk.keyboardplayground.util.HoldListener;
import com.rk.keyboardplayground.R;

public class KeyboardLayout extends ConstraintLayout {
    private KeyboardListener listener;
    private boolean capsEnabled;
    Button[] characters;

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
        inflate(getContext(), R.layout.keyboard, this);
        setButtonListener();
    }

    public void setKeyboardListener(KeyboardListener listener) {
        this.listener = listener;
    }

    private void setButtonListener() {
        characters = new Button[26];
        int j = 0;
        LinearLayout firstRow = findViewById(R.id.first_row);
        for (int i = 0; i < firstRow.getChildCount(); i++) {
            Button tmp = (Button) firstRow.getChildAt(i);
            tmp.setOnClickListener(v -> handleCharacterListener((Button) v));
            characters[j++] = tmp;
        }
        LinearLayout secondRow = findViewById(R.id.second_row);
        for (int i = 1; i < secondRow.getChildCount() - 1; i++) {
            Button tmp = (Button) secondRow.getChildAt(i);
            tmp.setOnClickListener(v -> handleCharacterListener((Button) v));
            characters[j++] = tmp;
        }
        LinearLayout thirdRow = findViewById(R.id.third_row);
        for (int i = 1; i < thirdRow.getChildCount() - 1; i++) {
            Button tmp = (Button) thirdRow.getChildAt(i);
            tmp.setOnClickListener(v -> handleCharacterListener((Button) v));
            characters[j++] = tmp;
        }
        thirdRow.findViewById(R.id.key_caps).setOnClickListener(v -> handleCaps());
        thirdRow.findViewById(R.id.key_backspace).setOnClickListener(v -> handleBackspace());
        LinearLayout fourthRow = findViewById(R.id.fourth_row);
        fourthRow.findViewById(R.id.key_numpad).setOnClickListener(v -> handleNumpad());
        fourthRow.findViewById(R.id.key_space).setOnClickListener(v -> handleSpace());
        fourthRow.findViewById(R.id.key_return).setOnClickListener(v -> handleReturn());

        thirdRow.findViewById(R.id.key_backspace).setOnTouchListener(new HoldListener(200, 50));
    }

    private void handleNumpad() {
        //TODO
    }

    private void handleSpace() {
        if (listener != null)
            listener.onSpace();
    }

    private void handleReturn() {
        if (listener != null)
            listener.onReturn();
    }

    private void handleBackspace() {
        if (listener != null)
            listener.onBackspace();
    }

    private void handleCharacterListener(Button view) {
        if (listener != null) {
            if (capsEnabled)
                listener.onCharacterKey((char) (view.getText().charAt(0) - 32));
            else listener.onCharacterKey(view.getText().charAt(0));
        }
    }

    private void handleCaps() {
        capsEnabled = !capsEnabled;
        for (Button b : characters)
            b.setAllCaps(capsEnabled);
        if (listener != null)
            listener.onCap();
    }

    public void reset() {
        if (capsEnabled) {
            capsEnabled = false;
            for (Button b : characters)
                b.setAllCaps(capsEnabled);
        }
    }

    public interface KeyboardListener {
        void onCharacterKey(char a);

        void onBackspace();

        void onSpace();

        void onCap();

        void onReturn();
    }
}


