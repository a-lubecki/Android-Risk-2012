package fr.istic.androidrisk.ihm.connexion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class RelativeLayoutCheckable extends RelativeLayout implements Checkable {

	private CheckBox _checkbox;

	public RelativeLayoutCheckable(Context context) {
		super(context);
	}

	public RelativeLayoutCheckable(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RelativeLayoutCheckable(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// find checked text view
		int childCount = getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View v = getChildAt(i);
			if (v instanceof CheckBox) {
				_checkbox = (CheckBox) v;
			}
		}
	}

	@Override
	public boolean isChecked() {
		return _checkbox != null ? _checkbox.isChecked() : false;
	}

	@Override
	public void setChecked(boolean checked) {
		if (_checkbox != null) {
			_checkbox.setChecked(checked);
		}
	}

	@Override
	public void toggle() {
		if (_checkbox != null) {
			_checkbox.toggle();
		}
	}

}
