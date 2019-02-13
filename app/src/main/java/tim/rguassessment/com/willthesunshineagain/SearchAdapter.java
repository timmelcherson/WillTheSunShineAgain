package tim.rguassessment.com.willthesunshineagain;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import tim.rguassessment.com.willthesunshineagain.db.City;



public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemViewHolder>
        implements Filterable {


    private static final String TAG = "TAGG";
    private Context mContext;
    private final LayoutInflater mInflater;
    private List<City> mCities, mCitiesFull;


    static class ItemViewHolder extends RecyclerView.ViewHolder /*implements View.OnCreateContextMenuListener */{

        private final TextView itemTextView;

        private ItemViewHolder(View view){
            super(view);
            itemTextView = view.findViewById(R.id.city_list_item);
        }

    }


    SearchAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.city_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        if (mCities != null){
            City current = mCities.get(position);
            holder.itemTextView.setText(current.getCityName());

            holder.itemTextView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("EXTRA_CITY_ID_FROM_SEARCH", mCities.get(position).getCityId());
                mContext.startActivity(intent);

                }
            });

        }
        else {
            // If data is not ready yet
            holder.itemTextView.setText("No favorite city yet!");
        }
    }


    void setCities(List<City> cities){
        mCities = new ArrayList<>(cities);
        mCitiesFull = new ArrayList<>(cities);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<City> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(mCitiesFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (City city : mCitiesFull){
                    if (city.getCityName().toLowerCase().contains(filterPattern)){
                        filteredList.add(city);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCities.clear();
            mCities.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };



    @Override
    public int getItemCount() {
        if (mCities != null){
            return mCities.size();
        }
        else {
            return 0;
        }
    }

}
