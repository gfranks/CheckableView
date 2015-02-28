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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CheckableView extends FrameLayout implements View.OnClickListener {

    private static final int DEFAULT_ANIMATION_DURATION = 300;

    /**
     * The checked state of the CheckableView
     */
    private boolean mIsChecked;
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
     * OnCheckedChangeListener to receive callbacks when state has changed
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private boolean mIsInflated;
    private OnClickListener mOnClickListener;

    public CheckableView(Context context) {
        super(context);
        super.setOnClickListener(this);
        mAnimationDuration = DEFAULT_ANIMATION_DURATION;
        init();
    }

    public CheckableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckableView, defStyleAttr, 0);
        mIsChecked = a.getBoolean(R.styleable.CheckableView_isChecked, false);
        mCheckedImageResId = a.getResourceId(R.styleable.CheckableView_checkedImage, -1);
        mNormalImageResId = a.getResourceId(R.styleable.CheckableView_normalImage, -1);
        mCheckedImageColor = a.getColor(R.styleable.CheckableView_checkedColor, context.getResources().getColor(R.color.theme_gray_light));
        mNormalImageColor = a.getColor(R.styleable.CheckableView_normalColor, context.getResources().getColor(R.color.theme_gray_lightest));
        mBorderColor = a.getColor(R.styleable.CheckableView_borderColor, context.getResources().getColor(R.color.theme_gray_light));
        mBorderWidth = a.getInt(R.styleable.CheckableView_borderWidth, 5);
        mBorderRadius = a.getFloat(R.styleable.CheckableView_borderRadius, 12);
        mNormalBackgroundColor = a.getColor(R.styleable.CheckableView_normalBackgroundColor, context.getResources().getColor(R.color.theme_gray_super_light));
        mCheckedBackgroundColor = a.getColor(R.styleable.CheckableView_checkedBackgroundColor, context.getResources().getColor(R.color.theme_white));
        mAnimationDuration = a.getInt(R.styleable.CheckableView_animationDuration, DEFAULT_ANIMATION_DURATION);
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
        initBackgrounds();
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
        initBackgrounds();
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
        initBackgrounds();
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
        initBackgrounds();
    }

    /**
     *
     * @return Duration used for all animation
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
     * @return Boolean determing state of CheckableView
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
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mIsChecked = ss.mIsChecked;
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

        setCheckedImageColor(mCheckedImageColor);
        setNormalImageColor(mNormalImageColor);
        setCheckedImageResource(mCheckedImageResId);
        setNormalImageResource(mNormalImageResId);

        initBackgrounds();

        if (isChecked()) {
            animateChecked(false);
        } else {
            animateUnchecked(false);
        }
    }

    private void initBackgrounds() {
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

        LayerDrawable checkedOverlayBackground = (LayerDrawable) getResources().getDrawable(R.drawable.bg_checked_overlay);
        ((GradientDrawable) checkedOverlayBackground.getDrawable(0)).setColor(getCheckedBackgroundColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCheckedOverlay.setBackground(checkedOverlayBackground);
        } else {
            mCheckedOverlay.setBackgroundDrawable(checkedOverlayBackground);
        }
    }

    private void animateChecked(boolean animate) {
        if (animate) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    getTranslateAnimator(getCheckedImageView(), -mImageViewContainer.getMeasuredHeight(), 0),
                    getTranslateAnimator(getNormalImageView(), 0, mImageViewContainer.getMeasuredHeight()));
            set.start();
            startCheckedOverlayAnimation(true);
            startBackgroundTransition(true);
        } else {
            measure(MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            getCheckedImageView().setTranslationY(0);
            getNormalImageView().setTranslationY(getMeasuredHeight());
            mCheckedOverlay.setVisibility(View.VISIBLE);
            ((TransitionDrawable) mImageViewContainer.getBackground()).startTransition(0);
        }
    }

    private void animateUnchecked(boolean animate) {
        if (animate) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    getTranslateAnimator(getCheckedImageView(), 0, -mImageViewContainer.getMeasuredHeight()),
                    getTranslateAnimator(getNormalImageView(), mImageViewContainer.getBottom(), 0));
            set.start();
            startCheckedOverlayAnimation(false);
            startBackgroundTransition(false);
        } else {
            measure(MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            getCheckedImageView().setTranslationY(-getMeasuredHeight());
            getNormalImageView().setTranslationY(0);
            mCheckedOverlay.setVisibility(View.GONE);
            ((TransitionDrawable) mImageViewContainer.getBackground()).resetTransition();
        }
    }

    private Animator getTranslateAnimator(final View view, float fromY, float toY) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setDuration(mIsInflated ? getAnimationDuration() : 0);
        animator.setInterpolator(new OvershootInterpolator(1.5f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationY((float) animation.getAnimatedValue());
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

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mIsChecked = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mIsChecked ? 1 : 0);
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
