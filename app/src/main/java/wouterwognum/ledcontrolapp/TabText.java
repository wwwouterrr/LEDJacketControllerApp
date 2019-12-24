package wouterwognum.ledcontrolapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class TabText extends Fragment
{
    private mySeekBarChangeListener sChangeListener;

    public TabText ()
    {
        sChangeListener = new mySeekBarChangeListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_text, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        SeekBar slTC = view.findViewById(R.id.sliderTimePerCharacter);
        SeekBar slBB = view.findViewById(R.id.sliderBlankBetween);
        SeekBar slFO = view.findViewById(R.id.sliderFadeout);

        slTC.setOnSeekBarChangeListener(sChangeListener);
        slBB.setOnSeekBarChangeListener(sChangeListener);
        slFO.setOnSeekBarChangeListener(sChangeListener);

        Spinner textPalette = view.findViewById(R.id.spinnerTextPalette);
        textPalette.setAdapter(new ArrayAdapter<ColorPalettes>(this.getContext(), android.R.layout.simple_spinner_item, ColorPalettes.values()));

        textPalette.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedPalette = parent.getItemAtPosition(position).toString();
                Log.d("selected",selectedPalette);
                ((MainActivity) getActivity()).setBLEChar(BLEChars.TextPAL, selectedPalette);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        Button setText = view.findViewById(R.id.buttonSetText);
        setText.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String displayText = ((EditText)getView().findViewById(R.id.displayText)).getText().toString();
                if (displayText == null) displayText = "";
                String part1 = displayText.length() > 20 ? displayText.substring(0,20) : displayText;
                String part2 = displayText.length() > 20 ? displayText.substring(20) : "";
                ((MainActivity) getActivity()).setBLEChar(BLEChars.Text1, part1);
                ((MainActivity) getActivity()).setBLEChar(BLEChars.Text2, part2);
            }
        });

        CheckBox flashColours = view.findViewById(R.id.cbFlashColours);
        flashColours.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ((MainActivity) getActivity()).setBLEChar(BLEChars.TextFFC, String.valueOf(isChecked));
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
                case R.id.sliderTimePerCharacter:
                    ((MainActivity) getActivity()).setBLEChar(BLEChars.TextTPC, val);
                    break;

                case R.id.sliderBlankBetween:
                    ((MainActivity) getActivity()).setBLEChar(BLEChars.TextBBC, val);
                    break;

                case R.id.sliderFadeout:
                    ((MainActivity) getActivity()).setBLEChar(BLEChars.TextFAD, val);
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

