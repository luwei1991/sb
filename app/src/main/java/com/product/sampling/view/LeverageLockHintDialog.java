package com.product.sampling.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.product.sampling.R;

import java.util.Locale;

import androidx.annotation.NonNull;

public class LeverageLockHintDialog extends Dialog implements View.OnClickListener {

    private Activity mActivity;

    public LeverageLockHintDialog(@NonNull Context context) {
        super(context, R.style.DefaultDialogStyle);
        mActivity = (Activity) context;
        initDialog(context);
    }

    private void initDialog(Context context) {
        setContentView(R.layout.balance_dialog_lock_hint);
        Window window = getWindow();
        if (window != null) {
            window.getAttributes().gravity = Gravity.CENTER;
        }
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            setOwnerActivity(activity);
        }

        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_lock_detail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.tv_ok:
                dismiss();
                break;
            case R.id.tv_lock_detail:
                dismiss();
                break;
        }
    }


}
