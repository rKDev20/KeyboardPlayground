package com.rk.keyboardplayground.service;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.rk.keyboardplayground.customViews.CandidateView;
import com.rk.keyboardplayground.customViews.KeyboardLayout;
import com.rk.keyboardplayground.util.IterativeWordSuggestion;
import com.rk.keyboardplayground.util.SPManager;


public class CustomInputMethod extends InputMethodService implements KeyboardLayout.KeyboardListener, CandidateView.CandidateListener {
    private final static String TAG = "Playground";
    private StringBuilder currentWord;
    private KeyboardLayout keyboardLayout;
    private CandidateView candidateLayout;
    private InputConnection inputConnection;
    private IterativeWordSuggestion iterativeWordSuggestion;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
        currentWord = new StringBuilder();
        iterativeWordSuggestion = new IterativeWordSuggestion(getApplicationContext());
        super.onCreate();
    }

    @Override
    public void onInitializeInterface() {
        Log.d(TAG, "onInitialiseInterface called");
    }


    @Override
    public View onCreateInputView() {
        Log.d(TAG, "onCreateInputView called");
        keyboardLayout = new KeyboardLayout(getApplicationContext());
        keyboardLayout.setKeyboardListener(this);
        setCandidatesViewShown(true);
        return keyboardLayout;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d(TAG, "onCreateCandidateView called");
        candidateLayout = new CandidateView(getApplicationContext());
        candidateLayout.setWordSelectedListener(this);
        return candidateLayout;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.d(TAG,"onStartInputView");
        keyboardLayout.setSize(SPManager.getSize(this));
        inputConnection = getCurrentInputConnection();
        keyboardLayout.reset();
    }

    @Override
    public void onFinishInput() {
        Log.d(TAG, "onFinishInput called");
        super.onFinishInput();
    }

    @Override
    public void onDestroy() {
        iterativeWordSuggestion.close();
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onCharacterKey(char a) {
        inputConnection.commitText(String.valueOf(a), 1);
        currentWord.append(a);
        candidateLayout.setSuggestion(iterativeWordSuggestion.getSuggestion(a));
    }

    @Override
    public void onBackspace() {
        inputConnection.deleteSurroundingText(1, 0);
        candidateLayout.setSuggestion(null);
        iterativeWordSuggestion.reset();
        try {
            currentWord.deleteCharAt(currentWord.length() - 1);
        } catch (StringIndexOutOfBoundsException e) {
            currentWord = new StringBuilder();
        }
    }

    @Override
    public void onComputeInsets(InputMethodService.Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }

    @Override
    public void onSpace() {
        inputConnection.commitText(" ", 1);
        candidateLayout.setSuggestion(null);
        iterativeWordSuggestion.reset();
        currentWord = new StringBuilder();
    }

    @Override
    public void onCap() {
        //TODO
    }

    @Override
    public void onReturn() {
        inputConnection.commitText("\n", 1);
        currentWord = new StringBuilder();
    }

    @Override
    public void onSelectSuggestion(String s) {
        inputConnection.commitText(s.substring(currentWord.length()) + " ", 1);
        iterativeWordSuggestion.reset();
        iterativeWordSuggestion.updateFrequency(s);
        currentWord = new StringBuilder();
    }
}
