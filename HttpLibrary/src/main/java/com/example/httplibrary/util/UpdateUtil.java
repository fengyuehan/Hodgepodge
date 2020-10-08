package com.example.httplibrary.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.jumai.hhpocket.R;
import com.jumai.hhpocket.application.BaseData;
import com.jumai.hhpocket.application.Constants;
import com.jumai.hhpocket.model.bean.UpgradeBean;
import com.jumai.hhpocket.net.CallBack;
import com.jumai.hhpocket.net.RetrofitEngine;
import com.jumai.hhpocket.request.WalletModel;
import com.jumai.hhpocket.ui.dialog.ConfirmCallback;
import com.jumai.hhpocket.ui.dialog.UpgradeDialog;
import com.jumai.hhpocket.ui.dialog.UpgradeProgressDialog;
import com.jumai.hhpocket.utils.CommonUtil;
import com.jumai.hhpocket.utils.DataManage;
import com.jumai.hhpocket.utils.LanguageManager;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * Created by yf on 2018/1/23.
 * Email：yunfei10306@163.com
 * 描述：更新(建议在APP启动的时候判断下Apk存放路径是否有该版本apk存在，存在就删除，防止出现下载后的各版本apk存在SD卡中)
 * 更新的原理其实就是在
 */

public class UpdateUtil {
    public static final String UPDATE = "update";
    //设置下载url
    public static final int TYPE_NOTIFICATION = 0;
    public static final int TYPE_APP = 1;
    private AppCompatActivity mActivity;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String UPDATE_URL;
    private File apkFile;
    //是否静默下载
    private boolean mSilentDownload = false;
    private int mType;
    private UpgradeDialog mUpgradeDialog;
    private UpgradeProgressDialog mProgressDialog;
    private UpgradeListener mListener;
    //是否自己控制强制更新（不由后台控制）
    private boolean mOutControlUpgrade;

    public UpdateUtil(AppCompatActivity activity) {
        mActivity = activity;
    }

    public UpdateUtil setType(int type) {
        mType = type;
        return this;
    }

    public UpdateUtil setSilentDownload(boolean silentDownload) {
        mSilentDownload = silentDownload;
        return this;
    }

    public UpdateUtil setOutControlUpgrade(boolean outControlUpgrade) {
        mOutControlUpgrade = outControlUpgrade;
        return this;
    }

    /**
     * 更新进度
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@com.hwangjr.rxbus.annotation.Tag(UPDATE)}
    )
    public void updateProgress(FileDownLoadProgressEvent downloadProgressEvent) {
        if (mType == TYPE_NOTIFICATION) {
            setProgress(downloadProgressEvent.progress);
        } else if (mType == TYPE_APP) {
            if (mProgressDialog != null) {
                mProgressDialog.setProgress(downloadProgressEvent.progress);
            }
        }
    }

    /**
     * 检测更新
     */
    public void checkUpdate() {
        WalletModel.getUpgradeInfo().compose(RxUtil.lifecycle((RxAppCompatActivity) mActivity)).subscribe(new CallBack<BaseData<UpgradeBean>>() {
            @Override
            public void onSuccess(BaseData<UpgradeBean> resultBean) {
                if (Constants.CODE_OK.equals(resultBean.code)) {
                    if (CommonUtil.needUpgradeNew(mActivity, resultBean.data.versionName)) {
                        if (mListener != null) {
                            if (mOutControlUpgrade) {
                                mListener.getForceStatus(false);
                                resultBean.data.isForce = 0;
                                mListener.needShowDialog(true);
                                upgrade(resultBean);
                            } else {
                                mListener.getForceStatus(resultBean.data.isForce == 1);
                                if (resultBean.data.isForce == 1) {
                                    mListener.needShowDialog(true);
                                    upgrade(resultBean);
                                } else {
                                    if (!resultBean.data.versionName.equals(DataManage.getLatestVersion())) {
                                        mListener.needShowDialog(true);
                                        upgrade(resultBean);
                                    } else {
                                        mListener.needShowDialog(false);
                                    }
                                }
                            }
                        }
                    } else {
                        if (mListener != null) {
                            mListener.needShowDialog(false);
                        }
                    }
                    //设置最新版本号
                    DataManage.setLatestVersion(resultBean.data.versionName);
                }
            }

            @Override
            public void onFail(int code, String message) {
            }
        });
    }

    /**
     * 开始更新
     */
    private void upgrade(BaseData<UpgradeBean> resultBean) {
        UPDATE_URL = resultBean.data.updateUrl;
        String currentLanguage = LanguageManager.getCurrentLanguage();
        showUpgradeDialog(currentLanguage.equals("zh") ? resultBean.data.upgradeInfo : resultBean.data.upgradeInfoEn, resultBean.data.isForce);
    }

    /**
     * 下载更新通知
     */
    private void showUpdateNotification() {
        notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(mActivity, null)
                .setSmallIcon(R.drawable.icon_logo)
                .setContentTitle(mActivity.getString(getPackageInfo().applicationInfo.labelRes) + mActivity.getString(R.string.tips_downloading1))
                .setProgress(100, 0, false)
                .setAutoCancel(true);
    }

    /**
     * 下载APK安装包
     * 如果APK已存在，直接安装，否则下载后安装
     */
    private void download() {
        File downloadPathFile = getDownloadPathFile();
        /*if (downloadPathFile.exists()) {
            installApk(downloadPathFile);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        } else {*/
        register();
            /*
            如不想生成通知栏形式显示下载进度，可替换成dialog形式，如头条更新
            可自定义一个dialog，下载进度在updateProgress()参数downloadProgressEvent.progress中
            因为公司不同，dialog长的也不一样这里也就没写
            如需静默下载，改变silentdownload为true即可
             */
        if (mType == TYPE_NOTIFICATION) {
            showUpdateNotification();
            ToastUtils.showShort(R.string.tips_downloading1);
        }
        RetrofitEngine.getInstanceForDownload(mSilentDownload).create(DownloadApi.class).downloadAPK(UPDATE_URL)
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new CallBack<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            apkFile = writeFile(responseBody.source());
                            install();
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (!mSilentDownload) {
                                endUpdate();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {
                        ToastUtils.showShort(R.string.tips_download_failed);
                        if (!mSilentDownload) {
                            endUpdate();
                        }
                    }
                });
        //}
    }


    private void register() {
        RxBus.get().register(this);
    }

    private void unregister() {
        RxBus.get().unregister(this);
    }

    /**
     * 更新结束
     */
    private void endUpdate() {
        if (mType == TYPE_NOTIFICATION) {
            notificationManager.cancel(0);
        }
        unregister();
        if (mUpgradeDialog != null) {
            mUpgradeDialog.dismiss();
        }
    }


    /**
     * 安装
     */
    private void install() {
        installApk(apkFile);
    }

    /**
     * 设置进度
     */
    private void setProgress(int progress) {
        Log.e("Update", "下载接收 progress:    " + progress);
        if (progress < 0) {
            endUpdate();
            throw new IllegalArgumentException(mActivity.getString(R.string.tips_set_download_url));
        } else {
            notificationBuilder.setProgress(100, progress, false);
            notificationBuilder.setContentText(progress + "%");
            notificationManager.notify(0, notificationBuilder.build());
            if (progress == 100) {
                endUpdate();
                installApk(apkFile);
            }
        }
    }

    interface DownloadApi {
        @Streaming
        @GET
        Observable<ResponseBody> downloadAPK(@Url String url);
    }

    /**
     * 写入文件
     */
    private File writeFile(BufferedSource source) throws IOException {
        File file = getDownloadPathFile();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }

        BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
        bufferedSink.writeAll(source);
        bufferedSink.close();
        source.close();
        return file;
    }

    /**
     * APK存放路径
     */
    @NonNull
    private File getDownloadPathFile() {
        String appName = mActivity.getString(getPackageInfo().applicationInfo.labelRes);
        //下载后的保存路径 如以retrofitEngine1.1.apk命名
        return new File(Environment.getExternalStorageDirectory() + "/" + appName, appName + getPackageInfo().versionName + ".apk");
    }

    /**
     * 安装 apk 文件
     */
    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mActivity, intent, "application/vnd.android.package-archive", apkFile, true);
        startActivity(intent);
    }

    private void showUpgradeDialog(String upgradeMsg, int forceUpgrade) {
        if (mUpgradeDialog == null) {
            mUpgradeDialog = new UpgradeDialog(mActivity, new ConfirmCallback() {
                @Override
                public void callback() {
                    showUpgradeProgress();
                }
            });
        }

        mUpgradeDialog.setUpgradeMsg(upgradeMsg.replaceAll("&", "\n"));

        if (mOutControlUpgrade) {
            mUpgradeDialog.setForceUpgrade(false);
        } else {
            mUpgradeDialog.setForceUpgrade(forceUpgrade == 1);
        }

        mUpgradeDialog.show();
    }

    @SuppressLint("CheckResult")
    private void showUpgradeProgress() {
        new RxPermissions(mActivity)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        mProgressDialog = new UpgradeProgressDialog(mActivity);
                        mProgressDialog.show();
                        download();
                    } else {
                        ToastUtils.showLong(R.string.tips_app_upgrade);
                        mActivity.finish();
                    }
                });
    }

    private PackageInfo getPackageInfo() {
        PackageManager pm = mActivity.getPackageManager();
        try {
            return pm.getPackageInfo(mActivity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUpgradeListener(UpgradeListener listener) {
        mListener = listener;
    }

    public interface UpgradeListener {
        void getForceStatus(boolean isForce);

        void needShowDialog(boolean show);
    }
}
