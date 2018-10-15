package com.sskj.lib.loadprogress;

/**
 * 作者 :吕志豪
 * 简书：https://www.jianshu.com/u/6e525b929aac
 * github：https://github.com/lvzhihao100
 * 描述：
 * 创建时间：2018-09-05 09:39
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.Indicator;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 自定义进度条 ProgressDialog
 *
 * @author haiwei 2016/3/9.
 */
public class ProgressDialogs {
    private Dialog dialog;
    private Context context;
    private static final int CLOSE_SAMPLE_VIEW = 0;  //定义关闭SampleView的动作信号标志
    private DelayCloseController delayCloseController;
    private Indicator indicator;

    private Handler mHandler = new MyHandler(this);

    private class DelayCloseController extends TimerTask {
        private Timer timer = new Timer();
        private int actionFlags = 0;//标志位参数

        public DelayCloseController() {
            super();
            timer.schedule(this, 30000);   //启动定时器
        }

        public void setCloseFlags(int flag) {
            actionFlags = flag;
        }

        @Override
        public void run() {
            Message messageFinish = new Message();
            messageFinish.what = actionFlags;
            mHandler.sendMessage(messageFinish);
        }
    }

    public ProgressDialogs(Context context) {
        this.context = context;
    }

    public void setIndicator(Indicator indicator){
        this.indicator=indicator;
    }

    public void showDialog() {
        showDialog("加载中");
    }

    public void showDialog(String msg) {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.layout_progress_dialog);
        window.setBackgroundDrawable(new BitmapDrawable());//去掉黑边
        TextView textView = (TextView) window.findViewById(R.id.loadingText);
        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView) window.findViewById(R.id.loadingView);
        if (indicator!=null){
            avLoadingIndicatorView.setIndicator(indicator);
        }
        if (TextUtils.isEmpty(msg)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(msg);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    dialog.dismiss();
                }
                return false;
            }
        });

    }


    public void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
//        if (delayCloseController != null) {
//            delayCloseController.timer.cancel();
//            delayCloseController.timer.purge();
//            delayCloseController = null;
//        }
    }

    public static class MyHandler extends Handler {

        private WeakReference<ProgressDialogs> weakReference = null;

        public MyHandler(ProgressDialogs progressDialogs) {
            weakReference = new WeakReference<ProgressDialogs>(progressDialogs);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLOSE_SAMPLE_VIEW:
                    if (weakReference.get().dialog != null && weakReference.get().dialog.isShowing()) {
//                        Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                        weakReference.get().closeDialog();
                    }
                    break;
            }
        }
    }

}
