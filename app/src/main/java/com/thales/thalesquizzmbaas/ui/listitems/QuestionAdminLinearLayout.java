package com.thales.thalesquizzmbaas.ui.listitems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.thales.thalesquizzmbaas.model.Question;
import com.thales.thalesquizzmbaas.ui.R;
import de.greenrobot.event.EventBus;

/**
 * Line to manage a question
 */
public class QuestionAdminLinearLayout extends LinearLayout {

    @InjectView(R.id.mQuestionTextTextView)
    TextView mQuestionTextTextView;


    @InjectView(R.id.mPublishQuestion)
    Button mPublishQuestion;

    @InjectView(R.id.mDeleteQuestion)
    Button mDeleteQuestion;

    @InjectView(R.id.mStopQuestion)
    Button mStopQuestion;

    EventBus mBus;
    Question mQuestion;
    int mIndex;

    private class QuestionAdminLinearLayoutEvent {
        public Question question;
        public int index;
    }


    public QuestionAdminLinearLayout(Context context) {
        super(context);
        init();
    }

    public QuestionAdminLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionAdminLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mBus = EventBus.getDefault();
    }

    public void setQuestion(Question question, int i) {
        if (mDeleteQuestion == null) {
            ButterKnife.inject(this, this);
        }
        mQuestion = question;
        mIndex = i;
        if (question != null) {
            mQuestionTextTextView.setText(mQuestion.getQuestionText());
        }
    }

    public void modeLive(){
        mPublishQuestion.setVisibility(View.VISIBLE);
        mDeleteQuestion.setVisibility(View.GONE);
        mStopQuestion.setVisibility(View.VISIBLE);
    }

    public void modeoffline(){
        mPublishQuestion.setVisibility(View.GONE);
        mDeleteQuestion.setVisibility(View.VISIBLE);
        mStopQuestion.setVisibility(View.GONE);
    }

    public class PublishQuestionEvent extends QuestionAdminLinearLayoutEvent {
    }


    @OnClick(R.id.mPublishQuestion)
    public void onPublishClicked() {
        if (mDeleteQuestion == null) {
            ButterKnife.inject(this, this);
        }
        PublishQuestionEvent event = new PublishQuestionEvent();
        event.question = mQuestion;
        event.index = mIndex;
        mBus.post(event);
    }


    public class DeleteQuestionEvent extends QuestionAdminLinearLayoutEvent {
    }

    @OnClick(R.id.mDeleteQuestion)
    public void onDeleteClicked() {
        if (mDeleteQuestion == null) {
            ButterKnife.inject(this, this);
        }
        DeleteQuestionEvent event = new DeleteQuestionEvent();
        event.question = mQuestion;
        event.index = mIndex;
        mBus.post(event);
    }

    public class StopQuestionEvent extends QuestionAdminLinearLayoutEvent {
    }

    @OnClick(R.id.mStopQuestion)
    public void onStopClicked() {
        if (mDeleteQuestion == null) {
            ButterKnife.inject(this, this);
        }
        StopQuestionEvent event = new StopQuestionEvent();
        event.question = mQuestion;
        event.index = mIndex;
        mBus.post(event);
    }

}
