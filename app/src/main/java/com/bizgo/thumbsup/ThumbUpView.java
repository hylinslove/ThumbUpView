package com.bizgo.thumbsup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ThumbUpView extends FrameLayout {

    //上下文
    private Context mContext;
    //控件宽度
    private double mWidth;
    //控件长度
    private double mHeight;
    //动画时长
    private int animateDuration = 3000;
    //点赞图片出现时长
    private int showDuration = 1000;
    //随机工具
    private Random mRandom = new Random();
    //动画图片资源
    private final List<Integer> imageResList = new ArrayList<>();
    //正在进行中的动画
    private List<AnimatorSet> animatorSets = new ArrayList<>();


    public ThumbUpView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public ThumbUpView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.initTypeArray(attrs);
    }

    private void initTypeArray(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ThumbUpView);
        animateDuration = typedArray.getInteger(R.styleable.ThumbUpView_animate_duration, 3000);
        showDuration = typedArray.getInteger(R.styleable.ThumbUpView_show_duration, 1000);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
    }

    /**
     * 添加图片资源
     *
     * @param drawableId 图片id
     */
    public void addImageRes(Integer drawableId) {
        this.imageResList.add(drawableId);
    }

    /**
     * 添加图片资源
     *
     * @param drawableIds 图片id集合
     */
    public void addImageRes(Integer... drawableIds) {
        this.imageResList.addAll(Arrays.asList(drawableIds));
    }

    /**
     * 添加图片资源
     *
     * @param drawableIds 图片id集合
     */
    public void addImageResList(List<Integer> drawableIds) {
        this.imageResList.addAll(drawableIds);
    }

    /**
     * 点击点赞，添加一个点赞动画
     */
    public void addFavor() {
        //非空验证
        if (imageResList.isEmpty()) {
            throw new NullPointerException("no image found");
        }
        //随机获取一个图片
        int drawableId = imageResList.get(mRandom.nextInt(imageResList.size()));
        //生成配置参数
        LayoutParams layoutParams = createLayoutParams(drawableId);
        //创建一个image view
        AppCompatImageView favorView = new AppCompatImageView(mContext);
        favorView.setImageResource(drawableId);
        //开始动画
        this.start(favorView, layoutParams);
    }

    /**
     * 生成同配置参数
     */
    private LayoutParams createLayoutParams(int drawableId) {
        //获取图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //只读图片，不加载到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mContext.getResources(), drawableId, options);
        //获取图片宽高
        int picWidth = options.outWidth;
        int picHeight = options.outHeight;

        return new LayoutParams(picWidth, picHeight, Gravity.BOTTOM | Gravity.CENTER);
    }

    /**
     * 开始执行动画
     *
     * @param favorView    点赞动画图片
     * @param layoutParams 配置参数
     */
    private void start(final AppCompatImageView favorView, LayoutParams layoutParams) {
        //设置进入动画
        AnimatorSet enterAnimator = generateEnterAnim(favorView);
        //设置路径动画
        AnimatorSet pathAnimator = generatePathAnim(favorView, layoutParams.width, layoutParams.height);
        //设置消失动画
        AnimatorSet exitAnimator = generateExitAnim(favorView);

        //执行动画合集
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(pathAnimator, enterAnimator, exitAnimator);
        animatorSet.addListener(new AnimationEndListener(favorView, animatorSet));
        animatorSet.start();

        super.addView(favorView, layoutParams);

    }

    /**
     * 进入动画
     *
     * @param child child
     * @return AnimatorSet
     */
    private AnimatorSet generateEnterAnim(View child) {
        AnimatorSet enterAnimation = new AnimatorSet();
        enterAnimation.playTogether(
                ObjectAnimator.ofFloat(child, ALPHA, 0.2f, 1f),
                ObjectAnimator.ofFloat(child, SCALE_X, 0.2f, 1f),
                ObjectAnimator.ofFloat(child, SCALE_Y, 0.2f, 1f)
        );
        enterAnimation.setInterpolator(new LinearInterpolator());
        return enterAnimation.setDuration(showDuration);
    }

    /**
     * 路径动画
     *
     * @param child child
     * @return AnimatorSet
     */
    private AnimatorSet generatePathAnim(View child, int picWidth, int picHeight) {
        AnimatorSet pathAnimation = new AnimatorSet();

        //左右平移方案
//        ObjectAnimator moveY = ObjectAnimator.ofFloat(child, TRANSLATION_Y, 0f, (float) -mHeight + picHeight);
//        ObjectAnimator moveX;
//        int randomX = mRandom.nextInt(10);
//        float offX = (float) (mWidth / 2) - (float) (picWidth / 2);
//        if (randomX % 3 == 0) {
//            moveX = ObjectAnimator.ofFloat(child, TRANSLATION_X, 0f, -offX, offX);
//        } else if (randomX % 4 == 0) {
//            moveX = ObjectAnimator.ofFloat(child, TRANSLATION_X, 0f, offX, -offX);
//        } else if (randomX % 2 == 0) {
//            moveX = ObjectAnimator.ofFloat(child, TRANSLATION_X, 0f, -offX, offX, -offX);
//        } else {
//            moveX = ObjectAnimator.ofFloat(child, TRANSLATION_X, 0f, offX, -offX, offX);
//        }
//        pathAnimation.playTogether(
//                moveY, moveX
//        );

        //贝塞尔方案
        Path path = new Path();
        path.moveTo(0, 0);
        int randomX = mRandom.nextInt(11);
        float offX = (float) (mWidth - picWidth);
        float offY = -(float) ((mHeight - picHeight) / 4);

        if(randomX == 0) {//概率 1/11
            path.lineTo(0, offY * 4);
        } else if (randomX % 3 == 0) { //概率 3/11
            path.cubicTo(-offX, offY * 2, offX, offY * 2, 0, offY * 4);
        } else if (randomX % 4 == 0) { //概率 2/11
            path.cubicTo(offX, offY * 2, -offX, offY * 2, 0, offY * 4);
        } else if (randomX % 2 == 0) { //概率 2/11
            path.quadTo(-offX, offY * 2, 0, offY * 4);
        } else { //概率 3/11
            path.quadTo(offX, offY * 2, 0, offY * 4);
        }

        ObjectAnimator move = ObjectAnimator.ofFloat(child, TRANSLATION_X, TRANSLATION_Y, path);
        pathAnimation.play(move);
        pathAnimation.setInterpolator(new LinearInterpolator());
        return pathAnimation.setDuration(animateDuration);
    }


    /**
     * 进入动画
     *
     * @param child child
     * @return AnimatorSet
     */
    private AnimatorSet generateExitAnim(View child) {
        AnimatorSet enterAnimation = new AnimatorSet();
        enterAnimation.playTogether(
                ObjectAnimator.ofFloat(child, ALPHA, 1f, 1f, 1f, 1f, 0f)
        );
        enterAnimation.setInterpolator(new LinearInterpolator());
        return enterAnimation.setDuration(animateDuration);
    }


    protected class AnimationEndListener extends AnimatorListenerAdapter {
        private View child;
        private final AnimatorSet animatorSet;

        protected AnimationEndListener(View child, AnimatorSet animatorSet) {
            this.child = child;
            this.animatorSet = animatorSet;
            animatorSets.add(animatorSet);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            removeView(child);
            animatorSets.remove(this.animatorSet);
            child = null;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.destroy();
    }

    private void destroy() {
        this.removeAllViews();
        if (animatorSets != null) {
            for (AnimatorSet animatorSet : animatorSets) {
                animatorSet.getListeners().clear();
                animatorSet.cancel();
            }
            animatorSets.clear();
            animatorSets = null;
        }
    }
}
