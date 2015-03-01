package com.github.gfranks.checkable.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckableView extends FrameLayout implements View.OnClickListener {

    private static final int DEFAULT_ANIMATION_DURATION = 300;
    private static final int DEFAULT_BORDER_WIDTH = 4;
    private static final int DEFAULT_BORDER_RADIUS = 12;

    public enum CheckPosition {
        TOP_LEFT,
        TOP_RIGHT,
        CENTER,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    /**
     * Container of the checked/normal images
     */
    private View mImageViewContainer;
    /**
     * ImageView which holds the checked image
     */
    private ImageView mCheckedImageView;
    /**
     * Resource Id of the checked image to be set
     */
    private int mCheckedImageResId;
    /**
     * Color used to set the color filter on the checked image view
     */
    private int mCheckedImageColor;
    /**
     * ImageView which holds the normal image
     */
    private ImageView mNormalImageView;
    /**
     * Resource Id of the normal image to be set
     */
    private int mNormalImageResId;
    /**
     * Color used to set the color filter on the normal image view
     */
    private int mNormalImageColor;
    /**
     * View used to indicate if the CheckableView is checked. Non-configurable.
     */
    private View mCheckedOverlay;
    /**
     * TextView to display the label
     */
    private TextView mLabelView;
    /**
     * String used as the label on the label text view
     */
    private String mLabel;
    /**
     * Color to be set on the label text view
     */
    private int mLabelTextColor;
    /**
     * Color used as the border color of the CheckableView
     */
    private int mBorderColor;
    /**
     * Width of the border of the CheckableView
     */
    private int mBorderWidth;
    /**
     * Radius to be used to define the corners of the border of the CheckableView
     */
    private float mBorderRadius;
    /**
     * Color used as the background when the state is unchecked
     */
    private int mNormalBackgroundColor;
    /**
     * Color used as the background when the state is checked
     */
    private int mCheckedBackgroundColor;
    /**
     * Duration used for all animation
     */
    private int mAnimationDuration;
    /**
     * Color of checkmark used in checked overlay
     */
    private int mCheckmarkColor;
    /**
     * Position of the checkable overlay
     */
    private CheckPosition mCheckmarkPosition;
    /**
     * The checked state of the CheckableView
     */
    private boolean mIsChecked;
    /**
     * OnCheckedChangeListener to receive callbacks when state has changed
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private boolean mIsInflated;
    private OnClickListener mOnClickListener;

    public CheckableView(Context context) {
        super(context);
        super.setOnClickListener(this);
        setTag(getClass().getName());
        mCheckedImageColor = context.getResources().getColor(R.color.cv_gray);
        mNormalImageColor = context.getResources().getColor(R.color.cv_gray_lightest);
        mLabelTextColor = context.getResources().getColor(R.color.cv_gray);
        mBorderColor = context.getResources().getColor(R.color.cv_gray_super_light);
        mBorderWidth = DEFAULT_BORDER_WIDTH;
        mBorderRadius = DEFAULT_BORDER_RADIUS;
        mNormalBackgroundColor = context.getResources().getColor(R.color.cv_gray_super_light);
        mCheckedBackgroundColor = context.getResources().getColor(R.color.cv_white);
        mAnimationDuration = DEFAULT_ANIMATION_DURATION;
        mCheckmarkColor = context.getResources().getColor(R.color.cv_green);
        mCheckmarkPosition = CheckPosition.TOP_RIGHT;
        mIsChecked = false;
        init();
    }

    public CheckableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckableView, defStyleAttr, 0);
        mCheckedImageResId = a.getResourceId(R.styleable.CheckableView_cv_checkedImage, -1);
        mNormalImageResId = a.getResourceId(R.styleable.CheckableView_cv_normalImage, -1);
        mCheckedImageColor = a.getColor(R.styleable.CheckableView_cv_checkedColor, context.getResources().getColor(R.color.cv_gray));
        mNormalImageColor = a.getColor(R.styleable.CheckableView_cv_normalColor, context.getResources().getColor(R.color.cv_gray_lightest));
        mLabel = a.getString(R.styleable.CheckableView_cv_label);
        mLabelTextColor = a.getColor(R.styleable.CheckableView_cv_labelTextColor, context.getResources().getColor(R.color.cv_gray));
        mBorderColor = a.getColor(R.styleable.CheckableView_cv_borderColor, context.getResources().getColor(R.color.cv_gray_super_light));
        mBorderWidth = a.getInt(R.styleable.CheckableView_cv_borderWidth, DEFAULT_BORDER_WIDTH);
        mBorderRadius = a.getFloat(R.styleable.CheckableView_cv_borderRadius, DEFAULT_BORDER_RADIUS);
        mNormalBackgroundColor = a.getColor(R.styleable.CheckableView_cv_normalBackgroundColor, context.getResources().getColor(R.color.cv_gray_super_light));
        mCheckedBackgroundColor = a.getColor(R.styleable.CheckableView_cv_checkedBackgroundColor, context.getResources().getColor(R.color.cv_white));
        mAnimationDuration = a.getInt(R.styleable.CheckableView_cv_animationDuration, DEFAULT_ANIMATION_DURATION);
        mCheckmarkColor = a.getColor(R.styleable.CheckableView_cv_checkmarkColor, context.getResources().getColor(R.color.cv_green));
        mCheckmarkPosition = CheckPosition.values()[a.getInt(R.styleable.CheckableView_cv_checkmarkPosition, CheckPosition.TOP_RIGHT.ordinal())];
        mIsChecked = a.getBoolean(R.styleable.CheckableView_cv_isChecked, false);
        a.recycle();

        init();
    }

    /**
     *
     * @param onCheckedChangeListener Listener used for state callbacks
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    /**
     *
     * @return The checked image view
     */
    public ImageView getCheckedImageView() {
        return mCheckedImageView;
    }
    /**
     *
     * @param imageResId Resource Id to be set as the checked image
     */
    public void setCheckedImageResource(int imageResId) {
        mCheckedImageResId = imageResId;
        getCheckedImageView().setImageResource(imageResId);
    }

    /**
     *
     * @param image Drawable to be set as the checked image
     */
    public void setCheckedImageDrawable(Drawable image) {
        getCheckedImageView().setImageDrawable(image);
    }

    /**
     *
     * @param checkedImageColor Color to be set as the color filter for the checked image. Only set if you wish to change the color of the image you provide
     */
    public void setCheckedImageColor(int checkedImageColor) {
        mCheckedImageColor = checkedImageColor;
        getCheckedImageView().setColorFilter(mCheckedImageColor, PorterDuff.Mode.SRC_IN);
    }

    /**
     *
     * @return The normal image view
     */
    public ImageView getNormalImageView() {
        return mNormalImageView;
    }

    /**
     *
     * @param imageResId Resource Id to be set as the normal image
     */
    public void setNormalImageResource(int imageResId) {
        mNormalImageResId = imageResId;
        getNormalImageView().setImageResource(imageResId);
    }

    /**
     *
     * @param image Drawable to be set as the normal image
     */
    public void setNormalImageDrawable(Drawable image) {
        getNormalImageView().setImageDrawable(image);
    }

    /**
     *
     * @param normalImageColor Color to be set as the color filter for the normal image. Only set if you wish to change the color of the image you provide
     */
    public void setNormalImageColor(int normalImageColor) {
        mNormalImageColor = normalImageColor;
        getNormalImageView().setColorFilter(mNormalImageColor, PorterDuff.Mode.SRC_IN);
    }

    /**
     *
     * @return TextView used as the label view for CheckableView
     */
    public TextView getLabelView() {
        return mLabelView;
    }

    /**
     *
     * @return String used as the label for the CheckableView
     * @see #setLabel(String)
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     *
     * @param labelResId String resource to be set as the label for the CheckableView
     */
    public void setLabel(int labelResId) {
        setLabel(getContext().getString(labelResId));
    }

    /**
     *
     * @param label String to be set as the label for the CheckableView
     */
    public void setLabel(String label) {
        mLabel = label;
        if (getLabel() != null) {
            getLabelView().setText(getLabel());
            getLabelView().setVisibility(View.VISIBLE);
            ((FrameLayout.LayoutParams) mImageViewContainer.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_image_container_margin) * 2;
        } else {
            getLabelView().setVisibility(View.GONE);
            ((FrameLayout.LayoutParams) mImageViewContainer.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_image_container_margin);
        }
        mImageViewContainer.requestLayout();
        initCheckableOverlayPosition();
    }

    /**
     *
     * @return Color used as the label text view color
     * @see #setLabelTextColor(int)
     */
    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    /**
     *
     * @param labelTextColor Color to be set as the label text view text color
     */
    public void setLabelTextColor(int labelTextColor) {
        mLabelTextColor = labelTextColor;
        mLabelView.setTextColor(getLabelTextColor());
    }

    /**
     *
     * @return Color used as the border border
     * @see #setBorderColor(int)
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     *
     * @param borderColor Color to be used as the border (if width exceeds 0)
     */
    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        initImageContainerBackground();
    }

    /**
     *
     * @return Width (in px) of the border wrapping the CheckableView
     * @see #setBorderWidth(int)
     */
    public int getBorderWidth() {
        return mBorderWidth;
    }

    /**
     *
     * @param borderWidth Width to be used for the border
     */
    public void setBorderWidth(int borderWidth) {
        mBorderWidth = borderWidth;
        initImageContainerBackground();
    }

    /**
     *
     * @return Radius used to draw the border corners
     * @see #setBorderRadius(float)
     */
    public float getBorderRadius() {
        return mBorderRadius;
    }

    /**
     *
     * @param borderRadius Radius to be used to define the corners of the border of the CheckableView
     */
    public void setBorderRadius(float borderRadius) {
        mBorderRadius = borderRadius;
    }

    /**
     *
     * @return Sets the color of the normal background to be displayed when unchecked
     * @see #setNormalBackgroundColor(int)
     */
    public int getNormalBackgroundColor() {
        return mNormalBackgroundColor;
    }

    /**
     *
     * @param normalBackgroundColor Color used as the background when the state is unchecked
     */
    public void setNormalBackgroundColor(int normalBackgroundColor) {
        mNormalBackgroundColor = normalBackgroundColor;
        initImageContainerBackground();
    }

    /**
     *
     * @return Sets the color of the normal background to be displayed when checked
     * @see #setCheckedBackgroundColor(int)
     */
    public int getCheckedBackgroundColor() {
        return mCheckedBackgroundColor;
    }

    /**
     *
     * @param checkedBackgroundColor Color used as the background when the state is checked
     */
    public void setCheckedBackgroundColor(int checkedBackgroundColor) {
        mCheckedBackgroundColor = checkedBackgroundColor;
        initImageContainerBackground();
    }

    /**
     *
     * @return Duration used for all animation
     * @see #setAnimationDuration(int)
     */
    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    /**
     *
     * @param animationDuration Duration to be used for all animation
     */
    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    /**
     *
     * @return Color used to color the checkmark in the checked overlay
     * @see #setCheckmarkColor(int)
     */
    public int getCheckmarkColor() {
        return mCheckmarkColor;
    }

    /**
     *
     * @param checkedmarkColor Color to be used to color the checkmark in the checked overlay
     */
    public void setCheckmarkColor(int checkedmarkColor) {
        mCheckmarkColor = checkedmarkColor;
        initCheckableOverlayBackground();
    }

    /**
     *
     * @return CheckPosition enum for the position of the checkmark
     * @see #setCheckmarkPosition(com.github.gfranks.checkable.view.CheckableView.CheckPosition)
     */
    public CheckPosition getCheckmarkPosition() {
        return mCheckmarkPosition;
    }

    /**
     *
     * @param checkmarkPosition CheckPosition enum to determine where the checkmark should be drawn
     * @see com.github.gfranks.checkable.view.CheckableView.CheckPosition
     */
    public void setCheckmarkPosition(CheckPosition checkmarkPosition) {
        mCheckmarkPosition = checkmarkPosition;
        initCheckableOverlayPosition();
    }

    /**
     *
     * @return Boolean determining state of CheckableView
     * @see #setChecked(boolean)
     */
    public boolean isChecked() {
        return mIsChecked;
    }

    /**
     *
     * @param isChecked Boolean to set the state of the CheckableView
     */
    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
        if (isChecked()) {
            animateChecked(mIsInflated);
        } else {
            animateUnchecked(mIsInflated);
        }

        if (mOnCheckedChangeListener != null && mIsInflated) {
            mOnCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
    }

    /**
     *
     * @param isChecked Boolean to set the state of the CheckableView without animation
     */
    public void forceSetChecked(boolean isChecked) {
        mIsChecked = isChecked;
        if (isChecked()) {
            animateChecked(false);
        } else {
            animateUnchecked(false);
        }

        if (mOnCheckedChangeListener != null && mIsInflated) {
            mOnCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
    }

    /**
     * Toggles the CheckableView
     */
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mIsChecked = mIsChecked;
        if (mLabel != null) {
            ss.mLabel = mLabel;
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mIsChecked = ss.mIsChecked;
        setLabel(ss.mLabel);
        if (isChecked()) {
            animateChecked(false);
        } else {
            animateUnchecked(false);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIsInflated = true;
    }

    private void init() {
        inflate(getContext(), R.layout.layout_checkable_view, this);

        mImageViewContainer = findViewById(R.id.checkable_view_image_container);
        mCheckedImageView = (ImageView) findViewById(R.id.checkable_view_checked_image);
        mNormalImageView = (ImageView) findViewById(R.id.checkable_view_normal_image);
        mCheckedOverlay = findViewById(R.id.checkable_view_checked_overlay);
        mLabelView = (TextView) findViewById(R.id.checkable_view_label);

        setCheckedImageColor(mCheckedImageColor);
        setNormalImageColor(mNormalImageColor);
        setCheckedImageResource(mCheckedImageResId);
        setNormalImageResource(mNormalImageResId);
        setLabel(mLabel);
        setLabelTextColor(mLabelTextColor);

        initBackgrounds();

        if (isChecked()) {
            animateChecked(false);
        } else {
            animateUnchecked(false);
        }
    }

    private void initBackgrounds() {
        initImageContainerBackground();
        initCheckableOverlayBackground();
        initCheckableOverlayPosition();
    }

    private void initImageContainerBackground() {
        GradientDrawable normalBackground = (GradientDrawable) getResources().getDrawable(R.drawable.bg_checkable_view);
        normalBackground.setColor(getNormalBackgroundColor());
        normalBackground.setStroke(getBorderWidth(), getBorderColor());
        normalBackground.setCornerRadius(getBorderRadius());
        GradientDrawable checkedBackground = (GradientDrawable) getResources().getDrawable(R.drawable.bg_checkable_view);
        checkedBackground.setColor(getCheckedBackgroundColor());
        checkedBackground.setStroke(getBorderWidth(), getBorderColor());
        checkedBackground.setCornerRadius(getBorderRadius());

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] {normalBackground, checkedBackground} );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mImageViewContainer.setBackground(transitionDrawable);
        } else {
            mImageViewContainer.setBackgroundDrawable(transitionDrawable);
        }
    }

    private void initCheckableOverlayBackground() {
        LayerDrawable checkedOverlayBackground = (LayerDrawable) getResources().getDrawable(R.drawable.bg_checked_overlay);
        ((GradientDrawable) checkedOverlayBackground.getDrawable(0)).setColor(getCheckedBackgroundColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkedOverlayBackground.getDrawable(1).setTint(getCheckmarkColor());
        } else {
            checkedOverlayBackground.getDrawable(1).mutate().setColorFilter(getCheckmarkColor(), PorterDuff.Mode.SRC_IN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCheckedOverlay.setBackground(checkedOverlayBackground);
        } else {
            mCheckedOverlay.setBackgroundDrawable(checkedOverlayBackground);
        }
    }

    private void initCheckableOverlayPosition() {
        switch (getCheckmarkPosition()) {
            case TOP_LEFT:
                ((LayoutParams) mCheckedOverlay.getLayoutParams()).gravity = Gravity.TOP|Gravity.START;
                break;
            case TOP_RIGHT:
                ((LayoutParams) mCheckedOverlay.getLayoutParams()).gravity = Gravity.TOP|Gravity.END;
                break;
            case CENTER:
                ((LayoutParams) mCheckedOverlay.getLayoutParams()).gravity = Gravity.CENTER;
                if (getLabel() != null) {
                    ((FrameLayout.LayoutParams) mCheckedOverlay.getLayoutParams()).bottomMargin = (getResources().getDimensionPixelSize(R.dimen.checkable_overlay_margin) / 2) +
                            (getResources().getDimensionPixelSize(R.dimen.checkable_image_container_margin) / 2);
                } else {
                    ((FrameLayout.LayoutParams) mCheckedOverlay.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_overlay_margin) / 2;
                }
                break;
            case BOTTOM_LEFT:
                ((LayoutParams) mCheckedOverlay.getLayoutParams()).gravity = Gravity.BOTTOM|Gravity.START;
                if (getLabel() != null) {
                    ((FrameLayout.LayoutParams) mCheckedOverlay.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_overlay_margin) +
                            getResources().getDimensionPixelSize(R.dimen.checkable_image_container_margin);
                } else {
                    ((FrameLayout.LayoutParams) mCheckedOverlay.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_overlay_margin);
                }
                break;
            case BOTTOM_RIGHT:
                ((LayoutParams) mCheckedOverlay.getLayoutParams()).gravity = Gravity.BOTTOM|Gravity.END;
                if (getLabel() != null) {
                    ((FrameLayout.LayoutParams) mCheckedOverlay.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_overlay_margin) +
                            getResources().getDimensionPixelSize(R.dimen.checkable_image_container_margin);
                } else {
                    ((FrameLayout.LayoutParams) mCheckedOverlay.getLayoutParams()).bottomMargin = getResources().getDimensionPixelSize(R.dimen.checkable_overlay_margin);
                }
                break;
        }
    }

    private void animateChecked(boolean animate) {
        if (animate) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    getImageTranslateAnimator(getCheckedImageView(), -mImageViewContainer.getMeasuredHeight(), 0),
                    getImageTranslateAnimator(getNormalImageView(), 0, mImageViewContainer.getMeasuredHeight()));
            set.start();
            startCheckedOverlayAnimation(true);
            startBackgroundTransition(true);
        } else {
            measure(MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            getCheckedImageView().setTranslationY(0);
            float translationY = getMeasuredHeight();
            if (getLabel() != null) {
                translationY *=2;
            }
            getNormalImageView().setTranslationY(translationY);
            mCheckedOverlay.setVisibility(View.VISIBLE);
            ((TransitionDrawable) mImageViewContainer.getBackground()).startTransition(0);
        }
    }

    private void animateUnchecked(boolean animate) {
        if (animate) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    getImageTranslateAnimator(getCheckedImageView(), 0, -mImageViewContainer.getMeasuredHeight()),
                    getImageTranslateAnimator(getNormalImageView(), mImageViewContainer.getBottom(), 0));
            set.start();
            startCheckedOverlayAnimation(false);
            startBackgroundTransition(false);
        } else {
            measure(MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            float translationY = -getMeasuredHeight();
            if (getLabel() != null) {
                translationY *=2;
            }
            getCheckedImageView().setTranslationY(translationY);
            getNormalImageView().setTranslationY(0);
            mCheckedOverlay.setVisibility(View.GONE);
            ((TransitionDrawable) mImageViewContainer.getBackground()).resetTransition();
        }
    }

    private Animator getImageTranslateAnimator(final View view, float fromY, float toY) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setDuration(mIsInflated ? getAnimationDuration() : 0);
        animator.setInterpolator(new OvershootInterpolator(1.5f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        return animator;
    }

    private void startCheckedOverlayAnimation(final boolean scaleUp) {
        ScaleAnimation animation = new ScaleAnimation(scaleUp ? 0 : 1,
                scaleUp ? 1 : 0,
                scaleUp ? 0 : 1,
                scaleUp ? 1 : 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(mIsInflated ? getAnimationDuration() : 0);
        animation.setStartOffset(scaleUp ? (getAnimationDuration() / 6) : (getAnimationDuration() / 4));
        if (scaleUp) {
            animation.setInterpolator(new OvershootInterpolator(4f));
        } else {
            animation.setInterpolator(new AnticipateInterpolator(4f));
        }
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (scaleUp) {
                    mCheckedOverlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!scaleUp) {
                    mCheckedOverlay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCheckedOverlay.startAnimation(animation);
    }

    private void startBackgroundTransition(boolean isChecking) {
        if (isChecking) {
            ((TransitionDrawable) mImageViewContainer.getBackground()).startTransition(mIsInflated ? getAnimationDuration() / 2 : 0);
        } else {
            ((TransitionDrawable) mImageViewContainer.getBackground()).reverseTransition(mIsInflated ? getAnimationDuration() / 2 : 0);
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

        boolean mIsChecked;
        String mLabel;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mIsChecked = in.readInt() == 1;
            mLabel = in.readString();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mIsChecked ? 1 : 0);
            out.writeString(mLabel);
        }
    }

    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a CheckableView has changed.
         *
         * @param checkableView The CheckableView view whose state has changed.
         * @param isChecked  The new checked state of CheckableView.
         */
        void onCheckedChanged(CheckableView checkableView, boolean isChecked);
    }
}
