package com.fifteen15studios.basedialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BaseDialog extends Dialog {

    public final static int BUTTON_POSITIVE = 1;
    public final static int BUTTON_NEGATIVE = -1;
    public final static int BUTTON_NEUTRAL = 0;

    private boolean cancelled = false;
    private int useButton = 0;

    private CharSequence text = null;
    private CharSequence title = "";

    private boolean hidePositive = true;
    private boolean hideNeutral = false;
    private boolean hideNegative = true;

    private String positiveLabel;
    private String negativeLabel;
    private String neutralLabel;

    private int layoutID = -1;
    private View layout;

    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    private View.OnClickListener neutralListener;

    // Default Constructor
    public BaseDialog(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_base);
        if(layoutID != -1)
            setContentView(layoutID);
        else if(layout!=null)
            setContentView(layout);
        setUseButton(useButton);
        setTitle(title);
        setText(text);

        hideButtons();
    }

    /**
     *  If this method is never called, no positive button will be shown
     *
     * @param textResource Button text resource ID
     * @param listener what to do when the button is clicked
     */
    public void setPositiveButton(int textResource, Button.OnClickListener listener)
    {
        setPositiveButton(getContext().getResources().getString(textResource), listener);
    }

    /**
     * If this method is never called, no positive button will be shown
     *
     * @param text Button text. If null, it will be android.R.string.ok
     * @param listener what to do when the button is clicked
     */
    public void setPositiveButton(String text, Button.OnClickListener listener)
    {
        Button okButton = findViewById(R.id.positiveButton);
        positiveLabel = text;

        if(listener==null)
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            };

        positiveListener = listener;
        hidePositive = false;

        if(okButton!=null) {
            okButton.setVisibility(View.VISIBLE);

            if (text != null)
                okButton.setText(text);

            okButton.setOnClickListener(listener);
        }
    }

    /**
     *  If this method is never called, no neutral button will be shown
     *
     * @param textResource Button text resource ID
     * @param listener what to do when the button is clicked
     */
    public void setNeutralButton(int textResource, Button.OnClickListener listener)
    {
        setNeutralButton(getContext().getResources().getString(textResource), listener);
    }

    /**
     * If this method is never called, no positive button will be shown
     *
     * @param text Button text. If null, it will be @strings\neutral
     * @param listener what to do when the button is clicked
     */
    public void setNeutralButton(String text, Button.OnClickListener listener)
    {
        Button button = findViewById(R.id.neutralButton);
        neutralLabel = text;

        if(listener==null)
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            };

        neutralListener = listener;
        hideNeutral = false;

        if(button!=null) {
            button.setVisibility(View.VISIBLE);

            if (text != null)
                button.setText(text);

            button.setOnClickListener(listener);
        }
    }

    /**
     * If this method is never called, negative button will have text of android.R.string.cancel
     * and will cancel the dialog when clicked.
     *
     * @param textResource Button text resource ID
     * @param listener What to do when button is clicked.
     */
    public void setNegativeButton(int textResource, Button.OnClickListener listener)
    {
        setNegativeButton(getContext().getResources().getString(textResource), listener);
    }

    /**
     * If this method is never called, negative button will have text of android.R.string.cancel
     * and will cancel the dialog when clicked.
     *
     * @param text Button text. If null, it will be android.R.string.cancel
     * @param listener What to do when button is clicked
     */
    public void setNegativeButton(String text, Button.OnClickListener listener)
    {
        Button cancelButton = findViewById(R.id.negativeButton);

        negativeLabel = text;

        if(listener==null)
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            };

        negativeListener = listener;
        hideNegative = false;

        if(cancelButton!=null) {
            cancelButton.setVisibility(View.VISIBLE);

            if (text != null)
                cancelButton.setText(text);

            cancelButton.setOnClickListener(listener);
        }
    }

    /**
     * Sets the middle part of the dialog - below the title, above the buttons
     *
     * @param view The view that will be displayed as the body of the dialog
     */
    @Override
    public void setContentView(@NonNull View view) {
        layout = view;
        try {
            setContentView(view.getId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the middle part of the dialog - below the title, above the buttons
     *
     * @param layoutResID resource ID of the view that will be displayed as the body of the dialog
     */
    @Override
    public void setContentView(int layoutResID) {
        layoutID = layoutResID;
        try {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup parent = findViewById(R.id.content);

            if(inflater!=null)
                inflater.inflate(layoutResID, parent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Resize the dialog after view has changed.
     */
    private void resize()
    {
        TableLayout tableLayout = findViewById(R.id.buttonTable);
        int tableHeight = tableLayout.getHeight();

        ScrollView scrollView = findViewById(R.id.scrollView);

        //check to see if a row is completely gone
        int rows = tableLayout.getChildCount();
        int visibleRows = rows;
        for(int i = 0; i < rows; i++)
        {
            TableRow row = (TableRow)tableLayout.getChildAt(i);

            int children = row.getChildCount();
            int visibleChildren = children;

            for(int j = 0; j < children; j++)
                if(row.getChildAt(i).getVisibility() == View.GONE)
                {
                    visibleChildren--;
                }

            if(visibleChildren==0)
                visibleRows--;
        }

        //height of a button in pixels
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getContext().getResources().getDisplayMetrics());

        //if buttons aren't showing properly, fix layout
        if (tableHeight < (visibleRows * px))
        {
            RelativeLayout.LayoutParams tableParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tableParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            if(Build.VERSION.SDK_INT >= 17)
                tableParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            else
                tableParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            tableLayout.setLayoutParams(tableParams);

            RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            scrollParams.addRule(RelativeLayout.ABOVE, R.id.buttonTable);
            scrollParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
            scrollView.setLayoutParams(scrollParams);
        }
    }

    /**
     * Changes the title of the dialog. If no title is set, title will be empty
     *
     * @param titleId Resource ID of the title string
     */
    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getResources().getString(titleId));
    }

    /**
     * Changes the title of the dialog. If no title is set, or if null, title will be empty
     *
     * @param title Title to be displayed on the dialog
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.title = title;
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null)
            toolbar.setTitle(title);
    }

    /**
     *
     * @return True if dialog was cancelled, otherwise false
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancels operations. Sets "cancelled" as true. Use "isCancelled()" to find if dialog was cancelled
     */
    @Override
    public void cancel() {
        cancelled = true;
        super.cancel();
    }

    /**
     * Default to button to show
     *
     * @param button Accepts constants {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     */
    public void setUseButton(int button) {
        this.useButton = button;

        Button okButton = findViewById(R.id.positiveButton);
        Button cancelButton = findViewById(R.id.negativeButton);
        Button neutralButton = findViewById(R.id.neutralButton);
        if(okButton != null && cancelButton != null && neutralButton != null) {
            switch (button) {
                case BUTTON_POSITIVE:

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                    okButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.GONE);
                    neutralButton.setVisibility(View.GONE);
                    break;
                case BUTTON_NEGATIVE:
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancel();
                        }
                    });
                    cancelButton.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.GONE);
                    neutralButton.setVisibility(View.GONE);
                    break;
                case BUTTON_NEUTRAL:
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                    cancelButton.setVisibility(View.GONE);
                    okButton.setVisibility(View.GONE);
                    neutralButton.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    /**
     *
     * @return default button, one of {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     */
    public int getUseButton() {
        return useButton;
    }

    /**
     *
     * @param button which button to hid. Can be {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     * @param hide true if you want to hide the button, otherwise false
     */
    public void setHideButton(int button, boolean hide)
    {
        switch (button)
        {
            case BUTTON_NEGATIVE:
                hideNegative = hide;
                break;
            case BUTTON_NEUTRAL:
                hideNeutral = hide;
                break;
            case BUTTON_POSITIVE:
                hidePositive = hide;
                break;
        }

        hideButtons();
    }

    private void hideButtons()
    {
        Button positiveButton = findViewById(R.id.positiveButton);
        Button neutralButton = findViewById(R.id.neutralButton);
        Button negativeButton = findViewById(R.id.negativeButton);

        if(positiveButton !=null && hidePositive)
            positiveButton.setVisibility(View.GONE);
        else if(positiveButton != null)
            setPositiveButton(positiveLabel, positiveListener);

        if(neutralButton !=null && hideNeutral)
            neutralButton.setVisibility(View.GONE);
        else if(neutralButton != null)
            setNeutralButton(neutralLabel, neutralListener);

        if(negativeButton !=null && hideNegative)
            negativeButton.setVisibility(View.GONE);
        else if(negativeButton != null)
            setNegativeButton(negativeLabel, negativeListener);
    }

    /**
     *
     * @param button Which button to find visibility of.
     *               Can be {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     * @return visibility of button. can be View.GONE, View.VISIBLE, or View.INVISIBLE
     */
    public int getButtonVisibility(int button)
    {
        switch (button)
        {
            case BUTTON_NEGATIVE:
                return findViewById(R.id.negativeButton).getVisibility();
            case BUTTON_NEUTRAL:
                return findViewById(R.id.neutralButton).getVisibility();
            case BUTTON_POSITIVE:
                return findViewById(R.id.positiveButton).getVisibility();
        }

        return View.GONE;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        resize();
    }

    /** Displays body text of the dialog
     *
     * @param id if of the text resource to user
     */
    public void setText(int id)
    {
        setText(getContext().getResources().getString(id));
    }

    /** Displays body text of the dialog
     *
     * @param text Text to display
     */
    public void setText(CharSequence text)
    {
        setContentView(R.layout.text_screen);

        TextView textView = findViewById(R.id.text);
        if(text!=null && textView !=null)
        {
            textView.setText(text);
            //add links, if present
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        this.text = text;
    }
}
