package wouterwognum.ledcontrolapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class TabEffects extends Fragment
{
    private mySeekBarChangeListener sChangeListener;

    public TabEffects ()
    {
        sChangeListener = new mySeekBarChangeListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_effects, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        SeekBar slSX = view.findViewById(R.id.sliderEffectSpeed);
        SeekBar slSC = view.findViewById(R.id.sliderEffectScale);
        SeekBar slSF = view.findViewById(R.id.sliderEffectFade);

        slSX.setOnSeekBarChangeListener(sChangeListener);
        slSC.setOnSeekBarChangeListener(sChangeListener);
        slSF.setOnSeekBarChangeListener(sChangeListener);

        Spinner effectPalette = view.findViewById(R.id.spinnerEffectPalette);
        effectPalette.setAdapter(new ArrayAdapter<ColorPalettes>(this.getContext(), android.R.layout.simple_spinner_item, ColorPalettes.values()));

        Spinner effectType = view.findViewById(R.id.spinnerEffectType);
        effectType.setAdapter(new ArrayAdapter<EffectTypes>(this.getContext(), android.R.layout.simple_spinner_item, EffectTypes.values()));

        effectPalette.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedPalette = parent.getItemAtPosition(position).toString();
                ((MainActivity) getActivity()).setBLEChar(BLEChars.EffectPAL, selectedPalette);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        effectType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedEffect = parent.getItemAtPosition(position).toString();
                ((MainActivity) getActivity()).setBLEChar(BLEChars.Effect, selectedEffect);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        CheckBox colourLoop = view.findViewById(R.id.cbEffectColourLoop);
        colourLoop.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ((MainActivity) getActivity()).setBLEChar(BLEChars.EffectCLL, String.valueOf(isChecked));
            }
        });
    }

    class mySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            TextView displayValue = seekBar.getRootView().findViewById(seekBar.getLabelFor());
            String val = String.valueOf(progress * Integer.parseInt(seekBar.getTag().toString()));
            displayValue.setText(val);

            switch (seekBar.getId())
            {
                case R.id.sliderEffectSpeed:
                    ((MainActivity) getActivity()).setBLEChar(BLEChars.EffectSPD, val);
                    break;

                case R.id.sliderEffectScale:
                    ((MainActivity) getActivity()).setBLEChar(BLEChars.EffectSCL, val);
                    break;

                case R.id.sliderEffectFade:
                    ((MainActivity) getActivity()).setBLEChar(BLEChars.EffectFAD, val);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    }
}
