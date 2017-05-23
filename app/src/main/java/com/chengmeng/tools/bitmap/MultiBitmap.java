package com.chengmeng.tools.bitmap;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.WXAPI;
import com.alibaba.mobileim.appmonitor.ResourceUtil;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.gingko.model.base.MulitImageVO;
import com.alibaba.mobileim.gingko.model.base.PicViewObject;
import com.alibaba.mobileim.kit.common.IMBaseActivity;
import com.alibaba.mobileim.ui.chat.MultiImageActivity;
import com.alibaba.mobileim.ui.multi.common.AlbumAdapter;
import com.alibaba.mobileim.ui.multi.common.GalleryAdapter;
import com.alibaba.mobileim.ui.multi.common.ImageBucket;
import com.alibaba.mobileim.ui.multi.common.ImageItem;
import com.alibaba.mobileim.ui.multi.common.OnCheckChangedListener;
import com.alibaba.mobileim.ui.multi.common.PhotoChooseHelper;
import com.alibaba.mobileim.ui.multi.common.YWPopupWindow;
import com.alibaba.mobileim.ui.uicommon.SmallStaticDataShareUtil;
import com.alibaba.mobileim.utility.IMUtil;
import com.alibaba.mobileim.utility.ResourceLoader;
import com.alibaba.wxlib.thread.WXThreadPoolMgr;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiBitmap extends IMBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnCheckChangedListener {
    public static final String MAX_COUNT = "maxCount";
    public static final String BUCKET_ID = "bucketId";
    public static final String RESULT_LIST = "result_list";
    public static final String NEED_COMPRESS = "need_compress";
    public static final String MAX_TOAST = "max_toast";
    public static final String PRE_CHECKED = "pre_checked_images";
    private static final int SELECT_PREVIEW_REQUEST_CODE = 900;
    private static final int SELECT_ITEM_REQUEST_CODE = 901;
    public static final int SELECT_ALBUM_REQUEST_CODE = 902;
    public static final String ALL_PIC = "常用图片";
    public static final String TITLE_RIGHT_TEXT = "titleRightText";
    public static final String NEED_CHOOSE_ORIGINAL_PIC = "need_choose_original_pic";
    private GridView gridGallery;
    private Handler mHandler;
    private GalleryAdapter mAdapter;
    private ImageView imgNoMedia;
    private TextView mPreviewBtn;
    private TextView picDirView;
    private TextView mSelectedCount;
    private int mMaxCount;
    private String mMaxToast;
    private String mTitleRightText;
    private PhotoChooseHelper photoChooseHelper;
    private List<ImageItem> mImageItemList = new ArrayList();
    private String mUserId;
    private YWPopupWindow ywPopupWindow;
    private ListView albumListView;
    private AlbumAdapter albumAdapter;
    private ImageBucket defaultImageBucket;
    private int currentDirIndex;
    private TextView finish;
    private boolean mNeddCompress = true;
    private RelativeLayout mLeftButton;
    private ImageView mSendOriginalCheck;
    private TextView mSendOriginal;
    private boolean mUseOrignal = false;
    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            ImageItem item = MultiBitmap.this.mAdapter.getItem(position);
            Intent data = (new Intent()).putExtra("single_path", item.getImagePath());
            MultiBitmap.this.setResult(-1, data);
            MultiBitmap.this.finish();
        }
    };

    public MultiBitmap() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int layoutId = ResourceLoader.getLayoutIdByName("aliwx_multi_pick_gallery");
        this.setContentView(layoutId);
        this.mUserId = WXAPI.getInstance().getLoginUserId();
        if (TextUtils.isEmpty(this.mUserId)) {
            this.finish();
        } else {
            this.init();
        }
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        SmallStaticDataShareUtil.setUseOrignal(this.mUseOrignal);
        this.ywPopupWindow.hidePopUpWindow();
    }

    protected void onResume() {
        super.onResume();
        this.mUseOrignal = SmallStaticDataShareUtil.isUseOrignal();
        this.checkAndUpdateSendOrignalState();
    }

    @TargetApi(9)
    private void init() {
        Intent intent = this.getIntent();
        ArrayList preCheckedList = null;
        this.mTitleRightText = "发送";
        if (intent != null) {
            this.mMaxCount = intent.getIntExtra("maxCount", -1);
            this.mMaxToast = intent.getStringExtra("max_toast");
            if (intent.hasExtra("titleRightText")) {
                this.mTitleRightText = intent.getStringExtra("titleRightText");
            }

            preCheckedList = intent.getStringArrayListExtra("pre_checked_images");
        }

        this.mHandler = new Handler();
        this.photoChooseHelper = PhotoChooseHelper.getHelper();
        this.photoChooseHelper.init(this.getApplicationContext());
        View button = this.findViewById(ResourceLoader.getRIdByName("title_back"));
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);
        this.finish = (TextView) this.findViewById(ResourceLoader.getRIdByName("finish"));
        this.finish.setEnabled(false);
        this.finish.setOnClickListener(this);
        this.finish.setText(this.mTitleRightText);
        this.finish.setAlpha(0.5F);
        this.mPreviewBtn = (TextView) this.findViewById(ResourceLoader.getRIdByName("preview"));
        this.mPreviewBtn.setOnClickListener(this);
        this.mPreviewBtn.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_gray_02"));
        this.picDirView = (TextView) this.findViewById(ResourceLoader.getRIdByName("pic_dir"));
        this.picDirView.setOnClickListener(this);
        this.picDirView.setText("常用图片");
        this.ywPopupWindow = new YWPopupWindow(this);
        int popupLayoutId = ResourceLoader.getLayoutIdByName("aliwx_multi_pick_album");
        int dd = ResourceUtil.getDimenByName(this, "aliwx_popup_height");
        float a = this.getResources().getDimension(dd);
        this.ywPopupWindow.initView(this.picDirView, popupLayoutId, dd, new YWPopupWindow.ViewInit() {
            public void initView(View v) {
                int albumListViewId = ResourceUtil.getIdByName(MultiBitmap.this, "album_list");
                MultiBitmap.this.albumListView = (ListView) v.findViewById(albumListViewId);
                MultiBitmap.this.albumListView.setFastScrollEnabled(false);
                MultiBitmap.this.albumListView.setFastScrollAlwaysVisible(false);
                MultiBitmap.this.albumListView.setVerticalScrollBarEnabled(false);
                List mImageBucketList = MultiBitmap.this.photoChooseHelper.getImagesBucketList(false);
                if (mImageBucketList != null && MultiBitmap.this.defaultImageBucket != null) {
                    mImageBucketList.add(0, MultiBitmap.this.defaultImageBucket);
                }

                MultiBitmap.this.albumAdapter = new AlbumAdapter(MultiBitmap.this, mImageBucketList, new View.OnClickListener() {
                    public void onClick(View view) {
                        int albumPicId = ResourceUtil.getIdByName(MultiBitmap.this, "album_pic");
                        int i = ((Integer) view.getTag(albumPicId)).intValue();
                        MultiBitmap.this.currentDirIndex = i;
                        MultiBitmap.this.albumAdapter.setIndex(MultiBitmap.this.currentDirIndex);
                        ImageBucket bucket = MultiBitmap.this.albumAdapter.getItem(i);
                        MultiBitmap.this.picDirView.setText(bucket.getBucketName());
                        List images = bucket.getImageList();
                        MultiBitmap.this.mImageItemList.clear();
                        MultiBitmap.this.mImageItemList.addAll(images);
                        MultiBitmap.this.mAdapter.updateDataAndNotify(images);
                        MultiBitmap.this.ywPopupWindow.hidePopUpWindow();
                    }
                });
                MultiBitmap.this.albumListView.setAdapter(MultiBitmap.this.albumAdapter);
            }
        });

        this.mSelectedCount = (TextView) this.findViewById(ResourceLoader.getRIdByName("selected_count"));
        this.mSelectedCount.setOnClickListener(this);
        this.gridGallery = (GridView) this.findViewById(ResourceLoader.getRIdByName("gridGallery"));
        this.gridGallery.setFastScrollEnabled(false);
        this.gridGallery.setFastScrollAlwaysVisible(false);
        this.gridGallery.setVerticalScrollBarEnabled(false);
        this.mAdapter = new GalleryAdapter(this.getApplicationContext(), this.mImageItemList);
        this.gridGallery.setOnItemClickListener(this);
        this.mAdapter.setMaxCount(this.mMaxCount);
        this.mAdapter.setMaxToast(this.mMaxToast);
        this.mAdapter.setOnCheckChangedListener(this);
        this.gridGallery.setAdapter(this.mAdapter);
        this.imgNoMedia = (ImageView) this.findViewById(ResourceLoader.getRIdByName("imgNoMedia"));
        this.mSelectedCount.setVisibility(View.GONE);

        WXThreadPoolMgr.getInstance().doAsyncRun(new Runnable() {
            public void run() {
                MultiBitmap.this.photoChooseHelper.getImagesBucketList(true);
                final List localImageItemList = MultiBitmap.this.photoChooseHelper.getImageItemList();
                final ArrayList allPics = new ArrayList();
                if (localImageItemList != null) {
                    Iterator i$ = localImageItemList.iterator();

                    label38:
                    while (true) {
                        ImageItem item;
                        String imagepath;
                        do {
                            do {
                                do {
                                    if (!i$.hasNext()) {
                                        break label38;
                                    }

                                    item = (ImageItem) i$.next();
                                } while (item == null);
                            } while (item.getImagePath() == null);

                            imagepath = item.getImagePath();
                        }
                        while (imagepath.toLowerCase().indexOf("dcim") <= 0 && imagepath.toLowerCase().indexOf("screenshots") <= 0 && imagepath.toLowerCase().indexOf("pictures") <= 0);

                        allPics.add(item);
                    }
                }

                if (allPics.isEmpty()) {
                    allPics.addAll(localImageItemList);
                }

                MultiBitmap.this.defaultImageBucket = new ImageBucket();
                MultiBitmap.this.defaultImageBucket.setBucketName("常用图片");
                MultiBitmap.this.defaultImageBucket.setCount(allPics.size());
                MultiBitmap.this.defaultImageBucket.setImageList(allPics);
                MultiBitmap.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (localImageItemList != null) {
                            MultiBitmap.this.mImageItemList.clear();
                            MultiBitmap.this.mImageItemList.addAll(allPics);
                            MultiBitmap.this.mAdapter.notifyDataSetChanged();
                            MultiBitmap.this.checkImageStatus();
                        }

                    }
                });
            }
        }, true);
        this.initLeftBottomButton();

        List selectedSet = this.photoChooseHelper.getSelectedList();
        if (preCheckedList != null && preCheckedList.size() > 0) {
            selectedSet.addAll(preCheckedList);
            this.updateSelectedCountView();
        } else {
            this.mPreviewBtn.setEnabled(false);
        }
    }

    private String getSendBtnTitle(int selectedCount) {
        return "发送(" + String.valueOf(selectedCount) + "/" + this.mMaxCount + ")";
    }

    private void reInit(final String bucketId) {
        this.updateSelectedCountView();
        WXThreadPoolMgr.getInstance().doAsyncRun(new Runnable() {
            public void run() {
                ImageBucket localImageBucket = MultiBitmap.this.photoChooseHelper.getImageBucket(bucketId);
                if (localImageBucket != null) {
                    final List localImageItemList = localImageBucket.getImageList();
                    MultiBitmap.this.mHandler.post(new Runnable() {
                        public void run() {
                            if (localImageItemList != null) {
                                MultiBitmap.this.mImageItemList.clear();
                                MultiBitmap.this.mImageItemList.addAll(localImageItemList);
                                MultiBitmap.this.mAdapter.notifyDataSetChanged();
                                MultiBitmap.this.checkImageStatus();
                            }

                        }
                    });
                }

            }
        });
    }

    private void checkImageStatus() {
        if (this.mAdapter.isEmpty()) {
            this.imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            this.imgNoMedia.setVisibility(View.GONE);
        }

    }

    private void updateSelectedCountView() {
        List selectedList = this.photoChooseHelper.getSelectedList();
        if (selectedList != null) {
            int count = selectedList.size();
            if (count > 0) {
                this.mSelectedCount.setVisibility(View.VISIBLE);
                this.mSelectedCount.setText(String.valueOf(count));
                this.finish.setText(this.mTitleRightText);
                this.mPreviewBtn.setEnabled(true);
                this.mPreviewBtn.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_white"));
                this.finish.setEnabled(true);
                this.finish.setAlpha(1.0F);
                if (this.getIntent().getBooleanExtra("need_choose_original_pic", true)) {
                    this.mLeftButton.setVisibility(View.VISIBLE);
                } else {
                    this.mLeftButton.setVisibility(View.INVISIBLE);
                }
            } else {
                this.finish.setText(this.mTitleRightText);
                this.mPreviewBtn.setEnabled(false);
                this.mPreviewBtn.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_gray_02"));
                this.finish.setEnabled(false);
                this.finish.setAlpha(0.5F);
                this.mLeftButton.setVisibility(View.INVISIBLE);
                this.mSelectedCount.setVisibility(View.GONE);
            }
        }

    }

    private String getTitleRightText(int value) {
        return this.mTitleRightText + "(" + value + "/" + this.mMaxCount + ")";
    }

    protected void onDestroy() {
        SmallStaticDataShareUtil.setUseOrignal(false);
        if (this.mAdapter != null) {
            this.mAdapter.recycle();
        }

        super.onDestroy();
    }

    public void onBackPressed() {
        if (this.ywPopupWindow.isShowing() && !this.isFinishing()) {
            this.ywPopupWindow.hidePopUpWindow();
        } else {
            this.onback();
            super.onBackPressed();
        }
    }

    private void onback() {
        PhotoChooseHelper.getHelper().getSelectedList().clear();
        this.finish();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == ResourceLoader.getRIdByName("title_back")) {
            this.onback();
        } else if (id == ResourceLoader.getRIdByName("finish")) {
            this.setResult((Intent) null, !this.mUseOrignal);
        } else {
            List mImageBucketList;
            if (id == ResourceLoader.getRIdByName("preview")) {
                mImageBucketList = PhotoChooseHelper.getHelper().getSelectedList();
                if (mImageBucketList != null && mImageBucketList.size() > 0) {
                    if (!this.isFinishing()) {
                        this.ywPopupWindow.hidePopUpWindow();
                    }

                    ArrayList result = new ArrayList();
                    ArrayList checkedList = new ArrayList();
                    long _id = 100L;
                    if (mImageBucketList != null && mImageBucketList.size() > 0) {
                        Iterator vo = mImageBucketList.iterator();

                        while (vo.hasNext()) {
                            String intent = (String) vo.next();
                            PicViewObject bundle = new PicViewObject();
                            bundle.setPicPreViewUrl(intent);
                            bundle.setPicUrl(intent);
                            bundle.setPicId(Long.valueOf(_id++));
                            if (!intent.endsWith(".gif") && !intent.endsWith(".GIF")) {
                                bundle.setPicType(1);
                            } else {
                                bundle.setPicType(4);
                            }

                            result.add(bundle);
                            checkedList.add(intent);
                        }
                    }

                    MulitImageVO var11 = new MulitImageVO(0, result);
                    Intent var12 = new Intent(this, MultiImageActivity.class);
                    Bundle var13 = new Bundle();
                    var13.putSerializable("mulit_image_vo", var11);
                    var12.putExtra("mulit_image_vo", var13);
                    var12.putExtra("mulit_image_pick_mode", 1);
                    var12.putStringArrayListExtra("mulit_image_checked_list", checkedList);
                    var12.putExtra("maxCount", this.mMaxCount);
                    var12.putExtra("max_toast", this.mMaxToast);
                    if (this.getIntent().hasExtra("titleRightText")) {
                        var12.putExtra("multi_select_finish_button_text", this.getIntent().getStringExtra("titleRightText"));
                    }

                    if (this.getIntent().hasExtra("need_choose_original_pic")) {
                        var12.putExtra("need_choose_original_pic", this.getIntent().getBooleanExtra("need_choose_original_pic", true));
                    }

                    this.startActivityForResult(var12, 900);
                }
            } else if (id != ResourceLoader.getRIdByName("selected_count")) {
                if (id == ResourceLoader.getRIdByName("pic_dir")) {
                    if (this.ywPopupWindow.isShowing()) {
                        this.ywPopupWindow.hidePopUpWindow();
                    } else {
                        mImageBucketList = this.photoChooseHelper.getImagesBucketList(false);
                        if (this.albumAdapter != null && mImageBucketList != null) {
                            if (this.defaultImageBucket != null) {
                                mImageBucketList.add(0, this.defaultImageBucket);
                            }

                            this.albumAdapter.updateDataAndNotify(mImageBucketList);
                        }

                        this.ywPopupWindow.showPopUpWindow();
                    }
                } else if (id == ResourceLoader.getRIdByName("left_button")) {
                    this.changeSendOrignalState();
                }
            }
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList result = new ArrayList();
        ArrayList checkedList = new ArrayList();
        List selectedSet = PhotoChooseHelper.getHelper().getSelectedList();
        int size = selectedSet.size();
        if (size >= this.mMaxCount) {
            for (int picId = size - 1; picId >= this.mMaxCount - 1; --picId) {
                selectedSet.remove(picId);
            }
        }

        checkedList.addAll(selectedSet);
        long var18 = 100L;
        if (this.mImageItemList != null && this.mImageItemList.size() > 0) {
            Iterator vo = this.mImageItemList.iterator();

            while (vo.hasNext()) {
                ImageItem intent = (ImageItem) vo.next();
                String bundle = intent.getImagePath();
                String e = intent.getThumbnailPath();
                if (TextUtils.isEmpty(e)) {
                    e = bundle;
                }

                PicViewObject picViewObject = new PicViewObject();
                picViewObject.setPicPreViewUrl(e);
                picViewObject.setPicUrl(bundle);
                picViewObject.setPicId(Long.valueOf(var18++));
                picViewObject.setPicType(1);
                result.add(picViewObject);
            }
        }

        MulitImageVO var19 = new MulitImageVO(position, result);
        Intent var20 = new Intent(this, MultiImageActivity.class);
        Bundle var21 = new Bundle();
        var21.putSerializable("mulit_image_vo", var19);
        var20.putExtra("mulit_image_vo", var21);
        var20.putExtra("mulit_image_pick_mode", 1);
        if (this.getIntent().hasExtra("titleRightText")) {
            var20.putExtra("multi_select_finish_button_text", this.getIntent().getStringExtra("titleRightText"));
        }

        if (this.getIntent().hasExtra("need_choose_original_pic")) {
            var20.putExtra("need_choose_original_pic", this.getIntent().getBooleanExtra("need_choose_original_pic", true));
        }

        var20.putStringArrayListExtra("mulit_image_checked_list", checkedList);
        var20.putExtra("maxCount", this.mMaxCount);
        var20.putExtra("max_toast", this.mMaxToast);

        try {
            this.startActivityForResult(var20, 901);
        } catch (Exception var17) {
            WxLog.e("Exception", var17.getMessage(), var17);
        }

    }

    public void OnCheckChanged() {
        this.updateSelectedCountView();
        this.checkAndUpdateSendOrignalState();
    }

    private void setResult(Intent data, boolean needCompress) {
        ArrayList selectedList = new ArrayList(this.photoChooseHelper.getSelectedList());
        Intent intent = new Intent();
        intent.putStringArrayListExtra("result_list", selectedList);
        intent.putExtra("need_compress", needCompress);
        this.setResult(-1, intent);
        PhotoChooseHelper.getHelper().recycle();
        this.finish();
    }

    private boolean needCompress(Intent data) {
        boolean needCompress = true;
        if (data != null) {
            needCompress = !data.getBooleanExtra("send_orginal", false);
        }

        return needCompress;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList checkedList;
        ArrayList uncheckedList;
        List selectedList;
        if (resultCode == -1) {
            if (requestCode != 900 && requestCode != 901) {
                if (requestCode == 902) {
                    String checkedList1 = data.getStringExtra("bucketId");
                    this.reInit(checkedList1);
                }
            } else {
                checkedList = data.getStringArrayListExtra("mulit_image_result_checked_list");
                uncheckedList = data.getStringArrayListExtra("mulit_image_result_unchecked_list");
                selectedList = this.mAdapter.getSelectedSet();
                if (selectedList != null) {
                    selectedList.removeAll(uncheckedList);
                    selectedList.addAll(checkedList);
                }

                this.setResult(data, this.needCompress(data));
            }
        } else if (resultCode == 0 && (requestCode == 900 || requestCode == 901) && data != null) {
            checkedList = data.getStringArrayListExtra("mulit_image_result_checked_list");
            uncheckedList = data.getStringArrayListExtra("mulit_image_result_unchecked_list");
            selectedList = this.mAdapter.getSelectedSet();
            if (selectedList != null) {
                selectedList.removeAll(uncheckedList);
                selectedList.addAll(checkedList);
            }

            this.mAdapter.notifyDataSetChanged();
            this.updateSelectedCountView();
        }

    }

    private void initLeftBottomButton() {
        this.mLeftButton = (RelativeLayout) this.findViewById(ResourceLoader.getRIdByName("left_button"));
        this.mLeftButton.setVisibility(View.INVISIBLE);
        this.mLeftButton.setOnClickListener(this);
        this.mSendOriginalCheck = (ImageView) this.findViewById(ResourceLoader.getRIdByName("send_original_check"));
        this.mSendOriginal = (TextView) this.findViewById(ResourceLoader.getRIdByName("send_original"));
    }

    private void changeSendOrignalState() {
        if (this.mLeftButton.getVisibility() == View.VISIBLE && !this.mUseOrignal) {
            this.mSendOriginal.setText(this.getCurrentTotalPicSize());
            this.mSendOriginal.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_white"));
            this.mSendOriginalCheck.setImageResource(ResourceLoader.getIdByName("drawable", "aliwx_send_original_btn_on"));
            this.mUseOrignal = true;
        } else if (this.mLeftButton.getVisibility() == View.VISIBLE && this.mUseOrignal) {
            this.mSendOriginal.setText(this.getCurrentTotalPicSize());
            this.mSendOriginal.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_gray_02"));
            this.mSendOriginalCheck.setImageResource(ResourceLoader.getIdByName("drawable", "aliwx_send_original_btn_off"));
            this.mUseOrignal = false;
        }

    }

    private void checkAndUpdateSendOrignalState() {
        if (this.mLeftButton.getVisibility() == View.VISIBLE && this.mUseOrignal) {
            this.mSendOriginal.setText(this.getCurrentTotalPicSize());
            this.mSendOriginal.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_blue"));
            this.mSendOriginalCheck.setImageResource(ResourceLoader.getIdByName("drawable", "aliwx_send_original_btn_on"));
        } else if (this.mLeftButton.getVisibility() == View.VISIBLE && !this.mUseOrignal) {
            this.mSendOriginal.setText(this.getCurrentTotalPicSize());
            this.mSendOriginal.setTextColor(ResourceUtil.getRealColorByName(this, "aliwx_color_gray_02"));
            this.mSendOriginalCheck.setImageResource(ResourceLoader.getIdByName("drawable", "aliwx_send_original_btn_off"));
        }

    }

    private String getCurrentTotalPicSize() {
        long totalFileSize = 0L;
        List selectedSet = this.mAdapter.getSelectedSet();
        if (selectedSet != null) {
            Iterator sendOriginalText = selectedSet.iterator();

            while (sendOriginalText.hasNext()) {
                String picLocalPath = (String) sendOriginalText.next();
                if (picLocalPath != null) {
                    File f = new File(picLocalPath);
                    if (f.exists() && f.isFile()) {
                        totalFileSize += f.length();
                    }
                }
            }
        }

        if (totalFileSize > 0L) {
            StringBuilder sendOriginalText1 = (new StringBuilder(ResourceLoader.getStringByName("aliwx_send_original"))).append("(共").append(IMUtil.bytes2KOrM(totalFileSize)).append(")");
            return sendOriginalText1.toString();
        } else {
            return ResourceLoader.getStringByName("aliwx_send_original");
        }
    }
}
