package com.rk.keyboardplayground.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.rk.keyboardplayground.R;
import com.rk.keyboardplayground.customViews.EmojiLayout;

public class EmojiViewPagerAdapter extends PagerAdapter {

    private Context context;
    private final int pages;
    private final String[][] emojis;
    private EmojiLayout.EmojiListener listener;

    public EmojiViewPagerAdapter(Context context, int pages, String[][] emojis) {
        this.context = context;
        this.pages = pages;
        this.emojis = emojis;
    }

    public void setListener(EmojiLayout.EmojiListener listener){
        this.listener=listener;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        GridView view = (GridView) LayoutInflater.from(context).inflate(R.layout.emoji_page, collection, false);
        view.setOnItemClickListener((parent, view1, position1, id) -> {
            if (listener != null)
                listener.onEmoji(emojis[position][position1]);
        });
        String[] emojiForThisPage = emojis[position];
        view.setAdapter(new ArrayAdapter<>(context, R.layout.emoji_holder, emojiForThisPage));
        collection.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return pages;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return emojis[position][0];
    }
}
