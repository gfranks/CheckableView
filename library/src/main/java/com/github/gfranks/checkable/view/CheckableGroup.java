package com.github.gfranks.checkable.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class CheckableGroup extends LinearLayout implements CheckableView.OnCheckedChangeListener {

    /**
     * Int tracking the last checked CheckableView
     */
    private int mLastCheckedPosition;
    /**
     * Listener to receive onCheckedChange callbacks when a child CheckableView's checked state changes
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;
    /**
     * List of all the added CheckableViews
     */
    private List<CheckableView> mCheckableViews;

    public CheckableGroup(Context context) {
        super(context);
        mCheckableViews = new ArrayList<CheckableView>();
    }

    public CheckableGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCheckableViews = new ArrayList<CheckableView>();
    }

    /**
     *
     * @param onCheckedChangeListener Listener to receive onCheckedChange callbacks when a child CheckableView's checked state changes
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    /**
     *
     * @return Retrieve the position of the current checked CheckableView
     */
    public int getCheckedCheckableViewPosition() {
        for (int i=0; i<mCheckableViews.size(); i++) {
            if (mCheckableViews.get(i).isChecked()) {
                return i;
            }
        }

        return NO_ID;
    }

    /**
     *
     * @return Retrieve the current checked CheckableView
     */
    public CheckableView getCheckedCheckableView() {
        for (int i=0; i<mCheckableViews.size(); i++) {
            if (mCheckableViews.get(i).isChecked()) {
                return mCheckableViews.get(i);
            }
        }

        return null;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mLastCheckedPosition = mLastCheckedPosition;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mLastCheckedPosition = ss.mLastCheckedPosition;
    }

    @Override
    public void onCheckedChanged(CheckableView checkableView, boolean isChecked) {
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, checkableView, isChecked);
        }

        if (isChecked) {
            mLastCheckedPosition = mCheckableViews.indexOf(checkableView);
            updateCheckedPosition();
        }
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        CheckableView checkableView;
        if (child instanceof CheckableView) {
            checkableView = (CheckableView) child;

            if (!mCheckableViews.contains(checkableView)) {
                mCheckableViews.add(checkableView);
            }
            checkableView.setOnCheckedChangeListener(this);

            if (checkableView.isChecked()) {
                mLastCheckedPosition = mCheckableViews.indexOf(checkableView);
                updateCheckedPosition();
            }
        } else {
            if (child instanceof ViewGroup) {
                addAllCheckableViewsFromGroup((ViewGroup) child);
            }
        }

        return super.drawChild(canvas, child, drawingTime);
    }

    private void addAllCheckableViewsFromGroup(ViewGroup root) {
        final int childCount = root.getChildCount();
        CheckableView checkableView;
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                addAllCheckableViewsFromGroup((ViewGroup) child);
            }

            if (child instanceof CheckableView) {
                checkableView = (CheckableView) child;
                if (!mCheckableViews.contains(checkableView)) {
                    mCheckableViews.add(checkableView);
                }
                checkableView.setOnCheckedChangeListener(this);
                if (checkableView.isChecked()) {
                    mLastCheckedPosition = mCheckableViews.indexOf(checkableView);
                    updateCheckedPosition();
                }
            }
        }
    }

    private void updateCheckedPosition() {
        for (int i=0; i<mCheckableViews.size(); i++) {
            if (i != mLastCheckedPosition) {
                if (mCheckableViews.get(i).isChecked()) {
                    mCheckableViews.get(i).setChecked(false);
                }
            }
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        int mLastCheckedPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mLastCheckedPosition = in.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mLastCheckedPosition);
        }
    }

    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a CheckableView has changed.
         *
         * @param checkableGroup The CheckableGroup which the CheckableView belongs to
         * @param checkableView The CheckableView view whose state has changed.
         * @param isChecked  The new checked state of CheckableView.
         */
        void onCheckedChanged(CheckableGroup checkableGroup, CheckableView checkableView, boolean isChecked);
    }
}
