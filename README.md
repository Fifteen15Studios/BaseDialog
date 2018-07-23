# BaseDialog
An Android dialog that mimics system dialogs, but can use custom views.

## Using a Custom View

To display a custom view call `setContentView` passing in a view or a resource id, or you can call `setText(String text)` to treat it as an AlertDialog.

From there, you can get elements of that view by using `findViewById(int resourceID)`

## Buttons

By default, the dialog will contain 1 button - a "neutral" button. This button will call `dismiss()` when clicked. To use a different button as default, call `setUseButton(int button)` passing in `BaseDialog.BUTTON_POSITIVE`, `BaseDialog.BUTTON_NEGATIVE`, or `BaseDialg.BUTTON_NEUTRAL`.

Activate other buttons, or change the button's properties, using `setPositiveButton(String text, onClickListener)`, `setNegativeButton(String text, onClickListener)` or `setNeutralButton(String text, onClickListener)`. The button text will be set to `text`, and the button will follow the instructions in the onClickListener when clicked. These methods will also accept a string resource id instead of a String value. If `text` is null, each button has its own default value: `BUTTON_POSITIVE` defaults to `android.R.string.ok`, `BUTTON_NEGATIVE` defaults to `android.R.string.cancel` and `BUTTON_NEUTRAL` defaults to to `R.string.neutral`. If onClickListener is null, it will call `dismiss()` when clicked.

Buttons can be explicitly hidden using `setHideButton(int button, boolean hide)`, passing in `BaseDialog.BUTTON_POSITIVE`, `BaseDialog.BUTTON_NEGATIVE`, or `BaseDialg.BUTTON_NEUTRAL`, and `true` to hide, or `false` to show.

## Finalizing

Lastly, the title of the dialog can be set using `setTitle` passing either a string or a resource id.

To show the dialog, simply call `show()`

## Usage

The class can be sub-classed Ex: `public class MyDialog extends BaseDialog`
or it can be used on the fly:

    BaseDialog dialog = new BaseDialog(context);
    dialog.setTitle(R.string.dialog_title);
    dialog.setText(R.string.dialog_text);
    
    dialog.setPositiveButton(context.getResources().getString(R.string.positive_label), new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            //do something
            dialog.dismiss();
        }
    });
    dialog.setNegativeButton(context.getResources().getString(R.string.negative_label), new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              dialog.dismiss();
        }
    });
    dialog.setHideButton(BaseDialog.BUTTON_NEUTRAL, true);

    dialog.show();
