package wang.shenglifei.jeep.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import wang.shenglifei.jeep.R;
import wang.shenglifei.jeep.activitys.ResultActivity;
import wang.shenglifei.jeep.activitys.ShowActivity;
import wang.shenglifei.jeep.helper.Helper;


public class BarcodeScanFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
        barcodeView.decodeSingle(callback);
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appContext = getActivity().getApplication().getApplicationContext();
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_barcode_scan, container, false);
        barcodeView = (CompoundBarcodeView)view.findViewById(R.id.barcode_scanner);
        return view;
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String barcodeResult = result.getText();
            try {
                URL u = new URL(barcodeResult);
                u.toURI();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(barcodeResult));
                startActivity(i);
            } catch (MalformedURLException | URISyntaxException e) {
                Intent intent = new Intent();
                intent.putExtra(Helper.result,barcodeResult);
                if (!barcodeResult.equals("岳麓书院")) {
                    intent.setClass(getActivity(), ShowActivity.class);
                } else {
                    intent.setClass(getActivity(), ResultActivity.class);
                }
                startActivity(intent);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };
    private CompoundBarcodeView barcodeView;
    Context appContext;

}
