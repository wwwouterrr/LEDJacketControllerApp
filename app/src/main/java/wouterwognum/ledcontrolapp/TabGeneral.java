package wouterwognum.ledcontrolapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class TabGeneral extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.tab_general, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        SeekBar sBrightness = view.findViewById(R.id.sliderBrightness);
        sBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                ((MainActivity) getActivity()).setBLEChar(BLEChars.Brightness, String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        SeekBar sSTE = view.findViewById(R.id.sliderShowTextEvery);
        sSTE.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                TextView displayValue = seekBar.getRootView().findViewById(seekBar.getLabelFor());
                String val = String.valueOf(progress * Integer.parseInt(seekBar.getTag().toString()));
                displayValue.setText(val);

                ((MainActivity) getActivity()).setBLEChar(BLEChars.TextEVR, val);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        RadioGroup modeGroup = view.findViewById(R.id.rgModes);
        modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
               switch (checkedId)
               {
                   case R.id.rbDemo:
                       ((MainActivity) getActivity()).setBLEChar(BLEChars.Mode, "demo");
                       break;

                   case R.id.rbText:
                       ((MainActivity) getActivity()).setBLEChar(BLEChars.Mode, "text");
                       break;

                   case R.id.rbAlternate:
                       ((MainActivity) getActivity()).setBLEChar(BLEChars.Mode, "alternate");
                       break;

                   case R.id.rbEffects:
                       ((MainActivity) getActivity()).setBLEChar(BLEChars.Mode, "effect");
                       break;
               }
            }
        });
    }
}