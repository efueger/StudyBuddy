package ch.epfl.sweng.studdybuddy.util;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.tools.GroupsRecyclerAdapter;

public class FeedFilter extends Filter {
    GroupsRecyclerAdapter adapter;
    private List<Group>  filterList;

    public FeedFilter(GroupsRecyclerAdapter adapter, List<Group> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    public List<Group> forceFiltering(CharSequence constraint) {
        return (List<Group>) performFiltering(constraint).values;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if(constraint == null || constraint.length() == 0) {
            results.count=filterList.size();
            results.values=filterList;
            return results;
        }
        constraint=constraint.toString().toUpperCase();
        ArrayList<Group> filteredGroup=new ArrayList<>();
        for(int i=0;i<filterList.size();i++) {
            if(filterList.get(i).getCourse().getCourseName().toUpperCase().contains(constraint)) { //ADD PLAYER TO FILTERED PLAYERS
                filteredGroup.add(filterList.get(i));
            }
        }
        results.count=filteredGroup.size();
        results.values=filteredGroup;
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setGroupList((List<Group>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }

    public void setFilterList(List<Group> newfilterList) {//used for filtering out full groups
        this.filterList = newfilterList;
    }
}
