package cn.jianke.customdrawableanimation.module;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import cn.jianke.customdrawableanimation.R;
import cn.jianke.customdrawableanimation.common.FrameAnimImageView;

/**
 * @className: MainActivity
 * @classDescription: 优化帧动画测试页面
 * @author: leibing
 * @createTime: 2016/12/19
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    // 动画图片控件
    private ImageView frameAnimIv;
    // 帧动画工具
    private FrameAnimImageView mFrameAnimImageView;
    // 图片资源ID列表
    private List<Integer> mResourceIdList;
    // DrawableAnim动画
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // findView
        frameAnimIv = (ImageView) findViewById(R.id.iv_frame_anim);
        // init FrameAnimImageView
        mFrameAnimImageView = new FrameAnimImageView();
        // 初始化图片资源ID列表
        mResourceIdList = new ArrayList<>();
        // 添加图片资源
        mResourceIdList.add(R.drawable.refresh_gif0000);
        mResourceIdList.add(R.drawable.refresh_gif0001);
        mResourceIdList.add(R.drawable.refresh_gif0002);
        mResourceIdList.add(R.drawable.refresh_gif0003);
        mResourceIdList.add(R.drawable.refresh_gif0004);
        mResourceIdList.add(R.drawable.refresh_gif0005);
        mResourceIdList.add(R.drawable.refresh_gif0006);
        mResourceIdList.add(R.drawable.refresh_gif0007);
        mResourceIdList.add(R.drawable.refresh_gif0008);
        // onClick
        findViewById(R.id.btn_drawble_anim).setOnClickListener(this);
        findViewById(R.id.btn_switch_img).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        // 停止播放动画
        stopDrawableAnim();
        stopSwitchImgAnim();
        super.onDestroy();
    }

    /**
     * 启动图片切换帧动画
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param mResourceIdList 图片资源ID列表
     * @return
     */
    private void startSwitchImgAnim(List<Integer> mResourceIdList){
        // 若图片资源ID列表为空则返回
        if (mResourceIdList == null || mResourceIdList.size() == 0)
            return;
        // 设置图片资源
        mFrameAnimImageView.setAnimation(frameAnimIv, mResourceIdList);
        // 开始播放动画
        mFrameAnimImageView.start(true, 100);
    }

    /**
     * 停止图片切换帧动画
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param
     * @return
     */
    private void stopSwitchImgAnim(){
        if (mFrameAnimImageView != null) {
            mFrameAnimImageView.stop();
        }
    }

    /**
     * 启动DrawableAnim实现的帧动画
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param
     * @return
     */
    private void startDrawableAnim(){
        if (frameAnimIv == null)
            return;
        // 设置动画
        frameAnimIv.setImageResource(R.drawable.pull_to_refresh_loading);
        animationDrawable = (AnimationDrawable) frameAnimIv.getDrawable();
        // 启动动画
        animationDrawable.start();
    }

    /**
     * 停止DrawableAnim实现的帧动画
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param
     * @return
     */
    private void stopDrawableAnim(){
        if (animationDrawable == null)
            return;
        animationDrawable.stop();
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); ++i){
            Drawable frame = animationDrawable.getFrame(i);
            if (frame instanceof BitmapDrawable) {
                ((BitmapDrawable)frame).getBitmap().recycle();
            }
            frame.setCallback(null);
        }
        animationDrawable.setCallback(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_drawble_anim:
                // DrawableAnim方式实现动画
                startDrawableAnim();
                break;
            case R.id.btn_switch_img:
                // 切换图片实现动画
                startSwitchImgAnim(mResourceIdList);
                break;
            default:
                break;
        }
    }
}
