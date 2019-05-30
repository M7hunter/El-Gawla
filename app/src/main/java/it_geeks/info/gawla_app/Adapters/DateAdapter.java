package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.SalonDate;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private Context context;
    private List<SalonDate> dateList;
    private ClickInterface.OnItemClickListener clickListener;
    private int selectedPosition = 0;
    private String currentMonth;

    public DateAdapter(Context context, List<SalonDate> dateList, ClickInterface.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.dateList = dateList;
        this.clickListener = onItemClickListener;
        currentMonth = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale(SharedPrefManager.getInstance(context).getSavedLang()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salon_date, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SalonDate salonDate = dateList.get(i);

//        Log.d("dateAdapter::", "salonDate.getDayOfMonth() == " + salonDate.getDayOfMonth());
//        Log.d("dateAdapter::", "Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//        Log.d("dateAdapter::", "salonDate.getMonth() == " + salonDate.getMonth());
//        Log.d("dateAdapter::", "currentMonth == " + currentMonth);

        // check if selected
        if (selectedPosition == i) { // selected
            if (Integer.parseInt(salonDate.getDayOfMonth()) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && salonDate.getMonth().equals(currentMonth)) { // if today
                dayIsToday(viewHolder);
            } else {
                viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white_bordered_nice_blue));
            }
        } else { // !selected
            if (Integer.parseInt(salonDate.getDayOfMonth()) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && salonDate.getMonth().equals(currentMonth)) { // if today
                dayIsToday(viewHolder);
            } else {
                dayNotToday(viewHolder);
            }
        }

        viewHolder.dayOfMonth.setText(salonDate.getDayOfMonth());
        viewHolder.month.setText(salonDate.getMonth());
        viewHolder.dayOfWeek.setText(salonDate.getDayOfWeek());
        viewHolder.salonsCount.setText(String.valueOf(salonDate.getSalonsCount()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, viewHolder.getAdapterPosition());

                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    private void dayIsToday(ViewHolder viewHolder) {
        viewHolder.month.setTextColor(Color.WHITE);
        viewHolder.dayOfWeek.setTextColor(Color.WHITE);
        viewHolder.dayOfWeek.setTextColor(Color.WHITE);
        viewHolder.dayOfMonth.setTextColor(Color.WHITE);
        viewHolder.salonsCount.setTextColor(Color.WHITE);
        viewHolder.separator.setBackgroundColor(Color.WHITE);
        viewHolder.salonsCount.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_nice_blue));
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_nice_blue));
    }

    private void dayNotToday(ViewHolder viewHolder) {
        viewHolder.month.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.dayOfWeek.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.dayOfWeek.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.dayOfMonth.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.salonsCount.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.separator.setBackgroundColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.salonsCount.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_pale_grey));
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white));
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayOfMonth, month, dayOfWeek, salonsCount;
        View separator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayOfMonth = itemView.findViewById(R.id.date_day_of_month);
            month = itemView.findViewById(R.id.date_month);
            dayOfWeek = itemView.findViewById(R.id.date_day_of_week);
            salonsCount = itemView.findViewById(R.id.date_salons_count);
            separator = itemView.findViewById(R.id.date_separator);
        }
    }
}