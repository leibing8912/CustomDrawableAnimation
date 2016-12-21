package cn.jianke.customdrawableanimation.common;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @className: FrameAnimImageView
 * @classDescription: 帧动画优化（根据图片之间切换的时间间隔来定时设置ImageView的背景图片，
 * 这样始终只是一个ImageView实例，更换的只是其背景，占用内存会比AnimationDrawable小很多）
 * @author: leibing
 * @createTime: 2016/12/19
 */
public class FrameAnimImageView {
    // why use this to  replace drawableAnim
    /**
     * AnimationDrawable也是一个耗内存大户，图片帧数越多耗内存越大，
     * 具体可以查看AnimationDrawable的源码，在AnimationDrawable实例化的时候，
     * Drawable的createFromXmlInner方法会调用AnimationDrawable的inflate方法，
     * 该方法里面有一个while循环去一次性将所有帧都读取出来，
     * 也就是在初始化的时候就将所有的帧读在内存中了，有多少张图片，它就要消耗对应大小的内存。
     *
     * 根据图片之间切换的时间间隔来定时设置ImageView的背景图片，
     * 这样始终只是一个ImageView实例，更换的只是其背景，占用内存会比AnimationDrawable小很多。
     */
    // 消息（开始）
    private static final int MSG_START = 0xf1;
    // 消息（停止）
    private static final int MSG_STOP  = 0xf2;
    // 运行状态(停止)
    private static final int STATE_STOP = 0xf3;
    // 运行状态(正在运行)
    private static final int STATE_RUNNING = 0xf4;
    // 运行状态
    private int mState = STATE_RUNNING;
    // 图片
    private ImageView mImageView;
    // 图片资源ID列表
    private List<Integer> mResourceIdList = null;
    // 定时任务
    private Timer mTimer = null;
    private AnimTimerTask mTimeTask = null;
    // 记录播放位置
    private int mFrameIndex = 0;
    // 播放形式
    private boolean isLooping = false;
    // 动画Handler
    private Handler AnimHanlder = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_START:{
                    // 开始轮播图片
                    if(mFrameIndex >=0 && mFrameIndex < mResourceIdList.size()
                            && mState == STATE_RUNNING){
                        mImageView.setImageResource(mResourceIdList.get(mFrameIndex));
                        mFrameIndex++;
                    }
                }
                break;
                case MSG_STOP:{
                    // 停止轮播图片
                    if (mTimeTask != null) {
                        mFrameIndex = 0;
                        mTimer.purge();
                        mTimeTask.cancel();
                        mState = STATE_STOP;
                        mTimeTask = null;
                        mImageView.setImageResource(0);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    /**
     * Constructor
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param
     * @return
     */
    public FrameAnimImageView(){
        // init Timer
        mTimer = new Timer();
    }

    /**
     * 设置动画播放资源
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param imageview 图片控件实例
     * @param resourceIdList 图片资源ID列表
     * @return
     */
    public void setAnimation(ImageView imageview, List<Integer> resourceIdList ){
        mImageView = imageview;
        mResourceIdList = resourceIdList;
    }

    /**
     * 开始播放动画
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     *  @param loop 是否循环播放
     *  @param duration 动画播放时间间隔
     * @return
     */
    public void start(boolean loop, int duration){
        stop();
        isLooping = loop;
        mFrameIndex = 0;
        mState = STATE_RUNNING;
        mTimeTask = new AnimTimerTask( );
        mTimer.schedule(mTimeTask, 0, duration);
    }

    /**
     * 停止动画播放
     * @author leibing
     * @createTime 2016/12/19
     * @lastModify 2016/12/19
     * @param
     * @return
     */
    public void stop(){
        if (mTimeTask != null) {
            mFrameIndex = 0;
            mState = STATE_STOP;
            mTimer.purge();
            mTimeTask.cancel();
            mTimeTask = null;
            mImageView.setBackgroundResource(0);
        }
    }

    /**
     * @className: AnimTimerTask
     * @classDescription: 定时器任务
     * @author: leibing
     * @createTime: 2016/12/19
     */
    class AnimTimerTask extends TimerTask {
        @Override
        public void run() {
            if(mFrameIndex < 0 || mState == STATE_STOP){
                return;
            }

            if( mFrameIndex < mResourceIdList.size() ){
                Message msg = AnimHanlder.obtainMessage(MSG_START,0,0,null);
                msg.sendToTarget();
            }else{
                mFrameIndex = 0;
                if(!isLooping){
                    Message msg = AnimHanlder.obtainMessage(MSG_STOP,0,0,null);
                    msg.sendToTarget();
                }
            }
        }
    }
}
