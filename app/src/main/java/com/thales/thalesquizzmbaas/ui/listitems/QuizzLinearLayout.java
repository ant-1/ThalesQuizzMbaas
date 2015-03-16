package com.thales.thalesquizzmbaas.ui.listitems;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.thales.thalesquizzmbaas.mbass.Mbaas;
import com.thales.thalesquizzmbaas.model.Question;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.ui.R;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;


public class QuizzLinearLayout extends LinearLayout {

    @InjectView(R.id.mQuizzNameTextView)
    TextView mQuizzNameTextView;


    @InjectView(R.id.mQuizzStatusImageView)
    ImageView mQuizzStatusImageView;

    @InjectView(R.id.mQuizzTeaserTextView)
    TextView mQuizzTeaserTextView;

    @InjectView(R.id.mQuizzAdminTextView)
    TextView mQuizzAdminTextView;

    Quizz mQuizz;

    private class QuestionAdminLinearLayoutEvent {
        public Question question;
        public int index;
    }


    public QuizzLinearLayout(Context context) {
        super(context);
        init();
    }

    public QuizzLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuizzLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    public void setQuizz(Quizz quizz) {
        if (mQuizzStatusImageView == null) {
            ButterKnife.inject(this, this);
        }
        mQuizz = quizz;
        if (mQuizz.getStatus() != null && mQuizz.getStatus().equals(ThalesQuizzMbaasConstants.STATUS_STARTED)) {
            mQuizzStatusImageView.setImageResource(R.drawable.on);
        } else {
            mQuizzStatusImageView.setImageResource(R.drawable.off);
        }
        mQuizzNameTextView.setText(mQuizz.getName());
        mQuizzTeaserTextView.setText(mQuizz.getTeaser());
        if (mQuizz.getCreatorId() != null && mQuizz.getCreatorId().equals(Mbaas.getInstance().getUserId())) {
            mQuizzAdminTextView.setText(getContext().getString(R.string.quizz_admin));
        } else {
            mQuizzAdminTextView.setText(getContext().getString(R.string.quizz_play_it));
        }
    }

}
