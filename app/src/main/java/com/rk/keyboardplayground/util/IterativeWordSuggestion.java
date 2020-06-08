package com.rk.keyboardplayground.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.rk.keyboardplayground.database.DatabaseHelper;
import com.rk.keyboardplayground.model.Pair;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class IterativeWordSuggestion {
    private final static String TAG = "Suggestion";

    private Context context;
    private Node root;
    private Node workingRoot;
    private Disposable disposbale;

    public IterativeWordSuggestion(Context context) {
        this.context = context;
        Observable<List<Pair>> dictionaryObservable = DatabaseHelper.getInstance(context).dao().getDictionary();
        disposbale = dictionaryObservable.subscribe(list -> {
            Log.d(TAG, "List updated");
            root = new Node();
            for (Pair p : list)
                root.add(p.name, p.frequency);
            workingRoot = root;
        });
    }

    @SuppressLint("CheckResult")
    public void updateFrequency(String s) {
        DatabaseHelper.getInstance(context)
                .dao()
                .addFrequency(s)
                .subscribeOn(Schedulers.io())
                .subscribe();
//                .subscribe(()->Log.d(TAG,"updated"));
    }

    private static class Node {
        char ch;
        int frequency;
        Node[] child;
        String s;

        Node() {
            s = "";
            frequency = -1;
            child = new Node[26];
        }

        Node(char i, String s) {
            frequency = -1;
            ch = i;
            this.s = s + i;
            child = new Node[26];
        }

        void add(String word, int frequency) {
            word = word.toLowerCase();
            Node current = this;
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                if (ch < 96 || ch > 122)
                    continue;
                current = current.getNode(ch, current.s);
            }
            current.frequency = frequency;
        }

        Node getNode(char i, String s) {
            if (child[i - 97] == null)
                child[i - 97] = new Node(i, s);
            return child[i - 97];
        }

        Node getNullableNode(char i) {
            return child[i - 97];
        }
    }

    public void reset() {
        workingRoot = root;
    }

    public void close() {
        disposbale.dispose();
    }

    public String[] getSuggestion(char s) {
        if (workingRoot != null) {
            String[] ret = new String[3];
            if (s >= 65 && s <= 90)
                s = (char) (s + 32);
            workingRoot = workingRoot.getNullableNode(s);
            if (workingRoot == null)
                return null;
            Node[] max = new Node[5];
            iterate(workingRoot, max);
            if (max[0] != null) {
                ret[1] = max[0].s;
                if (max[1] != null)
                    ret[0] = max[1].s;
                if (max[2] != null)
                    ret[2] = max[2].s;
            } else {
                return null;
            }
            return ret;
        } else return null;
    }

    private static void check(Node node, Node[] max) {
        int i;
        boolean flag = false;
        for (i = 0; i < 5; i++) {
            if (max[i] == null || node.frequency > max[i].frequency) {
                flag = true;
                break;
            }
        }
        if (flag) {
            System.arraycopy(max, i, max, i + 1, 4 - i);
            max[i] = node;
        }
    }


    static void iterate(Node node, Node[] max) {
        if (node.frequency != -1)
            check(node, max);
        for (char i = 'a'; i < 'z'; i++) {
            Node tmp = node.getNullableNode(i);
            if (tmp != null)
                iterate(tmp, max);
        }
    }
}
