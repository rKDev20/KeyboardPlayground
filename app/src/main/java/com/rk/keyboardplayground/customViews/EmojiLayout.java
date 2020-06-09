package com.rk.keyboardplayground.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.rk.keyboardplayground.R;
import com.rk.keyboardplayground.adapters.EmojiViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EmojiLayout{
    private static final String TAG = "EmojiLayout";
    private String[][] emojiList;
    View view;
    private EmojiListener listener;
    EmojiViewPagerAdapter adapter;

    public EmojiLayout(View view) {
        this.view=view;
        emojiList = new String[3][];
        initEmojiListFromAssets();
        ViewPager pager = view.findViewById(R.id.viewPager);
        Button backspace = view.findViewById(R.id.key_backspace);
        Button qwerty = view.findViewById(R.id.key_qwerty);
        adapter = new EmojiViewPagerAdapter(view.getContext(), 3, emojiList);
        pager.setAdapter(adapter);
        backspace.setOnClickListener(v -> listener.onBackspace());
        qwerty.setOnClickListener(v -> listener.onQwerty());
    }

    public void setListener(EmojiLayout.EmojiListener listener) {
        this.listener = listener;
        adapter.setListener(listener);
    }

    private void initEmojiListFromAssets() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(view.getContext().getResources().getAssets().open("emoji.json")));
            StringBuilder builder = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null)
                builder.append(tmp);
            JSONArray array = new JSONArray(builder.toString());
            emojiList = new String[array.length()][];
            for (int i = 0; i < array.length(); i++) {
                JSONArray list = array.getJSONArray(i);
                emojiList[i] = new String[list.length()];
                for (int j = 0; j < list.length(); j++) {
                    emojiList[i][j] = list.getString(j);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public interface EmojiListener {
        void onEmoji(String emoji);

        void onBackspace();

        void onQwerty();
    }

}
