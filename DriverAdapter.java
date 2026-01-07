package saksham.medrescue.saksham;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import sachdeva.saksham.medrescue.R;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    private final Context context;
    private final List<Driver> driverList;
    private final OnDriverClickListener listener;

    // Optional listener so you can handle clicks (e.g. confirm booking)
    public interface OnDriverClickListener {
        void onDriverClick(Driver driver);
    }

    public DriverAdapter(Context context, List<Driver> driverList, OnDriverClickListener listener) {
        this.context = context;
        this.driverList = driverList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(sachdeva.saksham.medrescue.R.layout.item_driver, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = driverList.get(position);

        holder.tvName.setText(driver.getName() != null ? driver.getName() : "Unknown");
        holder.tvPhone.setText(driver.getPhone() != null ? driver.getPhone() : "N/A");
        holder.tvVehicle.setText(driver.getAmbulanceType() != null ? driver.getAmbulanceType() : "Ambulance");
        // rating is double in model; set to float for RatingBar
        float rating = (float) driver.getRating();
        holder.ratingBar.setRating(rating);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDriverClick(driver);
            } else {
                Toast.makeText(context, "Selected: " + driver.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverList == null ? 0 : driverList.size();
    }

    public static class DriverViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvVehicle;
        RatingBar ratingBar;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            ratingBar = itemView.findViewById(R.id.tvRatingBar);
        }
    }
}
