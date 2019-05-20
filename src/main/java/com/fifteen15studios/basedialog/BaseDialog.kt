package com.fifteen15studios.basedialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.lang.IllegalStateException

open class BaseDialog (context: Context) : Dialog(context) {

    companion object {
        const val BUTTON_POSITIVE = 4
        const val BUTTON_NEUTRAL = 2
        const val BUTTON_NEGATIVE = 1
    }

    private var cancelled = false
    private var useButton = 0

    private var text = ""
    private var title = ""

    private var hidePositive = true
    private var hideNeutral = true
    private var hideNegative = true

    private var positiveLabel = context.resources.getString(android.R.string.ok)
    private var negativeLabel = context.resources.getString(android.R.string.cancel)
    private var neutralLabel = context.resources.getString(R.string.dialog_neutral)

    private var layoutID = -1
    private var layout = View(context)

    private var positiveListener = View.OnClickListener { dismiss() }
    private var negativeListener = View.OnClickListener { cancel() }
    private var neutralListener = View.OnClickListener { dismiss() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.dialog_base)
        if(layoutID != -1)
            setContentView(layoutID)
        else if(layout!=View(context))
            setContentView(layout)
        setUseButton(useButton)
        setTitle(title)
        setText(text)
    }

    /**
     *  If this method is never called, no positive button will be shown
     *
     * @param textResource Button text resource ID
     * @param listener what to do when the button is clicked
     */
    fun setPositiveButton(textResource : Int, listener : View.OnClickListener?)
    {
        setPositiveButton(context.resources.getString(textResource), listener)
    }

    /**
     * If this method is never called, no positive button will be shown
     *
     * @param text Button text. If null, it will be android.R.string.ok
     * @param listener what to do when the button is clicked
     */
    fun setPositiveButton(text : String?, listener : View.OnClickListener?)
    {
        val okButton = findViewById<Button>(R.id.positiveButton)

        useButton = useButton or BUTTON_POSITIVE

        positiveLabel = if(text !=null && text != "") { text }
        else { context.resources.getString(android.R.string.ok) }

        if(listener!=null)
            positiveListener = listener
        hidePositive = false

        if(okButton!=null) {
            okButton.visibility = View.VISIBLE

            if (text != null)
                okButton.text = positiveLabel

            okButton.setOnClickListener(listener)
        }
    }

    /**
     *  If this method is never called, no neutral button will be shown
     *
     * @param textResource Button text resource ID
     * @param listener what to do when the button is clicked
     */
    fun setNeutralButton(textResource : Int, listener : View.OnClickListener? )
    {
        setNeutralButton(context.resources.getString(textResource), listener)
    }

    /**
     * If this method is never called, no positive button will be shown
     *
     * @param text Button text. If null, it will be @strings\neutral
     * @param listener what to do when the button is clicked
     */
    fun setNeutralButton(text: String?, listener: View.OnClickListener?)
    {
        val button = findViewById<Button>(R.id.neutralButton)

        useButton = useButton or BUTTON_NEUTRAL

        neutralLabel = if(text !=null && text != "") { text }
        else {context.resources.getString(R.string.dialog_neutral)}

        if(listener!=null)
            neutralListener = listener
        hideNeutral = false

        if(button!=null) {
            button.visibility = View.VISIBLE

            if (text != null)
                button.text = neutralLabel

            button.setOnClickListener(listener)
        }
    }

    /**
     * If this method is never called, negative button will have text of android.R.string.cancel
     * and will cancel the dialog when clicked.
     *
     * @param textResource Button text resource ID
     * @param listener What to do when button is clicked.
     */
    fun setNegativeButton(textResource: Int, listener : View.OnClickListener? )
    {
        setNegativeButton(context.resources.getString(textResource), listener)
    }

    /**
     * If this method is never called, negative button will have text of android.R.string.cancel
     * and will cancel the dialog when clicked.
     *
     * @param text Button text. If null, it will be android.R.string.cancel
     * @param listener What to do when button is clicked
     */
    fun setNegativeButton(text: String?, listener: View.OnClickListener?)
    {
        val cancelButton = findViewById<Button>(R.id.negativeButton)

        useButton = useButton or BUTTON_NEGATIVE

        negativeLabel = if(text !=null && text != "") { text }
        else { context.resources.getString(android.R.string.cancel) }

        if(listener!=null)
            negativeListener = listener
        hideNegative = false

        if(cancelButton!=null) {
            cancelButton.visibility = View.VISIBLE

            if (text != null)
                cancelButton.text = negativeLabel

            cancelButton.setOnClickListener(listener)
        }
    }

    /**
     * Sets the middle part of the dialog - below the title, above the buttons
     *
     * @param view The view that will be displayed as the body of the dialog
     */

    override fun setContentView(view : View) {
        layout = view
        try {
            val parent = findViewById<ViewGroup>(R.id.content)
            parent.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        catch (e :  IllegalStateException)
        {
            val parent = findViewById<ViewGroup>(R.id.content)
            val viewParent = view.parent as ViewGroup
            viewParent.removeView(view)
            parent.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * Sets the middle part of the dialog - below the title, above the buttons
     *
     * @param layoutResID resource ID of the view that will be displayed as the body of the dialog
     */
    override fun setContentView(layoutResID : Int) {
        layoutID = layoutResID
        try {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            val parent = findViewById<ViewGroup>(R.id.content)

            if(inflater!=null && inflater is LayoutInflater)
                inflater.inflate(layoutResID, parent)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * Resize the dialog after view has changed.
     */
    private fun resize()
    {
        val tableLayout = findViewById<TableLayout>(R.id.buttonTable)
        val tableHeight = tableLayout.height

        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        //check to see if a row is completely gone
        val rows = tableLayout.childCount
        var visibleRows = rows
        for(i in 0 until rows)
        {
            val row = tableLayout.getChildAt(i)

            if(row is TableRow) {
                val children = row.childCount
                var visibleChildren = children

                for (j in 0 until children) {
                    if (row.getChildAt(i).visibility == View.GONE) {
                        visibleChildren--
                    }
                }

                if (visibleChildren == 0)
                    visibleRows--
            }
        }

        //height of a button in pixels
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, context.resources.displayMetrics)

        //if buttons aren't showing properly, fix layout
        if (tableHeight < (visibleRows * px))
        {
            val tableParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tableParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            if(Build.VERSION.SDK_INT >= 17)
                tableParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            else
                tableParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
            tableLayout.layoutParams = tableParams

            val scrollParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            scrollParams.addRule(RelativeLayout.ABOVE, R.id.buttonTable)
            scrollParams.addRule(RelativeLayout.BELOW, R.id.toolbar)
            scrollView.layoutParams = scrollParams
        }
    }

    /**
     * Changes the title of the dialog. If no title is set, title will be empty
     *
     * @param titleId Resource ID of the title string
     */
    override fun setTitle(titleId:Int) {
        setTitle(context.resources.getString(titleId))
    }

    /**
     * Changes the title of the dialog. If no title is set, or if null, title will be empty
     *
     * @param title Title to be displayed on the dialog
     */
    override fun setTitle(title : CharSequence?) {
        this.title = title.toString()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if(toolbar!=null)
            toolbar.title = title
    }

    /**
     *
     * @return True if dialog was cancelled, otherwise false
     */
    fun isCancelled() : Boolean {
        return cancelled
    }

    /**
     * Cancels operations. Sets "cancelled" as true. Use "isCancelled()" to find if dialog was cancelled
     */
    override fun cancel() {
        cancelled = true
        super.cancel()
    }

    /**
     * Default to button to show
     *
     * @param button Accepts constants {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     *
     * Use these in conjunction with each other using logical and
     *
     * Java Ex: setUseButton(BUTTON_POSITIVE & BUTTON_NEGATIVE)
     * Kotlin Ex: setUseButton(BUTTON_POSITIVE and BUTTON_NEGATIVE)
     *
     */
    fun setUseButton(button : Int) {
        this.useButton = button

        val okButton = findViewById<Button>(R.id.positiveButton)
        val cancelButton = findViewById<Button>(R.id.negativeButton)
        val neutralButton = findViewById<Button>(R.id.neutralButton)
        if(okButton != null && cancelButton != null && neutralButton != null) {

            //If positive button enabled
            if(button and BUTTON_POSITIVE == BUTTON_POSITIVE)
            {
                hidePositive = false
                if(!okButton.hasOnClickListeners())
                    okButton.setOnClickListener{ dismiss()}
            }
            else
                hidePositive = true

            //If neutral button enabled
            if(button and BUTTON_NEUTRAL == BUTTON_NEUTRAL)
            {
                hideNeutral = false
                if(!neutralButton.hasOnClickListeners())
                    neutralButton.setOnClickListener{ dismiss() }
            }
            else
                hideNeutral = true

            //If negative button enabled
            if(button and BUTTON_NEGATIVE == BUTTON_NEGATIVE)
            {
                hideNegative = false
                if(!cancelButton.hasOnClickListeners())
                    cancelButton.setOnClickListener{ cancel() }
            }
            else
                hideNegative = true
        }

        hideButtons()
    }

    /**
     *
     * @return default button, one of {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     */
    fun getUseButton() : Int {
        return useButton
    }

    /**
     *
     * @param button which button to hid. Can be {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     * @param hide true if you want to hide the button, otherwise false
     *
     * Use these in conjunction with each other using logical and
     *
     * Java Ex: setHideButton(BUTTON_POSITIVE & BUTTON_NEGATIVE)
     * Kotlin Ex: setHideButton(BUTTON_POSITIVE and BUTTON_NEGATIVE)
     */
    fun setHideButton(button: Int, hide : Boolean)
    {
        //turn off bits
        useButton = useButton.and(button.inv())

        if(button and BUTTON_POSITIVE == BUTTON_POSITIVE)
            hidePositive = hide
        if(button and BUTTON_NEUTRAL == BUTTON_NEUTRAL)
            hideNeutral = hide
        if(button and BUTTON_NEGATIVE == BUTTON_NEGATIVE)
            hideNegative = hide

        hideButtons()
    }

    private fun hideButtons()
    {
        val positiveButton = findViewById<Button>(R.id.positiveButton)
        val neutralButton = findViewById<Button>(R.id.neutralButton)
        val negativeButton = findViewById<Button>(R.id.negativeButton)

        if(positiveButton !=null && hidePositive)
            positiveButton.visibility = View.GONE
        else if(positiveButton != null)
            setPositiveButton(positiveLabel, positiveListener)

        if(neutralButton !=null && hideNeutral)
            neutralButton.visibility = View.GONE
        else if(neutralButton != null)
            setNeutralButton(neutralLabel, neutralListener)

        if(negativeButton !=null && hideNegative)
            negativeButton.visibility = View.GONE
        else if(negativeButton != null)
            setNegativeButton(negativeLabel, negativeListener)
    }

    /**
     *
     * @param button Which button to find visibility of.
     *               Can be {@link #BUTTON_NEGATIVE}, {@link #BUTTON_POSITIVE}, or {@link #BUTTON_NEUTRAL}
     * @return visibility of button. can be View.GONE, View.VISIBLE, or View.INVISIBLE
     */
    fun getButtonVisibility(button: Int) : Int
    {
        when (button)
        {
            BUTTON_NEGATIVE ->
                return findViewById<Button>(R.id.negativeButton).visibility
            BUTTON_NEUTRAL ->
                return findViewById<Button>(R.id.neutralButton).visibility
            BUTTON_POSITIVE ->
                return findViewById<Button>(R.id.positiveButton).visibility
        }

        return View.GONE
    }

    override fun show() {
        this.setOnShowListener {
            val negativeButton = findViewById<Button>(R.id.negativeButton)

            if(negativeButton != null && negativeLabel != "")
                negativeButton.text = negativeLabel

            val positiveButton = findViewById<Button>(R.id.positiveButton)

            if(positiveButton != null && positiveLabel != "")
                positiveButton.text = positiveLabel

            val neutralButton = findViewById<Button>(R.id.neutralButton)

            if(neutralButton != null && neutralLabel != "")
                neutralButton.text = neutralLabel

            hideButtons()
        }
        super.show()
    }

    override fun onWindowFocusChanged(hasFocus : Boolean) {
        super.onWindowFocusChanged(hasFocus)
        resize()
    }

    /** Displays body text of the dialog
     *
     * @param id if of the text resource to user
     */
    fun setText(id : Int)
    {
        setText(context.resources.getString(id))
    }

    /** Displays body text of the dialog
     *
     * @param text Text to display
     */
    fun setText(text : CharSequence?)
    {
        setContentView(R.layout.text_screen)

        val textView = findViewById<TextView>(R.id.text)
        if(text!=null && textView !=null)
        {
            textView.text = text
            //add links, if present
            textView.movementMethod = LinkMovementMethod.getInstance()
        }

        if(text!=null)
            this.text = text.toString()
    }
}