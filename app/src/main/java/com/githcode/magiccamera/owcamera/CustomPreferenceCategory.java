package com.githcode.magiccamera.owcamera;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.githcode.magiccamera.owcamera.ui.OnLayoutClickListener;

public class CustomPreferenceCategory extends PreferenceCategory {

    private OnLayoutClickListener onLayoutClickListener;
    private View cameraLayout;
    private TextView cameraText;
    private TextView cameraText1;
    private View cameraView;
    private View photoLayout;
    private TextView photoText;
    private TextView photoText1;
    private View photoView;
    private Boolean updateCamera = true;

    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_category_with_bar);
    }

    @Override
    public void onBindView(View holder) {
        super.onBindView(holder);

        cameraLayout = holder.findViewById(R.id.cameraLayout);
        cameraText = holder.findViewById(R.id.cameraText);
        cameraText1 = holder.findViewById(R.id.cameraText1);
        cameraView = holder.findViewById(R.id.cameraView);

        photoLayout = holder.findViewById(R.id.photoLayout);
        photoText = holder.findViewById(R.id.photoText);
        photoText1 = holder.findViewById(R.id.photoText1);
        photoView = holder.findViewById(R.id.photoView);

        if (updateCamera) {
            cameraText.setTextColor(getContext().getResources().getColor(R.color.colorBlue));
            photoText.setTextColor(getContext().getResources().getColor(R.color.black));

            cameraView.setBackgroundResource(R.color.colorBlue);
            photoView.setBackgroundResource(R.color.white);

            cameraText1.setVisibility(View.VISIBLE);
            photoText1.setVisibility(View.GONE);
        } else {
            cameraText.setTextColor(getContext().getResources().getColor(R.color.black));
            photoText.setTextColor(getContext().getResources().getColor(R.color.colorBlue));

            cameraView.setBackgroundResource(R.color.white);
            photoView.setBackgroundResource(R.color.colorBlue);

            cameraText1.setVisibility(View.GONE);
            photoText1.setVisibility(View.VISIBLE);
        }

        cameraLayout.setOnClickListener(v -> {
            if (onLayoutClickListener != null) onLayoutClickListener.onCameraLayoutClick();
        });

        photoLayout.setOnClickListener(v -> {
            if (onLayoutClickListener != null) onLayoutClickListener.onPhotoLayoutClick();
        });
    }

    public void setOnLayoutClickListener(OnLayoutClickListener listener) {
        this.onLayoutClickListener = listener;
    }

    public void updateCameraUI(Boolean updateCamera) {
        this.updateCamera = updateCamera;
    }

    public void updatePhotoUI(Boolean updateCamera) {
        this.updateCamera = updateCamera;
    }
}
