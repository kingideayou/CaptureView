package me.next.drawrectview;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import me.next.captureview.CaptureView;
import me.next.captureview.OnButtonClickListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        CaptureView captureView = (CaptureView) findViewById(R.id.pv_board);
        captureView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onConfirmClick(Rect rect) {
                Toast.makeText(getApplicationContext(), "选中区域 : " + rect.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClick() {
                Toast.makeText(getApplicationContext(), "点击取消按钮", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
