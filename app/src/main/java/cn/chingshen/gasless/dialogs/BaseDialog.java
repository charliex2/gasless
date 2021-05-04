package cn.chingshen.gasless.dialogs;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context, int themeResId, int layoutResId) {
        super(context, themeResId);
        this.setContentView(layoutResId);
    }
}
