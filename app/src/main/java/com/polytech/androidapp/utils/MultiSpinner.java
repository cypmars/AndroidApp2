package com.polytech.androidapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Cyprien on 01/05/2017.
 */

public class MultiSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener
{
    private ArrayList<?> listitems;
    private boolean[] checked;

    public MultiSpinner(Context context)
    {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1)
    {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2)
    {
        super(arg0, arg1, arg2);
    }

    public boolean isCheck(int i){
        if (checked[i])
            return true;
        else
            return false;
    }

    public void setChecked(int i){
        checked[i] = true;
    }

    @Override
    public void onClick(DialogInterface dialog, int ans, boolean isChecked)
    {
        if (isChecked)
            checked[ans] = true;
        else
            checked[ans] = false;
    }


    @Override
    public void onCancel(DialogInterface dialog)
    {

    }

    @Override
    public boolean performClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                listitems.toArray(new CharSequence[listitems.size()]), checked, this);
        builder.setPositiveButton("Valider",
                new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String str ="";
                        for (int i = 0; i < listitems.size(); i++)
                        {
                            if (checked[i])
                            {
                                str=listitems.get(i).toString()+", "+str;
                            }
                        }
                        setTitle(str);
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(ArrayList<?> items, String allText,
                         multispinnerListener listener)
    {
        this.listitems = items;

        checked = new boolean[items.size()];
        for (int i = 0; i < checked.length; i++)
            checked[i] =false;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[] { allText });
        setAdapter(adapter);
    }

    public void setTitle(String allText){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[] { allText });
        setAdapter(adapter);
    }


    public interface multispinnerListener
    {
        public void onItemschecked(boolean[] checked);
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for(int i = 0; i < listitems.size(); ++i) {
            if(checked[i]) {
                if(foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(listitems.get(i));
            }
        }

        return sb.toString();
    }
}