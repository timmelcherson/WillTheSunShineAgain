package tim.rguassessment.com.willthesunshineagain;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.ItemViewHolder>{


    private static final String TAG = "TAGG";
    private Context mContext;
    private final LayoutInflater mInflater;
    private List<City> mCities, mCitiesFull; // Cached copy of cities


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemTextView;

        private ItemViewHolder(View view){
            super(view);
            itemTextView = view.findViewById(R.id.city_list_item);
        }
    }

    FavoritesListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = mInflater.inflate(R.layout.city_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position){

        if (mCities != null && mCities.size() > 0){

            // Make sure favorites list is generated in alphabetical order if it has at least 2 items
            if (mCities.size() > 1){
                Collections.sort(mCities, new Comparator<City>() {
                    @Override
                    public int compare(City c1, City c2) {
                        return c1.getCityName().compareTo(c2.getCityName());
                    }
                });
            }


            City current = mCities.get(position);
            holder.itemTextView.setText(current.getCityName());

            holder.itemTextView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("EXTRA_CITY_ID_FROM_FAVORITES", mCities.get(position).getCityId());
                    mContext.startActivity(intent);

                }
            });
        }
        /*else {
            // If data is not ready yet
            holder.itemTextView.setText("No favorite city yet!");
            Log.d(TAG, "There are no cities to be found");
        }*/
    }


    void setCities(List<City> cities){
        mCities = new ArrayList<>(cities);
        mCitiesFull = new ArrayList<>(cities);
        notifyDataSetChanged();
    }

//    public Filter getFilter() {
//        return filter;
//    }

//    private Filter filter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            List<City> filteredList = new ArrayList<>();
//
//            if (constraint == null || constraint.length() == 0){
//                Log.d(TAG, "Search contains nothing, displaying full list");
//                filteredList.addAll(mCitiesFull);
//            }
//            else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
//                for (City city : mCitiesFull){
//                    if (city.getCityName().toLowerCase().contains(filterPattern)){
//                        filteredList.add(city);
//                    }
//                }
//            }
//
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            mCities.clear();
//            mCities.addAll((List)results.values);
//            Log.d(TAG, "mCities in publishResults has size: " + String.valueOf(mCities.size()));
//            notifyDataSetChanged();
//        }
//    };


    public void filterByTemp(List<City> mCitiesFiltered){
        mCities.clear();
        mCities.addAll(mCitiesFiltered);
        notifyDataSetChanged();
    }

    public void filterByHumidity(List<City> mCitiesFiltered){
        mCities.clear();
        mCities.addAll(mCitiesFiltered);
        notifyDataSetChanged();
    }

    public void filterByWind(List<City> mCitiesFiltered){
        mCities.clear();
        mCities.addAll(mCitiesFiltered);
        notifyDataSetChanged();
    }


    public City getCityAt(int position){
        return mCities.get(position);
    }


    @Override
    public int getItemCount(){
        if (mCities != null){
            return mCities.size();
        }
        else {
            return 0;
        }
    }


}
