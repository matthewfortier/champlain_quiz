package com.matthewfortier.champlainquiz;

import java.util.ArrayList;

/**
 * Created by matthewfortier on 9/27/17.
 */

public class Question {

    private String mQuestion;
    private String mAnswer;
    private ArrayList<String> mOptions;
    private String mImageId;
    private boolean mCorrect = false;
    private String mType;

    public Question(String mQuestion, String mAnswer, ArrayList<String> mOptions, String mType) {
        this.mQuestion = mQuestion;
        this.mAnswer = mAnswer;
        this.mOptions = mOptions;
        this.mType = mType;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public ArrayList<String> getOptions() {
        return mOptions;
    }

    public String getImageId() {
        return mImageId;
    }

    public void setImageId(String mImageId) {
        this.mImageId = mImageId;
    }

    public boolean isCorrect() {
        return mCorrect;
    }

    public void setCorrect(boolean mCorrect) {
        this.mCorrect = mCorrect;
    }

    public void setOptions(ArrayList<String> mOptions) {
        this.mOptions = mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String mAnswer) {
        this.mAnswer = mAnswer;
    }
}
