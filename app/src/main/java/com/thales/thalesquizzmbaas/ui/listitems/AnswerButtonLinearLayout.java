package com.thales.thalesquizzmbaas.ui.listitems;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.thales.thalesquizzmbaas.ui.R;
import de.greenrobot.event.EventBus;


public class AnswerButtonLinearLayout extends LinearLayout {

    @InjectView(R.id.mAnswerQuestionButton)
    Button mAnswerQuestionButton;

    EventBus mBus;
    String mAnswer;

    public AnswerButtonLinearLayout(Context context) {
        super(context);
        init();
    }

    public AnswerButtonLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnswerButtonLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mBus = EventBus.getDefault();
    }

    public void setAnswer(String answer) {
        if (mAnswerQuestionButton == null) {
            ButterKnife.inject(this, this);
        }
        mAnswer = answer;
        if (answer != null) {
            mAnswerQuestionButton.setText(answer);
        }
    }

    public class AnwserButtonLinearLayoutEvent {
        public String answer;
    }

    @OnClick(R.id.mAnswerQuestionButton)
    public void onPublishClicked() {
        if (mAnswerQuestionButton == null) {
            ButterKnife.inject(this, this);
        }
        AnwserButtonLinearLayoutEvent event = new AnwserButtonLinearLayoutEvent();
        event.answer = mAnswer;
        mBus.post(event);
    }



}
