package com.thales.thalesquizzmbaas.ui.listitems;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.thales.thalesquizzmbaas.ui.R;

public class QCMAnswerResultTotalRelativeLayout extends RelativeLayout {

    @InjectView(R.id.mQCMAnswerResultTextView)
    TextView mQCMAnswerResultTextView;

    @InjectView(R.id.mQCMAnswerValueTextView)
    TextView mQCMAnswerValueTextView;

    @InjectView(R.id.mQCMAnswerResultRelativeLayout)
    RelativeLayout mQCMAnswerResultRelativeLayout;

    public QCMAnswerResultTotalRelativeLayout(Context context) {
        super(context);
        init();
    }

    public QCMAnswerResultTotalRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QCMAnswerResultTotalRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    public void setResult(long count, long total, String answer) {
        if (mQCMAnswerResultRelativeLayout == null) {
            ButterKnife.inject(this, this);
        }
        int resultSize = 0;
        if (total > 0 && count > 0) {
            int totalSize = getWidth();
            resultSize = ((int) (totalSize * count / total)) - 2;
            ViewGroup.LayoutParams layoutParams = mQCMAnswerResultRelativeLayout.getLayoutParams();
            layoutParams.width = resultSize;
            int[] colors = new int[2];
            colors[0] = Color.argb(255, 0, 0, 255);
            colors[1] = Color.argb(255, 0, 0, (int) (255 * count / total));
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
            mQCMAnswerResultRelativeLayout.setBackground(gradientDrawable);
            mQCMAnswerResultRelativeLayout.setLayoutParams(layoutParams);
        }
        mQCMAnswerResultRelativeLayout.invalidate();
        mQCMAnswerValueTextView.setText(answer);
        mQCMAnswerResultTextView.setText(count + "/" + total);
    }

}
