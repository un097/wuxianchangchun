package cn.ffcs.wisdom.city.modular.query.views;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

public class DateView extends BaseTextView {

	DatePickerDialog datePickerDialog;

	private int mYear;
	private int mMonth;
	private int mDay;

	public DateView(Context context) {
		this(context, null);
	}

	public DateView(Context context, AttributeSet attrs) {
		super(context, attrs);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				datePickerDialog = new DatePickerDialog(getContext(), mDateSetListener, mYear,
						mMonth, mDay);
				datePickerDialog.show();
			}
		});
	}

	@Override
	public String getValue() {
		return "" + mYear + mMonth + mDay;
	}

	private void updateDateDisplay() {
		setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateDisplay();
		}
	};

}
