package yours.aw.whatseasy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

public class Splash extends Activity {
    public TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        txt = findViewById(R.id.txt);
        final Intent i = new Intent(Splash.this, MainActivity.class);
        ShimmerFrameLayout container =
                findViewById(R.id.shimmer_view_container);
        container.startShimmerAnimation();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}
